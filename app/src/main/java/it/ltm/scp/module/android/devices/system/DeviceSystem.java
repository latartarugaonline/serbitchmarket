package it.ltm.scp.module.android.devices.system;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;

import java.util.List;

import it.ltm.scp.module.android.App;
import it.ltm.scp.module.android.api.APICallback;
import it.ltm.scp.module.android.api.APICallbackV2;
import it.ltm.scp.module.android.api.pos.PosAPI;
import it.ltm.scp.module.android.api.system.SystemAPI;
import it.ltm.scp.module.android.devices.pos.DevicePos;
import it.ltm.scp.module.android.devices.scanner.DeviceScanner;
import it.ltm.scp.module.android.devices.terminal.TerminalManagerFactory;
import it.ltm.scp.module.android.model.Result;
import it.ltm.scp.module.android.model.devices.pos.gson.PosInfo;
import it.ltm.scp.module.android.model.devices.scanner.ScannerInfo;
import it.ltm.scp.module.android.model.devices.scanner.ScannerInfoData;
import it.ltm.scp.module.android.model.devices.system.gson.SystemInfo;
import it.ltm.scp.module.android.model.devices.system.gson.update.Update;
import it.ltm.scp.module.android.model.devices.system.gson.update.UpdateConfig;
import it.ltm.scp.module.android.model.devices.system.gson.update.UpdateRepository;
import it.ltm.scp.module.android.model.devices.system.gson.update.UpdateStatus;
import it.ltm.scp.module.android.monitor.model.terminal.Hw;
import it.ltm.scp.module.android.monitor.model.terminal.Pinpad;
import it.ltm.scp.module.android.monitor.model.terminal.Pos;
import it.ltm.scp.module.android.monitor.model.terminal.Sw;
import it.ltm.scp.module.android.monitor.model.terminal.Tablet;
import it.ltm.scp.module.android.monitor.model.terminal.Terminal;
import it.ltm.scp.module.android.monitor.model.terminal.TerminalReport;
import it.ltm.scp.module.android.utils.AppUtils;
import it.ltm.scp.module.android.utils.Errors;

/**
 * Created by HW64 on 18/10/2016.
 */
public class DeviceSystem {

    private static DeviceSystem mInstance;
    public static SystemInfo sysInfo = null;

    private DeviceSystem(){}

    public static synchronized DeviceSystem getInstance(){
        if(mInstance == null){
            mInstance = new DeviceSystem();
        }
        return mInstance;
    }

    public void getSystemInfo(APICallback apiCallback){
        new SystemAPI().getSystemInfo(apiCallback);
    }

    public void updateSystemInfo(){
        new SystemAPI().getSystemInfo(new APICallback() {
            @Override
            public void onFinish(Result result) {
                if(result.getCode() == Errors.ERROR_OK){
                    sysInfo = (SystemInfo) result.getData();
                    DevicePos.getInstance().updateCacheWithSystemInfo(sysInfo);
                }
            }
        });
    }

    public void checkAndUpdate(final APICallbackV2<UpdateStatus> apiCallbackV2){
        new SystemAPI().checkForUpdate(new APICallbackV2<Update>() {
            @Override
            public void onResult(Update result) {
                if(result.isUpgradable()){
                    new SystemAPI().doUpdate(null, apiCallbackV2);
                } else {
                    apiCallbackV2.onError(Errors.ERROR_GENERIC,
                            "Nessun aggiornamento trovato", null);
                }
            }

            @Override
            public void onError(int code, String message, Exception e) {
                apiCallbackV2.onError(code, message, e);
            }
        });
    }

    public void forceUpdateWithRepo(String repo, final APICallbackV2<UpdateStatus> callbackV2){
        UpdateRepository updateRepository = new UpdateRepository();
        updateRepository.setSourceRepository(repo);
        new SystemAPI().doUpdate(updateRepository, callbackV2);
    }

    public void getUpdateConfig(final APICallbackV2<UpdateConfig> callbackV2){
        new SystemAPI().getUpdateConfig(callbackV2);
    }

    public void putUpdateConfig(UpdateConfig config, final APICallbackV2<String> callbackV2){
        new SystemAPI().putUpdateConfig(callbackV2, config);
    }


    public void generateTerminalReport(final APICallbackV2<TerminalReport> callbackV2){
        chainPos(callbackV2);
    }

    private void chainPos(final APICallbackV2<TerminalReport> callbackV2){
        PosInfo cached = DevicePos.getInstance().getCachedPosInfo();
        if (cached != null){
            chainSystem(callbackV2, cached);
            return;
        }
        new PosAPI().getPosInfo(new APICallback() {
            @Override
            public void onFinish(Result result) {
                if(result.getCode() == Errors.ERROR_OK){
                    PosInfo posInfo = (PosInfo)result.getData();
                    chainSystem(callbackV2, posInfo);
                } else {
                    callbackV2.onError(result.getCode(),
                            result.getDescription(),
                            null);
                }
            }
        });
    }

    private void chainSystem(final APICallbackV2<TerminalReport> callbackV2, final PosInfo posInfo) {
        DeviceSystem.getInstance().getSystemInfo(new APICallback() {
            @Override
            public void onFinish(Result result) {
                if(result.getCode() == Errors.ERROR_OK){
                    SystemInfo systemInfo= (SystemInfo)result.getData();
                    chainBcr(callbackV2, posInfo, systemInfo);
                } else {
                    callbackV2.onError(result.getCode(),
                            result.getDescription(),
                            null);
                }
            }
        });
    }

    private void chainBcr(final APICallbackV2<TerminalReport> callbackV2, final PosInfo posInfo, final SystemInfo systemInfo) {
        DeviceScanner.getInstance().getScannerInfo(new APICallbackV2<ScannerInfo>() {
            @Override
            public void onResult(ScannerInfo result) {
                buildReportData(callbackV2, posInfo, systemInfo, result);
            }

            @Override
            public void onError(int code, String message, Exception e) {
//                callbackV2.onError(code, message, e);
                //bypass errori su BCR?
                buildReportData(callbackV2, posInfo, systemInfo, null);
            }
        });
    }

    private void buildReportData(APICallbackV2<TerminalReport> callbackV2, PosInfo posInfo, SystemInfo systemInfo, ScannerInfo result) {
        Hw hw = new Hw();
        Pos pos = new Pos();
        pos.setReleaseFw(posInfo.getPOSRelease());
        pos.setSerialNumber(posInfo.getPOSSerial());
        pos.setEmvVersion(posInfo.getEMVVersion());
        hw.setPos(pos);

        if (!posInfo.getPinpadModel().trim().isEmpty()){
            Pinpad pinpad = new Pinpad();
            pinpad.setModel(posInfo.getPinpadModel());
            pinpad.setSerialNumber(posInfo.getAuxExtDeviceDescription());
            hw.setPinpad(pinpad);
        }

        Terminal terminal = new Terminal();
        terminal.setModel(TerminalManagerFactory.get().getDeviceName());
        terminal.setReleaseFw(systemInfo.getSystemVersion());
        terminal.setSerialNumber(systemInfo.getSerialNumber());
        terminal.setPartNumber(systemInfo.getPartNumber());
        hw.setTerminal(terminal);

        if(result != null){
            for (ScannerInfoData scannerInfoData : result.getData()){
                hw.addBcr(scannerInfoData.getId(), scannerInfoData.getModelName());
            }
        }


        Tablet tablet = new Tablet();
        tablet.setSerialNumber(AppUtils.getDeviceSerial());
        hw.setTablet(tablet);

        TerminalReport terminalReport = new TerminalReport();
        terminalReport.setHw(hw);

        //set sw

        Sw sw = new Sw();
        PackageManager pm = App.getContext().getPackageManager();
        List<PackageInfo> installedPackages = pm.getInstalledPackages(0);
        if(!installedPackages.isEmpty()){

            for (PackageInfo pInfo :
                    installedPackages) {
                it.ltm.scp.module.android.monitor.model.terminal.App app = new it.ltm.scp.module.android.monitor.model.terminal.App();
                app.setId(pInfo.packageName);
                app.setName(String.valueOf(pm.getApplicationLabel(pInfo.applicationInfo)));
                app.setVersion(pInfo.versionName);
                sw.addApp(app);
            }

        }
        terminalReport.setSw(sw);

        callbackV2.onResult(terminalReport);
    }

 /*   public void restoreConfigAndClear(UpdateConfig config){
        Log.d("DeviceSystem", "restoreConfigAndClear: restoring config..");
        putUpdateConfig(config, new APICallbackV2<String>() {
            @Override
            public void onResult(String result) {
                //default config restored, delete backup
                Log.d("DeviceSystem", "restoreConfigAndClear: config restored, deleting backup");
                AppUtils.clearUpdateConfig(App.getContext());
            }

            @Override
            public void onError(int code, String message, Exception e) {
                Log.e("DeviceSystem", "restoreConfigAndClear: error restoring config");
                Log.e("DeviceSystem", "onError() called with: code = [" + code + "], message = [" + message + "], e = [" + e + "]");
                //do nothing
            }
        });
    }*/


}
