package it.ltm.scp.module.android.managers;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.util.Log;

import java.io.IOException;

import it.ltm.scp.module.android.api.RestAPIModule;
import it.ltm.scp.module.android.model.devices.pos.gson.PosInfo;
import it.ltm.scp.module.android.model.devices.pos.gson.PosResult;
import it.ltm.scp.module.android.utils.Constants;
import it.ltm.scp.module.android.utils.Properties;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;

/**
 * Created by HW64 on 03/03/2017.
 * Aspetta che le API REST e la connessione internet siano attivi e inizializzati
 */

public class Ping {

    /**
     * Comunica il termine dell'operazione
     */
    public interface Callback {
        void onFinish();
    }

    private static final int KILL_TIMEOUT = 120000;
    private static final int CALL_TIMEOUT = 5000;  //10000
    private static final int START_DELAY = 1000;    //5000
    private boolean restReady = false;
    private boolean internetReady = false;
    private boolean isRunning = false;

    private Callback mCallback;

    private final String TAG = Ping.class.getSimpleName();

    /**
     * termina l'operazione dopo un timeout
     */
    private Runnable timer = new Runnable() {
        @Override
        public void run() {
            Log.d(TAG, "time out");
            clearBackgroundTasks();
            sendResult();
        }
    };

    /**
     * chiama rest api in loop fino a che non ottiene risposta dal server
     */
    private final Runnable checkRestApi = new Runnable() {
        @Override
        public void run() {
            if(!isRunning)
                return;
            Log.d(TAG, "checking REST API");
            Call<PosResult<PosInfo>> getPosInfoCall = RestAPIModule.getPosInstance().getPosInfo();
            try {
                getPosInfoCall.execute();
                restReady = true;
                Log.d(TAG, "check REST API: OK");
                quit();
            } catch (IOException e) {
                Log.e(TAG, "check REST API: " + e.getMessage());
                restReady = false;
                mBackgroundHandler.postDelayed(this, CALL_TIMEOUT);
            }
        }
    };

    /**
     * chiama service market in loop fino a che non ottiene risposta
     */
    private final Runnable checkInternet = new Runnable() {
        @Override
        public void run() {
            if(!isRunning)
                return;
            String url = Properties.get(Constants.PROP_URL_SERVICE_MARKET_BASE)
                    +  Properties.get(Constants.PROP_URL_SERVICE_MARKET_PATH_CTX)
                    + Properties.get(Constants.PROP_URL_SERVICE_MARKET_PATH_PING);
            Log.d(TAG, "checking internet @" + url);
            Request request = new Request.Builder()
                    .url(url)
                    .get()
                    .build();
            try {
                new OkHttpClient().newCall(request).execute().close();
//                OkHttpClient.Builder builder = new OkHttpClient.Builder();
//                builder = ServiceMarketAPI.configureToIgnoreCertificate(builder);
//                builder.build().newCall(request).execute().close();
                internetReady = true;
                Log.d(TAG, "check internet: OK");
                quit();
            } catch (IOException e) {
                Log.e(TAG, "check internet: " + e.getMessage());
                internetReady = false;
                mBackgroundHandler.postDelayed(this, CALL_TIMEOUT);
            }
        }
    };

    private final Runnable mStartTask = new Runnable() {
        @Override
        public void run() {
            mBackgroundHandler.post(checkRestApi);
            mBackgroundHandler.post(checkInternet);
        }
    };

    private Handler mBackgroundHandler;
    private Handler mMainHandler;
    private HandlerThread mBackgroundThread;

    public Ping() {
        mBackgroundThread = new HandlerThread(TAG);
        mBackgroundThread.start();
        mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
        mMainHandler = new Handler(Looper.getMainLooper());
    }

    public void checkConnectivity(Callback callback){
        isRunning = true;
        this.mCallback = callback;
        mMainHandler.postDelayed(timer, KILL_TIMEOUT);
        mBackgroundHandler.postDelayed(mStartTask, START_DELAY);
    }

    private boolean canQuit(){
        return restReady && internetReady;
    }

    private void quit(){
        if(canQuit()){
            Log.d(TAG, "quit");
            clearTimerTask();
            clearBackgroundTasks();
            sendResult();
        }
    }

    public void clearBackgroundTasks() {
        isRunning = false;
        mBackgroundHandler.removeCallbacks(checkInternet);
        mBackgroundHandler.removeCallbacks(checkRestApi);
    }

    public void clearTimerTask() {
        mMainHandler.removeCallbacks(timer);
    }

    public boolean isRunning(){
        return isRunning;
    }

    private void sendResult(){
        mMainHandler.post(new Runnable() {
            @Override
            public void run() {
                if(mCallback != null){
                    mCallback.onFinish();
                    mCallback = null;
                }
            }
        });
    }


}
