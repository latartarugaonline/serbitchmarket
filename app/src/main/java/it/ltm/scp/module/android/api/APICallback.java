package it.ltm.scp.module.android.api;

import it.ltm.scp.module.android.model.Result;

/**
 * Created by HW64 on 13/09/2016.
 *
 * Generic callback for API calls.
 */
public interface APICallback {
    void onFinish(Result result);
}
