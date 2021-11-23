package it.ltm.scp.module.android.model.devices.system.gson.update;

/**
 * Created by HW64 on 29/05/2017.
 */

public class UpdateConfig {
    private boolean enabled;
    private String sourceRepository;
    private String firstUpdate;
    private String updateFrequency;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getSourceRepository() {
        return sourceRepository;
    }

    public void setSourceRepository(String sourceRepository) {
        this.sourceRepository = sourceRepository;
    }

    public String getFirstUpdate() {
        return firstUpdate;
    }

    public void setFirstUpdate(String firstUpdate) {
        this.firstUpdate = firstUpdate;
    }

    public String getUpdateFrequency() {
        return updateFrequency;
    }

    public void setUpdateFrequency(String updateFrequency) {
        this.updateFrequency = updateFrequency;
    }
}
