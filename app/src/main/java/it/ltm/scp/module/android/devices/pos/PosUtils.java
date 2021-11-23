package it.ltm.scp.module.android.devices.pos;

import it.ltm.scp.module.android.managers.ConnectionManager;
import it.ltm.scp.module.android.managers.ConnectionManagerFactory;
import it.ltm.scp.module.android.managers.secure.SecureManager;
import it.ltm.scp.module.android.utils.Errors;

/**
 * Created by HW64 on 17/11/2016.
 */

public class PosUtils {

    public static String getMessageFromErrorCode(int code) {
        switch (code) {
            case Errors.ERROR_NET_SERVER_RESPONSE:
                return Errors.getMap().get(Errors.ERROR_NET_SERVER_RESPONSE);

            case SecureManager.OP_EXCEPTION:
                return SecureManager.OP_EXCEPTION_MESSAGE;

            case Errors.ERROR_NET_IO_IPOS:

                if (ConnectionManagerFactory.getConnectionManagerInstance().getState() == ConnectionManager.State.CONNECTED) {
                    return Errors.getMap().get(Errors.ERROR_NET_IO_IPOS);
                } else {
                    return Errors.ERROR_NET_IO_CHECK_WIRELESS;
                }

            case DevicePos.ERROR_POS_AUTH:
                return DevicePos.ERROR_POS_AUTH_MESSAGE;

            case DevicePos.ERROR_POS_ABORT:
                return DevicePos.ERROR_POS_ABORT_MESSAGE;

            case DevicePos.ERROR_POS_INTERNAL:
                return DevicePos.ERROR_POS_INTERNAL_MESSAGE;

            case DevicePos.ERROR_POS_CONNECTION:
                return DevicePos.ERROR_POS_CONNECTION_MESSAGE;

            case DevicePos.ERROR_POS_UNREACH:
                return DevicePos.ERROR_POS_UNREACH_MESSAGE;

            case DevicePos.ERROR_POS_ABORT_TIMEOUT:
                return DevicePos.ERROR_POS_ABORT_TIMEOUT_MESSAGE;

            case DevicePos.ERROR_POS_SERVER:
                return DevicePos.ERROR_POS_SERVER_MESSAGE;

            case DevicePos.ERROR_POS_PAY_ABORT:
                return DevicePos.ERROR_POS_PAY_ABORT_MESSAGE;

            case DevicePos.ERROR_POS_BUSY:
                return DevicePos.ERROR_POS_BUSY_MESSAGE;

            case DevicePos.ERROR_POS_PAY_ABORT_TIMEOUT:
                return DevicePos.ERROR_POS_PAY_ABORT_TIMEOUT_MESSAGE;

            case DevicePos.ERROR_POS_PAY_AUTH:
                return DevicePos.ERROR_POS_AUTH_MESSAGE;

            case 501: // ERROR_POS_TSN_CONF
                return DevicePos.ERROR_POS_CONF_MESSAGE;

            case 502: // ERROR_POS_TSN_AUTH
                return DevicePos.ERROR_POS_AUTH_MESSAGE;

            case 503: // ERROR_POS_TSN_ABORT
                return "Richiesta annullata esplicitamente dall’operatore.";

            case 504: // ERROR_POS_TSN_ABORT_TIMEOUT
                return DevicePos.ERROR_POS_OP_ABORT_MESSAGE;

            case 505: // ERROR_POS_TSN_CARD
                return "Carta non valida.";

            case 506: // ERROR_POS_TSN_READ
                return "Errore di lettura.";

            case 104: // TIMEOUT GENERICO
                return "Operazione annullata per tempo scaduto";

            case 601:
                return DevicePos.ERROR_POS_CONF_MESSAGE;

            case 602:
                return DevicePos.ERROR_POS_AUTH_MESSAGE;

            case 603:
                return DevicePos.ERROR_POS_OP_ABORT_MESSAGE;

            case 604:
                return DevicePos.ERROR_POS_OP_TIMEOUT;

            default:
                return Errors.ERROR_MESSAGE_POS_DEFAULT;

        }
    }

    public static int parsePosCode(int oldCode){
        if(oldCode == 104){
            return Errors.ERROR_ICT_GENERIC + oldCode + 10; // 10114 per evitare sovrapposizione con 10104
        }
        if(oldCode < 1000 && oldCode > 0) // evita di incrementare i codici già corretti (serie 10000)
            return Errors.ERROR_ICT_GENERIC + oldCode;
        else
            return oldCode;
    }

    public static String appendCodeToMessage(String message, int code){
        return message + " (" + String.valueOf(code) + ")";
    }
}
