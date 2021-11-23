package it.ltm.scp.module.android.model.devices.scanner;

import java.util.List;

public class ScannerInfo {
    private ScannerFirmwareVersion firmwareVersion;

    /*
     * Parametri aggiunti da fw iPOS 1.4.62:
     */
    private int code;
    private String message;
    private List<ScannerInfoData> data;

    public static final String ID_ZEBRA = "zebra";
    public static final String ID_ED40 = "default";


    //TODO ha senso mantenere vecchi campi?
    public ScannerFirmwareVersion getFirmwareVersion() {
        return firmwareVersion;
    }

    public void setFirmwareVersion(ScannerFirmwareVersion firmwareVersion) {
        this.firmwareVersion = firmwareVersion;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<ScannerInfoData> getData() {
        return data;
    }

    public void setData(List<ScannerInfoData> data) {
        this.data = data;
    }

    public boolean isZebraAttached(){
        for (ScannerInfoData info : data){
            if(ScannerInfo.ID_ZEBRA.equals(info.getId())){
                return true;
            }
        }
        return false;
    }
}
