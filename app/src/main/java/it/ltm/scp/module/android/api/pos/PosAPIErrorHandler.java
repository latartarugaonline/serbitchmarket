package it.ltm.scp.module.android.api.pos;

import android.util.Log;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javax.net.ssl.SSLException;

import it.ltm.scp.module.android.api.APICallback;
import it.ltm.scp.module.android.api.APIErrorHandler;
import it.ltm.scp.module.android.devices.pos.PosUtils;
import it.ltm.scp.module.android.model.Result;
import it.ltm.scp.module.android.model.devices.pos.gson.PosResult;
import it.ltm.scp.module.android.utils.Errors;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by HW64 on 30/10/2016.
 */
public class PosAPIErrorHandler extends APIErrorHandler {

    private String TAG = PosAPIErrorHandler.class.getSimpleName();

    protected void processPosError(final APICallback callback, Response response){
        String err = "";
        PosResult errorResult = null;
        try {
            err += response.errorBody().string();
            errorResult = parsePosErrorJson(err);
            String message = PosUtils.getMessageFromErrorCode(errorResult.getCode());

            Log.e(TAG, err);
            callback.onFinish(new Result(PosUtils.parsePosCode(errorResult.getCode()),
                    message,
                    null));
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
        }

    }

    protected void processPosResponse(final APICallback callback, Response response){
        String err = "";
        PosResult errorResult = null;
        try {
            if(response.isSuccessful()){
                callback.onFinish(new Result(Errors.ERROR_OK,
                        response.body()));
            } else {
                err += response.errorBody().string();
                errorResult = parsePosErrorJson(err);
                String message = PosUtils.getMessageFromErrorCode(errorResult.getCode());

                Log.e(TAG, err);
                callback.onFinish(new Result(PosUtils.parsePosCode(errorResult.getCode()),
                        message,
                        null));
            }
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
        }
    }

    protected void processPosException(Call call, Throwable t, APICallback callback){
        Log.e(TAG, "processPosException: " + call.request().url().toString(), t);
        if(t instanceof SSLException){
            callback.onFinish(
                    new Result(Errors.ERROR_SECURITY_CERTIFICATE_IPOS,
                            Errors.getMap().get(Errors.ERROR_SECURITY_CERTIFICATE_IPOS),
                            t.getMessage(),
                            null));
            return;
        }
        callback.onFinish(
                new Result(Errors.ERROR_NET_IO_IPOS,
                        Errors.getMap().get(Errors.ERROR_NET_IO_IPOS),
                        t.getMessage(),
                        null));
    }

    private PosResult parsePosErrorJson(String json) throws Exception {
        PosResult posResult = new PosResult();
        JsonElement element = new JsonParser().parse(json);
        JsonObject mainObj = element.getAsJsonObject();  //Ex: {"code":"107","message":"ICT220 Unreachable"}
        String jsonCode = mainObj.get("code").getAsString();
        String jsonMessage = mainObj.get("message").getAsString();

        posResult.setCode(Integer.parseInt(jsonCode));
        posResult.setMessage(jsonMessage);
        return posResult;
    }


}
