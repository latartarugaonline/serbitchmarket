package it.ltm.scp.module.android.monitor.battery;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import it.ltm.scp.module.android.R;
import it.ltm.scp.module.android.devices.terminal.TerminalManagerFactory;


public class BatteryMonitorService extends Service {

    private static final String TAG = "BatteryMonitorService";
    private final String NOTIFICATION_CHANNEL_ID = "ltm_battery_service";
    private final int NOTIFICATION_ID = 888;

    private BatteryMonitor mBatteryMonitor;


    private long TASK_DELAY = 10 * 60 * 1000; //10min
    HandlerThread mThread;
    Handler mHandler;

    private Runnable mTimerTask;
    private boolean destroyed = false;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: ");
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            // da Oreo 8.0 in su le notifiche devono avere un channel id specificato

            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,
                    "LIS Service Market",
                    NotificationManager.IMPORTANCE_LOW);
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(notificationChannel);

            Notification.Builder builder = new Notification.Builder(this, NOTIFICATION_CHANNEL_ID)
                    .setPriority(Notification.PRIORITY_MIN)
                    .setSmallIcon(R.mipmap.icon_puntolis)
                    .setContentTitle(getString(R.string.app_name))
                    .setContentText("Monitor");
            startForeground(NOTIFICATION_ID, builder.build());
        } else {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                    .setPriority(NotificationCompat.PRIORITY_MIN)
                    .setSmallIcon(R.mipmap.icon_puntolis)
                    .setContentTitle(getString(R.string.app_name))
                    .setContentText("Monitor");

            startForeground(NOTIFICATION_ID, builder.build());
        }
        return START_NOT_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: ");

        mThread = new HandlerThread("BatteryMonitorService");
        mThread.start();
        mHandler = new Handler(mThread.getLooper());


        mBatteryMonitor = TerminalManagerFactory.get().getBatteryMonitor();
        mBatteryMonitor.init(this);

        mTimerTask  = new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "Executing task..");
                if(destroyed){
                    Log.d(TAG, "run: Service destroyed, quit runnable");
                    return;
                }
                mBatteryMonitor.checkAndCreateLogFile(getApplicationContext());
                mBatteryMonitor.dumpBatteryStatusToFile();
                if(mBatteryMonitor.fileLogExpired()){
                    Log.d(TAG, "run: log file expired, sending log to monitoring service");
                    mBatteryMonitor.scheduleSendLogFile();
                }

                mHandler.postDelayed(this, TASK_DELAY);
            }
        };
        mHandler.postDelayed(mTimerTask, TASK_DELAY);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        destroyed = true;
        mHandler.removeCallbacks(mTimerTask);
        Log.d(TAG, "onDestroy: Service destroyed");
    }
}
