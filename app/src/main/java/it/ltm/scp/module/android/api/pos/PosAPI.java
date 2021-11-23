package it.ltm.scp.module.android.api.pos;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import it.ltm.scp.module.android.api.APICallback;
import it.ltm.scp.module.android.api.RestAPIModule;
import it.ltm.scp.module.android.model.Result;
import it.ltm.scp.module.android.model.devices.pos.buzzer.gson.PosBuzzer;
import it.ltm.scp.module.android.model.devices.pos.display.gson.PosDisplay;
import it.ltm.scp.module.android.model.devices.pos.gson.Auth;
import it.ltm.scp.module.android.model.devices.pos.gson.AuthRequest;
import it.ltm.scp.module.android.model.devices.pos.gson.PosInfo;
import it.ltm.scp.module.android.model.devices.pos.gson.PosResult;
import it.ltm.scp.module.android.model.devices.pos.gson.AuthAsyncWrapper;
import it.ltm.scp.module.android.model.devices.pos.gson.ResultMessage;
import it.ltm.scp.module.android.model.devices.pos.payment.gson.Payment;
import it.ltm.scp.module.android.model.devices.pos.payment.gson.PaymentAsyncWrapper;
import it.ltm.scp.module.android.model.devices.pos.payment.gson.PaymentType;
import it.ltm.scp.module.android.model.devices.pos.printer.gson.PosPrint;
import it.ltm.scp.module.android.model.devices.pos.printer.gson.PosPrintComplex;
import it.ltm.scp.module.android.model.devices.pos.prompt.PromptRequest;
import it.ltm.scp.module.android.model.devices.pos.prompt.PromptResponseAsyncWrapper;
import it.ltm.scp.module.android.model.devices.pos.prompt.PromptResponseData;
import it.ltm.scp.module.android.model.devices.pos.tsn.TsnAsyncWrapper;
import it.ltm.scp.module.android.model.devices.pos.tsn.TsnData;
import it.ltm.scp.module.android.model.devices.pos.tsn.TsnRequest;
import it.ltm.scp.module.android.utils.Errors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by HW64 on 15/09/2016.
 */
public class PosAPI extends PosAPIErrorHandler {

    private final String TAG = PosAPI.class.getSimpleName();

    public void doAuthentication(AuthRequest authRequest, final APICallback callback) {
        Call<PosResult<Auth>> doAuth = RestAPIModule.getPosInstance().doAuth(authRequest);
        doAuth.enqueue(new Callback<PosResult<Auth>>() {
            @Override
            public void onResponse(Call<PosResult<Auth>> call, Response<PosResult<Auth>> response) {
                if(response.isSuccessful()){
                    Auth auth = response.body().getData();
                    if(auth != null)
                        callback.onFinish(new Result(Errors.ERROR_OK, auth));
                    else{
                        int code = response.body().getCode();
                        callback.onFinish(new Result(code));
                    }
                } else {
                    try {
                        PosResult errorResult = new Gson().fromJson(response.errorBody().string(), PosResult.class);
                        callback.onFinish(new Result(errorResult.getCode()));
                    } catch (IOException e){
                        processError(callback, response);
                    }
                }
            }

            @Override
            public void onFailure(Call<PosResult<Auth>> call, Throwable t) {

                processError(callback, t);
            }
        });

    }

    public void doAuthenticationAsync(AuthRequest authRequest, final APICallback callback){
        Call<AuthAsyncWrapper> call = RestAPIModule.getPosInstance().doAuthAsync(authRequest);
        call.enqueue(new Callback<AuthAsyncWrapper>() {
            @Override
            public void onResponse(Call<AuthAsyncWrapper> call, Response<AuthAsyncWrapper> response) {
                if(response.isSuccessful()){
                    callback.onFinish(new Result(Errors.ERROR_OK,
                            response.body()));
                } else {
                    try {
                        String errorCode = response.errorBody().string();
                        Log.e(TAG, "onResponse: " + errorCode);
                        PosResult posResult = new Gson().fromJson(errorCode, PosResult.class);
                        callback.onFinish(new Result(Errors.ERROR_NET_SERVER_KO, posResult));
                    } catch (Exception e) {
                        Log.e(TAG, "onResponse: ", e);
                        callback.onFinish(new Result(Errors.ERROR_NET_UNABLE_READ_ERROR_BODY,
                                Errors.getMap().get(Errors.ERROR_NET_UNABLE_READ_ERROR_BODY),
                                null));
                    }
                }
            }

            @Override
            public void onFailure(Call<AuthAsyncWrapper> call, Throwable t) {

                processError(callback, t);
            }
        });
    }

    public void getAuthStatus(String requestId, final APICallback callback){
        Log.w("@@@@@", "requestId: " + requestId);
        Call<AuthAsyncWrapper> call = RestAPIModule.getPosInstance().getAuthStatus(requestId);
        call.enqueue(new Callback<AuthAsyncWrapper>() {
            @Override
            public void onResponse(Call<AuthAsyncWrapper> call, Response<AuthAsyncWrapper> response) {
                if(response.isSuccessful()){
                    callback.onFinish(new Result(Errors.ERROR_OK,
                            response.body()));
                } else {
                    try {
                        String errorCode = response.errorBody().string();
                        PosResult posResult = new Gson().fromJson(errorCode, PosResult.class);
                        callback.onFinish(new Result(Errors.ERROR_NET_SERVER_KO, posResult));
                    } catch (Exception e) {
                        Log.e(TAG, "", e);
                        callback.onFinish(new Result(Errors.ERROR_NET_UNABLE_READ_ERROR_BODY,
                                Errors.getMap().get(Errors.ERROR_NET_UNABLE_READ_ERROR_BODY),
                                null));
                    }
                }
            }

            @Override
            public void onFailure(Call<AuthAsyncWrapper> call, Throwable t) {

                processError(callback, t);
            }
        });
    }

    public Result doAuthenticationSync(final AuthRequest authRequest){
        AsyncTask<Void, Void, Result> mTask = new AsyncTask<Void, Void, Result>() {
            @Override
            protected Result doInBackground(Void... voids) {
                Call<PosResult<Auth>> doAuth = RestAPIModule.getPosInstance().doAuth(authRequest);
                try {
                    PosResult<Auth> posResult = doAuth.execute().body();
                    Auth auth = posResult.getData();
                    return new Result(Errors.ERROR_OK,
                            auth);
                } catch (IOException e) {
                    Log.e(TAG, "", e);
                    return new Result(Errors.ERROR_NET_IO_IPOS,
                            Errors.getMap().get(Errors.ERROR_NET_IO_IPOS),
                            e.getMessage());
                }
            }
        };
        try {
            return mTask.execute().get();
        } catch (InterruptedException | ExecutionException e) {
            Log.e(TAG, "", e);
            return new Result(Errors.ERROR_NET_IO_IPOS,
                    Errors.getMap().get(Errors.ERROR_NET_IO_IPOS),
                    e.getMessage());
        }
    }

    public void getPosInfo(final APICallback callback){
        Call<PosResult<PosInfo>> getPosInfo = RestAPIModule.getPosInstance().getPosInfo();
        getPosInfo.enqueue(new Callback<PosResult<PosInfo>>() {
            @Override
            public void onResponse(Call<PosResult<PosInfo>> call, Response<PosResult<PosInfo>> response) {
                if(response.isSuccessful()){
                    PosInfo posInfo = response.body().getData();
                    posInfo.setPhysicalUserCode(posInfo.getUserCode());
                    if(posInfo != null){
                        callback.onFinish(
                                new Result(Errors.ERROR_OK, posInfo)
                        );
                    } else {
                        processError(callback, response.raw().toString());
                    }
                } else {
                    processPosError(callback, response);
                }
            }

            @Override
            public void onFailure(Call<PosResult<PosInfo>> call, Throwable t) {

                processError(callback, t);
            }
        });
    }

    public Result getPosInfoSync(){
        AsyncTask<Void, Void, Result> mTask = new AsyncTask<Void, Void, Result>() {
            @Override
            protected Result doInBackground(Void... voids) {
                Call<PosResult<PosInfo>> getPosInfo = RestAPIModule.getPosInstance().getPosInfo();
                try {
                    PosResult<PosInfo> posResult = getPosInfo.execute().body();
                    PosInfo posInfo = posResult.getData();
                    return new Result(Errors.ERROR_OK,
                            posInfo);
                } catch (IOException e) {
                    Log.e(TAG, "", e);
                    return new Result(Errors.ERROR_NET_IO_IPOS,
                            Errors.getMap().get(Errors.ERROR_NET_IO_IPOS),
                            e.getMessage());
                }
            }
        };
        try {
            return mTask.execute().get();
        } catch (InterruptedException | ExecutionException e) {
            Log.e(TAG, "", e);
            return new Result(Errors.ERROR_NET_IO_IPOS,
                    Errors.getMap().get(Errors.ERROR_NET_IO_IPOS),
                    e.getMessage());
        }
    }

    public void print(PosPrint posPrint, final APICallback callback){
        Call<PosResult<ResultMessage>> print = RestAPIModule.getPosInstance().print(posPrint);
        print.enqueue(new Callback<PosResult<ResultMessage>>() {
            @Override
            public void onResponse(Call<PosResult<ResultMessage>> call, Response<PosResult<ResultMessage>> response) {
                if(response.isSuccessful()){
                    PosResult result = response.body();
                    if(result.getData() != null){
                        callback.onFinish(new Result(Errors.ERROR_OK));
                    } else {
                        processError(callback, response.raw().toString());
                    }
                } else {
                    processError(callback, response);
                }
            }

            @Override
            public void onFailure(Call<PosResult<ResultMessage>> call, Throwable t) {

                processError(callback, t);
            }
        });
    }

    public void printComplex(PosPrintComplex posPrintComplex, final APICallback callback){
        Call<PosResult<ResultMessage>> call = RestAPIModule.getPosInstance().printerComplex(posPrintComplex);
        call.enqueue(new Callback<PosResult<ResultMessage>>() {
            @Override
            public void onResponse(Call<PosResult<ResultMessage>> call, Response<PosResult<ResultMessage>> response) {
                if(response.isSuccessful()){
                    PosResult result = response.body();
                    if(result.getData() != null){
                        callback.onFinish(new Result(Errors.ERROR_OK));
                    } else {
                        processError(callback, response.raw().toString());
                    }
                } else {
                    processError(callback, response);
                }
            }

            @Override
            public void onFailure(Call<PosResult<ResultMessage>> call, Throwable t) {

                processError(callback, t);
            }
        });
    }

    public void display(PosDisplay posDisplay, final APICallback callback){
        Call<PosResult<ResultMessage>> display = RestAPIModule.getPosInstance().display(posDisplay);
        display.enqueue(new Callback<PosResult<ResultMessage>>() {
            @Override
            public void onResponse(Call<PosResult<ResultMessage>> call, Response<PosResult<ResultMessage>> response) {
                if(response.isSuccessful()){
                    PosResult result = response.body();
                    if(result.getData() != null){
                        callback.onFinish(new Result(Errors.ERROR_OK));
                    } else {
                        processError(callback, response.raw().toString());
                    }
                } else {
                    processError(callback, response);
                }
            }

            @Override
            public void onFailure(Call<PosResult<ResultMessage>> call, Throwable t) {


                processError(callback, t);
            }
        });
    }

    public void clearDisplay(final APICallback callback){
        Call<PosResult<ResultMessage>> clear = RestAPIModule.getPosInstance().clearDisplay();
        clear.enqueue(new Callback<PosResult<ResultMessage>>() {
            @Override
            public void onResponse(Call<PosResult<ResultMessage>> call, Response<PosResult<ResultMessage>> response) {
                if(response.isSuccessful()){
                    PosResult result = response.body();
                    if(result.getData() != null){
                        callback.onFinish(new Result(Errors.ERROR_OK));
                    } else {
                        processError(callback, response.raw().toString());
                    }
                } else {
                    processError(callback, response);
                }
            }

            @Override
            public void onFailure(Call<PosResult<ResultMessage>> call, Throwable t) {

                processError(callback, t);

            }
        });
    }

    public void buzz(PosBuzzer posBuzzer, final APICallback callback){
        Call<PosResult<ResultMessage>> buzz = RestAPIModule.getPosInstance().buzz(posBuzzer);
        buzz.enqueue(new Callback<PosResult<ResultMessage>>() {
            @Override
            public void onResponse(Call<PosResult<ResultMessage>> call, Response<PosResult<ResultMessage>> response) {
                if(response.isSuccessful()){
                        callback.onFinish(new Result(Errors.ERROR_OK));
                } else {
                    processError(callback, response);
                }
            }

            @Override
            public void onFailure(Call<PosResult<ResultMessage>> call, Throwable t) {

                processError(callback, t);

            }
        });
    }



    public void getPayments(final APICallback callback){
        Call<List<Payment>> paymentListCall = RestAPIModule.getPosInstance().getPaymentsList();
        paymentListCall.enqueue(new Callback<List<Payment>>() {
            @Override
            public void onResponse(Call<List<Payment>> call, Response<List<Payment>> response) {
                if(response.isSuccessful()){
                    callback.onFinish(
                            new Result(Errors.ERROR_OK, response.body())
                    );
                } else {
                    processError(callback, response);
                }
            }

            @Override
            public void onFailure(Call<List<Payment>> call, Throwable t) {

                processError(callback, t);

            }
        });
    }

    public void getPayment(String paymentId, final APICallback callback){
        Call<Payment> paymentCall = RestAPIModule.getPosInstance().getPayment(paymentId);
        paymentCall.enqueue(new Callback<Payment>() {
            @Override
            public void onResponse(Call<Payment> call, Response<Payment> response) {
                if(response.isSuccessful()){
                    callback.onFinish(new Result(Errors.ERROR_OK, response.body()));
                } else {
                    processError(callback, response);
                }
            }

            @Override
            public void onFailure(Call<Payment> call, Throwable t) {

                processError(callback, t);

            }
        });
    }

    public void processPayment(String paymentType, PaymentType payment, final APICallback callback){
        Call<Payment> paymentCall = RestAPIModule.getPosInstance().processPayment(paymentType, payment);
        paymentCall.enqueue(new Callback<Payment>() {
            @Override
            public void onResponse(Call<Payment> call, Response<Payment> response) {
                if(response.isSuccessful()){
                    callback.onFinish(new Result(Errors.ERROR_OK, response.body()));
                } else {
                    processError(callback, response);
                }
            }

            @Override
            public void onFailure(Call<Payment> call, Throwable t) {

                processError(callback, t);

            }
        });
    }

    public void processPaymentAsync(String paymentType, PaymentType payment, final APICallback callback){
        Call<PaymentAsyncWrapper> payCall = RestAPIModule.getPosInstance().processPaymentAsync(paymentType, payment);
        payCall.enqueue(new Callback<PaymentAsyncWrapper>() {
            @Override
            public void onResponse(Call<PaymentAsyncWrapper> call, Response<PaymentAsyncWrapper> response) {
                if(response.isSuccessful()){
                    callback.onFinish(new Result(Errors.ERROR_OK,
                            response.body()));
                } else {
                    try {
                        String errorCode = response.errorBody().string();
                        PosResult posResult = new Gson().fromJson(errorCode, PosResult.class);
                        callback.onFinish(new Result(Errors.ERROR_NET_SERVER_KO, posResult));
                    } catch (Exception e) {
                        Log.e(TAG, "", e);
                        callback.onFinish(new Result(Errors.ERROR_NET_UNABLE_READ_ERROR_BODY,
                                Errors.getMap().get(Errors.ERROR_NET_UNABLE_READ_ERROR_BODY)));
                    }
                }
            }

            @Override
            public void onFailure(Call<PaymentAsyncWrapper> call, Throwable t) {

                processError(callback, t);
            }
        });
    }


    public void getPaymentStatus(String requestId, final APICallback callback){
        Call<PaymentAsyncWrapper> call = RestAPIModule.getPosInstance().getPaymentStatus(requestId);
        call.enqueue(new Callback<PaymentAsyncWrapper>() {
            @Override
            public void onResponse(Call<PaymentAsyncWrapper> call, Response<PaymentAsyncWrapper> response) {
                if(response.isSuccessful()){
                    callback.onFinish(new Result(Errors.ERROR_OK,
                            response.body()));
                } else {
                    try {
                        String errorCode = response.errorBody().string();
                        PosResult posResult = new Gson().fromJson(errorCode, PosResult.class);
                        callback.onFinish(new Result(Errors.ERROR_NET_SERVER_KO, posResult));
                    } catch (Exception e) {
                        Log.e(TAG, "", e);
                        callback.onFinish(new Result(Errors.ERROR_NET_UNABLE_READ_ERROR_BODY,
                                Errors.getMap().get(Errors.ERROR_NET_UNABLE_READ_ERROR_BODY)));
                    }
                }
            }

            @Override
            public void onFailure(Call<PaymentAsyncWrapper> call, Throwable t) {

                processError(callback, t);
            }
        });
    }

    public void getPrompt(PromptRequest request, final APICallback callback){
        Call<PosResult<PromptResponseData>> call = RestAPIModule.getPosInstance().getPrompt(request);
        call.enqueue(new Callback<PosResult<PromptResponseData>>() {
            @Override
            public void onResponse(Call<PosResult<PromptResponseData>> call, Response<PosResult<PromptResponseData>> response) {
                processPosResponse(callback, response);
            }

            @Override
            public void onFailure(Call<PosResult<PromptResponseData>> call, Throwable t) {
                Log.e(TAG, "onFailure: " + call.request().url().toString(), t);
                processError(callback, t);
            }
        });
    }

    public void getPromptCustomTimeout(PromptRequest request, int timeoutSeconds, final APICallback callback){
        PosAPIService posAPIService = RestAPIModule.getPosInstanceCustomTimeout(timeoutSeconds);

        Call<PosResult<PromptResponseData>> call = posAPIService.getPrompt(request);

        call.enqueue(new Callback<PosResult<PromptResponseData>>() {
            @Override
            public void onResponse(Call<PosResult<PromptResponseData>> call, Response<PosResult<PromptResponseData>> response) {
                processPosResponse(callback, response);
            }

            @Override
            public void onFailure(Call<PosResult<PromptResponseData>> call, Throwable t) {
                Log.e(TAG, "onFailure: " + call.request().url().toString(), t);
                processError(callback, t);
            }
        });
    }


    public void getPromptAsync(PromptRequest request, final APICallback callback){
        Call<PromptResponseAsyncWrapper> call = RestAPIModule.getPosInstance().getPromptAsync(request);
        call.enqueue(new Callback<PromptResponseAsyncWrapper>() {
            @Override
            public void onResponse(Call<PromptResponseAsyncWrapper> call, Response<PromptResponseAsyncWrapper> response) {
                processPosResponse(callback, response);
            }

            @Override
            public void onFailure(Call<PromptResponseAsyncWrapper> call, Throwable t) {
                Log.e(TAG, "onFailure: " + call.request().url().toString(), t);
                processError(callback, t);
            }
        });
    }

    public void getPromptAsyncStatus(String requestId, final APICallback callback){
        Call<PromptResponseAsyncWrapper> call = RestAPIModule.getPosInstance().getPromptAsyncStatus(requestId);
        call.enqueue(new Callback<PromptResponseAsyncWrapper>() {
            @Override
            public void onResponse(Call<PromptResponseAsyncWrapper> call, Response<PromptResponseAsyncWrapper> response) {
                processPosResponse(callback, response);
            }

            @Override
            public void onFailure(Call<PromptResponseAsyncWrapper> call, Throwable t) {
                processPosException(call, t, callback);
            }
        });
    }

    public void getTsn(int timeout, String displayMessage, String readType, final APICallback callback){
        TsnRequest tsnRequest = new TsnRequest(timeout, readType, displayMessage);
        Call<PosResult<TsnData>> getTsnCall = RestAPIModule.getPosInstance().getTsn(tsnRequest);
        getTsnCall.enqueue(new Callback<PosResult<TsnData>>() {
            @Override
            public void onResponse(Call<PosResult<TsnData>> call, Response<PosResult<TsnData>> response) {
                try {
                    if(response.isSuccessful()){
                        PosResult<TsnData> result = response.body();
                        TsnData tsnData = result.getData();
                        callback.onFinish(new Result(Errors.ERROR_OK, tsnData));
                    } else {
                        processPosError(callback, response);
                    }
                } catch (Exception e){
                    Log.e(TAG, "onResponse: " + call.request().url().toString(), e);
                    callback.onFinish(new Result(Errors.ERROR_NET_UNABLE_READ_ERROR_BODY,
                            Errors.getMap().get(Errors.ERROR_NET_UNABLE_READ_ERROR_BODY),
                            null));
                }
            }

            @Override
            public void onFailure(Call<PosResult<TsnData>> call, Throwable t) {
                Log.e(TAG, "onFailure: " + call.request().url().toString(), t);
                processError(callback, t);
            }
        });
    }

    public void getTsnAsync(int timeout, String displayMessage, final String readType, final APICallback callback){
        TsnRequest tsnRequest = new TsnRequest(timeout, readType, displayMessage);
        Call<TsnAsyncWrapper> tsnAsyncWrapperCall = RestAPIModule.getPosInstance().getTsnAsync(tsnRequest);
        tsnAsyncWrapperCall.enqueue(new Callback<TsnAsyncWrapper>() {
            @Override
            public void onResponse(Call<TsnAsyncWrapper> call, Response<TsnAsyncWrapper> response) {
                processPosResponse(callback, response);
            }

            @Override
            public void onFailure(Call<TsnAsyncWrapper> call, Throwable t) {
                Log.e(TAG, "onFailure: " + call.request().url().toString(), t);
                processError(callback, t);
            }
        });
    }

    public void getTsnAsyncStatus(String requestId, final APICallback callback){
        Call<TsnAsyncWrapper> call = RestAPIModule.getPosInstance().getTsnAsyncStatus(requestId);
        call.enqueue(new Callback<TsnAsyncWrapper>() {
            @Override
            public void onResponse(Call<TsnAsyncWrapper> call, Response<TsnAsyncWrapper> response) {
                processPosResponse(callback, response);
            }

            @Override
            public void onFailure(Call<TsnAsyncWrapper> call, Throwable t) {
                processPosException(call, t, callback);
            }
        });
    }

}
