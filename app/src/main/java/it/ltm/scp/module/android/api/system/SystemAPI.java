package it.ltm.scp.module.android.api.system;

import android.util.Log;

import it.ltm.scp.module.android.api.APICallback;
import it.ltm.scp.module.android.api.APICallbackV2;
import it.ltm.scp.module.android.api.APIErrorHandler;
import it.ltm.scp.module.android.api.RestAPIModule;
import it.ltm.scp.module.android.model.Error;
import it.ltm.scp.module.android.model.Result;
import it.ltm.scp.module.android.model.devices.system.gson.SystemInfo;
import it.ltm.scp.module.android.model.devices.system.gson.update.Update;
import it.ltm.scp.module.android.model.devices.system.gson.update.UpdateConfig;
import it.ltm.scp.module.android.model.devices.system.gson.update.UpdateRepository;
import it.ltm.scp.module.android.model.devices.system.gson.update.UpdateStatus;
import it.ltm.scp.module.android.utils.Errors;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by HW64 on 18/10/2016.
 */
public class SystemAPI extends APIErrorHandler {

    private String TAG = SystemAPI.class.getSimpleName();


    public void getSystemInfo(final APICallback apiCallback){
        Call<SystemInfo> systemInfoCall = RestAPIModule.getSystemInstance().getSystemInfo();
        systemInfoCall.enqueue(new Callback<SystemInfo>() {
            @Override
            public void onResponse(Call<SystemInfo> call, Response<SystemInfo> response) {
                if(response.isSuccessful()){
                    apiCallback.onFinish(new Result(Errors.ERROR_OK,
                            response.body()));
                } else {
                    processError(apiCallback, response);
                }
            }

            @Override
            public void onFailure(Call<SystemInfo> call, Throwable t) {
                Log.e(TAG, "onFailure: ", t);
                processError(apiCallback, t);
            }
        });
    }

    public void checkForUpdate(final APICallbackV2<Update> callbackV2){
        Call<Update> updateCall = RestAPIModule.getSystemInstance().checkForUpdate();
        updateCall.enqueue(new Callback<Update>() {
            @Override
            public void onResponse(Call<Update> call, Response<Update> response) {
                Log.d(TAG, "onResponse: ");
                try {
                    if(response.isSuccessful()){
                        callbackV2.onResult(response.body());
                    } else {
                        processResponseKO(callbackV2, response);
                    }
                } catch (Exception e){
                    Log.e(TAG, "onResponse: ", e);
                    callbackV2.onError(Errors.ERROR_NET_UNABLE_READ_ERROR_BODY,
                            Errors.getMap().get(Errors.ERROR_NET_UNABLE_READ_ERROR_BODY),
                            e);
                }
            }

            @Override
            public void onFailure(Call<Update> call, Throwable t) {
                Log.e(TAG, "onFailure: ", t);
                processException(callbackV2, t, call);
            }
        });
    }

    public void doUpdate(UpdateRepository repository, final APICallbackV2<UpdateStatus> callbackV2){
        Call<UpdateStatus> updateStatusCall = RestAPIModule.getSystemInstance().doUpdate(repository);
        updateStatusCall.enqueue(new Callback<UpdateStatus>() {
            @Override
            public void onResponse(Call<UpdateStatus> call, Response<UpdateStatus> response) {
                Log.d(TAG, "onResponse: ");
                try {
                    if(response.isSuccessful()){
                        callbackV2.onResult(response.body());
                    } else {
                        processResponseKO(callbackV2, response);
                    }
                } catch (Exception e){
                    Log.e(TAG, "onResponse: ", e);
                    callbackV2.onError(Errors.ERROR_NET_UNABLE_READ_ERROR_BODY,
                            Errors.getMap().get(Errors.ERROR_NET_UNABLE_READ_ERROR_BODY),
                            e);
                }
            }

            @Override
            public void onFailure(Call<UpdateStatus> call, Throwable t) {
                Log.e(TAG, "onFailure: ", t);
                processException(callbackV2, t, call);
            }
        });
    }

    public void getUpdateConfig(final APICallbackV2<UpdateConfig> callback){
        Call<UpdateConfig> updateConfigCall = RestAPIModule.getSystemInstance().getUpdateConfig();
        updateConfigCall.enqueue(new Callback<UpdateConfig>() {
            @Override
            public void onResponse(Call<UpdateConfig> call, Response<UpdateConfig> response) {
                try {
                    if(response.isSuccessful()) {
                        UpdateConfig config = response.body();
                        callback.onResult(config);
                    } else if(response.code() == 404){ //TODO ripristinare dopo app version 0.4.8
                        //manca configurazione
                        callback.onError(404, "", null);
                    } else {
                        processResponseKO(callback, response);
                    }
                } catch (Exception e){
                    Log.e(TAG, "onResponse: ", e);
                    callback.onError(Errors.ERROR_NET_UNABLE_READ_ERROR_BODY,
                            Errors.getMap().get(Errors.ERROR_NET_UNABLE_READ_ERROR_BODY),
                            e);
                }
            }

            @Override
            public void onFailure(Call<UpdateConfig> call, Throwable t) {
                processException(callback, t, call);
            }
        });
    }

    public void putUpdateConfig(final APICallbackV2<String> callback, UpdateConfig config){
        Call<Void> call = RestAPIModule.getSystemInstance().putUpdateConfig(config);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                try {
                    if(response.isSuccessful()){
                        callback.onResult("success");
                    } else {
                        processResponseKO(callback, response);
                    }
                } catch (Exception e){
                    Log.e(TAG, "onResponse: ", e);
                    callback.onError(Errors.ERROR_NET_UNABLE_READ_ERROR_BODY,
                            Errors.getMap().get(Errors.ERROR_NET_UNABLE_READ_ERROR_BODY),
                            e);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                processException(callback, t, call);
            }
        });
    }
}
