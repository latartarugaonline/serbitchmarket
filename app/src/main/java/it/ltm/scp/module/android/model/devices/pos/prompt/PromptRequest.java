package it.ltm.scp.module.android.model.devices.pos.prompt;

/**
 * Created by HW64 on 21/06/2017.
 */

public class PromptRequest {
    private String prompts;

    public PromptRequest(String description) {
        this.prompts = description;
    }

    public String getPrompts() {
        return prompts;
    }

    public void setPrompts(String prompts) {
        this.prompts = prompts;
    }

}
