package it.ltm.scp.module.android.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.pm.ApplicationInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.PersistableBundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.ConsoleMessage;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import it.ltm.scp.module.android.R;
import it.ltm.scp.module.android.api.APICallbackV2;
import it.ltm.scp.module.android.controllers.ScannerCameraActivityController;
import it.ltm.scp.module.android.devices.scanner.DeviceScanner;
import it.ltm.scp.module.android.managers.ConnectionManager;
import it.ltm.scp.module.android.managers.ConnectionManagerFactory;
import it.ltm.scp.module.android.managers.TerminalStatusManager;
import it.ltm.scp.module.android.model.devices.scanner.ImageRequestWrapper;
import it.ltm.scp.module.android.model.devices.scanner.ScannerInfo;
import it.ltm.scp.module.android.model.devices.scanner.VideoResponse;
import it.ltm.scp.module.android.model.devices.scanner.ZebraConfig;
import it.ltm.scp.module.android.monitor.LogWrapper;
import it.ltm.scp.module.android.utils.Errors;

public class ScannerCameraActivity extends BaseDialogActivity {

    public static final String MESSAGE_TAKE_SNAPSHOT = "Acquisizione foto in corso";

    WebView mWebView;
    ImageView mImageView;
    View mViewfinderView;
    View mPanelView;
    View mProgressLayout;
    TextView mProgressMessage;
    TextView mImageLabelView;
    ProgressBar mProgressBar;
    Button mRetryButton;
    View mScattaButton;
    View mAccettaButton;
    View mRifiutaButton;
    ImageView mBackButton;
    ViewGroup mRootLayout;

    private TerminalStatusManager mTerminalStatusManager = new TerminalStatusManager();
    private ScannerCameraActivityController mController;
    private ImageRequestWrapper mImageRequestWrapper;

    private final String TAG = this.getClass().getSimpleName();
    public final static String INTENT_DATA = "intent_image_request_data";
    public final static String INTENT_IDC_PREFERRED = "intent_idc_preferred";
    public final static String INTENT_IDC_TIMEOUT = "intent_idc_timeout";
    public static final String MESSAGE_START_PREVIEW = "Avvio lettore in modalità camera";
    public static final String MESSAGE_CLOSE_ACTIVITY = "Acquisizione documento in corso";

    private final int STATE_PROGRESS = 0;
    private final int STATE_ERROR = 1;
    private final int STATE_PREVIEW = 2;
    private final int STATE_IMG = 3;

    private boolean isFinish = false;
    private boolean useIdc = false;
    private boolean idcPreferred = false;
    private int idcTimeout = 0;

    private Bitmap mPreviewBitmap;
    private String mStreamUrl = "http://192.168.99.1:8080/?action=stream"; //default
    private String HTML_PAGE_PATH = "html/scanner_stream.html";
    private String HTML_IDC_PAGE_PATH = "file:///android_asset/html/scanner_stream_idc.html";

    private Handler mHandler = new Handler(Looper.getMainLooper());
    private SwitchModeAfterTimeout timerTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_bcr_camera);

        setupView();

        mImageRequestWrapper = (ImageRequestWrapper) getIntent().getSerializableExtra(INTENT_DATA);

        //Test only:
/*        if(mImageRequestWrapper == null){
            useIdc = true;
            idcTimeout = 7;
            ImageRequest imageRequest = new ImageRequest();
            imageRequest.setId("img1");
            imageRequest.setDescriptionLabel("Foto documento");
            ArrayList<ImageRequest> imageRequestArrayList = new ArrayList<>();
            imageRequestArrayList.add(imageRequest);
            mImageRequestWrapper = new ImageRequestWrapper(imageRequestArrayList, "test");

//            mImageLabelView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    if(useIdc = false){
//
//                    }
//                    useIdc = !useIdc;
//                    startPreview();
//                }
//            });
        }*/

        useIdc = getIntent().getBooleanExtra(INTENT_IDC_PREFERRED, false);
        idcTimeout = getIntent().getIntExtra(INTENT_IDC_TIMEOUT, 0);
        timerTask = new SwitchModeAfterTimeout();
        initWebView();
        mController = new ScannerCameraActivityController(mImageRequestWrapper.getImageRequestList(), this);
    }

    private void setupView() {
        mWebView = findViewById(R.id.surface_video);
        mImageView = findViewById(R.id.layout_video_img);
        mViewfinderView = findViewById(R.id.layout_video_viewfinder);
        mPanelView = findViewById(R.id.layout_video_panel_right);
        mProgressLayout = findViewById(R.id.video_progress_layout);
        mProgressMessage = findViewById(R.id.video_progress_text);
        mImageLabelView = findViewById(R.id.text_bcr_camera_label);
        mProgressBar = findViewById(R.id.video_progress);
        mRetryButton = findViewById(R.id.video_retry_button);
        mScattaButton = findViewById(R.id.button_video_scatta);
        mAccettaButton = findViewById(R.id.button_video_accetta);
        mRifiutaButton = findViewById(R.id.button_video_rifiuta);
        mBackButton = findViewById(R.id.button_bcr_camera_back);
        mRootLayout = findViewById(R.id.layout_video_root);
    }

    private void initWebView() {
        final boolean isDebuggable = 0 != (getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        mWebView.setWebViewClient(new StreamingWebViewClient());
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                Log.e(TAG, "onJsAlert: " + message);
                return super.onJsAlert(view, url, message, result);
            }

            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                if (isDebuggable) {
                    return super.onConsoleMessage(consoleMessage);
                } else {
                    return true;
                }
            }
        });
        if (isDebuggable) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
    }

    public void updateLabelText(String text) {
        mImageLabelView.setText(text);
    }

    public void runOnUiThreadDelay(Runnable runnable, long millis) {
        mImageLabelView.postDelayed(runnable, millis);
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause: ");
        super.onPause();
        mController.detach(getApplicationContext());
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume: ");
        super.onResume();
        mController.attach(getApplicationContext());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.d(TAG, "onSaveInstanceState: ");
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        Log.d(TAG, "onSaveInstanceState: bundle, persistableBundle");
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy: ");
        stopPreview();

        mRootLayout.removeView(mWebView);
        mWebView.removeAllViews();
        mWebView.destroy();
        super.onDestroy();
        if (mPreviewBitmap != null)
            mPreviewBitmap.recycle();
    }

    @Override
    public void onBackPressed() {
        backButton(mBackButton);
    }

    public void retry(View view) {
        if (isFinish) {
            finishActivityAndRestoreScannerMode();
        } else {
            startPreview();
        }
    }

    public void startPreview() {
        Log.d(TAG, "startPreview() called");
        switchLayout(STATE_PROGRESS);
        setMessage(MESSAGE_START_PREVIEW);

        mTerminalStatusManager.checkState(new TerminalStatusManager.StateListener() {
            @Override
            public void onFinish() {

                if (useIdc) {
                    startPreviewWithIDCMode();
                } else {
                    startPreviewWithCameraMode();
                }

            }

            @Override
            public void onReconnect(String message) {
                setMessage(message);
            }

            @Override
            public void onError(String message) {
                showError(message);
            }

            @Override
            public void onPing(String message) {
                setMessage(message);
            }
        });
    }


    public void startPreviewWithCameraMode() {
        Log.d(TAG, "startPreviewWithCameraMode() called");
        DeviceScanner.getInstance().startVideoMode(new APICallbackV2<VideoResponse>() {
            @Override
            public void onResult(VideoResponse result) {
                Log.d(TAG, "startPreviewWithCameraMode: scanner video mode set OK");
                Log.d(TAG, "startPreviewWithCameraMode: play video");
                if (result.getData() != null) {
                    mStreamUrl = result.getData().getStream();
                }
                String page = loadHtmlFromAsset().replace("URL_BCR_STREAM", mStreamUrl);
                mWebView.loadData(page, "text/html; charset=UTF-8;", null);
            }

            @Override
            public void onError(int code, String message, Exception e) {
                Log.e(TAG, "startPreviewWithCameraMode: scanner video mode set KO, " + message, e);
                showError(message + " (" + code + ")");
            }
        });


    }

    private void startPreviewWithIDCMode() {
        Log.d(TAG, "startPreviewWithIDCMode() called");
        Log.d(TAG, "startPreviewWithIDCMode() verifying connected BCR's..");
        DeviceScanner.getInstance().getScannerInfo(new APICallbackV2<ScannerInfo>() {

            @Override
            public void onResult(ScannerInfo result) {
                if (result.isZebraAttached()) {
                    Log.d(TAG, "startPreviewWithIDCMode() ZEBRA scanner found, starting idc mode");

                    DeviceScanner.getInstance().startIdcMode(new APICallbackV2<Void>() {

                        @Override
                        public void onResult(Void result) {
                            Log.d(TAG, "startPreviewWithIDCMode: startIdcMode: IDC mode");
                            mWebView.loadUrl(HTML_IDC_PAGE_PATH);
                        }

                        @Override
                        public void onError(int code, String message, Exception e) {
                            LogWrapper.e(TAG, "startPreviewWithIDCMode: startIdcMode: " + code + ", " + message, e);
                            useIdc = false;
                            startPreviewWithCameraMode();
                        }

                    }, ZebraConfig.defaultIDCConfig());


                } else {
                    LogWrapper.e(TAG, "startPreviewWithIDCMode: ZEBRA scanner not found");
                    useIdc = false;
                    startPreviewWithCameraMode();
                }
            }

            @Override
            public void onError(int code, String message, Exception e) {
                LogWrapper.e(TAG, "startPreviewWithIDCMode: getScannerInfo: " + code + ", " + message, e);
//                showError(message + " (" + code + ")"); // Se manca l'API sulle info del BCR fare fallback a modalità foto normale
                useIdc = false;
                startPreviewWithCameraMode();
            }
        });
    }


    public void stopPreview() {
        stopTimer();
        mWebView.stopLoading();
    }

    public void backButton(View view) {
        setResult(Activity.RESULT_CANCELED, getIntent());
        finishActivityAndRestoreScannerMode();
    }

    private void finishActivityAndRestoreScannerMode() {
        isFinish = true;
        switchLayout(STATE_PROGRESS);
        setMessage(MESSAGE_CLOSE_ACTIVITY);
        mBackButton.setEnabled(false);
        stopPreview();
        Log.d(TAG, "closeActivity: stopping scanner video mode");
        mTerminalStatusManager.checkState(new TerminalStatusManager.StateListener() {
            @Override
            public void onFinish() {
                if (useIdc) {
                    restoreScannerIdcMode();
                } else {
                    restoreScannerCameraMode();
                }
            }

            @Override
            public void onReconnect(String message) {
                setMessage(message);
            }

            @Override
            public void onError(String message) {
                showError(message);
            }

            @Override
            public void onPing(String message) {
                setMessage(message);
            }
        });

    }

    private void restoreScannerCameraMode() {
        DeviceScanner.getInstance().stopVideoMode(new APICallbackV2<VideoResponse>() {
            @Override
            public void onResult(VideoResponse result) {
                Log.d(TAG, "closeActivity: stopping scanner video mode OK");
                mBackButton.setEnabled(false);
                finish();
            }

            @Override
            public void onError(int code, String message, Exception e) {
                Log.e(TAG, "closeActivity: stopping scanner video mode KO: " + message, e);
                if (code == Errors.ERROR_NET_NOT_FOUND) {
                    Log.d(TAG, "onError: API not found, user can quit");
                    onResult(null);
                    return;
                }
                mBackButton.setEnabled(true);
                showError(message + " (" + code + ")");
            }
        });
    }

    private void restoreScannerIdcMode() {
        DeviceScanner.getInstance().stopIdcMode(new APICallbackV2<Void>() {
            @Override
            public void onResult(Void result) {
                Log.d(TAG, "closeActivity: stopping scanner IDC mode OK");
                mBackButton.setEnabled(false);
                finish();
            }

            @Override
            public void onError(int code, String message, Exception e) {
                LogWrapper.e(TAG, "closeActivity: stopping scanner IDC mode KO: " + message, e);
                if (code == Errors.ERROR_NET_NOT_FOUND) {
                    Log.d(TAG, "onError: API not found, user can quit");
                    onResult(null);
                    return;
                }
                mBackButton.setEnabled(true);
                showError(message + " (" + code + ")");
            }
        });
    }

    public void showError(String message) {
        if (!mImageView.isShown()) {
            switchLayout(STATE_ERROR);
            setMessage(message);
        }
    }


    private void setMessage(String message) {
        mProgressMessage.setText(message);
    }

    private void switchLayout(int STATE) {
        switch (STATE) {
            case STATE_PROGRESS: //messaggio + barra progressiva circolare
                mProgressLayout.setVisibility(View.VISIBLE);
                mProgressBar.setVisibility(View.VISIBLE);
                mRetryButton.setVisibility(View.GONE);
                mImageView.setVisibility(View.GONE);
                mPanelView.setVisibility(View.INVISIBLE);
                mWebView.setVisibility(View.INVISIBLE);
                mViewfinderView.setVisibility(View.INVISIBLE);
                break;
            case STATE_ERROR: //messaggio di errore + tasto retry
                mProgressLayout.setVisibility(View.VISIBLE);
                mProgressBar.setVisibility(View.GONE);
                mRetryButton.setVisibility(View.VISIBLE);
                mImageView.setVisibility(View.GONE);
                mPanelView.setVisibility(View.INVISIBLE);
                mViewfinderView.setVisibility(View.INVISIBLE);
                break;
            case STATE_PREVIEW: //schermata video di inquadratura fotocamera. Con IDC = schermata GIF di istruzioni
                mProgressLayout.setVisibility(View.GONE);
                mImageView.setVisibility(View.GONE);
                mPanelView.setVisibility(View.VISIBLE);
                mScattaButton.setVisibility(useIdc ? View.INVISIBLE : View.VISIBLE);
                mAccettaButton.setVisibility(View.INVISIBLE);
                mRifiutaButton.setVisibility(View.INVISIBLE);
                mWebView.setVisibility(View.VISIBLE);
                mViewfinderView.setVisibility(useIdc ? View.INVISIBLE : View.VISIBLE);
                // se modalità idc attiva, attivo timeout
                if (useIdc) startTimer();
                break;
            case STATE_IMG: // schermata di anteprima dell'immagine acquisita, possibilità di accettarla o ripetere lo scatto
                mProgressLayout.setVisibility(View.GONE);
                mImageView.setVisibility(View.VISIBLE);
                mPanelView.setVisibility(View.VISIBLE);
                mScattaButton.setVisibility(View.INVISIBLE);
                mAccettaButton.setVisibility(View.VISIBLE);
                mRifiutaButton.setVisibility(View.VISIBLE);
                mWebView.setVisibility(View.INVISIBLE);
                mViewfinderView.setVisibility(View.INVISIBLE);
                break;
            default:
                break;
        }
    }

    public void onPictureReceived(String base64image) {
        Log.d(TAG, "onPictureReceived() called ");
        stopPreview();
        byte[] byteArray = Base64.decode(base64image, Base64.DEFAULT);
        mPreviewBitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        mController.setCurrentImageBytes(base64image);
        switchLayout(STATE_IMG);
        mImageView.setImageBitmap(mPreviewBitmap);
    }

    public void acceptImage(View view) {
        mController.acceptImage(useIdc);
    }

    public void rejectImage(View view) {
        mController.discardImage();
        switchLayout(STATE_PROGRESS);
        mWebView.reload();
    }

    public void takeSnapshot(View view) {
        switchLayout(STATE_PROGRESS);
        setMessage(MESSAGE_TAKE_SNAPSHOT);
        mTerminalStatusManager.checkState(new TerminalStatusManager.StateListener() {
            @Override
            public void onFinish() {
                mController.takeSnapshot();
            }

            @Override
            public void onReconnect(String message) {
                setMessage(message);
            }

            @Override
            public void onError(String message) {
                showError(message);
            }

            @Override
            public void onPing(String message) {
                setMessage(message);
            }
        });
    }


    public void sendResultOK() {
        setResult(RESULT_OK, getIntent());
        finishActivityAndRestoreScannerMode();
    }

    private String loadHtmlFromAsset() {
        try {
            StringBuilder buf = new StringBuilder();
            InputStream is = getAssets().open(HTML_PAGE_PATH);
            BufferedReader in =
                    new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String str;

            while ((str = in.readLine()) != null) {
                buf.append(str);
            }

            in.close();
            return buf.toString();

        } catch (IOException e) {
            Log.e(TAG, "", e);
            return null;
        }
    }

    private void startTimer() {
        mHandler.postDelayed(timerTask, idcTimeout * 1000);
    }

    private void stopTimer() {
        mHandler.removeCallbacks(timerTask);
    }

    @Override
    public void onCloseApp(Dialog dialog) {
        // chiudo l'activity seguendo il giro di ripristino barcode
        dialog.dismiss();
        backButton(mBackButton);
    }

    @Override
    public void onRetryAuth() {
        //void
    }

    @Override
    public void onRetryPosInfo() {
        //void
    }

    @Override
    public void onRetryBarcode() {
        //void
    }

    @Override
    public void onPrinterReady() {
        //void
    }


    class StreamingWebViewClient extends WebViewClient {
        private boolean hasError;
        //        private boolean retry_from_res_err = false; //TODO includere in prossima release
        private Runnable timeoutTask = new Runnable() {
            @Override
            public void run() {
                hasError = true;
                mWebView.stopLoading();
                String error = getFormattedErrorMessage(Errors.getMap().get(Errors.ERROR_BCR_STREAMING),
                        "APP_TIMEOUT",
                        String.valueOf(Errors.ERROR_BCR_STREAMING));
                showError(error);
                Log.d(TAG, "run: webview timeoutTask, stop loading");
            }
        };
        private final int TIMEOUT = 30000;

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            Log.e("onReceivedError: ", request.getUrl().toString() + ": " + error.getDescription().toString() + " " + String.valueOf(error.getErrorCode()));
            /*if(error.getErrorCode() == WebViewClient.ERROR_TOO_MANY_REQUESTS && !retry_from_res_err){ //TODO includere in prossima release
                Log.w(TAG, "onReceivedError: too many request, retry once..");
                retry_from_res_err = true;
                view.reload();
                return;
            }*/
            hasError = true;
            if (ConnectionManagerFactory.getConnectionManagerInstance().getState() != ConnectionManager.State.CONNECTED) {
                showError(Errors.ERROR_NET_IO_CHECK_WIRELESS);
            } else {
                showError(getFormattedErrorMessage(Errors.getMap().get(Errors.ERROR_BCR_STREAMING),
                        error.getDescription().toString(),
                        String.valueOf(error.getErrorCode())));
            }
        }

        @Override
        public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
            Log.e("onReceivedHttpError: ", request.getUrl() + ": " + errorResponse.getReasonPhrase() + " " + String.valueOf(errorResponse.getStatusCode()));
            hasError = true;
            if (ConnectionManagerFactory.getConnectionManagerInstance().getState() != ConnectionManager.State.CONNECTED) {
                showError(Errors.ERROR_NET_IO_CHECK_WIRELESS);
            } else {
                showError(getFormattedErrorMessage(Errors.getMap().get(Errors.ERROR_NET_IO),
                        errorResponse.getReasonPhrase(),
                        String.valueOf(errorResponse.getStatusCode())));
            }
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            hasError = false;
            super.onPageStarted(view, url, favicon);
            mHandler.postDelayed(timeoutTask, TIMEOUT);
        }


        @Override
        public void onPageFinished(WebView view, String url) {
            mHandler.removeCallbacks(timeoutTask);
            Log.d(TAG, "onPageFinished() called");
            if (!hasError && !mImageView.isShown()) {
//                retry_from_res_err = false; //TODO includere in prossima release
                switchLayout(STATE_PREVIEW);
            }
        }

        public String getFormattedErrorMessage(String mainMessage, String log, String code) {
            String message = "";
            message += mainMessage;
            if (log != null) {
                message += "\n \n(" + log;
                if (code != null) {
                    message += ", codice: " + code;
                }
                message += ")";
            }
            return message;
        }
    }


    class SwitchModeAfterTimeout implements Runnable {

        @Override
        public void run() {
            if (useIdc) {

                switchLayout(STATE_PROGRESS);
                setMessage("Ripristino modalità di acquisizione con fotocamera");

                DeviceScanner.getInstance().stopIdcMode(new APICallbackV2<Void>() {
                    @Override
                    public void onResult(Void result) {
                        useIdc = false;
                        startPreview();
                    }

                    @Override
                    public void onError(int code, String message, Exception e) {
                        LogWrapper.e(TAG, "IDC timeout, restoring camera mode failed: " + message, e);
                        startTimer();
                    }
                });
            }
        }
    }

}