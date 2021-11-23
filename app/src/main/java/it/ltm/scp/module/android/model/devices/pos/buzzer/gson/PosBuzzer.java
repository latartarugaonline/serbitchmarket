package it.ltm.scp.module.android.model.devices.pos.buzzer.gson;

/**
 * Created by HW64 on 20/09/2016.
 */
public class PosBuzzer {
    private int type;
    private int duration;

    public PosBuzzer() {
    }


    public PosBuzzer(int type, int duration) {
        this.type = type;
        this.duration = duration;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}
