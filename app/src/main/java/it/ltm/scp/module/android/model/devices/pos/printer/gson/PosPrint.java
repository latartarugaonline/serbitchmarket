package it.ltm.scp.module.android.model.devices.pos.printer.gson;

/**
 * Created by HW64 on 20/09/2016.
 */
public class PosPrint {
    private int type;
    private String str;

    public int getType() {
        return type;
    }

    public PosPrint() {
    }

    public PosPrint(int type, String str) {
        this.type = type;
        this.str = str;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getStr() {
        return str;
    }

    public void setStr(String str) {
        this.str = str;
    }
}
