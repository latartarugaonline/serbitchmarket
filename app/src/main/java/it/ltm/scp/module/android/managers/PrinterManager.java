package it.ltm.scp.module.android.managers;

import android.os.Handler;
import android.os.Looper;

import it.ltm.scp.module.android.api.APICallback;
import it.ltm.scp.module.android.api.APICallbackV2;
import it.ltm.scp.module.android.api.printer.PrinterAPI;
import it.ltm.scp.module.android.devices.pos.PosUtils;
import it.ltm.scp.module.android.devices.printer.DevicePrinter;
import it.ltm.scp.module.android.devices.terminal.TerminalManagerFactory;
import it.ltm.scp.module.android.model.Result;
import it.ltm.scp.module.android.model.devices.printer.gson.Document;
import it.ltm.scp.module.android.model.devices.printer.gson.PrinterInfo;
import it.ltm.scp.module.android.model.devices.printer.gson.status.Status;
import it.ltm.scp.module.android.monitor.PrintMonitor;
import it.ltm.scp.module.android.utils.Errors;

/**
 * Created by HW64 on 10/03/2017.
 */

public class PrinterManager {

    private final String TAG = PrinterManager.class.getSimpleName();

    public interface PrintCallback {
        void onPrinterException(String message);
        void onPrinterStatus(Status status);
        void onResult(Result result, String callbackName);
    }

    private Document mDocument;
    private String mCallbackName;
    private PrintCallback mCallback;
    private Handler mMainHandler = new Handler(Looper.getMainLooper());

    public void executePrint(Document document, String callbackName, PrintCallback callback) {
        this.mDocument = document;
        this.mCallbackName = callbackName;
        this.mCallback = callback;

        new PrinterAPI().getPrinterStatusV2Sync(new APICallbackV2<PrinterInfo>() {
            @Override
            public void onResult(PrinterInfo result) {
                    final Status status = result.getStatus();
                    if(status.getGeneralState() == 0){
                        print();
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                String printerStatusKoMessage = TerminalManagerFactory.get().getKoFromPrinterStatus(status);
                                Result result1 = new Result(Errors.ERROR_PRINTER_GENERAL
                                        , Errors.getMap().get(Errors.ERROR_PRINTER_GENERAL) + "(" + printerStatusKoMessage + ")"
                                        , null,
                                        status);
                                PrintMonitor.schedulePrinterMonitor(TAG, "getPrinterStatus: ", result1, mDocument);
                                mCallback.onPrinterStatus(status);
                                mCallback.onResult(result1, mCallbackName);
                                mCallback = null;
//                                LogWrapper.e(TAG, "getPrinterStatus: " + result1.toJsonString());
                            }
                        });
                    }
            }

            @Override
            public void onError(final int code, final String message, final Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Result result = new Result(code,
                                message,
                                null);
                        PrintMonitor.schedulePrinterMonitor(TAG, "getPrinterStatus: ", result, mDocument, e);
                        mCallback.onResult(result, mCallbackName);
                        String errorMessage = PosUtils.appendCodeToMessage(message, code);
                        mCallback.onPrinterException(errorMessage);
                        mCallback = null;
//                        LogWrapper.e(TAG, "getPrinterStatus: " + result.toJsonString(), e);
                    }
                });
            }
        });
    }

    private void print() {
        DevicePrinter.getInstance().print(mDocument, new APICallback() {
            @Override
            public void onFinish(Result result) {
                if (mCallbackName != null) {
                    if(result.getCode() != Errors.ERROR_OK){
//                        LogWrapper.e(TAG, "doPrint: " + result.toJsonString());
                        PrintMonitor.schedulePrinterMonitor(TAG, "doPrint: ", result, mDocument);
                    }
                    mCallback.onResult(result, mCallbackName);
                    mCallback = null;
                }

            }
        });
    }

    private void runOnUiThread(Runnable r){
        mMainHandler.post(r);
    }

}
