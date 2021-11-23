package it.ltm.scp.module.android.managers;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.gson.Gson;

import it.ltm.scp.module.android.App;
import it.ltm.scp.module.android.UploadReceiver;
import it.ltm.scp.module.android.UploadService;
import it.ltm.scp.module.android.managers.secure.SecureManager;
import it.ltm.scp.module.android.model.BinaryPart;
import it.ltm.scp.module.android.model.MultipartUploadRequestInfo;
import it.ltm.scp.module.android.model.TextPart;
import it.ltm.scp.module.android.model.UploadFormDataPart;
import it.ltm.scp.module.android.model.UploadRequestInfo;
import it.ltm.scp.module.android.model.UploadStatusResult;
import it.ltm.scp.module.android.utils.Errors;

public class UploadManager implements UploadReceiver.Listener {

    public interface ConfirmCallback {
        void onRequestQueued();
        void onError(int code, String message);
    }

    private UploadReceiver mUploadServiceReceiver;
    private IntentFilter mServiceIntentFilter;
    private Intent mServiceIntent = new Intent(App.getContext(), UploadService.class);
    private MultipartUploadRequestInfo multipartUploadRequestInfo;

    @Override
    public void onUpdateStatus(UploadStatusResult status) {
        setStatus(status);
    }


    private final static String TAG = UploadManager.class.getSimpleName();

    private static UploadManager mInstance;

    public final static int STATUS_FAIL = -1;
    public final static int STATUS_IDLE_COMPLETE = 100;
//    private static int STATUS; //-1: idle/error, 0..99: progress, 100 complete
    private final static String FILENAME_DEFAULT = "upload";
    private final static String FILENAME_MULTIPART = "upload_multipart";
    private final static String PREFERENCES_UPLOAD = "pref_u";
    private final static String PREFERENCES_UPLOAD_STATUS = "pref_u_status";
    private final static String PREFERENCES_UPLOAD_MULTIPART = "pref_u_multi";
    private final UploadStatusResult defaultStatus = new UploadStatusResult(STATUS_IDLE_COMPLETE);
    private UploadStatusResult currentStatus;

    public static synchronized UploadManager getInstance(){
        if(mInstance == null){
            mInstance = new UploadManager();
        }
        return mInstance;
    }

    public void init(){
        Log.d(TAG, "init: inizializzo upload manager");
        mServiceIntentFilter = new IntentFilter(UploadService.FILTER);
        mUploadServiceReceiver = new UploadReceiver(this);
        LocalBroadcastManager.getInstance(App.getContext()).registerReceiver(mUploadServiceReceiver, mServiceIntentFilter);

        Log.d(TAG, "init: Broadcast receiver registrato.");
        //carica status da preferences
        currentStatus = getLastStatus();
        if(isStatusRunning()){
            Log.d(TAG, "init: trovato task in sospeso, riesumo task..");
            startUploadService(FILENAME_MULTIPART);
        }
    }


    public UploadStatusResult getStatusResult(){
        Log.d(TAG, "getStatusResult() called: " + currentStatus.toJsonString());
        return currentStatus;
//        return currentStatus == null ? defaultStatus : currentStatus;
    }

    public boolean isStatusRunning(){
        return currentStatus.getStatus() > STATUS_FAIL && currentStatus.getStatus() < STATUS_IDLE_COMPLETE;
    }

    public void doUpload(String jsonBody, String URL, int numRetry, long retryInterval, ConfirmCallback listener){
        Log.d(TAG, "doUpload() called with: jsonBody = [...], URL = [" + URL + "], numRetry = [" + numRetry + "], retryInterval = [" + retryInterval + "], listener = [" + listener + "]");
        if(isStatusRunning()){
            Log.w(TAG, "doUpload: operazione già in corso. impossibile prendere in carico verso: \n"
            + URL);
            listener.onError(Errors.ERROR_UPLOAD_BUSY, Errors.getMap().get(Errors.ERROR_UPLOAD_BUSY));
            return;
        }
        //blocca richieste successive fino alla presa in carico:
        lock();
        //salva dati ingresso con cifratura su disco
        try {
            UploadRequestInfo uploadRequestInfo = new UploadRequestInfo(URL, jsonBody, numRetry, retryInterval);
            String json = new Gson().toJson(uploadRequestInfo);
            InternalStorage.saveFileWithEncryption(json, FILENAME_DEFAULT);
            setStatus(currentStatus);
            listener.onRequestQueued(); //presa in carico
            setUploadMultipart(false);
            startUploadService(FILENAME_DEFAULT);
        } catch (Exception e) {
            Log.e(TAG, "doUpload: ", e);
            //torna disponibile
            setStatus(new UploadStatusResult(Errors.ERROR_UPLOAD_SAVE_FILE,
                    Errors.getMap().get(Errors.ERROR_UPLOAD_SAVE_FILE),
                    STATUS_FAIL));
            listener.onError(10000, "Errore durante il processamento della richiesta.");
        }
    }

    private void lock() {
        //blocca richieste successive fino alla presa in carico:
        currentStatus.setStatus(0);
    }

    public void postInitMultipartBuilder(String url, int numRetry, long retryInterval){
        multipartUploadRequestInfo = new MultipartUploadRequestInfo(url, numRetry, retryInterval);
    }

    public void postAddTextPart(String name, String value){
        UploadFormDataPart textPart = new TextPart(name, value);
        multipartUploadRequestInfo.addFormDataPart(textPart);
    }

    public void postAddBinaryPart(String name, String filename, String mimetype, byte[] content, boolean encrypt){
        UploadFormDataPart binPart = new BinaryPart(name, mimetype, filename, encrypt, content);
        multipartUploadRequestInfo.addFormDataPart(binPart);
    }

    public void postTimeoutMultipart(int writeTimeout, int readTimeout){
        multipartUploadRequestInfo.setReadTimeout(readTimeout);
        multipartUploadRequestInfo.setWriteTimeout(writeTimeout);
    }

    public void postMultipart(ConfirmCallback listener){
        Log.d(TAG, "postMultipart() called with: listener = [" + listener + "]");
        if(isStatusRunning()){
            Log.w(TAG, "doUpload: operazione già in corso. impossibile prendere in carico verso: \n");
            listener.onError(Errors.ERROR_UPLOAD_BUSY, Errors.getMap().get(Errors.ERROR_UPLOAD_BUSY));
            return;
        }
        //blocca richieste successive fino alla presa in carico:
        lock();
        //salva dati ingresso con cifratura su disco
        try {
            // cifro binari
            for (UploadFormDataPart part :
                    multipartUploadRequestInfo.getParts()) {
                if(part instanceof BinaryPart){
                    if(((BinaryPart) part).isEncrypt()){
                        ((BinaryPart) part).setContent(SecureManager.getInstance().encryptFileBytes(((BinaryPart) part).getContent()));
                    }
                }
            }
            InternalStorage.saveObjectToFile(multipartUploadRequestInfo, FILENAME_MULTIPART);
            setStatus(currentStatus);
            listener.onRequestQueued(); //presa in carico
            setUploadMultipart(true);
            startUploadService(FILENAME_MULTIPART);
        } catch (Exception e) {
            Log.e(TAG, "doUpload: ", e);
            //torna disponibile
            setStatus(new UploadStatusResult(Errors.ERROR_UPLOAD_SAVE_FILE,
                    Errors.getMap().get(Errors.ERROR_UPLOAD_SAVE_FILE),
                    STATUS_FAIL));
            listener.onError(10000, "Errore durante il processamento della richiesta.");
        }

    }

    private void startUploadService(String filename){
        Log.d(TAG, "startUploadService: avvio servizio in corso");
        mServiceIntent.putExtra(UploadService.INTENT_FILENAME, filename);
        mServiceIntent.putExtra(UploadService.INTENT_IS_MULTIPART, getUploadMultipart());
        App.getContext().startService(mServiceIntent);
    }

    public void killUploadService(){
        Log.d(TAG, "killUploadService: ");
        /*if(!isStatusRunning())
            return;*/
        Log.d(TAG, "killUploadService: stopping service and restoring status to " + STATUS_IDLE_COMPLETE);
        setStatus(defaultStatus);
        App.getContext().stopService(mServiceIntent);
    }

    private UploadStatusResult getLastStatus(){
        Context context = App.getContext();
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCES_UPLOAD, Context.MODE_PRIVATE);
        String json =  sharedPreferences.getString(PREFERENCES_UPLOAD_STATUS, defaultStatus.toJsonString());
        return new Gson().fromJson(json, UploadStatusResult.class);
    }

    private void setStatus(UploadStatusResult status){
        Log.d(TAG, "setStatus() called with: status = [" + status.toJsonString() + "]");
        //update current status
        currentStatus.setStatus(status.getStatus());
        currentStatus.setCode(status.getCode());
        currentStatus.setDescription(status.getDescription());
        Context context = App.getContext();
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCES_UPLOAD, Context.MODE_PRIVATE);
        sharedPreferences.edit().putString(PREFERENCES_UPLOAD_STATUS, currentStatus.toJsonString()).commit();
    }

    private void setUploadMultipart(boolean isMultipart){
        Context context = App.getContext();
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCES_UPLOAD, Context.MODE_PRIVATE);
        sharedPreferences.edit().putBoolean(PREFERENCES_UPLOAD_MULTIPART, isMultipart).commit();
    }

    private boolean getUploadMultipart(){
        Context context = App.getContext();
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCES_UPLOAD, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(PREFERENCES_UPLOAD_MULTIPART, false);
    }
}
