package it.ltm.scp.module.android;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import it.ltm.scp.module.android.managers.InternalStorage;
import it.ltm.scp.module.android.managers.UploadManager;
import it.ltm.scp.module.android.managers.secure.SecureManager;
import it.ltm.scp.module.android.model.BinaryPart;
import it.ltm.scp.module.android.model.MultipartUploadRequestInfo;
import it.ltm.scp.module.android.model.TextPart;
import it.ltm.scp.module.android.model.UploadFormDataPart;
import it.ltm.scp.module.android.model.UploadStatusResult;
import it.ltm.scp.module.android.utils.Constants;
import it.ltm.scp.module.android.utils.Errors;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UploadService extends Service {

    public static final String INTENT_FILENAME = "upload_service_fn";
    public static final String INTENT_IS_MULTIPART = "upload_service_is_multi";
    public static final String INTENT_UPDATE_STATUS = "upload_service_u_s";
    public static final String FILTER = "it.ltm.android.api.upload";

    private final String TAG = this.getClass().getSimpleName();
    private LocalBroadcastManager mLocalBroadcastManager;
    private String filename;
    private Callback requestCallback;
    private Handler mHandler;
    private HandlerThread mHandlerThread;
    private RequestRunnable mExecuteRequestRunnable;
    private int numRetry;
    private long retryInterval;
    private long RETRY_INTERVAL_DEFAULT = 5000;
    private int retryCount = 0;
    private boolean isMultipart;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: ");
        mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);
        requestCallback = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "onFailure: ", e);
                retryRequest(call.clone());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d(TAG, "onResponse() called with: call = [" + call.request().url() + "], response = [" + response.code() + " " + response.message() + "\n" + response.body().string() + "]");
                if(response.isSuccessful()){
                    sendStatus(new UploadStatusResult(UploadManager.STATUS_IDLE_COMPLETE));
                    stopSelf();
                } else if(response.code() == 401){
                    Log.e(TAG, "onResponse: autenticazione fallita");
                    sendStatus(new UploadStatusResult(Errors.ERROR_UPLOAD_AUTH,
                            Errors.getMap().get(Errors.ERROR_UPLOAD_AUTH),
                            UploadManager.STATUS_FAIL));
                    stopSelf();
                } else {
                    retryRequest(call.clone());
                }
            }
        };
        mHandlerThread = new HandlerThread("UploadThread");
        mHandlerThread.start();
        mHandler = new Handler(mHandlerThread.getLooper());
        mExecuteRequestRunnable = new RequestRunnable();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: Service started: startId: " + startId
        + "\n service : " + this.toString());
        filename = intent.getStringExtra(INTENT_FILENAME);
        isMultipart = intent.getBooleanExtra(INTENT_IS_MULTIPART, false);
        try {
            //TODO vecchia implementazione
//            String json = InternalStorage.loadFileWithEncryption(filename);
////            Log.d(TAG, "onStartCommand: json: \n" + json);
//            UploadRequestInfo uploadRequestInfo = new Gson().fromJson(json, UploadRequestInfo.class);
//            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), uploadRequestInfo.getJsonBody())

            MultipartUploadRequestInfo uploadRequestInfo = (MultipartUploadRequestInfo)InternalStorage.loadObjectFromFile(filename);

            numRetry = uploadRequestInfo.getNumRetry();
            retryInterval = uploadRequestInfo.getRetryInterval();
            if(retryInterval < RETRY_INTERVAL_DEFAULT){
                retryInterval = RETRY_INTERVAL_DEFAULT;
            }

            OkHttpClient.Builder okHttpBuilder = new OkHttpClient.Builder();
            okHttpBuilder.connectTimeout(Constants.BCKGRND_TIMEOUT_CONNECTION, TimeUnit.SECONDS);
            okHttpBuilder.readTimeout(uploadRequestInfo.getReadTimeout(), TimeUnit.SECONDS);
            okHttpBuilder.writeTimeout(uploadRequestInfo.getWriteTimeout(), TimeUnit.SECONDS);

            OkHttpClient okHttpClient = okHttpBuilder.build();

            MultipartBody.Builder multipartBuilder = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM);

            for (UploadFormDataPart part : uploadRequestInfo.getParts()){
                if(part instanceof BinaryPart){
                    BinaryPart binaryPart = (BinaryPart)part;
                    if(binaryPart.isEncrypt()){
                        binaryPart.setContent(SecureManager.getInstance().decryptFileBytes(binaryPart.getContent()));
                    }
                    multipartBuilder.addFormDataPart(binaryPart.getName()
                            , binaryPart.getFileName()
                            , RequestBody.create(MediaType.parse(binaryPart.getMediaType()), binaryPart.getContent()));
                } else if (part instanceof TextPart){
                    TextPart textPart = (TextPart)part;
                    multipartBuilder.addFormDataPart(textPart.getName(), textPart.getValue());
                }
            }

            RequestBody multipartRequestBody = multipartBuilder.build();

            final Request request = new Request.Builder()
                    .post(multipartRequestBody)
                    .url(uploadRequestInfo.getUrl())
                    .build();
            Call call = okHttpClient.newCall(request);
            executeRequest(call);
        } catch (Exception e) {
            Log.e(TAG, "onStartCommand: errore durante il recupero del file su disco", e);
            sendStatus(new UploadStatusResult(Errors.ERROR_UPLOAD_LOAD_FILE,
                    Errors.getMap().get(Errors.ERROR_UPLOAD_LOAD_FILE),
                    UploadManager.STATUS_FAIL));
            stopSelf();
        }
        return START_NOT_STICKY;
    }

    private void executeRequest(Call call) {
        mExecuteRequestRunnable.setCall(call);
        mHandler.post(mExecuteRequestRunnable);
    }

    private void retryRequest(Call call){
        if(numRetry == -1 || retryCount < numRetry){
            mExecuteRequestRunnable.setCall(call);
            mHandler.postDelayed(mExecuteRequestRunnable, retryInterval);
            retryCount++;
        } else {
            //tentativi di retry terminati, restituire errore
            sendStatus(new UploadStatusResult(Errors.ERROR_UPLOAD_TIMEOUT,
                    Errors.getMap().get(Errors.ERROR_UPLOAD_TIMEOUT),
                    UploadManager.STATUS_FAIL));
        }
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy: ");
        InternalStorage.deleteFile(filename);
        mHandlerThread.quit();
        super.onDestroy();
    }

    private void sendStatus(UploadStatusResult status) {
        Intent broadcastIntent = new Intent(FILTER);
        broadcastIntent.putExtra(INTENT_UPDATE_STATUS, status);
        mLocalBroadcastManager.sendBroadcast(broadcastIntent);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    class RequestRunnable implements Runnable {

        private Call call;

        public void setCall(Call call){
            this.call = call;
        }

        @Override
        public void run() {
            call.enqueue(requestCallback);
        }
    }
}
