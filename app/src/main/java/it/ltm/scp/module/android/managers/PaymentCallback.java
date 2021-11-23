package it.ltm.scp.module.android.managers;

import it.ltm.scp.module.android.model.Result;

public interface PaymentCallback{
    void onPaymentResult(String callback, Result result);
}