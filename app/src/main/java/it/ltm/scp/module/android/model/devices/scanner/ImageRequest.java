package it.ltm.scp.module.android.model.devices.scanner;

import java.io.Serializable;

public class ImageRequest implements Serializable, Cloneable {


    private static final long serialVersionUID = 3846392958678192490L;
    String id;
    String descriptionLabel;
    String imgData;
    boolean idcUsed = false;

    @Override
    public Object clone() {
        try {
            return (ImageRequest)super.clone();
        } catch (CloneNotSupportedException e) {
            ImageRequest clone = new ImageRequest();
            clone.setId(this.id);
            clone.setDescriptionLabel(this.descriptionLabel);
            clone.setIdcUsed(this.idcUsed);
            //ignore imgData
            return (ImageRequest)clone;
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescriptionLabel() {
        return descriptionLabel;
    }

    public void setDescriptionLabel(String descriptionLabel) {
        this.descriptionLabel = descriptionLabel;
    }

    public String getImgData() {
        return imgData;
    }

    public void setImgData(String imgData) {
        this.imgData = imgData;
    }

    public boolean isIdcUsed() {
        return idcUsed;
    }

    public void setIdcUsed(boolean idcUsed) {
        this.idcUsed = idcUsed;
    }
}
