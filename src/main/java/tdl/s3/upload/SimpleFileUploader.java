package tdl.s3.upload;

import com.amazonaws.services.s3.AmazonS3;

import java.io.File;



/**
 * @author vdanyliuk
 * @version 11.04.17.
 */
public class SimpleFileUploader extends AbstractFileUploader {

    public SimpleFileUploader(final AmazonS3 s3Provider, String bucket) {
        super(s3Provider, bucket);
    }

    @Override
    protected void uploadInternal(AmazonS3 s3, String bucket, File file, String newName) {
        s3.putObject(bucket, newName, file);
    }

}
