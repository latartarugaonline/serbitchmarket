package it.ltm.scp.module.android.api.sm;

import android.net.Uri;
import android.util.Log;

import it.ltm.scp.module.android.BuildConfig;
import it.ltm.scp.module.android.utils.Constants;
import it.ltm.scp.module.android.utils.Properties;
import okhttp3.CertificatePinner;

/**
 * Created by HW64 on 27/01/2017.
 */

public class Pinner {
    public static CertificatePinner getPinner(){
        Uri smUri = Uri.parse(Properties.get(Constants.PROP_URL_SERVICE_MARKET_BASE)
                +  Properties.get(Constants.PROP_URL_SERVICE_MARKET_PATH_CTX));
        String sign = Properties.get(Constants.PROP_SM_CERT_SIGNATURE);
        String sign_dc = Properties.get(Constants.PROP_SM_CERT_SIGNATURE_DC);
        String sign_entrst = Properties.get(Constants.PROP_SM_CERT_SIGNATURE_ENTRST);
        Log.w("Pinner", "pinning certificates, host: " + smUri.getHost()
                + "\n sign 1: " + sign
                + "\n sign 2: " + sign_dc);
        CertificatePinner pinner = new CertificatePinner.Builder()
//                .add(smUri.getHost(), "sha1/BOGUSPIN") //fake
                .add(smUri.getHost(), sign_dc)
                .add(smUri.getHost(), sign)
                .add(smUri.getHost(), sign_entrst)
                .build();
        return pinner;
    }
}
