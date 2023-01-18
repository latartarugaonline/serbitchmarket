package it.ltm.scp.module.android.managers;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;

import it.ltm.scp.module.android.api.APICallback;
import it.ltm.scp.module.android.api.sm.ServiceMarketAPI;
import it.ltm.scp.module.android.devices.pos.DevicePos;
import it.ltm.scp.module.android.devices.pos.PosUtils;
import it.ltm.scp.module.android.model.Result;
import it.ltm.scp.module.android.model.devices.pos.gson.Auth;
import it.ltm.scp.module.android.model.devices.pos.gson.PosInfo;
import it.ltm.scp.module.android.model.sm.gson.AuthDTO;
import it.ltm.scp.module.android.model.sm.gson.PosInfoDTO;
import it.ltm.scp.module.android.model.sm.gson.ServiceMarketRedirectData;
import it.ltm.scp.module.android.model.sm.gson.ServiceMarketResult;
import it.ltm.scp.module.android.managers.secure.SecureManager;
import it.ltm.scp.module.android.utils.AppUtils;
import it.ltm.scp.module.android.utils.Errors;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by HW64 on 18/11/2016.
 */

public class LisManager {

    private final String BARCODE_VERIFYING = "Verifica servizio in corso.";
    private final String BARCODE_SERVICE_OPENING = "Apertura servizio in corso.";
    private final String BARCODE_NOT_RESOLVED = "Lettura codice non corretta o servizio non trovato.";
    public static final String BARCODE_INVALID = "Codice a barre non valido.";
    private final String BARCODE_RETRY = "Nuovo tentativo in corso";
    private final String OPEN_APP_SCHEME = "OPENAPP";

    public interface LisCallback {
        void onLisComplete(String url, String html);
        void onSendDataToWebView(String url, byte[] postData);
        void onSendDataToSandbox(String scheme, String domain, String appCode, String posCode, String clientCode);
        void onBarcodeReceived(String barcode);
        void onBarcodeError(String message);
        void onBarcodeMessage(String message);
        void onBarcodeRedirect(String message);
        void onBarcodeReauth(PosInfo posInfo);
        void onBarcodeErrorICT(String error, int code);
    }

    private LisCallback mCallback;

    private Context mContext;

    private String mCurrentBarcode;
    private boolean isRunning = false;


    private final String TAG = LisManager.class.getSimpleName();

    public LisManager(Context mContext, LisCallback callback) {
        this.mContext = mContext.getApplicationContext();
        this.mCallback = callback;
    }


    public void retryBarcode(){
        if(mCurrentBarcode != null){
            resolveBarcode(mCurrentBarcode);
        }
    }

    public String getCurrentBarcode(){
        return mCurrentBarcode;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void stopRunning(){
        isRunning = false;
    }

    public void resolveBarcode(String code) {
        Log.d(TAG, "resolveBarcode: SERVICE MARKET CALL - START");
        if(isRunning || AppUtils.isBarcodeEAN8or13(code)){
            return;
        }
        if(!isValid(code)){
            Log.d(TAG, "resolveBarcode: BARCODE INVALID: " + code);
            mCallback.onBarcodeRedirect(BARCODE_INVALID);
//            isRunning = false;
            return;
        }
        final Auth auth = AppUtils.getAuthData(mContext);
        if(auth == null){
            Log.d(TAG, "resolveBarcode: Auth data is null, abort lis2.0");
            return;
        }
        mCurrentBarcode = code;
        isRunning = true;
        mCallback.onBarcodeMessage(BARCODE_VERIFYING);
        new ServiceMarketAPI().resolveBarcode(code, auth.getUserCode(), new APICallback() {
            @Override
            public void onFinish(Result result) {
                Log.d(TAG, "resolveBarcode: SERVICE MARKET CALL - END");
                switch (result.getCode()){
                    case Errors.ERROR_NET_IO:
                        onHttpRequestException();
                        isRunning = false;
                        break;
                    case Errors.ERROR_OK:
                        ServiceMarketResult serviceMarketResult = (ServiceMarketResult) result.getData();
                        buildBarcodeRedirectData(serviceMarketResult, auth);
                        break;
                    default:
//                        mCallback.onLisComplete(null, null);
                        mCallback.onBarcodeRedirect(BARCODE_NOT_RESOLVED);
//                        isRunning = false;
                        break;
                }

            }
        });
    }

    private void buildBarcodeRedirectData(final ServiceMarketResult serviceMarketResult, final Auth auth) {
        Log.d(TAG, "resolveBarcode: GET POS INFO - START");
        DevicePos.getInstance().getPosInfo(auth, mContext, new DevicePos.PosInfoCallback() {
            @Override
            public void onResult(Result result) {
                Log.d(TAG, "resolveBarcode: GET POS INFO - END");
                if (result.getCode() == Errors.ERROR_OK) {
                    PosInfo posInfo = (PosInfo) result.getData();
                    ServiceMarketRedirectData serviceMarketRedirectData = new ServiceMarketRedirectData();
                    //decrypt token
                    String encrTkn = SecureManager.getInstance().decryptString(auth.getToken());
                    auth.setToken(encrTkn);
                    serviceMarketRedirectData.setAuthData(new AuthDTO(auth));
                    serviceMarketRedirectData.setPosData(new PosInfoDTO(posInfo));
                    serviceMarketRedirectData.setLisData(serviceMarketResult.getLisData());
                    serviceMarketRedirectData.setServiceData(serviceMarketResult.getServiceData());
                    String jsonInput = new Gson().toJson(serviceMarketRedirectData);
//                    buildRedirectHtml(jsonInput, serviceMarketResult.getLisData().getServiceUrl());

                    String serviceUrl = serviceMarketResult.getLisData().getServiceUrl();

                    String scheme;
                    String domain;
                    String appCode;

                    String[] params = serviceUrl.split(":",3);
                    if(params.length == 3)
                    {
                        scheme = params[0];
                        domain = params[1];
                        appCode = params[2];
                        if(scheme != null && scheme.compareTo(OPEN_APP_SCHEME) == 0)
                            mCallback.onSendDataToSandbox(scheme, domain, appCode, serviceMarketRedirectData.getLisData().getPosCode(), serviceMarketRedirectData.getLisData().getClientCode());
                        else
                            Log.e(TAG, "sendToWebView: Error on scheme, scheme = " + scheme);
                    }
                    else
                        sendToWebView(jsonInput, serviceMarketResult.getLisData().getServiceUrl());
                }
            }

            @Override
            public void onReauth(PosInfo posInfo) {
                isRunning = false;
                mCallback.onBarcodeReauth(posInfo);
            }

            @Override
            public void onError(String message, int code) {
                isRunning = false;
                mCallback.onBarcodeErrorICT(message, code);
            }
        });
    }

    private boolean isValid(String code){
        return AppUtils.barcodeValid(code);
    }

    /*
    fa caricare la pagina di LIS CLIENT direttamente alla webview, mantenendo la sessione web
     */
    private void sendToWebView(String jsonInput, final String url){
        try {
            String urlencoded = URLEncoder.encode(jsonInput, "UTF-8");
//            Log.d(TAG, "test: \n" + urlencoded);
            String content = "input=" + urlencoded;
            byte[] postData = content.getBytes(Charset.forName("UTF-8"));
            mCallback.onSendDataToWebView(url, postData);
        } catch (UnsupportedEncodingException e) {
//            isRunning = false;
            Log.e(TAG, "sendToWebView: ", e);
            mCallback.onBarcodeRedirect("Errore interno");
        }
    }

    /*
    carica pagina di LIS CLIENT (html) in asincrono e la inietta nella webview,
     si può perdere la sessione web
     */
    @Deprecated
    private void buildRedirectHtml(String jsonInput, final String url) {
        mCallback.onBarcodeMessage(BARCODE_SERVICE_OPENING);
        OkHttpClient okHttpClient = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("input", jsonInput)
                .build();
        final Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Log.d(TAG, "buildRedirectHtml: GET LIS HTML PAGE - START");
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "onFailure: ", e);
                onHttpRequestException();
                isRunning = false;
            }

            @Override
            public void onResponse(Call call, Response response) {
                Log.d(TAG, "buildRedirectHtml: GET LIS HTML PAGE - END");
                Log.d(TAG, "buildRedirectHtml: GET LIS HTML PAGE - get string response");
                //TODO controllare codice http reponse
                final String html;
                try {
                    if(response.isSuccessful()){
                        html = response.body().string();
                        Log.d(TAG, "buildRedirectHtml: GET LIS HTML PAGE - get string response - END");
                        mCallback.onLisComplete(url, html);
                    } else {
                        String error = Errors.getMap().get(Errors.ERROR_NET_IO)
                                + " (" + response.message()
                                + ", codice: "
                                + response.code()
                                + ")";
                        mCallback.onBarcodeError(error);
                        isRunning = false;
                    }
                } catch (IOException e) {
                    Log.e(TAG, "buildRedirectHtml: ", e);
                    String message = PosUtils.appendCodeToMessage(Errors.getMap().get(Errors.ERROR_NET_UNABLE_READ_ERROR_BODY),
                            Errors.ERROR_NET_UNABLE_READ_ERROR_BODY);
                    mCallback.onBarcodeError(message);
                    isRunning = false;
                } finally {
                    response.close();
                }

            }
        });

    }

    private void onHttpRequestException(){
        if(ConnectionManagerFactory.getConnectionManagerInstance().getState() == ConnectionManager.State.CONNECTED){
            mCallback.onBarcodeError(PosUtils.appendCodeToMessage(
                    Errors.getMap().get(Errors.ERROR_NET_IO),
                    Errors.ERROR_NET_IO
            ));
        } else {
            mCallback.onBarcodeError(Errors.ERROR_NET_IO_CHECK_WIRELESS);
        }
    }
}
