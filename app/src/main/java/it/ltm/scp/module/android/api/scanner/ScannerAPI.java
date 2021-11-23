package it.ltm.scp.module.android.api.scanner;

import android.util.Log;

import java.io.IOException;

import it.ltm.scp.module.android.api.APICallbackV2;
import it.ltm.scp.module.android.api.RestAPIModule;
import it.ltm.scp.module.android.model.devices.scanner.ScannerInfo;
import it.ltm.scp.module.android.model.devices.scanner.ScannerPatchConfig;
import it.ltm.scp.module.android.model.devices.scanner.ScannerSnapshotREST;
import it.ltm.scp.module.android.model.devices.scanner.VideoRequest;
import it.ltm.scp.module.android.model.devices.scanner.VideoResponse;
import it.ltm.scp.module.android.model.devices.scanner.ZebraConfig;
import it.ltm.scp.module.android.utils.Errors;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ScannerAPI extends ScannerAPIErrorHandler {

    private final String TAG = ScannerAPI.class.getSimpleName();

    public void startVideoMode(final APICallbackV2<VideoResponse> callback, VideoRequest videoRequest) {
        Call<VideoResponse> videoResponseCall = RestAPIModule.getScannerInstance().startVideoMode(videoRequest);
        videoResponseCall.enqueue(new Callback<VideoResponse>() {
            @Override
            public void onResponse(Call<VideoResponse> call, Response<VideoResponse> response) {
                if(response.isSuccessful()){
                    callback.onResult(response.body());
                } else {
                    processScannerResponseKO(callback, response);
                }
            }

            @Override
            public void onFailure(Call<VideoResponse> call, Throwable t) {
                Log.e(TAG, "onFailure: ", t);
                processException(callback, t, call);
            }
        });
    }

    public void stopVideoMode(final APICallbackV2<VideoResponse> callback) {
        Call<VideoResponse> videoResponseCall = RestAPIModule.getScannerInstance().stopVideoMode();
        videoResponseCall.enqueue(new Callback<VideoResponse>() {
            @Override
            public void onResponse(Call<VideoResponse> call, Response<VideoResponse> response) {
                if(response.isSuccessful()){
                    callback.onResult(response.body());
                } else {
                    processScannerResponseKO(callback, response);
                }
            }

            @Override
            public void onFailure(Call<VideoResponse> call, Throwable t) {
                Log.e(TAG, "onFailure: ", t);
                processException(callback, t, call);
            }
        });
    }

    public void getSnapshot(final APICallbackV2<ScannerSnapshotREST> callbackV2){
        Call<ScannerSnapshotREST> scannerSnapshotRESTCall = RestAPIModule.getScannerInstance().getSnapshot();
        scannerSnapshotRESTCall.enqueue(new Callback<ScannerSnapshotREST>() {
            @Override
            public void onResponse(Call<ScannerSnapshotREST> call, Response<ScannerSnapshotREST> response) {
                if(response.isSuccessful()){
                    callbackV2.onResult(response.body());
                } else { //TODO gestire meglio il KO
                    try {
                        processResponseKO(callbackV2, response);
                    } catch (IOException e) {
                        callbackV2.onError(Errors.ERROR_NET_UNABLE_READ_ERROR_BODY,
                                Errors.getMap().get(Errors.ERROR_NET_UNABLE_READ_ERROR_BODY),
                                e);
                    }
                }
            }

            @Override
            public void onFailure(Call<ScannerSnapshotREST> call, Throwable t) {
                Log.e(TAG, "onFailure: " + call.request().url().toString(), t);
                processException(callbackV2, t, call);
            }
        });
    }

    public void getScannerInfo(final APICallbackV2<ScannerInfo> callback){
        Call<ScannerInfo> getScannerInfoCall = RestAPIModule.getScannerInstance().getScannerInfo();
        getScannerInfoCall.enqueue(new Callback<ScannerInfo>() {
            @Override
            public void onResponse(Call<ScannerInfo> call, Response<ScannerInfo> response) {
                Log.d(TAG, "onResponse: " + response.toString());
                if(response.isSuccessful()){
                    callback.onResult(response.body());
                } else {
                    try {
                        processResponseKO(callback, response);
                    } catch (IOException e) {
                        callback.onError(Errors.ERROR_NET_UNABLE_READ_ERROR_BODY,
                                Errors.getMap().get(Errors.ERROR_NET_UNABLE_READ_ERROR_BODY),
                                e);
                    }
                }
            }

            @Override
            public void onFailure(Call<ScannerInfo> call, Throwable t) {
                Log.e(TAG, "onFailure: " + call.request().url().toString(), t);
                processException(callback, t, call);
            }
        });
    }

    public void startIdcMode(final APICallbackV2<Void> callback, ZebraConfig zebraConfig){
        Call<Void> startIdcModeCall = RestAPIModule.getScannerInstance().startIdcMode(zebraConfig);
        startIdcModeCall.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Log.d(TAG, "onResponse: " + response.toString());
                if(response.isSuccessful()){
                    callback.onResult(response.body());
                } else {
                    /*try {
                        processResponseKO(callback, response);
                    } catch (IOException e) {
                        callback.onError(Errors.ERROR_NET_UNABLE_READ_ERROR_BODY,
                                Errors.getMap().get(Errors.ERROR_NET_UNABLE_READ_ERROR_BODY),
                                e);
                    }*/
                    processScannerResponseKO(callback, response);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "onFailure: " + call.request().url().toString(), t);
                processException(callback, t, call);
            }
        });
    }

    public void stopIdcMode(final APICallbackV2<Void> callback){
        Call<Void> stopIdcModeCall = RestAPIModule.getScannerInstance().stopIdcMode();
        stopIdcModeCall.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Log.d(TAG, "onResponse: " + response.toString());
                if(response.isSuccessful()){
                    callback.onResult(response.body());
                } else {
                    /*try {
                        processResponseKO(callback, response);
                    } catch (IOException e) {
                        callback.onError(Errors.ERROR_NET_UNABLE_READ_ERROR_BODY,
                                Errors.getMap().get(Errors.ERROR_NET_UNABLE_READ_ERROR_BODY),
                                e);
                    }*/
                    processScannerResponseKO(callback, response);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "onFailure: " + call.request().url().toString(), t);
                processException(callback, t, call);
            }
        });
    }

    public void patchScannerConfig(final APICallbackV2<Void> callback, String scannerID, ScannerPatchConfig config){
        Call<Void> voidCall = RestAPIModule.getScannerInstance().patchScannerConfig(scannerID, config);
        voidCall.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Log.d(TAG, "patchScannerConfig: onResponse: " + response.toString());
                if(response.isSuccessful()){
                    callback.onResult(response.body());
                } else {
                    processScannerResponseKO(callback, response);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "patchScannerConfig: onFailure: " + call.request().url().toString(), t);
                processException(callback, t, call);
            }
        });
    }

    public void putScannerConfig(final APICallbackV2<Void> callback, String scannerID, ZebraConfig config){
        Call<Void> voidCall = RestAPIModule.getScannerInstance().putScannerConfig(scannerID, config);
        voidCall.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Log.d(TAG, "putScannerConfig: onResponse: " + response.toString());
                if(response.isSuccessful()){
                    callback.onResult(response.body());
                } else {
                    processScannerResponseKO(callback, response);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "putScannerConfig: onFailure: " + call.request().url().toString(), t);
                processException(callback, t, call);
            }
        });
    }

}
