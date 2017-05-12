package tdl.s3.sync;

import java.io.File;

public class DummyProgressListener implements ProgressListener {

    @Override
    public void uploadFileStarted(File file, String uploadId) {
        //DO NOTHING
    }

    @Override
    public void uploadFileProgress(String uploadId, long uploadedByte) {
        //DO NOTHING
    }

    @Override
    public void uploadFileFinished(File file) {
        //DO NOTHING
    }

}
