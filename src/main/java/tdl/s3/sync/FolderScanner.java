package tdl.s3.sync;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collections;
import java.util.function.BiConsumer;

import static java.nio.file.FileVisitResult.CONTINUE;
import static java.nio.file.FileVisitResult.SKIP_SUBTREE;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
class FolderScanner {

    private final Filters filters;

    FolderScanner(Filters filters) {
        this.filters = filters;
    }

    void traverseFolder(Path folderPath, BiConsumer<File, String> fileConsumer, boolean recursive) throws IOException {
        if (!Files.isDirectory(folderPath)) {
            throw new IllegalArgumentException("Path should represent directory.");
        }
        int scanDepth = recursive ? Integer.MAX_VALUE : 1;
        Files.walkFileTree(
                folderPath,
                Collections.singleton(FileVisitOption.FOLLOW_LINKS),
                scanDepth,
                getVisitor(fileConsumer, recursive, folderPath)
        );
    }

    private FileVisitor<? super Path> getVisitor(BiConsumer<File, String> fileConsumer, boolean recursive, Path currentDir) {
        return new FileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                return recursive || currentDir.equals(dir) ? CONTINUE : SKIP_SUBTREE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                if (Files.isRegularFile(file) && filters.accept(file)) {
                    fileConsumer.accept(file.toFile(), currentDir.relativize(file).toString());
                }
                return CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                log.warn("Can't read file " + file + " due to exception: " + exc.getMessage());
                return CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                return CONTINUE;
            }
        };
    }

    public List<String> getUploadFilesRelativePathList(Path folderPath) {
        try {
            //TODO: Handle non-recursive
            File base = folderPath.toFile();
            return Files.find(
                    folderPath,
                    Integer.MAX_VALUE,
                    (filePath, fileAttr) -> {
                        return fileAttr.isRegularFile() && filters.accept(filePath);
                    }
            ).map(path -> {
                return base.toURI().relativize(path.toFile().toURI()).getPath();
            })
            .collect(Collectors.toList());
        } catch (IOException ex) {
            return new ArrayList<>();
        }
    }
}
