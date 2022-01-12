package it.ltm.scp.module.android.ui;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.ConsoleMessage;
import android.webkit.CookieManager;
import android.webkit.JsResult;
import android.webkit.MimeTypeMap;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.google.gson.Gson;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

import it.ltm.scp.module.android.R;
import it.ltm.scp.module.android.api.APICallback;
import it.ltm.scp.module.android.controllers.MainActivityController;
import it.ltm.scp.module.android.devices.display.DeviceDisplay;
import it.ltm.scp.module.android.js.JsMainInterface;
import it.ltm.scp.module.android.managers.ConnectionManager;
import it.ltm.scp.module.android.managers.PictureSessionManager;
import it.ltm.scp.module.android.managers.TerminalStatusManager;
import it.ltm.scp.module.android.model.Result;
import it.ltm.scp.module.android.model.devices.pos.payment.gson.Payment;
import it.ltm.scp.module.android.model.devices.scanner.ImageRequest;
import it.ltm.scp.module.android.model.devices.scanner.ImageRequestWrapper;
import it.ltm.scp.module.android.utils.AppUtils;
import it.ltm.scp.module.android.utils.CameraUtils;
import it.ltm.scp.module.android.utils.Constants;
import it.ltm.scp.module.android.utils.CustomProgressBar;
import it.ltm.scp.module.android.utils.Errors;
import it.ltm.scp.module.android.utils.Properties;

public class MainActivity extends BaseDialogActivity {

    WebView webView;
    View errorLayout;
    TextView errorMessage;
    CustomProgressBar progressBar;
    View retryButton;
    ViewGroup rootLayout;

    private JsMainInterface jsMainInterface;
    private String urlToLoad;
    private MainActivityController mController;
    private Runnable mExecuteUiTransactionWhenStateIsReady;
    private boolean hasHttpError = false;

    private final String TAG = MainActivity.class.getSimpleName();
    private final int STATE_LOADING = 0;
    private final int STATE_FINISH_OK = 1;
    private final int STATE_FINISH_KO = 2;
    private final int STATE_CONNECTING = 3;
    private final int ACTIVITY_REQUEST_PICTURE = 20;
    private final int ACTIVITY_REQUEST_BCR_PICTURE = 21;
    private final int SANDBOX_ACTIVITY_RESULT_REQUEST_CODE = 12345;
    private final int APM_CODE = 9999;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);
        setupView();
        init();
    }

    private void setupView() {
        webView = findViewById(R.id.webView);
        errorLayout = findViewById(R.id.main_layout_error);
        errorMessage = findViewById(R.id.main_layout_error_message);
        progressBar = findViewById(R.id.main_layout_progress);
        retryButton = findViewById(R.id.main_layout_error_retry);
        rootLayout = findViewById(R.id.main_layout_root);
    }

    private void init() {
        final boolean isDebuggable = 0 != (getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE);
        mController = new MainActivityController(this);

        jsMainInterface = new JsMainInterface(mController);
        webView.setWebViewClient(new CustomWebViewClient());
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                Log.e(TAG, "onJsAlert: " + message);
                return super.onJsAlert(view, url, message, result);
            }

            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                if(isDebuggable){
                    return super.onConsoleMessage(consoleMessage);
                } else {
                    return true;
                }
            }
        });
        if (isDebuggable)
        { WebView.setWebContentsDebuggingEnabled(true); }
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.addJavascriptInterface(jsMainInterface, JsMainInterface.JavascriptLibraryName);
        webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        webView.getSettings().setAppCacheEnabled(true);
        webView.getSettings().setAppCachePath(getCacheDir().toString());
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        webView.getSettings().setAllowContentAccess(false);
        webView.getSettings().setAllowFileAccess(false);
        CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true);
        CookieManager.getInstance().setAcceptCookie(true);
        setUrlToLoad(Properties.get(Constants.PROP_URL_SERVICE_MARKET_BASE)
                + Properties.get(Constants.PROP_URL_SERVICE_MARKET_PATH_CTX)
                + Properties.get(Constants.PROP_URL_SERVICE_MARKET_PATH_WEBVIEW));

        AppUtils.disableActionMenuButtonFromWebView(webView);
        mController.loadWebView(urlToLoad); //TODO restore
        //mController.loadWebView("http://google.com/asd"); //TODO test http error


        // init display
        DeviceDisplay.getInstance().initDefaultTemplate(getApplicationContext(), new APICallback() {
            @Override
            public void onFinish(Result result) {
//                if (result.getCode() != Errors.ERROR_OK) {
//                    Snackbar snackbar = Snackbar.make(webView, "Errore inizializzazione display", Snackbar.LENGTH_LONG);
//                    snackbar.show();
//                } else {
//                    Snackbar snackbar = Snackbar.make(webView, "display inizializzato", Snackbar.LENGTH_LONG);
//                    snackbar.show();
//                }
            }
        });

        webView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    //dito poggiato sullo schermo
                    mController.startIdleTimer();
                }
                return false;
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        mController.attach(getApplicationContext());
        mController.refreshPosData();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if(mExecuteUiTransactionWhenStateIsReady != null){
            mExecuteUiTransactionWhenStateIsReady.run();
            mExecuteUiTransactionWhenStateIsReady = null;
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        Configuration overrideConf = new Configuration(newBase.getResources().getConfiguration());
        overrideConf.fontScale = 1.00f;
        applyOverrideConfiguration(overrideConf);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mController.detach(getApplicationContext());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        rootLayout.removeView(webView);
        webView.removeAllViews();
        webView.destroy();
        mController.destroy();
    }

    @Override
    public void onBackPressed() {
        if (hasHttpError) {
            hasHttpError = false;
            webView.loadUrl(urlToLoad);
        }
    }

    public void reLoadWebView(View view) {
        mController.loadWebView(urlToLoad);
    }

    public void closeApp(View view) {
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == ACTIVITY_REQUEST_PICTURE){
            Result result;
            switch (resultCode){
                case RESULT_OK:
                    try {
                        byte[] imageData = CameraUtils.getAndClearImageBytes();
                        String parsed = Base64.encodeToString(imageData, Base64.NO_WRAP);
                        result = new Result(Errors.ERROR_OK,
                                parsed);
                        mController.sendCameraResult(result);
                    } catch (NullPointerException e){
                        result = new Result(Errors.ERROR_GENERIC,
                                "Errore ottenimento immagine, riprovare l'operazione.",
                                null);
                        mController.sendCameraResult(result);
                    }
                    break;
                case CameraActivity.RESULT_OK_MULTI:
                    mController.sendMultiImageCameraResult();
                    break;
                case RESULT_CANCELED:
                    result = new Result(Errors.ERROR_GENERIC,
                            "Operazione annullata dall'operatore.", null);
                    mController.sendCameraResult(result);
                    break;
                case CameraActivity.RESULT_PERMISSION_DENIED:
                    result = new Result(Errors.ERROR_SECURITY_PERMISSION,
                            Errors.getMap().get(Errors.ERROR_SECURITY_PERMISSION), null);
                    mController.sendCameraResult(result);
                    break;
                default:
                    break;
            }
        } else if(requestCode == ACTIVITY_REQUEST_BCR_PICTURE){
            Result result;
            ImageRequestWrapper imageRequestWrapper = (ImageRequestWrapper) data.getSerializableExtra(ScannerCameraActivity.INTENT_DATA);
            switch (resultCode){
                case RESULT_OK:
                    String callback = imageRequestWrapper.getJsCallbackName();
                    List<ImageRequest> imageRequestList = imageRequestWrapper.getImageRequestList();
                    for (ImageRequest imgRqst : imageRequestList) {
                        imgRqst.setImgData(PictureSessionManager.getPictureFromSession(imgRqst.getId()));
                    }
                    result = new Result(Errors.ERROR_OK,
                            imageRequestList);
                    mController.sendCallbackToJs(result, callback);
                    break;
                case RESULT_CANCELED:
                    result = new Result(Errors.ERROR_GENERIC,
                            "Operazione annullata dall'operatore.", null);
                    if(imageRequestWrapper != null)
                        mController.sendCallbackToJs(result, imageRequestWrapper.getJsCallbackName());
                    break;
                default:
                    break;
            }
        }
        else if(requestCode == SANDBOX_ACTIVITY_RESULT_REQUEST_CODE){
            switch(resultCode){
                case RESULT_OK:
                    if(data != null) {
                        final String barcode = data.getStringExtra("barCode");
                        if(barcode != null && barcode.length() > 0) {
                            Log.i(TAG, "Extra from SANDBOX: barcode = " + barcode);
                            mExecuteUiTransactionWhenStateIsReady = new Runnable() {
                                // nel caso di barcode non valido, viene aperto subito il popup
                                // attraverso il transactionManager dei Fragment, ma in questa callback
                                // lancia IllegalStateException, spostare l'esecuzione del metodo nella callback
                                // onPostResume
                                @Override
                                public void run() {
                                    mController.onBarcodeEvent(barcode);
                                }
                            };
                        }
                    }
                    break;
                default:
                    break;
            }
        }
        else if(requestCode == APM_CODE)
        {
            if(resultCode == RESULT_OK)
            {
                Bundle bundle = data.getExtras();
                String response = bundle.getString("response");
                Log.d(TAG, "Extra from APM_APP: response = " + response);

                Payment p = new Gson().fromJson(response, Payment.class);
                Log.d(TAG, "Extra from APM_APP: Payment = " + p.toString());

                mController.getAPMAppManager().onAPMResponse(p);
                /*
                Example of APM JSON Response
                {
                    "status": "AUTHORIZED",
                    "authorizedAmount": 540,
                    "transactionTag": "12345678123456781234567812345678",
                    "ltmResponse":
                    {
                        "result": "0",
                        "description": "Operation successfully completed",
                        "data":
                        {
                            "ResponseGTX": "000",
                            "tranId": "12345678",
                            "ReceiptLinesQuantity": "10",
                            "LineSize": "24",
                            "ReceiptRows": "This is a test receipt",
                            "tranId": "12345678",
                            "ProcTranId": "KV79dDNZk3lbgntga0ntzRRg64VZhRhHXNqTmw49kgdQdtm6ocrHW4naZHP5eo5em76gA3P3b1E"
                        }
                    }
                }
                */
            }
            else {
                Log.d(TAG, "ERROR from APM_APP: resultCode = " + resultCode);
                mController.getAPMAppManager().onAPMResponseError(resultCode);
            }
        }
    }

    public void sendCallbackToJs(Result result, String callbackName) {
        sendRawStringToJs(result.toJsonString(), callbackName);
    }

    public void sendRawStringToJs(final String s, final String callbackName){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "sendCallbackToJs() called with: result = [" + s + "], callbackName = [" + callbackName + "]");
                webView.evaluateJavascript("javascript:"
                        + callbackName
                        + "('" + s + "')", null);
            }
        });
    }

    public void setUrlToLoad(String url) {
        this.urlToLoad = url;
    }

    public void loadUrl(String url) {
        webView.loadUrl(url);
    }


    public void sendBarcodeToJs(final String code) {
        Log.d(TAG, "sendBarcodeToJs() called with: code = [" + code + "]");
        webView.evaluateJavascript("javascript:"
                + "setValueFromBarcode"
                + "(\"" + code + "\")", new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String s) {
                mController.resolveBarcode(s, code);
            }
        });
    }


    public void showSnackBar(String text){
//        Snackbar.make(webView, text, Snackbar.LENGTH_LONG).show();
        AppUtils.getMessageSnackbar(webView, text).show();
    }

    public void showKeyboard() {
        View view = this.getCurrentFocus();
        if(view != null){
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            try {
                imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
            } catch (NullPointerException e){
                Log.e(TAG, "showKeyboard: ", e);
            }
        }
    }

    public void hideKeyboard() {
        View view = this.getCurrentFocus();
        if(view != null){
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            try {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            } catch (NullPointerException e){
                Log.e(TAG, "hideKeyboard: ", e);
            }
        }
    }

    public void launchCamera(int numShots, String[] labels){
        Intent cameraIntent = new Intent(MainActivity.this, CameraActivity.class);
        cameraIntent.putExtra(CameraActivity.INTENT_EXTRA_CAMERA_NUM_SHOTS, numShots);
        cameraIntent.putExtra(CameraActivity.INTENT_EXTRA_CAMERA_LABELS, labels);
        startActivityForResult(cameraIntent, ACTIVITY_REQUEST_PICTURE);
    }

    public void launchBcrCamera(ImageRequestWrapper imageRequestWrapper, boolean idcPreferred, int timeout){
        Intent cameraIntent = new Intent(MainActivity.this, ScannerCameraActivity.class);
        cameraIntent.putExtra(ScannerCameraActivity.INTENT_DATA, imageRequestWrapper);
        cameraIntent.putExtra(ScannerCameraActivity.INTENT_IDC_PREFERRED, idcPreferred);
        cameraIntent.putExtra(ScannerCameraActivity.INTENT_IDC_TIMEOUT, timeout);
        startActivityForResult(cameraIntent, ACTIVITY_REQUEST_BCR_PICTURE);
    }

    public void switchLayout(int state) {
        switch (state) {
            case STATE_LOADING:
                errorLayout.setVisibility(View.VISIBLE);
                errorMessage.setText("Caricamento Service Market");
                progressBar.setVisibility(View.VISIBLE);
                webView.setVisibility(View.GONE);
                retryButton.setVisibility(View.GONE);
                break;
            case STATE_FINISH_OK:
                errorLayout.setVisibility(View.GONE);
                retryButton.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                webView.setVisibility(View.VISIBLE);
                break;
            case STATE_FINISH_KO:
                errorLayout.setVisibility(View.VISIBLE);
                retryButton.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                webView.setVisibility(View.GONE);
                break;
            case STATE_CONNECTING:
                errorLayout.setVisibility(View.VISIBLE);
                errorMessage.setText(TerminalStatusManager.MESSAGE_RECONNECT);
                progressBar.setVisibility(View.VISIBLE);
                webView.setVisibility(View.GONE);
                retryButton.setVisibility(View.GONE);
                break;
        }
    }

    /**
     * Dialog communication
     * @see it.ltm.scp.module.android.ui.MainDialogFragment
     */

    @Override
    public void onRetryAuth() {
        Log.e(TAG, "onRetryAuth: ");
        mController.onRetryAuth();
    }

    @Override
    public void onRetryPosInfo() {
        Log.e(TAG, "onRetryPosInfo: ");
        mController.onRetryPosInfo();
    }

    @Override
    public void onRetryBarcode() {
        mController.onRetryBarcode();
    }

    @Override
    public void onPrinterReady() {
        mController.onPrinterReady();
    }

    @Override
    public void onCredentialAcquired(String username, String password) {
        super.onCredentialAcquired(username, password);
        mController.onCredentialAcquired(username, password);
    }

    public void loadWebViewDataWithBaseURL(final String url, final String html) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                webView.loadDataWithBaseURL(url, html, "text/html", "UTF-8", null);
            }
        });
    }

    public void postDataToWebView(final String url, final byte[] postData){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switchLayout(STATE_LOADING);
                errorMessage.setText("Caricamento servizio");
                webView.postUrl(url, postData);
            }
        });
    }

    public void clearWebviewCache(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                webView.clearCache(true);
            }
        });
    }

    public void clearWebViewLocalStorage(){
        webView.evaluateJavascript("localStorage.clear();", new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String s) {
                Log.d(TAG, "clearWebViewLocalStorage: " + s);
            }
        });
    }

    public void showErrorLayout(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switchLayout(STATE_FINISH_KO);
                errorMessage.setText(message);
            }
        });
    }

    public String getFormattedErrorMessage(String mainMessage, String log, String code) {
        String message = "";
        message += mainMessage;
        if(log != null){
            message += "\n \n(" +   log;
            if(code != null){
                message += ", codice: " + code;
            }
            message += ")";
        }
        return message;
    }

    public String getCurrentUrl(){
        return webView.getUrl();
    }


    class CustomWebViewClient extends WebViewClient {

        private boolean hasError = false;

        private final String SSL_ERROR_MESSAGE = "Errore certificato di sicurezza. Riprova o contatta il supporto.";
        private Runnable timeoutTask = new Runnable() {
            @Override
            public void run() {
                hasError = true;
                webView.stopLoading();
                String error = getFormattedErrorMessage(Errors.getMap().get(Errors.ERROR_NET_IO),
                        "APP_TIMEOUT",
                        String.valueOf(Errors.ERROR_NET_IO));
                showErrorLayout(appendLogData(error, webView.getUrl()));
                Log.d(TAG, "run: webview timeoutTask, stop loading");
            }
        };
        private final int TIMEOUT = 120000;
        private Handler mHandler = new Handler(Looper.getMainLooper());

        /**
         * forza l'apertura dei link all'interno della webview piuttosto che in un browser esterno.
         *
         * @param view
         * @param request
         * @return
         */
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            return false;
        }

        /**
         * intercetta le richieste web per caricare da locale le risorse "thirdParty":
         *
         * @param view
         * @param request
         * @return
         */
        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
            String url = request.getUrl().toString();
            try {
                if (url.contains("include/js/thirdParty")) {
                    Log.d(TAG, "LOCAL: " + url);
                    //load local js
                    return loadThirdPartyResourceFromAssets(request.getUrl(), "text/javascript");
                } else if (url.contains("include/css/thirdParty")) {
                    Log.d(TAG, "LOCAL: " + url);
                    return loadThirdPartyResourceFromAssets(request.getUrl(), "text/css");
                } else if (url.contains("include/fonts/thirdParty")) {
                    Log.d(TAG, "LOCAL: " + url);
                    return loadThirdPartyResourceFromAssets(request.getUrl(), null); //provare anche mimetype application/octet-stream
                } else if (url.contains("loadImageFromDevice")){
                    Log.d(TAG, "LOCAL: " + url);
                    int index = url.lastIndexOf("/");
                    String fileName = url.substring(index + 1);
                    return loadImage(fileName, "image/*");
                }
                Log.d(TAG, "REMOTE: " + url);
                return super.shouldInterceptRequest(view, request);
            } catch (Exception e) {
                Log.e(TAG, "Error loading  from assets: " +                        e.getMessage(), e);
                return super.shouldInterceptRequest(view, request);
            }

        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            Log.e("onReceivedError: ", request.getUrl().toString() + ": " + error.getDescription().toString() + " " + String.valueOf(error.getErrorCode()));
            hasError = true;
            if (mController.getConnectionState() != ConnectionManager.State.CONNECTED) {
                mController.loadWebView(urlToLoad);
            } else {
                String errorMessage = getFormattedErrorMessage(Errors.getMap().get(Errors.ERROR_NET_IO),
                        error.getDescription().toString(),
                        String.valueOf(error.getErrorCode())
                        );
                showErrorLayout(appendLogData(errorMessage, request.getUrl().toString()));
            }
        }

        @Override
        public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
            Log.e("onReceivedHttpError: ", request.getUrl() + ": " + errorResponse.getReasonPhrase() + " " + String.valueOf(errorResponse.getStatusCode()));
            //check ignore tag:
            if(request.getUrl().toString().contains(Properties.get(Constants.PROP_HTTP_IGNORE_E))){
                Log.w(TAG, "onReceivedHttpError: ERROR SKIPPED (IGNORE TAG)");
                return;
            }
            //ignore image errors:
            String ext = MimeTypeMap.getFileExtensionFromUrl(request.getUrl().toString());
            if(ext.matches("png|ico|jpg|gif|jpeg")){
                Log.w(TAG, "onReceivedHttpError: ERROR SKIPPED (IMAGE)");
                return;
            }
            hasError = true;
            hasHttpError = true;
            if (mController.getConnectionState() != ConnectionManager.State.CONNECTED) {
                showErrorLayout(Errors.ERROR_NET_IO_CHECK_WIRELESS);
            } else {
                String errorMessage = getFormattedErrorMessage(Errors.getMap().get(Errors.ERROR_NET_IO),
                        errorResponse.getReasonPhrase(),
                        String.valueOf(errorResponse.getStatusCode()));
                showErrorLayout(appendLogData(errorMessage, request.getUrl().toString()));
            }
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            Log.d(TAG, "webview page START : " + url);
            hasError = false;
            mHandler.postDelayed(timeoutTask, TIMEOUT);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);

            mHandler.removeCallbacks(timeoutTask);
            Log.d(TAG, "webview page END");
            mController.onPageFinished(); //notification per LisManager
            if (!hasError) {
                switchLayout(STATE_FINISH_OK);
            }
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            Log.e(TAG, "onReceivedSslError: " + error.toString());
            hasError = true;
            Log.d(TAG, "onReceivedSslError: Stopping timeout..");
            mHandler.removeCallbacks(timeoutTask);
            String errorMsg = getFormattedErrorMessage(SSL_ERROR_MESSAGE,
                    "SSL error",
                    String.valueOf(error.getPrimaryError()));
            showErrorLayout(errorMsg);
        }

        private String appendLogData(String originalErrorMessage, String url){
            try {
                url = AppUtils.formatUrlResource(url);
                String finalLogMessage = url + " - " + AppUtils.getCurrentDate();
                return originalErrorMessage +
                        "\n\n" +
                        "(" + finalLogMessage + ")";
            } catch (Exception e){
                Log.e(TAG, "appendLogData: ", e);
                return originalErrorMessage;
            }
        }

        private WebResourceResponse loadThirdPartyResourceFromAssets(Uri uri, String mimetype) throws Exception {
            List<String> t = uri.getPathSegments();
            String filePath = "";
            boolean isPathFile = false;
            for (int i=0; i < t.size(); i++) {
                if(isPathFile){
                    filePath += "/" + t.get(i);
                } else if(t.get(i).equals("thirdParty")){
                    isPathFile = true;
                }
            }
            filePath = filePath.replaceFirst("/", "");
            Log.w(TAG, "loadThirdPartyResourceFromAssets: filename: " + filePath);

            return loadResourceFromAssets(filePath, mimetype);
        }

        private WebResourceResponse loadResourceFromAssets(String path, String mimetype) throws Exception {
            AssetManager assetManager = getAssets();
            InputStream input = assetManager.open(path);
            return new WebResourceResponse(mimetype, "UTF-8", input);
        }

        private WebResourceResponse loadImage(String idImage, String mimetype) throws Exception {

            if(PictureSessionManager.hasPicture(idImage)){
                Log.d(TAG, "loadImage: image present, loading");
                byte[] imgArray = Base64.decode(PictureSessionManager.getPictureFromSession(idImage), Base64.DEFAULT);
                InputStream is = new ByteArrayInputStream(imgArray);
                return new WebResourceResponse(mimetype, "UTF-8", is);
            } else {
                //placeholder error image
//                imgArray = Base64.decode(Base64ImageSample.img, Base64.DEFAULT);
                Log.w(TAG, "loadImage: image not present, loading placeholder");
                return loadResourceFromAssets("img/image_not_found.jpeg", mimetype);
            }

        }
    }

}

