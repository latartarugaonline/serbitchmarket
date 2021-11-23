package it.ltm.scp.module.android.monitor.model.terminal;

import java.io.Serializable;

public class Pinpad implements Serializable {
    private static final long serialVersionUID = -9046510081886641874L;
    private String model;
    private String serialNumber;

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }
}
