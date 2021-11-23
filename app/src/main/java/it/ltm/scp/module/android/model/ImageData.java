package it.ltm.scp.module.android.model;

import java.io.Serializable;

public class ImageData implements Serializable {

    private static final long serialVersionUID = -1L;
    private String fileName;
    private String fileContent;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileContent() {
        return fileContent;
    }

    public void setFileContent(String fileContent) {
        this.fileContent = fileContent;
    }

    @Override
    public String toString() {
        return "ImageData [fileName=" + fileName + ", fileContent="
                + fileContent.length() + "]";
    }


}