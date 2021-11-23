package it.ltm.scp.module.android.api.display;
import it.ltm.scp.module.android.api.APICallback;
import it.ltm.scp.module.android.api.APIErrorHandler;
import it.ltm.scp.module.android.api.RestAPIModule;
import it.ltm.scp.module.android.model.Result;
import it.ltm.scp.module.android.model.devices.display.gson.DisplayContent;
import it.ltm.scp.module.android.model.devices.display.gson.TemplateList;
import it.ltm.scp.module.android.utils.Errors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by HW64 on 09/09/2016.
 */
public class DisplayAPI extends APIErrorHandler {

    private String TAG = DisplayAPI.class.getSimpleName();

    public void getTemplateList(final APICallback apiCallback){

        //TODO rifare con nuova risposta
        Call<TemplateList> templateListCall = RestAPIModule.getDisplayInstance().getTemplates();
        templateListCall.enqueue(new Callback<TemplateList>() {
            @Override
            public void onResponse(Call<TemplateList> call, Response<TemplateList> response) {
                if(response.isSuccessful()){
                    apiCallback.onFinish(
                            new Result(Errors.ERROR_OK,
                                    response.body()));
                } else {
                    processError(apiCallback, response);
                }
            }

            @Override
            public void onFailure(Call<TemplateList> call, Throwable t) {
                processError(apiCallback, t);
            }
        });
    }

    public void getTemplate(String templateName, final APICallback apiCallback){
        Call<String> stringCall = RestAPIModule.getDisplayInstance().getTemplate(templateName);
        stringCall.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response.isSuccessful()){
                    apiCallback.onFinish(
                            new Result(Errors.ERROR_OK,
                                    response.body()));
                } else {
                    processError(apiCallback, response);
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                processError(apiCallback, t);
            }
        });
    }

    public void sendTemplate(String templateName, String htmlTemplate, final APICallback apiCallback){
        Call<Void> updateTemplateCall = RestAPIModule.getDisplayInstance().sendTemplate(templateName, htmlTemplate);
        updateTemplateCall.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.isSuccessful()){
                    apiCallback.onFinish(new Result(Errors.ERROR_OK));
                } else {
                    processError(apiCallback, response);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                processError(apiCallback, t);
            }
        });
    }

    public void getCss(final APICallback apiCallback){
        Call<String> getCssCall = RestAPIModule.getDisplayInstance().getCss();
        getCssCall.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response.isSuccessful()){
                    apiCallback.onFinish(new Result(Errors.ERROR_OK,
                            response.body()));
                } else {
                    processError(apiCallback, response);
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                processError(apiCallback, t);
            }
        });
    }

    public void setCss(String css, final APICallback apiCallback){
        Call<Void> setCssCall = RestAPIModule.getDisplayInstance().setCss(css);
        setCssCall.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.isSuccessful()){
                    apiCallback.onFinish(new Result(Errors.ERROR_OK));
                } else {
                    processError(apiCallback, response);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                processError(apiCallback, t);
            }
        });
    }

    public void showDisplay(DisplayContent content, final APICallback callback){
        Call<Void> call = RestAPIModule.getDisplayInstance().showDisplay(content);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.isSuccessful()){
                    callback.onFinish(new Result(Errors.ERROR_OK));
                } else {
                    processError(callback, response);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                processError(callback, t);
            }
        });
    }

}
