package it.ltm.scp.module.android.model.devices.pos.tsn;

/**
 * Created by HW64 on 23/02/2017.
 */

public class TsnRequest {
    private Integer timeout;
    private String readType;
    private String messageOnDisplay;

    public TsnRequest(Integer timeout, String readType, String messageOnDisplay) {
        this.timeout = timeout;
        if(readType == null)
            this.readType = "Auto";
        else
            this.readType = readType;
        this.messageOnDisplay = messageOnDisplay;
    }

    public Integer getTimeout() {
        return timeout;
    }

    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }

    public String getReadType() {
        return readType;
    }

    public void setReadType(String readType) {
        this.readType = readType;
    }

    public String getMessageOnDisplay() {
        return messageOnDisplay;
    }

    public void setMessageOnDisplay(String messageOnDisplay) {
        this.messageOnDisplay = messageOnDisplay;
    }
}
