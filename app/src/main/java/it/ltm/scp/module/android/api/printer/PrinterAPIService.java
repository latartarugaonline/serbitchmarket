package it.ltm.scp.module.android.api.printer;

import it.ltm.scp.module.android.model.devices.printer.gson.Document;
import it.ltm.scp.module.android.model.devices.printer.gson.PrinterInfo;
import it.ltm.scp.module.android.api.RestAPIModule;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * Created by HW64 on 22/08/2016.
 *
 * Interfaccia che definisce le firme dei metodi REST disponibili che vengono integrati con libreria Retrofit2
 * @see RestAPIModule
 */
public interface PrinterAPIService {

    @GET("api/devices/printer")
    Call<PrinterInfo> getPrinterInfo();

    @POST("api/devices/printer/line")
    Call<PrinterInfo> print(@Body Document document);

    @POST("api/devices/printer/line/reset")
    Call<Void> resetPrinterConf();
}
