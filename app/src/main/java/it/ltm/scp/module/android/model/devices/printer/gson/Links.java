package it.ltm.scp.module.android.model.devices.printer.gson;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by HW64 on 23/08/2016.
 */


public class Links {

    @SerializedName("print")
    @Expose
    private Print print;
    @SerializedName("test")
    @Expose
    private Test test;
    @SerializedName("line")
    @Expose
    private Line line;

    /**
     *
     * @return
     * The print
     */
    public Print getPrint() {
        return print;
    }

    /**
     *
     * @param print
     * The print
     */
    public void setPrint(Print print) {
        this.print = print;
    }

    /**
     *
     * @return
     * The test
     */
    public Test getTest() {
        return test;
    }

    /**
     *
     * @param test
     * The test
     */
    public void setTest(Test test) {
        this.test = test;
    }

    /**
     *
     * @return
     * The line
     */
    public Line getLine() {
        return line;
    }

    /**
     *
     * @param line
     * The line
     */
    public void setLine(Line line) {
        this.line = line;
    }

}