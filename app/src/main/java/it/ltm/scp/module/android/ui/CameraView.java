package it.ltm.scp.module.android.ui;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

/**
 * Created by HW64 on 25/01/2017.
 */

public class CameraView extends SurfaceView implements SurfaceHolder.Callback{

    SurfaceHolder mHolder;
    Camera mCamera;
    private static final String TAG = "CameraView";


    // fixed camera size
    int previewFixedHeight = 1072;

    public CameraView(Context context) {
        super(context);
        mHolder = getHolder();
        mHolder.addCallback(this);
    }

    private void stopPreviewAndFreeCamera() {

        if (mCamera != null) {
            // Call stopPreview() to stop updating the preview surface.
            mCamera.stopPreview();

            // Important: Call release() to release the camera for use by other
            // applications. Applications should release the camera immediately
            // during onPause() and re-open() it during onResume()).
            mCamera.release();

            mCamera = null;
        }
    }

    @Override
    protected void onLayout(boolean b, int i, int i1, int i2, int i3) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        try {
            mCamera.setPreviewDisplay(mHolder);
        } catch (IOException e) {
            Log.e(TAG, "", e);
        }

    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int x, int y) {
        try {   // stoppare eventuale precedente istanza di Camera
            mCamera.stopPreview();
            Camera.Parameters param = mCamera.getParameters();
//        List<Camera.Size> avaibleSizeList = param.getSupportedPreviewSizes();


//        param.setPreviewSize(i1, i2);  i1 = x ; i2 = y
//        Camera.Size tempSize = avaibleSizeList.get(2);
//
            param.setPreviewSize(previewFixedHeight, previewFixedHeight);
//            param.setPictureSize(previewFixedHeight, previewFixedHeight);

            // parametri alternativi per riduzione peso foto:
            param.setPictureSize(640, 480);
            param.setJpegQuality(90);


//        param.setColorEffect(Camera.Parameters.EFFECT_MONO);    // bianco e nero
            mCamera.setParameters(param);
            mCamera.startPreview();
        } catch (Exception e){
            // Camera ancora non attiva, ignorare eccezione
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        if (mCamera != null) {
            // Call stopPreview() to stop updating the preview surface.
            stopPreviewAndFreeCamera();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(heightMeasureSpec, heightMeasureSpec);  // 1:1 ratio
    }

    public void setCamera(Camera camera) {
        this.mCamera = camera;
    }
}
