package it.ltm.scp.module.android.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import java.io.File;

/**
 * Created by HW64 on 01/02/2017.
 */

public class RootUtils {
    private final static String TAG = RootUtils.class.getSimpleName();

    private static boolean checkRootMethod2(Context context) {
        return isPackageInstalled("eu.chainfire.supersu", context);
    }

    private static boolean isPackageInstalled(String packagename, Context context) {
        PackageManager pm = context.getPackageManager();
        try {
            pm.getPackageInfo(packagename, PackageManager.GET_ACTIVITIES);
            Log.w(TAG, "checkRootMethod3:\"su\" package found!");
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    private static boolean checkRootMethod3(){
        for(String pathDir : System.getenv("PATH").split(":")){
            if(new File(pathDir, "su").exists()) {
                Log.w(TAG, "checkRootMethod3:\"su\" binary found at: " + pathDir);
                return true;
            }
        }
        return false;
    }

    private static boolean checkRootMethod1() {
        String buildTags = android.os.Build.TAGS;
        return buildTags != null && buildTags.contains("test-keys");
    }

    public static boolean isDeviceRooted(Context context) {
        if(AppUtils.isIGP()){ //TODO bypass sicurezza IGP2030S
            Log.w(TAG, "isDeviceRooted: IGP detected, bypass root checks..");
            return false;
        }
        return checkRootMethod1() || checkRootMethod3() || checkRootMethod2(context);
    }
}
