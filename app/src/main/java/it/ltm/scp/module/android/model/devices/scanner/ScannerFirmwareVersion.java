package it.ltm.scp.module.android.model.devices.scanner;

public class ScannerFirmwareVersion {
    private String type;
    private int[] data;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int[] getData() {
        return data;
    }

    public void setData(int[] data) {
        this.data = data;
    }
}
