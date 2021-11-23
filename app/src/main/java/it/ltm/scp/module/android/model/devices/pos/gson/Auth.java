package it.ltm.scp.module.android.model.devices.pos.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by HW64 on 15/09/2016.
 */
public class Auth {
    @SerializedName("UserCode")
    private String userCode;
    @SerializedName("Status")
    private String status;
    @SerializedName("Token")
    private String token;
    @SerializedName("TokenExpiryDate")
    private String tokenExpiryDate;
    private String userName;

    private String physicalUserCode;
    private String physicalToken;
    private String physicalTokenExpiryDate;


    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTokenExpiryDate() {
        return tokenExpiryDate;
    }

    public void setTokenExpiryDate(String tokenExpiryDate) {
        this.tokenExpiryDate = tokenExpiryDate;
    }

    public String getPhysicalUserCode() {
        return physicalUserCode;
    }

    public void setPhysicalUserCode(String physicalUserCode) {
        this.physicalUserCode = physicalUserCode;
    }

    public String getPhysicalToken() {
        return physicalToken;
    }

    public void setPhysicalToken(String physicalToken) {
        this.physicalToken = physicalToken;
    }

    public String getPhysicalTokenExpiryDate() {
        return physicalTokenExpiryDate;
    }

    public void setPhysicalTokenExpiryDate(String physicalTokenExpiryDate) {
        this.physicalTokenExpiryDate = physicalTokenExpiryDate;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
