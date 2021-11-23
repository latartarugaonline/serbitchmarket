package it.ltm.scp.module.android.monitor;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

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
import it.ltm.scp.module.android.managers.InternalStorage;
import it.ltm.scp.module.android.model.Result;
import it.ltm.scp.module.android.model.devices.pos.gson.Auth;
import it.ltm.scp.module.android.model.devices.pos.gson.PosInfo;
import it.ltm.scp.module.android.model.devices.printer.gson.Document;
import it.ltm.scp.module.android.monitor.model.Monitor;
import it.ltm.scp.module.android.monitor.model.PrinterMessage;
import it.ltm.scp.module.android.utils.AppUtils;

public class PrintMonitor {
    private static final String TAG = "PrintMonitor";
    private static final String FILENAME_PREFIX = "printError_";

    public static void schedulePrinterMonitor(String tag, String method, Result result, Document document, Throwable tr){
        Log.d(TAG, "schedulePrinterMonitor() called with: tag = [" + tag + "], method = [" + method + "], result = [" + result + "], document = [" + document + "], tr = [" + tr + "]");

        //prepare data
        Gson gson = new GsonBuilder().disableHtmlEscaping().create();
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

        if(userCode == null){
            Log.e(TAG, "schedulePrinterMonitor: usercode is NULL, abort monitor");
            return;
        }
        String filename = FILENAME_PREFIX + System.currentTimeMillis();
        Log.d(TAG, "schedulePrinterMonitor: filename: " + filename);
        PrinterMessage printerMessage = new PrinterMessage(tag, Log.getStackTraceString(tr), method, result, document);

        try {
            String json = new GsonBuilder().disableHtmlEscaping().create().toJson(printerMessage);
            InternalStorage.saveObjectToFile(json, filename);
        } catch (Exception e) {
            Log.e(TAG, "onResult: ", e);
            return;
        }


        String posInfoString = gson.toJson(posInfo);
        String message = LogMonitorWork.FILE_CONTEXT + filename;
        String extra = TerminalManagerFactory.get().getDeviceName();

        //prepare work job

        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        Data inputData = new Data.Builder()
                .putString(LogMonitorWork.DATA_MESSAGE, message)
                .putString(LogMonitorWork.DATA_LEVEL, Monitor.LEVEL_ERROR)
                .putString(LogMonitorWork.DATA_PATH, null)
                .putString(LogMonitorWork.DATA_EXTRA, extra)
                .putString(LogMonitorWork.DATA_POS, posInfoString)
                .putString(LogMonitorWork.DATA_USERCODE, userCode)
                .putLong(LogMonitorWork.DATA_TIMESTAMP, System.currentTimeMillis())
                .build();

        OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(LogMonitorWork.class)
                .addTag(TAG)
                .setConstraints(constraints)
                .setInputData(inputData)
                .setBackoffCriteria(BackoffPolicy.LINEAR, 10, TimeUnit.SECONDS)
                .build();

        WorkManager.getInstance().enqueue(workRequest);
    }

    public static void schedulePrinterMonitor(String tag, String s, Result result1, Document document) {
        schedulePrinterMonitor(tag, s, result1, document, null);
    }
}
