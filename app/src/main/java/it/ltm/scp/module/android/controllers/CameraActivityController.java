package it.ltm.scp.module.android.controllers;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.util.Log;

import java.lang.ref.WeakReference;

import it.ltm.scp.module.android.App;
import it.ltm.scp.module.android.model.devices.pos.gson.AuthAsyncWrapper;
import it.ltm.scp.module.android.model.devices.pos.payment.gson.PaymentAsyncWrapper;
import it.ltm.scp.module.android.model.devices.pos.prompt.PromptResponseAsyncWrapper;
import it.ltm.scp.module.android.model.devices.pos.tsn.TsnAsyncWrapper;
import it.ltm.scp.module.android.model.devices.printer.gson.status.Status;
import it.ltm.scp.module.android.model.devices.scanner.ScannerSnapshot;
import it.ltm.scp.module.android.model.devices.scanner.ScannerStatus;
import it.ltm.scp.module.android.model.devices.scanner.ScannerUpdate;
import it.ltm.scp.module.android.model.devices.system.gson.update.UpdateStatus;
import it.ltm.scp.module.android.ui.CameraActivity;
import it.ltm.scp.module.android.utils.AppUtils;
import it.ltm.scp.module.android.utils.CameraUtils;


/**
 * Created by HW64 on 25/01/2017.
 */

public class CameraActivityController extends WebSocketController{

    private WeakReference<CameraActivity> mView;

    private int numShots;
    private int currentShot;
    private String[] labels;
    private boolean isSingleShot = true;
    private Camera mCamera;
    byte[] mCurrPictureBytes;
    Camera.PictureCallback mPictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] bytes, Camera camera) {
            mCurrPictureBytes = bytes;
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            try {
                getView().showPreview(bitmap);
            } catch (NullPointerException e){
                Log.w(TAG, "onPictureTaken: ", e);
            }
        }
    };

    private final String TAG = CameraActivityController.class.getSimpleName();

    public CameraActivityController(CameraActivity view, int numShots, String[] labels) {
        this.mView = new WeakReference<>(view);
        this.numShots = numShots;
        this.currentShot = 1;
        this.labels = labels;
        if(numShots > 1) {
            isSingleShot = false;
        }
        updateLabelView();

    }

    private void updateLabelView() {
        String currentLabel ="";

        if(!isSingleShot){
            //mostra numero di foto corrente e numero totale
            currentLabel += currentShot + "/" + numShots;
        }
        try {
            //mostra titolo foto se presente
             currentLabel += " " + labels[currentShot -1];
        } catch (Exception e){
            Log.e(TAG, "updateLabelView: ", e);
        }
        try {
            getView().updateLabelView(currentLabel);
        } catch (Exception e){
            Log.e(TAG, "updateLabelView: ", e);
        }
    }

    private CameraActivity getView() throws NullPointerException {
        if(mView.get() == null){
            throw new NullPointerException(CameraActivity.class.getSimpleName() + " is null");
        } else {
            return mView.get();
        }
    }

    public Camera getCameraInstance(){
        Camera c = null;
        int cameraCount = Camera.getNumberOfCameras();
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        for (int i=0; i < cameraCount; i++){
            Camera.getCameraInfo(i, cameraInfo);
            if(cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT){
                try {
                    c = Camera.open(i); // attempt to get a Camera instance
                    mCamera = c;
                    Log.d(TAG, "Camera API: camera open");
                }
                catch (Exception e){
                    // Camera is not available (in use or does not exist)
                    Log.e(TAG, "Camera API: exception opening camera", e);
                }
            }
        }

        return c; // returns null if camera is unavailable
    }

    public void releaseCamera(){
        this.mCamera = null;
    }

    public void takePicture(){
        Log.d(TAG, "taking picture");
        mCamera.takePicture(null, null, mPictureCallback);
    }

    public void acceptImage() {
        try {
            if(isSingleShot){
                getView().sendImageBytes(mCurrPictureBytes);
            } else {
                CameraUtils.addImageToCache(mCurrPictureBytes);
                if(currentShot == numShots){
                    getView().sendMultipleImages();
                } else {
                    currentShot ++;
                    getView().hidePreview();
                    updateLabelView();
                }
            }
        } catch (NullPointerException e){
            Log.w(TAG, "acceptImage: ", e);
        }
    }

    public void rejectImage() {
        mCurrPictureBytes = null;
    }

    @Override
    public void onPrinterStatus(Status status) {

    }

    @Override
    public void onBarcodeEvent(String code) {

    }

    @Override
    public void onBarcodeStatusEvent(ScannerStatus status) {
        Log.d(TAG, "onBarcodeStatusEvent() called with: status = [" + status + "]");
        // Ignored
        if(status.getScanner().equalsIgnoreCase(ScannerStatus.SCANNER_ZEBRA)
                && status.getStatus().equalsIgnoreCase(ScannerStatus.STATUS_READY)){
            try {
                getView().processBarcodeStatus("", false, true);
            } catch (NullPointerException e) {
                Log.e(TAG, "onBarcodeStatusEvent: view is null");
            }

        }
    }


    @Override
    public void onBcrUpdateEvent(ScannerUpdate update) {
        Log.d(TAG, "onBcrUpdateEvent() called with: update = [" + update + "]");
        if(update.getCode().equals(ScannerUpdate.BCR_UPDATE_START_INSTALL)){
            //show popup
            try {
                getView().processBarcodeStatus(ScannerUpdate.BCR_UPDATE_MESSAGE, false, false);
            } catch (NullPointerException e){
                Log.e(TAG, "onBcrUpdateEvent: view is null");
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
        //void
    }


}
