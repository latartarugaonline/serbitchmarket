package it.ltm.scp.module.android.monitor.model.terminal;

import java.io.Serializable;

public class Terminal implements Serializable {

    private static final long serialVersionUID = -1881528193236338287L;
    private String model;
    private String releaseFw;
    private String serialNumber;
    private String partNumber;

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getReleaseFw() {
        return releaseFw;
    }

    public void setReleaseFw(String releaseFw) {
        this.releaseFw = releaseFw;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getPartNumber() {
        return partNumber;
    }

    public void setPartNumber(String partNumber) {
        this.partNumber = partNumber;
    }
}
