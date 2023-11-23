package it.ltm.scp.module.android.devices.terminal;

import android.util.Log;

import it.ltm.scp.module.android.utils.AppUtils;

public class TerminalManagerFactory {

    private static TerminalManager mInstance;

    public static TerminalManager get() {
        if (mInstance == null) {
            if (AppUtils.isIGP()) {
                mInstance = new TerminalManagerIGP2030SImpl();
            } else if (AppUtils.isSunmi()) {
                mInstance = new TerminalManagerSunmiImpl();
            } else if (AppUtils.isSunmiLite()) {
                mInstance = new TerminalManagerSunmiLiteImpl();
            } else if (AppUtils.isP2Pro()) {
                mInstance = new TerminalManagerP2ProImpl();
            } else if (AppUtils.isSunmiS()) {
                mInstance = new TerminalManagerSunmiSImpl();
            } else {
                mInstance = new TerminalManagerDefaultImpl();
            }
        }
        return mInstance;
    }
}
