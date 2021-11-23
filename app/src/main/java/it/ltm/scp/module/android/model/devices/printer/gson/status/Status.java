package it.ltm.scp.module.android.model.devices.printer.gson.status;

/**
 * Created by HW64 on 23/08/2016.
 */
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Status implements Serializable {

    @SerializedName("paper")
    @Expose
    private Paper paper;
    @SerializedName("platen")
    @Expose
    private Platen platen;
    @SerializedName("paperJam")
    @Expose
    private PaperJam paperJam;
    @SerializedName("autoCutter")
    @Expose
    private AutoCutter autoCutter;
    @SerializedName("thermalHead")
    @Expose
    private ThermalHead thermalHead;
    @SerializedName("generalState")
    @Expose
    private Integer generalState;

    @SerializedName("usbAttached")
    private UsbAttached usbAttached;

    public final static int CODE_PAPER_OK = 110;
    public final static int CODE_PLATEN_OK = 100;
    public final static int CODE_CUTTER_OK = 130;
    public final static int CODE_JAM_OK = 120;
    public final static int CODE_THERMAL_OK = 140;
    public final static int CODE_USB_OK = 150;

    public final static String MESSAGE_PAPER_OK= "Carta terminata o non presente";
    public final static String MESSAGE_PLATEN_OK= "Sportello stampante aperto";
    public final static String MESSAGE_CUTTER_OK= "Errore taglierina";
    public final static String MESSAGE_USB_OK = "Stampante scollegata o spenta";
    public final static String MESSAGE_JAM_OK= "";
    public final static String MESSAGE_THERMAL_OK= "";


    public Paper getPaper() {
        return paper;
    }

    public void setPaper(Paper paper) {
        this.paper = paper;
    }

    public Platen getPlaten() {
        return platen;
    }

    public void setPlaten(Platen platen) {
        this.platen = platen;
    }

    public PaperJam getPaperJam() {
        return paperJam;
    }

    public void setPaperJam(PaperJam paperJam) {
        this.paperJam = paperJam;
    }

    public AutoCutter getAutoCutter() {
        return autoCutter;
    }

    public void setAutoCutter(AutoCutter autoCutter) {
        this.autoCutter = autoCutter;
    }

    public ThermalHead getThermalHead() {
        return thermalHead;
    }

    public void setThermalHead(ThermalHead thermalHead) {
        this.thermalHead = thermalHead;
    }

    public Integer getGeneralState() {
        return generalState;
    }

    public void setGeneralState(Integer generalState) {
        this.generalState = generalState;
    }

    public UsbAttached getUsbAttached() {
        return usbAttached;
    }

    public void setUsbAttached(UsbAttached usbAttached) {
        this.usbAttached = usbAttached;
    }

    public Integer calculateGeneralState() {
        int state = 1;
        if (getAutoCutter().statusCode == CODE_CUTTER_OK
                && getPaper().statusCode == CODE_PAPER_OK
                && getPaperJam().statusCode == CODE_JAM_OK
                && getPlaten().statusCode == CODE_PLATEN_OK
                && getThermalHead().statusCode == CODE_THERMAL_OK
                && (getUsbAttached() == null || (getUsbAttached().getStatusCode() == CODE_USB_OK))) {
            state = 0;
        }
        return state;
    }
}
