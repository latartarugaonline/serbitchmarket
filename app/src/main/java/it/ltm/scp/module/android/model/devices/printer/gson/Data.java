package it.ltm.scp.module.android.model.devices.printer.gson;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import okhttp3.OkHttpClient;

/**
 * Created by HW64 on 23/08/2016.
 */
public class Data {


    @SerializedName("op")
    @Expose
    private String op;
    @SerializedName("value")
    @Expose
    private Object value;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("option")
    @Expose
    private String option;
    @SerializedName("part")
    @Expose
    private Boolean part;
    @SerializedName("format")
    @Expose
    private String format;
    @SerializedName("align")
    @Expose
    private String align;
    @SerializedName("width")
    @Expose
    private Integer width;
    @SerializedName("height")
    @Expose
    private Integer height;
    @SerializedName("encoded")
    @Expose
    private String encoded;
    @SerializedName("code")
    @Expose
    private String code;
    @SerializedName("bctype")
    @Expose
    private String bctype;
    @SerializedName("font")
    @Expose
    private String font;
    @SerializedName("position")
    @Expose
    private String position;
    @SerializedName("model")
    @Expose
    private Integer model;
    @SerializedName("errors")
    @Expose
    private Integer errors;
    @SerializedName("mode")
    @Expose
    private Integer mode;
    @SerializedName("size")
    @Expose
    private Integer size;
    @SerializedName("direction")
    @Expose
    private String direction;
    @SerializedName("codeset")
    private String codeset;
    @SerializedName("zoom")
    private Integer zoom;
    @SerializedName("halftone")
    private Integer halftone;


    //public keys

    // op
    public static final String OP_STYLE = "style";
    public static final String OP_CMD = "cmd";
    public static final String OP_TEXT = "text";

    // type
    public static final String TYPE_CUT = "cut";
    public static final String TYPE_EOL = "eol";
    public static final String TYPE_FEED = "feed";
    public static final String TYPE_TAB = "tab";
    public static final String TYPE_IMAGE = "image";
    public static final String TYPE_BARCODE = "barcode";
    public static final String TYPE_QRCODE = "qrcode";
    public static final String TYPE_MATRIX = "matrix";
    public static final String TYPE_MAXICODE = "maxicode";
    public static final String TYPE_ROW = "row";
    public static final String TYPE_BITMAP = "bitmap";

    // option
    public static final String OPTION_NEGATIVE = "negative";
    public static final String OPTION_FLIP = "flip";
    public static final String OPTION_ROTATE = "rotate";
    public static final String OPTION_LINE = "line";
    public static final String OPTION_SPACE = "space";
    public static final String OPTION_ALIGN = "align";
    public static final String OPTION_FONT = "font";
    public static final String OPTION_BOLD = "bold";
    public static final String OPTION_UNDERLINE = "underline";
    public static final String OPTION_SIZE = "size";


    /**
     * Costruttore con il Builder passato come parametro
     * @param builder
     */
    public Data(Builder builder) {
        this.op = builder._op;
        this.value = builder._value;
        this.type = builder._type;
        this.option = builder._option;
        this.part = builder._part;
        this.format = builder._format;
        this.align = builder._align;
        this.width = builder._width;
        this.height = builder._height;
        this.encoded = builder._encoded;
        this.code = builder._code;
        this.bctype = builder._bctype;
        this.font = builder._font;
        this.position = builder._position;
        this.model = builder._model;
        this.errors = builder._errors;
        this.mode = builder._mode;
        this.size = builder._size;
        this.direction = builder._direction;
        this.codeset = builder._codeset;
        this.zoom = builder._zoom;
        this.halftone = builder._halftone;
    }

    /*
    Getters and Setters
     */

    public Integer getZoom() {
        return zoom;
    }

    public void setZoom(Integer zoom) {
        this.zoom = zoom;
    }

    public String getOption() {
        return option;
    }

    public void setOption(String option) {
        this.option = option;
    }

    public Boolean isPart() {
        return part;
    }

    public void setPart(Boolean part) {
        this.part = part;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getAlign() {
        return align;
    }

    public void setAlign(String align) {
        this.align = align;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public String getEncoded() {
        return encoded;
    }

    public void setEncoded(String encoded) {
        this.encoded = encoded;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getBctype() {
        return bctype;
    }

    public void setBctype(String bctype) {
        this.bctype = bctype;
    }

    public String getFont() {
        return font;
    }

    public void setFont(String font) {
        this.font = font;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public Integer getModel() {
        return model;
    }

    public void setModel(Integer model) {
        this.model = model;
    }

    public Integer getErrors() {
        return errors;
    }

    public void setErrors(Integer errors) {
        this.errors = errors;
    }

    public Integer getMode() {
        return mode;
    }

    public void setMode(Integer mode) {
        this.mode = mode;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getOp() {
        return op;
    }

    public void setOp(String op) {
        this.op = op;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCodeset() {
        return codeset;
    }

    public void setCodeset(String codeset) {
        this.codeset = codeset;
    }

    public Integer getHalftone() {
        return halftone;
    }

    public void setHalftone(Integer halftone) {
        this.halftone = halftone;
    }


    /**
     * Builder class
     */
    public static class Builder {
        private String _op;
        private Object _value;
        private String _type;
        private String _option;
        private Boolean _part;
        private String _format;
        private String _align;
        private Integer _width;
        private Integer _height;
        private String _encoded;
        private String _code;
        private String _bctype;
        private String _font;
        private String _position;
        private Integer _model;
        private Integer _errors;
        private Integer _mode;
        private Integer _size;
        private String _direction;
        private String _codeset;
        private Integer _zoom;
        private Integer _halftone;


        public Builder halftone(Integer _halftone) {
            this._halftone = _halftone;
            return this;
        }
        public Builder op(String _op) {
            this._op = _op;
            return this;
        }

        public Builder zoom(Integer _zoom){
            this._zoom = _zoom;
            return this;
        }

        public Builder value(Object _value) {
            this._value = _value;
            return this;
        }

        public Builder part(Boolean _part){
            this._part = _part;
            return this;
        }

        public Builder option(String _option) {
            this._option = _option;
            return this;
        }

        public Builder type(String _type) {
            this._type = _type;
            return this;
        }

        public Builder format(String _format){
            this._format = _format;
            return this;
        }

        public Builder align(String _align){
            this._align = _align;
            return this;
        }

        public Builder width(Integer _width){
            this._width = _width;
            return this;
        }

        public Builder height(Integer _height){
            this._height = _height;
            return this;
        }

        public Builder encoded(String _encoded){
            this._encoded = _encoded;
            return this;
        }

        public Builder code(String _code){
            this._code = _code;
            return this;
        }
        public Builder bctype(String _bctype){
            this._bctype = _bctype;
            return this;
        }
        public Builder font(String _font){
            this._font = _font;
            return this;
        }
        public Builder position(String _position){
            this._position = _position;
            return this;
        }

        public Builder model(int _model){
            this._model = _model;
            return this;
        }

        public Builder errors(Integer _errors){
            this._errors = _errors;
            return this;
        }

        public Builder mode(Integer _mode){
            this._mode = _mode;
            return this;
        }

        public Builder size(Integer _size){
            this._size = _size;
            return this;
        }

        public Builder direction(String _direction){
            this._direction = _direction;
            return this;
        }

        public Builder codeset(String _codeset){
            this._codeset = _codeset;
            return this;
        }


        public Data build(){
            return new Data(this);
        }
    }
}
