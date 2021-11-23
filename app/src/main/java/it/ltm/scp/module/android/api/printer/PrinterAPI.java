package it.ltm.scp.module.android.api.printer;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;

import it.ltm.scp.module.android.api.APICallback;
import it.ltm.scp.module.android.api.APICallbackV2;
import it.ltm.scp.module.android.api.APIErrorHandler;
import it.ltm.scp.module.android.devices.pos.PosUtils;
import it.ltm.scp.module.android.devices.printer.DocumentBuilderImpl;
import it.ltm.scp.module.android.model.Error;
import it.ltm.scp.module.android.utils.AppUtils;
import it.ltm.scp.module.android.utils.Errors;
import it.ltm.scp.module.android.api.RestAPIModule;
import it.ltm.scp.module.android.model.Result;
import it.ltm.scp.module.android.model.devices.printer.gson.Document;
import it.ltm.scp.module.android.model.devices.printer.gson.PrinterInfo;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import it.ltm.scp.module.android.model.devices.printer.gson.status.Status;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by HW64 on 26/08/2016.
 *
 * Classe che implementa la logica dei metodi per le chiamate REST definite in:
 * @see PrinterAPIService
 */
public class PrinterAPI extends APIErrorHandler{


    private String TAG = PrinterAPI.class.getSimpleName();

    /**
     * Metodo che ottiene informazioni sulla stampante
     *
     * @param apiCallback callback di ritorno
     * @return ritorna un oggetto Status contenuto nel Result
     * @see Status
     */
    public void getPrinterStatus(final APICallback apiCallback){
        Call<PrinterInfo> printerStatusCall = RestAPIModule.getPrinterInstance().getPrinterInfo();
        printerStatusCall.enqueue(new Callback<PrinterInfo>() {
            @Override
            public void onResponse(Call<PrinterInfo> call, Response<PrinterInfo> response) {
                if(response.isSuccessful()){
                    apiCallback.onFinish(
                            new Result(Errors.ERROR_OK,
                                    "get printer info OK",
                                    null,
                                    response.body().getStatus()));
                } else {
                    processError(apiCallback, response);
                }
            }

            @Override
            public void onFailure(Call<PrinterInfo> call, Throwable t) {

                processError(apiCallback, t);
            }
        });
    }

    public void getPrinterStatusV2(final APICallbackV2<PrinterInfo> apiCallback){
//        DocumentBuilderImpl builder = new DocumentBuilderImpl();
//        builder.setText("");
        Call<PrinterInfo> printerInfoCall = RestAPIModule.getPrinterInstance().print(AppUtils.getVoidDocument());
        printerInfoCall.enqueue(new Callback<PrinterInfo>() {
            @Override
            public void onResponse(Call<PrinterInfo> call, Response<PrinterInfo> response) {
                parsePrinterStatusResponse(response, apiCallback);
            }

            @Override
            public void onFailure(Call<PrinterInfo> call, Throwable t) {
                processException(apiCallback, t, call);
            }
        });
    }

    public void getPrinterStatusV2Sync(final APICallbackV2<PrinterInfo> apiCallback) {
//        DocumentBuilderImpl builder = new DocumentBuilderImpl();
//        builder.setText("");
        Call<PrinterInfo> printerInfoCall = RestAPIModule.getPrinterInstance().print(AppUtils.getVoidDocument());
        try {
            Response<PrinterInfo> response = printerInfoCall.execute();
            parsePrinterStatusResponse(response, apiCallback);
        } catch (IOException e) {
            processException(apiCallback, e, printerInfoCall);
        }
    }

    private void parsePrinterStatusResponse(Response<PrinterInfo> response, APICallbackV2<PrinterInfo> apiCallback) {
        try {
            if(response.isSuccessful()){
                PrinterInfo info = response.body();
                if(info.getStatus().getGeneralState() == null){
                    apiCallback.onError(Errors.ERROR_NET_UNABLE_READ_ERROR_BODY,
                            Errors.getMap().get(Errors.ERROR_NET_UNABLE_READ_ERROR_BODY),
                            null);
                }  else {
                    apiCallback.onResult(response.body());
                }
            } else {
                // info.getError() ritorna un codice d'errore che è il codice http della response
                // e potrebbe confondere o sovrapporsi con altri codici se ci si aggiunge 10k
//                String jsonError = response.errorBody().string();
//                PrinterInfo info = new Gson().fromJson(jsonError, PrinterInfo.class);
//                if(info.getError() != null){
//                    apiCallback.onError(PosUtils.parsePosCode(info.getError().getCode()),
//                            Errors.getMap().get(Errors.ERROR_NET_SERVER_KO),
//                            null);
//                    return;
//                }


                processResponseKO(apiCallback, response);
            }
        } catch (Exception e) {
            Log.e(TAG, "parsePrinterStatusResponse: ", e);
            apiCallback.onError(Errors.ERROR_NET_UNABLE_READ_ERROR_BODY,
                    Errors.getMap().get(Errors.ERROR_NET_UNABLE_READ_ERROR_BODY),
                    e);
        }
    }


    public void print(Document document, final APICallback apiCallback){
        Call<PrinterInfo> printCall = RestAPIModule.getPrinterInstance().print(document);
//        String json = new Gson().toJson(document);
        printCall.enqueue(new Callback<PrinterInfo>() {
            @Override
            public void onResponse(Call<PrinterInfo> call, Response<PrinterInfo> response) {
                if(response.isSuccessful()){
                    apiCallback.onFinish(new Result(Errors.ERROR_OK,
                            "print OK",
                            null,
                            response.body().getStatus()));
                } else {
                    processError(apiCallback, response);
                }
            }

            @Override
            public void onFailure(Call<PrinterInfo> call, Throwable t) {

                processError(apiCallback, t);
            }
        });
    }

    public Result printSync(final Document document){
        AsyncTask<Void, Void, Result> mTask = new AsyncTask<Void, Void, Result>() {
            @Override
            protected Result doInBackground(Void... voids) {

                Call<PrinterInfo> printCall = RestAPIModule.getPrinterInstance().print(document);
                try {
                    PrinterInfo printerInfo = printCall.execute().body();
                    return new Result(Errors.ERROR_OK,
                            printerInfo);
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



    public void resetPrinterConf(final APICallback apiCallback){
        Call<Void> resetCall = RestAPIModule.getPrinterInstance().resetPrinterConf();
        resetCall.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.isSuccessful()){
                    Log.d(TAG, response.message());
                    apiCallback.onFinish(
                            new Result(Errors.ERROR_OK,
                                    null,
                                    null,
                                    null));
                } else {
                    processError(apiCallback, response);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

                processError(apiCallback, t);
            }
        });
    }
}
