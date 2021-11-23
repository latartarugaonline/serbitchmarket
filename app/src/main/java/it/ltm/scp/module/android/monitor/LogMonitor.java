package it.ltm.scp.module.android.monitor;

import android.util.Log;

import com.google.gson.Gson;

import java.util.concurrent.TimeUnit;

import androidx.work.BackoffPolicy;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import it.ltm.scp.module.android.App;
import it.ltm.scp.module.android.devices.pos.DevicePos;
import it.ltm.scp.module.android.devices.terminal.TerminalManagerFactory;
import it.ltm.scp.module.android.model.devices.pos.gson.Auth;
import it.ltm.scp.module.android.model.devices.pos.gson.PosInfo;
import it.ltm.scp.module.android.monitor.model.Message;
import it.ltm.scp.module.android.utils.AppUtils;

public class LogMonitor {

    private static final String TAG = "LogMonitor";

    public static void scheduleLogMonitor(String tag, String msg, Throwable tr, String level) {
        scheduleLogMonitor(tag, msg, tr, null, level, null);
    }

    public static void scheduleLogMonitor(String tag, String msg, Throwable tr, String level, String extra) {
        scheduleLogMonitor(tag, msg, tr, null, level, extra);
    }

    public static void scheduleLogMonitor(String tag, String msg, Throwable tr, String path, String level, String extra){
        Log.d(TAG, "scheduleLogMonitor() called with: tag = [" + tag + "], msg = [" + msg + "], tr = [" + tr + "]");

        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        Message message = new Message();
        message.setEx(Log.getStackTraceString(tr));
        message.setMessage(msg);
        message.setTag(tag);

        PosInfo posInfo = DevicePos.getInstance().getCachedPosInfo();
        String userCode = null;
        if(posInfo == null){
            Auth auth = AppUtils.getAuthData(App.getContext());
            if(auth != null){
                userCode = auth.getUserCode();
            }
        } else {
            userCode = posInfo.getUserCode();
        }

        String posInfoString = new Gson().toJson(posInfo);

        if(null == extra)
            extra = TerminalManagerFactory.get().getDeviceName();

        Data inputData = new Data.Builder()
                .putString(LogMonitorWork.DATA_MESSAGE, message.toJsonString())
                .putString(LogMonitorWork.DATA_LEVEL, level)
                .putString(LogMonitorWork.DATA_PATH, path)
                .putString(LogMonitorWork.DATA_EXTRA, extra)
                .putString(LogMonitorWork.DATA_POS, posInfoString)
                .putString(LogMonitorWork.DATA_USERCODE, userCode)
                .putLong(LogMonitorWork.DATA_TIMESTAMP, System.currentTimeMillis())
                .build();

        OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(LogMonitorWork.class)
                .addTag(LogMonitorWork.TAG)
                .setConstraints(constraints)
                .setInputData(inputData)
                .setBackoffCriteria(BackoffPolicy.LINEAR, 10, TimeUnit.SECONDS)
                .build();

        WorkManager.getInstance().enqueue(workRequest);

//        WorkManager.getInstance().getWorkInfoByIdLiveData(id).observe(this, new Observer<WorkInfo>() {
//            @Override
//            public void onChanged(@Nullable WorkInfo workInfo) {
//
//            }
//        });
    }

    public static void stopAllWorks(){
        WorkManager.getInstance().cancelAllWorkByTag(LogMonitorWork.TAG);
    }


}
