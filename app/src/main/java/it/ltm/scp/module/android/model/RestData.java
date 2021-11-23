package it.ltm.scp.module.android.model;

import com.google.gson.annotations.Expose;

import java.io.Serializable;

public class RestData implements Serializable {

    private static final long serialVersionUID = -1L;

    @Expose
    private String token;
    @Expose
    private String userCode;
    @Expose
    private String callerTRXID;
    @Expose
    private String flagFinalize;
    @Expose
    private String documentID;

    @Expose(serialize = false)
    private ImageData imageFront;
    @Expose(serialize = false)
    private ImageData imageBack;

    public String getCallerTRXID() {
        return callerTRXID;
    }

    public void setCallerTRXID(String callerTRXID) {
        this.callerTRXID = callerTRXID;
    }

    public String getFlagFinalize() {
        return flagFinalize;
    }

    public void setFlagFinalize(String flagFinalize) {
        this.flagFinalize = flagFinalize;
    }

    public String getDocumentID() {
        return documentID;
    }

    public void setDocumentID(String documentID) {
        this.documentID = documentID;
    }

    public ImageData getImageFront() {
        return imageFront;
    }

    public void setImageFront(ImageData imageFront) {
        this.imageFront = imageFront;
    }

    public ImageData getImageBack() {
        return imageBack;
    }

    public void setImageBack(ImageData imageBack) {
        this.imageBack = imageBack;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    @Override
    public String toString() {
        return "RestData [token=" + token + ", userCode=" + userCode
                + ", callerTRXID=" + callerTRXID + ", flagFinalize="
                + flagFinalize + ", documentID=" + documentID + ", imageFront="
                + imageFront + ", imageBack=" + imageBack + "]";
    }

}
