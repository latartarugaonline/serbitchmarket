package it.ltm.scp.module.android.utils;

import java.util.HashMap;

/**
 * Created by HW64 on 13/09/2016.
 */
public class Errors {


    public static final int LANG_CURRENT = 0; //modify this value to change default language
    //language codes
    public static final int LANG_IT = 0;


    // errors code

    public static final int ERROR_OK = 0;

    public static final int INTERNAL_RECONNECT = 10; // codice errore per uso interno

    public static final int ERROR_ICT_GENERIC = 10000; // da modificare a 20000 nella prossima rel

    public static final int ERROR_GENERIC = 10000;
    public static final int ERROR_NET_GENERIC = 10100;
    public static final int ERROR_NET_IO_IPOS = 10101;
    public static final int ERROR_NET_IO = 10104;
    public static final int ERROR_NET_SERVER_RESPONSE = 10102;
    public static final int ERROR_NET_SERVER_KO = 10105;    //server risponde con un codice http != 200
    public static final int ERROR_NET_SM_SERVER_KO = 10080; // webservice servicemarket risponde con codice http !=200
    public static final int ERROR_NET_UNABLE_READ_ERROR_BODY = 10108; //eccezione quando viene letto l'error body dopo una chiamata rest con Retrofit
    public static final int ERROR_NET_NOT_FOUND = 10109; //server risponde con codice 404 (api mancante)

    public static final int ERROR_SECURITY_INTERNAL = 10010;
    public static final int ERROR_SECURITY_SIGNATURE = 10011;
    public static final int ERROR_SECURITY_CERTIFICATE_GENERIC = 10012;
    public static final int ERROR_SECURITY_CERTIFICATE_PINNING = 10013;
    public static final int ERROR_SECURITY_ROOT = 10014;
    public static final int ERROR_SECURITY_PERMISSION = 10015;
    public static final int ERROR_SECURITY_CERTIFICATE_IPOS = 10016;

    public static final int ERROR_CHECK_VERSION = 10020;
    public static final int ERROR_MIN_VERSION = 10021;
    public static final int ERROR_PRINTER_GENERAL = 10030;
    public static final int ERROR_RETRIEVE_AUTH_DATA= 10017;

    public static final int ERROR_BCR_STREAMING = 10040;
    public static final int ERROR_BCR_IMAGE_NOT_FOUND = 10041;
    public static final int ERROR_BCR_INPUT = 10042;
    public static final int ERROR_BCR_API_GENERIC = 10050;
    public static final int ERROR_BCR_NOT_PLUGGED = 10052;
    public static final int ERROR_BCR_ZEBRA_REQUIRED = 10053;

    public static final int ERROR_UPLOAD_GENERIC = 10060;
    public static final int ERROR_UPLOAD_BUSY = 10061;
    public static final int ERROR_UPLOAD_SAVE_FILE = 10062;
    public static final int ERROR_UPLOAD_LOAD_FILE = 10063;
    public static final int ERROR_UPLOAD_TIMEOUT = 10064;
    public static final int ERROR_UPLOAD_AUTH = 10065;


    public static final int ERROR_CIE_GENERIC = 10070;
    public static final int ERROR_CIE_PARSING_MRZ = 10071;
    public static final int ERROR_CIE_INVALID_MRZ = 10072;
    public static final int ERROR_CIE_CALLBACK_NULL = 10073;

    // App APM Errors
    public static final int ERROR_APM_FUNCTIONALITY_NOT_AVAILABLE = 10080;
    public static final int ERROR_APM_POS_REAUTH = 10081;
    public static final int ERROR_APM_ACTIVITY_RESULT_KO = 10082;
    public static final int ERROR_APM_FUNCTIONALITY_NOT_FOUND = 10083;


    public static final int ERROR_INPUT_GENERIC = 10300;
    public static final int ERROR_INPUT_TSN = 10301;
    public static final int ERROR_NOT_IMPL = 10399;

    // default messages
    public static final String ERROR_MESSAGE_POS_DEFAULT = "Errore interno, riprovare o contattare il supporto";

    public static final String ERROR_NET_IO_CHECK_WIRELESS = "Problemi di connettività wireless tra tablet e LIS@, si prega di premere il tasto 'RIPROVA'.";


    public static final HashMap<Integer, String> MAP_ERRORS_IT;
    static {
        MAP_ERRORS_IT = new HashMap<>();
        configureItDefaultMap();
        if(AppUtils.isIGP()){
            configureItIgpMap();
        } else if (AppUtils.isSunmi()) {
            configureItSunmiMap();
        } else if (AppUtils.isP2Pro()) {
            configureItP2proMap();
        }
        else if (AppUtils.isSunmiS()) {
            configureItSunmiSMap();
        }
    }

    private static void configureItIgpMap() {
        MAP_ERRORS_IT.put(ERROR_NET_IO_IPOS, "Problemi di comunicazione verificare lo stato della connessione e che il terminale sia operativo.");
        MAP_ERRORS_IT.put(ERROR_NET_SERVER_KO, "Si è verificato un problema interno a LISPower. Contatta il supporto tecnico.");
        MAP_ERRORS_IT.put(ERROR_SECURITY_CERTIFICATE_IPOS, "Errore di sicurezza, certificato di LISPower non valido.");
        MAP_ERRORS_IT.put(INTERNAL_RECONNECT, "Connessione in corso. Prego verificare che il terminale sia operativo (luce verde)");
        MAP_ERRORS_IT.put(ERROR_MIN_VERSION, "Versione LISPower incompatibile. Contattare il supporto per l'aggiornamento.");
    }

    private static void configureItSunmiMap() {
        MAP_ERRORS_IT.put(ERROR_NET_IO_IPOS, "Problemi di comunicazione verificare lo stato della connessione e che il terminale sia operativo.");
        MAP_ERRORS_IT.put(ERROR_NET_SERVER_KO, "Si è verificato un problema interno a LIS Tech2. Contatta il supporto tecnico.");
        MAP_ERRORS_IT.put(ERROR_SECURITY_CERTIFICATE_IPOS, "Errore di sicurezza, certificato di LIS Tech2 non valido.");
        MAP_ERRORS_IT.put(INTERNAL_RECONNECT, "Connessione in corso. Prego verificare che il terminale sia operativo (luce verde)");
        MAP_ERRORS_IT.put(ERROR_MIN_VERSION, "Versione LIS Tech2 incompatibile. Contattare il supporto per l'aggiornamento.");
    }

    private static void configureItP2proMap() {
        MAP_ERRORS_IT.put(ERROR_NET_IO_IPOS, "Problema di comunicazione interna, riavviare il dispositivo. Se il problema persiste contattare il servizio clienti.");
        MAP_ERRORS_IT.put(ERROR_NET_SERVER_KO, "Si è verificato un problema interno a LIS Tech2. Contatta il supporto tecnico.");
        MAP_ERRORS_IT.put(ERROR_SECURITY_CERTIFICATE_IPOS, "Errore di sicurezza, certificato di LIS Tech2 non valido.");
        MAP_ERRORS_IT.put(INTERNAL_RECONNECT, "Connessione in corso. Prego verificare che il terminale sia operativo (luce verde)");
        MAP_ERRORS_IT.put(ERROR_MIN_VERSION, "Versione LIS Tech2 incompatibile. Contattare il supporto per l'aggiornamento.");
    }

    private static void configureItSunmiSMap() {
        MAP_ERRORS_IT.put(ERROR_NET_IO_IPOS, "Problema di comunicazione interna, riavviare il dispositivo. Se il problema persiste contattare il servizio clienti.");
        MAP_ERRORS_IT.put(ERROR_NET_SERVER_KO, "Si è verificato un problema interno a LIS Tech2. Contatta il supporto tecnico.");
        MAP_ERRORS_IT.put(ERROR_SECURITY_CERTIFICATE_IPOS, "Errore di sicurezza, certificato di LIS Tech2 non valido.");
        MAP_ERRORS_IT.put(INTERNAL_RECONNECT, "Connessione in corso. Prego verificare che il terminale sia operativo (luce verde)");
        MAP_ERRORS_IT.put(ERROR_MIN_VERSION, "Versione LIS Tech2 incompatibile. Contattare il supporto per l'aggiornamento.");
    }


    private static void configureItDefaultMap() {
        MAP_ERRORS_IT.put(ERROR_GENERIC, "Errore generico");
        MAP_ERRORS_IT.put(ERROR_NET_GENERIC, "Errore di rete generico");
        MAP_ERRORS_IT.put(ERROR_NET_IO_IPOS, "Problemi di comunicazione tra tablet e LIS@, verificare lo stato della connessione e che il terminale sia operativo.");
        MAP_ERRORS_IT.put(ERROR_NET_IO, "Problemi di connettività, verificare lo stato della linea Internet.");
        MAP_ERRORS_IT.put(ERROR_NET_SERVER_RESPONSE, "Errore nella risposta del server");
        MAP_ERRORS_IT.put(ERROR_NET_SERVER_KO, "Si è verificato un problema interno a LIS@. Contatta il supporto tecnico.");
        MAP_ERRORS_IT.put(ERROR_NET_UNABLE_READ_ERROR_BODY, "Si è verificato un problema nel processare la risposta del server");
        MAP_ERRORS_IT.put(ERROR_NET_NOT_FOUND, "Funzionalità non disponibile, si prega di verificare la versione del terminale. Contattare il supporto tecnico.");
        MAP_ERRORS_IT.put(ERROR_SECURITY_INTERNAL, "Errore di sicurezza interno");
        MAP_ERRORS_IT.put(ERROR_INPUT_GENERIC, "Parametro di input non valido");
        MAP_ERRORS_IT.put(ERROR_SECURITY_PERMISSION, "Permesso negato esplicitamente dall'operatore.");
        MAP_ERRORS_IT.put(ERROR_SECURITY_SIGNATURE, "Errore di sicurezza, l'applicazione risulta compromessa.");
        MAP_ERRORS_IT.put(ERROR_SECURITY_CERTIFICATE_GENERIC, "Errore di sicurezza, certificato di Service Market non valido.");
        MAP_ERRORS_IT.put(ERROR_SECURITY_ROOT, "Errore di sicurezza, il dispositivo risulta compromesso.");
        MAP_ERRORS_IT.put(ERROR_INPUT_TSN, "Errore nella lettura della tessera sanitaria.");
        MAP_ERRORS_IT.put(ERROR_CHECK_VERSION, "Errore durante il controllo di compatibilità.");
        MAP_ERRORS_IT.put(ERROR_PRINTER_GENERAL, "Stampa annullata a causa di un problema della stampante.");
        MAP_ERRORS_IT.put(ERROR_SECURITY_CERTIFICATE_IPOS, "Errore di sicurezza, certificato di LIS@ non valido.");
        MAP_ERRORS_IT.put(ERROR_NOT_IMPL, "Errore, metodo non implementato.");
        MAP_ERRORS_IT.put(ERROR_BCR_STREAMING, "Errore durante l'avvio della fotocamera. Controllare che lo scanner sia collegato e ripetere l'operazione.");
        MAP_ERRORS_IT.put(ERROR_BCR_NOT_PLUGGED, "Errore: controllare che lo scanner sia collegato e ripetere l'operazione.");
        MAP_ERRORS_IT.put(ERROR_BCR_API_GENERIC, "Errore: controllare che lo scanner sia collegato e ripetere l'operazione.");
        MAP_ERRORS_IT.put(ERROR_BCR_IMAGE_NOT_FOUND, "Immagine non trovata, ripetere l'operazione.");
        MAP_ERRORS_IT.put(ERROR_BCR_INPUT, "Errore, parametri di input non validi. Contattare il supporto tecnico.");
        MAP_ERRORS_IT.put(ERROR_UPLOAD_GENERIC, "Si è verificato un errore in fase di upload dei documenti.");
        MAP_ERRORS_IT.put(ERROR_UPLOAD_BUSY, "E' già in corso un upload di documenti.");
        MAP_ERRORS_IT.put(ERROR_UPLOAD_SAVE_FILE, "Si è verificato un errore in fase di upload dei documenti.");
        MAP_ERRORS_IT.put(ERROR_UPLOAD_LOAD_FILE, "Si è verificato un errore in fase di upload dei documenti.");
        MAP_ERRORS_IT.put(ERROR_UPLOAD_TIMEOUT, "Si è verificato un errore in fase di upload dei documenti. Tempo scaduto.");
        MAP_ERRORS_IT.put(ERROR_UPLOAD_AUTH, "Si è verificato un errore di autenticazione in fase di upload dei documenti.");
        MAP_ERRORS_IT.put(INTERNAL_RECONNECT, "Connessione rete wireless in corso. Prego verificare che il terminale sia operativo (luce verde fissa)");
        MAP_ERRORS_IT.put(ERROR_MIN_VERSION, "Versione LIS@ incompatibile. Contattare il supporto per l'aggiornamento.");
        MAP_ERRORS_IT.put(ERROR_BCR_ZEBRA_REQUIRED, "Errore: controllare che lo scanner ZEBRA sia collegato e ripetere l'operazione.");
        MAP_ERRORS_IT.put(ERROR_CIE_GENERIC, "Problema durante la lettura della carta CIE. Riprovare o contattare il supporto.");
        MAP_ERRORS_IT.put(ERROR_CIE_INVALID_MRZ, "Problema durante la lettura della carta CIE. Codice MRZ errato, provare una nuova lettura.");
        MAP_ERRORS_IT.put(ERROR_RETRIEVE_AUTH_DATA, "Si è verificato un errore nel recupero dati di autenticazione. Riprovare o contattare il supporto.");
        MAP_ERRORS_IT.put(ERROR_NET_SM_SERVER_KO, "Si è verificato un errore. Riprovare o contattare il supporto.");
        MAP_ERRORS_IT.put(ERROR_APM_FUNCTIONALITY_NOT_AVAILABLE, "Funzionalità non disponibile.");
        MAP_ERRORS_IT.put(ERROR_APM_POS_REAUTH, "Si è verificato un errore di autenticazione.");
        MAP_ERRORS_IT.put(ERROR_APM_ACTIVITY_RESULT_KO, "Si è verificato un errore interno. Riprovare o contattare il supporto.");
        MAP_ERRORS_IT.put(ERROR_APM_FUNCTIONALITY_NOT_FOUND, "Funzionalità non disponibile.");
    }

    public static final HashMap<Integer, String> getMap(){
        switch (LANG_CURRENT){
            case LANG_IT:
                return MAP_ERRORS_IT;
            default:
                return MAP_ERRORS_IT;
        }
    }
}
