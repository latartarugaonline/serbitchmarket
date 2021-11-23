package it.ltm.scp.module.android.monitor.battery;

import android.content.Context;

public interface BatteryMonitor {

    boolean monitorEnabled();

    void checkAndCreateLogFile(Context context);

    void init(Context context);

    void dumpBatteryStatusToFile();

    void scheduleSendLogFile();

    boolean fileLogExpired();

    String getDefaultDirName();

    String getDefaultLogFileName();


}
