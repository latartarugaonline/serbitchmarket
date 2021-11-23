package it.ltm.scp.module.android;

import android.app.Application;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;
import android.arch.lifecycle.ProcessLifecycleOwner;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import it.ltm.scp.module.android.devices.terminal.TerminalManagerFactory;
import it.ltm.scp.module.android.managers.ConnectionManagerFactory;
import it.ltm.scp.module.android.managers.UploadManager;
import it.ltm.scp.module.android.monitor.battery.BatteryMonitorService;
import it.ltm.scp.module.android.utils.Properties;
import it.ltm.scp.module.android.websocket.WebSocketService;

/**
 * Created by HW64 on 08/03/2017.
 */

public class App extends Application implements LifecycleObserver {

    private static Context mContext;
    private final String TAG = App.class.getSimpleName();
    private Intent webSocketIntent;

    @Override
    public void onCreate() {
        super.onCreate();
        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
        mContext = this;
        Properties.init(this);
        ConnectionManagerFactory.getConnectionManagerInstance().init(this);
        UploadManager.getInstance().init();
        //init websocket service
        webSocketIntent = new Intent(this, WebSocketService.class);
        if(TerminalManagerFactory.get().getBatteryMonitor().monitorEnabled()){
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                this.startForegroundService(new Intent(this, BatteryMonitorService.class));
            } else {
                this.startService(new Intent(this, BatteryMonitorService.class));
            }
        }
    }

    public static Context getContext(){
        return mContext;
    }


    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onAppForegrounded() {
        Log.d(TAG, "App in foreground");
        ConnectionManagerFactory.getConnectionManagerInstance().resume(this);
        startService(webSocketIntent);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onAppBackgrounded() {
        Log.d(TAG, "App in background");
        ConnectionManagerFactory.getConnectionManagerInstance().pause(this);
        stopService(webSocketIntent);
    }
}
