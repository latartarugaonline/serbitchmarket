package it.ltm.scp.module.android.model.devices.pos.gson;

/**
 * Created by HW64 on 15/09/2016.
 */
public class PosInfo {

    //@SerializedName("StatusSlot00")
    private String StatusSlot00;
    //@SerializedName("TermID00")
    private String TermID00;
    //@SerializedName("StatusSlot01")
    private String StatusSlot01;
    //@SerializedName("TermID01")
    private String TermID01;
    //@SerializedName("UserCode")
    private String UserCode; // diventerà usercode virtuale
    private String PhysicalUserCode;
    //@SerializedName("POSRelease")
    private String POSRelease;
    //@SerializedName("POSSerial")
    private String POSSerial;
    //@SerializedName("POSStatus")
    private String POSStatus;
    //@SerializedName("PinpadModel")
    private String PinpadModel;
    //@SerializedName("EMVKeyPresent")
    private String EMVKeyPresent;
    //@SerializedName("EMVVersion")
    private String EMVVersion;
    //@SerializedName("AuxExtDevicePresent")
    private String AuxExtDevicePresent;
    //@SerializedName("AuxExtDeviceDescription")
    private String AuxExtDeviceDescription;
    private String AppVersion;
    private String TabletSerial;
    private String GuidaLisaVersion;
    private String IPOSVersion;
    private String IPOSSerial;
    private String POSType;

    public String getStatusSlot00() {
        return StatusSlot00;
    }

    public void setStatusSlot00(String statusSlot00) {
        this.StatusSlot00 = statusSlot00;
    }

    public String getTermID00() {
        return TermID00;
    }

    public void setTermID00(String termID00) {
        this.TermID00 = termID00;
    }

    public String getStatusSlot01() {
        return StatusSlot01;
    }

    public void setStatusSlot01(String statusSlot01) {
        this.StatusSlot01 = statusSlot01;
    }

    public String getTermID01() {
        return TermID01;
    }

    public void setTermID01(String termID01) {
        this.TermID01 = termID01;
    }

    public String getUserCode() {
        return UserCode;
    }

    public void setUserCode(String userCode) {
        this.UserCode = userCode;
    }

    public String getPOSRelease() {
        return POSRelease;
    }

    public void setPOSRelease(String POSRelease) {
        this.POSRelease = POSRelease;
    }

    public String getPOSSerial() {
        return POSSerial;
    }

    public void setPOSSerial(String POSSerial) {
        this.POSSerial = POSSerial;
    }

    public String getPOSStatus() {
        return POSStatus;
    }

    public void setPOSStatus(String POSStatus) {
        this.POSStatus = POSStatus;
    }

    public String getPinpadModel() {
        return PinpadModel;
    }

    public void setPinpadModel(String pinpadModel) {
        this.PinpadModel = pinpadModel;
    }

    public String getEMVKeyPresent() {
        return EMVKeyPresent;
    }

    public void setEMVKeyPresent(String EMVKeyPresent) {
        this.EMVKeyPresent = EMVKeyPresent;
    }

    public String getEMVVersion() {
        return EMVVersion;
    }

    public void setEMVVersion(String EMVVersion) {
        this.EMVVersion = EMVVersion;
    }

    public String getAuxExtDevicePresent() {
        return AuxExtDevicePresent;
    }

    public void setAuxExtDevicePresent(String auxExtDevicePresent) {
        this.AuxExtDevicePresent = auxExtDevicePresent;
    }

    public String getAuxExtDeviceDescription() {
        return AuxExtDeviceDescription;
    }

    public void setAuxExtDeviceDescription(String auxExtDeviceDescription) {
        this.AuxExtDeviceDescription = auxExtDeviceDescription;
    }

    public String getPOSType() {
        return POSType;
    }

    public void setPOSType(String POSType) {
        this.POSType = POSType;
    }



    public String getAppVersion() {
        return AppVersion;
    }

    public void setAppVersion(String appVersion) {
        AppVersion = appVersion;
    }

    public String getIPOSVersion() {
        return IPOSVersion;
    }

    public void setIPOSVersion(String IPOSVersion) {
        this.IPOSVersion = IPOSVersion;
    }

    public String getIPOSSerial() {
        return IPOSSerial;
    }

    public void setIPOSSerial(String IPOSSerial) {
        this.IPOSSerial = IPOSSerial;
    }

    public String getTabletSerial() {
        return TabletSerial;
    }

    public void setTabletSerial(String tabletSerial) {
        TabletSerial = tabletSerial;
    }

    public String getGuidaLisaVersion() {
        return GuidaLisaVersion;
    }

    public void setGuidaLisaVersion(String guidaLisaVersion) {
        GuidaLisaVersion = guidaLisaVersion;
    }

    public String getPhysicalUserCode() {
        return PhysicalUserCode;
    }

    public void setPhysicalUserCode(String physicalUserCode) {
        PhysicalUserCode = physicalUserCode;
    }
}
