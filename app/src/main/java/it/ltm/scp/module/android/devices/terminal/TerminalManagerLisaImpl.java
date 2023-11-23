package it.ltm.scp.module.android.devices.terminal;

import it.ltm.scp.module.android.utils.Constants;

public class TerminalManagerLisaImpl extends TerminalManagerIGP2030SImpl{

    @Override
    public String getDeviceName() {
        return "LISA";
    }

    @Override
    public String getRestApiUrl(){
        return Constants.URL_API_REST_LISA;
    }

    @Override
    public String getWsUrl(){
        return Constants.URL_WEB_SOCKET_LISA;
    }
}
