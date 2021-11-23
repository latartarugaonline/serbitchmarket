package it.ltm.scp.module.android.model.devices.system.gson.update;

/**
 * Created by HW64 on 28/03/2017.
 */

public class UpdateStatus {

    /** Constanti definite su iPOS */
    public final static int STATE_DOWNLOADING = 3;
    public final static int STATE_DOWNLOADED = 4;
    public final static int STATE_START = 5;
    public final static int STATE_SUCCESS = 1;
    public final static int STATE_FAIL = 2;

    private String status;
    private String message;
    private int generalState;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getGeneralState() {
        return generalState;
    }

    public void setGeneralState(int generalState) {
        this.generalState = generalState;
    }
}
