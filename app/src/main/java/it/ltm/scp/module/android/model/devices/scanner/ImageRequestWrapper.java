package it.ltm.scp.module.android.model.devices.scanner;

import java.io.Serializable;
import java.util.ArrayList;

public class ImageRequestWrapper implements Serializable{

    private static final long serialVersionUID = -4599890602711553463L;
    ArrayList<ImageRequest> imageRequestList;
    String jsCallbackName;


    public ImageRequestWrapper(ArrayList<ImageRequest> imageRequestList, String jsCallbackName) {
        this.imageRequestList = imageRequestList;
        this.jsCallbackName = jsCallbackName;
    }

    public ArrayList<ImageRequest> getImageRequestList() {
        return imageRequestList;
    }

    public void setImageRequestList(ArrayList<ImageRequest> imageRequestList) {
        this.imageRequestList = imageRequestList;
    }

    public String getJsCallbackName() {
        return jsCallbackName;
    }

    public void setJsCallbackName(String jsCallbackName) {
        this.jsCallbackName = jsCallbackName;
    }
}
