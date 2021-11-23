package it.ltm.scp.module.android.model.sm.gson;

/**
 * Created by HW64 on 19/10/2016.
 */
public class LisData {
    private String input = "";
    private String amount = "";
    private String emitter = "";
    private String serviceUrl = "";
    private String ltmCode = "";
    private String posCode = "";
    private String clientCode = "";

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public String getEmitter() {
        return emitter;
    }

    public void setEmitter(String emitter) {
        this.emitter = emitter;
    }

    public String getServiceUrl() {
        return serviceUrl;
    }

    public void setServiceUrl(String serviceUrl) {
        this.serviceUrl = serviceUrl;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getLtmCode() {
        return ltmCode;
    }

    public void setLtmCode(String ltmCode) {
        this.ltmCode = ltmCode;
    }

    public String getPosCode() {
        return posCode;
    }

    public void setPosCode(String posCode) {
        this.posCode = posCode;
    }

    public String getClientCode() {
        return clientCode;
    }

    public void setClientCode(String clientCode) {
        this.clientCode = clientCode;
    }
}
