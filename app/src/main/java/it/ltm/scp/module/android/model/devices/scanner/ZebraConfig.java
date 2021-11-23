package it.ltm.scp.module.android.model.devices.scanner;

public class ZebraConfig {

    /*
    IDC related
     */

    //fields
    private String IDCMode;
    private String IDCBorder;
    private boolean IDCBrighten;
    private boolean IDCSharpen;
    private boolean code39; //disabilitare per scansioni nuove C.I. che presentano codice a barre sul retro

    //constants
    public static final String IDC_MODE_FREE_FORM = "2";
    public static final String IDC_MODE_OFF = "0";

    public static final String IDC_BORDER_AED = "3";
    public static final String IDC_BORDER_NONE = "0";
    /*
    Image capture related
     */
    private String jpegQualityValue; //def 80
    private String imageBrightness; //def 180
    private String imageEnhancement; //def 1
    private String videoRotation; //def 0
    private String snapshotTimeout; //def 255 (no timeout)


    /*
    Methods
     */
    public static ZebraConfig defaultIDCConfig(){
        return new ZebraConfig.Builder()
                .IDCMode(IDC_MODE_FREE_FORM)
                .IDCBorder(IDC_BORDER_AED)
                .IDCBrighten(false)
                .IDCSharpen(true)
                .code39(false)
                .build();
    }




    /*
    GETTER & SETTER
     */

    public String getIDCMode() {
        return IDCMode;
    }

    public void setIDCMode(String IDCMode) {
        this.IDCMode = IDCMode;
    }

    public String getIDCBorder() {
        return IDCBorder;
    }

    public void setIDCBorder(String IDCBorder) {
        this.IDCBorder = IDCBorder;
    }

    public boolean isIDCBrighten() {
        return IDCBrighten;
    }

    public void setIDCBrighten(boolean IDCBrighten) {
        this.IDCBrighten = IDCBrighten;
    }

    public boolean isIDCSharpen() {
        return IDCSharpen;
    }

    public void setIDCSharpen(boolean IDCSharpen) {
        this.IDCSharpen = IDCSharpen;
    }

    public String getJpegQualityValue() {
        return jpegQualityValue;
    }

    public void setJpegQualityValue(String jpegQualityValue) {
        this.jpegQualityValue = jpegQualityValue;
    }

    public String getImageBrightness() {
        return imageBrightness;
    }

    public void setImageBrightness(String imageBrightness) {
        this.imageBrightness = imageBrightness;
    }

    public String getImageEnhancement() {
        return imageEnhancement;
    }

    public void setImageEnhancement(String imageEnhancement) {
        this.imageEnhancement = imageEnhancement;
    }

    public String getVideoRotation() {
        return videoRotation;
    }

    public void setVideoRotation(String videoRotation) {
        this.videoRotation = videoRotation;
    }

    public String getSnapshotTimeout() {
        return snapshotTimeout;
    }

    public void setSnapshotTimeout(String snapshotTimeout) {
        this.snapshotTimeout = snapshotTimeout;
    }

    public boolean isCode39() {
        return code39;
    }

    public void setCode39(boolean code39) {
        this.code39 = code39;
    }
 /*
    Builder pattern
     */

    public ZebraConfig(Builder builder){
        this.IDCMode = builder._IDCMode;
        this.IDCBorder = builder._IDCBorder;
        this.IDCBrighten = builder._IDCBrighten;
        this.IDCSharpen = builder._IDCSharpen;
        this.jpegQualityValue = builder._jpegQualityValue;
        this.imageBrightness = builder._imageBrightness;
        this.imageEnhancement = builder._imageEnhancement;
        this.videoRotation = builder._videoRotation;
        this.snapshotTimeout = builder._snapshotTimeout;
        this.code39 = builder._code39;
    }


    public static class Builder {

        private String _IDCMode;
        private String _IDCBorder;
        private boolean _IDCBrighten;
        private boolean _IDCSharpen;
        private boolean _code39;


        private String _jpegQualityValue; //def 80
        private String _imageBrightness; //def 180
        private String _imageEnhancement; //def 1
        private String _videoRotation; //def 0
        private String _snapshotTimeout;

        public ZebraConfig build(){
            return new ZebraConfig(this);
        }


        public Builder IDCMode(String _IDCMode) {
            this._IDCMode = _IDCMode;
            return this;
        }

        public Builder IDCBorder(String _IDCBorder) {
            this._IDCBorder = _IDCBorder;
            return this;
        }

        public Builder IDCBrighten(boolean _IDCBrighten) {
            this._IDCBrighten = _IDCBrighten;
            return this;
        }

        public Builder IDCSharpen(boolean _IDCSharpen) {
            this._IDCSharpen = _IDCSharpen;
            return this;
        }

        public Builder jpegQualityValue(String _jpegQualityValue) {
            this._jpegQualityValue = _jpegQualityValue;
            return this;
        }

        public Builder imageBrightness(String _imageBrightness) {
            this._imageBrightness = _imageBrightness;
            return this;
        }

        public Builder imageEnhancement(String _imageEnhancement) {
            this._imageEnhancement = _imageEnhancement;
            return this;
        }

        public Builder videoRotation(String _videoRotation) {
            this._videoRotation = _videoRotation;
            return this;
        }

        public Builder snapshotTimeout(String _snapshotTimeout) {
            this._snapshotTimeout = _snapshotTimeout;
            return this;
        }

        public Builder code39(boolean enabled) {
            this._code39 = enabled;
            return this;
        }

    }
}
