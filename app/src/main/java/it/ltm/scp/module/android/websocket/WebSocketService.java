package it.ltm.scp.module.android.websocket;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import it.ltm.scp.module.android.utils.Properties;

/**
 * Created by HW64 on 12/10/2016.
 */
public class WebSocketService extends Service {

    private HandlerThread mHandlerThread;
    private Handler mHandler;
    private StompClientManager stompManager;
    private LocalBroadcastManager mLocalBroadcastManager;
    public static final String FILTER = "it.ltm.android.api.ws";
    private final String TAG = "WebSocketService";
    private boolean kill = false;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate SERVICE");
        Properties.init(getApplicationContext());
        mHandlerThread = new HandlerThread("DemoApp.WebSocketThread");
        mHandlerThread.start();
        mHandler = new Handler(mHandlerThread.getLooper());
        stompManager = new StompClientManager();
        mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        kill = false;
        mHandler.post(
                new Runnable() {
                    @Override
                    public void run() {
                        stompManager.setStompClientManagerListener(new StompClientManager.StompClientManagerListener() {
                            @Override
                            public void onMessage(String message) {
                                Log.d(TAG, "onMessage: " + message);
                                Intent messageIntent = new Intent(FILTER);
                                messageIntent.putExtra("message", message);
                                mLocalBroadcastManager.sendBroadcast(messageIntent);
                            }

                            @Override
                            public void onErrorReceived(String error) {
                                Log.e(TAG, "onErrorReceived: " + error);
                            }

                            @Override
                            public void onConnected() {
                                Log.d(TAG, "onConnected");
                                    stompManager.unSubAll();
                                    stompManager.subscribeAll();

                            }

                            @Override
                            public void onStateChanged(String newState) {

                            }

                            @Override
                            public void onDisconnected() {
                                Log.d(TAG, "onDisconnected");
                                if(!kill)
                                    stompManager.connect();
                            }

                            @Override
                            public void onConnectedTimeOut(String error) {
                                stompManager.connect();
                            }

                            @Override
                            public void onConnectException(String error) {
                                if(!kill){
                                    try {
                                        Thread.sleep(2000);
                                    } catch (InterruptedException e) {
                                        Log.e(TAG, "", e);
                                    }
                                    Log.d(TAG, "reconnecting..");
                                    stompManager.connect();
                                }

                            }
                        });
                        stompManager.connect();
                    }
                }
        );
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy KILL SERVICE");
        kill = true;
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    //stompManager.unSubAll();
                    Thread.sleep(200); // tempo per fare le unsubscribe
                    stompManager.disconnect();
                    mHandlerThread.quit();
                } catch (InterruptedException e) {
                    Log.e(TAG, "", e);
                }
            }
        });
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}