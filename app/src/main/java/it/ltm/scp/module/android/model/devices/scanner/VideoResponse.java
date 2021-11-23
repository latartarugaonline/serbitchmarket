package it.ltm.scp.module.android.model.devices.scanner;

public class VideoResponse {
    private int code;
    private String message;
    private VideoResponseData data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public VideoResponseData getData() {
        return data;
    }

    public void setData(VideoResponseData data) {
        this.data = data;
    }
}
