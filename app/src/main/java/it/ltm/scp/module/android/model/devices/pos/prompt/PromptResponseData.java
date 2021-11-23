package it.ltm.scp.module.android.model.devices.pos.prompt;

import com.google.gson.annotations.SerializedName;

/**
 * Created by HW64 on 22/06/2017.
 */

public class PromptResponseData {
    @SerializedName("Result")
    private String result;
    @SerializedName("HEXData")
    private String hexData;


    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getHexData() {
        return hexData;
    }

    public void setHexData(String hexData) {
        this.hexData = hexData;
    }

}
