package it.ltm.scp.module.android.devices.display;

import android.content.Context;
import android.util.Log;

import it.ltm.scp.module.android.api.APICallback;
import it.ltm.scp.module.android.api.display.DisplayAPI;
import it.ltm.scp.module.android.model.Result;
import it.ltm.scp.module.android.model.devices.display.gson.DefaultDisplayContent;
import it.ltm.scp.module.android.model.devices.display.gson.DisplayContent;
import it.ltm.scp.module.android.utils.Errors;

import java.io.IOException;

/**
 * Created by HW64 on 09/09/2016.
 */
public class DeviceDisplay {

    // thread safe singleton
    private static DeviceDisplay mInstance;
    private static final String TAG = "DeviceDisplay";

    private DeviceDisplay(){}

    public static synchronized DeviceDisplay getInstance(){
        if(mInstance == null){
            mInstance = new DeviceDisplay();
        }
        return mInstance;
    }

    public void initDefaultTemplate(Context context, APICallback callback){
        try {
            String htmlTemplate = DisplayUtils.getTemplateFromAssets(context, "html/templates/template_default.html");
            sendTemplate("default", htmlTemplate, callback);
        } catch (IOException e) {
            Log.e(TAG, "", e);
            callback.onFinish(new Result(Errors.ERROR_GENERIC,
                    "Impossibile caricare trovare il template di default.",
                    e.getMessage()));
        }

    }

    public void getTemplateList(APICallback callback){
        new DisplayAPI().getTemplateList(callback);
    }

    public void getTemplate(String templateName, APICallback callback){
        new DisplayAPI().getTemplate(templateName, callback);
    }

    public void sendTemplate(String templateName, String htmlTemplate, APICallback callback){
        new DisplayAPI().sendTemplate(templateName, htmlTemplate, callback);
    }

    public void getCss(APICallback callback){
        new DisplayAPI().getCss(callback);
    }

    public void setCss(String css, APICallback callback){
        new DisplayAPI().setCss(css, callback);
    }

    public void showDisplay(String templateName, String html, APICallback callback){
        DisplayContent displayContent = new DisplayContent();
        DefaultDisplayContent defaultDisplayContent = new DefaultDisplayContent();
        defaultDisplayContent.setLines(html);
        displayContent.setTemplate(templateName);
        displayContent.setData(defaultDisplayContent);
        new DisplayAPI().showDisplay(displayContent, callback);
    }

    public void showDisplay(String templateName, String[] lines, APICallback callback){
        String content = processLines(lines);
        DisplayContent displayContent = new DisplayContent();
        DefaultDisplayContent defaultDisplayContent = new DefaultDisplayContent();
        defaultDisplayContent.setLines(content);
        displayContent.setTemplate(templateName);
        displayContent.setData(defaultDisplayContent);
        new DisplayAPI().showDisplay(displayContent, callback);
    }

    private String processLines(String[] lines) {
        String content = "";
        for (int i = 0; i < lines.length; i++){
            content += "<div>" + lines[i] + "</div>";
        }
        return content;
    }
}
