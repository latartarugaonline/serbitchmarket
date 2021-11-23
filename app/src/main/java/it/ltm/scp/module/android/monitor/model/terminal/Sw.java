package it.ltm.scp.module.android.monitor.model.terminal;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Sw implements Serializable {
    private static final long serialVersionUID = -4701886241432590281L;
    private List<App> apps;

    public Sw() {
        apps = new ArrayList<>();
    }

    public List<App> getApps() {
        return apps;
    }

    public void setApps(List<App> apps) {
        this.apps = apps;
    }

    public void addApp(App app) {
        this.apps.add(app);
    }

}
