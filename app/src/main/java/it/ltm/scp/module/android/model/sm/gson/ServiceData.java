package it.ltm.scp.module.android.model.sm.gson;

import java.util.Map;

/**
 * Created by HW64 on 19/10/2016.
 */
public class ServiceData {
    private String id = "";
    private String categoryClassName = "";
    private String title = "";
    private String idSGW = "";
    private String iconPath = "";
    private String favourited = "";
    private String categoryId = "";
    private String subCategoryId = "";
    private Map<String, Object> extendibleParams = null;

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getSubCategoryId() {
        return subCategoryId;
    }

    public void setSubCategoryId(String subCategoryId) {
        this.subCategoryId = subCategoryId;
    }

    public String getFavourited() {
        return favourited;
    }

    public void setFavourited(String favourited) {
        this.favourited = favourited;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCategoryClassName() {
        return categoryClassName;
    }

    public void setCategoryClassName(String categoryClassName) {
        this.categoryClassName = categoryClassName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIdSGW() {
        return idSGW;
    }

    public void setIdSGW(String idSGW) {
        this.idSGW = idSGW;
    }

    public String getIconPath() {
        return iconPath;
    }

    public void setIconPath(String iconPath) {
        this.iconPath = iconPath;
    }

    public Map<String, Object> getExtendibleParams() {
        return extendibleParams;
    }

    public void setExtendibleParams(Map<String, Object> extendibleParams) {
        this.extendibleParams = extendibleParams;
    }
}
