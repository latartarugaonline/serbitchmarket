package it.ltm.scp.module.android.devices.terminal;

import it.ltm.scp.module.android.utils.Constants;

public class TerminalManagerP2ProImpl extends TerminalManagerIGP2030SImpl {

    @Override
    public String getDeviceName() {
        return Constants.DEVICE_P2_PRO;
    }

    @Override
    public String getRestApiUrl(){
        return Constants.URL_API_REST_P2PRO;
    }

    @Override
    public String getWsUrl(){
        return Constants.URL_WEB_SOCKET_P2PRO;
    }
}
