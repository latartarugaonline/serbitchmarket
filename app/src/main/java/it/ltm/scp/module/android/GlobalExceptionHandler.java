package it.ltm.scp.module.android;

import android.content.Context;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import it.ltm.scp.module.android.utils.AppUtils;

public class GlobalExceptionHandler implements Thread.UncaughtExceptionHandler {
    private Thread.UncaughtExceptionHandler defaultErrorHanlder;
    private Context context;

    public GlobalExceptionHandler(Context context) {
        this.defaultErrorHanlder = Thread.getDefaultUncaughtExceptionHandler();
        this.context = context;
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        writeLogToFile(e);
        defaultErrorHanlder.uncaughtException(t, e);
    }

    private void writeLogToFile(Throwable e) {
        if (AppUtils.isSunmi() || AppUtils.isSunmiS()) {
            try {
                File logDirectory = new File(context.getExternalFilesDir(null), "service_market_log");
                if (!logDirectory.exists()) {
                    logDirectory.mkdirs();
                }

                cleanOldLogs(logDirectory);

                SimpleDateFormat dateFormat = new SimpleDateFormat("dd_MM_yyyy", Locale.getDefault());
                String fileName = "log_" + dateFormat.format(new Date()) + ".txt";
                File logFile = new File(logDirectory, fileName);

                FileWriter writer = new FileWriter(logFile, true);
                writer.append("Timestamp: ").append(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(new Date())).append("\n");
                writer.append("Error: ").append(e.toString()).append("\n");
                writer.append("Stack Trace:\n");
                for (StackTraceElement element : e.getStackTrace()) {
                    writer.append("\t").append(element.toString()).append("\n");
                }
                writer.append("\n");
                writer.flush();
                writer.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    private void cleanOldLogs(File directory) {
        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                long now = System.currentTimeMillis();
                for (File file : files) {
                    if (now - file.lastModified() > 30L * 24 * 60 * 60 * 1000) {
                        file.delete();
                    }
                }
            }
        }
    }
}

