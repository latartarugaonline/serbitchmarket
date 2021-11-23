package it.ltm.scp.module.android.model.devices.scanner;

public class ScannerStatus {
    private int generalState;
    private String scanner;
    private String message;
    private String status; //status: ready, restart, stop, start

    public static final String STATUS_STOP = "stop";
    public static final String STATUS_RESTART = "restart";
    public static final String STATUS_START = "start";
    public static final String STATUS_READY = "ready";

    public static final String SCANNER_ZEBRA = "zebra";

    public final static String MESSAGE_REBOOT= "Verifica operatività Barcode Reader in corso, assicurarsi che sia collegato correttamente.";

    public int getGeneralState() {
        return generalState;
    }

    public void setGeneralState(int generalState) {
        this.generalState = generalState;
    }

    public String getScanner() {
        return scanner;
    }

    public void setScanner(String scanner) {
        this.scanner = scanner;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
