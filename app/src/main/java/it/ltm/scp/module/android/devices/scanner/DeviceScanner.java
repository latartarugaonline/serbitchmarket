package it.ltm.scp.module.android.devices.scanner;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.util.List;

import it.ltm.scp.module.android.api.APICallbackV2;
import it.ltm.scp.module.android.api.scanner.ScannerAPI;
import it.ltm.scp.module.android.model.devices.scanner.ScannerInfo;
import it.ltm.scp.module.android.model.devices.scanner.ScannerInfoData;
import it.ltm.scp.module.android.model.devices.scanner.ScannerPatchConfig;
import it.ltm.scp.module.android.model.devices.scanner.ScannerSnapshotREST;
import it.ltm.scp.module.android.model.devices.scanner.VideoRequest;
import it.ltm.scp.module.android.model.devices.scanner.VideoResponse;
import it.ltm.scp.module.android.model.devices.scanner.ZebraConfig;
import it.ltm.scp.module.android.utils.Errors;

public class DeviceScanner {
    public static final int DELAY_MILLIS = 10;
    public static final int TIMEOUT_MRZ = 5 * 60 * 1000;
    private static DeviceScanner mInstance;
    private static Handler mMainHandler;
    private final String TAG = DeviceScanner.class.getSimpleName();

    public static final String STREAM_PROTOCOL = "http";
    public static final String BCR_PREVIEW_PRIORITY = "external";
    public static final boolean STOP_STREAMING_AFTER_SNAPSHOT = false;

    public Runnable mRestoreScannerFromMRZ = new Runnable() {
        @Override
        public void run() {
            Log.d(TAG, "run: MRZ TIMEOUT: restoring scanner...");
            disableReadMRZ(new APICallbackV2<Void>() {
                @Override
                public void onResult(Void result) {
                    Log.d(TAG, "onResult: TIMEOUT: restore Scanner MRZ -> OFF");
                }

                @Override
                public void onError(int code, String message, Exception e) {
                    Log.e(TAG, "onError: TIMEOUT: restore Scanner MRZ FAILED");
                    Log.e(TAG, "onError() called with: code = [" + code + "], message = [" + message + "], e = [" + e + "]", e);
                }
            });
        }
    };

    private DeviceScanner(){}

    public static synchronized DeviceScanner getInstance(){
        if(mInstance == null){
            mInstance = new DeviceScanner();
            mMainHandler = new Handler(Looper.getMainLooper());
        }
        return mInstance;
    }

    public void getScannerInfo(APICallbackV2<ScannerInfo> callback){
        new ScannerAPI().getScannerInfo(callback);
    }

    public void startVideoMode(final APICallbackV2<VideoResponse> callback){

        mMainHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                VideoRequest videoRequest = new VideoRequest();
                videoRequest.setProtocol(STREAM_PROTOCOL);
                videoRequest.setStopStreamingAfterSnapshot(STOP_STREAMING_AFTER_SNAPSHOT);
                videoRequest.setSourcePriority(BCR_PREVIEW_PRIORITY);

                new ScannerAPI().startVideoMode(new APICallbackV2<VideoResponse>() {
                    @Override
                    public void onResult(final VideoResponse result) {
                        Log.d(TAG, "onResult: start video mode result with " +
                                "code: " + result.getCode() + ", message: " + result.getMessage());
                        callback.onResult(result);

                    }

                    @Override
                    public void onError(int code, String message, Exception e) {
                        Log.e(TAG, "onError: " + message + ": " + code, e);
                        callback.onError(code, message, e);
                    }
                }, videoRequest);
            }
        }, DELAY_MILLIS);
    }

    /**
     * metodo per riattivare a best effort la modalità video del lettore bcr, ignorando la response.
     * utile per rimettere la modalità video subito dopo aver ricevuto lo snapshot in modo
     * che il server per lo streaming sia pronto in caso di un secondo scatto
     */
    public void startVideoModeSilently(){
        startVideoMode(new APICallbackV2<VideoResponse>() {
            @Override
            public void onResult(VideoResponse result) {
                Log.d(TAG, "onResult: start video mode (silent) result with " +
                        "code: " + result.getCode() + ", message: " + result.getMessage());
            }

            @Override
            public void onError(int code, String message, Exception e) {
                Log.e(TAG, "onError: " + message + ": " + code, e);
            }
        });
    }

    public void stopVideoMode(final APICallbackV2<VideoResponse> callback){
        mMainHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                new ScannerAPI().stopVideoMode(callback);
            }
        }, DELAY_MILLIS);
    }

    public void stopVideoModeSilently(){
        stopVideoMode(new APICallbackV2<VideoResponse>() {
            @Override
            public void onResult(VideoResponse result) {
                Log.d(TAG, "onResult: stop video mode (silent) called with: "  +
                        "code: " + result.getCode() + ", message: " + result.getMessage());
            }

            @Override
            public void onError(int code, String message, Exception e) {
                Log.e(TAG, "onError: " + message + ": " + code, e);
            }
        });
    }

    public void getSnapshot(APICallbackV2<ScannerSnapshotREST> callbackV2) {
        new ScannerAPI().getSnapshot(callbackV2);
    }

    public void startIdcMode(APICallbackV2<Void> callback, ZebraConfig zebraConfig){
        new ScannerAPI().startIdcMode(callback, zebraConfig);
    }

    public void stopIdcMode(final APICallbackV2<Void> callback){
        ScannerPatchConfig config = new ScannerPatchConfig();
        config.addPatch(ScannerPatchConfig.PATCH_CODE39, true);

        new ScannerAPI().patchScannerConfig(new APICallbackV2<Void>() {
                                                @Override
                                                public void onResult(Void result) {
                                                    Log.d(TAG, "onResult: config patched, stopping idc mode..");
                                                    new ScannerAPI().stopIdcMode(callback);
                                                }

                                                @Override
                                                public void onError(int code, String message, Exception e) {
                                                    callback.onError(code, message, e);
                                                }
                                            }
                , ScannerPatchConfig.ZEBRA_ID, config);


    }

    public void enableReadMRZ(final APICallbackV2<Void> callbackV2){
        ScannerPatchConfig MRZPatch = new ScannerPatchConfig();
        MRZPatch.addPatch(ScannerPatchConfig.PATCH_CODE39, false);
        MRZPatch.addPatch(ScannerPatchConfig.PATCH_ID_685, 20);
        MRZPatch.addPatch(ScannerPatchConfig.PATCH_ID_691, 3);

        new ScannerAPI().patchScannerConfig(new APICallbackV2<Void>() {
            @Override
            public void onResult(Void result) {
                Log.d(TAG, "onResult: MRZ ON, start timeout");
                mMainHandler.postDelayed(mRestoreScannerFromMRZ, TIMEOUT_MRZ);
                callbackV2.onResult(result);
            }

            @Override
            public void onError(int code, String message, Exception e) {
                callbackV2.onError(code, message, e);
            }
        }, ScannerPatchConfig.ZEBRA_ID, MRZPatch);
    }

    public void disableReadMRZ(final APICallbackV2<Void> callbackV2){
        //remove timeout if exist
        Log.d(TAG, "disableReadMRZ: removing timeout");
        mMainHandler.removeCallbacks(mRestoreScannerFromMRZ);
        ScannerPatchConfig MRZPatch = new ScannerPatchConfig();
        MRZPatch.addPatch(ScannerPatchConfig.PATCH_CODE39, true);
        MRZPatch.addPatch(ScannerPatchConfig.PATCH_ID_685, 24);
        MRZPatch.addPatch(ScannerPatchConfig.PATCH_ID_691, 1);

        new ScannerAPI().patchScannerConfig(callbackV2, ScannerPatchConfig.ZEBRA_ID, MRZPatch);
    }

    public void clearScannerConfigFromIpos(final APICallbackV2<Void> callbackV2){
        ZebraConfig zebraConfig = new ZebraConfig.Builder()
                .code39(true)
                .build();
        new ScannerAPI().putScannerConfig(callbackV2, ScannerPatchConfig.ZEBRA_ID, zebraConfig);
    }
}