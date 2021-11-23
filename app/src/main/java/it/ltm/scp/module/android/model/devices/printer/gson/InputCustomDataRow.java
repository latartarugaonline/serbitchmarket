package it.ltm.scp.module.android.model.devices.printer.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by HW64 on 05/05/2017.
 */

public class InputCustomDataRow {

    @SerializedName("alg")
    private String align;
    private String text;
    @SerializedName("column")
    private Integer column;
    @SerializedName("indexColumn")
    private Integer indexColumn;
    @SerializedName("font")
    private String font;

    public String getFont() {
        return font;
    }

    public void setFont(String font) {
        this.font = font;
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

    public Integer getColumn() {
        return column;
    }

    public void setColumn(Integer column) {
        this.column = column;
    }

    public Integer getIndexColumn() {
        return indexColumn;
    }

    public void setIndexColumn(Integer indexColumn) {
        this.indexColumn = indexColumn;
    }
}
