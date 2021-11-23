package it.ltm.scp.module.android.managers;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;

import it.ltm.scp.module.android.model.devices.scanner.ImageRequest;

public class PictureSessionManager {
    private static Map<String, ImageRequest> mSessionMap;
    private final static String TAG = PictureSessionManager.class.getSimpleName();

    static {
        mSessionMap = new HashMap<>();
    }

    public static void savePictureInSession(ImageRequest imageRequest){
        Log.d(TAG, "savePictureInSession: picture saved with id:" + imageRequest.getId());
        mSessionMap.put(imageRequest.getId(), imageRequest);
    }

    public static String getPictureFromSession(String id){
        return mSessionMap.get(id).getImgData();
    }

    public static boolean isPictureTakenWithIdcMode(String id){
        return mSessionMap.get(id).isIdcUsed();
    }

    public static boolean hasPicture(String id){ return mSessionMap.containsKey(id); }


    public static void clearSession(){
        mSessionMap.clear();
    }
}