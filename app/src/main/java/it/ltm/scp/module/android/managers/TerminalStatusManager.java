package it.ltm.scp.module.android.managers;

import android.util.Log;

import it.ltm.scp.module.android.model.Error;
import it.ltm.scp.module.android.utils.Errors;

/**
 * Created by HW64 on 08/03/2017.
 */

public class TerminalStatusManager {

    public interface StateListener {
        void onFinish();
        void onReconnect(String message);
        void onError(String message);
        void onPing(String message);
    }

    private final String TAG = TerminalStatusManager.class.getSimpleName();

    private final String PING_MESSAGE = "Verifica stato terminale in corso";
    public final static String MESSAGE_RECONNECT = Errors.getMap().get(Errors.INTERNAL_RECONNECT); //Verifica connessione wireless in corso
    private StateListener mListener;


    public void checkState(StateListener listener){
        mListener = listener;
        Log.d(TAG, "checkState: register listener: " + listener.toString());
        phase1(false);
    }

    public void checkStateAndForcePing(StateListener listener){
        mListener = listener;
        Log.d(TAG, "checkState: register listener: " + listener.toString());
        phase1(true);
    }

    public void removeListeners(){
        mListener = null;
    }

    private void phase1(boolean forcePing) {
        Log.d(TAG, "check iPOS state: ");
        ConnectionManager connectionManager = ConnectionManagerFactory.getConnectionManagerInstance();
        if(connectionManager.getState() == ConnectionManager.State.CONNECTED){
            if(forcePing)
                phase3();
            else{
                Log.d(TAG, "phase1: iPOS ready, return");
                finish();}
        } else {
            phase2();
        }
    }

    private void phase2() {
        Log.d(TAG, "phase2: reconnect");
        reconnect(MESSAGE_RECONNECT);
        ConnectionManagerFactory.getConnectionManagerInstance().register(new ConnectionManager.ConnectionCallback() {
            @Override
            public void onTimeout() {
                error(Errors.ERROR_NET_IO_CHECK_WIRELESS);
            }

            @Override
            public void onConnected() {
                phase3();
            }
        });
        ConnectionManagerFactory.getConnectionManagerInstance().forceReconnectWifi();
    }

    private void phase3() {
        Log.d(TAG, "phase3: ping");
        ping(PING_MESSAGE);
        new Ping().checkConnectivity(new Ping.Callback() {
            @Override
            public void onFinish() {
                finish();
            }
        });
    }


    private void finish() {
        if(mListener != null){
            mListener.onFinish();
            Log.d(TAG, "checkState: unregister listener: " + mListener.toString());
            removeListeners();
        }
    }

    private void error(String message) {
        if(mListener != null){
            mListener.onError(message);
            Log.d(TAG, "checkState: unregister listener: " + mListener.toString());
            removeListeners();
        }
    }

    private void reconnect(String message) {
        if(mListener != null){
            mListener.onReconnect(message);
        }
    }

    private void ping(String message) {
        if(mListener != null){
            mListener.onPing(message);
        }
    }

}
