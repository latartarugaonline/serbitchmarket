package it.ltm.scp.module.android.monitor.model.terminal;

import java.io.Serializable;

public class Pos implements Serializable {
    private static final long serialVersionUID = -8028267737822079159L;
    private String releaseFw;
    private String serialNumber;
    private String emvVersion;

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

    public String getEmvVersion() {
        return emvVersion;
    }

    public void setEmvVersion(String emvVersion) {
        this.emvVersion = emvVersion;
    }
}
