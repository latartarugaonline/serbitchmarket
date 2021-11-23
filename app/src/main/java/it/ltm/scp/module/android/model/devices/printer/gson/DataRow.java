package it.ltm.scp.module.android.model.devices.printer.gson;

import com.google.gson.annotations.SerializedName;


/**
 * Created by HW64 on 02/11/2016.
 */
public class DataRow {
    @SerializedName("class")
    private String factor;
    private String align;
    private String text;

    private DataRow(){}

    public DataRow(Integer columnClass, InputDataRowItem inputDataRowItem){
        this();
        this.factor = "c" + String.valueOf(columnClass);
        this.align = inputDataRowItem.getAlign();
        this.text = inputDataRowItem.getText();
    }

    public DataRow(InputCustomDataRow inputCustomDataRow){
        this();
        this.factor = "c" + inputCustomDataRow.getColumn();
        this.align = inputCustomDataRow.getAlign();
        this.text = inputCustomDataRow.getText();
    }

    public String getFactor() {
        return factor;
    }

    public void setFactor(String factor) {
        this.factor = factor;
    }

    public String getAlign() {
        return align;
    }

    public void setAlign(String align) {
        this.align = align;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
