package it.ltm.scp.module.android.model.devices.pos;

/**
 * Created by HW64 on 20/02/2017.
 *
 * Oggetto base che rappresenta un evento ricevuto dalla WebSocket
 */

public class AsyncWrapper {
    private Integer code;
    private String requestID;
    private String requestStatus;
    private Long requestTimestamp;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getRequestID() {
        return requestID;
    }

    public void setRequestID(String requestID) {
        this.requestID = requestID;
    }

    public String getRequestStatus() {
        return requestStatus;
    }

    public void setRequestStatus(String requestStatus) {
        this.requestStatus = requestStatus;
    }

    public Long getRequestTimestamp() {
        return requestTimestamp;
    }

    public void setRequestTimestamp(Long requestTimestamp) {
        this.requestTimestamp = requestTimestamp;
    }
}
