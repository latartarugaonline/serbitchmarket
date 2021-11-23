package it.ltm.scp.module.android.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStreamReader;

import it.ltm.scp.module.android.App;
import it.ltm.scp.module.android.managers.secure.SecureManager;

/**
 * Created by HW64 on 03/10/2016.
 */
public class Properties {

    private static java.util.Properties properties;
    private static final String TAG = "Properties";



    public static final String  TABLE_CONF = "config";

    public static final String COLUMN_SECTION = "SEZIONE";
    public static final String COLUMN_KEY     = "CHIAVE";
    public static final String COLUMN_VALUE   = "VALORE";

    public static final String COLUMN_SECTION_DEFAULT = "NCS";
    public static final String COLUMN_KEY_DEFAULT     = "AMBIENTE";
    public static final String COLUMN_VALUE_DEFAULT   = "PRODUZIONE";

    private static final String AUTHORITY  = "it.linear.setupTerminale.provider.TerminalContentProvider";

    public static final Uri CONTENT_URI_CONF = Uri.parse("content://" + AUTHORITY + "/" + TABLE_CONF);

    public static void init(Context context) {
        if(properties == null){
            AssetManager assetManager = context.getAssets();
            InputStreamReader reader = null;
            try {
                reader = new InputStreamReader(assetManager.open(Constants.CONFIG_ASSET_PATH), "UTF-8");
                properties = new java.util.Properties();
                properties.load(reader);
            } catch (IOException e) {
                Log.e(TAG, "", e);
            } finally {
                try {
                    reader.close();
                } catch (Exception e) {
                    Log.e(TAG, "", e);
                }
            }
        }
    }

    public static String get(String entry){
        try {
          //todo: Implementare quando pronta release IGP2030s, cambiare anche file properties service_market_base_url con service_market_base_url_prod
            if(entry.equals(Constants.PROP_URL_SERVICE_MARKET_BASE)){
                //URL base verrà fornito dal device tramite un contentProvider specifico. Se il provider non viene trovato verrà utilizzato come riferimento il puntamento di produzione
                return retrieveBaseUrlFromProvider(entry);
            }
            return SecureManager.getInstance().decryptProperty(properties.getProperty(entry));
        } catch (Exception e){
            init(App.getContext());
            return SecureManager.getInstance().decryptProperty(properties.getProperty(entry));
        }
    }

    private static String retrieveBaseUrlFromProvider(String defaultEntry) {
        String selectionClause = COLUMN_SECTION + " = ? AND " + COLUMN_KEY + " = ?";
        String[] selectionArgs = {COLUMN_SECTION_DEFAULT, COLUMN_KEY_DEFAULT};


        String[] projection = { COLUMN_SECTION, COLUMN_KEY, COLUMN_VALUE };

        String res = "";

        Log.d(TAG, "retrieveBaseUrlFromProvider: querying provider..");
        Cursor cursor = App.getContext().getContentResolver().query(CONTENT_URI_CONF, projection, selectionClause, selectionArgs, null);

        try {


            if(cursor.moveToFirst()){
                res = cursor.getString(2);
                Log.d(TAG, "retrieveBaseUrlFromProvider: result found: " + res);
            }

            if ("COLLAUDO".equalsIgnoreCase(res)){
                return SecureManager.getInstance().decryptProperty(properties.getProperty(Constants.PROP_URL_SERVICE_MARKET_BASE_COLLAUDO));
            } else if("SVILUPPO".equalsIgnoreCase(res)){
                return SecureManager.getInstance().decryptProperty(properties.getProperty(Constants.PROP_URL_SERVICE_MARKET_BASE_SVILUPPO));
            } else {

                Log.d(TAG, "retrieveBaseUrlFromProvider: no match with result, returning default value");
                return SecureManager.getInstance().decryptProperty(properties.getProperty(defaultEntry));
            }


        } catch (Exception e){
            Log.e(TAG, "queryCurrentAmbiente: Exception occurred, returning default value");
            Log.e(TAG, "queryCurrentAmbiente: ", e);
            return SecureManager.getInstance().decryptProperty(properties.getProperty(defaultEntry));
        } finally {
            if(cursor != null)
                cursor.close();
        }

    }
}
