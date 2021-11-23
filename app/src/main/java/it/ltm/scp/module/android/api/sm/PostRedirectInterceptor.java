package it.ltm.scp.module.android.api.sm;

import android.util.Log;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class PostRedirectInterceptor implements Interceptor {
    private static final String TAG = "PostRedirectInterceptor";
    private static final String LOCATION_HEADER = "Location";


    @Override
    public Response intercept(Chain chain) throws IOException {

        Request request = chain.request();
        Log.d(TAG, "INTERCEPTOR,  request URL: " + request.url().toString());
        Response response = chain.proceed(request);

        /*
        Funziona solo se viene eseguita una POST ed è attiva una redirect:
        è necessario instradare manualmente la chiamata e ripetere la POST con la nuova location indicata.
        in caso di GET invece, la redirect avviene automaticamente e
        sulla @response il codice è 200 OK con già specificato il nuovo indirizzo
         */
        if(response.code() == 307){

            Log.w(TAG, "intercept: HTTP 307, request being forwarded to new URL");
            String newUrl = response.header(LOCATION_HEADER);
            Log.w(TAG, "intercept: new URL: " + newUrl);
            request = request.newBuilder().url(newUrl).build();
            response = chain.proceed(request);
        }

        return response;
    }
}
