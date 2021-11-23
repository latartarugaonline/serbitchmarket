package it.ltm.scp.module.android;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import it.ltm.scp.module.android.model.UploadStatusResult;


public class UploadReceiver extends BroadcastReceiver {

    public interface Listener {
        void onUpdateStatus(UploadStatusResult status);
    }

    private final String TAG = this.getClass().getSimpleName();
    private Listener mListener;

    public UploadReceiver(Listener mListener) {
        this.mListener = mListener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive: Intent Action: " + intent.getAction());
        if(intent.getAction().equals(UploadService.FILTER)){
            UploadStatusResult status = (UploadStatusResult) intent.getSerializableExtra(UploadService.INTENT_UPDATE_STATUS);
            if(mListener != null){
                mListener.onUpdateStatus(status);
            }
        }
    }
}
