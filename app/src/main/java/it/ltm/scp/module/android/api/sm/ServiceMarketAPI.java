package it.ltm.scp.module.android.api.sm;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.io.IOException;

import javax.net.ssl.SSLException;
import javax.net.ssl.SSLPeerUnverifiedException;

import it.ltm.scp.module.android.api.APICallback;
import it.ltm.scp.module.android.api.APICallbackV2;
import it.ltm.scp.module.android.api.RestAPIModule;
import it.ltm.scp.module.android.model.Result;
import it.ltm.scp.module.android.model.sm.gson.ServiceMarketResult;
import it.ltm.scp.module.android.model.sm.gson.VirtualAuth;
import it.ltm.scp.module.android.model.sm.gson.VirtualAuthData;
import it.ltm.scp.module.android.model.sm.gson.VirtualAuthEnabled;
import it.ltm.scp.module.android.model.sm.gson.VirtualAuthEnabledRequest;
import it.ltm.scp.module.android.model.sm.gson.VirtualAuthRequest;
import it.ltm.scp.module.android.monitor.model.Monitor;
import it.ltm.scp.module.android.utils.AppUtils;
import it.ltm.scp.module.android.utils.Constants;
import it.ltm.scp.module.android.utils.Errors;
import it.ltm.scp.module.android.utils.Properties;
import okhttp3.CertificatePinner;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by HW64 on 19/10/2016.
 */
public class ServiceMarketAPI {

    //TODO singleton? in caso doppia lettura barcode

    private final String TAG = ServiceMarketAPI.class.getSimpleName();

    public void resolveBarcode(String inputBarcode, String userCode, final APICallback apiCallback){
        Call<ServiceMarketResult> resolveBarcodeCall = RestAPIModule.getServiceMarket().resolveBarcode(inputBarcode, userCode);
        resolveBarcodeCall.enqueue(new Callback<ServiceMarketResult>() {
            @Override
            public void onResponse(Call<ServiceMarketResult> call, Response<ServiceMarketResult> response) {
                if(response.isSuccessful()){
                    processResponse(response.body(), apiCallback);
                } else {
                    processResponseKO(response, apiCallback);
                }
            }

            @Override
            public void onFailure(Call<ServiceMarketResult> call, Throwable t) {
                processFailure(t, apiCallback);
            }
        });
    }

    public void login(final VirtualAuthRequest request, final APICallbackV2<VirtualAuth> callback){
        Call<VirtualAuth> login = RestAPIModule.getServiceMarket().login(request);
        login.enqueue(new Callback<VirtualAuth>() {
            @Override
            public void onResponse(Call<VirtualAuth> call, Response<VirtualAuth> response) {
                if(response.isSuccessful()){
                    callback.onResult(response.body());
                } else {
                    processResponseKO(response, callback);
                }
            }

            @Override
            public void onFailure(Call<VirtualAuth> call, Throwable t) {
                Log.e(TAG, "onFailure() called with: call = [" + call.request().url() + "]", t);
                processFailure(t, callback);
            }
        });
    }

    public void loginEnabled(String usercode, final APICallbackV2<VirtualAuthEnabled> callback){
        //mock
        /*boolean enabled = true;
        VirtualAuthEnabled virtualAuthEnabled = new VirtualAuthEnabled();
        virtualAuthEnabled.setCode(0);
        virtualAuthEnabled.setAuthRequired(false);

        callback.onResult(virtualAuthEnabled);*/


        VirtualAuthEnabledRequest request = new VirtualAuthEnabledRequest(usercode);
        Call<VirtualAuthEnabled> call = RestAPIModule.getServiceMarket().loginEnabled(request);
        call.enqueue(new Callback<VirtualAuthEnabled>() {
            @Override
            public void onResponse(Call<VirtualAuthEnabled> call, Response<VirtualAuthEnabled> response) {
                if(response.isSuccessful()){
                    callback.onResult(response.body());
                } else {
                    processResponseKO(response, callback);
                }
            }

            @Override
            public void onFailure(Call<VirtualAuthEnabled> call, Throwable t) {
                Log.e(TAG, "onFailure() called with: call = [" + call.request().url() + "]", t);
                processFailure(t, callback);
            }
        });

    }

    public Call<String> getSendLogCall(Monitor monitor){
        return RestAPIModule.getServiceMarket().sendLog(monitor);
    }

    /*
     * Con Retrofit2 non è possibile costruire l'URL
     * dinamicamente caricandolo da file di properties.
     *
     * Es:
     * @GET("{path}")
     * Call<ResponseBody> getMainPage(@Path("path")String path);
     *
     * passando un path completo (/esempio/pagina.jsp) non viene fatto l'encoding dei caratteri '/'
     * compromettendo l'URL finale.
     *
     * facendo invece una struttura di tipo:
     *
     * @GET("{service}/{mainPage}")
     * Call<ResponseBody> getMainPage(@Path("service")String service,
     *                                  @Path("mainPage")String mainPage,
     *                                  @Query("terminalType")String terminalType);
     *
     * se cambia la struttura dell'URL non è possibile richiamare questa funzione.
     */
    public void getMainPage(String url, final APICallback apiCallback){
        Log.d(TAG, "getMainPage() called with: path = [" + url + "]");
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        getSmHttpClient().newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                Log.w(TAG, "onFailure: ", e);
                apiCallback.onFinish(new Result(Errors.ERROR_NET_IO_IPOS,
                        Errors.getMap().get(Errors.ERROR_NET_IO_IPOS),
                        e.getClass().getSimpleName().replace("Exception", "")
                        ));
            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) {
                try {
                    if(response.isSuccessful()){
                        String html = response.body().string();
                        apiCallback.onFinish(new Result(Errors.ERROR_OK,
                                html));
                    } else {
                        String errorMessage = response.message();
                        apiCallback.onFinish(new Result(Errors.ERROR_NET_SERVER_KO,
                                Errors.getMap().get(Errors.ERROR_NET_SERVER_KO),
                                errorMessage));
                    }
                } catch (IOException e){
                    processBodyFailure(e, apiCallback);
                }

            }
        });
    }

    public void checkCert(final APICallback apiCallback){
        String url = Properties.get(Constants.PROP_URL_SERVICE_MARKET_BASE)
                +  Properties.get(Constants.PROP_URL_SERVICE_MARKET_PATH_CTX)
                + Properties.get(Constants.PROP_URL_SERVICE_MARKET_PATH_PING);
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        getSmHttpClient().newCall(request).enqueue(new okhttp3.Callback() {

            Handler mainThreadHandler = new Handler(Looper.getMainLooper());

            @Override
            public void onFailure(okhttp3.Call call, final IOException e) {
                Log.e(TAG, "onFailure: ", e);
                mainThreadHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        String reason = e.getClass().getSimpleName().replace("Exception", "");
                        if (e instanceof SSLPeerUnverifiedException) {
                            Log.e(TAG, "onFailure: ", e);
                            apiCallback.onFinish(new Result(Errors.ERROR_SECURITY_CERTIFICATE_PINNING,
                                    Errors.getMap().get(Errors.ERROR_SECURITY_CERTIFICATE_GENERIC),
                                    reason));
                            return;
                        }
                        if(e instanceof SSLException){
                            Log.e(TAG, "onFailure: ", e);
                            apiCallback.onFinish(new Result(Errors.ERROR_SECURITY_CERTIFICATE_GENERIC,
                                    Errors.getMap().get(Errors.ERROR_SECURITY_CERTIFICATE_GENERIC),
                                    reason));
                            return;
                        }
                        apiCallback.onFinish(new Result(Errors.ERROR_NET_IO,
                                Errors.getMap().get(Errors.ERROR_NET_IO),
                                reason)
                        );
                    }
                });
            }

            @Override
            public void onResponse(okhttp3.Call call, final okhttp3.Response response) {
                try {
                    if(response.isSuccessful()){
                        mainThreadHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                apiCallback.onFinish(new Result(Errors.ERROR_OK));
                            }
                        });
                    } else {
                        final String errorMessage = Errors.getMap().get(Errors.ERROR_NET_IO)
                                + "\n \n ("
                                +   response.message() + ", Codice: " + response.code()
                                + ", " + AppUtils.formatUrlResource(call.request().url().toString())
                                + " - " + AppUtils.getCurrentDate()
                                + ")";
                        mainThreadHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                apiCallback.onFinish(new Result(Errors.ERROR_NET_SERVER_KO,
                                        errorMessage, //TODO rivedere messaggio
                                        errorMessage));
                            }
                        });

                    }
                } finally {
                    response.close();
                }
            }
        });
    }

    private void processBodyFailure(IOException e, APICallback apiCallback) {
        apiCallback.onFinish(new Result(
                Errors.ERROR_NET_UNABLE_READ_ERROR_BODY,
                Errors.getMap().get(Errors.ERROR_NET_UNABLE_READ_ERROR_BODY),
                e.getMessage(),
                null
        ));
    }


    private void processFailure(Throwable t, APICallback apiCallback) {
        apiCallback.onFinish(new Result(
                Errors.ERROR_NET_IO,
                Errors.getMap().get(Errors.ERROR_NET_IO),
                t.toString()
        ));
    }

    private void processFailure(Throwable t, APICallbackV2 apiCallbackV2){
        apiCallbackV2.onError(
                Errors.ERROR_NET_IO,
                Errors.getMap().get(Errors.ERROR_NET_IO),
                new Exception(t)
        );
    }

    private void processResponseKO(Response<ServiceMarketResult> response, APICallback apiCallback) {
        try {
            String errorMessage = response.errorBody().string();
            Log.e(TAG, "processResponseKO: body: " + errorMessage);
            apiCallback.onFinish(new Result(Errors.ERROR_NET_SM_SERVER_KO,
                    Errors.getMap().get(Errors.ERROR_NET_SM_SERVER_KO),
                    errorMessage));
        } catch (IOException e) {
            Log.e(TAG, "", e);
            apiCallback.onFinish(new Result(Errors.ERROR_NET_UNABLE_READ_ERROR_BODY,
                    Errors.getMap().get(Errors.ERROR_NET_UNABLE_READ_ERROR_BODY),
                    null));
        }
    }

    private void processResponseKO(Response response, APICallbackV2 apiCallbackV2) {
        try {
            String errorMessage = response.errorBody().string();
            Log.e(TAG, "processResponseKO: body: " + errorMessage);
            apiCallbackV2.onError(Errors.ERROR_NET_SM_SERVER_KO,
                    Errors.getMap().get(Errors.ERROR_NET_SM_SERVER_KO)
                    + " - " + response.code(),
                    null);
        } catch (IOException e) {
            Log.e(TAG, "", e);
            apiCallbackV2.onError(Errors.ERROR_NET_UNABLE_READ_ERROR_BODY,
                    Errors.getMap().get(Errors.ERROR_NET_UNABLE_READ_ERROR_BODY),
                    null);
        }
    }

    private void processResponse(ServiceMarketResult body, APICallback apiCallback) {
        switch (body.getCode()){
            case 0:
                apiCallback.onFinish(new Result(Errors.ERROR_OK, body));
                break;
            default:
                apiCallback.onFinish(new Result(Errors.ERROR_NET_SERVER_RESPONSE,
                        Errors.getMap().get(Errors.ERROR_NET_SERVER_RESPONSE),
                        body.getCode() + ": " +body.getDescription()));

        }
    }

    private OkHttpClient getSmHttpClient(){
        CertificatePinner pinner = Pinner.getPinner();
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
//        builder = configureToIgnoreCertificate(builder);
        if (pinner != null) {
            builder.certificatePinner(pinner);
        }
        return builder.build();
    }



    //TEST ONLY

    //Setting testMode configuration. If set as testMode, the connection will skip certification check
    /*public static OkHttpClient.Builder configureToIgnoreCertificate(OkHttpClient.Builder builder) {

        try {

            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[] {
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType)
                                throws CertificateException {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType)
                                throws CertificateException {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            builder.sslSocketFactory(sslSocketFactory, (X509TrustManager)trustAllCerts[0]);
            builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
        } catch (Exception e) {
        }
        return builder;
    }*/

}
