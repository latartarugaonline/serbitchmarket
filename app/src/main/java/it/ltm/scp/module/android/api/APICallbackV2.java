package it.ltm.scp.module.android.api;

/**
 * Created by HW64 on 02/03/2017.
 */

public interface APICallbackV2<T> {
    void onResult(T result);
    void onError(int code, String message, Exception e);
}
