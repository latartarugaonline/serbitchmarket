package it.ltm.scp.module.android.monitor.model.terminal;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Hw implements Serializable {
    private static final long serialVersionUID = -4311186470775224173L;
    private Pos pos;
    private Pinpad pinpad;
    private Terminal terminal;
    private List<Bcr> bcr;
    private Tablet tablet;

    public Hw(){
        this.bcr = new ArrayList<>();
    }

    public Pos getPos() {
        return pos;
    }

    public void setPos(Pos pos) {
        this.pos = pos;
    }

    public Pinpad getPinpad() {
        return pinpad;
    }

    public void setPinpad(Pinpad pinpad) {
        this.pinpad = pinpad;
    }

    public Terminal getTerminal() {
        return terminal;
    }

    public void setTerminal(Terminal terminal) {
        this.terminal = terminal;
    }

    public List<Bcr> getBcr() {
        return bcr;
    }

    public void setBcr(List<Bcr> bcr) {
        this.bcr = bcr;
    }

    public Tablet getTablet() {
        return tablet;
    }

    public void setTablet(Tablet tablet) {
        this.tablet = tablet;
    }

    public void addBcr(String model, String description){
        Bcr bcrItem = new Bcr();
        bcrItem.setModel(model);
        bcrItem.setDescription(description);
        this.bcr.add(bcrItem);
    }
}
