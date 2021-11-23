package it.ltm.scp.module.android.devices.terminal;

import it.ltm.scp.module.android.utils.Constants;

public class TerminalManagerSunmiImpl extends TerminalManagerIGP2030SImpl {

    @Override
    public String getDeviceName() {
        return Constants.DEVICE_SUNMI;
    }

/*    @Override
    public void parsePrinterStatus(HashMap<Integer, String> map, Status printerStatus) {
        super.parsePrinterStatus(map, printerStatus);
    }*/
}
