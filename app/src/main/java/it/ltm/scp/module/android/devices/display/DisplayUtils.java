package it.ltm.scp.module.android.devices.display;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by HW64 on 16/09/2016.
 */
public class DisplayUtils {

    public static String getTemplateFromAssets(Context context, String path) throws IOException {
        AssetManager assetManager = context.getAssets();
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(assetManager.open(path)));
        StringBuilder builder = new StringBuilder();
        String temp;
        while ((temp = reader.readLine()) != null){
            builder.append(temp);
        }
        reader.close();
        return builder.toString();
    }
}
