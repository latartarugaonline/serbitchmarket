package it.ltm.scp.module.android.monitor.model.terminal;

import java.io.Serializable;

public class App implements Serializable {
    private static final long serialVersionUID = 5695449433513779444L;
    private String name;
    private String id;
    private String version;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
