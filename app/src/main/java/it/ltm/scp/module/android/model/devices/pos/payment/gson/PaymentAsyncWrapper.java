package it.ltm.scp.module.android.model.devices.pos.payment.gson;

import it.ltm.scp.module.android.model.devices.pos.AsyncWrapper;
import it.ltm.scp.module.android.model.devices.pos.gson.Auth;
import it.ltm.scp.module.android.model.devices.pos.gson.PosResult;

/**
 * Created by HW64 on 21/10/2016.
 */
public class PaymentAsyncWrapper extends AsyncWrapper{
    private Payment response;

    public Payment getResponse() {
        return response;
    }

    public void setResponse(Payment response) {
        this.response = response;
    }
}
