package it.ltm.scp.module.android.model;

import java.io.Serializable;

public class UploadFormDataPart implements Serializable {
    private static final long serialVersionUID = -4734066805852003142L;
    private String name;
    private String mediaType;

    public UploadFormDataPart(String name) {
        this.name = name;
    }

    public UploadFormDataPart(String name, String mediaType) {
        this.name = name;
        this.mediaType = mediaType;
    }

    public String getName() {
        return name;
    }

    public String getMediaType() {
        return mediaType;
    }
}
