package it.ltm.scp.module.android.devices.printer;

import android.support.annotation.NonNull;

import it.ltm.scp.module.android.exceptions.InvalidArgumentException;

/**
 * Created by HW64 on 07/09/2016.
 * Classe per la validazione dei parametri in input nel DocumentBuilder.
 *
 * @see DocumentBuilderImpl
 *
 */
public class DocumentBuilderHelper implements DocumentBuilder {

    @Override
    public void setEol() {

    }

    @Override
    public void setNewLines(int lines) {

    }
    @Override
    public void setSize(int size) throws InvalidArgumentException {
        if(size < 0 || size > 7)
            throw new InvalidArgumentException("Invalid setSize param: " +
                    String.valueOf(size) +
                    ". Param must be between " +
                    "0-7 interval.");

    }

    @Override
    public void setText(String text) {

    }

    @Override
    public void setFeed(int value, String direction) throws InvalidArgumentException {
        if(value < 1 || value > 255)
            throw new InvalidArgumentException("Invalid setFeed param: " +
                    String.valueOf(value) +
                    ". Param must be between " +
                    "1-255 interval.");
        if(!direction.matches("f|b"))
            throw new InvalidArgumentException("Invalid setFeed param: " +
                    direction +
                    ". Param must be \"f\" or \"b\", default is \"f\". ");
    }

    @Override
    public void setCut(boolean part) {

    }

    @Override
    public void setFont(String fontType) throws InvalidArgumentException {
        if(!fontType.matches("a|b")){
            throw new InvalidArgumentException("Invalid setFont param: " +
                    fontType +
                    ". Param must be \"a\" or \"b\" ");
        }
    }

    @Override
    public void setFlip(boolean flip) {

    }

    @Override
    public void setNegative(boolean negative) {

    }

    @Override
    public void setRotate(boolean rotate) {

    }

    @Override
    public void setLine(int value) throws InvalidArgumentException {
        if(value < 0 || value > 127){
            throw new InvalidArgumentException("Invalid setLine param: " +
                    String.valueOf(value) +
                    ". Param must be between " +
                    "0-127 interval.");
        }
    }

    @Override
    public void setSpacing(int spacing) throws InvalidArgumentException {
        if(spacing < 0 || spacing > 255)
            throw new InvalidArgumentException("Invalid setSpacing param: " +
                    String.valueOf(spacing) +
                    ". Param must be between " +
                    "0-255 interval.");
    }

    @Override
    public void setAlign(String align) throws InvalidArgumentException {
        if(!align.matches("left|center|right")){
            throw new InvalidArgumentException("Invalid setAlign param: " +
                    align +
                    ". Param must be \"left\"," +
                    " \"center\", \"right\"");
        }
    }

    @Override
    public void setBold(boolean bold) {

    }

    @Override
    public void setUnderline(int value) throws InvalidArgumentException {
        if(value < 0 || value > 2)
            throw new InvalidArgumentException("Invalid setUnderline param: " +
                    String.valueOf(value) +
                    ". Param must be between " +
                    "0-2 interval.");
    }

    @Override
    public void tab() {    }

    @Override
    public void setBarcode(@NonNull String code, String bctype, int width, int height, String font, String position, String codeset) throws InvalidArgumentException {
        // check code param
        /*if(code.length() != 8
                || !code.matches("[0-9]+")){
            throw new InvalidArgumentException("Invalid setBarcode param: " +
                    code + ". Value must be a 8 numbers string.");
        }*/

        //check bctype param
        if(!bctype.matches("EAN8|EAN13|UPC-A|UPC-E|CODE39|ITF|CODABAR|CODE128"))
            throw new InvalidArgumentException("Invalid setBarcode param: " +
                    bctype + ". Value must be: EAN8 (default) | EAN13 | " +
                    "UPC-A | UPC-E | CODE39 | ITF | CODABAR.");

        //check width param
        if(width < 1 || width > 6)
            throw new InvalidArgumentException("Invalid setBarcode param: " +
                    String.valueOf(width) + ". Value must be between 1-6.");

        //check height param
        if(height < 1 || height > 12)
            throw new InvalidArgumentException("Invalid setBarcode param: " +
                    String.valueOf(height) + ". Value must be between 1-12.");

        //check font param
        if(!font.matches("a|b"))
            throw new InvalidArgumentException("Invalid setBarcode param: " +
                    font + ". Value must be \"a\" or \"b\".");

        //check position param
        if(!position.matches("off|above|below|both"))
            throw new InvalidArgumentException("Invalid setBarcode param: " +
                    position + ". Value must be: off | above | below | both.");
    }

    @Override
    public void setImage(String format, String align, int width, int height, String encoded, int zoom) throws InvalidArgumentException {

        //check width param
        if(width < 1 || width > 576)
            throw new InvalidArgumentException("Invalid setImage width param: " +
                    String.valueOf(width) + ". Value must be between 1-576.");

        //check height param
        if(height < 1 || height > 576)
            throw new InvalidArgumentException("Invalid setImage height param: " +
                    String.valueOf(height) + ". Value must be between 1-576.");

        //check format param
        if(!format.matches("normal|double|2width|2height"))
            throw new InvalidArgumentException("Invalid setImage param: " +
                    format + ". Value must be: normal | double | 2width | 2height.");

        //check align param
        if(!align.matches("left|center|right"))
            throw new InvalidArgumentException("Invalid setImage param: " +
                    align + ". Value must be: left | center | right.");

        // stampante iPOS gestisce autonomamente valori fuori scala
        /*if(zoom < 1 || zoom > 16)
            throw new InvalidArgumentException("Invalid setImage width param: " +
                    String.valueOf(zoom) + ". Value must be between 1-16.");*/
    }

    @Override
    public void setQrCode(String code, int model, int errors, int mode, int size) throws InvalidArgumentException {
        if(code.length() > 256)
            throw new InvalidArgumentException("Invalid setQrCode code param: " +
                    code + ". Value must be max 256 alphanumeric characters.");

        if(model < 1 || model > 2)
            throw new InvalidArgumentException("Invalid setQrCode model param: " +
                    String.valueOf(model) + ". Value must be 1 or 2.");

        if(errors < 0 || errors > 3)
            throw new InvalidArgumentException("Invalid setQrCode errors param: " +
                    String.valueOf(errors) + ". Value must be between 0-3.");

        if(mode < 1 || mode > 5)
            throw new InvalidArgumentException("Invalid setQrCode mode param: " +
                    String.valueOf(mode) + ". Value must be between 1-5.");

        if(size < 1 || size > 10)
            throw new InvalidArgumentException("Invalid setQrCode size param: " +
                    String.valueOf(size) + ". Value must be between 1-10.");
    }

    @Override
    public void setMatrix(String code) throws InvalidArgumentException {
        if(code.length() > 256)
            throw new InvalidArgumentException("Invalid setMatrix code param: " +
                    code + ". Value must be max 256 alphanumeric characters.");
    }

    @Override
    public void setMaxicode(String code, int mode) throws InvalidArgumentException {
        if(code.length() > 256)
            throw new InvalidArgumentException("Invalid setMaxicode code param: " +
                    code + ". Value must be max 256 alphanumeric characters.");

        if(mode < 1 || mode > 2)
            throw new InvalidArgumentException("Invalid setMaxicode mode param: " +
                    String.valueOf(mode) + ". Value must be 1 or 2.");
    }

    @Override
    public void setTextForCopies(String text) {

    }

    @Override
    public void setRow(String input) {

    }

    @Override
    public void setCustomRow(String inputData) {

    }
}
