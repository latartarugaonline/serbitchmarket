package it.ltm.scp.module.android.model.sm.gson;

/**
 * Created by HW64 on 19/10/2016.
 */
public class ServiceMarketResult {
    private int code;
    private String description;
    private ServiceData serviceData;
    private LisData lisData;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ServiceData getServiceData() {
        return serviceData;
    }

    public void setServiceData(ServiceData serviceData) {
        this.serviceData = serviceData;
    }

    public LisData getLisData() {
        return lisData;
    }

    public void setLisData(LisData lisData) {
        this.lisData = lisData;
    }
}
