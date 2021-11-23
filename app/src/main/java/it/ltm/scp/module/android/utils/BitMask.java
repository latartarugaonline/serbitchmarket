package it.ltm.scp.module.android.utils;

import android.util.Log;

import java.io.Serializable;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;

import it.ltm.scp.module.android.devices.printer.DocumentBuilderImpl;
import it.ltm.scp.module.android.exceptions.InvalidArgumentException;
import it.ltm.scp.module.android.js.JsMainInterface;

/**
 * Created by panstudio on 27/09/16.
 */
public class BitMask implements Serializable {

    private int size;
    private boolean[][] bool;
    private String [] originalValue;
    private String [] originalOption;
    private DocumentBuilderImpl builder;


    public BitMask(int size, DocumentBuilderImpl builder) {
        this.size = size;
        this.bool = new boolean[size][size];
        this.originalValue = new String[size];
        this.originalOption = new String[size];
        for(int i = 0; i<size; i++) {
            for (int j = 0; j < size; j++) {
                this.bool[i][j] = false;
            }
            this.originalValue[i] = "";
            this.originalOption[i] = "";
        }
        this.builder = builder;

        //to change with default configuration
        this.originalOption[0] = "direction";
        this.originalValue[0] = "right";
        this.originalOption[1] = "negative";
        this.originalValue[1] = "false";
        this.originalOption[2] = "flip";
        this.originalValue[3] = "false";
        this.originalOption[4] = "rotate";
        this.originalValue[4] = "false";
        this.originalOption[5] = "strike";
        this.originalValue[5] = "false";
        this.originalOption[6] = "line";
        this.originalValue[6] = "0";
        this.originalOption[7] = "align";
        this.originalValue[7] = "left";
        this.originalOption[8] = "font";
        this.originalValue[8] = "a";
        this.originalOption[9] = "bold";
        this.originalValue[9] = "false";
        this.originalOption[10] = "underline";
        this.originalValue[10] = "0";
        this.originalOption[11] = "size";
        this.originalValue[11] = "0";
    }


    public void setOriginal(int index, boolean val) {
        if (index < 0)
            throw new IndexOutOfBoundsException("bitIndex < 0: " + index);
        else if (index > size)
            throw new IndexOutOfBoundsException("bitIndex > size " + index);
            this.bool[index][0] = val;

    }

    public void setOriginalValue(int index, String val) {
        if (index < 0)
            throw new IndexOutOfBoundsException("bitIndex < 0: " + index);
        else if (index > size)
            throw new IndexOutOfBoundsException("bitIndex > size " + index);
        this.originalValue[index] = val;

    }

    public void setOriginalOption(int index, String val) {
        if (index < 0)
            throw new IndexOutOfBoundsException("bitIndex < 0: " + index);
        else if (index > size)
            throw new IndexOutOfBoundsException("bitIndex > size " + index);
        this.originalOption[index] = val;

    }


    public void setEdited(int index, boolean val) {
        if (index < 0)
            throw new IndexOutOfBoundsException("bitIndex < 0: " + index);
        else if (index > size)
            throw new IndexOutOfBoundsException("bitIndex > size " + index);
        this.bool[index][1] = val;
    }


    public void setEdited(String option, boolean val) {
       for(int i = 0; i<this.originalOption.length; i++)
           if(this.originalOption[i] == option)
                this.bool[i][1] = val;
    }

    public boolean getOriginalAt(int index) {
        return this.bool[index][0];
    }

    public boolean getEditedAt(int index) {
        return this.bool[index][1];
    }






    public boolean and() {
        boolean result = false;
        for (int i=0; i<bool.length-1; i++)
            if (bool[i][0] && bool[i][1])
                result = true;
            else {
                result = false;
                break;
            }
        return result;
    }


    public void appendRestoreInstruction() throws InvalidArgumentException {

        for(int i = 0; i<this.size; i++) {

            if (!(bool[i][0] && bool[i][1])) {
                switch (originalOption[i]) {
                    case "direction":
                        builder.setAlign(originalValue[i]);

                        break;

                    case "negative":
                        builder.setNegative(Boolean.parseBoolean(originalValue[i]));
                        break;

                    case "flip":
                        builder.setFlip(Boolean.parseBoolean(originalValue[i]));
                        break;

                    case "rotate":
                        builder.setRotate(Boolean.parseBoolean(originalValue[i]));
                        break;

                    case "strike":

                        break;

                    case "line":
                        builder.setLine(Integer.parseInt(originalValue[i]));
                        break;

                    case "align":
                        builder.setAlign(originalValue[i]);
                        break;

                    case "font":
                        builder.setFont(originalValue[i]);
                        break;

                    case "bold":
                        builder.setBold(Boolean.parseBoolean(originalValue[i]));
                        break;

                    case "underline":
                        builder.setUnderline(Integer.parseInt(originalValue[i]));
                        break;

                    case "size":
                        builder.setSize(Integer.parseInt(originalValue[i]));
                        break;
                }
                setEdited(i, false);
            }
        }
    }
   /* public Integer returnChangedIntex(int startPos) {
        boolean result = false;
        for (int i=startPos; i<bool.length-1; i++)
            if (!bool[i][0] && bool[i][1])
                 return i;

        return -1;
    }

    public Vector<Integer> changedIndex() {
        Vector<Integer> v = new Vector<Integer>();
        for(int i = 0; i<getSize(); i++ ) {
            int indexToAdd = returnChangedIntex(i);
                if(indexToAdd >= 0)
                    v.add(indexToAdd);
        }
            return v;
    }*/


    public boolean[][] getBool() {
        return bool;
    }

    public int getSize() {
        return size;
    }


}