package it.ltm.scp.module.android.managers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

import static android.content.Context.CONNECTIVITY_SERVICE;
import static android.content.Context.WIFI_SERVICE;

public class ConnectionManagerDefaultImpl extends BroadcastReceiver implements ConnectionManager {
    private final static String TAG = ConnectionManager.class.getSimpleName();
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private final static int HANDLER_TIMEOUT = 180000;

    private final Runnable mTimeoutTask = new Runnable() {
        @Override
        public void run() {
            for (Iterator<ConnectionManager.ConnectionCallback> it = mListeners.iterator(); it.hasNext();) {
                it.next().onTimeout();
                it.remove();
            }
        }
    };

    private ConcurrentLinkedQueue<ConnectionManager.ConnectionCallback> mListeners = new ConcurrentLinkedQueue<>();

    private ConnectionManager.State state;
    private WifiManager wifiManager;

    /**
     * Init WifiManager instance and State
     * @param context
     */
    public synchronized void init(Context context){
        Log.d(TAG, "init: ");
//        mInstance = getInstance();
        state = ConnectionManager.State.CONNECTING;
        wifiManager = (WifiManager) context.getSystemService(WIFI_SERVICE);
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo n = cm.getActiveNetworkInfo();
        if(n != null && n.isConnectedOrConnecting()){
            Log.d(TAG, "init: state = CONNECTED");
            state = ConnectionManager.State.CONNECTED;
        }
    }

    /**
     * Start receiver
     * @param context
     */
    public synchronized void resume(Context context){
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
        context.registerReceiver(this, intentFilter);
    }

    public synchronized void pause(Context context){
        context.unregisterReceiver(this);
    }

    public void clearListeners(){
        for(Iterator<ConnectionManager.ConnectionCallback> it = mListeners.iterator(); it.hasNext();) {
            Log.w(TAG, "onReceive: unregister " + it.next().toString() );
            it.remove();
        }
    }

    public void register(ConnectionManager.ConnectionCallback callback){
        Log.w(TAG, "register: " + callback.getClass().getSimpleName() + ":" + callback.toString());
        mListeners.add(callback);
    }

    public void unregister(ConnectionManager.ConnectionCallback callback){
        Log.w(TAG, "unregister: " + callback.getClass().getSimpleName() + ":" + callback.toString());
        for(Iterator<ConnectionManager.ConnectionCallback> it = mListeners.iterator(); it.hasNext();) {
            if(it.next() == callback)
                it.remove();
        }
    }

    private boolean reconnectWifi(){
        if(state != ConnectionManager.State.CONNECTING){
            Log.e(TAG, "reconnectWifi: ");
            wifiManager.setWifiEnabled(false);
            wifiManager.setWifiEnabled(true);
            state = ConnectionManager.State.CONNECTING;
            return true;
        } else return false;
    }

    public ConnectionManager.State getState(){
        if(state == ConnectionManager.State.CONNECTING && !wifiManager.isWifiEnabled()){
            wifiManager.setWifiEnabled(true);
        }
        return state;
    }

    public void forceReconnectWifi(){
        reconnectWifi();
        mHandler.postDelayed(mTimeoutTask, HANDLER_TIMEOUT);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action  = intent.getAction();
        if(action.equals(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION)){
            SupplicantState suplSate=((SupplicantState)intent.getParcelableExtra(WifiManager.EXTRA_NEW_STATE));

            Log.e(TAG, "onReceive: STATE: " + suplSate);
            switch (suplSate){
                case COMPLETED:
                    state = ConnectionManager.State.CONNECTED;
                    stopTimeout();
                    for (Iterator<ConnectionManager.ConnectionCallback> it = mListeners.iterator(); it.hasNext();) {
                        ConnectionManager.ConnectionCallback listener = it.next();
                        Log.w(TAG, "onReceive: executing callback: " + listener.toString());
                        listener.onConnected();
                        Log.w(TAG, "onReceive: unregister " + listener.toString() );
                        it.remove();
                    }
                    break;
                case DISCONNECTED:
                    state = ConnectionManager.State.DISCONNECTED;
                    break;
                case SCANNING:
                    state = ConnectionManager.State.DISCONNECTED;  //State.CONNECTING è meno aggressivo e potrebbe passare piu tempo per il recupero connessione
                    break;
                default:
                    break;
            }

            int supl_error = intent.getIntExtra(WifiManager.EXTRA_SUPPLICANT_ERROR, -1);
            if(supl_error == WifiManager.ERROR_AUTHENTICATING){
                Log.e(TAG, "onReceive: AUTH_ERROR, reconnecting..");
                reconnectWifi();
                /*codice vecchio per problema wi-fi ipos
                if(!reconnectWifi()){
                    state = State.AUTHERROR;
                }*/
            }
        }
    }

    private void stopTimeout() {
        mHandler.removeCallbacks(mTimeoutTask);
    }
}
