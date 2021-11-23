package it.ltm.scp.module.android.controllers;

import android.content.Context;

/**
 * Created by HW64 on 03/04/2017.
 */

public interface BaseControllerLifecycle {

    public void attach(Context context);
    public void detach(Context context);
    public void destroy();
}
