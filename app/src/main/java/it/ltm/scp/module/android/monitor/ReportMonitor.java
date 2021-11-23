package it.ltm.scp.module.android.monitor;

import android.util.Log;

import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import androidx.work.BackoffPolicy;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.ExistingWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import it.ltm.scp.module.android.api.APICallbackV2;
import it.ltm.scp.module.android.devices.system.DeviceSystem;
import it.ltm.scp.module.android.devices.terminal.TerminalManagerFactory;
import it.ltm.scp.module.android.managers.InternalStorage;
import it.ltm.scp.module.android.monitor.model.Monitor;
import it.ltm.scp.module.android.monitor.model.terminal.TerminalReport;

public class ReportMonitor {
    private static final String TAG = "ReportMonitor";
    private static String FILENAME = "terminal_report";

    public static void sendReportDelayed(final String usercode){
        Log.d(TAG, "sendReportDelayed() called with: usercode = [" + usercode + "]");
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Log.e(TAG, "run: ", e);
                }
                DeviceSystem.getInstance().generateTerminalReport(new APICallbackV2<TerminalReport>() {
                    @Override
                    public void onResult(TerminalReport result) {
                        scheduleReportMonitor(result, usercode);
                    }

                    @Override
                    public void onError(int code, String message, Exception e) {
                        Log.e(TAG, "onError() called with: code = [" + code + "], message = [" + message + "], e = [" + e + "]", e);
                    }
                });
            }
        });
        thread.start();

    }

    public static void scheduleReportMonitor(TerminalReport report, String usercode){

        try {
            String json = new GsonBuilder().disableHtmlEscaping().create().toJson(report);
            InternalStorage.saveObjectToFile(json, FILENAME);
        } catch (Exception e) {
            Log.e(TAG, "onResult: ", e);
            return;
        }

        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        String message = LogMonitorWork.FILE_CONTEXT + FILENAME;
        String extra = TerminalManagerFactory.get().getDeviceName();

        Data inputData = new Data.Builder()
                .putString(LogMonitorWork.DATA_MESSAGE, message)
                .putString(LogMonitorWork.DATA_LEVEL, Monitor.LEVEL_REPORT)
                .putString(LogMonitorWork.DATA_PATH, null)
                .putString(LogMonitorWork.DATA_EXTRA, extra)
                .putString(LogMonitorWork.DATA_POS, "n.d.")
                .putString(LogMonitorWork.DATA_USERCODE, usercode)
                .putLong(LogMonitorWork.DATA_TIMESTAMP, System.currentTimeMillis())
                .build();

        OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(LogMonitorWork.class)
                .addTag(TAG)
                .setConstraints(constraints)
                .setInputData(inputData)
                .setBackoffCriteria(BackoffPolicy.LINEAR, 10, TimeUnit.SECONDS)
                .build();


        WorkManager.getInstance().beginUniqueWork(TAG,
                ExistingWorkPolicy.KEEP,
                workRequest)
                .enqueue();
    }
}
