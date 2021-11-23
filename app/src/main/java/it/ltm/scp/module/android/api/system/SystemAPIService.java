package it.ltm.scp.module.android.api.system;

import it.ltm.scp.module.android.model.devices.system.gson.SystemInfo;
import it.ltm.scp.module.android.model.devices.system.gson.update.Update;
import it.ltm.scp.module.android.model.devices.system.gson.update.UpdateConfig;
import it.ltm.scp.module.android.model.devices.system.gson.update.UpdateRepository;
import it.ltm.scp.module.android.model.devices.system.gson.update.UpdateStatus;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;

/**
 * Created by HW64 on 18/10/2016.
 */
public interface SystemAPIService {

    @GET("api/system")
    Call<SystemInfo> getSystemInfo();

    @GET("api/system/update")
    Call<Update> checkForUpdate();

    @POST("api/system/update")
    Call<UpdateStatus> doUpdate(@Body UpdateRepository repository);

    @GET("api/system/update/config")
    Call<UpdateConfig> getUpdateConfig();

    @PUT("api/system/update/config")
    Call<Void> putUpdateConfig(@Body UpdateConfig config);
}
