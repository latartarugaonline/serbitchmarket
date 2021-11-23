package it.ltm.scp.module.android.monitor.battery;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import androidx.work.BackoffPolicy;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import it.ltm.scp.module.android.App;
import it.ltm.scp.module.android.devices.pos.DevicePos;
import it.ltm.scp.module.android.managers.InternalStorage;
import it.ltm.scp.module.android.model.devices.pos.gson.Auth;
import it.ltm.scp.module.android.model.devices.pos.gson.PosInfo;
import it.ltm.scp.module.android.monitor.LogMonitorWork;
import it.ltm.scp.module.android.utils.AppUtils;


public class BatteryMonitorLisaImpl implements BatteryMonitor {

    private String DEFAULT_DIR_NAME = "battery_log";
    private String DEFAULT_FILE_NAME = "battery_dump.txt";
    private String DATE_FILE_NAME = "date.txt";
    private static final String TAG = "BatteryMonitorLisaImpl";
    private File logFile;
    private File creationDateFile;
    private LinkedHashMap<String, String> mPath2Value;
    private int EXPIRY_HOURS = 24;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ITALY);


    @Override
    public boolean monitorEnabled() {
        return false;
    }

    @Override
    public void checkAndCreateLogFile(Context context) {
//        File root = context.getFilesDir();

        // external public storage, need permission granted
        File root = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS), DEFAULT_DIR_NAME);

        if(!root.exists())
            root.mkdirs();

        logFile = new File(root, DEFAULT_FILE_NAME);
        creationDateFile = new File(root, DATE_FILE_NAME);



        try {
            if(!creationDateFile.exists()){
                Log.d(TAG, "checkAndCreateLogFile: create date file");
                creationDateFile.createNewFile();
            }
            if(!logFile.exists()){
                Log.d(TAG, "checkAndCreateLogFile: create log file");
                logFile.createNewFile();
                saveFileCreationDate();
            }
        } catch (IOException e){
            Log.e(TAG, "checkAndCreateLogFile: ", e);
        }
    }

    private void saveFileCreationDate() {
        Log.d(TAG, "saveFileCreationDate: ");
        long now = System.currentTimeMillis();
        try {
            writeLogToFile(String.valueOf(now), creationDateFile.getPath());
        } catch (IOException e) {
            Log.e(TAG, "saveFileCreationDate: ", e);
            //cannot save file creatin date, clearing all files
            deleteFile();
        }
    }

    private long getFileCreationDateMillis(){
        Log.d(TAG, "getFileCreationDateMillis: ");
        String content = readFile(creationDateFile, false);
        return Long.parseLong(content);
    }

    @Override
    public void init(Context context) {
        Log.d(TAG, "init: ");
        setupMapBatteryFiles();
        checkAndCreateLogFile(context);
    }

    private void setupMapBatteryFiles() {
        if(mPath2Value == null){
            mPath2Value = new LinkedHashMap<>();
        }
        mPath2Value.put("Status", "/sys/class/power_supply/battery/status");
        mPath2Value.put("Temp", "/sys/class/power_supply/battery/batt_temp");
        mPath2Value.put("State Of Charge", "/sys/class/power_supply/battery/batt_read_raw_soc");
//        mPath2Value.put("Capacity", "/sys/class/power_supply/battery/capacity");
//        mPath2Value.put("Capacity Max", "/sys/class/power_supply/battery/batt_capacity_max");
        mPath2Value.put("Voltage", "/sys/class/power_supply/battery/voltage_now");
        mPath2Value.put("Current", "/sys/class/power_supply/battery/current_now");
    }

    @Override
    public void dumpBatteryStatusToFile() {
        readBatteryLogFiles();
    }

    @Override
    public void scheduleSendLogFile() {


        String logFileContent = readFile(logFile, true);
        if(logFileContent == null){
            Log.w(TAG, "scheduleSendLogFile: file not found, abort task");
            return;
        }

        /*salvo contenuto file in un altro file su memoria privata interna. Questo meccanismo consente al Worker di monitoraggio
        di bypassare il limite di di dati da passare in Data (10kb). Il file interno verrà cancellato se invocata la funzione disconnetti */

        String copyFileName = DEFAULT_DIR_NAME + "_" + System.currentTimeMillis();

        try {
            InternalStorage.saveObjectToFile(logFileContent, copyFileName);
        } catch (Exception e) {
            Log.e(TAG, "scheduleSendLogFile: cannot save copy file", e);
            Log.e(TAG, "scheduleSendLogFile: aborting sending report..delete log files");
            deleteFile();
        }

        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

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

        String extra = "BATTERY";
        String level = "DEBUG";


        Data inputData = new Data.Builder()
                .putString(LogMonitorWork.DATA_MESSAGE, LogMonitorWork.FILE_CONTEXT + copyFileName)
                .putString(LogMonitorWork.DATA_LEVEL, level)
                .putString(LogMonitorWork.DATA_PATH, null)
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
        deleteFile();

    }

    @Override
    public boolean fileLogExpired() {
        try {
            long creationDateMillis = getFileCreationDateMillis();
            Date fileCreationDate = new Date(creationDateMillis);
            Calendar fileCalendar = Calendar.getInstance();
            fileCalendar.setTime(fileCreationDate);
            fileCalendar.add(Calendar.HOUR_OF_DAY, EXPIRY_HOURS);
//            fileCalendar.add(Calendar.MINUTE, 5);
            Date expiryDate = fileCalendar.getTime();
            Log.d(TAG, "log file expiry date: " + simpleDateFormat.format(expiryDate));
            Date now = new Date(System.currentTimeMillis());
            Log.d(TAG, "log now date: " + simpleDateFormat.format(now));
            return now.after(expiryDate);
        } catch (NumberFormatException e){
            Log.e(TAG, "fileLogExpired: ", e);
            deleteFile();
            return false;
        }
    }

    @Override
    public String getDefaultDirName() {
        return DEFAULT_DIR_NAME;
    }

    @Override
    public String getDefaultLogFileName() {
        return DEFAULT_FILE_NAME;
    }


    private void readBatteryLogFiles() {
        Log.d(TAG, "readBatteryLogFiles: reading logs from filesystem");
        String log = "";
        String currentDate = getCurrentDate();

        // OLD verbose impl
        /*log += "##### INIZIO LOG : " + currentDate + "\n";
        for (String valueName : mPath2Value.keySet()) {
            log += valueName + ": ";
            String valueContent = readFromFile(mPath2Value.get(valueName));
            log += valueContent + "|";
        }
        log += "\n ##### FINE LOG" + "\n\n";*/
        log += currentDate + "|";
        for (String valueName : mPath2Value.keySet()) {
            String valueContent = readFileFromPath(mPath2Value.get(valueName), false);
            log += valueContent + "|";
        }
        log += "\n";
        try {
            writeLogToFile(log, logFile.getPath());
        } catch (IOException e) {
            Log.e(TAG, "readBatteryLogFiles: ", e);
            //skip this log
        }
    }

    private void writeLogToFile(String log, String path) throws IOException {
            Log.d(TAG, "file path: "+ path);
            FileWriter fw = new FileWriter(path,true); //the true will append the new data
            fw.write(log);//appends the string to the file
            fw.close();
    }

    private String getCurrentDate() {
        String date;
        try{
            Date timestampDate = new Date(System.currentTimeMillis());
            date = simpleDateFormat.format(timestampDate);
        } catch (Exception e){
            Log.e(TAG, "getCurrentDate: ", e);
            date = "error";
        }
        return date;
    }

    private String readFileFromPath(String path, boolean includeLineSeparator){
        File file = new File(path);
        return readFile(file, includeLineSeparator);
    }

    private String readFile(File file, boolean includeLineSeparator){
        StringBuilder contentString = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                contentString.append(line);
                if(includeLineSeparator){
                    contentString.append("\n");
                }
            }
            br.close();
            return contentString.toString();
        }
        catch (IOException e) {
            Log.e(TAG, "readFromFile: error reading file", e);
            return null;
        }
    }

    private void deleteFile(){
        Log.d(TAG, "deleteFile: check if file exists: " + logFile.exists());
        logFile.delete();
        creationDateFile.delete();
        Log.d(TAG, "deleteFile: check if file exists: " + logFile.exists());
    }

}
