package it.ltm.scp.module.android.model.devices.pos.payment.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by HW64 on 20/09/2016.
 */
public class Payment {

    private double requestedAmount;
    private long requestedCurrencyCode;
    private String transactionType;
    private String status;
    private double authorizedAmount;
    private long authorizedCurrencyCode;
    private LmtResponse ltmResponse;
    private long saleRef;
    @SerializedName("_id")
    private String id;
    @SerializedName("_rev")
    private String rev;

    private String code;
    private String message;

    public double getRequestedAmount() {
        return requestedAmount;
    }

    public void setRequestedAmount(double requestedAmount) {
        this.requestedAmount = requestedAmount;
    }

    public long getRequestedCurrencyCode() {
        return requestedCurrencyCode;
    }

    public void setRequestedCurrencyCode(long requestedCurrencyCode) {
        this.requestedCurrencyCode = requestedCurrencyCode;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getAuthorizedAmount() {
        return authorizedAmount;
    }

    public void setAuthorizedAmount(double authorizedAmount) {
        this.authorizedAmount = authorizedAmount;
    }

    public long getAuthorizedCurrencyCode() {
        return authorizedCurrencyCode;
    }

    public void setAuthorizedCurrencyCode(long authorizedCurrencyCode) {
        this.authorizedCurrencyCode = authorizedCurrencyCode;
    }

    public LmtResponse getLtmResponse() {
        return ltmResponse;
    }

    public void setLtmResponse(LmtResponse ltmResponse) {
        this.ltmResponse = ltmResponse;
    }

    public long getSaleRef() {
        return saleRef;
    }

    public void setSaleRef(long saleRef) {
        this.saleRef = saleRef;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRev() {
        return rev;
    }

    public void setRev(String rev) {
        this.rev = rev;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "Payment{" +
                "requestedAmount=" + requestedAmount +
                ", requestedCurrencyCode=" + requestedCurrencyCode +
                ", transactionType='" + transactionType + '\'' +
                ", status='" + status + '\'' +
                ", authorizedAmount=" + authorizedAmount +
                ", authorizedCurrencyCode=" + authorizedCurrencyCode +
                ", ltmResponse=" + ltmResponse +
                ", saleRef=" + saleRef +
                ", id='" + id + '\'' +
                ", rev='" + rev + '\'' +
                ", code='" + code + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
