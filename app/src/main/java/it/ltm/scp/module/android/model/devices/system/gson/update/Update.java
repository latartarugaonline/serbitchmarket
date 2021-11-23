package it.ltm.scp.module.android.model.devices.system.gson.update;

/**
 * Created by HW64 on 28/03/2017.
 */

public class Update {
    private boolean upgradable;
    private String message;

    public boolean isUpgradable() {
        return upgradable;
    }

    public void setUpgradable(boolean upgradable) {
        this.upgradable = upgradable;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
