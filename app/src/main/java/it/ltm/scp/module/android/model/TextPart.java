package it.ltm.scp.module.android.model;

public class TextPart extends UploadFormDataPart {

    private String value;


    public TextPart(String name, String value) {
        super(name);
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
