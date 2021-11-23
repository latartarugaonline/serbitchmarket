package it.ltm.scp.module.android.api.scanner;

import android.util.Log;

import com.google.gson.Gson;

import it.ltm.scp.module.android.api.APICallbackV2;
import it.ltm.scp.module.android.api.APIErrorHandler;
import it.ltm.scp.module.android.model.devices.scanner.VideoResponse;
import it.ltm.scp.module.android.utils.Errors;
import retrofit2.Response;

public class ScannerAPIErrorHandler extends APIErrorHandler {
    private final String TAG = this.getClass().getSimpleName();

    protected void processScannerResponseKO(final APICallbackV2 callback, Response response){
        String err = "";
        try {
            err += response.errorBody().string();
            Log.d(TAG, "processScannerResponseKO: " + err);
            if(response.code() == 404){
                super.apiNotFound(callback);
                return;
            }
            VideoResponse videoResponse = new Gson().fromJson(err, VideoResponse.class);
            switch (videoResponse.getCode()){
                case 503:
                case 2:
                    callback.onError(Errors.ERROR_BCR_NOT_PLUGGED,
                            Errors.getMap().get(Errors.ERROR_BCR_NOT_PLUGGED),
                            null);
                    break;
                default:
                    callback.onError(Errors.ERROR_BCR_API_GENERIC,
                                Errors.getMap().get(Errors.ERROR_BCR_API_GENERIC),
                                null);
                    break;
            }

        } catch (Exception e){
            callback.onError(
                            Errors.ERROR_NET_UNABLE_READ_ERROR_BODY,
                            Errors.getMap().get(Errors.ERROR_NET_UNABLE_READ_ERROR_BODY),
                            e
            );
        }
    }

}
