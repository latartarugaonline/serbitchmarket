package it.ltm.scp.module.android.managers;
import android.util.Log;
import android.webkit.CookieManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import it.ltm.scp.module.android.model.CustomHttpRequest;
import it.ltm.scp.module.android.utils.Constants;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;


/**
 * Created by HW64 on 02/05/2017.
 */

public class HttpRequestManager {

    private String TAG = HttpRequestManager.class.getSimpleName();
    private static HttpRequestManager mInstance;

    private ThreadPoolExecutor mPoolExecutor;
    private LinkedBlockingQueue<Runnable> mBlockingQueue;
    private OkHttpClient client;

    private HttpRequestManager() {
        mBlockingQueue = new LinkedBlockingQueue<Runnable>();
        mPoolExecutor = new ThreadPoolExecutor(
                10,
                10,
                60L,
                TimeUnit.SECONDS,
                mBlockingQueue
        );
        client = new OkHttpClient.Builder()
                .cookieJar(new WebViewCookieHandler())
                .connectTimeout(Constants.BCKGRND_TIMEOUT_CONNECTION, TimeUnit.SECONDS)
                .readTimeout(Constants.BCKGRND_TIMEOUT_READ, TimeUnit.SECONDS)
                .writeTimeout(Constants.BCKGRND_TIMEOUT_WRITE, TimeUnit.SECONDS)
                .retryOnConnectionFailure(false)
                .build();
    }

    public static HttpRequestManager getInstance(){
        if (mInstance == null) {
            mInstance = new HttpRequestManager();
        }
        return mInstance;
    }

    public void startTask(CustomHttpRequest request){
        HttpRequestRunnable runnable = new HttpRequestRunnable(request, client);
        mPoolExecutor.execute(runnable);
        Log.d(TAG, "active count: " + mPoolExecutor.getActiveCount()
                + "\nmin thread pool size: " + mPoolExecutor.getCorePoolSize()
                + "\ntot thread pool size: " + mPoolExecutor.getPoolSize()
                + "\ntask count: " + mPoolExecutor.getTaskCount()
                + "\ntask completed: " + mPoolExecutor.getCompletedTaskCount());
    }

    /**
     * sincronizza i cookie della webview con il client okhttp3 per effettuare le richieste
     */
    class WebViewCookieHandler implements CookieJar {
        private CookieManager webviewCookieManager = CookieManager.getInstance();

        @Override
        public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {

        }

        @Override
        public List<Cookie> loadForRequest(HttpUrl url) {
            String urlString = url.toString();
            String cookiesString = webviewCookieManager.getCookie(urlString);

            if (cookiesString != null && !cookiesString.isEmpty()) {
                //We can split on the ';' char as the cookie manager only returns cookies
                //that match the url and haven't expired, so the cookie attributes aren't included
                String[] cookieHeaders = cookiesString.split(";");
                List<Cookie> cookies = new ArrayList<>(cookieHeaders.length);

                for (String header : cookieHeaders) {
                    cookies.add(Cookie.parse(url, header));
                }

                return cookies;
            }

            return Collections.emptyList();
        }
    }
}
