package it.ltm.scp.module.android.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import butterknife.OnClick;
import it.ltm.scp.module.android.R;
import it.ltm.scp.module.android.controllers.LaunchActivityController;
import it.ltm.scp.module.android.utils.AppUtils;
import it.ltm.scp.module.android.utils.CustomProgressBar;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LaunchActivity extends BaseDialogActivity {
    @BindView(R.id.progressbar_launch)
    CustomProgressBar mProgress;
    @BindView(R.id.tw_launch_main_message)
    TextView mTextView;
    @BindView(R.id.button_launch_error_retry)
    View mRetryButton;
    @BindView(R.id.close_launch)
    View mCloseButton;

    private LaunchActivityController mController;


    private final String TAG = LaunchActivity.class.getSimpleName();

    private final int STATE_DEFAULT = 0;
    private final int STATE_CONNECTING = 1;
    private final int STATE_ERROR = 2;
    private final int STATE_FINISH = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
        ButterKnife.bind(this);
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mController = new LaunchActivityController(this);
        mController.checkConnectivity();
        launchAppSwitcherSilently();
    }

    private void launchAppSwitcherSilently() {
        Log.d(TAG, "launchAppSwitcherSilently: init");
        Intent launchIntent = getPackageManager().getLaunchIntentForPackage("overlay.lottomatica.com.overlay");
        if(launchIntent != null){
            Log.d(TAG, "launchAppSwitcherSilently: intent found, launching app..");
            launchIntent.setAction(Intent.ACTION_MAIN);
            launchIntent.addCategory(Intent.CATEGORY_DEFAULT);
            launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            startActivity(launchIntent);
        } else {
            Log.w(TAG, "launchAppSwitcherSilently: intent not found for package");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mController.attach(getApplicationContext());
    }

    @Override
    protected void onPause() {
        super.onPause();
        mController.detach(getApplicationContext());
    }

    @Override
    protected void onDestroy() {
        mController.destroy();
        super.onDestroy();
    }

    @OnClick(R.id.button_launch_error_retry)
    public void onRetry(){
        mController.retry();
    }

    @OnClick(R.id.close_launch)
    public void closeApp(){
        finish();
    }

    @Override
    public void onCredentialAcquired(String username, String password) {
        super.onCredentialAcquired(username, password);
        mController.onCredentialAcquired(username, password);
    }

    private void setProgress(boolean progress){
        mProgress.setVisibility(progress ? View.VISIBLE : View.GONE);
    }

    private void showRetryButton(boolean show){
        mRetryButton.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
    }

    private void showCloseButton(boolean show) {
        mCloseButton.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
    }

    public void showSnackBar(String text){
//        Snackbar.make(mTextView, text, Snackbar.LENGTH_LONG).show();
        AppUtils.getMessageSnackbar(mTextView, text).show();
    }
    public void updateText(String text){
        mTextView.setText(text);
    }

    public void setTextSize(int size){
        mTextView.setTextSize(size);
    }




    public void startApp() {
        switchLayout(STATE_FINISH);
        updateText("Benvenuto");
        mTextView.setTextSize(36);
        mTextView.setTextColor(Color.BLACK);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(LaunchActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        }, 3000);
    }


    public void switchLayout(int status){
        switch (status){
            case STATE_DEFAULT:
                setProgress(true);
                showRetryButton(false);
                showCloseButton(true);
                break;
            case STATE_ERROR:
                setProgress(false);
                showRetryButton(true);
                showCloseButton(true);
                break;
            case STATE_CONNECTING:
                setProgress(true);
                showRetryButton(false);
                showCloseButton(true);
                break;
            case STATE_FINISH:
                setProgress(false);
                showRetryButton(false);
                showCloseButton(false);
                break;
        }
    }


    @Override
    public void onRetryAuth() {
        // not used
    }

    @Override
    public void onRetryPosInfo() {
        // not used
    }


    @Override
    public void onRetryBarcode() {
        // not used
    }

    @Override
    public void onPrinterReady() {
        // not used
    }
}
