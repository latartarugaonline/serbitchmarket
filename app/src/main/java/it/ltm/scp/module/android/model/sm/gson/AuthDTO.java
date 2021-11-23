package it.ltm.scp.module.android.model.sm.gson;


import it.ltm.scp.module.android.model.devices.pos.gson.Auth;

/**
 * Created by HW64 on 20/10/2016.
 */
public class AuthDTO {
    private String usercode = "";
    private String status = "";
    private String token = "";
    private String tokenExpiryDate = "";
    private int terminalType = 1;

    public AuthDTO(Auth auth) {
        this.usercode = auth.getUserCode();
        this.status = auth.getStatus();
        this.token = auth.getToken();
        this.tokenExpiryDate = auth.getTokenExpiryDate();
    }

    public int getTerminalType() {
        return terminalType;
    }

    public void setTerminalType(int terminalType) {
        this.terminalType = terminalType;
    }

    public String getUsercode() {
        return usercode;
    }

    public void setUsercode(String usercode) {
        this.usercode = usercode;
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
}
