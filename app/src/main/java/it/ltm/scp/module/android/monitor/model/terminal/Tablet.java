package it.ltm.scp.module.android.monitor.model.terminal;

import java.io.Serializable;

public class Tablet implements Serializable {
    private static final long serialVersionUID = 5262030484249104083L;
    private String serialNumber;

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }
}
