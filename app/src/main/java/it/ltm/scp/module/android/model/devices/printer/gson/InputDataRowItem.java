package it.ltm.scp.module.android.model.devices.printer.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by HW64 on 02/11/2016.
 */
public class InputDataRowItem {
    @SerializedName("alg")
    private String align;
    private String text;
    private Integer clm;

    public Integer getClm() {
        return clm;
    }

    public void setClm(Integer clm) {
        this.clm = clm;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getAlign() {
        return align;
    }

    public void setAlign(String align) {
        this.align = align;
    }
}
