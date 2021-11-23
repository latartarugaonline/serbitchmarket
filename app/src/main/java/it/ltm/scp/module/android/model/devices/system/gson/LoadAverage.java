package it.ltm.scp.module.android.model.devices.system.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by HW64 on 18/10/2016.
 */
public class LoadAverage {

    @SerializedName("1min")
    private String oneMin;
    @SerializedName("5min")
    private String fiveMin;
    @SerializedName("15min")
    private String fifteenMin;

    public String getOneMin() {
        return oneMin;
    }

    public void setOneMin(String oneMin) {
        this.oneMin = oneMin;
    }

    public String getFiveMin() {
        return fiveMin;
    }

    public void setFiveMin(String fiveMin) {
        this.fiveMin = fiveMin;
    }

    public String getFifteenMin() {
        return fifteenMin;
    }

    public void setFifteenMin(String fifteenMin) {
        this.fifteenMin = fifteenMin;
    }
}
