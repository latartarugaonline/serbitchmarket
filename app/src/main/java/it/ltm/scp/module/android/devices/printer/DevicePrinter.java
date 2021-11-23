package it.ltm.scp.module.android.devices.printer;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import it.ltm.scp.module.android.api.APICallback;
import it.ltm.scp.module.android.api.RestAPIModule;
import it.ltm.scp.module.android.api.printer.PrinterAPI;
import it.ltm.scp.module.android.exceptions.InvalidArgumentException;
import it.ltm.scp.module.android.model.Result;
import it.ltm.scp.module.android.model.devices.printer.gson.Document;
import it.ltm.scp.module.android.model.devices.printer.gson.PrinterInfo;
import it.ltm.scp.module.android.utils.Constants;
import it.ltm.scp.module.android.utils.Properties;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by HW64 on 23/08/2016.
 */
public class DevicePrinter {

    private String TAG = DevicePrinter.class.getSimpleName();

    // thread safe singleton
    private static DevicePrinter mInstance;

    private DevicePrinter(){}

    public static synchronized DevicePrinter getInstance(){
        if(mInstance == null){
            mInstance = new DevicePrinter();
        }
        return mInstance;
    }


    public void getPrinterStatus(APICallback callback){
        new PrinterAPI().getPrinterStatus(callback);
    }


    //TEST ONLY:
    public PrinterInfo getPrinterStatusSync() throws ExecutionException, InterruptedException {
        AsyncTask mTask = new AsyncTask() {
            @Override
            protected PrinterInfo doInBackground(Object[] objects) {
                Call<PrinterInfo> printerStatusCall = RestAPIModule.getPrinterInstance().getPrinterInfo();
                try {
                    Response<PrinterInfo> response = printerStatusCall.execute();
                    return response.body();
                } catch (IOException e) {
                    Log.e(TAG, "", e);
                }
                return null;
            }
        };

        PrinterInfo info = (PrinterInfo) mTask.get();
        return info;
    }


    public void print(Document document, APICallback callback){
        new PrinterAPI().print(document, callback);
    }

    public Result printSync(Document document){
        return new PrinterAPI().printSync(document);
    }


    public void resetPrinterConf(APICallback callback){
        new PrinterAPI().resetPrinterConf(callback);
    }

    public void setLocalDefaultParam(){
        try {
            print(getLocalDefaultConfig(), new APICallback() {
                @Override
                public void onFinish(Result result) {
                    Log.d(TAG, "Restored local default values.");
                }
            });
        } catch (InvalidArgumentException e) {
            Log.e(TAG, "", e);
        }
    }

    public void initPaper(){
        DocumentBuilderImpl builder = new DocumentBuilderImpl();
        builder.setNewLines(2);
        builder.setCut(false);
        print(builder.build(), new APICallback() {
            @Override
            public void onFinish(Result result) {
                Log.d(TAG, "onFinish() called with: result = [" + result.toJsonString() + "]");
            }
        });
    }

    /**
     * Metodo che resetta gli stili a default della stampante.
     *
     * !! ATTENZIONE: i parametri di default sono anche definiti nel file "parseManager.js"
     *
     * @return Document pronto per essere inviato
     * @throws InvalidArgumentException parametro di default non accettato o non valido
     */
    private Document getLocalDefaultConfig() throws InvalidArgumentException {
        DocumentBuilderImpl documentBuilder = new DocumentBuilderImpl();
        documentBuilder.setAlign(DefaultPrinterConfig.getAlign());
        documentBuilder.setBold(DefaultPrinterConfig.getBold());
        documentBuilder.setNegative(DefaultPrinterConfig.getNegative());
        documentBuilder.setSize(DefaultPrinterConfig.getSize());
        documentBuilder.setFont(DefaultPrinterConfig.getFont());
        documentBuilder.setFlip(DefaultPrinterConfig.getFlip());
        documentBuilder.setRotate(DefaultPrinterConfig.getRotate());
        documentBuilder.setLine(DefaultPrinterConfig.getLine());
        documentBuilder.setSpacing(DefaultPrinterConfig.getSpacing());
        documentBuilder.setUnderline(DefaultPrinterConfig.getUnderline());
        return documentBuilder.build();
    }
}
