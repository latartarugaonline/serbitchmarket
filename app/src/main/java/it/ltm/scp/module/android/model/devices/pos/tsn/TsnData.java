package it.ltm.scp.module.android.model.devices.pos.tsn;

import com.google.gson.annotations.SerializedName;

/**
 * Created by HW64 on 21/02/2017.
 *
 * Rappresenta i dati di una tessera sanitaria
 */

public class TsnData {
    @SerializedName("TSNData")
    private String tsnData;
    @SerializedName("ReadType")
    private String readType;

    public String getTsnData() {
        return tsnData;
    }

    public void setTsnData(String tsnData) {
        this.tsnData = tsnData;
    }

    public String getReadType() {
        return readType;
    }

    public void setReadType(String readType) {
        this.readType = readType;
    }
}
