package it.ltm.scp.module.android.devices.terminal;

import android.util.Log;

import java.util.HashMap;

import it.ltm.scp.module.android.BuildConfig;
import it.ltm.scp.module.android.managers.ConnectionManager;
import it.ltm.scp.module.android.managers.ConnectionManagerDefaultImpl;
import it.ltm.scp.module.android.model.Version;
import it.ltm.scp.module.android.model.devices.printer.gson.status.Paper;
import it.ltm.scp.module.android.model.devices.printer.gson.status.Status;
import it.ltm.scp.module.android.monitor.battery.BatteryMonitor;
import it.ltm.scp.module.android.monitor.battery.BatteryMonitorLisaImpl;
import it.ltm.scp.module.android.utils.Constants;
import it.ltm.scp.module.android.utils.Properties;

public class TerminalManagerDefaultImpl implements TerminalManager {
    private static final String TAG = "TerminalManagerDefaultI";

    @Override
    public String getDeviceName() {
        return Constants.DEVICE_IPOS;
    }

    @Override
    public String getRestApiUrl(){
            return Properties.get(Constants.PROP_URL_REST_API_BASE);
    }

    @Override
    public String getWsUrl(){
            return Properties.get(Constants.PROP_WS_URL);
    }

    @Override
    public ConnectionManager getConnectionManager(){
            return new ConnectionManagerDefaultImpl();
    }

    @Override
    public void parsePrinterStatus(HashMap<Integer, String> map, Status printerStatus) {
        Log.d(TAG, "parsePrinterStatus() called");
        Paper paper = printerStatus.getPaper();
        if(paper.getStatusCode() == Status.CODE_PAPER_OK){
            map.remove(Status.CODE_PAPER_OK);
        } else {
            map.put(Status.CODE_PAPER_OK, Status.MESSAGE_PAPER_OK);
        }
        if(printerStatus.getAutoCutter().getStatusCode() == Status.CODE_CUTTER_OK){
            map.remove(Status.CODE_CUTTER_OK);
        } else {
            map.put(Status.CODE_CUTTER_OK, Status.MESSAGE_CUTTER_OK);
        }

        if(printerStatus.getPlaten().getStatusCode() == Status.CODE_PLATEN_OK){
            map.remove(Status.CODE_PLATEN_OK);
        } else {
            map.put(Status.CODE_PLATEN_OK, Status.MESSAGE_PLATEN_OK);
        }

        if(printerStatus.getThermalHead().getStatusCode() == Status.CODE_THERMAL_OK){
            map.remove(Status.CODE_THERMAL_OK);
        } else {
            map.put(Status.CODE_THERMAL_OK, printerStatus.getThermalHead().getText());
        }

        if(printerStatus.getPaperJam().getStatusCode() == Status.CODE_JAM_OK){
            map.remove(Status.CODE_JAM_OK);
        } else {
            map.put(Status.CODE_JAM_OK, printerStatus.getPaperJam().getText());
        }
    }

    @Override
    public String getKoFromPrinterStatus(Status printerStatus) {
        Log.d(TAG, "getKoFromPrinterStatus() called");
        String koMessage = "";
        if(printerStatus.getAutoCutter().getStatusCode() != Status.CODE_CUTTER_OK) {
            koMessage += " Taglierina: " + printerStatus.getAutoCutter().getText() + ";";
        }
        if(printerStatus.getPaper().getStatusCode() != Status.CODE_PAPER_OK) {
            koMessage += " Carta: " + printerStatus.getPaper().getText() + ";";
        }
        if(printerStatus.getPlaten().getStatusCode() != Status.CODE_PLATEN_OK) {
            koMessage += " Sportello: " + printerStatus.getPlaten().getText() + ";";
        }
        if(printerStatus.getPaperJam().getStatusCode() != Status.CODE_JAM_OK) {
            koMessage += " Jam carta: " + printerStatus.getPaperJam().getText() + ";";
        }
        if(printerStatus.getThermalHead().getStatusCode() != Status.CODE_THERMAL_OK) {
            koMessage += " Thermal: " + printerStatus.getThermalHead().getText() + ";";
        }

        return koMessage;
    }

    @Override
    public boolean autoCutPaperWhenStatusOK() {
        return true;
    }

    @Override
    public boolean clearZebraConfigFromIpos() {
        return true;
    }

    @Override
    public Version getMinVersion() {
        return new Version(BuildConfig.IPOS_MIN);
    }

    @Override
    public BatteryMonitor getBatteryMonitor() {
        return new BatteryMonitorLisaImpl();
    }
}
