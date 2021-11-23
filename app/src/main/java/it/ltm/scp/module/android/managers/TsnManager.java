package it.ltm.scp.module.android.managers;

import android.util.Log;

import it.ltm.scp.module.android.api.APICallback;
import it.ltm.scp.module.android.devices.pos.DevicePos;
import it.ltm.scp.module.android.devices.pos.PosUtils;
import it.ltm.scp.module.android.model.Result;
import it.ltm.scp.module.android.model.devices.pos.gson.PosResult;
import it.ltm.scp.module.android.model.devices.pos.tsn.TsnAsyncWrapper;
import it.ltm.scp.module.android.model.devices.pos.tsn.TsnDTO;
import it.ltm.scp.module.android.model.devices.pos.tsn.TsnData;
import it.ltm.scp.module.android.utils.Errors;
import it.ltm.scp.module.android.utils.TsnUtils;

/**
 * Created by HW64 on 24/02/2017.
 */

public class TsnManager extends AsynchronousMode<TsnAsyncWrapper> {

    public interface TsnListener {
        void onTsnComplete(String callback, Result result);
    }


    private TsnListener listener;
    private String jsCallback;


    public TsnManager(TsnListener listener) {
        this.listener = listener;
    }

    public void getTsnAsync(final String jsCallback, int timeout, String message, String readType){
        this.jsCallback = jsCallback;
        DevicePos.getInstance().getTsnAsync(timeout, message, readType, new APICallback() {
            @Override
            public void onFinish(Result result) {
                if(result.getCode() == Errors.ERROR_OK){
                    TsnAsyncWrapper wrapper = (TsnAsyncWrapper) result.getData();
                    setRequestID(wrapper.getRequestID());
                    startChecker();
                } else {
                    listener.onTsnComplete(jsCallback, result);
                }
            }
        });
    }

    @Override
    protected int getType() {
        return TYPE_TSN;
    }

    @Override
    protected void processEvent(TsnAsyncWrapper event) {
        if (event.getRequestID().equals(getRequestID())) {
            stopChecker();
            try {
                PosResult<TsnData> tsnResult = event.getResponse();
                if (tsnResult.getCode() == Errors.ERROR_OK) {
                    TsnData tsnData = tsnResult.getData();
                    TsnDTO tsnDTO = TsnUtils.parseTsnData(tsnData.getTsnData());
                    listener.onTsnComplete(jsCallback,
                            new Result(
                                    Errors.ERROR_OK,
                                    tsnDTO
                            ));
                } else {
                    listener.onTsnComplete(jsCallback,
                            new Result(PosUtils.parsePosCode(tsnResult.getCode()),
                                    PosUtils.getMessageFromErrorCode(tsnResult.getCode()),
                                    null));
                }
            } catch (Exception e) {
                Log.e("TsnManager", "processEvent: ", e);
                listener.onTsnComplete(jsCallback,
                        new Result(Errors.ERROR_GENERIC,
                                Errors.getMap().get(Errors.ERROR_GENERIC),
                                null));
            }
        }
    }

    public void processTsnEvent(TsnAsyncWrapper wrapper){
        processEvent(wrapper);
    }
}
