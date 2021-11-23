package it.ltm.scp.module.android.model.devices.pos.gson;

import it.ltm.scp.module.android.model.devices.pos.AsyncWrapper;

/**
 * Created by HW64 on 21/10/2016.
 */
public class AuthAsyncWrapper extends AsyncWrapper{
    private PosResult<Auth> response;

    public PosResult<Auth> getResponse() {
        return response;
    }

    public void setResponse(PosResult<Auth> response) {
        this.response = response;
    }
}
