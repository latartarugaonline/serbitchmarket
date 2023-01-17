package it.ltm.scp.module.android.managers.secure;

import android.content.Context;
import android.util.Log;

import it.ltm.scp.module.android.api.APICallback;
import it.ltm.scp.module.android.api.APICallbackV2;
import it.ltm.scp.module.android.api.pos.PosAPI;
import it.ltm.scp.module.android.api.sm.ServiceMarketAPI;
import it.ltm.scp.module.android.devices.pos.DevicePos;
import it.ltm.scp.module.android.devices.pos.PosUtils;
import it.ltm.scp.module.android.exceptions.AppSignatureException;
import it.ltm.scp.module.android.managers.AsynchronousMode;
import it.ltm.scp.module.android.managers.ConnectionManager;
import it.ltm.scp.module.android.managers.ConnectionManagerFactory;
import it.ltm.scp.module.android.model.Result;
import it.ltm.scp.module.android.model.devices.pos.gson.Auth;
import it.ltm.scp.module.android.model.devices.pos.gson.AuthAsyncWrapper;
import it.ltm.scp.module.android.model.devices.pos.gson.PosInfo;
import it.ltm.scp.module.android.model.devices.pos.gson.PosResult;
import it.ltm.scp.module.android.model.sm.gson.VirtualAuth;
import it.ltm.scp.module.android.model.sm.gson.VirtualAuthData;
import it.ltm.scp.module.android.model.sm.gson.VirtualAuthEnabled;
import it.ltm.scp.module.android.model.sm.gson.VirtualAuthRequest;
import it.ltm.scp.module.android.monitor.ReportMonitor;
import it.ltm.scp.module.android.utils.AppUtils;
import it.ltm.scp.module.android.utils.Errors;
import it.ltm.scp.module.android.utils.RootUtils;

/**
 * Created by HW64 on 27/09/2016.
 */
public class Authenticator extends AsynchronousMode<AuthAsyncWrapper> {



    public interface AuthenticatorCallback {
        void onAuthResult(Result result, String jsCallback);

        void onAuthFailed(String errorMessage, int errorCode);

        void onLoginFailed(String message);

        void onAuthMessage(int code);
    }


    private SecureManager mSecureManager;
    private AuthenticatorCallback mCallback;
    private Context mContext;

    public static final int STATUS_START = 0;
    public static final int STATUS_POS_CALLING = 1;
    public static final int STATUS_FINISH = 2;
    public static final int STATUS_REQUEST_LOGIN_CREDENTIAL = 3;

    private String mJsCallback;
    private PosInfo mPosInfo;
    private Auth mAuthData;
    private ServiceMarketAPI mServiceMarketAPI;
    private boolean loginEnabled = false;

    private final String TAG = Authenticator.class.getSimpleName();



    public Authenticator(Context context, AuthenticatorCallback callback) {
        mSecureManager = SecureManager.getInstance();
        mCallback = callback;
        mContext = context.getApplicationContext();
        mAuthData = new Auth();
        mServiceMarketAPI = new ServiceMarketAPI();
        checkPinpad();
    }

    public void reauth(String jsCallback, PosInfo posInfo) {
        AppUtils.clearAuthData(mContext);
        mJsCallback = jsCallback;
        mPosInfo = posInfo;
        start(false, false);
    }

    public void retry() {
        reauth(mJsCallback, mPosInfo);
    }

    public void start(final boolean shouldResetKeys, boolean includeSecurityCheck) {
        mCallback.onAuthMessage(STATUS_START);

        //signature check
        if (!includeSecurityCheck) {
            startAuthProcedure(shouldResetKeys);
        } else {
            //app signature check
            try {
                checkSignature();
            } catch (AppSignatureException e) {
                Log.e(TAG, "startAuth: ", e);
                mCallback.onAuthFailed(Errors.getMap().get(Errors.ERROR_SECURITY_SIGNATURE),
                        Errors.ERROR_SECURITY_SIGNATURE);
                return;
            }
            Log.d(TAG, "startAuth: Signature check: OK");

            //root check
            if (!AppUtils.isIGP() && RootUtils.isDeviceRooted(mContext)) {
                mCallback.onAuthFailed(Errors.getMap().get(Errors.ERROR_SECURITY_ROOT),
                        Errors.ERROR_SECURITY_ROOT);
                return;
            }
            Log.d(TAG, "startAuth: root check: OK");

            //ssl certificate check
            new ServiceMarketAPI().checkCert(new APICallback() {
                @Override
                public void onFinish(Result result) {
                    if (result.getCode() == Errors.ERROR_OK) {
                        Log.d(TAG, "onFinish: SSL check: OK");
                        startAuthProcedure(shouldResetKeys);
                    } else {
                        Log.e(TAG, "onFinish: SSL check: KO");
//                        String message = result.getDescription() + "\n \n (" + result.getExceptionMessage() +")";
                        mCallback.onAuthFailed(result.getDescription(), result.getCode());
                    }

                }
            });
        }


    }

    private void startAuthProcedure(boolean shouldResetKeys) {
        Auth auth = AppUtils.getAuthData(mContext);

        // controllo se esiste un token
        if (auth != null) {
            if (AppUtils.isTokenValid(auth.getTokenExpiryDate())) {
                mCallback.onAuthResult(
                        new Result(Errors.ERROR_OK, auth),
                        mJsCallback);
                mCallback.onAuthMessage(STATUS_FINISH);
                return;
            }
        }
        // se non presente effettuare procedura d'avvio
        Result result = mSecureManager.initKeys(shouldResetKeys);
        switch (result.getCode()) {
            case SecureManager.OP_EXCEPTION:
                mCallback.onAuthFailed(PosUtils.getMessageFromErrorCode(result.getCode()), Errors.ERROR_SECURITY_INTERNAL);
                break;
            default:
                verify();
                break;
        }
    }

    private void checkSignature() throws AppSignatureException {
        mSecureManager.checkSignature(mContext);
    }

    private void verify() {
        if (mSecureManager.validateKeys()) {
            doAuth();
        } else {
            mCallback.onAuthFailed(PosUtils.getMessageFromErrorCode(SecureManager.OP_VERIFY_FAIL), Errors.ERROR_SECURITY_INTERNAL);
        }
    }

    private void checkPinpad() {
        new PosAPI().getPosInfo(new APICallback() {
            @Override
            public void onFinish(Result result) {
                try {
                    PosInfo posInfo = (PosInfo) result.getData();
                    DevicePos.getInstance().setPinpadRelatedMessage(posInfo.getPOSType());
                } catch (Exception ex) {
                    Log.e(TAG, "Unable to read POS Type", ex);
                }
            }
        });
    }

    private void doAuth() {
        try {
            String exponent = mSecureManager.getExponent();
            String modulus = mSecureManager.getModulus();
            mCallback.onAuthMessage(STATUS_POS_CALLING);
            DevicePos.getInstance().doAuthenticationAsync(modulus,
                    exponent,
                    AppUtils.getDeviceName(),
                    new APICallback() {
                        @Override
                        public void onFinish(Result result) {
                            switch (result.getCode()){
                                case Errors.ERROR_OK:
                                    AuthAsyncWrapper authWrapper = (AuthAsyncWrapper) result.getData();
                                    setRequestID(authWrapper.getRequestID());
                                    Log.w("@@@@@", "requestId from websocket: " + authWrapper.getRequestID());
                                    startChecker();
                                    break;
                                case Errors.ERROR_NET_SERVER_KO:
                                    PosResult posResult = (PosResult) result.getData();
                                    mCallback.onAuthFailed(PosUtils.getMessageFromErrorCode(posResult.getCode()),
                                            PosUtils.parsePosCode(posResult.getCode()));
                                    break;
                                case Errors.ERROR_NET_IO_IPOS:
                                    if (ConnectionManagerFactory.getConnectionManagerInstance().getState() == ConnectionManager.State.CONNECTED) {
                                        mCallback.onAuthFailed(result.getDescription(),
                                                result.getCode());
                                    } else {
                                        mCallback.onAuthFailed(Errors.ERROR_NET_IO_CHECK_WIRELESS,
                                                result.getCode());
                                    }
                                    break;
                                default:
                                    mCallback.onAuthFailed(result.getDescription(),
                                            result.getCode());
                                    break;
                            }
                        }
                    });
        } catch (Exception e) {
            Log.e(TAG, "", e);
            mCallback.onAuthFailed(PosUtils.getMessageFromErrorCode(SecureManager.OP_EXCEPTION), Errors.ERROR_SECURITY_INTERNAL);
        }
    }


    private void checkLoginEnabled(){

        // funzione disabilitata. Verranno mantenuti i dati di autenticazione di carta fisica.
        // la chiamata di checklogin verrà bypassata simulando che la risposta sia con loginEnabled=false
        // per riabilitare la funzione ripristinare le porzioni di codice commentate in basso

        mServiceMarketAPI.loginEnabled(mAuthData.getPhysicalUserCode(), new APICallbackV2<VirtualAuthEnabled>() {
            @Override
            public void onResult(VirtualAuthEnabled result) {
                if(result.getCode() == 0){
                    Log.d(TAG, "onResult: login enabled = " + result.isAuthRequired());
                    loginEnabled = result.isAuthRequired();
                    if(loginEnabled) {
                        // mostra UI richiesta credenziali login
                        mCallback.onAuthMessage(STATUS_REQUEST_LOGIN_CREDENTIAL);
                    } else {
                        //utilizzo i dati fisici come fossero virtuali per retrocompatibilità, bypassando la login
                        VirtualAuth fakeVirtualAuth = new VirtualAuth();
                        fakeVirtualAuth.setCode(0);
                        VirtualAuthData virtualAuthData = new VirtualAuthData();
                        virtualAuthData.setCvc(mAuthData.getPhysicalUserCode());
                        virtualAuthData.setToken(mAuthData.getPhysicalToken());
                        virtualAuthData.setTokenExpirationDate(mAuthData.getPhysicalTokenExpiryDate());
                        fakeVirtualAuth.setAuthenticationData(virtualAuthData);
                        onLoginSuccess(fakeVirtualAuth);
                    }


                } else {
                    Log.e(TAG, "onResult: " + result.logError());
                    mCallback.onAuthFailed(result.getDescription(), result.getCode());
                }
            }

            @Override
            public void onError(int code, String message, Exception e) {
                Log.d(TAG, "onError() called with: code = [" + code + "], message = [" + message + "], e = [" + e + "]");
                mCallback.onAuthFailed(message, code);
            }
        });
    }

    public void onCredentialAcquired(String username, String password) {
        doVirtualAuth(username, password);
    }

    private void doVirtualAuth(final String userName, String password){
        VirtualAuthRequest virtualAuthRequest = new VirtualAuthRequest();
        virtualAuthRequest.setUsername(userName);
        virtualAuthRequest.setPassword(password);
        virtualAuthRequest.setTokenCartaFisica(SecureManager.getInstance().decryptString(mAuthData.getPhysicalToken()));
        virtualAuthRequest.setUsercodeCartaFisica(mAuthData.getPhysicalUserCode());
        try {
            virtualAuthRequest.setEsponente(SecureManager.getInstance().getExponent());
            virtualAuthRequest.setModulo(SecureManager.getInstance().getModulus());
        } catch (Exception e) {
            Log.e(TAG, "Error getting exponent and modulus of public key", e);
            mCallback.onAuthFailed(PosUtils.getMessageFromErrorCode(SecureManager.OP_EXCEPTION), Errors.ERROR_SECURITY_INTERNAL);
        }

        mServiceMarketAPI.login(virtualAuthRequest
                , new APICallbackV2<VirtualAuth>() {
                    @Override
                    public void onResult(VirtualAuth result) {
                        if(result.getCode() == Errors.ERROR_OK) {
                            mAuthData.setUserName(userName);
                            onLoginSuccess(result);
                        } else {
                            String message = result.getDescription()
                                    + " (" + result.getCode() + ")";
                            Log.e(TAG, "onResult: login failed: " + message);
                            mCallback.onLoginFailed(message);
                        }
                    }

                    @Override
                    public void onError(int code, String message, Exception e) {
                        Log.e(TAG, "onError() called with: code = [" + code + "], message = [" + message + "], e = [" + e + "]");
                        String finalMessage = PosUtils.appendCodeToMessage(message, code);
                        mCallback.onLoginFailed(finalMessage);
                    }
                });
    }

    private void onLoginSuccess(VirtualAuth response){
        //finalizzo oggetto auth e salvo nello storage android con il token ancora cifrato
        VirtualAuthData data = response.getAuthenticationData();
        mAuthData.setTokenExpiryDate(data.getTokenExpirationDate());
        mAuthData.setToken(data.getToken());
        mAuthData.setUserCode(data.getCvc());
        AppUtils.setAuthData(mContext, mAuthData);

        Result result;
        if (mPosInfo == null) { //called from auth_getData()
            //prima di restituire l'oggetto auth ai client, decifro i token
            mAuthData.setToken(SecureManager.getInstance().decryptString(mAuthData.getToken()));
            mAuthData.setPhysicalToken(SecureManager.getInstance().decryptString(mAuthData.getPhysicalToken()));
            result = new Result(Errors.ERROR_OK, mAuthData);
            mCallback.onAuthResult(result, mJsCallback);
        } else {
            mPosInfo.setUserCode(mAuthData.getUserCode());
            mPosInfo.setPhysicalUserCode(mAuthData.getPhysicalUserCode());
            result = new Result(Errors.ERROR_OK, mPosInfo);
            mCallback.onAuthResult(result, mJsCallback);
        }
        mPosInfo = null;
        mJsCallback = null;
        ReportMonitor.sendReportDelayed(mAuthData.getUserCode());
        mCallback.onAuthMessage(Authenticator.STATUS_FINISH);
    }


    @Override
    protected int getType() {
        return TYPE_AUTH;
    }

    @Override
    protected void processEvent(AuthAsyncWrapper event) {
        String id = event.getRequestID();
        if (id.equals(getRequestID())) {
            stopChecker();
            try {
                PosResult<Auth> authResult = event.getResponse();
                if (authResult.getCode() == Errors.ERROR_OK) {

                    // old impl:
                    /*Auth auth = authResult.getData();
                    Log.e(TAG, "USER: " + auth.getUserCode() + "; TOKEN CRYPT: " + auth.getToken());
                    AppUtils.setAuthData(mContext, auth);
                    Result result;
                    if (mPosInfo == null) { //called from auth_getData()
                        auth.setToken(SecureManager.getInstance().decryptString(auth.getToken()));
                        result = new Result(Errors.ERROR_OK, auth);
                        mCallback.onAuthResult(result, mJsCallback);
                    } else {
                        mPosInfo.setUserCode(auth.getUserCode());
                        result = new Result(Errors.ERROR_OK, mPosInfo);
                        mCallback.onAuthResult(result, mJsCallback);
                    }
                    mPosInfo = null;
                    mJsCallback = null;
                    ReportMonitor.sendReportDelayed(auth.getUserCode());
                    mCallback.onAuthMessage(Authenticator.STATUS_FINISH);*/

                    //new impl
                    mAuthData.setPhysicalUserCode(authResult.getData().getUserCode());
                    mAuthData.setPhysicalToken(authResult.getData().getToken());
                    mAuthData.setPhysicalTokenExpiryDate(authResult.getData().getTokenExpiryDate());
                    Log.e(TAG, " PHYSICAL USER: " + mAuthData.getPhysicalUserCode() + "; PHYSICAL TOKEN CRYPT: " + mAuthData.getPhysicalToken());

                    checkLoginEnabled();

                } else {
                    mCallback.onAuthFailed(PosUtils.getMessageFromErrorCode(authResult.getCode()),
                            PosUtils.parsePosCode(authResult.getCode()));
                }
            } catch (Exception e) {
                Log.e(TAG, "processEvent: ", e);
                mCallback.onAuthFailed(PosUtils.getMessageFromErrorCode(Errors.ERROR_GENERIC), Errors.ERROR_GENERIC);
            }

        }
    }

    public void processAuthEvent(AuthAsyncWrapper wrapper){
        processEvent(wrapper);
    }

}
