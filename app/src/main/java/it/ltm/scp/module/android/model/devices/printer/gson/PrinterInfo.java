package it.ltm.scp.module.android.model.devices.printer.gson;

/**
 * Created by HW64 on 22/08/2016.
 */
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import it.ltm.scp.module.android.model.Error;
import it.ltm.scp.module.android.model.devices.printer.gson.status.Status;

public class PrinterInfo {

    @SerializedName("status")
    @Expose
    private Status status;
    @SerializedName("_links")
    @Expose
    private Links links;
    @SerializedName("error")
    @Expose
    private Error error;

    /**
     *
     * @return
     * The status
     */
    public Status getStatus() {
        return status;
    }

    /**
     *
     * @param status
     * The status
     */
    public void setStatus(Status status) {
        this.status = status;
    }

    /**
     *
     * @return
     * The links
     */
    public Links getLinks() {
        return links;
    }

    /**
     *
     * @param links
     * The _links
     */
    public void setLinks(Links links) {
        this.links = links;
    }

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }
}