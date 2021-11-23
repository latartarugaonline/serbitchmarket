package it.ltm.scp.module.android.controllers;

import android.content.Context;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

import it.ltm.scp.module.android.websocket.WebSocketReceiver;
import it.ltm.scp.module.android.websocket.WebSocketService;

/**
 * Created by HW64 on 21/02/2017.
 */

public abstract class WebSocketController implements BaseControllerLifecycle, WebSocketReceiver.WebSocketListener {

    private WebSocketReceiver mWebSocketReceiver;
    private IntentFilter filter;

    public WebSocketController() {
        this.filter = new IntentFilter(WebSocketService.FILTER);
        this.mWebSocketReceiver = new WebSocketReceiver(this);
    }

    @Override
    public void attach(Context context){
        LocalBroadcastManager.getInstance(context).registerReceiver(mWebSocketReceiver, filter);
    }

    @Override
    public void detach(Context context) {
        LocalBroadcastManager.getInstance(context).unregisterReceiver(mWebSocketReceiver);
    }

    @Override
    public void destroy() {

    }
}
