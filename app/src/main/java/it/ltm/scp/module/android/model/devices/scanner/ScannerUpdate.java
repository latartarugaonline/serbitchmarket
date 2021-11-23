package it.ltm.scp.module.android.model.devices.scanner;

public class ScannerUpdate {
    //orginal json: {"code":"START","message":"Firmware upload started","size":48473,"status":0}
    // {"code":"PROGRESS","message":"Firmware upload in progress","progress":2050,"status":0}
    // {"code":"COMPLETED","message":"Firmware upload completed","status":0}
    // --> {"code":"RESTART","message":"Start of new Zebra firmware, it takes a few minutes","status":0}

    /**
    @param code: constant values
     */

    public static final String BCR_UPDATE_START_INSTALL = "RESTART";
    public static final String BCR_UPDATE_START_UPLOAD = "START";
    public static final String BCR_UPDATE_PROGRESS_UPLOAD = "PROGRESS";
    public static final String BCR_UPDATE_COMPLETE_UPLOAD = "COMPLETED";

    public static final String BCR_UPDATE_MESSAGE = "Attenzione, aggiornamento del dispositivo Barcode Reader in corso.\nSi prega di non scollegare la periferica e di non spegnere il terminale durante l'operazione.\nAl termine della procedura sarà possibile riprendere le operazioni.";



    private String code;
    private String message;
    private int size;
    private int status;
    private int progress;


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }
}
