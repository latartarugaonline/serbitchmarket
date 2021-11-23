package it.ltm.scp.module.android.model.sm.gson;

import it.ltm.scp.module.android.model.devices.pos.gson.PosInfo;

/**
 * Created by HW64 on 20/10/2016.
 */
public class PosInfoDTO {
    private String slot00Status = "";
    private String termID00 = "";
    private String slot01Status = "";
    private String termID01 = "";
    private String userCode = "";
    private String posRelease = "";
    private String posSerial = "";
    private String posStatus = "";
    private String pinpadModel = "";
    private String emvKeyPresent = "";
    private String emvVersion = "";
    private String auxExtDevicePresent = "";
    private String auxExtDeviceDescr = "";
    private String type = "";
    private String appVersion;
    private String tabletSerial;
    private String iPosVersion;
    private String iPosSerial;


    public PosInfoDTO(PosInfo posInfo) {
        this.slot00Status = posInfo.getStatusSlot00();
        this.termID00 = posInfo.getTermID00();
        this.slot01Status = posInfo.getStatusSlot01();
        this.termID01 = posInfo.getTermID01();
        this.userCode = posInfo.getUserCode();
        this.posRelease = posInfo.getPOSRelease();
        this.posSerial = posInfo.getPOSSerial();
        this.posStatus = posInfo.getPOSStatus();
        this.pinpadModel = posInfo.getPinpadModel();
        this.emvKeyPresent = posInfo.getEMVKeyPresent();
        this.emvVersion = posInfo.getEMVVersion();
        this.auxExtDevicePresent = posInfo.getAuxExtDevicePresent();
        this.auxExtDeviceDescr = posInfo.getAuxExtDeviceDescription();
        this.type = posInfo.getPOSType();
        this.appVersion = posInfo.getAppVersion();
        this.tabletSerial = posInfo.getTabletSerial();
        this.iPosSerial = posInfo.getIPOSSerial();
        this.iPosVersion = posInfo.getIPOSVersion();
    }

    public String getSlot00Status() {
        return slot00Status;
    }

    public void setSlot00Status(String slot00Status) {
        this.slot00Status = slot00Status;
    }

    public String getTermID00() {
        return termID00;
    }

    public void setTermID00(String termID00) {
        this.termID00 = termID00;
    }

    public String getSlot01Status() {
        return slot01Status;
    }

    public void setSlot01Status(String slot01Status) {
        this.slot01Status = slot01Status;
    }

    public String getTermID01() {
        return termID01;
    }

    public void setTermID01(String termID01) {
        this.termID01 = termID01;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public String getPosRelease() {
        return posRelease;
    }

    public void setPosRelease(String posRelease) {
        this.posRelease = posRelease;
    }

    public String getPosSerial() {
        return posSerial;
    }

    public void setPosSerial(String posSerial) {
        this.posSerial = posSerial;
    }

    public String getPosStatus() {
        return posStatus;
    }

    public void setPosStatus(String posStatus) {
        this.posStatus = posStatus;
    }

    public String getPinpadModel() {
        return pinpadModel;
    }

    public void setPinpadModel(String pinpadModel) {
        this.pinpadModel = pinpadModel;
    }

    public String getEmvKeyPresent() {
        return emvKeyPresent;
    }

    public void setEmvKeyPresent(String emvKeyPresent) {
        this.emvKeyPresent = emvKeyPresent;
    }

    public String getEmvVersion() {
        return emvVersion;
    }

    public void setEmvVersion(String emvVersion) {
        this.emvVersion = emvVersion;
    }

    public String getAuxExtDevicePresent() {
        return auxExtDevicePresent;
    }

    public void setAuxExtDevicePresent(String auxExtDevicePresent) {
        this.auxExtDevicePresent = auxExtDevicePresent;
    }

    public String getAuxExtDeviceDescr() {
        return auxExtDeviceDescr;
    }

    public void setAuxExtDeviceDescr(String auxExtDeviceDescr) {
        this.auxExtDeviceDescr = auxExtDeviceDescr;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public String getTabletSerial() {
        return tabletSerial;
    }

    public void setTabletSerial(String tabletSerial) {
        this.tabletSerial = tabletSerial;
    }

    public String getiPosVersion() {
        return iPosVersion;
    }

    public void setiPosVersion(String iPosVersion) {
        this.iPosVersion = iPosVersion;
    }

    public String getiPosSerial() {
        return iPosSerial;
    }

    public void setiPosSerial(String iPosSerial) {
        this.iPosSerial = iPosSerial;
    }
}
