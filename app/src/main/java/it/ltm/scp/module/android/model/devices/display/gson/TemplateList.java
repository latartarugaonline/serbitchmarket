package it.ltm.scp.module.android.model.devices.display.gson;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by HW64 on 09/09/2016.
 */
public class TemplateList {

    @SerializedName("templateList")
    @Expose
    private List<String> templateList = new ArrayList<>();

    public List<String> getTemplateList() {
        return templateList;
    }

    public void setTemplateList(List<String> templateList) {
        this.templateList = templateList;
    }
}
