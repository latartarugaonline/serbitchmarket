package it.ltm.scp.module.android.monitor.model;

import com.google.gson.Gson;

public class Message {
    private String tag;
    private String message;
    private String ex;

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getEx() {
        return ex;
    }

    public void setEx(String ex) {
        this.ex = ex;
    }

    public String toJsonString(){
        return new Gson().toJson(this);
    }
}
