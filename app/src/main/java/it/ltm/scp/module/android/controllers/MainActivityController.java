package it.ltm.scp.module.android.controllers;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Base64;
import android.util.Log;
import android.webkit.CookieManager;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import it.ltm.scp.module.android.App;
import it.ltm.scp.module.android.BuildConfig;
import it.ltm.scp.module.android.api.APICallback;
import it.ltm.scp.module.android.api.APICallbackV2;
import it.ltm.scp.module.android.devices.display.DeviceDisplay;
import it.ltm.scp.module.android.devices.pos.DevicePos;
import it.ltm.scp.module.android.devices.pos.PosUtils;
import it.ltm.scp.module.android.devices.scanner.DeviceScanner;
import it.ltm.scp.module.android.devices.terminal.TerminalManagerFactory;
import it.ltm.scp.module.android.managers.APMAppManager;
import it.ltm.scp.module.android.managers.ConnectionManager;
import it.ltm.scp.module.android.managers.ConnectionManagerFactory;
import it.ltm.scp.module.android.managers.PaymentCallback;
import it.ltm.scp.module.android.managers.TerminalStatusManager;
import it.ltm.scp.module.android.managers.PaymentManager;
import it.ltm.scp.module.android.devices.printer.DevicePrinter;
import it.ltm.scp.module.android.devices.system.DeviceSystem;
import it.ltm.scp.module.android.managers.PictureSessionManager;
import it.ltm.scp.module.android.managers.PrinterManager;
import it.ltm.scp.module.android.managers.PromptManager;
import it.ltm.scp.module.android.managers.TsnManager;
import it.ltm.scp.module.android.managers.UploadManager;
import it.ltm.scp.module.android.model.CIE;
import it.ltm.scp.module.android.managers.cie.CIEReader;
import it.ltm.scp.module.android.managers.cie.CIEReaderCallback;
import it.ltm.scp.module.android.model.RestData;
import it.ltm.scp.module.android.model.Result;
import it.ltm.scp.module.android.model.devices.pos.gson.Auth;
import it.ltm.scp.module.android.model.devices.pos.gson.AuthAsyncWrapper;
import it.ltm.scp.module.android.model.devices.pos.gson.PosInfo;
import it.ltm.scp.module.android.model.devices.pos.payment.gson.PaymentAsyncWrapper;
import it.ltm.scp.module.android.model.devices.pos.payment.gson.PaymentType;
import it.ltm.scp.module.android.model.devices.pos.prompt.PromptRequest;
import it.ltm.scp.module.android.model.devices.pos.prompt.PromptResponseAsyncWrapper;
import it.ltm.scp.module.android.model.devices.pos.tsn.TsnAsyncWrapper;
import it.ltm.scp.module.android.model.devices.printer.gson.Document;
import it.ltm.scp.module.android.managers.LisManager;
import it.ltm.scp.module.android.managers.secure.Authenticator;
import it.ltm.scp.module.android.managers.secure.SecureManager;
import it.ltm.scp.module.android.model.devices.printer.gson.status.Status;
import it.ltm.scp.module.android.model.devices.scanner.ImageRequest;
import it.ltm.scp.module.android.model.devices.scanner.ImageRequestWrapper;
import it.ltm.scp.module.android.model.devices.scanner.ScannerInfo;
import it.ltm.scp.module.android.model.devices.scanner.ScannerSnapshot;
import it.ltm.scp.module.android.model.devices.scanner.ScannerStatus;
import it.ltm.scp.module.android.model.devices.scanner.ScannerUpdate;
import it.ltm.scp.module.android.model.devices.system.gson.update.UpdateStatus;
import it.ltm.scp.module.android.ui.MainActivity;
import it.ltm.scp.module.android.utils.AppUtils;
import it.ltm.scp.module.android.utils.CameraUtils;
import it.ltm.scp.module.android.utils.Constants;
import it.ltm.scp.module.android.utils.Errors;
import it.ltm.scp.module.android.utils.Properties;

import static android.content.Context.ACTIVITY_SERVICE;

/**
 * Created by HW64 on 24/11/2016.
 */

public class MainActivityController extends WebSocketController implements  Authenticator.AuthenticatorCallback,
        PaymentCallback,
        TsnManager.TsnListener,
        LisManager.LisCallback,
        PromptManager.PromptListener {

    // view
    private WeakReference<MainActivity> mView;

    // logic
    private Authenticator mAuthenticator;
    private String mPosInfoCallbackName;
    private String mCameraJsCallbackName;
    private PaymentManager mPaymentManager;
    private APMAppManager mAPMAppManager;
    private TsnManager mTsnManager;
    private LisManager mLisManager;
    private PromptManager mPromptManager;
//    private TerminalStatusManager mIposStatusManager;
    private Handler mMainHandler;
    private Runnable mRedirectHomePagTask = new Runnable() {
        @Override
        public void run() {
            reloadMainPage();
        }
    };

    private final int STATE_LOADING = 0;
    private final int STATE_FINISH_OK = 1;
    private final int STATE_FINISH_KO = 2;
    private final int STATE_CONNECTING = 3;

    private final int TIMER_IDLE_WEBVIEW = 10 * 60 * 1000;  //10min

    private final int SANDBOX_ACTIVITY_RESULT_REQUEST_CODE = 12345;

    private final String TAG = MainActivityController.class.getSimpleName();

    public MainActivityController(MainActivity activity) {
        this.mView = new WeakReference<MainActivity>(activity);
        mAuthenticator = new Authenticator(activity.getApplicationContext(), this);
        mPaymentManager = new PaymentManager(this);
        mAPMAppManager = new APMAppManager(this);
        mTsnManager = new TsnManager(this);
        mLisManager = new LisManager(activity.getApplicationContext(), this);
        mPromptManager = new PromptManager(this);
        ConnectionManagerFactory.getConnectionManagerInstance().init(activity.getApplicationContext());
        mMainHandler = new Handler(Looper.getMainLooper());
    }

    public APMAppManager getAPMAppManager()
    {
        return mAPMAppManager;
    }

    private MainActivity getView() {
        return mView.get();
    }

    @Override
    public void attach(Context context) {
        super.attach(context);
        //set default printer params
        DevicePrinter.getInstance().setLocalDefaultParam();
        DeviceScanner.getInstance().stopVideoModeSilently();
    }

    @Override
    public void destroy() {
        super.destroy();
//        mIposStatusManager.removeListeners();
        mPaymentManager = null;
        mAPMAppManager = null;
        mLisManager = null;
        mAuthenticator.kill();
        mAuthenticator = null;

        stopIdleTimer();
    }

    public void startIdleTimer(){
        stopIdleTimer();
        Log.d(TAG, "startIdleTimer: ");
        mMainHandler.postDelayed(mRedirectHomePagTask, TIMER_IDLE_WEBVIEW);
    }

    public void stopIdleTimer(){
        Log.d(TAG, "stopIdleTimer: ");
        mMainHandler.removeCallbacks(mRedirectHomePagTask);
    }

    public void sendCameraResult(Result result) {
        sendCallbackToJs(result, mCameraJsCallbackName);
    }

    public void sendMultiImageCameraResult(){
        ArrayList<String> images = CameraUtils.getCacheList();
        String jsonStart = "{\"code\":0, \"data\":[";
        String jsonEnd = "]}";
        for (String image: images){
            jsonStart += ("\"" + image + "\",");
        }
        jsonStart = jsonStart.substring(0, jsonStart.length() -1);
        jsonStart += jsonEnd;
        CameraUtils.clearCache();
        try {
            getView().sendRawStringToJs(jsonStart, mCameraJsCallbackName);
        } catch (Exception e){
            Log.e(TAG, "sendMultiImageCameraResult: ", e);
        }
    }


    /**
     * @see PaymentManager
     */

    @Override
    public void onPaymentResult(String callback, Result result) {
        try {
            getView().sendCallbackToJs(result, callback);
        } catch (NullPointerException e) {
            return;
        }

    }

    /**
     * @see it.ltm.scp.module.android.managers.TsnManager
     */
    @Override
    public void onTsnComplete(String callback, Result result) {
        try {
            getView().sendCallbackToJs(result, callback);
        } catch (Exception e){ return; }
    }


    /**
     * @see LisManager
     */

    @Override
    public void onLisComplete(String url, String html) {
        try {
            if (!getView().isAuthPending()) {
                if (url != null && html != null) {
                    Log.e(TAG, "onLisComplete: WEBVIEW LOAD - START");
                    getView().loadWebViewDataWithBaseURL(url, html);
                } else {
                    getView().onLisComplete();
                }
            }
        } catch (NullPointerException e) {
            return;
        }
    }

    /**
     * @see PromptManager
     */
    @Override
    public void onPromptComplete(String callback, Result result) {
        sendCallbackToJs(result, callback);
    }

    @Override
    public void onSendDataToWebView(String url, byte[] postData) {
        try {
            if (!getView().isAuthPending()) {
                if (url != null && postData != null) {
                    Log.e(TAG, "postDataToWebView: WEBVIEW LOAD - START");
                    getView().postDataToWebView(url, postData);
                }
                getView().onLisComplete();
            }
        }catch (Exception e) {
            Log.i(TAG, "onSendDataToWebView Exception " + e.getMessage());
            Log.i(TAG, Log.getStackTraceString(e));
            return;
        }
    }

    @Override
    public void onSendDataToSandbox(String scheme, String domain, String appCode, String posCode, String clientCode)
    {
        try
        {
            if(!getView().isAuthPending())
            {
                Intent externalIntent = getView().getPackageManager().getLaunchIntentForPackage(domain);
                externalIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                externalIntent.setPackage(null);

                Auth authData = AppUtils.getAuthData(getView().getApplicationContext());
                JsonObject obj = new JsonObject();

                if(AppUtils.isTokenValid(authData.getTokenExpiryDate()))
                {
                    obj.addProperty("serviceUrl", Properties.get(Constants.PROP_URL_SERVICE_MARKET_BASE));
                    obj.addProperty("appCode", appCode);
                    obj.addProperty("userCode", authData.getUserCode());
                    obj.addProperty("clientCode", clientCode);
                    obj.addProperty("posCode", posCode);
                    obj.addProperty("ltmToken", SecureManager.getInstance().decryptString(authData.getToken()));
                    obj.addProperty("barCode", mLisManager.getCurrentBarcode());

                    Set<Map.Entry<String, JsonElement>> set = obj.entrySet();
                    for(Map.Entry<String, JsonElement> entry : set)
                    {
                        String key = entry.getKey();
                        String value = entry.getValue().getAsString();
                        externalIntent.putExtra(key, value);
                    }
                    getView().startActivityForResult(externalIntent, SANDBOX_ACTIVITY_RESULT_REQUEST_CODE);
                }
                else
                {
                    Log.d(TAG, "Token is invalid. About to reauth...");
                    onBarcodeReauth(null);
                }

                if(mLisManager != null)
                    mLisManager.stopRunning();

                getView().onLisComplete();
            }
        }catch (Exception e) {
            Log.i(TAG, "onSendDataToSandbox Exception " + e.getMessage());
            Log.i(TAG, Log.getStackTraceString(e));
            return;
        }
    }

    @Override
    public void onBarcodeReceived(String barcode) {
        try {
            if (!getView().isAuthPending())
                getView().sendBarcodeToJs(barcode);
        } catch (NullPointerException e) {
            return;
        }

    }

    @Override
    public void onBarcodeError(String message) {
        try {
            getView().processBarcodeStatus(message, true, false);
        } catch (NullPointerException e) {
            return;
        }

    }

    @Override
    public void onBarcodeRedirect(String message) {
        try {
//            Toast.makeText(getView(), message, Toast.LENGTH_SHORT).show();
            message += " Reindirizzamento verso Service Market in corso.";
            getView().processBarcodeStatus(message, false, false);
            if(!reloadMainPage()){
                mMainHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(mLisManager != null)
                            mLisManager.stopRunning();
                        getView().onLisComplete();
                    }
                },
                2000);
            }
        } catch (NullPointerException e) {
            return;
        }

    }

    @Override
    public void onBarcodeMessage(String message) {
        try {
            if (!getView().isAuthPending()) {
                getView().processBarcodeStatus(message, false, false);
            }
        } catch (NullPointerException e) {
            return;
        }

    }

    @Override
    public void onBarcodeReauth(PosInfo posInfo) {
        try {
            if (!getView().isAuthPending()) {
                reauth(null, posInfo);
                getView().processBarcodeStatus("Errore di autenticazione.", false, false);
            }
        } catch (NullPointerException e) {
            return;
        }

    }

    @Override
    public void onBarcodeErrorICT(String error, int code) {
        try {
            if (!getView().isAuthPending()) {
                errorICT220(null, error, code);
                getView().processBarcodeStatus("Errore di autenticazione.", false, false);
            }
        } catch (NullPointerException e) {
            return;
        }

    }

    /**
     * @see it.ltm.scp.module.android.managers.secure.Authenticator
     */

    @Override
    public void onAuthResult(Result result, String jsCallback) {
        try {
            getView().sendCallbackToJs(result, jsCallback);
        } catch (NullPointerException e) {
            return;
        }

    }

    @Override
    public void onAuthFailed(String errorMessage, int errorCode) {
        errorMessage = PosUtils.appendCodeToMessage(errorMessage, errorCode);
        processAuthStatus(errorMessage, true, false);
    }

    @Override
    public void onAuthMessage(final int code) {
        getView().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switch (code) {
                    case Authenticator.STATUS_START:
                        processAuthStatus("Autenticazione in corso", false, false);
                        break;
                    case Authenticator.STATUS_REQUEST_LOGIN_CREDENTIAL:
                        requestLoginCredential("", false);
                        break;
                    case Authenticator.STATUS_FINISH:
                        processAuthStatus("", false, true);
                        break;

                    default:
                        break;
                }
            }
        });

    }

    @Override
    public void onLoginFailed(String message) {
        requestLoginCredential(message, false);
    }

    /**
     * @see it.ltm.scp.module.android.ui.MainActivity
     */

    public void loadWebView(final String urlToLoad) {
        new TerminalStatusManager().checkState(new TerminalStatusManager.StateListener() {
            @Override
            public void onFinish() {
                forwardLoadWebView(urlToLoad);
            }

            @Override
            public void onReconnect(String message) {
                try {
                    getView().switchLayout(STATE_CONNECTING);
                } catch (NullPointerException e) {
                    Log.w(TAG, "processICTStatus: ", e);
                }
            }

            @Override
            public void onError(String message) {
                try {
                    getView().showErrorLayout(message);
                } catch (NullPointerException e) {
                    Log.w(TAG, "processICTStatus: ", e);
                }
            }

            @Override
            public void onPing(String message) {}
        });
    }

    private void forwardLoadWebView(String urlToLoad) {
        try {
            getView().switchLayout(STATE_LOADING);
            getView().loadUrl(urlToLoad);
        } catch (NullPointerException e) {
            Log.w(TAG, "processICTStatus: ", e);
        }
    }

    public void resolveBarcode(final String flag, final String code) {
        Log.i(TAG, "MainActivityController::resolveBarcode START: flag" + flag + " code " + code);
        new TerminalStatusManager().checkState(new TerminalStatusManager.StateListener() {
            @Override
            public void onFinish() {
                try {
                    Log.d(TAG, "resolveBarcode: flag: " + flag);
                    int barcodeResult = Integer.parseInt(flag);
                    if (barcodeResult == 0) {
                        if(AppUtils.isGevSellCode(code)) {
                            Log.d(TAG, "onFinish: Barcode GeV vendita rilevato, stop lis2.0");
                            return;
                        }
                        else mLisManager.resolveBarcode(code);
                    }
                } catch (NumberFormatException e) {
                    Log.e(TAG, "resolveBarcode: value not an int. (" + flag + ")");
                } catch (NullPointerException e) {
                    return;
                }
            }

            @Override
            public void onReconnect(String message) {
                onBarcodeMessage(message);
            }

            @Override
            public void onError(String message) {
                onBarcodeError(message);
            }

            @Override
            public void onPing(String message) {
                onBarcodeError(message);
            }
        });
    }

    public void onRetryBarcode() {
        String currentBarcode = mLisManager.getCurrentBarcode();
        if(currentBarcode != null){
//            onBarcodeMessage("Nuovo tentativo in corso");
            resolveBarcode("0", currentBarcode);
        }
    }

    public void onPageFinished(){
        if(mLisManager.isRunning()){
            Log.d(TAG, "onPageFinished: closing LisManager");
            mLisManager.stopRunning();
        }
        try {
            getView().onLisComplete();
        } catch (Exception e){}
    }

    public void pay(String jsonInput, String callbackName)
    {
        PaymentType p = APMAppManager.paymentTypeFromJson(jsonInput);
        if(APMAppManager.isAppCodePayment(p))
            mAPMAppManager.launchAppAPM(getView(), p, APMAppManager.APM_COMMAND_PAYMENT, callbackName);
        else
            mPaymentManager.pay(p, callbackName);
    }

    public void onRetryAuth() {
        new TerminalStatusManager().checkState(new TerminalStatusManager.StateListener() {
            @Override
            public void onFinish() {
                mAuthenticator.retry();
            }

            @Override
            public void onReconnect(String message) {
                processAuthStatus(message, false, false);
            }

            @Override
            public void onError(String message) {
                processAuthStatus(message, true, false);
            }

            @Override
            public void onPing(String message) {
                processAuthStatus(message, false, false);
            }
        });
    }



    public void onPrinterReady() {
        if(TerminalManagerFactory.get().autoCutPaperWhenStatusOK())
            DevicePrinter.getInstance().initPaper();
    }


    public void sendCallbackToJs(Result result, String callbackName) {
        try {
            result.clearExceptionLog();
            getView().sendCallbackToJs(result, callbackName);
        } catch (NullPointerException e) {
            return;
        }

    }

    public void reauth(final String callbackName, final PosInfo oldPosInfo) {
        new TerminalStatusManager().checkState(new TerminalStatusManager.StateListener() {
            @Override
            public void onFinish() {
                mAuthenticator.reauth(callbackName, oldPosInfo);
            }

            @Override
            public void onReconnect(String message) {
                processAuthStatus(message, false, false);
            }

            @Override
            public void onError(String message) {
                processAuthStatus(message, true, false);
            }

            @Override
            public void onPing(String message) {
                processAuthStatus(message, false, false);
            }
        });
    }

    public void reauthICT220(final String callbackName) {
        mPosInfoCallbackName = callbackName;
        processICTStatus(DevicePos.ERROR_POS_AUTH_MESSAGE, true, false);
    }

    public void errorICT220(final String callbackName, String message, int code) {
        Log.e(TAG, "errorICT220: " + message + " " + code);
        mPosInfoCallbackName = callbackName;
        if (code == Errors.ERROR_NET_IO_IPOS && getConnectionState() != ConnectionManager.State.CONNECTED) {
            message = PosUtils.appendCodeToMessage(Errors.ERROR_NET_IO_CHECK_WIRELESS, PosUtils.parsePosCode(code));
            processICTStatus(message, true, false);
        } else {
            message = PosUtils.appendCodeToMessage(message, PosUtils.parsePosCode(code));
            processICTStatus(message, true, false);
        }

    }

    private void processICTStatus(String message, boolean showReload, boolean finish) {
        try {
            getView().processICTStatus(message, showReload, finish);
        } catch (NullPointerException e) {
            Log.w(TAG, "processICTStatus: ", e);
        }
    }

    private void processAuthStatus(String message, boolean showReload, boolean finish) {
        try {
            getView().processAuthStatus(message, showReload, finish);
        } catch (NullPointerException e) {
            Log.w(TAG, "processICTStatus: ", e);
        }
    }

    private void proccessUpdateStatus(String message, boolean finish){
        try {
            getView().processUpdateStatus(message, finish);
        } catch (Exception e){}
    }


    public ConnectionManager.State getConnectionState() {
        return ConnectionManagerFactory.getConnectionManagerInstance().getState();
    }


    /**
     * JsInterface methods
     *
     * @see it.ltm.scp.module.android.js.JsMainInterface
     */

    public void disconnectApp() {
        try {
            AppUtils.clearAuthData(getView().getApplicationContext());
            DevicePos.getInstance().clearCache();
            getView().finish();
            ((ActivityManager) getView().getSystemService(ACTIVITY_SERVICE))
                    .clearApplicationUserData();
        } catch (NullPointerException e) {
            return;
        }

    }

    private void logOutAndReboot(){
        logOut();
        rebootApp(4000, "Cambio carta operatore rilevato, riavvio applicazione in corso");
    }

    public void logOutAndExit(){
        logOut();
        getView().closeApp();
    }

    public void logOut(){
        AppUtils.clearAuthData(getView().getApplicationContext());
        DevicePos.getInstance().clearCache();
        CookieManager.getInstance().removeAllCookies(null);
        CookieManager.getInstance().removeSessionCookies(null);

//                        WebStorage.getInstance().deleteAllData(); //cancella tutti i localstorage delle webview
        getView().clearWebViewLocalStorage(); //cancella localstorage della pagina
    }

    public void rebootApp(long duration, String message){
        AppUtils.restartAppWithDialog(getView(), duration, message);
    }

    public void doUpload(String jsonBody, String url, int numRetry, long retryInterval, final String callbackName) {

        //TODO vecchia implementazione, sostituita temporaneamente con multipart per retrocompatibilità
        /*UploadManager.getInstance().doUpload(jsonBody, url, numRetry, retryInterval, new UploadManager.ConfirmCallback() {
            @Override
            public void onRequestQueued() {
                sendCallbackToJs(new Result(Errors.ERROR_OK), callbackName);
            }

            @Override
            public void onError(int code, String message) {
                Result errorResult = new Result(code, message, null);
                sendCallbackToJs(errorResult, callbackName);
            }
        });*/



        Log.d(TAG, "doUpload() called with: jsonBody = [...], url = [" + url + "], numRetry = [" + numRetry + "], retryInterval = [" + retryInterval + "], callbackName = [" + callbackName + "]");

        RestData dataIn = new Gson().fromJson(jsonBody, RestData.class);
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        byte[] imgFrontContent = Base64.decode(dataIn.getImageFront().getFileContent(), Base64.NO_WRAP);
        byte[] imgBackContent = Base64.decode(dataIn.getImageBack().getFileContent(), Base64.NO_WRAP);

        String jsonConverted = gson.toJson(dataIn);
        url = url + "/multipart";
        postInitMultipartBuilder(url, numRetry, retryInterval);
        postAddTextPart("jsonMetadata", jsonConverted);
        postAddBinaryPart("frontImg"
                ,dataIn.getImageFront().getFileName()
                ,"application/octet-stream"
                ,imgFrontContent
                ,true);

        postAddBinaryPart("backImg"
                ,dataIn.getImageBack().getFileName()
                ,"application/octet-stream"
                ,imgBackContent
                ,true);

        postMultipart(callbackName);

    }

    public void postInitMultipartBuilder(String url, int numRetry, long retryInterval){
        UploadManager.getInstance().postInitMultipartBuilder(url, numRetry, retryInterval);
    }

    public void postAddTextPart(String name, String value){
        UploadManager.getInstance().postAddTextPart(name, value);
    }

    public void postSetTimeouts(int writeTimeout, int readTimeout){
        UploadManager.getInstance().postTimeoutMultipart(writeTimeout, readTimeout);
    }

    public void postAddBinaryPart(String name, String filename, String mimetype, byte[] content, boolean encrypt){
        Log.d(TAG, "postAddBinaryPart() called with: name = [" + name + "], filename = [" + filename + "], mimetype = [" + mimetype + "], content = [..], encrypt = [" + encrypt + "]");
        UploadManager.getInstance().postAddBinaryPart(name, filename, mimetype, content, encrypt);
    }

    public void postMultipart(final String callbackName){
        Log.d(TAG, "postMultipart() called with: callbackName = [" + callbackName + "]");
        UploadManager.getInstance().postMultipart(new UploadManager.ConfirmCallback() {
            @Override
            public void onRequestQueued() {
                sendCallbackToJs(new Result(Errors.ERROR_OK), callbackName);
            }

            @Override
            public void onError(int code, String message) {
                Result errorResult = new Result(code, message, null);
                sendCallbackToJs(errorResult, callbackName);
            }
        });
    }

    public String getUploadStatus() {
        return UploadManager.getInstance().getStatusResult().toJsonString();
    }

    public void takePicture(int numShots, String[] labels, String callbackName) {
        try {
            this.mCameraJsCallbackName = callbackName;
            getView().launchCamera(numShots, labels);
        } catch (NullPointerException e) {
            Log.e(TAG, "takePicture: ", e);
        }

    }

    public void getAuthData(boolean includeToken, boolean checkTokenExpiration, String callbackName) {
        try {
            Auth authData = AppUtils.getAuthData(getView().getApplicationContext());
            if (authData == null) {
                reauth(callbackName, null);
                return;
            }
            if (checkTokenExpiration && !AppUtils.isTokenValid(authData.getTokenExpiryDate())) {
                reauth(callbackName, null);
                return;
            }
            if (!includeToken) {
                authData.setToken(null);
            } else {
                String encrTkn = SecureManager.getInstance().decryptString(authData.getToken());
                authData.setToken(encrTkn);
                String encrPhysicalTkn = SecureManager.getInstance().decryptString(authData.getPhysicalToken());
                authData.setPhysicalToken(encrPhysicalTkn);
                Log.e(TAG, "USER: " + authData.getUserCode() + "; TOKEN DECRYPT: " + authData.getToken());
            }
            Result result = new Result(Errors.ERROR_OK, authData);
            getView().sendCallbackToJs(result, callbackName);
        } catch (NullPointerException e) {
            Log.e(TAG, "getAuthData: ", e);
            Result errorResult = new Result(Errors.ERROR_RETRIEVE_AUTH_DATA,
                    Errors.getMap().get(Errors.ERROR_RETRIEVE_AUTH_DATA),
                    null);
            getView().sendCallbackToJs(errorResult, callbackName);
        }

    }

    public void getPosData(final String callbackName) {
        this.mPosInfoCallbackName = callbackName;
        new TerminalStatusManager().checkState(new TerminalStatusManager.StateListener() {
            @Override
            public void onFinish() {
                forwardPosInfo(callbackName);
            }

            @Override
            public void onReconnect(String message) {
                processICTStatus(message, false, false);
            }

            @Override
            public void onError(String message) {
                processICTStatus(message, true, false);
            }

            @Override
            public void onPing(String message) {
                processICTStatus(message, false, false);
            }
        });
    }

    public void onRetryPosInfo() {
        processICTStatus("Nuova richiesta in corso", false, false);
        getPosData(mPosInfoCallbackName);
        /*new TerminalStatusManager().checkState(new TerminalStatusManager.StateListener() {
            @Override
            public void onFinish() {
                getPosData(mPosInfoCallbackName);
                processICTStatus("Nuova richiesta in corso", false, false);
            }

            @Override
            public void onReconnect(String message) {
                processICTStatus(message, false, false);
            }

            @Override
            public void onError(String message) {
                processICTStatus(message, true, false);
            }

            @Override
            public void onPing(String message) {
                processICTStatus(message, false, false);
            }
        });*/
    }

    private void forwardPosInfo(final String callbackName) {
        Auth auth = AppUtils.getAuthData(getView().getApplicationContext());
        DevicePos.getInstance().getPosInfo(auth, new DevicePos.PosInfoCallback() {
                    @Override
                    public void onResult(Result result) {
                        try {
                            String guidaLisaVersion = getView().getPackageManager().getPackageInfo(BuildConfig.PKG_GUIDA, 0).versionName;
                            ((PosInfo)result.getData()).setGuidaLisaVersion(guidaLisaVersion);
                        } catch (Exception e){
                            Log.e(TAG, "onResult: ", e);
                        }
                        sendCallbackToJs(result, callbackName);
                        getView().onPosInfoComplete();
                    }

                    @Override
                    public void onReauth(PosInfo posInfo) {
//                        reauth(callbackName, posInfo);
                        logOutAndReboot();
                    }

                    @Override
                    public void onError(String message, int code) {
                        errorICT220(callbackName, message, code);
                    }
                }
        );
    }


    public void getCustomPrompt(String prompts, final String callback) {
        PromptRequest promptRequest = new PromptRequest(prompts);
        mPromptManager.getPromptAsync(callback, promptRequest);
    }

    public void print(String callbackName, Document document) {
        Log.d(TAG, "print: check Printer status");
        new PrinterManager().executePrint(document, callbackName, new PrinterManager.PrintCallback() {
            @Override
            public void onPrinterException(String message) {
                processPrinterException(message);
            }

            @Override
            public void onPrinterStatus(Status status) {
                processPrinterStatus(status);
            }

            @Override
            public void onResult(Result result, String callbackName) {
                sendCallbackToJs(result, callbackName);
            }
        });
    }

    private void processPrinterException(String message) {
        try {
            getView().processPrinterException(message);
        } catch (Exception e){
            Log.e(TAG, "processPrinterException: ", e);
        }
    }

    public void getTsn(int timeout, String message, String readType, final String callbackName) {
        mTsnManager.getTsnAsync(callbackName, timeout, message, readType);
    }

    private void processPrinterStatus(Status status) {
        Log.d(TAG, "processPrinterStatus() called with: status = [" + status + "]");
        try {
            getView().processPrinterEvent(status);
        } catch (Exception e){
            Log.e(TAG, "processPrinterStatus: ", e);
        }
    }

    public void showDisplay(String templateName, String html, final String callbackName) {
        DeviceDisplay.getInstance().showDisplay(templateName, html, new APICallback() {
            @Override
            public void onFinish(Result result) {
                sendCallbackToJs(result, callbackName);
            }
        });
    }

    public void showDisplayWithLines(String templateName, String[] lines, final String callbackName) {
        DeviceDisplay.getInstance().showDisplay(templateName, lines, new APICallback() {
            @Override
            public void onFinish(Result result) {
                sendCallbackToJs(result, callbackName);
            }
        });
    }

    public void getSystemInfo(final String callbackName) {
        DeviceSystem.getInstance().getSystemInfo(new APICallback() {
            @Override
            public void onFinish(Result result) {
                sendCallbackToJs(result, callbackName);
            }
        });
    }

    public void getPrinterStatus(final String callbackName) {
        DevicePrinter.getInstance().getPrinterStatus(new APICallback() {
            @Override
            public void onFinish(Result result) {
                sendCallbackToJs(result, callbackName);
            }
        });
    }

    public void launchGuidaApp() {
        try {
            Intent guidaIntent = getView().getPackageManager().getLaunchIntentForPackage(BuildConfig.PKG_GUIDA);
            guidaIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            getView().startActivity(guidaIntent);
        } catch (Exception e){
            Log.e(TAG, "launchGuidaApp: ", e);
            Toast.makeText(getView(), "Manuale LIS@ non trovato", Toast.LENGTH_LONG).show();
        }
    }

    public void launchAppFromPackage(String id) {
        try {
            Intent guidaIntent = getView().getPackageManager().getLaunchIntentForPackage(id);
            guidaIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            getView().startActivity(guidaIntent);
        } catch (Exception e){
            Log.e(TAG, "launchGuidaApp: ", e);
            Toast.makeText(getView(), "Funzione non disponibile", Toast.LENGTH_LONG).show();
        }
    }

    public void launchAppFromIdWithExtra(String id, String extra) {
        try {
            Intent externalIntent = getView().getPackageManager().getLaunchIntentForPackage(id);
            //apre activity nello stesso task
            externalIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            externalIntent.setPackage(null);

            if(extra != null){
                JsonObject root = new JsonParser().parse(extra).getAsJsonObject();
                Set<Map.Entry<String, JsonElement>> set = root.entrySet();
                for (Map.Entry<String, JsonElement> entry : set){
                    String key = entry.getKey();
                    String value = entry.getValue().getAsString();
                    Log.d(TAG, "launchAppFromIdWithExtra: + \n"
                            + "test2: \n intent values: " + key + ", " + value);
                    externalIntent.putExtra(key, value);
                }
            }
            getView().startActivityForResult(externalIntent, SANDBOX_ACTIVITY_RESULT_REQUEST_CODE);
        } catch (Exception e){
            Log.e(TAG, "launchAppFromIdWithExtra: ", e);
            Toast.makeText(getView(), "Funzione non disponibile", Toast.LENGTH_LONG).show();
        }
    }

    public void refreshPosData() {
        try {
            Auth auth = AppUtils.getAuthData(getView().getApplicationContext());
//            DevicePos.getInstance().refreshPosInfo(auth);
            DevicePos.getInstance().refreshPosInfo(auth, new DevicePos.PosInfoCallback() {
                @Override
                public void onResult(Result result) {
                    //void
                }

                @Override
                public void onReauth(PosInfo posInfo) {
                    logOutAndReboot();
                }

                @Override
                public void onError(String message, int code) {
                    //void
                }
            });
        } catch (NullPointerException e) {
            return;
        }

    }

    private boolean reloadMainPage() {
        String url = Properties.get(Constants.PROP_URL_SERVICE_MARKET_BASE)
                +  Properties.get(Constants.PROP_URL_SERVICE_MARKET_PATH_CTX);
        try {
            String currentUrl = getView().getCurrentUrl();
            if(currentUrl.startsWith(url)){
                Log.d(TAG, "reloadMainPage: Already on Main page, skip");
                return false;
            } else {
                hideKeyboard();
                String finalUrl = url + Properties.get(Constants.PROP_URL_SERVICE_MARKET_PATH_WEBVIEW);
                loadWebView(finalUrl);
                return true;
            }
        } catch (Exception e){
            Log.e(TAG, "reloadMainPage: ", e);
            return false;
        }
    }


    private void showSnackBar(String message) {
        try {
            getView().showSnackBar(message);
        } catch (NullPointerException e){}
    }

    public void showKeyboard() {
        try {
            getView().showKeyboard();
        } catch (NullPointerException e) {
            return;
        }

    }

    public void hideKeyboard() {
        try {
            getView().hideKeyboard();
        } catch (NullPointerException e) {
            return;
        }
    }

    public String getBcrPictureById(String id) {
        /*Result result;
        if(PictureSessionManager.hasPicture(id)){
            result = new Result(Errors.ERROR_OK, PictureSessionManager.getPictureFromSession(id));
        } else {
            result = new Result(Errors.ERROR_BCR_IMAGE_NOT_FOUND, Errors.getMap().get(Errors.ERROR_BCR_IMAGE_NOT_FOUND));
        }
        return  result.toJsonString();*/

        //l'oggetto Result rispetto allo standard è stato modificato con l'aggiunta del campo "idc",
        //verrà quindi creato il json manualmente
        String result;
        if(PictureSessionManager.hasPicture(id)){
            JsonObject jsonRoot = new JsonObject();
            jsonRoot.addProperty("code", Errors.ERROR_OK);
            jsonRoot.addProperty("idc", PictureSessionManager.isPictureTakenWithIdcMode(id));
            jsonRoot.addProperty("data", PictureSessionManager.getPictureFromSession(id));
            result = new Gson().toJson(jsonRoot);
        } else {
            result = new Result(Errors.ERROR_BCR_IMAGE_NOT_FOUND, Errors.getMap().get(Errors.ERROR_BCR_IMAGE_NOT_FOUND)).toJsonString();
        }
        return  result;


    }

    public void cleanPictureSession() {
        PictureSessionManager.clearSession();
    }

    public void takeBcrPicture(String imageRequestListJson, String callbackName, boolean idcPreferred, int timeout){
        try {
            ArrayList imageRequestList = new Gson().fromJson(imageRequestListJson, new TypeToken<ArrayList<ImageRequest>>(){}.getType());
            ImageRequestWrapper imageRequestWrapper = new ImageRequestWrapper(imageRequestList, callbackName);
            getView().launchBcrCamera(imageRequestWrapper, idcPreferred, timeout);
        } catch (NullPointerException e){
            Result nullPointerResult = new Result(Errors.ERROR_BCR_INPUT, Errors.getMap().get(Errors.ERROR_BCR_INPUT), e.getMessage());
            sendCallbackToJs(nullPointerResult, callbackName);
            Log.e(TAG, "takeBcrPicture: ", e);
        } catch (JsonSyntaxException e){
            Result errorParsingResult = new Result(Errors.ERROR_BCR_INPUT, "Errore parsing json", e.getMessage());
            sendCallbackToJs(errorParsingResult, callbackName);
            Log.e(TAG, "takeBcrPicture: ", e);
        }
    }


    @Override
    public void onPrinterStatus(Status status) {
        Log.d(TAG, "onPrinterStatus() called with: status = [" + status + "]");
        processPrinterStatus(status);
    }

    @Override
    public void onBarcodeEvent(String code) {
        // FIXME: 22/07/2019 centralizzare funzione sostituzione caratteri
        // Bollettini MAV, sostituzione caratteri < e > con il PIPE |
        // Qrcode avviso PA, sostituzione ; e = con PIPE |
        code = code.replaceAll("[<>;=]", "|");
            //togli gli a capo
        code = code.replaceAll("\\\\n", "");
        // pulizia carattere unicode null presente su alcuni datamatrix
        code = code.replaceAll("\\\\u0000", "");


        if(AppUtils.barcodeValid(code)){
            onBarcodeReceived(code);
        } else {
            Log.d(TAG, "resolveBarcode: BARCODE INVALID: " + code);
            onBarcodeRedirect(LisManager.BARCODE_INVALID);
        }

    }

    @Override
    public void onBarcodeStatusEvent(ScannerStatus status) {
        Log.d(TAG, "onBarcodeStatusEvent() called with: status = [" + status + "]");
        // Ignored
        if(ScannerStatus.SCANNER_ZEBRA.equalsIgnoreCase(status.getScanner())
            && ScannerStatus.STATUS_READY.equalsIgnoreCase(status.getStatus())){
                try {
                    getView().processBarcodeStatus("", false, true);
                } catch (NullPointerException e) {
                    Log.e(TAG, "onBarcodeStatusEvent: view is null");
                }

        }
    }


    @Override
    public void onBcrUpdateEvent(ScannerUpdate update) {
        Log.d(TAG, "onBcrUpdateEvent() called with: update = [" + update + "]");
        if(update.getCode().equals(ScannerUpdate.BCR_UPDATE_START_INSTALL)){
            //show popup
            try {
                getView().processBarcodeStatus(ScannerUpdate.BCR_UPDATE_MESSAGE, false, false);
            } catch (NullPointerException e){
                Log.e(TAG, "onBcrUpdateEvent: view is null");
            }
        }
    }

    @Override
    public void onAuthEvent(AuthAsyncWrapper wrapper) {
        mAuthenticator.processAuthEvent(wrapper);
    }

    @Override
    public void onPaymentEvent(PaymentAsyncWrapper wrapper) {
        mPaymentManager.processPaymentEvent(wrapper);
    }

    @Override
    public void onTsnEvent(TsnAsyncWrapper wrapper) {
        mTsnManager.processTsnEvent(wrapper);
    }

    @Override
    public void onPromptEvent(PromptResponseAsyncWrapper wrapper) {
        mPromptManager.processPromptEvent(wrapper);
    }

    @Override
    public void onUpdateEvent(UpdateStatus status) {
        switch (status.getGeneralState()){
            case UpdateStatus.STATE_DOWNLOADED :
                showSnackBar("Download aggiornamento completato");
                break;
            case UpdateStatus.STATE_DOWNLOADING :
                showSnackBar("Download aggiornamento LIS@");
                break;
            case UpdateStatus.STATE_START:
                proccessUpdateStatus("Aggiornamento di LIS@ in corso, il terminale si spegnerà a breve. " +
                        "Riavviare il terminale dopo lo spegnimento per riprendere le operazioni", false);
                break;
            default:
                DeviceSystem.getInstance().updateSystemInfo();
                try {
                    if(getView().isUpdatePending()){
                        proccessUpdateStatus(status.getMessage(), true);
                    }
                } catch (Exception e) {break;}
                break;
        }
    }

    @Override
    public void onPowerKeyPressed() {
        AppUtils.clearAuthData(App.getContext());
        try {
            AppUtils.closeAppWithDialog(getView());
        } catch (Exception e){
            Log.e(TAG, "onPowerKeyPressed: ", e);
        }
    }

    @Override
    public void onSnapshotReceived(ScannerSnapshot snapshot) {
        //void
    }

    public void stopUpload() {
        UploadManager.getInstance().killUploadService();
    }

    public void getBcrInfo(final String callbackName) {
        DeviceScanner.getInstance().getScannerInfo(new APICallbackV2<ScannerInfo>() {
            @Override
            public void onResult(ScannerInfo result) {
                sendCallbackToJs(new Result(0, result), callbackName);
            }

            @Override
            public void onError(int code, String message, Exception e) {
                sendCallbackToJs(new Result(code, message, null), callbackName);
            }
        });
    }

    public void enableReadMRZ(final String callbackName) {
        DeviceScanner.getInstance().enableReadMRZ(new APICallbackV2<Void>() {
            @Override
            public void onResult(Void result) {
                sendCallbackToJs(new Result(0, result), callbackName);
            }

            @Override
            public void onError(int code, String message, Exception e) {
                sendCallbackToJs(new Result(code, message, null), callbackName);
            }
        });
    }

    public void disableReadMRZ(final String callbackName) {
        DeviceScanner.getInstance().disableReadMRZ(new APICallbackV2<Void>() {
            @Override
            public void onResult(Void result) {
                sendCallbackToJs(new Result(0, result), callbackName);
            }

            @Override
            public void onError(int code, String message, Exception e) {
                sendCallbackToJs(new Result(code, message, null), callbackName);
            }
        });
    }

    public void readCIE(String mrz, final String callbackName) {
         new CIEReader().startRead(mrz, new CIEReaderCallback() {
             @Override
             public void onSuccess(CIE iCie) {
                 sendCallbackToJs(new Result(0, iCie), callbackName);
             }

             @Override
             public void onFailure(int iReason, String iReasonString) {
                sendCallbackToJs(new Result(iReason, iReasonString, null), callbackName);
             }
         });
    }

    private void requestLoginCredential(String message, boolean finish){
        try {
            getView().requestLoginCredential(message, finish);
        } catch (NullPointerException e){
            Log.w(TAG, "requestLoginCredential: ", e);
        }
    }

    public void onCredentialAcquired(String username, String password) {
        requestLoginCredential("", true); //chiudi UI login
        mAuthenticator.onCredentialAcquired(username, password);
    }
}
