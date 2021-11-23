package it.ltm.scp.module.android.model;

import com.google.gson.Gson;

import java.io.Serializable;

public class UploadStatusResult implements Serializable{


    private int code;
    private String description;
    private int status;

    public UploadStatusResult(int code, String description, int status) {
        this.code = code;
        this.description = description;
        this.status = status;
    }

    public UploadStatusResult(int status) {
        this(0, null, status);
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }


    public String toJsonString(){
        return new Gson().toJson(this);
    }
}
