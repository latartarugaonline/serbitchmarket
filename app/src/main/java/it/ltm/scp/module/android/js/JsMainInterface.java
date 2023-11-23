package it.ltm.scp.module.android.js;

import android.os.Bundle;
import android.util.Log;
import android.webkit.JavascriptInterface;

import it.ltm.scp.module.android.api.APICallbackV2;
import it.ltm.scp.module.android.controllers.MainActivityController;
import it.ltm.scp.module.android.devices.printer.DevicePrinter;
import it.ltm.scp.module.android.devices.printer.DocumentBuilderImpl;
import it.ltm.scp.module.android.devices.scanner.DeviceScanner;
import it.ltm.scp.module.android.exceptions.InvalidArgumentException;
import it.ltm.scp.module.android.managers.HttpRequestManager;
import it.ltm.scp.module.android.model.CustomHttpRequest;
import it.ltm.scp.module.android.model.Result;
import it.ltm.scp.module.android.model.devices.printer.gson.Document;
import it.ltm.scp.module.android.utils.Errors;


/**
 * Created by HW64 on 26/08/2016.
 *
 * Classe che che espone metodi tramite annotazione @JavascriptInterface
 * richiamabili da javascript caricato dentro una WebView.
 * Il campo 'JavascriptLibraryName' sarà il nome della libreria chiamabile da Javascript
 *
 * see MainActivity#onCreate(Bundle)
 * @link https://developer.android.com/reference/android/webkit/WebView.html#addJavascriptInterface%28java.lang.Object,%20java.lang.String%29
 */
public class JsMainInterface {
    
    private String TAG = JsMainInterface.class.getSimpleName();

    private DocumentBuilderImpl builder;
    public final static String JavascriptLibraryName = "localDeviceManager";
    private MainActivityController mController;

    public JsMainInterface(MainActivityController controller){
        builder = new DocumentBuilderImpl();
        mController = controller;
    }

    @JavascriptInterface
    public void app_disconnect(){
        Log.d(TAG, "app_disconnect: ");
       mController.disconnectApp();
    }

    @JavascriptInterface
    public void app_logOut(){
        Log.d(TAG, "app_logOut: ");
        mController.logOutAndExit();
    }

    @JavascriptInterface
    public void app_launchGuida(){
        Log.d(TAG, "app_launchGuida() called");
        mController.launchGuidaApp();
    }

    @JavascriptInterface
    public void app_launchAppFromId(String id){
        Log.d(TAG, "app_launchAppFromId() called with: id = [" + id + "]");
        mController.launchAppFromPackage(id);
    }

    @JavascriptInterface
    public void app_launchAppFromIdWithExtra(String id, String extra){
        Log.d(TAG, "app_launchAppFromIdWithExtra() called with: id = [" + id + "], extra = [" + extra + "]");
        mController.launchAppFromIdWithExtra(id, extra);
    }

    @JavascriptInterface
    public void auth_getData(boolean includeToken, boolean checkTokenExpiration, String callbackName){
        Log.d(TAG, "auth_getData: ");
        mController.getAuthData(includeToken, checkTokenExpiration, callbackName);
    }


    @JavascriptInterface
    public void auth_doAuth(String callbackName){
        Log.d(TAG, "auth_doAuth: ");
        mController.reauth(callbackName, null);
    }

    @JavascriptInterface
    public void bcr_cleanPictureMemory(){
        Log.d(TAG, "bcr_cleanPictureMemory() called");
        mController.cleanPictureSession();
    }

    @JavascriptInterface
    public String bcr_getPictureById(String id){
        Log.d(TAG, "bcr_getPictureById() called with: id = [" + id + "]");
        return mController.getBcrPictureById(id);
    }

    @JavascriptInterface
    public void bcr_enableReadMRZ(String callbackName){
        Log.d(TAG, "bcr_enableReadMRZ() called with: callbackName = [" + callbackName + "]");
        mController.enableReadMRZ(callbackName);
    }



    @JavascriptInterface
    public void bcr_disableReadMRZ(String callbackName){
        Log.d(TAG, "bcr_disableReadMRZ() called with: callbackName = [" + callbackName + "]");
        mController.disableReadMRZ(callbackName);
    }


    @JavascriptInterface
    public void pos_readCIE(String mrz, String callbackName){
        Log.d(TAG, "pos_readCIE() called with: mrz = [" + mrz + "], callbackName = [" + callbackName + "]");
        /*Result result = new Result(0, new CieMockData());
        mController.sendCallbackToJs(result, callbackName);
        DeviceScanner.getInstance().disableReadMRZ(new APICallbackV2<Void>() {
            @Override
            public void onResult(Void result) {
                Log.d(TAG, "onResult() called with: result = [" + result + "]");
            }

            @Override
            public void onError(int code, String message, Exception e) {
                Log.w(TAG, "onError() called with: code = [" + code + "], message = [" + message + "], e = [" + e + "]", e);
            }
        });*/

        mController.readCIE(mrz, callbackName);
    }

    @JavascriptInterface
    public void bcr_takeMultiPictures(String imageRequestListJson, String callbackName){
        Log.d(TAG, "bcr_takeMultiPictures() called with: imageRequestListJson = [" + imageRequestListJson + "], callbackName = [" + callbackName + "]");
        mController.takeBcrPicture(imageRequestListJson, callbackName, false, 0);
    }

    @Deprecated
    @JavascriptInterface
    public void bcr_takeMultiPicturesIdc(String imageRequestListJson, String callbackName, boolean idcPreferred){
        Log.d(TAG, "bcr_takeMultiPicturesIdc() called with: imageRequestListJson = [" + imageRequestListJson + "], callbackName = [" + callbackName + "], idcPreferred = [" + idcPreferred + "]");
        mController.takeBcrPicture(imageRequestListJson, callbackName, idcPreferred, 0);
    }

    @JavascriptInterface
    public void bcr_takeMultiPicturesIdcTimeout(String imageRequestListJson, String callbackName, boolean idcPreferred, int timeout){
        Log.d(TAG, "bcr_takeMultiPicturesIdcTimeout() called with: imageRequestListJson = [" + imageRequestListJson + "], callbackName = [" + callbackName + "], idcPreferred = [" + idcPreferred + "], timeout = [" + timeout + "]");
        mController.takeBcrPicture(imageRequestListJson, callbackName, idcPreferred, timeout);
    }

    @JavascriptInterface
    public void camera_takePicture(String callbackName){
        Log.d(TAG, "camera_takePicture() called with: callbackName = [" + callbackName + "]");
        mController.takePicture(1, null, callbackName);
    }

    /**
     * @param numShots number of picture to be taken
     * @param callbackName javascript method name to return result
     */
    @JavascriptInterface
    public void camera_takeMultiPictures(int numShots, String[] labels, String callbackName){
        Log.d(TAG, "camera_takeMultiPictures() called with: numShots = [" + numShots + "], labels = [" + labels + "], callbackName = [" + callbackName + "]");
        mController.takePicture(numShots, labels, callbackName);
    }

    @JavascriptInterface
    public void display_setContentHtml(String templateName, String html, String callbackName){
        Log.d(TAG, "display_setContentHtml() called with: templateName = [" + templateName + "], html = [" + html + "], callbackName = [" + callbackName + "]");
        mController.showDisplay(templateName, html, callbackName);
    }

    @JavascriptInterface
    public void display_setContentLines(String templateName, String[] lines, String callbackName){
        mController.showDisplayWithLines(templateName, lines, callbackName);
    }

    @JavascriptInterface
    public void ipos_getSystemInfo(String callbackName){
        mController.getSystemInfo(callbackName);
    }

    @JavascriptInterface
    public void keyboard_show(){
        Log.d(TAG, "keyboard_show() called");
        mController.showKeyboard();
    }

    @JavascriptInterface
    public void http_executeRequest(String url,
                                    int jsonSuccessResponseCode,
                                    String method,
                                    String formBodyData,
                                    String callbackConfirm,
                                    String callbackResult,
                                    int numRetry,
                                    long retryInterval){
        Log.d(TAG, "http_executeRequest() called with: url = [" + url + "], jsonSuccessResponseCode = [" + jsonSuccessResponseCode + "], method = [" + method + "], formBodyData = [" + formBodyData + "], callbackConfirm = [" + callbackConfirm + "], callbackResult = [" + callbackResult + "], numRetry = [" + numRetry + "], retryInterval = [" + retryInterval + "]");
        CustomHttpRequest customHttpRequest = new CustomHttpRequest(url, jsonSuccessResponseCode, method, formBodyData, callbackConfirm, callbackResult, numRetry, retryInterval);
        HttpRequestManager.getInstance().startTask(customHttpRequest);
        Result result = new Result(0);
        mController.sendCallbackToJs(result, callbackConfirm);
    }

    @JavascriptInterface
    public void http_postUpload(String jsonBody, String URL, int numRetry, long retryInterval, String callbackName){
        Log.d(TAG, "http_postUpload() called with: jsonBody = [...], URL = [" + URL + "], numRetry = [" + numRetry + "], retryInterval = [" + retryInterval + "], callbackName = [" + callbackName + "]");
        mController.doUpload(jsonBody, URL, numRetry, retryInterval, callbackName);
    }

    @JavascriptInterface
    public void http_postMultipartUploadInit(String url, int numRetry, long retryInterval){
        Log.d(TAG, "http_postMultipartUploadInit() called with: url = [" + url + "], numRetry = [" + numRetry + "], retryInterval = [" + retryInterval + "]");
        mController.postInitMultipartBuilder(url, numRetry, retryInterval);
    }

    @JavascriptInterface
    public void http_postMultipartAddTextPart(String name, String value){
        Log.d(TAG, "http_postMultipartAddTextPart() called with: name = [" + name + "], value = [" + value + "]");
        mController.postAddTextPart(name, value);
    }

    @JavascriptInterface
    public void http_postMultipartAddBinaryPart(String name, String filename, String mimetype, byte[] content, boolean encrypt){
        Log.d(TAG, "http_postMultipartAddBinaryPart() called with: name = [" + name + "], filename = [" + filename + "], mimetype = [" + mimetype + "], content = [..], encrypt = [" + encrypt + "]");
        mController.postAddBinaryPart(name, filename, mimetype, content, encrypt);
    }

    @JavascriptInterface
    public void http_postMultipartSetTimeouts(int writeTimeout, int readTimeout){
        Log.d(TAG, "postSetTimeouts() called with: writeTimeout = [" + writeTimeout + "], readTimeout = [" + readTimeout + "]");
        mController.postSetTimeouts(writeTimeout, readTimeout);
    }

    @JavascriptInterface
    public void http_postMultipart(String callbackName){
        Log.d(TAG, "http_postMultipart() called with: callbackName = [" + callbackName + "]");
        mController.postMultipart(callbackName);
    }

    @JavascriptInterface
    public String http_uploadStatus(){
        Log.d(TAG, "http_uploadStatus() called");
        String status = mController.getUploadStatus();
        Log.d(TAG, "http_uploadStatus() returning: " + status);
        return status;
    }

    @JavascriptInterface
    public void http_stopUpload(){
        Log.d(TAG, "http_stopUpload() called");
        mController.stopUpload();
    }


    @JavascriptInterface
    public void keyboard_hide(){
        Log.d(TAG, "keyboard_hide() called");
        mController.hideKeyboard();
    }

    @JavascriptInterface
    public void pos_getData(final String callbackName){
        Log.d(TAG, "pos_getData: ");
        mController.getPosData(callbackName);
    }

    @JavascriptInterface
    public void pos_getCustomPrompt(String prompt, String callback){
        Log.d(TAG, "pos_getCustomPrompt() called with: prompt = [" + prompt + "], callback = [" + callback + "]");
        mController.getCustomPrompt(prompt, callback);
    }

    @JavascriptInterface
    public void pos_refreshData(){
        Log.d(TAG, "pos_refreshData: ");
        mController.refreshPosData();
    }


    @JavascriptInterface
    public void pos_pay(String jsonData, String callbackName){
        Log.d(TAG, "pos_pay() called with: jsonData = [" + jsonData + "], callbackName = [" + callbackName + "]");
        mController.pay(jsonData, callbackName);
    }

    @JavascriptInterface
    public void pos_getTsn(int timeout, String message, String readType, String callbackName){
        Log.d(TAG, "pos_getTsn: ");
        mController.getTsn(timeout, message,readType,callbackName);
    }

    @JavascriptInterface
    public void printer_getStatus(String callbackName){
        mController.getPrinterStatus(callbackName);
    }

    @JavascriptInterface
    public void printer_setText(String text){
        Log.e(TAG, "printer_setText: " + text);
        builder.setText(text);
    }

    @JavascriptInterface
    public void printer_setTextForCopies(String text){
        builder.setTextForCopies(text);
    }

    @JavascriptInterface
    public void printer_setNewLines(int lines){
        builder.setNewLines(lines);
    }

    @JavascriptInterface
    public void printer_setEol(){
        builder.setEol();
    }

    @JavascriptInterface
    public String printer_setTextSize(int size){
        try {
            builder.setSize(size);
            return new Result(0).toJsonString();
        } catch (InvalidArgumentException e) {
            Log.e(TAG, "", e);
            return generateResultFromEx(e).toJsonString();
        }
    }

    @JavascriptInterface
    public String printer_setFeed(int value, String direction){
        try {
            builder.setFeed(value, direction);
            return new Result(0).toJsonString();
        } catch (InvalidArgumentException e) {
            Log.e(TAG, "", e);
            return generateResultFromEx(e).toJsonString();
        }
    }

    @JavascriptInterface
    public void printer_setCut(boolean partial){
        builder.setCut(partial);
    }

    @JavascriptInterface
    public String printer_setFont(String font){
        try {
            builder.setFont(font);
            return new Result(0).toJsonString();
        } catch (InvalidArgumentException e) {
            Log.e(TAG, "", e);
            return generateResultFromEx(e).toJsonString();
        }
    }

    @JavascriptInterface
    public void printer_setFlip(boolean flip){
        builder.setFlip(flip);
    }

    @JavascriptInterface
    public void printer_setNegativeMode(boolean negativeMode){
        builder.setNegative(negativeMode);
    }

    @JavascriptInterface
    public void printer_setRotateEnabled(boolean rotateEnabled){
        builder.setRotate(rotateEnabled);
    }

    @JavascriptInterface
    public String printer_setLineSpacing(int lineSpacing){
        try {
            builder.setLine(lineSpacing);
            return new Result(0).toJsonString();
        } catch (InvalidArgumentException e) {
            Log.e(TAG, "", e);
            return generateResultFromEx(e).toJsonString();
        }
    }

    @JavascriptInterface
    public String printer_setCharSpacing(int charSpacing){
        try {
            builder.setSpacing(charSpacing);
            return new Result(0).toJsonString();
        } catch (InvalidArgumentException e) {
            Log.e(TAG, "", e);
            return generateResultFromEx(e).toJsonString();
        }
    }

    @JavascriptInterface
    public String printer_setAlign(String align){
        try {
            builder.setAlign(align);
            return new Result(0).toJsonString();
        } catch (InvalidArgumentException e) {
            Log.e(TAG, "", e);
            return generateResultFromEx(e).toJsonString();
        }
    }

    @JavascriptInterface
    public void printer_setBold(boolean bold){
        builder.setBold(bold);
    }

    @JavascriptInterface
    public void printer_setRow(String inputData){
        builder.setRow(inputData);
    }

    @JavascriptInterface
    public void printer_setCustomRow(String inputData){
        builder.setCustomRow(inputData);
    }

    @JavascriptInterface
    public String printer_setUnderline(int underline){
        try {
            builder.setUnderline(underline);
            return new Result(0).toJsonString();
        } catch (InvalidArgumentException e) {
            Log.e(TAG, "", e);
            return generateResultFromEx(e).toJsonString();
        }
    }


    @JavascriptInterface
    public void printer_tab(){
        builder.tab();
    }


    @JavascriptInterface
    public String printer_setBarcode(String code, String bctype, int width, int height, String font, String position, String codeset){
        try {
            builder.setBarcode(code, bctype, width, height, font, position, codeset);
            return new Result(0).toJsonString();
        } catch (InvalidArgumentException e) {
            Log.e(TAG, "", e);
            return generateResultFromEx(e).toJsonString();
        }
    }

    @JavascriptInterface
    public String printer_setBitmap(String format, String align, int width, int height, String encoded, int zoom, int halftone, int mode){
        try {
            builder.setBitmap(format, align, width, height, encoded, zoom, halftone,mode);
            return new Result(1).toJsonString();
        } catch (InvalidArgumentException e) {
            Log.e(TAG, "", e);
            return generateResultFromEx(e).toJsonString();
        }
    }

    @JavascriptInterface
    public String printer_setImage(String format, String align, int width, int height, String encoded, int zoom){
        try {
            builder.setImage(format, align, width, height, encoded, zoom);
            return new Result(1).toJsonString();
        } catch (InvalidArgumentException e) {
            Log.e(TAG, "", e);
            return generateResultFromEx(e).toJsonString();
        }
    }

    @JavascriptInterface
    public String printer_setQrCode(String code, int model, int errors, int mode, int size){
        try {
            builder.setQrCode(code, model, errors, mode, size);
            return new Result(0).toJsonString();
        } catch (InvalidArgumentException e) {
            Log.e(TAG, "", e);
            return generateResultFromEx(e).toJsonString();
        }
    }

    @JavascriptInterface
    public String printer_setMatrix(String code){
        try {
            builder.setMatrix(code);
            return new Result(0).toJsonString();
        } catch (InvalidArgumentException e) {
            Log.e(TAG, "", e);
            return generateResultFromEx(e).toJsonString();
        }
    }

    @JavascriptInterface
    public String printer_setMaxicode(String code, int mode){
        try {
            builder.setMaxicode(code, mode);
            return new Result(0).toJsonString();
        } catch (InvalidArgumentException e) {
            Log.e(TAG, "", e);
            return generateResultFromEx(e).toJsonString();
        }
    }

    @JavascriptInterface
    public void printer_clearDocument(){
        builder.clear();
    }

    @JavascriptInterface
    public void printer_print(String callbackName, int copies){
        Log.d(TAG, "printer_print: ");
        final Document document = builder.build();
        mController.print(callbackName, document);
        if(copies > 0){
            Document copyDocument = builder.buildCopy();
            for (int i=0; i < copies; i++){
                mController.print(null, copyDocument);
            }
        }
        builder.clear();
    }

    @JavascriptInterface
    public String printer_printSync(){
        Document document = builder.build();
        builder.clear();
        return DevicePrinter.getInstance().printSync(document).toJsonString();
    }

    @JavascriptInterface
    public String token_get() {
        return "1234";
    }


    private Result generateResultFromEx(Exception e){
        return new Result(Errors.ERROR_INPUT_GENERIC,
                Errors.getMap().get(Errors.ERROR_INPUT_GENERIC),
                e.getMessage(),
                null);
    }
}
