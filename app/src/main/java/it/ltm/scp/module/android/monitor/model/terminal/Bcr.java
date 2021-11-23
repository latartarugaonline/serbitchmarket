package it.ltm.scp.module.android.monitor.model.terminal;

import java.io.Serializable;

public class Bcr implements Serializable {
    private static final long serialVersionUID = -3652238993571659554L;
    private String model;
    private String description;

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
