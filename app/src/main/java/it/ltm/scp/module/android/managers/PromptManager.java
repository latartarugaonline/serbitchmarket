package it.ltm.scp.module.android.managers;

import it.ltm.scp.module.android.api.APICallback;
import it.ltm.scp.module.android.devices.pos.DevicePos;
import it.ltm.scp.module.android.devices.pos.PosUtils;
import it.ltm.scp.module.android.model.Result;
import it.ltm.scp.module.android.model.devices.pos.gson.PosResult;
import it.ltm.scp.module.android.model.devices.pos.prompt.PromptRequest;
import it.ltm.scp.module.android.model.devices.pos.prompt.PromptResponseAsyncWrapper;
import it.ltm.scp.module.android.model.devices.pos.prompt.PromptResponseData;
import it.ltm.scp.module.android.utils.Errors;

/**
 * Created by HW64 on 10/07/2017.
 */

public class PromptManager extends AsynchronousMode<PromptResponseAsyncWrapper> {

    public interface PromptListener {
        void onPromptComplete(String callback, Result result);
    }

    private PromptListener listener;
    private String jsCallback;

    public PromptManager(PromptListener listener) {
        this.listener = listener;
    }

    public void getPromptAsync(final String jsCallback, final PromptRequest request){
        this.jsCallback = jsCallback;
        DevicePos.getInstance().getPromptAsync(request, new APICallback() {
            @Override
            public void onFinish(Result result) {
                if(result.getCode() == Errors.ERROR_OK){
                    PromptResponseAsyncWrapper wrapper = (PromptResponseAsyncWrapper) result.getData();
                    setRequestID(wrapper.getRequestID());
                    startChecker();
                } else {
                    listener.onPromptComplete(jsCallback, result);
                }
            }
        });
    }

    @Override
    protected int getType() {
        return TYPE_PROMPT;
    }

    @Override
    protected void processEvent(PromptResponseAsyncWrapper event) {
        if(event.getRequestID().equals(getRequestID())){
            stopChecker();
            try {
                PosResult<PromptResponseData> promptResult = event.getResponse();
                if (promptResult.getCode() == Errors.ERROR_OK) {
                    PromptResponseData promptData = promptResult.getData();
                    listener.onPromptComplete(jsCallback,
                            new Result(
                                    Errors.ERROR_OK,
                                    promptData
                            ));
                } else {
                    listener.onPromptComplete(jsCallback,
                            new Result(PosUtils.parsePosCode(promptResult.getCode()),
                                    PosUtils.getMessageFromErrorCode(promptResult.getCode()),
                                    null));
                }
            } catch (Exception e) {
                listener.onPromptComplete(jsCallback,
                        new Result(Errors.ERROR_GENERIC,
                                Errors.getMap().get(Errors.ERROR_GENERIC),
                                null));
            }
        }
    }

    public void processPromptEvent(PromptResponseAsyncWrapper wrapper){
        processEvent(wrapper);
    }
}
