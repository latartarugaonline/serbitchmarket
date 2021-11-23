package it.ltm.scp.module.android.model.devices.printer.gson.status;

/**
 * Created by HW64 on 06/10/2016.
 */
public class ThermalHead {

    String text;
    int statusCode;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }
}
