package it.ltm.scp.module.android.api;

import android.util.Log;

import it.ltm.scp.module.android.model.Result;
import it.ltm.scp.module.android.monitor.LogWrapper;
import it.ltm.scp.module.android.utils.Errors;

import java.io.IOException;

import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by HW64 on 13/09/2016.
 */
public class APIErrorHandler {
    private final String TAG = APIErrorHandler.class.getSimpleName();

    protected void processError(final APICallback callback, Response response){
        String err = "";
        try {
            err += response.errorBody().string();
        } catch (Exception e) {
            Log.e(TAG, "", e);
            callback.onFinish(
                    new Result(
                            Errors.ERROR_NET_UNABLE_READ_ERROR_BODY,
                            Errors.getMap().get(Errors.ERROR_NET_UNABLE_READ_ERROR_BODY),
                            e.getMessage(),
                            null
                    )
            );
            return;
        }
        Log.e(TAG, err);
        callback.onFinish(
                new Result(Errors.ERROR_NET_SERVER_KO,
                        Errors.getMap().get(Errors.ERROR_NET_SERVER_KO),
                        "HTTP code: "
                                + response.code()
                                + ", message: "
                                + response.message()
                                + ", body: "
                                + err
                        ,
                        null));
    }

    protected void processResponseKO(final APICallbackV2 apiCallbackV2, Response response) throws IOException {
        //String err = response.errorBody().string();
        Log.e(TAG, "processResponseKO: " + response.toString());
        if(response.code() == 404){
            apiNotFound(apiCallbackV2);
            return;
        }
        ResponseBody responseBody = response.errorBody();
        String errorBody = responseBody == null? "" : responseBody.string();
        Exception exception = new Exception("HTTP CODE: "
                + response.code()
                + ", message: "
                + response.message()
                + ", body: "
                + errorBody
        );
        apiCallbackV2.onError(Errors.ERROR_NET_SERVER_KO,
                Errors.getMap().get(Errors.ERROR_NET_SERVER_KO), exception);
    }

    protected void processException(final APICallbackV2 apiCallbackV2, Throwable t, Call call){
        Log.e(TAG, "processException: "+ call.request().url().toString(), t);
        if(t instanceof SSLException){
            apiCallbackV2.onError(Errors.ERROR_SECURITY_CERTIFICATE_IPOS,
                    Errors.getMap().get(Errors.ERROR_SECURITY_CERTIFICATE_IPOS),
                    new Exception(t));
            return;
        }
        apiCallbackV2.onError(Errors.ERROR_NET_IO_IPOS,
                Errors.getMap().get(Errors.ERROR_NET_IO_IPOS),
                new Exception(t));
    }

    protected void processError(final APICallback callback, Throwable t){
        if(t instanceof SSLException){
            callback.onFinish(
                    new Result(Errors.ERROR_SECURITY_CERTIFICATE_IPOS,
                            Errors.getMap().get(Errors.ERROR_SECURITY_CERTIFICATE_IPOS),
                            LogWrapper.parseStackTrace(t),
                            null));
            return;
        }
        callback.onFinish(
                new Result(Errors.ERROR_NET_IO_IPOS,
                        Errors.getMap().get(Errors.ERROR_NET_IO_IPOS),
                        LogWrapper.parseStackTrace(t),
                        null));
    }

    protected void processError(final  APICallback callback, String rawResponse){
        callback.onFinish(
                new Result(Errors.ERROR_NET_SERVER_RESPONSE,
                        Errors.getMap().get(Errors.ERROR_NET_SERVER_RESPONSE),
                        rawResponse)
        );
    }

    protected void apiNotFound(final APICallback callback){
        Log.d(TAG, "apiNotFound() called");
        Result eResult = new Result(Errors.ERROR_NET_NOT_FOUND,
                Errors.getMap().get(Errors.ERROR_NET_NOT_FOUND),
                null);
        callback.onFinish(eResult);
    }

    protected void apiNotFound(final APICallbackV2 callback){
        Log.d(TAG, "apiNotFound() called");
        callback.onError(Errors.ERROR_NET_NOT_FOUND,
                Errors.getMap().get(Errors.ERROR_NET_NOT_FOUND),
                null);
    }

}
