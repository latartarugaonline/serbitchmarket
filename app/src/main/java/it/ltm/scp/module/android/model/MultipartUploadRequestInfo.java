package it.ltm.scp.module.android.model;

import java.util.ArrayList;
import java.util.List;

import it.ltm.scp.module.android.utils.Constants;

public class MultipartUploadRequestInfo extends UploadRequestInfo {
    private List<UploadFormDataPart> parts;
    private int writeTimeout;
    private int readTimeout;


    public MultipartUploadRequestInfo(String url, int numRetry, long retryInterval) {
        super(url, numRetry, retryInterval);
        parts = new ArrayList<>();
        writeTimeout = Constants.BCKGRND_TIMEOUT_WRITE;
        readTimeout = Constants.BCKGRND_TIMEOUT_READ;
    }

    public void addFormDataPart(UploadFormDataPart part){
        parts.add(part);
    }

    public List<UploadFormDataPart> getParts() {
        return parts;
    }

    public int getWriteTimeout() {
        return writeTimeout;
    }

    public void setWriteTimeout(int writeTimeout) {
        this.writeTimeout = writeTimeout;
    }

    public int getReadTimeout() {
        return readTimeout;
    }

    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
    }
}
