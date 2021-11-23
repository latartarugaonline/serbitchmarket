package it.ltm.scp.module.android.model.devices.system.gson;

/**
 * Created by HW64 on 18/10/2016.
 */
public class SystemInfo {

    private String serialNumber;
    private String partNumber;
    private String systemVersion;
    private String systemTime;
    private String uptime;
    private LoadAverage loadAverage;

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

    public String getSystemVersion() {
        return systemVersion;
    }

    public void setSystemVersion(String systemVersion) {
        this.systemVersion = systemVersion;
    }

    public String getSystemTime() {
        return systemTime;
    }

    public void setSystemTime(String systemTime) {
        this.systemTime = systemTime;
    }

    public String getUptime() {
        return uptime;
    }

    public void setUptime(String uptime) {
        this.uptime = uptime;
    }

    public LoadAverage getLoadAverage() {
        return loadAverage;
    }

    public void setLoadAverage(LoadAverage loadAverage) {
        this.loadAverage = loadAverage;
    }
}
