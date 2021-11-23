package it.ltm.scp.module.android.managers;

import android.os.Handler;
import android.util.Log;

import it.ltm.scp.module.android.api.APICallback;
import it.ltm.scp.module.android.devices.pos.DevicePos;
import it.ltm.scp.module.android.model.Result;
import it.ltm.scp.module.android.model.devices.pos.AsyncWrapper;
import it.ltm.scp.module.android.utils.Errors;

/**
 * Created by HW64 on 20/02/2017.
 */

public abstract class AsynchronousMode<T extends AsyncWrapper> {

    public static final int TYPE_PAYMENT = 0;
    public static final int TYPE_AUTH = 1;
    public static final int TYPE_TSN = 2;
    public static final int TYPE_PROMPT = 3;

    private final String TAG = AsynchronousMode.class.getSimpleName();
    private final String REQUEST_STATUS_COMPLETE = "completed";

    private String mRequestID;

    private Handler mHandler;
    private final int mInterval = 10000;
    private Runnable mScheduledChecker = new Runnable() {
        @Override
        public void run() {
            Log.w(TAG, "CHECKER: check");
            APICallback callback = new APICallback() {
                @Override
                public void onFinish(Result result) {
                    if (result.getCode() == Errors.ERROR_OK) {
                        T wrapper = (T) result.getData();
                        if (wrapper.getRequestStatus().equals(REQUEST_STATUS_COMPLETE)) {
                            processEvent(wrapper);
                            stopChecker();
                        } else {
                            Log.w(TAG, "CHECKER: wait");
                            mHandler.postDelayed(mScheduledChecker, mInterval);
                        }
                    } else {
                        Log.w(TAG, "CHECKER: wait");
                        mHandler.postDelayed(mScheduledChecker, mInterval);
                        //TODO intervenire o attendere la risposta?
                                    /*mCallback.onFailed(result.getCode());
                                    stopChecker();*/
                    }
                }
            };

            switch (getType()){
                case TYPE_AUTH:
                    DevicePos.getInstance().getAuthStatusById(mRequestID,callback);
                    break;
                case TYPE_PAYMENT:
                    DevicePos.getInstance().getPaymentStatusById(mRequestID, callback);
                    break;
                case TYPE_TSN:
                    DevicePos.getInstance().getTsnAsyncStatus(mRequestID, callback);
                    break;
                case TYPE_PROMPT:
                    DevicePos.getInstance().getPromptAsyncStatus(mRequestID, callback);
                default:
                    break;
            }

        }
    };

    public AsynchronousMode() {
        this.mHandler = new Handler();
        this.mRequestID = "init";
    }

    protected void stopChecker(){
        Log.w(TAG, "CHECKER: stop");
        mHandler.removeCallbacks(mScheduledChecker);
    }

    protected void startChecker(){
        Log.w(TAG, "CHECKER: startListener");
        mHandler.postDelayed(mScheduledChecker, mInterval);
    }

    public void kill(){
        mRequestID = null;
        stopChecker();
    }

    public String getRequestID() {
        return mRequestID;
    }

    public void setRequestID(String mRequestID) {
        this.mRequestID = mRequestID;
    }

    protected abstract int getType();

    protected abstract void processEvent(T event);
}
