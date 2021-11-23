package it.ltm.scp.module.android.monitor.model;

public class Monitor {

    /*{
        "userCode": "F005K02",
            "appCode": "00001",
            "contextPath": "https://INSOMNIA_REST/test",
            "timestamp": "2019-02-20 16:46:35.123",
            "posData": "{\"field\":\"value\"}",
            "level": "ERROR",
            "message": "this is the message",
            "extra": "these are the extra info"
    }*/

    private String userCode;
    private String appCode;
    private String contextPath;
    private String timestamp;
    private String posData;
    private String level;
    private String message;
    private String extra;

    public static final String LEVEL_ERROR = "ERROR";
    public static final String LEVEL_DEBUG = "DEBUG";
    public static final String LEVEL_WARN = "WARN";
    public static final String LEVEL_REPORT = "ASSET";


    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public String getAppCode() {
        return appCode;
    }

    public void setAppCode(String appCode) {
        this.appCode = appCode;
    }

    public String getContextPath() {
        return contextPath;
    }

    public void setContextPath(String contextPath) {
        this.contextPath = contextPath;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getPosData() {
        return posData;
    }

    public void setPosData(String posData) {
        this.posData = posData;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }
}
