package it.ltm.scp.module.android.api;


import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import it.ltm.scp.module.android.api.pos.PosAPIService;
import it.ltm.scp.module.android.api.scanner.ScannerAPIService;
import it.ltm.scp.module.android.api.sm.PostRedirectInterceptor;
import it.ltm.scp.module.android.api.sm.ServiceMarketAPIService;
import it.ltm.scp.module.android.api.system.SystemAPIService;
import it.ltm.scp.module.android.devices.terminal.TerminalManagerFactory;
import it.ltm.scp.module.android.managers.secure.SecureManager;
import it.ltm.scp.module.android.model.Result;
import it.ltm.scp.module.android.utils.AppUtils;
import it.ltm.scp.module.android.utils.Constants;
import it.ltm.scp.module.android.api.display.DisplayAPIService;
import it.ltm.scp.module.android.api.printer.PrinterAPIService;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import it.ltm.scp.module.android.utils.Properties;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by HW64 on 22/08/2016.
 */
public class RestAPIModule {

    private static PrinterAPIService service = null;
    private static DisplayAPIService displayService = null;
    private static PosAPIService posService = null;
    private static PosAPIService posServiceCustomTimeout = null;
    private static SystemAPIService systemService = null;
    private static ServiceMarketAPIService smService = null;
    private static ScannerAPIService scannerService = null;

    public static PrinterAPIService getPrinterInstance(){
        if (service == null){
            service = new Retrofit.Builder()
                    .baseUrl(TerminalManagerFactory.get().getRestApiUrl())
                    .addConverterFactory(GsonConverterFactory.create(getGsonClient()))
                    .client(getOkHttpClientNoRetry())
                    .build().create(PrinterAPIService.class);
        }
        return service;
    }

    public static ScannerAPIService getScannerInstance(){
        if (scannerService == null){
            scannerService = new Retrofit.Builder()
                    .baseUrl(TerminalManagerFactory.get().getRestApiUrl())
                    .addConverterFactory(GsonConverterFactory.create(getGsonClient()))
                    .client(getOkHttpClientWithToken())
                    .build().create(ScannerAPIService.class);
        }
        return scannerService;
    }

    public static SystemAPIService getSystemInstance(){
        if (systemService == null){
            systemService = new Retrofit.Builder()
                    .baseUrl(TerminalManagerFactory.get().getRestApiUrl())
                    .addConverterFactory(GsonConverterFactory.create(getGsonClient()))
                    .client(getOkHttpClientWithToken())
                    .build().create(SystemAPIService.class);
        }
        return systemService;
    }

    public static DisplayAPIService getDisplayInstance(){
        if(displayService == null){
            displayService = new Retrofit.Builder()
                    .baseUrl(TerminalManagerFactory.get().getRestApiUrl())
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create(getGsonClientHtmlEscape()))
                    .client(getOkHttpClientWithToken())
                    .build().create(DisplayAPIService.class);
        }
        return displayService;
    }

    public static PosAPIService getPosInstance(){
        if(posService == null){
            posService = new Retrofit.Builder()
                    .baseUrl(TerminalManagerFactory.get().getRestApiUrl())
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create(getGsonClient()))
                    .client(getOkHttpClientWithToken())
                    .build().create(PosAPIService.class);
        }
        return posService;
    }

    public static PosAPIService getPosInstanceCustomTimeout(int timeoutSeconds){
        posServiceCustomTimeout = new Retrofit.Builder()
                    .baseUrl(TerminalManagerFactory.get().getRestApiUrl())
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create(getGsonClient()))
                    .client(getOkHttpClientWithTimeout(timeoutSeconds))
                    .build().create(PosAPIService.class);
        return posServiceCustomTimeout;
    }

    public static ServiceMarketAPIService getServiceMarket(){
        if(smService == null){
            smService = new Retrofit.Builder()
                    .baseUrl(Properties.get(Constants.PROP_URL_SERVICE_MARKET_BASE))
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create(getGsonClient()))
                    .client(getOkHttpClientServiceMarketRest())
                    .build().create(ServiceMarketAPIService.class);
        }
        return smService;
    }



    private static Gson getGsonClient(){
        return new GsonBuilder().setLenient().create();
    }

    private static Gson getGsonClientHtmlEscape(){
        return new GsonBuilder().setLenient().disableHtmlEscaping().create();
    }

    private static OkHttpClient getOkHttpClientWithTimeout(int seconds){
        return OkHttpWithTokenBuilder().readTimeout(seconds, TimeUnit.SECONDS).build();
    }

    private static OkHttpClient getOkHttpClientNoRetry(){
        return OkHttpWithTokenBuilder().retryOnConnectionFailure(false)
                .readTimeout(40, TimeUnit.SECONDS) //hotfix per timeout stampa IGP
                .build();
    }

    private static OkHttpClient getOkHttpClientWithToken(){
        return OkHttpWithTokenBuilder().build();
    }

    private static OkHttpClient getOkHttpClientServiceMarketRest(){
        return OkHttpBuilderWithDefaultTimeouts()
                .addInterceptor(new PostRedirectInterceptor())
                .build();
    }

    private static OkHttpClient.Builder OkHttpWithTokenBuilder(){
        return OkHttpBuilderWithDefaultTimeouts()
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request request = chain.request();
                        Request.Builder requestBuilder = request.newBuilder();
                        String timestamp = AppUtils.getUnixTimestamp();
                        String token = SecureManager.getInstance().generateToken(timestamp,
                                Properties.get(Constants.PROP_PHRASE));
                        Log.w("@@@@@", "INTERCEPTOR,  request URL: " + request.url().toString());
                        Log.w("@@@@@", "INTERCEPTOR,  timestamp: " + timestamp + " \n token: " + token);
                        requestBuilder.header(Constants.HEAD_TIME, timestamp);
                        requestBuilder.header(Constants.HEAD_TOK, token);

                        if(request.method().equals("POST")){
                            requestBuilder.header("Connection", "close");
                        }

                        Request newRequest = requestBuilder.build();
                        /*// codice compatibile con SSL non attivo (disabilitare)
                        Response response;
                        try {
                            response = chain.proceed(newRequest);
                        } catch (IOException e) {
                            Log.e("@@@@@", "intercept: " + newRequest.url().toString(), e);
                            if(e instanceof SSLHandshakeException){
                                //ripetere la richiesta con schema http
                                HttpUrl oldUrl = newRequest.url();
                                Log.e("####", "SSL Ex.: " + oldUrl);
                                HttpUrl newUrl = oldUrl.newBuilder().scheme("http").port(4040).build();
                                Log.e("####", "new URL: " + newUrl.toString());
                                Request httpSupportRequest = newRequest.newBuilder().url(newUrl).build();
                                return chain.proceed(httpSupportRequest);
                            }
                            // lasciare la gestione al client di tutte le altre eccezioni
//                            return chain.proceed(newRequest);
                            throw e;
                        }
                        return response;*/
                        return chain.proceed(newRequest);


                        /*
                        // ####### MOCK #######

                        String jsonMockResponse = "{\n" +
                                "  \"code\": \"903\",\n" +
                                "  \"message\": \"Errore Mock stampante\"\n" +
                                "}";

                        Response mockResponse = chain.proceed(newRequest);
                        mockResponse = mockResponse.newBuilder().code(503)
                                .message("MOCK: Errore inaspettato")
                                .body(ResponseBody.create(MediaType.parse("application/json"),
                                        jsonMockResponse.getBytes()))
                                .build();


                        return mockResponse;*/
                    }
                });
    }

    private static OkHttpClient.Builder OkHttpBuilderWithDefaultTimeouts(){
        return new OkHttpClient.Builder().readTimeout(30, TimeUnit.SECONDS);
    }

}
