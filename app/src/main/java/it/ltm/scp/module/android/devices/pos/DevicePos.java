package it.ltm.scp.module.android.devices.pos;

import android.util.Log;

import it.ltm.scp.module.android.api.APICallback;
import it.ltm.scp.module.android.api.pos.PosAPI;
import it.ltm.scp.module.android.devices.system.DeviceSystem;
import it.ltm.scp.module.android.devices.terminal.TerminalManagerFactory;
import it.ltm.scp.module.android.exceptions.MalformedTsnException;
import it.ltm.scp.module.android.model.Result;
import it.ltm.scp.module.android.model.devices.pos.buzzer.gson.PosBuzzer;
import it.ltm.scp.module.android.model.devices.pos.display.gson.PosDisplay;
import it.ltm.scp.module.android.model.devices.pos.gson.Auth;
import it.ltm.scp.module.android.model.devices.pos.gson.AuthRequest;
import it.ltm.scp.module.android.model.devices.pos.gson.PosInfo;
import it.ltm.scp.module.android.model.devices.pos.payment.gson.PaymentType;
import it.ltm.scp.module.android.model.devices.pos.printer.gson.PosPrint;
import it.ltm.scp.module.android.model.devices.pos.printer.gson.PosPrintComplex;
import it.ltm.scp.module.android.model.devices.pos.prompt.PromptRequest;
import it.ltm.scp.module.android.model.devices.pos.tsn.TsnDTO;
import it.ltm.scp.module.android.model.devices.pos.tsn.TsnData;
import it.ltm.scp.module.android.model.devices.system.gson.SystemInfo;
import it.ltm.scp.module.android.utils.AppUtils;
import it.ltm.scp.module.android.utils.Errors;
import it.ltm.scp.module.android.utils.TsnUtils;

/**
 * Created by HW64 on 15/09/2016.
 */
public class DevicePos {

    public interface PosInfoCallback {
        void onResult(Result result);

        void onReauth(PosInfo posInfo);

        void onError(String message, int code);
    }

    private DevicePos() {
    }

    public static synchronized DevicePos getInstance() {
        if (mInstance == null) {
            mInstance = new DevicePos();
        }
        return mInstance;
    }

    private static final String POS_TYPE_P2_SMARTPAD = "58";
    private static final String TAG = DevicePos.class.getSimpleName();
    private static DevicePos mInstance;
    private PosInfo mCachedPosInfo;

    public static final int ERROR_POS_INTERNAL = 103;
    public String ERROR_POS_INTERNAL_MESSAGE = "Errore interno del POS, verifica eventuali operazioni in sospeso sul POS e riprova.";

    public static final int ERROR_POS_UNREACH = 107;
    public String ERROR_POS_UNREACH_MESSAGE = "Attenzione, POS non raggiungibile oppure eventuali operazioni in sospeso su POS. Verifica che il POS sia posizionato alla voce 'SERVIZI' e riprova.";

    public static final int ERROR_POS_CONNECTION = 190;
    public String ERROR_POS_CONNECTION_MESSAGE = "Impossibile connettersi al POS, riprova oppure contatta il supporto.";

    public static final int ERROR_POS_CONF = 201;
    public String ERROR_POS_CONF_MESSAGE = "Il POS non è configurato. Contatta il supporto tecnico.";

    public static final int ERROR_POS_AUTH = 202;
    public String ERROR_POS_AUTH_MESSAGE = "Inserisci la carta operatore nel POS, digita il PIN e segui le istruzioni indicate.";

    public static final int ERROR_POS_ABORT = 203;
    public String ERROR_POS_ABORT_MESSAGE = "Autenticazione annullata esplicitamente dall’operatore.";

    public static final int ERROR_POS_ABORT_TIMEOUT = 204;
    public String ERROR_POS_ABORT_TIMEOUT_MESSAGE = "Operazione annullata per tempo scaduto.";

    public static final int ERROR_POS_SERVER = 205;
    public String ERROR_POS_SERVER_MESSAGE = "Autenticazione fallita. Contatta il supporto tecnico.";

    public static final int ERROR_POS_BUSY = 423;
    public String ERROR_POS_BUSY_MESSAGE = "POS già in uso, verifica eventuali operazioni in sospeso sul POS e riprova.";

    public static final int ERROR_POS_PAY_ABORT_TIMEOUT = 403;
    public String ERROR_POS_PAY_ABORT_TIMEOUT_MESSAGE = "Pagamento annullato per tempo scaduto.";

    public static final int ERROR_POS_PAY_ABORT = 404;
    public String ERROR_POS_PAY_ABORT_MESSAGE = "Pagamento annullato esplicitamente dall’operatore.";

    public static final int ERROR_POS_PAY_AUTH = 406;

    public String ERROR_POS_OP_ABORT_MESSAGE = "Operazione sul POS annullata, riprovare.";
    public String ERROR_POS_OP_TIMEOUT = "Operazione annullata per tempo scaduto.";

    public String USERCODE_EMPTY = "00000000";

    public void doAuthentication(String modulus, String exponent, String deviceName, APICallback callback) {
        AuthRequest authRequest = new AuthRequest(modulus, exponent, deviceName);
        new PosAPI().doAuthentication(authRequest, callback);
    }

    public void doAuthenticationAsync(String modulus, String exponent, String deviceName, APICallback callback) {
        AuthRequest authRequest = new AuthRequest(modulus, exponent, deviceName);
        new PosAPI().doAuthenticationAsync(authRequest, callback);
    }

    public void getAuthStatusById(String requestId, APICallback callback) {
        new PosAPI().getAuthStatus(requestId, callback);
    }

    public void getPosInfo(final Auth auth, final PosInfoCallback callback) {
        if (mCachedPosInfo != null) {
            callback.onResult(new Result(Errors.ERROR_OK, mCachedPosInfo));
        } else {
            new PosAPI().getPosInfo(new APICallback() {
                @Override
                public void onFinish(Result result) {
                    validatePosInfo(result, auth, callback);
                }
            });
        }
    }

    public void refreshPosInfo(final Auth auth, final PosInfoCallback callback) {
        new PosAPI().getPosInfo(new APICallback() {
            @Override
            public void onFinish(Result result) {
                validatePosInfo(result, auth, callback);
            }
        });
    }

    public PosInfo getCachedPosInfo() {
        return mCachedPosInfo;
    }

    public Result getPosInfoSync() {
        return new PosAPI().getPosInfoSync();
    }

    public void print(int type, String printText, APICallback callback) {
        PosPrint posPrint = new PosPrint(type, printText);
        new PosAPI().print(posPrint, callback);
    }

    public void printComplex(String complex, APICallback callback) {
        PosPrintComplex posPrintComplex = new PosPrintComplex();
        posPrintComplex.setCommand(complex);
        new PosAPI().printComplex(posPrintComplex, callback);
    }

    public void display(String text, APICallback callback) {
        PosDisplay posDisplay = new PosDisplay(text);
        new PosAPI().display(posDisplay, callback);
    }

    public void clearDisplay(APICallback callback) {
        new PosAPI().clearDisplay(callback);
    }

    public void buzz(int type, int duration, APICallback callback) {
        PosBuzzer posBuzzer = new PosBuzzer(type, duration);
        new PosAPI().buzz(posBuzzer, callback);
    }

    public void getPayments(APICallback callback) {
        new PosAPI().getPayments(callback);
    }

    public void getPayment(String paymentId, APICallback callback) {
        new PosAPI().getPayment(paymentId, callback);
    }

    public void processPayment(String paymentType, PaymentType payment, APICallback callback) {
        new PosAPI().processPayment(paymentType, payment, callback);
    }

    public void processPaymentAsync(String paymentType, PaymentType payment, APICallback callback) {
        new PosAPI().processPaymentAsync(paymentType, payment, callback);
    }

    public void getPaymentStatusById(String requestId, APICallback callback) {
        new PosAPI().getPaymentStatus(requestId, callback);
    }

    public void getPromptAsync(PromptRequest request, APICallback callback) {
        new PosAPI().getPromptAsync(request, callback);
    }

    public void getPromptAsyncStatus(String requestID, APICallback callback) {
        new PosAPI().getPromptAsyncStatus(requestID, callback);
    }

    public void getTsn(int timeout, String displayMessage, String readType, final APICallback callback) {
        new PosAPI().getTsn(timeout, displayMessage, readType, new APICallback() {
            @Override
            public void onFinish(Result result) {
                if (result.getCode() == Errors.ERROR_OK) {
                    //parsare TsnData con TsnDTO
                    TsnData data = (TsnData) result.getData();
                    try {
                        TsnDTO tsnDTO = TsnUtils.parseTsnData(data.getTsnData());
                        callback.onFinish(new Result(
                                Errors.ERROR_OK,
                                tsnDTO
                        ));
                    } catch (MalformedTsnException e) {
                        Log.e(TAG, "onFinish: ", e);
                        callback.onFinish(new Result(Errors.ERROR_INPUT_TSN,
                                Errors.getMap().get(Errors.ERROR_INPUT_TSN),
                                e.getMessage()));
                    }
                } else {
                    //forward
                    callback.onFinish(result);
                }
            }
        });
    }

    public void getTsnAsync(int timeout, String displayMessage, String readType, final APICallback callback) {
        new PosAPI().getTsnAsync(timeout, displayMessage, readType, callback);
    }

    public void getTsnAsyncStatus(String requestID, final APICallback callback) {
        new PosAPI().getTsnAsyncStatus(requestID, callback);
    }

    private void validatePosInfo(Result result, Auth auth, PosInfoCallback callback) {
        if (result.getCode() == Errors.ERROR_OK) {
            PosInfo posInfo = (PosInfo) result.getData();
            if (posInfo.getPhysicalUserCode().isEmpty() || posInfo.getPhysicalUserCode().equals(USERCODE_EMPTY)) {
                mCachedPosInfo = null;
                if (callback != null)
                    callback.onError(ERROR_POS_AUTH_MESSAGE,
                            PosUtils.parsePosCode(ERROR_POS_AUTH));
            } else {
                //appendo informazioni versioni app e iPOS
                posInfo.setAppVersion(AppUtils.getAppVersion());
                posInfo.setTabletSerial(AppUtils.getDeviceSerial());
                if (DeviceSystem.sysInfo != null) {
                    String IPOSVersion = TerminalManagerFactory.get().getDeviceName() + "_" + DeviceSystem.sysInfo.getSystemVersion();
                    Log.d(TAG, "validatePosInfo: terminal version: " + IPOSVersion);
                    posInfo.setIPOSSerial(DeviceSystem.sysInfo.getSerialNumber());
                    posInfo.setIPOSVersion(IPOSVersion);
                } else {
                    DeviceSystem.getInstance().updateSystemInfo();
                }

                try {
                    if (posInfo.getPhysicalUserCode().equals(auth.getPhysicalUserCode())) {
                        posInfo.setUserCode(auth.getUserCode()); //metto usercode virtuale sulla PosInfo
                        mCachedPosInfo = posInfo;
                        if (callback != null)
                            callback.onResult(result);
                    } else {
                        mCachedPosInfo = null;
                        Log.e(TAG, "validatePosInfo: usercode changed, reauth");
                        if (callback != null)
                            callback.onReauth(posInfo);
                    }
                } catch (NullPointerException e) {
                    /*
                    /@Param auth in un caso risulta null:
                    - token scaduto
                    - nuova richiesta autenticazione -> cancellazione auth data esistenti
                    - nuova richiesta autenticazione fallisce -> dati auth = null
                    - richiesta di posInfo -> in questo metodo riceve i dati di auth = null
                     */
                    Log.e(TAG, "", e);
                    if (callback != null)
                        callback.onReauth(posInfo);
                }
            }
        } else {
            Log.e(TAG, result.toJsonString());
            mCachedPosInfo = null;
            if (callback != null) {
                callback.onError(result.getDescription(),
                        result.getCode());
            }
        }
    }

    public void updateCacheWithSystemInfo(SystemInfo systemInfo) {
        if (mCachedPosInfo != null) {
            mCachedPosInfo.setIPOSSerial(systemInfo.getSerialNumber());
            mCachedPosInfo.setIPOSVersion(systemInfo.getSystemVersion());
        }
    }

    public void clearCache() {
        mCachedPosInfo = null;
    }

    public void setPinpadRelatedMessage(String posType) {
        if (posType.equals(POS_TYPE_P2_SMARTPAD)) {
            ERROR_POS_AUTH_MESSAGE = "Digitare il codice della carta operatore nel POS e segui le istruzioni indicate per inserire il PIN.";
        }
    }
}
