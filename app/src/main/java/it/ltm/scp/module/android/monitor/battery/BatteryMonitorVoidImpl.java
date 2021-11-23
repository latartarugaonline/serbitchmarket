package it.ltm.scp.module.android.monitor.battery;

import android.content.Context;

public class BatteryMonitorVoidImpl implements BatteryMonitor {


    @Override
    public boolean monitorEnabled() {
        return false;
    }

    @Override
    public void checkAndCreateLogFile(Context context) {

    }

    @Override
    public void init(Context context) {

    }

    @Override
    public void dumpBatteryStatusToFile() {

    }

    @Override
    public void scheduleSendLogFile() {

    }

    @Override
    public boolean fileLogExpired() {
        return false;
    }

    @Override
    public String getDefaultDirName() {
        return null;
    }

    @Override
    public String getDefaultLogFileName() {
        return null;
    }
}
