package it.ltm.scp.module.android.model.devices.pos.payment.gson;

/**
 * Created by HW64 on 20/09/2016.
 */
public class PaymentType {

    private long amount;
    private long saleRef;
    private String slotTermId;
    private String paymentType;
    private String transactionTag;
    private String productCode;

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public long getSaleRef() {
        return saleRef;
    }

    public void setSaleRef(long saleRef) {
        this.saleRef = saleRef;
    }

    public String getSlotTermId() {
        return slotTermId;
    }

    public void setSlotTermId(String slotTermId) {
        this.slotTermId = slotTermId;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public String getTransactionTag() {
        return transactionTag;
    }

    public void setTransactionTag(String transactionTag) {
        this.transactionTag = transactionTag;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }
}
