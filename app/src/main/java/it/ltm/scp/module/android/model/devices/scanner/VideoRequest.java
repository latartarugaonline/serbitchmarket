package it.ltm.scp.module.android.model.devices.scanner;

import com.google.gson.annotations.SerializedName;

public class VideoRequest {
    private String protocol;
    private boolean stopStreamingAfterSnapshot;

    @SerializedName("source_priority")
    private String sourcePriority;

    //TODO mettere configurazione


    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public boolean isStopStreamingAfterSnapshot() {
        return stopStreamingAfterSnapshot;
    }

    public void setStopStreamingAfterSnapshot(boolean stopStreamingAfterSnapshot) {
        this.stopStreamingAfterSnapshot = stopStreamingAfterSnapshot;
    }

    public String getSourcePriority() {
        return sourcePriority;
    }

    public void setSourcePriority(String sourcePriority) {
        this.sourcePriority = sourcePriority;
    }
}
