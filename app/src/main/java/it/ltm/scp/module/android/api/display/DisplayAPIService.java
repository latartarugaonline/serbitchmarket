package it.ltm.scp.module.android.api.display;

import it.ltm.scp.module.android.model.devices.display.gson.DisplayContent;
import it.ltm.scp.module.android.model.devices.display.gson.Template;
import it.ltm.scp.module.android.model.devices.display.gson.TemplateList;

import it.ltm.scp.module.android.api.RestAPIModule;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * Created by HW64 on 09/09/2016.
 *
 * Interfaccia che definisce le firme dei metodi REST disponibili che vengono integrati con libreria Retrofit2
 * @see RestAPIModule
 */
public interface DisplayAPIService {

    @GET("api/devices/display/templates")
    Call<TemplateList> getTemplates();

    @GET("api/devices/display/template/{templateName}")
    Call<String> getTemplate(@Path("templateName") String templateName);

    @PUT("api/devices/display/template/{templateName}")
    Call<Void> sendTemplate(@Path("templateName") String templateName,@Body String htmlTemplate);

    @GET("api/devices/display/style")
    Call<String> getCss();

    @PUT("api/devices/display/style")
    Call<Void> setCss(@Body String css);

    @POST("api/devices/displayv2")
    Call<Void> showDisplay(@Body DisplayContent content);
}
