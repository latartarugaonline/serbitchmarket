package it.ltm.scp.module.android.model.sm.gson;

public class VirtualAuthEnabledRequest {
    private String userCode;

    public VirtualAuthEnabledRequest(String userCode) {
        this.userCode = userCode;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }
}
