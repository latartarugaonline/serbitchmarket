package it.ltm.scp.module.android.model.devices.pos.gson;

/**
 * Created by HW64 on 20/09/2016.
 */
public class AuthRequest {

    private String appPublicKeyModulus;
    private String appPublicKeyExp;
    private String device;

    public AuthRequest() {
    }

    public AuthRequest(String appPublicKeyModulus, String appPublicKeyExp, String device) {
        this.appPublicKeyModulus = appPublicKeyModulus;
        this.appPublicKeyExp = appPublicKeyExp;
        this.device = device;
    }

    public String getAppPublicKeyModulus() {
        return appPublicKeyModulus;
    }

    public void setAppPublicKeyModulus(String appPublicKeyModulus) {
        this.appPublicKeyModulus = appPublicKeyModulus;
    }

    public String getAppPublicKeyExp() {
        return appPublicKeyExp;
    }

    public void setAppPublicKeyExp(String appPublicKeyExp) {
        this.appPublicKeyExp = appPublicKeyExp;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }
}
