package it.ltm.scp.module.android.managers;

import android.content.Context;

/**
 * Created by HW64 on 08/03/2017.
 * Controlla lo stato del Wi-Fi sottoscrivendosi agli eventi di Android tramite BroadcastReceiver e lo registra.
 * In caso di mancata connessione può disattivare e riattivare il Wi-Fi ed attendere fino a 2 minuti che Android si riconnetta in automatico.
 * Può registrare dei listener @ConnectionCallback a cui notificare la riuscita di connessione oppure la scadenza del timeout
 */

public interface ConnectionManager {

    interface ConnectionCallback {
        void onTimeout();
        void onConnected();
    }



    /**
     * Init WifiManager instance and State
     * @param context
     */
    void init(Context context);

    /**
     * Start receiver
     * @param context
     */
    void resume(Context context);

    void pause(Context context);

    void clearListeners();

    void register(ConnectionCallback callback);

    void unregister(ConnectionCallback callback);

    State getState();

    void forceReconnectWifi();


    enum State {
        CONNECTED, DISCONNECTED, AUTHERROR, CONNECTING
    }
}