package it.ltm.scp.module.android.monitor.battery;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import it.ltm.scp.module.android.devices.terminal.TerminalManagerFactory;

public class ServiceBootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(this.getClass().getSimpleName(), "onReceive: " + intent.getAction());
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)
                && TerminalManagerFactory.get().getBatteryMonitor().monitorEnabled()) {


            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                context.startForegroundService(new Intent(context, BatteryMonitorService.class));
            } else {
                context.startService(new Intent(context, BatteryMonitorService.class));
            }


//            Intent i = new Intent(context, SampleOverlayShowActivity.class);
//            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            context.startActivity(i);
        }
    }
}
