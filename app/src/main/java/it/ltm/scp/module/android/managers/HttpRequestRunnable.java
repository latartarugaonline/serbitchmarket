package it.ltm.scp.module.android.managers;

import android.util.Log;
import java.io.IOException;
import java.util.HashMap;

import it.ltm.scp.module.android.model.CustomHttpRequest;
import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by HW64 on 02/05/2017.
 */

public class HttpRequestRunnable implements Runnable {

    private String TAG = HttpRequestRunnable.class.getSimpleName();

    private Request request;
    private OkHttpClient client;
    private int RETRY_MAX;
    private long interval;
    private int count = 0;
    private int jsonSuccessResponseCode = -1;

    public HttpRequestRunnable(CustomHttpRequest request, OkHttpClient client) {
        this.client = client;
        this.interval = request.getRetryInterval();
        this.RETRY_MAX = request.getNumRetry();
        this.jsonSuccessResponseCode = request.getJsonSuccessResponseCode();
        Request.Builder builder = new Request.Builder().url(request.getUrl());

        if(request.getMethod().equals("POST") && request.getFormData() != null){ //FIXME gestire caso body vuoto?
            FormBody.Builder formBodyBuilder = new FormBody.Builder();
            HashMap<String, String> formMap = request.getFormData();
            for (String key :
                    formMap.keySet()) {
                formBodyBuilder.add(key, formMap.get(key));
            }
            builder.post(formBodyBuilder.build());
        } else {
            builder.url(request.getUrl());
            builder.get();
        }
        this.request = builder.build();

    }

    @Override
    public void run() {
        Log.d(TAG, "run count: " + count);
        Call call = client.newCall(request);
        Response response = null;
        if (count < RETRY_MAX){
            try {
                response = call.execute();
                if(!response.isSuccessful()){
                    Log.e(TAG, "run: response KO");
                    retry();
                } else {
                    Log.e(TAG, "run: response OK");
                    //TODO criterio per confrontare json nella response
                    /*if(jsonSuccessResponseCode > -1){
                        String jsonResponse = response.body().string();
                        int firstDoubleQuoteindex = jsonResponse.indexOf("\"");
                        int lastDoubleQuoteIndex = jsonResponse.lastIndexOf("\"");
                        jsonResponse = jsonResponse.substring(firstDoubleQuoteindex + 1,
                                lastDoubleQuoteIndex);
                        jsonResponse = jsonResponse.replace("\\\"", "\"");

                        try {
                            HttpJsonResponse e = new Gson().fromJson(jsonResponse, HttpJsonResponse.class);
                            if(e.getCode() == jsonSuccessResponseCode){
                                return;
                            } else {
                                retry();
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "run: ", e);
                            retry();
                        }
                    }*/
                }
            } catch (IOException e) {
                Log.e(TAG, "run: call failed", e);
                retry();
            } finally {
                try {
                    response.close();
                } catch (NullPointerException e){}
            }
        }
    }

    private void retry(){
        try {
            Log.d(TAG, "run: sleeping");
            Thread.sleep(interval);
        } catch (InterruptedException e) {
            Log.e(TAG, "", e);
        } finally {
            count ++;
            run();
        }
    }
}
