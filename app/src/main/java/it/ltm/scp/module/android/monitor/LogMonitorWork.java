package it.ltm.scp.module.android.monitor;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import androidx.work.Worker;
import androidx.work.WorkerParameters;
import it.ltm.scp.module.android.api.sm.ServiceMarketAPI;
import it.ltm.scp.module.android.devices.pos.DevicePos;
import it.ltm.scp.module.android.managers.InternalStorage;
import it.ltm.scp.module.android.monitor.model.Monitor;
import it.ltm.scp.module.android.utils.Constants;
import retrofit2.Call;
import retrofit2.Response;

public class LogMonitorWork extends Worker {

    public static final String TAG = "LogMonitorWork";
    public final static String DATA_TIMESTAMP = "data_timestamp";
    public final static String DATA_EXTRA = "data_extra";
    public final static String DATA_PATH = "data_path";
    public final static String DATA_LEVEL = "data_level";
    public final static String DATA_MESSAGE = "data_message";
    public final static String DATA_POS = "data_pos";
    public final static String DATA_USERCODE = "data_usercode";
    private int MAX_RETRY = 5; // old is 3
    private String fileName;
    private boolean isMessageFromFile = false;

    public static final String FILE_CONTEXT = "file:";

    public LogMonitorWork(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.d(TAG, "doWork: init work");
        long timestamp = getInputData().getLong(DATA_TIMESTAMP, 0L);
        String extra = getInputData().getString(DATA_EXTRA);
        String path = getInputData().getString(DATA_PATH);
        String level = getInputData().getString(DATA_LEVEL);
        String message = getInputData().getString(DATA_MESSAGE);
        String posInfoString = getInputData().getString(DATA_POS);
        String userCode = getInputData().getString(DATA_USERCODE);

        isMessageFromFile = message.startsWith(FILE_CONTEXT);
        if(isMessageFromFile) fileName = message.replace(FILE_CONTEXT, "");

        if(userCode == null){
            Log.e(TAG, "doWork: USERCODE is null");
            if(isMessageFromFile)
                InternalStorage.deleteFile(fileName);
            return Result.failure();
        }

        if(userCode.isEmpty()
                || userCode.equals(DevicePos.USERCODE_EMPTY)){
            Log.e(TAG, "doWork: usercode not initialized: " + userCode);
            if(isMessageFromFile)
                InternalStorage.deleteFile(fileName);
            return Result.failure();
        }

        Log.d(TAG, "doWork: userCode: " + userCode);
        Log.d(TAG, "doWork: check if message is from file");

        try {
            if (isMessageFromFile) {
                Log.d(TAG, "doWork: message is from file, loading file: " + fileName);
                message = (String) InternalStorage.loadObjectFromFile(fileName);
            }
        } catch (NullPointerException e){
            Log.e(TAG, "doWork: message is null");
            Log.e(TAG, "doWork: ", e);
        } catch (Exception e) {
            Log.e(TAG, "doWork: ", e);
            return Result.failure();
        }


        String date;
        try{
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.ITALY);
            Date timestampDate = new Date(timestamp);
            date = simpleDateFormat.format(timestampDate);
        } catch (Exception e){
            Log.e(TAG, "doWork: ", e);
            date = "error";
        }

        Monitor monitor = new Monitor();
        monitor.setAppCode(Constants.APP_CODE);
        monitor.setExtra(extra);
        monitor.setContextPath(path);
        monitor.setLevel(level);
        monitor.setMessage(message);
        monitor.setPosData(posInfoString);
        monitor.setTimestamp(date);
        monitor.setUserCode(userCode);

        int currentRetryCount = getRunAttemptCount();


        Call<String> monitorCall = new ServiceMarketAPI().getSendLogCall(monitor);

        try {
            Response<String> response = monitorCall.execute();
            if(response.isSuccessful()){
                Log.d(TAG, "doWork: work complete");
                if(isMessageFromFile){
                    InternalStorage.deleteFile(fileName);
                }
                return Result.success();
            } else {
                Log.e(TAG, "doWork: " + response.code() + "; " + response.errorBody().string());
                return retryOrStop(currentRetryCount);
            }
        } catch (IOException e) {
            Log.e(TAG, "doWork: ", e);
            return retryOrStop(currentRetryCount);
        }

    }

    /**
     * Chiamato nel caso un cui il work venga stoppato dal WorkManager/OS, o perchè le constraint non sono più rispettate
     * {@see https://developer.android.com/reference/androidx/work/ListenableWorker.html#onStopped()}
     */
    @Override
    public void onStopped() {
        super.onStopped();
        InternalStorage.deleteFile(fileName);
    }

    private Result retryOrStop(int currentRetryCount){
        if(currentRetryCount < MAX_RETRY){
            Log.w(TAG, "doWork: fail, retry");
            return Result.retry();
        } else {
            Log.e(TAG, "doWork: work failed");
            if(isMessageFromFile)
                InternalStorage.deleteFile(fileName);
            return Result.failure();
        }
    }
}
