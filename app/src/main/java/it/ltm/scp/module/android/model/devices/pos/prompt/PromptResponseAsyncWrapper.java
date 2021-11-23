package it.ltm.scp.module.android.model.devices.pos.prompt;

import it.ltm.scp.module.android.model.devices.pos.AsyncWrapper;
import it.ltm.scp.module.android.model.devices.pos.gson.PosResult;

/**
 * Created by HW64 on 10/07/2017.
 */

public class PromptResponseAsyncWrapper extends AsyncWrapper {
    private PosResult<PromptResponseData> response;

    public PosResult<PromptResponseData> getResponse() {
        return response;
    }

    public void setResponse(PosResult<PromptResponseData> response) {
        this.response = response;
    }
}
