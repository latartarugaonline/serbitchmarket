package it.ltm.scp.module.android.model.devices.pos.tsn;

import it.ltm.scp.module.android.model.devices.pos.AsyncWrapper;
import it.ltm.scp.module.android.model.devices.pos.gson.PosResult;

/**
 * Created by HW64 on 21/02/2017.
 */

public class TsnAsyncWrapper extends AsyncWrapper {
    private PosResult<TsnData> response;

    public PosResult<TsnData> getResponse() {
        return response;
    }

    public void setResponse(PosResult<TsnData> response) {
        this.response = response;
    }
}
