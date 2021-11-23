package it.ltm.scp.module.android.model.sm.gson;


/**
 * Created by HW64 on 19/10/2016.
 */
public class ServiceMarketRedirectData {
    private PosInfoDTO posData;
    private AuthDTO authData;
    private ServiceData serviceData;
    private LisData lisData;

    public PosInfoDTO getPosData() {
        return posData;
    }

    public void setPosData(PosInfoDTO posData) {
        this.posData = posData;
    }

    public AuthDTO getAuthData() {
        return authData;
    }

    public void setAuthData(AuthDTO authData) {
        this.authData = authData;
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
