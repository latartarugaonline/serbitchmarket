package it.ltm.scp.module.android.model.sm.gson;

public class VirtualAuth {

    private int code;
    private String description;
    private VirtualAuthData authenticationData;

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

    public VirtualAuthData getAuthenticationData() {
        return authenticationData;
    }

    public void setAuthenticationData(VirtualAuthData authenticationData) {
        this.authenticationData = authenticationData;
    }
}
