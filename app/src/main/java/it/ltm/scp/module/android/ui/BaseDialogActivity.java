package it.ltm.scp.module.android.ui;

import android.app.Dialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import it.ltm.scp.module.android.model.devices.printer.gson.status.Status;

/**
 * Created by HW64 on 30/03/2017.
 * <p>
 * Integrazione del popup principale con la parte View (Activity)
 * da mostrare in caso di operazioni bloccanti
 */

public abstract class BaseDialogActivity extends AppCompatActivity implements MainDialogFragment.MainDialogListener {
    private String TAG = BaseDialogActivity.class.getSimpleName();
    private MainDialogFragment mDialog;

    public boolean isAuthPending() {
        return mDialog != null && mDialog.isAdded() && mDialog.isAuthShown();
    }

    public boolean isUpdatePending() {
        return mDialog != null && mDialog.isAdded() && mDialog.isUpdateShown();
    }

    public boolean isPrinterPending() {
        return mDialog != null && mDialog.isAdded() && mDialog.isPrinterShown();
    }

    public void processPrinterEvent(final Status status) {
        Log.d(TAG, "processPrinterEvent() called with: status = [" + status + "]");
        if (status.calculateGeneralState() == 0 && !isPrinterPending()) {
            //stato già OK, evita apertura e chiusura immediata del dialog (causa problemi con tastiera di sistema se aperta)
            return;
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (showDialog()) {
                        mDialog.processPrinterStatus(status);
                    }
                }
            });
        }

    }

    public void processAuthStatus(final String message, final boolean showReload, final boolean finish) {
        Log.e(TAG, "processAuthStatus: " + message);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (showDialog()) {
                    mDialog.processAuthStatus(message, showReload, finish);
                }
            }
        });
    }

    public void processICTStatus(final String message, final boolean showReload, final boolean finish) {
        Log.e(TAG, "processICTStatus: " + message);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (showDialog()) {
                    mDialog.processICTStatus(message, showReload, finish);
                }
            }
        });
    }

    public void onPosInfoComplete() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isDialogShowing()) {
                    mDialog.processICTStatus("", false, true);
                }
            }
        });
    }

    public boolean isDialogShowing() {
        return mDialog != null && mDialog.isAdded()
                && mDialog.getDialog() != null //fix quando in alcuni casi (es: richiesta posinfo subito dopo una doAuth) in cui mDialog risulta ancora added ma il getDialog ritorna null
                && mDialog.getDialog().isShowing();
    }

    public void onLisComplete() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isDialogShowing()) {
                    mDialog.processBarcodeStatus("", false, true);
                }
            }
        });

    }

    public void processBarcodeStatus(final String message, final boolean showReload, final boolean finish) {
        Log.e(TAG, "processBarcodeStatus: " + message);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (showDialog()) {
                    mDialog.processBarcodeStatus(message, showReload, finish);
                }
            }
        });
    }

    public void processUpdateStatus(final String message, final boolean finish) {
        Log.d(TAG, "processUpdateStatus() called with: message = [" + message + "], finish = [" + finish + "]");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (showDialog()) {
                    mDialog.processUpdateStatus(message, finish);
                }
            }
        });
    }

    public void requestLoginCredential(final String errorMessage, final boolean finish) {
        Log.d(TAG, "requestLoginCredential() called with: errorMessage = [" + errorMessage + "], finish = [" + finish + "]");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (showDialog()) {
                    mDialog.requestLoginCredential(errorMessage, finish);
                }
            }
        });
    }

    public boolean showDialog() {
        if (mDialog == null) {
            mDialog = MainDialogFragment.newInstance();
        }
        if (mDialog.isVisible() || mDialog.isAdded()) {
            return true;
        } else {
            if (isFinishing()) {
                Log.d(TAG, "showDialog: activity isFinishing, dont add dialog");
                return false;
            }
            try {
                mDialog.show(getSupportFragmentManager(), "dialog");
                getSupportFragmentManager().executePendingTransactions();
                mDialog = (MainDialogFragment) getSupportFragmentManager().findFragmentByTag("dialog");
                return true;
            } catch (IllegalStateException isex) {
                return false;
            }
        }

        /*
        if(mdialog.getdialog != null){
        return true;}
        else mdialog show etc..
         */
    }

    @Override
    public void onCloseApp(Dialog dialog) {
        dialog.dismiss();
        finish();
    }

    public void processPrinterException(String message) {
        if (showDialog()) {
            mDialog.processPrinterError(message);
        }
    }

    @Override
    public void onCredentialAcquired(String username, String password) {
        Log.d(TAG, "onCredentialAcquired() called with: username = [" + username + "], password = [" + password + "]");
    }
}
