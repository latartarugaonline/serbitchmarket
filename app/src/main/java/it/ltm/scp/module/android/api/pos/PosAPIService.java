package it.ltm.scp.module.android.api.pos;

import java.util.List;

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
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by HW64 on 20/09/2016.
 */
public interface PosAPIService {

    @POST("api/custom/lta/pos/auth")
    Call<PosResult<Auth>> doAuth(@Body AuthRequest authRequest);

    @Headers("X-RequestAsynch: true")
    @POST("api/custom/lta/pos/auth")
    Call<AuthAsyncWrapper> doAuthAsync(@Body AuthRequest authRequest);

    @Headers("X-RequestAsynch: true")
    @GET("api/requests/{requestId}")
    Call<AuthAsyncWrapper> getAuthStatus(@Path("requestId") String requestId);

    @GET("api/custom/lta/pos/info")
    Call<PosResult<PosInfo>> getPosInfo();

    @POST("api/custom/lta/pos/tsn")
    Call<PosResult<TsnData>> getTsn(@Body TsnRequest tsnRequest);

    @Headers("X-RequestAsynch: true")
    @POST("api/custom/lta/pos/tsn")
    Call<TsnAsyncWrapper> getTsnAsync(@Body TsnRequest tsnRequest);

    @Headers("X-RequestAsynch: true")
    @GET("api/requests/{requestId}")
    Call<TsnAsyncWrapper> getTsnAsyncStatus(@Path("requestId") String requestId);

    @Headers("X-RequestAsynch: true")
    @POST("api/custom/lta/pos/vas")
    Call<PromptResponseAsyncWrapper> getPromptAsync(@Body PromptRequest requestequest);

    @Headers("X-RequestAsynch: true")
    @GET("api/requests/{requestId}")
    Call<PromptResponseAsyncWrapper> getPromptAsyncStatus(@Path("requestId") String requestId);

    @POST("api/custom/lta/pos/vas")
    Call<PosResult<PromptResponseData>> getPrompt(@Body PromptRequest requestequest);


    // Printer api

    @POST("api/custom/lta/pos/printer/str")
    Call<PosResult<ResultMessage>> print(@Body PosPrint print);

    @POST("api/custom/lta/pos/printer/complex")
    Call<PosResult<ResultMessage>> printerComplex(@Body PosPrintComplex posPrintComplex);


    // Display api

    @POST("api/custom/lta/pos/display/str")
    Call<PosResult<ResultMessage>> display(@Body PosDisplay display);

    @POST("api/custom/lta/pos/display/clear")
    Call<PosResult<ResultMessage>> clearDisplay();


    // Buzzer api

    @POST("api/custom/lta/pos/buzzer")
    Call<PosResult<ResultMessage>> buzz(@Body PosBuzzer buzzer);


    // Payment api

    @GET("api/payments")
    Call<List<Payment>> getPaymentsList();

    @GET("api/payments/{paymentId}")
    Call<Payment> getPayment(@Path("paymentId")String paymentId);

    @Headers("X-RequestAsynch: true")
    @POST("api/payments/process/{paymentType}")
    Call<PaymentAsyncWrapper> processPaymentAsync(@Path("paymentType")String paymentType, @Body PaymentType payment);

    @Headers("X-RequestAsynch: true")
    @GET("api/requests/{requestId}")
    Call<PaymentAsyncWrapper> getPaymentStatus(@Path("requestId") String requestId);

    @POST("api/payments/process/{paymentType}")
    Call<Payment> processPayment(@Path("paymentType")String paymentType, @Body PaymentType payment);

}
