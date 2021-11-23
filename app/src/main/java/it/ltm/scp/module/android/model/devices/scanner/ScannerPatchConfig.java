package it.ltm.scp.module.android.model.devices.scanner;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;


public class ScannerPatchConfig {

    public final static String PATCH_CODE39 = "/code39";
    public final static String PATCH_ID_685 = "/685";
    public final static String PATCH_ID_691 = "/691";
    public final static String ZEBRA_ID = "zebra";

    @SerializedName("patches")
    @Expose
    private List<PatchItem> patches;


    public ScannerPatchConfig() {
        this.patches = new ArrayList<>();
    }

    public void addPatch(String path, Object value){
        patches.add(new PatchItem(path, value));
    }

    public List<PatchItem> getPatches() {
        return patches;
    }

    public void setPatches(List<PatchItem> patches) {
        this.patches = patches;
    }

    class PatchItem {
        private String op = "add";
        private String path;
        private Object value;

        public PatchItem(String path, Object value) {
            this.path = path;
            this.value = value;
        }
    }
}
