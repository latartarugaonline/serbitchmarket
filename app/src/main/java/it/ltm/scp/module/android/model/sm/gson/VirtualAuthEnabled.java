package it.ltm.scp.module.android.model.sm.gson;

public class VirtualAuthEnabled {
    private int code;
    private String description;
    private boolean isAuthRequired;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public boolean isAuthRequired() {
        return isAuthRequired;
    }

    public void setAuthRequired(boolean authRequired) {
        isAuthRequired = authRequired;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String logError(){
        return this.getDescription() + " (" + this.getCode() + ")";
    }
}
