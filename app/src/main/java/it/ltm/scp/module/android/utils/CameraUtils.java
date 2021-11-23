package it.ltm.scp.module.android.utils;

import android.content.Context;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

/**
 * Created by HW64 on 26/07/2017.
 */

public class CameraUtils {
    private static byte[] tempImageBytes;
    private static ArrayList<String> tempImageList = new ArrayList<>();

    public static void saveImageBytes(byte[] bytes){
        tempImageBytes = bytes;
    }

    public static byte[] getAndClearImageBytes(){
        try {
            return tempImageBytes;
        } finally {
            tempImageBytes = null;
        }
    }

    public static void addImageToCache(byte[] bytes){
        String byteToBase64 = Base64.encodeToString(bytes, Base64.NO_WRAP);
        tempImageList.add(byteToBase64);
        Log.d("CameraUtils", "addImageToCache: list size: " + tempImageList.size());
    }

    public static ArrayList<String> getCacheList() {
            return tempImageList;
    }

    public static void clearCache(){
        tempImageList.clear();
//        Runtime.getRuntime().gc();
    }

    /*

    //test only

    public static void saveImageToExternalStorage(String base64){
        File dir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "zebra");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        try {
            File file = new File(dir, String.valueOf(System.currentTimeMillis()) + ".jpg");
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(Base64.decode(base64, Base64.DEFAULT));
            fos.flush();
            fos.close();
        } catch (Exception e){
            Log.e("CameraUtils", "saveImageToExternalStorage: ", e);
        }
    }*/
}
