package it.ltm.scp.module.android.websocket;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.gson.Gson;

import it.ltm.scp.module.android.model.devices.pos.gson.AuthAsyncWrapper;
import it.ltm.scp.module.android.model.devices.pos.payment.gson.PaymentAsyncWrapper;
import it.ltm.scp.module.android.model.devices.pos.prompt.PromptResponseAsyncWrapper;
import it.ltm.scp.module.android.model.devices.pos.tsn.TsnAsyncWrapper;
import it.ltm.scp.module.android.model.devices.printer.gson.status.Status;
import it.ltm.scp.module.android.model.devices.scanner.ScannerSnapshot;
import it.ltm.scp.module.android.model.devices.scanner.ScannerStatus;
import it.ltm.scp.module.android.model.devices.scanner.ScannerUpdate;
import it.ltm.scp.module.android.model.devices.system.gson.update.UpdateStatus;

/**
 * Created by HW64 on 12/10/2016.
 */
public class WebSocketReceiver extends BroadcastReceiver {
    private WebSocketListener listener;
    private final String TAG = this.getClass().getSimpleName();

    public interface WebSocketListener {
        void onPrinterStatus(Status status);
        void onBarcodeEvent(String code);
        void onBarcodeStatusEvent(ScannerStatus status);
        void onAuthEvent(AuthAsyncWrapper wrapper);
        void onPaymentEvent(PaymentAsyncWrapper wrapper);
        void onTsnEvent(TsnAsyncWrapper wrapper);
        void onUpdateEvent(UpdateStatus status);
        void onPromptEvent(PromptResponseAsyncWrapper wrapper);
        void onPowerKeyPressed();
        void onSnapshotReceived(ScannerSnapshot snapshot);
        void onBcrUpdateEvent(ScannerUpdate update);
    }

    public WebSocketReceiver(WebSocketListener listener) {
        this.listener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive: Intent Action: " + intent.getAction());
        if(intent.getAction().equals(WebSocketService.FILTER)){
            String message = intent.getStringExtra("message");
            parseFrame(message);
        }
    }

    private void parseFrame(String frameString){
        String [] lines = frameString.split("\n");
        String destination = "";
        for(String s : lines){
            if(s.contains("destination: ")){
                destination = s;
                break;
            }
        }

        if(destination.contains("scanner/scan")){
            StringBuilder code = new StringBuilder();

            for(int i = 2; i<lines.length; i++) {
                String currentLine = lines[i];
                code.append(currentLine.replaceAll("[\"]", ""));
                if(currentLine.endsWith("\"")) //end of barcode content
                    break;
            }

            listener.onBarcodeEvent(code.toString());
            return;
        }
        if(destination.contains("scanner/snapshot")){
            for(int i = 1; i<lines.length; i++){
                String line = lines[i];
                if(line.startsWith("{")){
                    ScannerSnapshot scannerSnapshot = new Gson().fromJson(line, ScannerSnapshot.class);
                    listener.onSnapshotReceived(scannerSnapshot);
                    return;
                }
            }
        }
        if(destination.contains("async/auth")){
            for(int i = 1; i<lines.length; i++){
                String line = lines[i];
                if(line.startsWith("{")){
                    AuthAsyncWrapper authAsyncWrapper = new Gson().fromJson(line, AuthAsyncWrapper.class);
                    listener.onAuthEvent(authAsyncWrapper);
                    return;
                }
            }
        }
        if(destination.contains("async/payment")){
            for(int i = 1; i<lines.length; i++){
                String line = lines[i];
                if(line.startsWith("{")){
                    PaymentAsyncWrapper paymentAsyncWrapper = new Gson().fromJson(line, PaymentAsyncWrapper.class);
                    listener.onPaymentEvent(paymentAsyncWrapper);
                    return;
                }
            }
        }
        if(destination.contains("printer")){
            for(int i = 1; i<lines.length; i++){
                String line = lines[i];
                if(line.startsWith("{")){
                    try {
                        Status printerStatus = new Gson().fromJson(line, Status.class);
                        listener.onPrinterStatus(printerStatus);
                    } catch (Exception e){
                        Log.e(TAG, "parseFrame: Eccezione durante il parsing del body del messaggio", e);
                        return;
                    }
                    return;
                }
            }
        }

        if(destination.contains("async/tsn")){
            for(int i = 1; i<lines.length; i++){
                String line = lines[i];
                if(line.startsWith("{")){
                    TsnAsyncWrapper tsnAsyncWrapper = new Gson().fromJson(line, TsnAsyncWrapper.class);
                    listener.onTsnEvent(tsnAsyncWrapper);
                    return;
                }
            }
        }

        if(destination.contains("async/vas")){
            for(int i = 1; i<lines.length; i++){
                String line = lines[i];
                if(line.startsWith("{")){
                    PromptResponseAsyncWrapper promptAsyncWrapper = new Gson().fromJson(line, PromptResponseAsyncWrapper.class);
                    listener.onPromptEvent(promptAsyncWrapper);
                    return;
                }
            }
        }

        if(destination.contains("scanner/status")){
            for(int i = 1; i<lines.length; i++){
                String line = lines[i];
                if(line.startsWith("{")){
                    ScannerStatus scannerSnapshot = new Gson().fromJson(line, ScannerStatus.class);
                    listener.onBarcodeStatusEvent(scannerSnapshot);
                    return;
                }
            }
        }

        if(destination.contains("scanner/firmware_update")){
            for(int i = 1; i<lines.length; i++){
                String line = lines[i];
                if(line.startsWith("{")){
                    ScannerUpdate scannerUpdate = new Gson().fromJson(line, ScannerUpdate.class);
                    listener.onBcrUpdateEvent(scannerUpdate);
                    return;
                }
            }
        }

        if(destination.contains("system/update")){
            for(int i = 1; i<lines.length; i++){
                String line = lines[i];
                if(line.startsWith("{")){
                    UpdateStatus updateStatus = new Gson().fromJson(line, UpdateStatus.class);
                    listener.onUpdateEvent(updateStatus);
                    return;
                }
            }
        }

        if(destination.contains("keyevents/power")){
            listener.onPowerKeyPressed();
            return;
        }

        Log.d(TAG, "parseFrame: Destination not found");
    }
}

