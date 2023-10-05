package it.ltm.scp.module.android.devices.printer;

import android.support.annotation.NonNull;

import it.ltm.scp.module.android.exceptions.InvalidArgumentException;

/**
 * Created by HW64 on 25/08/2016.
 */
interface DocumentBuilder {

    void setEol();

    void setNewLines(int lines);

    void setSize(int size) throws InvalidArgumentException;

    void setText(String text);

    void setFeed(int value, String direction) throws InvalidArgumentException;

    void setCut(boolean part);

    void setFont(String fontType) throws InvalidArgumentException;

    void setFlip(boolean flip);

    void setNegative(boolean negative);

    void setRotate(boolean rotate);

    void setLine(int value) throws InvalidArgumentException;

    void setSpacing(int spacing) throws InvalidArgumentException;

    void setAlign(String align) throws InvalidArgumentException;

    void setBold(boolean bold);

    void setUnderline(int value) throws InvalidArgumentException;

    void tab();

    void setBarcode(@NonNull String code, String bctype, int width, int height, String font, String position, String codeset) throws InvalidArgumentException;

    void setImage(String format, String align, int width, int height, String encoded, int zoom) throws InvalidArgumentException;

    void setQrCode(String code, int model, int errors, int mode, int size) throws InvalidArgumentException;

    void setMatrix(String code) throws InvalidArgumentException;

    void setMaxicode(String code, int mode) throws InvalidArgumentException;

    void setTextForCopies(String text);

    void setRow(String input);

    void setCustomRow(String inputData);

    void setBitmap(@NonNull String format, String align, int width, int height, String encoded,int zoom, int halftone, int mode) throws InvalidArgumentException;



}
