package it.ltm.scp.module.android.controllers;

import android.support.annotation.NonNull;
import android.util.Log;

import java.lang.ref.WeakReference;

import it.ltm.scp.module.android.App;
import it.ltm.scp.module.android.api.APICallback;
import it.ltm.scp.module.android.api.APICallbackV2;
import it.ltm.scp.module.android.devices.pos.DevicePos;
import it.ltm.scp.module.android.devices.pos.PosUtils;
import it.ltm.scp.module.android.devices.scanner.DeviceScanner;
import it.ltm.scp.module.android.devices.system.DeviceSystem;
import it.ltm.scp.module.android.managers.ConnectionManager;
import it.ltm.scp.module.android.managers.ConnectionManagerFactory;
import it.ltm.scp.module.android.managers.TerminalStatusManager;
import it.ltm.scp.module.android.managers.secure.Authenticator;
import it.ltm.scp.module.android.model.Result;
import it.ltm.scp.module.android.model.Version;
import it.ltm.scp.module.android.model.devices.pos.gson.Auth;
import it.ltm.scp.module.android.model.devices.pos.gson.AuthAsyncWrapper;
import it.ltm.scp.module.android.model.devices.pos.gson.PosInfo;
import it.ltm.scp.module.android.model.devices.pos.payment.gson.PaymentAsyncWrapper;
import it.ltm.scp.module.android.model.devices.pos.prompt.PromptResponseAsyncWrapper;
import it.ltm.scp.module.android.model.devices.pos.tsn.TsnAsyncWrapper;
import it.ltm.scp.module.android.model.devices.printer.gson.status.Status;
import it.ltm.scp.module.android.model.devices.scanner.ScannerSnapshot;
import it.ltm.scp.module.android.model.devices.scanner.ScannerStatus;
import it.ltm.scp.module.android.model.devices.scanner.ScannerUpdate;
import it.ltm.scp.module.android.model.devices.system.gson.update.UpdateConfig;
import it.ltm.scp.module.android.model.devices.system.gson.update.UpdateStatus;
import it.ltm.scp.module.android.ui.LaunchActivity;
import it.ltm.scp.module.android.utils.AppUtils;
import it.ltm.scp.module.android.utils.Constants;
import it.ltm.scp.module.android.utils.Errors;
import it.ltm.scp.module.android.utils.Properties;

/**
 * Created by HW64 on 25/11/2016.
 */

public class LaunchActivityController extends WebSocketController implements Authenticator.AuthenticatorCallback {
    // view
    private WeakReference<LaunchActivity> mView;

    // logic
    private Authenticator mAuthenticator;
    private TerminalStatusManager mStateManager;

    private final int STATE_DEFAULT = 0;
    private final int STATE_CONNECTING = 1;
    private final int STATE_ERROR = 2;
    private final int STATE_FINISH = 3;

    private boolean shouldReset = false;

    private final String TAG = LaunchActivityController.class.getSimpleName();
    private final String UPDATE_CONFIG_DOWNLOAD_ERROR = "Errore download configurazione aggiornamento.";
    private final String UPDATE_CONFIG_UPLOAD_ERROR = "Errore upload configurazione aggiornamento.";

    public LaunchActivityController(LaunchActivity mView) {
        this.mView = new WeakReference<>(mView);
        this.mAuthenticator = new Authenticator(mView.getApplicationContext(), this);
        mStateManager = new TerminalStatusManager();
        ConnectionManagerFactory.getConnectionManagerInstance().init(mView.getApplicationContext());
    }

    private LaunchActivity getView() throws Exception {
        if (mView.get() == null) {
            throw new Exception(LaunchActivity.class.getSimpleName() + " is null");
        } else {
            return mView.get();
        }
    }

    @Override
    public void destroy() {
        super.destroy();
        mStateManager.removeListeners();
        mAuthenticator.kill();
        mAuthenticator = null;
    }

    private void updateViewMessage(String message) {
        try {
            getView().switchLayout(STATE_DEFAULT);
            getView().updateText(message);
        } catch (Exception e) {
        }
    }

    private void proccessUpdateStatus(String message, boolean finish) {
        try {
            getView().processUpdateStatus(message, finish);
        } catch (Exception e) {
        }
    }

    /**
     * @see it.ltm.scp.module.android.managers.secure.Authenticator
     */
    @Override
    public void onAuthResult(Result result, String jsCallback) {
        Auth auth = (Auth) result.getData();
        getPosInfo(auth);
    }

    private void getPosInfo(Auth auth) {
        Log.d(TAG, "getPosInfo: start PosInfo");
        DevicePos.getInstance().clearCache();
        DevicePos.getInstance().getPosInfo(auth, mView.get(), new DevicePos.PosInfoCallback() {
            @Override
            public void onResult(Result result) {
                Log.d(TAG, "getPosInfo: PosInfo OK");
                restoreZebraConfig();
                startApp();
            }

            @Override
            public void onReauth(PosInfo posInfo) {
                Log.w(TAG, "getPosInfo: PosInfo Reauth");
                try {
                    AppUtils.clearAuthData(getView());
                    startAuth(shouldReset);
                } catch (Exception e) {
                    Log.e(TAG, "", e);
                }
            }

            @Override
            public void onError(String message, int code) {
                Log.e(TAG, "onTimeout() called with: message = [" + message + "], code = [" + code + "]");
                String errorMessage = PosUtils.appendCodeToMessage(message,
                        PosUtils.parsePosCode(code));
                processError(false, errorMessage);
            }
        });
    }

    @Override
    public void onAuthFailed(String errorMessage, int errorCode) {
        errorMessage = PosUtils.appendCodeToMessage(errorMessage, errorCode);
        if (errorCode == Errors.ERROR_SECURITY_INTERNAL) {
            processError(true, errorMessage);
        } else {
            processError(false, errorMessage);
        }
    }

    @Override
    public void onAuthMessage(int code) {
        try {
            switch (code) {
                case Authenticator.STATUS_START:
                    updateViewMessage("Controllo sicurezza");
                    updateTextSize(22);
                    break;
                case Authenticator.STATUS_REQUEST_LOGIN_CREDENTIAL:
                    requestLoginCredential("", false);
                    break;
                case Authenticator.STATUS_POS_CALLING:
                    Log.d("SecureManager: ", "authenticating..");
                    updateTextSize(22);
                    updateViewMessage("Autenticazione in corso, segui istruzioni sul pos");
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            return;
        }
    }

    @Override
    public void onLoginFailed(String message) {
        requestLoginCredential(message, false);
    }

    public void checkConnectivity() {
        mStateManager.checkStateAndForcePing(new TerminalStatusManager.StateListener() {
            @Override
            public void onFinish() {
                start(false); //TODO ripristinare dopo app version 0.4.8
//                fase0(false);
            }

            @Override
            public void onReconnect(String message) {
                updateViewMessage(message);
            }

            @Override
            public void onError(String message) {
                processError(false, message);
            }

            @Override
            public void onPing(String message) {
                updateViewMessage(message);
            }
        });
    }

    /*
    Bonifica URL aggiornamento iPOS
     */
    public void fase0(final boolean shouldReset) {
        Log.d(TAG, "fase0: check update config..");
        DeviceSystem.getInstance().getUpdateConfig(new APICallbackV2<UpdateConfig>() {
            @Override
            public void onResult(UpdateConfig config) {
                Log.d(TAG, "fase0: update config, URL: " + config.getSourceRepository());
                String urlBs = Properties.get(Constants.PROP_URL_IPOS_REPO_BS);
                String urlBsvip = Properties.get(Constants.PROP_URL_IPOS_REPO_BSVIP);
                if (config.getSourceRepository().equals(urlBs)
                        || config.getSourceRepository().equals(urlBsvip)) {
                    Log.d(TAG, "fase0: update config OK");
                    start(shouldReset);
                } else {
                    Log.w(TAG, "fase0: update config KO");
                    config.setSourceRepository(urlBs);
                    restoreRemoteUrl(config, shouldReset);
                }
            }

            @Override
            public void onError(int code, String message, Exception e) {
                Log.d(TAG, "onError() called with: code = [" + code + "], message = [" + message + "], e = [" + e + "]");
                if (code == 404) {
                    Log.e(TAG, "fase0: error config not found, creating new config..");
                    UpdateConfig newConfig = new UpdateConfig();
                    newConfig.setSourceRepository(Properties.get(Constants.PROP_URL_IPOS_REPO_BS));
                    newConfig.setEnabled(true);
                    newConfig.setFirstUpdate("2017-09-27T09:00:00.000Z");
                    newConfig.setUpdateFrequency("daily");
                    restoreRemoteUrl(newConfig, shouldReset);
                    return;
                }
                Log.e(TAG, "fase0: error getting config, skipping..");
                showSnackBar(UPDATE_CONFIG_DOWNLOAD_ERROR);
                start(shouldReset);
            }
        });
    }

    private void restoreRemoteUrl(UpdateConfig config, final boolean shouldReset) {
        Log.d(TAG, "restoreRemoteUrl: pushing correct config..");
        DeviceSystem.getInstance().putUpdateConfig(config, new APICallbackV2<String>() {
            @Override
            public void onResult(String result) {
                Log.d(TAG, "restoreRemoteUrl: pushing OK");
                start(shouldReset);
            }

            @Override
            public void onError(int code, String message, Exception e) {
                Log.d(TAG, "onError() called with: code = [" + code + "], message = [" + message + "], e = [" + e + "]");
                Log.e(TAG, "restoreRemoteUrl: pushing KO");
                showSnackBar(UPDATE_CONFIG_UPLOAD_ERROR);
                start(shouldReset);
            }
        });
    }

    // FASE 1
    public void start(final boolean shouldReset) {
        switchLayout(STATE_DEFAULT);
        updateTextSize(22);
        updateViewMessage("Controllo compatibilità");
        Log.d(TAG, "startListener: Controllo compatibilità");
        DeviceSystem.getInstance().getSystemInfo(new APICallback() {
            @Override
            public void onFinish(Result result) {
                switch (result.getCode()) {
                    case Errors.ERROR_OK:
                        try {
                            Log.d(TAG, "onFinish: IPOS Version check: compatible");
                            startAuth(shouldReset);
                        } catch (Exception e) {
                            Log.e(TAG, "onFinish: ", e);
                            processError(false, PosUtils.appendCodeToMessage(
                                    Errors.getMap().get(Errors.ERROR_CHECK_VERSION),
                                    Errors.ERROR_CHECK_VERSION
                            ));
                            return;
                        }
                        break;
                    case Errors.ERROR_NET_IO_IPOS:
                        if (ConnectionManagerFactory.getConnectionManagerInstance().getState() != ConnectionManager.State.CONNECTED) {
                            mStateManager.checkState(new TerminalStatusManager.StateListener() {
                                @Override
                                public void onFinish() {
                                    start(shouldReset);
                                }

                                @Override
                                public void onReconnect(String message) {
                                    updateViewMessage(message);
                                }

                                @Override
                                public void onError(String message) {
                                    processError(false, message);
                                }

                                @Override
                                public void onPing(String message) {
                                    updateViewMessage(message);
                                }
                            });
                            break;
                        }
                    default:
                        String errorMessage = PosUtils.appendCodeToMessage(result.getDescription(), result.getCode());
                        processError(shouldReset, errorMessage);
                        break;
                }

            }
        });
    }

    private void forceUpdate(final String messageError) {
        updateViewMessage("Cerco aggiornamenti per LIS@");
        String repo = Properties.get(Constants.PROP_FW_REPO_URL);
        Log.d(TAG, "forcing update at " + repo + "..");
        DeviceSystem.getInstance().forceUpdateWithRepo(repo, new APICallbackV2<UpdateStatus>() {
            @Override
            public void onResult(UpdateStatus result) {
                if (result.getStatus().equals("started")) {
                    updateViewMessage("Aggiornamento LIS@ in corso");
                } else {
                    updateViewMessage(messageError);
                    switchLayout(STATE_ERROR);
                }
            }

            @Override
            public void onError(int code, String message, Exception e) {
                updateViewMessage(messageError);
                switchLayout(STATE_ERROR);
            }
        });

    }

    private void checkForUpdate(final String messageError) {
        updateViewMessage("Cerco aggiornamenti per LIS@");
        Log.d(TAG, "checkForUpdate: checking update..");
        DeviceSystem.getInstance().checkAndUpdate(new APICallbackV2<UpdateStatus>() {
            @Override
            public void onResult(UpdateStatus result) {
                if (result.getStatus().equals("started")) {
                    updateViewMessage("Aggiornamento LIS@ in corso");
                } else {
                    updateViewMessage(messageError);
                    switchLayout(STATE_ERROR);
                }
            }

            @Override
            public void onError(int code, String message, Exception e) {
                updateViewMessage(messageError);
                switchLayout(STATE_ERROR);
            }
        });
    }

    private void updateTextSize(int size) {
        try {
            getView().setTextSize(size);
        } catch (Exception e) {
        }
    }

    private void switchLayout(int type) {
        try {
            getView().switchLayout(type);
        } catch (Exception e) {
        }
    }

    @NonNull
    private String getVersionErrorMessage(Version iposVersion, Version minVersion) {
        String message = Errors.getMap().get(Errors.ERROR_MIN_VERSION);
        return message +
                "\n\n" +
                "(min: " + minVersion.get() +
                ", curr: " + iposVersion.get() +
                ")";
    }

    public void startAuth(final boolean shouldReset) {
        mStateManager.checkState(new TerminalStatusManager.StateListener() {
            @Override
            public void onFinish() {
                mAuthenticator.start(shouldReset, true);
            }

            @Override
            public void onReconnect(String message) {
                updateViewMessage(message);
            }

            @Override
            public void onError(String message) {
                processError(false, message);
            }

            @Override
            public void onPing(String message) {
                updateViewMessage(message);
            }
        });
    }

    public void retry() {
        start(shouldReset);
    }

    private void processError(final boolean shouldReset, String message) {
        this.shouldReset = shouldReset;
        updateViewMessage(message);
        switchLayout(STATE_ERROR);
    }

    private void restoreZebraConfig() {
        DeviceScanner.getInstance().clearScannerConfigFromIpos(new APICallbackV2<Void>() {
            @Override
            public void onResult(Void result) {
                Log.d(TAG, "onResult() called with: result = [" + result + "]");
            }

            @Override
            public void onError(int code, String message, Exception e) {
                Log.w(TAG, "onError() called with: code = [" + code + "], message = [" + message + "], e = [" + e + "]", e);
            }
        });
    }

    private void startApp() {
        try {
            getView().startApp();
        } catch (Exception e) {
            return;
        }
    }

    @Override
    public void onPrinterStatus(Status status) {
        try {
            getView().processPrinterEvent(status);
        } catch (Exception e) {
        }
    }

    @Override
    public void onBarcodeEvent(String code) {

    }

    @Override
    public void onBarcodeStatusEvent(ScannerStatus status) {
        Log.d(TAG, "onBarcodeStatusEvent() called with: status = [" + status + "]");
        // Ignored
        if (status.getScanner().equalsIgnoreCase(ScannerStatus.SCANNER_ZEBRA)
                && status.getStatus().equalsIgnoreCase(ScannerStatus.STATUS_READY)) {
            try {
                getView().processBarcodeStatus("", false, true);
            } catch (Exception e) {
                Log.e(TAG, "onBarcodeStatusEvent: view is null");
            }

        }
    }


    @Override
    public void onBcrUpdateEvent(ScannerUpdate update) {
        Log.d(TAG, "onBcrUpdateEvent() called with: update = [" + update + "]");
        if (update.getCode().equals(ScannerUpdate.BCR_UPDATE_START_INSTALL)) {
            //show popup
            try {
                getView().processBarcodeStatus(ScannerUpdate.BCR_UPDATE_MESSAGE, false, false);
            } catch (Exception e) {
                Log.e(TAG, "onBcrUpdateEvent: view is null");
            }
        }
    }

    @Override
    public void onAuthEvent(AuthAsyncWrapper wrapper) {
        mAuthenticator.processAuthEvent(wrapper);
    }

    @Override
    public void onPaymentEvent(PaymentAsyncWrapper wrapper) {

    }

    @Override
    public void onTsnEvent(TsnAsyncWrapper wrapper) {

    }

    @Override
    public void onUpdateEvent(UpdateStatus status) {
        switch (status.getGeneralState()) {
            case UpdateStatus.STATE_DOWNLOADED:
                showSnackBar("Download aggiornamento completato");
                break;
            case UpdateStatus.STATE_DOWNLOADING:
                showSnackBar("Download aggiornamento LIS@");
                break;
            case UpdateStatus.STATE_START:
                proccessUpdateStatus("Aggiornamento di LIS@ in corso, il terminale si spegnerà a breve. " +
                        "Riavviare il terminale dopo lo spegnimento per riprendere le operazioni", false);
                break;
            default:
                DeviceSystem.getInstance().updateSystemInfo();
                try {
                    if (getView().isUpdatePending()) {
                        proccessUpdateStatus(status.getMessage(), true);
                        //restarting controller
                        checkConnectivity();
                    }
                } catch (Exception e) {
                    break;
                }
                break;
        }
    }

    @Override
    public void onPowerKeyPressed() {
        AppUtils.clearAuthData(App.getContext());
        try {
            AppUtils.closeAppWithDialog(getView());
        } catch (Exception e) {
            Log.e(TAG, "onPowerKeyPressed: ", e);
        }

    }

    @Override
    public void onSnapshotReceived(ScannerSnapshot snapshot) {
        //void
    }

    @Override
    public void onPromptEvent(PromptResponseAsyncWrapper wrapper) {

    }

    private void showSnackBar(String message) {
        try {
            getView().showSnackBar(message);
        } catch (Exception e) {

        }
    }

    private void requestLoginCredential(String message, boolean finish) {
        try {
            getView().requestLoginCredential(message, finish);
        } catch (Exception e) {
            Log.w(TAG, "requestLoginCredential: ", e);
        }
    }

    public void onCredentialAcquired(String username, String password) {
        requestLoginCredential("", true); //chiudi UI login
        updateViewMessage("Verifica credenziali in corso");
        mAuthenticator.onCredentialAcquired(username, password);
    }
}
