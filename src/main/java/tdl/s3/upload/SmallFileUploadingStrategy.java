package tdl.s3.upload;

import com.amazonaws.services.s3.AmazonS3;
import lombok.extern.slf4j.Slf4j;

import java.io.File;


@Slf4j
public class SmallFileUploadingStrategy implements UploadingStrategy {

    @Override
    public void upload(AmazonS3 s3, File file, RemoteFile remoteFile) throws Exception {
        log.debug("Uploading file " + file + " with SmallFileUploadingStrategy.");
        s3.putObject(remoteFile.getBucket(), remoteFile.getFullPath(), file);
    }

}
