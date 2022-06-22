package it.ltm.scp.module.android.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.design.BuildConfig;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


import it.ltm.scp.module.android.devices.printer.DocumentBuilderImpl;
import it.ltm.scp.module.android.model.devices.pos.gson.Auth;
import it.ltm.scp.module.android.model.devices.printer.gson.Document;
import it.ltm.scp.module.android.model.devices.system.gson.update.UpdateConfig;
import it.ltm.scp.module.android.ui.BaseDialogActivity;
import it.ltm.scp.module.android.ui.LaunchActivity;

/**
 * Created by HW64 on 24/09/2016.
 */
public class AppUtils {
    private static final String TAG = AppUtils.class.getSimpleName();
    private static final String PREF_AUTH = "pref_a";
    private static final String PREF_CONF = "pref_c";
    private static final String PREF_AUTH_KEY = "a";
    private static final String PREF_REPO_KEY = "r";

    public static final String TERMINAL_IGP = "GT1200-SHPH";
    public static final String TERMINAL_IGP_2 = "IGP2030S";
    public static final String TERMINAL_SUNMI = "T2lite";
    public static final String TERMINAL_SUNMI_LITE = "D2s_LITE_D";
    public static final String TERMINAL_P2_PRO = "P2_PRO";
    public static final String TERMINAL_SUNMI_S = "T2s_LITE";
    public static Document voidDocument;

    static {
        DocumentBuilderImpl builder = new DocumentBuilderImpl();
        builder.setText("");
        voidDocument = builder.build();
        builder.clear();
    }

    public static String getDeviceSerial(){
        String serial = "";
        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class, String.class);
            serial = (String) get.invoke(c, "ril.serialnumber", "unknown");
        } catch (Exception ignored) {}
        return serial;
    }

    public static String getDeviceName(){
        String deviceName = "";
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            deviceName = model;
        } else {
            deviceName = manufacturer + " " + model;
        }
        return deviceName;
    }

    public static boolean isIGP(){
        return Build.MODEL.equals(TERMINAL_IGP) || Build.MODEL.contains(TERMINAL_IGP_2);
    }
 
    public static boolean isSunmi(){
        return Build.MODEL.contains(TERMINAL_SUNMI);
    }

    public static boolean isSunmiLite(){
        return Build.MODEL.equals(TERMINAL_SUNMI_LITE);
    }

    public static boolean isP2Pro(){
        return Build.MODEL.equals(TERMINAL_P2_PRO);
    }

    public static String getDeviceFirmware(){
        return Build.VERSION.RELEASE;
    }

    public static boolean isSunmiS(){
        return Build.MODEL.equals(TERMINAL_SUNMI_S);
    }

    public static String getAppVersion(){return BuildConfig.VERSION_NAME;}

    public static Auth getAuthData(Context context){
        SharedPreferences preferences = context.getSharedPreferences(PREF_AUTH, Context.MODE_PRIVATE);
        String json = preferences.getString(PREF_AUTH_KEY, null);
        Auth auth = new Gson().fromJson(json, Auth.class);
        return auth;
    }

    public static void setAuthData(Context context, Auth data){
        SharedPreferences preferences = context.getSharedPreferences(PREF_AUTH, Context.MODE_PRIVATE);
        String json = new Gson().toJson(data);
        preferences.edit().putString(PREF_AUTH_KEY, json).commit();
    }

    public static void clearAuthData(Context context){
        Log.d(TAG, "clearAuthData: ");
        SharedPreferences preferences = context.getSharedPreferences(PREF_AUTH, Context.MODE_PRIVATE);
        preferences.edit().clear().commit();
    }

    public static boolean isTokenValid(String expiryDate){
        SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyyyyHHmmss", Locale.ITALY);
        try {
            Date end = dateFormat.parse(expiryDate);
            Calendar c = Calendar.getInstance();
            Date start = c.getTime();
            return start.compareTo(end) < 0;
        } catch (ParseException e) {
            Log.e(TAG, "", e);
            return false;
        }
    }

    public static Snackbar getErrorSnackbar(View view, String message){
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_INDEFINITE);
        View snackbarLayout = snackbar.getView();
        snackbarLayout.setBackgroundColor(Color.RED);
        TextView tv=(TextView)snackbarLayout.findViewById(android.support.design.R.id.snackbar_text);
        tv.setTextSize(20);
        tv.setMaxLines(2);
        snackbar.setActionTextColor(Color.WHITE);
        return snackbar;
    }

    public static Snackbar getMessageSnackbar(View view, String message){
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
        View snackbarLayout = snackbar.getView();
        TextView tv=(TextView)snackbarLayout.findViewById(android.support.design.R.id.snackbar_text);
        tv.setTextSize(20);
        tv.setMaxLines(2);
        return snackbar;
    }

    public static String getUnixTimestamp(){
        long timestamp = System.currentTimeMillis();
        return Long.toString(timestamp);
    }

    public static boolean checkAlphanumericString(String input){
        return input.matches("^[\\p{L}\\d\\s|+\\-,./':]*"); //tutti caratteri inclusi accenti

        //vecchio filtro
        /*return input.matches("^[a-zA-Z0-9\\s|+-]{1,"
                + input.length()
                + "}$");*/
    }

    /**
     * Controlla se il barcode in input appartiene alla famiglia di codici:
     * EAN-8 : 7 o 8 cifre
     * EAN-13 : 12 o 13 cifre
     * @param input barcode
     * @return true se appartiene a una delle due famiglie
     */
    public static boolean isBarcodeEAN8or13(String input){
        return input.matches("^([0-9]{8}$)|([0-9]{13}$)");
    }

    public static boolean barcodeValid(String input){
        return AppUtils.checkAlphanumericString(input);
    }

    public static boolean checkCharOnlyString(String input){
        return input.matches("^[a-zA-Z'\\s]{1," + input.length() +
                "}$");
    }

    public static String formatUrlResource(String url){
        if(url.contains("?")){ //tolgo query param
            String[] splitQueryParam = url.split("\\?");
            url = splitQueryParam[0];
        }
        url = url.replace(Properties.get(Constants.PROP_URL_SERVICE_MARKET_BASE), ""); //tolgo base url
        String[] urlPaths = url.split("/");
        url = "";
        for(int i=0; i < urlPaths.length; i++){
            url += urlPaths[i] + "/";
        }
        return url;
    }

    public static String getCurrentDate(){
        long timestamp = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        Date date = new Date(timestamp);
        return sdf.format(date);
    }

    public static void closeAppWithDialog(final BaseDialogActivity activity){
        activity.processAuthStatus("Spegnimento, chiusura applicazione in corso.", false, false);
        Handler mMainHandler = new Handler(Looper.getMainLooper());
        mMainHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                activity.finishAffinity();
            }
        }, 3000);
    }

    public static void restartAppWithDialog(final BaseDialogActivity activity, long duration, String message){
        activity.processAuthStatus(message, false, false);
        Handler mMainHandler = new Handler(Looper.getMainLooper());
        mMainHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent restartApp = new Intent(activity.getApplicationContext(), LaunchActivity.class);
                activity.startActivity(restartApp);
                activity.finishAffinity();
            }
        }, duration);
    }

    public static Document getVoidDocument(){
        return voidDocument;
    }

/*    public static UpdateConfig getUpdateConfig(Context context){
        SharedPreferences preferences = context.getSharedPreferences(PREF_CONF, Context.MODE_PRIVATE);
        String json = preferences.getString(PREF_REPO_KEY, null);
        UpdateConfig config = new Gson().fromJson(json, UpdateConfig.class);
        return config;
    }

    public static void setUpdateConfig(Context context, UpdateConfig config){
        SharedPreferences preferences = context.getSharedPreferences(PREF_CONF, Context.MODE_PRIVATE);
        String json = new Gson().toJson(config);
        preferences.edit().putString(PREF_REPO_KEY, json).commit();
    }

    public static void clearUpdateConfig(Context context){
        SharedPreferences preferences = context.getSharedPreferences(PREF_CONF, Context.MODE_PRIVATE);
        preferences.edit().clear().commit();
    }*/

    public static boolean isGevSellCode(String code) {

        if(!code.matches("^[0-9]*$") || code.isEmpty()){
            return false;
        }

        int[] arr = new int[14];
        int[] multipliers = {173,53,229,71,199,331,107,131,379,151,167,313,223,89};
        int sum = 0;
        int result = 0;

        if(code.length() == 16) {
            for(int i = 0; i < 14 ; ++i)
            {
                arr[i] = Character.getNumericValue(code.charAt(i));
                sum+= arr[i] * multipliers[i];
            }
            sum+=41;
            result = sum % 97;

            return (result == Integer.parseInt(code.substring(14)));
        }
        else return false;
    }

    public static void disableActionMenuButtonFromWebView(WebView webView){
        Method method = null;
        try {
            method = webView.getSettings().getClass().getMethod("setDisabledActionModeMenuItems", int.class);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            return;
        }
        try {
            method.invoke(webView.getSettings(),
                    /*WebSettings.MENU_ITEM_SHARE*/ 1 |
                            /*WebSettings.MENU_ITEM_WEB_SEARCH*/ 2 |
                            /*WebSettings.MENU_ITEM_PROCESS_TEXT*/ 4);
        } catch (IllegalAccessException | InvocationTargetException e) {
            Log.e(TAG, e.getMessage(), e);
        }

    }
}
