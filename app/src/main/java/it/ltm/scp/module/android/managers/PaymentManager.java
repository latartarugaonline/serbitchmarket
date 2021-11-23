package it.ltm.scp.module.android.managers;

import com.google.gson.Gson;

import it.ltm.scp.module.android.api.APICallback;
import it.ltm.scp.module.android.devices.pos.DevicePos;
import it.ltm.scp.module.android.devices.pos.PosUtils;
import it.ltm.scp.module.android.model.Result;
import it.ltm.scp.module.android.model.devices.pos.gson.PosResult;
import it.ltm.scp.module.android.model.devices.pos.payment.gson.Payment;
import it.ltm.scp.module.android.model.devices.pos.payment.gson.PaymentAsyncWrapper;
import it.ltm.scp.module.android.model.devices.pos.payment.gson.PaymentType;
import it.ltm.scp.module.android.utils.Errors;

/**
 * Created by HW64 on 24/10/2016.
 */
public class PaymentManager extends AsynchronousMode<PaymentAsyncWrapper>{
    private String jsCallback;
    private PaymentCallback listener;

    private final String TAG = PaymentManager.class.getSimpleName();

    public PaymentManager(PaymentCallback listener) {
        this.listener = listener;
    }

    public void pay(String json, String callback) {
        PaymentType payment = new Gson().fromJson(json, PaymentType.class);
        pay(payment, callback);
    }

    public void pay(PaymentType payment, String callback) {
        this.jsCallback = callback;
        DevicePos.getInstance().processPaymentAsync("sale", payment, new APICallback() {
            @Override
            public void onFinish(Result result) {
                if(result.getCode() == Errors.ERROR_OK){
                    PaymentAsyncWrapper wrapper = (PaymentAsyncWrapper) result.getData();
                    setRequestID(wrapper.getRequestID());
                    startChecker();
                } else if(result.getCode() == Errors.ERROR_NET_SERVER_KO){
                    PosResult posResult = (PosResult) result.getData();
                    listener.onPaymentResult(jsCallback, new Result(PosUtils.parsePosCode(posResult.getCode()),
                            PosUtils.getMessageFromErrorCode(posResult.getCode()),
                            null)
                    );
                } else {
                    listener.onPaymentResult(jsCallback, result);
                }
            }
        });
    }

    public void processPaymentEvent(PaymentAsyncWrapper wrapper){
        processEvent(wrapper);
    }

    @Override
    protected int getType() {
        return AsynchronousMode.TYPE_PAYMENT;
    }

    @Override
    protected void processEvent(PaymentAsyncWrapper event) {
        if(event.getRequestID().equals(getRequestID())){
            stopChecker();
            Payment payment = event.getResponse();
            if(payment.getCode() != null){
                int parsedCode = Integer.parseInt(payment.getCode());
                listener.onPaymentResult(jsCallback, new Result(PosUtils.parsePosCode(parsedCode),
                        PosUtils.getMessageFromErrorCode(parsedCode), null));
                return;
            }
            listener.onPaymentResult(jsCallback, new Result(Errors.ERROR_OK, payment));
        }
    }

}
