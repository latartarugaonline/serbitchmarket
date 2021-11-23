package it.ltm.scp.module.android.monitor;

import android.util.Log;

import it.ltm.scp.module.android.model.Result;
import it.ltm.scp.module.android.monitor.model.Monitor;

public class LogWrapper {

    /*public static void e(String tag, Result result){
        String message = formatMessage(result);
        LogWrapper.e(tag, message);
    }

    private static String formatMessage(Result result) {
        String message = "";
        message += "code: " + result.getCode() + "; ";
        message += "message: " + result.getDescription() + "; ";
        message += "exceptionMessage: " + result.getExceptionMessage() + "; ";
        return message;
    }*/

    public static String parseStackTrace(Throwable tr){
        String stackTrace = Log.getStackTraceString(tr);
        stackTrace = stackTrace.replaceAll("[\\n\\t]", " ");
        return stackTrace;
    }

    public static void e(String tag, String msg) {
        LogWrapper.e(tag, msg, null, null);
    }

    public static void e(String tag, String msg, String extra) {
        LogWrapper.e(tag, msg, null, extra);
    }

    public static void e(String tag, String msg, Throwable tr) {
        LogWrapper.e(tag, msg, tr, null);
    }

    public static void e(String tag, String msg, Throwable tr, String extra) {
        Log.e(tag, msg, tr);
        LogMonitor.scheduleLogMonitor(tag, msg, tr, Monitor.LEVEL_ERROR, extra);
    }

    public class Web {
        public void e(String tag, String msg, String url){
            Log.e(tag, msg);
            LogMonitor.scheduleLogMonitor(tag, msg, null, url, Monitor.LEVEL_ERROR);
        }
    }

}
