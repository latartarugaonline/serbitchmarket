package it.ltm.scp.module.android.api.sm;

import it.ltm.scp.module.android.model.sm.gson.ServiceMarketResult;
import it.ltm.scp.module.android.model.sm.gson.VirtualAuth;
import it.ltm.scp.module.android.model.sm.gson.VirtualAuthEnabled;
import it.ltm.scp.module.android.model.sm.gson.VirtualAuthEnabledRequest;
import it.ltm.scp.module.android.model.sm.gson.VirtualAuthRequest;
import it.ltm.scp.module.android.monitor.model.Monitor;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by HW64 on 19/10/2016.
 */
public interface ServiceMarketAPIService {

    @GET("sm-rs/service/barcode/{inputBarcode}/{userCode}")
    Call<ServiceMarketResult> resolveBarcode(@Path("inputBarcode")String inputBarcode, @Path("userCode")String userCode);

    @FormUrlEncoded
    @POST
    Call<String> redirectLisData(@Field("input") String jsonData);

    @POST("MARKET_PLACE_MONITOR_WEB/rest/postMessage")
    Call<String> sendLog(@Body Monitor monitor);

    @POST("sm-rs/authservice/authentication")
    Call<VirtualAuth> login(@Body VirtualAuthRequest request);

    @POST("sm-rs/checkauthservice/checkauthentication")
    Call<VirtualAuthEnabled> loginEnabled(@Body VirtualAuthEnabledRequest request);

}
