package it.ltm.scp.module.android.managers;

import it.ltm.scp.module.android.devices.terminal.TerminalManagerFactory;

public class ConnectionManagerFactory {

    private static ConnectionManager mInstance;

    public static synchronized ConnectionManager getConnectionManagerInstance(){
        if (mInstance == null){
            mInstance = TerminalManagerFactory.get().getConnectionManager();
        }
        return mInstance;
    }
}
