package it.ltm.scp.module.android.model.devices.pos.payment.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by HW64 on 20/09/2016.
 */
public class PaymentData {

    @SerializedName("ResponseGTX")
    private String responseGTX;
    @SerializedName("ReceiptLinesQuantity")
    private String receiptLinesQuantity;
    @SerializedName("LineSize")
    private String lineSize;
    @SerializedName("ReceiptRows")
    private String receiptRows;

    public String getResponseGTX() {
        return responseGTX;
    }

    public void setResponseGTX(String responseGTX) {
        this.responseGTX = responseGTX;
    }

    public String getReceiptLinesQuantity() {
        return receiptLinesQuantity;
    }

    public void setReceiptLinesQuantity(String receiptLinesQuantity) {
        this.receiptLinesQuantity = receiptLinesQuantity;
    }

    public String getLineSize() {
        return lineSize;
    }

    public void setLineSize(String lineSize) {
        this.lineSize = lineSize;
    }

    public String getReceiptRows() {
        return receiptRows;
    }

    public void setReceiptRows(String receiptRows) {
        this.receiptRows = receiptRows;
    }

    @Override
    public String toString() {
        return "PaymentData{" +
                "responseGTX='" + responseGTX + '\'' +
                ", receiptLinesQuantity='" + receiptLinesQuantity + '\'' +
                ", lineSize='" + lineSize + '\'' +
                ", receiptRows='" + receiptRows + '\'' +
                '}';
    }
}
