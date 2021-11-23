package it.ltm.scp.module.android.model;

public class BinaryPart extends UploadFormDataPart {

    private String fileName;
    private boolean encrypt;
    private byte[] content;

    public BinaryPart(String name, String mediaType, String fileName, boolean encrypt, byte[] content) {
        super(name, mediaType);
        this.fileName = fileName;
        this.encrypt = encrypt;
        this.content = content;
    }

    public String getFileName() {
        return fileName;
    }

    public boolean isEncrypt() {
        return encrypt;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }
}
