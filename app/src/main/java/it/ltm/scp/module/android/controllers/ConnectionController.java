package it.ltm.scp.module.android.controllers;

import android.content.Context;

import it.ltm.scp.module.android.managers.ConnectionManagerFactory;

/**
 * Created by HW64 on 21/02/2017.
 */

public abstract class ConnectionController implements BaseControllerLifecycle {

    public void attach(Context context){
        ConnectionManagerFactory.getConnectionManagerInstance().resume(context);
    }

    public void detach(Context context){
        ConnectionManagerFactory.getConnectionManagerInstance().pause(context);
    }

    public void destroy(){
        ConnectionManagerFactory.getConnectionManagerInstance().clearListeners();
    }
}
