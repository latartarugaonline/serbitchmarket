package it.ltm.scp.module.android.model.devices.pos.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by HW64 on 21/09/2016.
 */
public class ResultMessage {
    @SerializedName("Result")
    private String result;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
