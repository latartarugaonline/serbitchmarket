package it.ltm.scp.module.android.model.devices.printer.gson.status;

public class UsbAttached {

    private String text;
    private int statusCode = Status.CODE_USB_OK;

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
