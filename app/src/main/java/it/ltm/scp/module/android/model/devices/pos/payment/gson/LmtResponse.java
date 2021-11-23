package it.ltm.scp.module.android.model.devices.pos.payment.gson;

/**
 * Created by HW64 on 20/09/2016.
 */
public class LmtResponse {
    private int result;
    private String description;
    private PaymentData data;

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public PaymentData getData() {
        return data;
    }

    public void setData(PaymentData data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "LmtResponse{" +
                "result=" + result +
                ", description='" + description + '\'' +
                ", data=" + data +
                '}';
    }
}
