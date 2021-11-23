package it.ltm.scp.module.android.utils;

import android.app.Activity;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

/**
 * Created by HW64 on 20/12/2016.
 *
 * Workaround for resizing the main WebView when a softKeyboard shows up,
 * as with the FULLSCREEN param set in the MainActivity it is not possible by default.
 *
 * For more information,
 * @link https://code.google.com/p/android/issues/detail?id=5497
 *
 * To use this class, simply invoke assistActivity() on an Activity that already has its content view set.
 */

public class AndroidBug5497Workaround {

    public static void assistActivity (Activity activity) {
        new AndroidBug5497Workaround(activity);
    }

    private View mWebView;
    private int webViewPreviousVisibleHeight;
    private FrameLayout.LayoutParams mWebViewLayoutParams;

    private AndroidBug5497Workaround(Activity activity) {
        FrameLayout content = (FrameLayout) activity.findViewById(android.R.id.content);
        mWebView = content.getChildAt(0);
        mWebView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                possiblyResizeChildOfContent();
            }
        });
        mWebViewLayoutParams = (FrameLayout.LayoutParams) mWebView.getLayoutParams();
    }

    private void possiblyResizeChildOfContent() {
        int webViewCurrentVisibleHeight = computeUsableHeight();
        if (webViewCurrentVisibleHeight != webViewPreviousVisibleHeight) {
            int totalHeightAvaible = mWebView.getRootView().getHeight();
            int heightDifference = totalHeightAvaible - webViewCurrentVisibleHeight;
            if (heightDifference > (totalHeightAvaible/4)) {
                // keyboard probably just became visible, reduce webView
                mWebViewLayoutParams.height = totalHeightAvaible - heightDifference;
            } else {
                // keyboard probably just became hidden, restore webView height
                mWebViewLayoutParams.height = totalHeightAvaible;
            }
            mWebView.requestLayout();
            webViewPreviousVisibleHeight = webViewCurrentVisibleHeight;
        }
    }

    private int computeUsableHeight() {
        Rect r = new Rect();
        mWebView.getWindowVisibleDisplayFrame(r);
        return (r.bottom);
    }

}
