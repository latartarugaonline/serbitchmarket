package it.ltm.scp.module.android.devices.terminal;

import java.util.HashMap;

import it.ltm.scp.module.android.managers.ConnectionManager;
import it.ltm.scp.module.android.model.Version;
import it.ltm.scp.module.android.model.devices.printer.gson.status.Status;
import it.ltm.scp.module.android.monitor.battery.BatteryMonitor;

public interface TerminalManager {

    String getDeviceName();

    String getRestApiUrl();

    String getWsUrl();

    ConnectionManager getConnectionManager();

    void parsePrinterStatus(HashMap<Integer, String> map, Status printerStatus);

    String getKoFromPrinterStatus(Status printerStatus);

    boolean autoCutPaperWhenStatusOK();

    boolean clearZebraConfigFromIpos();

    Version getMinVersion();

    BatteryMonitor getBatteryMonitor();
}
