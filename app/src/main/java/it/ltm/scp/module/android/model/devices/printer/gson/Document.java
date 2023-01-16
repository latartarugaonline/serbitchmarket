package it.ltm.scp.module.android.model.devices.printer.gson;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by HW64 on 23/08/2016.
 */
public class Document {

    public static final int CUT_NONE = 0;
    public static final int CUT_PARTIAL = 1;
    public static final int CUT_FULL = 2;

    @SerializedName("data")
    @Expose
    private List<Data> data;

    public List<Data> getData() {
        return data;
    }

    public void setData(List<Data> data) {
        this.data = data;
    }

    public void addData(List<Data> mData) {
        if (this.data == null) {
            this.data = new LinkedList<>();
        }
        this.data.addAll(mData);
    }

    public void clear() {
        this.data.clear();
    }
}
