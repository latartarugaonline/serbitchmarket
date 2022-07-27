package it.ltm.scp.module.android.devices.terminal;

import it.ltm.scp.module.android.utils.Constants;

public class TerminalManagerSunmiSImpl extends TerminalManagerIGP2030SImpl {

    @Override
    public String getDeviceName() {
        return Constants.DEVICE_SUNMI_S;
    }

    @Override
    public String getRestApiUrl() {
        return Constants.URL_API_REST_T2S_LITE;
    }

    @Override
    public String getWsUrl() {
        return Constants.URL_WEB_SOCKET_T2S_LITE;
    }
}

