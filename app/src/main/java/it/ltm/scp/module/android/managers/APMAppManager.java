package it.ltm.scp.module.android.managers;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import it.ltm.scp.module.android.devices.pos.PosUtils;
import it.ltm.scp.module.android.model.Error;
import it.ltm.scp.module.android.model.Result;
import it.ltm.scp.module.android.model.devices.pos.gson.Auth;
import it.ltm.scp.module.android.model.devices.pos.gson.PosInfo;
import it.ltm.scp.module.android.model.devices.pos.payment.gson.Payment;
import it.ltm.scp.module.android.model.devices.pos.payment.gson.PaymentType;
import it.ltm.scp.module.android.ui.MainActivity;
import it.ltm.scp.module.android.utils.AppUtils;
import it.ltm.scp.module.android.devices.pos.DevicePos;
import it.ltm.scp.module.android.utils.Errors;

public class APMAppManager
{
    private String jsCallback;
    private PaymentCallback listener;

    private static final String TAG = APMAppManager.class.getSimpleName();
    private static final String APM_PAYMENT_TYPE = "15";
    private static final String APM_PACKAGE_NAME = "it.lispay.apm";
    private static final String APM_ACTIVITY = "it.lispay.apm.MainActivity";
    private static final int APM_CODE = 9999;

    // APM COMMANDS
    public static final String APM_COMMAND_PAYMENT = "payment";
    public static final String APM_COMMAND_GET_LAST = "getLast";

    public APMAppManager(PaymentCallback listener)
    {
        this.listener = listener;
    }

    public String getJsCallback()
    {
        return jsCallback;
    }

    public static PaymentType paymentTypeFromJson(String iJson)
    {
        PaymentType payment = new Gson().fromJson(iJson, PaymentType.class);
        return payment;
    }

    public static boolean isAppCodePayment(PaymentType paymentType)
    {
        return paymentType != null && paymentType.getPaymentType().compareTo(APM_PAYMENT_TYPE) == 0;
    }

    public void launchAppAPM(final MainActivity iMainActivity, final PaymentType iPayment, final String iCmdType, String iJsCallback)
    {
        this.jsCallback = iJsCallback;
        Auth auth = AppUtils.getAuthData(iMainActivity.getApplicationContext());
        DevicePos.getInstance().getPosInfo(auth, new DevicePos.PosInfoCallback()
        {
            @Override
            public void onResult(Result result)
            {
                try {
                    JsonObject appAPMRequestData = new JsonObject();
                    appAPMRequestData.addProperty("cmdType", iCmdType); //TEST_WRONG_CMD

                    if(iCmdType.compareTo(APM_COMMAND_PAYMENT) == 0)
                    {
                        appAPMRequestData.addProperty("amount", iPayment.getAmount());
                        appAPMRequestData.addProperty("paymentType", iPayment.getPaymentType());
                        appAPMRequestData.addProperty("transactionTag", iPayment.getTransactionTag());
                        appAPMRequestData.addProperty("productCode", iPayment.getProductCode());

                        String termID00 = ((PosInfo)result.getData()).getTermID00();
                        appAPMRequestData.addProperty("terminalID", termID00);
                        appAPMRequestData.addProperty("currency", "EUR");
                    }
                    else if(iCmdType.compareTo(APM_COMMAND_GET_LAST) == 0)
                    {
                        // TO-DO...
                    }

                    Log.d(TAG, "EXTRAS FOR APP APM request = " + appAPMRequestData.toString());

                    Intent appAPMIntent = new Intent();
                    appAPMIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    appAPMIntent.setPackage(null);
                    appAPMIntent.setClassName(APM_PACKAGE_NAME, APM_ACTIVITY);
                    appAPMIntent.putExtra("request", String.valueOf(appAPMRequestData));
                    iMainActivity.startActivityForResult(appAPMIntent, APM_CODE);
                }
                catch(ActivityNotFoundException anfe){
                    Log.e(TAG, "AppCodeManager::launchAppCode::onResult: ActivityNotFoundException ", anfe);
                    listener.onPaymentResult(jsCallback, new Result(
                            Errors.ERROR_APM_FUNCTIONALITY_NOT_FOUND,
                            Errors.getMap().get(Errors.ERROR_APM_FUNCTIONALITY_NOT_FOUND),
                            null));
                }
                catch(Exception e){
                    Log.e(TAG, "AppCodeManager::launchAppCode::onResult: Exception ", e);
                    listener.onPaymentResult(jsCallback, new Result(
                            Errors.ERROR_APM_FUNCTIONALITY_NOT_AVAILABLE,
                            Errors.getMap().get(Errors.ERROR_APM_FUNCTIONALITY_NOT_AVAILABLE),
                            null));
                }
            }
            @Override
            public void onReauth(PosInfo posInfo)
            {
                listener.onPaymentResult(jsCallback, new Result(
                        Errors.ERROR_APM_POS_REAUTH,
                        Errors.getMap().get(Errors.ERROR_APM_POS_REAUTH),
                        null));
            }
            @Override
            public void onError(String message, int code)
            {
                listener.onPaymentResult(jsCallback, new Result(code, message, null));
            }
        });
    }

    public void onAPMResponse(Payment iPayment)
    {
        if(iPayment.getCode() != null)
        {
            int parsedCode = Integer.parseInt(iPayment.getCode());
            listener.onPaymentResult(jsCallback, new Result(PosUtils.parsePosCode(parsedCode),
                    PosUtils.getMessageFromErrorCode(parsedCode), null));
            return;
        }
        listener.onPaymentResult(jsCallback, new Result(Errors.ERROR_OK, iPayment));
    }

    public void onAPMResponseError(int result)
    {
        listener.onPaymentResult(jsCallback, new Result(
                Errors.ERROR_APM_ACTIVITY_RESULT_KO,
                Errors.getMap().get(Errors.ERROR_APM_ACTIVITY_RESULT_KO) + " - " + result,
                null));
    }
}
