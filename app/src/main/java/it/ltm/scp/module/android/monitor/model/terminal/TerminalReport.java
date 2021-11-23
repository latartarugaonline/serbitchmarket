package it.ltm.scp.module.android.monitor.model.terminal;

import java.io.Serializable;

public class TerminalReport implements Serializable {

    private static final long serialVersionUID = 5023900750292761148L;
    private Hw hw;
    private Sw sw;



    public Hw getHw() {
        return hw;
    }

    public void setHw(Hw hw) {
        this.hw = hw;
    }

    public Sw getSw() {
        return sw;
    }

    public void setSw(Sw sw) {
        this.sw = sw;
    }
}
