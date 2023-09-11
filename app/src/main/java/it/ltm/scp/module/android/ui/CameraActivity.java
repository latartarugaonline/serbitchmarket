package it.ltm.scp.module.android.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import it.ltm.scp.module.android.R;
import it.ltm.scp.module.android.controllers.CameraActivityController;
import it.ltm.scp.module.android.utils.CameraUtils;

public class CameraActivity extends BaseDialogActivity {

    ImageView mPreviewImage;
    TextView mLabelView;
    ViewGroup mCameraViewLayout;
    FloatingActionButton mAccettaButton;
    FloatingActionButton mRifiutaButton;
    FloatingActionButton mScattaButton;
    CameraView mCameraView;

    private CameraActivityController mController;

    private int PERMISSION_CAMERA_REQUEST_ID = 50;
    public static final int RESULT_PERMISSION_DENIED = -2;
    public static final int RESULT_OK_MULTI = 4;
    public static final String INTENT_EXTRA_CAMERA_NUM_SHOTS = "numShots";
    public static final String INTENT_EXTRA_CAMERA_LABELS = "labels";
    private final String TAG = CameraActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setupView();

        int numShots = getIntent().getIntExtra(INTENT_EXTRA_CAMERA_NUM_SHOTS, 1);
        String[] labels = getIntent().getStringArrayExtra(INTENT_EXTRA_CAMERA_LABELS);
        mController = new CameraActivityController(this, numShots, labels);
    }

    private void setupView() {
        mPreviewImage = findViewById(R.id.layout_camera_img);
        mLabelView = findViewById(R.id.text_camera_label);
        mCameraViewLayout = findViewById(R.id.layout_camera_view);
        mAccettaButton = findViewById(R.id.button_camera_accetta);
        mRifiutaButton = findViewById(R.id.button_camera_rifiuta);
        mCameraView = findViewById(R.id.button_camera_scatta);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mController.attach(getApplicationContext());
        init();
    }

    @Override
    protected void onPause() {
        mController.releaseCamera();
        mCameraViewLayout.removeView(mCameraView);
        super.onPause();
        mController.detach(getApplicationContext());
    }

    private void init() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "init: camera permission not found, requesting..");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PERMISSION_CAMERA_REQUEST_ID);
        } else {
            Log.d(TAG, "init: camera permission OK");
            setupCamera();
        }
    }

    private void setupCamera() {
        Camera camera = mController.getCameraInstance();
        if (mCameraView == null) {
            mCameraView = new CameraView(this);
        }
        mCameraView.setCamera(camera);
        mCameraViewLayout.addView(mCameraView);
    }

    public void scatta(View view) {
        mController.takePicture();
    }

    public void accept(View view) {
        mController.acceptImage();
    }

    public void rifiuta(View view) {
        hidePreview();
        mController.rejectImage(view);
    }

    public void back(View view) {
        CameraUtils.clearCache();
        setResult(Activity.RESULT_CANCELED);
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSION_CAMERA_REQUEST_ID) {
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "onRequestPermissionsResult: camera permission OK");
//                setupCamera();
            } else {
                Log.d(TAG, "onRequestPermissionsResult: camera permission DENIED FROM USER");
                setResult(RESULT_PERMISSION_DENIED);
                finish();
            }
        }
    }


    public void showPreview(Bitmap bitmap) {
        mPreviewImage.setVisibility(View.VISIBLE);
        mCameraViewLayout.setVisibility(View.GONE);
        togglePanel(true);
        mPreviewImage.setImageBitmap(bitmap);
    }

    public void updateLabelView(String label) {
        mLabelView.setText(label);
    }

    public void hidePreview() {
        mPreviewImage.setVisibility(View.GONE);
        mCameraViewLayout.setVisibility(View.VISIBLE);
        togglePanel(false);
    }

    public void sendImageBytes(byte[] imageBytes) {
        Intent resultIntent = new Intent();
        CameraUtils.saveImageBytes(imageBytes);
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }

    public void sendMultipleImages() {
        setResult(RESULT_OK_MULTI);
        finish();
    }

    private void togglePanel(boolean showConfirm) {
        if (showConfirm) {
            mScattaButton.setVisibility(View.INVISIBLE);
            mAccettaButton.setVisibility(View.VISIBLE);
            mRifiutaButton.setVisibility(View.VISIBLE);
        } else {
            mScattaButton.setVisibility(View.VISIBLE);
            mAccettaButton.setVisibility(View.INVISIBLE);
            mRifiutaButton.setVisibility(View.INVISIBLE);
        }
    }


    @Override
    public void onRetryAuth() {
        //ignorare
    }

    @Override
    public void onRetryPosInfo() {
        //ignorare
    }

    @Override
    public void onRetryBarcode() {
        //ignorare
    }

    @Override
    public void onPrinterReady() {
        //ignorare
    }
}
