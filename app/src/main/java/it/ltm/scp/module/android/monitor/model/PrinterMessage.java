package it.ltm.scp.module.android.monitor.model;

import it.ltm.scp.module.android.model.Result;
import it.ltm.scp.module.android.model.devices.printer.gson.Document;

public class PrinterMessage {

    private String tag;
    private String ex;
    private String method;
    private Result result;
    private Document receipt;

    public PrinterMessage(String tag, String ex, String method, Result result, Document receipt) {
        this.tag = tag;
        this.ex = ex;
        this.method = method;
        this.result = result;
        this.receipt = receipt;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public Document getReceipt() {
        return receipt;
    }

    public void setReceipt(Document receipt) {
        this.receipt = receipt;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getEx() {
        return ex;
    }

    public void setEx(String ex) {
        this.ex = ex;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }
}
