package it.ltm.scp.module.android.utils;

/**
 * Created by HW64 on 19/08/2016.
 */
public class Constants {

    //props keys:
    public static final String PROP_URL_REST_API_BASE = "api_base_url";
    public static final String PROP_URL_REST_API_BASE_LH = "api_base_url_lh";
    public static final String PROP_BUILD = "buildType";
    public static final String CONFIG_ASSET_PATH = "props/config.properties";
    public static final String PROP_URL_SERVICE_MARKET_BASE = "service_market_base_url";
    public static final String PROP_URL_SERVICE_MARKET_BASE_COLLAUDO = "service_market_base_url_collaudo";
    public static final String PROP_URL_SERVICE_MARKET_BASE_SVILUPPO = "service_market_base_url_sviluppo";
    public static final String PROP_URL_SERVICE_MARKET_PATH_CTX = "service_market_path_ctx";
    public static final String PROP_URL_SERVICE_MARKET_PATH_WEBVIEW = "service_market_path_webview";
    public static final String PROP_URL_SERVICE_MARKET_PATH_PING = "service_market_path_ping";
    public static final String PROP_PHRASE = "phrase";
    public static final String PROP_APP_SIGNATURE_HASH = "a_sign_hsh";
    public static final String PROP_SM_CERT_SIGNATURE = "sm_crt_sign";
    public static final String PROP_SM_CERT_SIGNATURE_DC = "sm_crt_sign_dc";
    public static final String PROP_SM_CERT_SIGNATURE_ENTRST = "sm_crt_sign_entrst";
    public static final String PROP_HTTP_IGNORE_E = "http_ignore";
    public static final String PROP_SUB_PAYMENT = "sub_payment";
    public static final String PROP_SUB_CASHDRAWER = "sub_cashdrawer";
    public static final String PROP_SUB_PRINTER = "sub_printer";
    public static final String PROP_SUB_SCANNER = "sub_scanner";
    public static final String PROP_SUB_SCANNER_STATUS = "sub_scanner_status";
    public static final String PROP_SUB_SCANNER_UPDATE = "sub_scanner_update";
    public static final String PROP_SUB_POWER = "sub_power";
    public static final String PROP_SUB_ASYNC = "sub_async";
    public static final String PROP_SUB_ASYNC_PAYMENT = "sub_async_payment";
    public static final String PROP_SUB_ASYNC_AUTH = "sub_async_auth";
    public static final String PROP_SUB_ASYNC_TSN = "sub_async_tsn";
    public static final String PROP_SUB_UPDATE = "sub_update";
    public static final String PROP_WS_URL = "websocket_url";
    public static final String PROP_WS_URL_LH = "websocket_url_lh";
    public static final String PROP_FW_REPO_URL = "fw_repo";
    public static final String PROP_WS_PROTOCOL = "websocket_protocol";
    public static final String PROP_SUB_PROMPT = "sub_prompt";
    public static final String PROP_SUB_SCANNER_SNAPSHOT = "sub_scanner_snapshot";
    public static final String PROP_URL_IPOS_REPO_BS = "url_repo_bs";
    public static final String PROP_URL_IPOS_REPO_BSVIP = "url_repo_bsvip";

    public static final String HEAD_TIME = "X-RequestTimestamp";
    public static final String HEAD_TOK = "X-SecurityToken";


    //tipo terminale
    public static final String DEVICE_IPOS = "LISA";
    public static final String DEVICE_AXIUM = "AXIUM";
    public static final String DEVICE_IGP = "IGP2030S";
    public static final String DEVICE_SUNMI = "T2LITE";
    public static final String DEVICE_SUNMI_LITE = "D2SLITE";
    public static final String DEVICE_P2_PRO = "P2PRO";

    //tipo app
    public static final String APP_CODE = "00000";

    public static int PROMPT_TEST_INDEX = 0;

    //okHttp timeout richieste in background
    public static int BCKGRND_TIMEOUT_CONNECTION = 15;
    public static int BCKGRND_TIMEOUT_READ = 120;
    public static int BCKGRND_TIMEOUT_WRITE = 60;

    //bcr message
}