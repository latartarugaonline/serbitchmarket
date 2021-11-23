package it.ltm.scp.module.android.model;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;

/**
 * Created by HW64 on 15/05/2017.
 */

public class CustomHttpRequest {
    private String url;
    private int jsonSuccessResponseCode;
    private String method;
    private String callbackConfirm;
    private String callbackResult;
    private int numRetry;
    private long retryInterval;
    HashMap<String, String> formData;

    public CustomHttpRequest(String url, int jsonSuccessResponseCode, String method, String body, String callbackConfirm, String callbackResult, int numRetry, long retryInterval) {
        this.url = url;
        this.jsonSuccessResponseCode = jsonSuccessResponseCode;
        this.method = method;
        if(body != null){
            try {
                formData = new Gson().fromJson(body, new TypeToken<HashMap<String, String>>(){}.getType());
            } catch (Exception e){
                Log.e("CustomHttpRequest", "CustomHttpRequest: error parsing body \n", e);
                formData = null;
            }
        }
        this.callbackConfirm = callbackConfirm;
        this.callbackResult = callbackResult;
        this.numRetry = numRetry;
        this.retryInterval = retryInterval;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getJsonSuccessResponseCode() {
        return jsonSuccessResponseCode;
    }

    public void setJsonSuccessResponseCode(int jsonSuccessResponseCode) {
        this.jsonSuccessResponseCode = jsonSuccessResponseCode;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getCallbackConfirm() {
        return callbackConfirm;
    }

    public void setCallbackConfirm(String callbackConfirm) {
        this.callbackConfirm = callbackConfirm;
    }

    public String getCallbackResult() {
        return callbackResult;
    }

    public void setCallbackResult(String callbackResult) {
        this.callbackResult = callbackResult;
    }

    public int getNumRetry() {
        return numRetry;
    }

    public void setNumRetry(int numRetry) {
        this.numRetry = numRetry;
    }

    public long getRetryInterval() {
        return retryInterval;
    }

    public void setRetryInterval(long retryInterval) {
        this.retryInterval = retryInterval;
    }

    public HashMap<String, String> getFormData() {
        return formData;
    }

    public void setFormData(HashMap<String, String> formData) {
        this.formData = formData;
    }
}
