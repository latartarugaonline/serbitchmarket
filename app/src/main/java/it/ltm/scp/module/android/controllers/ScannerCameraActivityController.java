package it.ltm.scp.module.android.controllers;

import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.List;

import it.ltm.scp.module.android.App;
import it.ltm.scp.module.android.api.APICallbackV2;
import it.ltm.scp.module.android.devices.scanner.DeviceScanner;
import it.ltm.scp.module.android.managers.PictureSessionManager;
import it.ltm.scp.module.android.model.devices.pos.gson.AuthAsyncWrapper;
import it.ltm.scp.module.android.model.devices.pos.payment.gson.PaymentAsyncWrapper;
import it.ltm.scp.module.android.model.devices.pos.prompt.PromptResponseAsyncWrapper;
import it.ltm.scp.module.android.model.devices.pos.tsn.TsnAsyncWrapper;
import it.ltm.scp.module.android.model.devices.printer.gson.status.Status;
import it.ltm.scp.module.android.model.devices.scanner.ImageRequest;
import it.ltm.scp.module.android.model.devices.scanner.ScannerSnapshot;
import it.ltm.scp.module.android.model.devices.scanner.ScannerSnapshotREST;
import it.ltm.scp.module.android.model.devices.scanner.ScannerStatus;
import it.ltm.scp.module.android.model.devices.scanner.ScannerUpdate;
import it.ltm.scp.module.android.model.devices.system.gson.update.UpdateStatus;
import it.ltm.scp.module.android.ui.ScannerCameraActivity;
import it.ltm.scp.module.android.utils.AppUtils;
import it.ltm.scp.module.android.utils.CameraUtils;

public class ScannerCameraActivityController extends WebSocketController {

    private List<ImageRequest> mImageRequestList;
    private WeakReference<ScannerCameraActivity> mView;
    private int mImageIndex;
    private String mCurrentImageBytes;
    private boolean isBcrUpdating = false;

    private final String TAG = ScannerCameraActivityController.class.getSimpleName();

    public ScannerCameraActivityController(List<ImageRequest> mImageRequestList, ScannerCameraActivity view) {
        this.mImageRequestList = mImageRequestList;
        mImageIndex = 0;
        this.mView = new WeakReference<>(view);
        processNextImage();
    }

    private ScannerCameraActivity getView() throws Exception {
        if(mView.get() == null) throw new Exception("View is null");
        return mView.get();
    }

    private void processNextImage(){
        if(mImageIndex < mImageRequestList.size()){
            try {
                getView().updateLabelText(getLabelByIndex(mImageIndex));
                getView().startPreview();
            } catch (Exception e) {
                Log.e(TAG, "processNextImage: ", e);
            }
        } else {
            sendResult();
        }
    }

    private String getLabelByIndex(int mImageIndex) {
        String label = mImageIndex + 1 + "/" + mImageRequestList.size();
        ImageRequest imgRequest = mImageRequestList.get(mImageIndex);
        if(imgRequest.getDescriptionLabel() != null){
            label += " " + imgRequest.getDescriptionLabel();
        }
        return label;
    }

    private void sendResult() {
        mCurrentImageBytes = null;
        try {
            getView().sendResultOK();
        } catch (Exception e) {
            Log.e(TAG, "sendResult: ", e);
        }
    }

    public void setCurrentImageBytes(String imageBytes) {
        this.mCurrentImageBytes = imageBytes;
    }


    public void acceptImage(boolean useIdc){
        ImageRequest imageRequest = mImageRequestList.get(mImageIndex);                                 // TODO: 15/07/2019 controllo su indice outofbound
        imageRequest.setIdcUsed(useIdc);

        /*FIX 0.6.4: inlcudo il base64 in un oggetto clonato, l'originale non può contenerlo poichè
        * viene ritornato tramite Intent result all'activity chiamante e supererebbe il limite di bytes imposto dall'OS
        *
        * note: !!! FAILED BINDER TRANSACTION !!! / android.os.TransactionTooLargeException
        */
        ImageRequest localImageRequestObject = (ImageRequest)imageRequest.clone();
        localImageRequestObject.setImgData(mCurrentImageBytes);

        /* Salvo in memoria locale l'oggetto clonato con l'immagine */
        PictureSessionManager.savePictureInSession(localImageRequestObject);
        mImageIndex ++;
        processNextImage();
    }

    public void discardImage(){
        mCurrentImageBytes = null;
//        processNextImage();
    }



    /*
        Websocket event impl
         */
    @Override
    public void onPrinterStatus(Status status) {

    }

    @Override
    public void onBarcodeEvent(String code) {

    }

    @Override
    public void onBarcodeStatusEvent(ScannerStatus status) {
            if(status.getStatus().equalsIgnoreCase(ScannerStatus.STATUS_STOP)){
                try {
                    if (!getView().isAuthPending() && !isBcrUpdating) {
                        getView().processBarcodeStatus(ScannerStatus.MESSAGE_REBOOT, false, false);
                    }
                } catch (Exception e) {
                    return;
                }
            } else if(status.getStatus().equalsIgnoreCase(ScannerStatus.STATUS_READY)){

                try {
                    isBcrUpdating = false;
                    getView().processBarcodeStatus(ScannerStatus.MESSAGE_REBOOT, false, true);
                    getView().retry();
                } catch (Exception e) {
                    return;
                }
            }
    }

    @Override
    public void onAuthEvent(AuthAsyncWrapper wrapper) {

    }

    @Override
    public void onPaymentEvent(PaymentAsyncWrapper wrapper) {

    }

    @Override
    public void onTsnEvent(TsnAsyncWrapper wrapper) {

    }

    @Override
    public void onUpdateEvent(UpdateStatus status) {

    }

    @Override
    public void onPromptEvent(PromptResponseAsyncWrapper wrapper) {

    }

    @Override
    public void onPowerKeyPressed() {
        AppUtils.clearAuthData(App.getContext());
        try {
            AppUtils.closeAppWithDialog(getView());
        } catch (Exception e){
            Log.e(TAG, "onPowerKeyPressed: ", e);
        }
    }

    @Override
    public void onSnapshotReceived(ScannerSnapshot snapshot) {
        if(mImageIndex >= mImageRequestList.size()){
            Log.w(TAG, "onSnapshotReceived: Image list is already complete. skip image");
            return;
        }
        mCurrentImageBytes = snapshot.getContent();
        try {
            getView().onPictureReceived(mCurrentImageBytes);
        } catch (Exception e) {
            Log.e(TAG, "onImageReceived: ", e);
        }
    }

    @Override
    public void onBcrUpdateEvent(ScannerUpdate update) {
        Log.d(TAG, "onBcrUpdateEvent() called with: update = [" + update + "]");
        if(update.getCode().equals(ScannerUpdate.BCR_UPDATE_START_INSTALL)){
            //show popup
            try {
                this.isBcrUpdating = true;
                getView().processBarcodeStatus(ScannerUpdate.BCR_UPDATE_MESSAGE, false, false);
            } catch (Exception e){
                Log.e(TAG, "onBcrUpdateEvent: view is null");
            }
        }
    }

    public void takeSnapshot() {
        DeviceScanner.getInstance().getSnapshot(new APICallbackV2<ScannerSnapshotREST>() {
            @Override
            public void onResult(ScannerSnapshotREST result) {
                try {
                    getView().onPictureReceived(result.getData());
                } catch (Exception e) {
                    Log.e(TAG, "takeSnapshot: ", e);
                }
            }

            @Override
            public void onError(int code, String message, Exception e) {
                Log.e(TAG, "onError() called with: code = [" + code + "], message = [" + message + "], e = [" + e + "]");
                try {
                    getView().showError(message + " (" + code + ")");
                } catch (Exception e1) {
                    Log.e(TAG, "takeSnapshot: ", e1);
                }
            }
        });
    }
}
