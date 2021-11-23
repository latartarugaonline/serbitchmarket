package it.ltm.scp.module.android.model;

import java.io.Serializable;

public class UploadRequestInfo implements Serializable {
    private static final long serialVersionUID = 7366955952542680711L;
    private String url;
    private String jsonBody;
    private int numRetry;
    private long retryInterval;

    public UploadRequestInfo(String url, String jsonBody, int numRetry, long retryInterval) {
        this.url = url;
        this.jsonBody = jsonBody;
        this.numRetry = numRetry;
        this.retryInterval = retryInterval;
    }

    public UploadRequestInfo(String url, int numRetry, long retryInterval) {
        this.url = url;
        this.numRetry = numRetry;
        this.retryInterval = retryInterval;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getJsonBody() {
        return jsonBody;
    }

    public void setJsonBody(String jsonBody) {
        this.jsonBody = jsonBody;
    }

    public int getNumRetry() {
        return numRetry;
    }

    public void setNumRetry(int numRetry) {
        this.numRetry = numRetry;
    }

    public long getRetryInterval() {
        return retryInterval;
    }

    public void setRetryInterval(long retryInterval) {
        this.retryInterval = retryInterval;
    }
}
