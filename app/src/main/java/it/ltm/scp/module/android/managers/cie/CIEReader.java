package it.ltm.scp.module.android.managers.cie;

import android.util.Log;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import it.ltm.scp.module.android.api.APICallback;
import it.ltm.scp.module.android.api.pos.PosAPI;
import it.ltm.scp.module.android.model.CIE;
import it.ltm.scp.module.android.model.Result;
import it.ltm.scp.module.android.model.devices.pos.gson.PosResult;
import it.ltm.scp.module.android.model.devices.pos.prompt.PromptRequest;
import it.ltm.scp.module.android.model.devices.pos.prompt.PromptResponseData;
import it.ltm.scp.module.android.utils.Errors;

public class CIEReader
{
    private static final String TAG = "CIEReader";

    private static final int REMOVE_CARD_ERRORS_RETRY = 5;
    private int mCurrentRemoveCardErrorRetry = 0;

    private String SELECT_APDU = "00A4040C07A0000002471001";
    private String GET_CHALLENGE_APDU = "0084000008";

    private String COMMAND_OPEN = "ATTESA CARTA:L11016;";
    private String COMMAND_TRANSMIT = "TRASMISSIONE:L2001601";
    private String COMMAND_CLOSE = "RIMUOVI CARTA:L30016;";
    private String END_COMMAND = ";";
    private int OPEN_TIMEOUT_SECONDS = 180;

    // MRZ section
    private MRZ mMRZ = null;
    private CIE mCIE = null;

    private byte[] kSessEnc = null;
    private byte[] kSessMac = null;
    private byte[] seq = null;

    private int index = 0;

    private CIEReaderCallback mCIEReaderCallback = null;

    public void startRead(String iMRZ, CIEReaderCallback iCallback)
    {
        mCIEReaderCallback = iCallback;
        if(mCIEReaderCallback != null)
        {
            if(iMRZ != null || iMRZ.length() == 90)
            {
                try {
                    String mrzString = iMRZ.replace('|', '<');
                    mMRZ = new MRZ(mrzString);
                    if(mMRZ.isValid())
                    {
                        Log.i(TAG, "Received MRZ = " + mMRZ.toString());

                        PromptRequest promptRequest = new PromptRequest(COMMAND_OPEN);
                        new PosAPI().getPromptCustomTimeout(promptRequest, OPEN_TIMEOUT_SECONDS, new APICallback() {
                            @Override
                            public void onFinish(Result result) {
                                if (result.getCode() == Errors.ERROR_OK) {
                                    Log.d(TAG, "COMMAND_OPEN done.");
                                    SelectCommand s = new SelectCommand();
                                    s.execute(null);
                                } else {
                                    Log.e(TAG, "COMMAND_OPEN error = " + result.toJsonString());
                                    tryRemoveCard(result.getCode(), result.getDescription());
                                }
                            }
                        });
                    }
                    else
                    {
                        if(mCIEReaderCallback != null)
                            mCIEReaderCallback.onFailure(Errors.ERROR_CIE_PARSING_MRZ, Errors.getMap().get(Errors.ERROR_CIE_INVALID_MRZ));
                    }
                } catch (Exception e) {
                    Log.i(TAG, "ERRORE " + e.getMessage());
                    Log.i(TAG, Log.getStackTraceString(e));
                    if(mCIEReaderCallback != null)
                        mCIEReaderCallback.onFailure(Errors.ERROR_CIE_PARSING_MRZ, Errors.getMap().get(Errors.ERROR_CIE_INVALID_MRZ));
                }
                mCIE = new CIE();
            }
            else
            {
                Log.e(TAG, "startRead error: Invalid MRZ");
                if(mCIEReaderCallback != null)
                    mCIEReaderCallback.onFailure(Errors.ERROR_CIE_INVALID_MRZ, Errors.getMap().get(Errors.ERROR_CIE_INVALID_MRZ));
            }
        }
        else
        {
            Log.e(TAG, "startRead error: CIEReaderCallback cannot be NULL");
            if(mCIEReaderCallback != null)
                mCIEReaderCallback.onFailure(Errors.ERROR_CIE_CALLBACK_NULL, Errors.getMap().get(Errors.ERROR_CIE_GENERIC));
        }
    }

    public void tryRemoveCard()
    {
        tryRemoveCard(Integer.MAX_VALUE, null);
    }

    public void tryRemoveCard(int errorCode, String errorDescription)
    {
        if(mCurrentRemoveCardErrorRetry < REMOVE_CARD_ERRORS_RETRY)
        {
            removeCard(errorCode, errorDescription);
            ++mCurrentRemoveCardErrorRetry;
        }
        else
        {
            if(mCIEReaderCallback != null && !(errorCode == Integer.MAX_VALUE && errorDescription == null))
                mCIEReaderCallback.onFailure(errorCode, errorDescription);
            mCurrentRemoveCardErrorRetry = 0;
        }
    }

    public void removeCard(final int errorCode, final String errorDescription)
    {
        PromptRequest promptRequest = new PromptRequest(COMMAND_CLOSE);
        new PosAPI().getPrompt(promptRequest, new APICallback() {
            @Override
            public void onFinish(Result result) {
                if(result.getCode() == Errors.ERROR_OK){
                    Log.d(TAG, "onFinish remove card call: OK"); // FIXME: 13/03/2020 se rimuove carta dopo errore pos, non invia callback di esito
                    if(errorCode < Integer.MAX_VALUE && errorDescription != null){
                        mCIEReaderCallback.onFailure(errorCode, errorDescription);
                    }
                } else {
                    tryRemoveCard(result.getCode(), result.getDescription());
                }
            }
        });
    }

    private void reset()
    {
        kSessEnc = null;
        kSessMac = null;
        seq = null;
        index = 0;
        tryRemoveCard();
    }

    interface Command
    {
        public void execute(String iArgs);

        @Override
        public String toString();
    }

    class SelectCommand implements Command
    {
        @Override
        public void execute(String iArgs)
        {
            Log.d(TAG, "Transmit SelectCommand command...");
            doSendAPDUMessage(COMMAND_TRANSMIT, SELECT_APDU, END_COMMAND, new GetChallengeCommand());
        }

        @Override
        public String toString()
        {
            return "SelectCommand";
        }
    }

    class GetChallengeCommand implements Command
    {
        @Override
        public void execute(String iArgs)
        {
            Log.d(TAG, "Transmit GetChallengeCommand command...");
            doSendAPDUMessage(COMMAND_TRANSMIT, GET_CHALLENGE_APDU, END_COMMAND, new BasicAccessControlCommand());
        }

        @Override
        public String toString()
        {
            return "GetChallengeCommand";
        }
    }

    class BasicAccessControlCommand implements Command
    {
        @Override
        public void execute(String iArgs)
        {
            Log.d(TAG, "Transmit BasicAccessControlCommand command...");

            boolean mrzIsSet = mMRZ.getDocumentNumber() != null && mMRZ.getBirthDate() != null && mMRZ.getExpiryDate() != null;
            if(mrzIsSet)
            {
                byte[] documentIdBytes = mMRZ.getDocumentNumber().getBytes();
                byte[] birthBytes = mMRZ.getBirthDate().getBytes();
                byte[] expireBytes = mMRZ.getExpiryDate().getBytes();

                try {
                    byte[] randomBytes = AppUtil.getApduData(iArgs);
                    Log.d(TAG, "randomBytes = " + AppUtil.bytesToHex(randomBytes));

                    byte seedPartDocumentIdBytes[] = AppUtil.appendByte(documentIdBytes, AppUtil.checkdigit(documentIdBytes));
                    Log.d(TAG, "seedPartDocumentIdBytes = " + AppUtil.bytesToHex(seedPartDocumentIdBytes));

                    byte seedPartBirth[] = AppUtil.appendByte(birthBytes, AppUtil.checkdigit(birthBytes));
                    Log.d(TAG, "seedPartBirth = " + AppUtil.bytesToHex(seedPartBirth));

                    byte seedPartExpire[] = AppUtil.appendByte(expireBytes, AppUtil.checkdigit(expireBytes));
                    Log.d(TAG, "seedPartExpire = " + AppUtil.bytesToHex(seedPartExpire));

                    // Concatenate MRZ infos
                    byte[] bacSeedData = AppUtil.appendByteArray(seedPartDocumentIdBytes, seedPartBirth);
                    Log.d(TAG, "bacSeedData = " + AppUtil.bytesToHex(bacSeedData));

                    bacSeedData = AppUtil.appendByteArray(bacSeedData, seedPartExpire);
                    Log.d(TAG, "bacSeedData = " + AppUtil.bytesToHex(bacSeedData));

                    byte[] bacEnc = AppUtil.getLeft(AppUtil.getSha1(AppUtil.appendByteArray(AppUtil.getLeft(AppUtil.getSha1(bacSeedData), 16), new byte[]{(byte) 0x00, 0x00, 0x00, 0x01})), 16);
                    Log.d(TAG, "bacEnc = " + AppUtil.bytesToHex(bacEnc));

                    byte[] bacMac = AppUtil.getLeft(AppUtil.getSha1(AppUtil.appendByteArray(AppUtil.getLeft(AppUtil.getSha1(bacSeedData), 16), new byte[]{(byte) 0x00, 0x00, 0x00, 0x02})), 16);
                    Log.d(TAG, "bacMac = " + AppUtil.bytesToHex(bacMac));

                    // Random bytes
                    byte[] random8Bytes = new byte[8];
                    AppUtil.getRandomByte(random8Bytes);
                    Log.d(TAG, "random8Bytes = " + AppUtil.bytesToHex(random8Bytes));

                    byte[] random16Bytes = new byte[16];
                    AppUtil.getRandomByte(random16Bytes);
                    Log.d(TAG, "random16Bytes = " + AppUtil.bytesToHex(random16Bytes));

                    byte[] dataDesEnc = Algorithms.desEnc(bacEnc, AppUtil.appendByteArray(AppUtil.appendByteArray(random8Bytes, randomBytes), random16Bytes));
                    Log.d(TAG, "dataDesEnc = " + AppUtil.bytesToHex(dataDesEnc));

                    byte[] dataDesEncMacEnc = Algorithms.macEnc(bacMac, AppUtil.getIsoPad(dataDesEnc));
                    Log.d(TAG, "dataDesEncMacEnc = " + AppUtil.bytesToHex(dataDesEncMacEnc));

                    // Auth apdu
                    byte[] data = AppUtil.appendByteArray(dataDesEnc, dataDesEncMacEnc);
                    Log.d(TAG, "data = " + AppUtil.bytesToHex(data));

                    byte[] authApdu = AppUtil.appendByte(AppUtil.appendByteArray(AppUtil.appendByteArray(new byte[]{0x00, (byte) 0x82, 0x00, 0x00, 0x28}, dataDesEnc), dataDesEncMacEnc), (byte)0x28);
                    Log.d(TAG, "authApdu = " + AppUtil.bytesToHex(authApdu));

                    BasicAccessControlSuccessCommand b = new BasicAccessControlSuccessCommand();
                    b.bacEnc = bacEnc;
                    b.bacMac = bacMac;
                    b.random16Bytes = random16Bytes;

                    doSendAPDUMessage(COMMAND_TRANSMIT, AppUtil.bytesToHex(authApdu), END_COMMAND, b);
                }
                catch(Exception e)
                {
                    Log.e(TAG,"Exception " + e.getLocalizedMessage());
                    reset();
                }
            }
            else
            {
                Log.e(TAG,"Try to perform basic access control without a valid MRZ");
                reset();
            }
        }

        @Override
        public String toString()
        {
            return "BasicAccessControlCommand";
        }
    }

    class BasicAccessControlSuccessCommand implements Command
    {
        public byte[] bacEnc = null;
        public byte[] bacMac = null;
        public byte[] random16Bytes = null;

        @Override
        public void execute(String iArgs)
        {
            Log.d(TAG, "Transmit BasicAccessControlSuccessCommand command...");

            boolean dataAreValid = bacEnc != null && bacMac != null && random16Bytes != null;
            if(dataAreValid)
            {
                try
                {
                    byte[] authApduResponse = AppUtil.getApduData(iArgs);

                    Log.d(TAG, "authApduResponse = " + AppUtil.bytesToHex(authApduResponse));
                    Log.d(TAG, "bacEnc = " + AppUtil.bytesToHex(bacEnc));
                    Log.d(TAG, "bacMac = " + AppUtil.bytesToHex(bacMac));
                    Log.d(TAG, "random16Bytes = " + AppUtil.bytesToHex(random16Bytes));

                    byte[] kIsMac = Algorithms.macEnc(bacMac, AppUtil.getIsoPad(AppUtil.getLeft(authApduResponse, 32)));
                    Log.d(TAG, "kIsMac = " + AppUtil.bytesToHex(kIsMac));

                    byte[] kIsMac2 = AppUtil.getRight(authApduResponse, 8);
                    Log.d(TAG, "kIsMac2 = " + AppUtil.bytesToHex(kIsMac2));

                    if(Arrays.equals(kIsMac, kIsMac2))
                    {
                        byte[] decResp = Algorithms.desDec(bacEnc, AppUtil.getLeft(authApduResponse, 32));
                        Log.d(TAG, "decResp = " + AppUtil.bytesToHex(decResp));

                        byte[] kMrtd = AppUtil.getRight(decResp, 16);
                        Log.d(TAG, "kMrtd = " + AppUtil.bytesToHex(kMrtd));

                        byte[] kSeed = AppUtil.stringXor(random16Bytes, kMrtd);
                        Log.d(TAG, "kSeed = " + AppUtil.bytesToHex(kSeed));

                        //session's key
                        kSessMac = AppUtil.getLeft(AppUtil.getSha1(AppUtil.appendByteArray(kSeed, new byte[]{0x00, 0x00, 0x00, 0x02})), 16);
                        Log.d(TAG, "kSessMac = " + AppUtil.bytesToHex(kSessMac));

                        kSessEnc = AppUtil.getLeft(AppUtil.getSha1(AppUtil.appendByteArray(kSeed, new byte[]{0x00, 0x00, 0x00, 0x01})), 16);
                        Log.d(TAG, "kSessEnc = " + AppUtil.bytesToHex(kSessEnc));

                        byte[] tmp = AppUtil.getSub(decResp, 4, 4);
                        Log.d(TAG, "tmp = " + AppUtil.bytesToHex(tmp));

                        byte[] tmp2 = AppUtil.getSub(decResp, 12, 4);
                        Log.d(TAG, "tmp2 = " + AppUtil.bytesToHex(tmp2));

                        seq = AppUtil.appendByteArray(tmp, tmp2);
                        Log.d(TAG, "seq = " + AppUtil.bytesToHex(seq));

                        Log.d(TAG,"BasicAccessControlSuccessCommand: Auth finished. Starting reading DGS...");

                        ReadDatagroupCommand command = new ReadDatagroupCommand();
                        command.datagroupNumber = 11;
                        command.execute(null);
                    }
                    else
                    {
                        Log.e(TAG,"BasicAccessControlSuccessCommand keys mismatch.");
                        reset();
                    }
                }
                catch (Exception e)
                {
                    Log.e(TAG,"Exception " + e.getLocalizedMessage());

                }
            }
            else
            {
                Log.e(TAG,"BasicAccessControlSuccessCommand data fields are null.");
            }
        }

        @Override
        public String toString()
        {
            return "BasicAccessControlSuccessCommand";
        }
    }

    class ReadDatagroupCommand implements Command
    {
        public int datagroupNumber = -1;
        private byte[] data = new byte[0];
        private int maxLen = -1;
        private boolean isFirstDataRequest = true;

        @Override
        public void execute(String iArgs)
        {
            Log.i(TAG, "ReadDatagroupCommand for datagroup = " + datagroupNumber);

            if(data.length == 0 && maxLen == -1)
            {
                try
                {
                    byte sum = (byte) ((byte) datagroupNumber + (byte) 0x80);
                    String hex = AppUtil.bytesToHex(new byte[]{sum});
                    byte[] msg = AppUtil.hexStringToByteArray("0cb0" + hex + "0006");
                    byte[] apdu = secureMessage(kSessEnc, kSessMac, msg);

                    maxLen = 0;
                    doSendAPDUMessage(COMMAND_TRANSMIT, AppUtil.bytesToHex(apdu), END_COMMAND, this);
                }
                catch(Exception e)
                {
                    Log.i(TAG, "ERRORE " + e.getMessage());
                    Log.i(TAG, Log.getStackTraceString(e));
                    tryRemoveCard(-6, "Errore");
                }
            }
            else
            {
                try
                {
                    if(isFirstDataRequest)
                    {
                        byte[] apduRespDg = AppUtil.getApduData(iArgs);
                        byte[] chunkLen = responseSecureMessage(kSessEnc, kSessMac, apduRespDg);
                        maxLen = Asn1Tag.parseLength(chunkLen);
                        if(maxLen < 0) {
                            tryRemoveCard(-4, "Carta non riconosciuta");
                        }
                    }
                    else
                    {
                        byte[] tempApduRespDg = AppUtil.getApduData(iArgs);
                        byte[] chunk = responseSecureMessage(kSessEnc, kSessMac, tempApduRespDg);
                        data = AppUtil.appendByteArray(data, chunk);
                    }

                    if(data.length < maxLen)
                    {
                        Log.i(TAG, "### max length" + maxLen);
                        Log.i(TAG, "% di completamento " + (double)(data.length / maxLen) * 100);

//                        int readLen = Math.min(0xe0, maxLen - data.length);
                        int readLen = Math.min(0x40, maxLen - data.length);
                        byte[] tempMsg = AppUtil.appendByte(AppUtil.appendByte(AppUtil.appendByte(AppUtil.hexStringToByteArray("0cb0"), (byte) ((byte) (data.length / 256) & (byte) 0x7f)), (byte) (data.length & 0xff)), (byte) readLen);
                        byte[] tempApduDg = secureMessage(kSessEnc, kSessMac, tempMsg);
                        doSendAPDUMessage(COMMAND_TRANSMIT, AppUtil.bytesToHex(tempApduDg), END_COMMAND, this);
                    }
                    else if(data.length == maxLen)
                    {
                        if(datagroupNumber == 11){
                            parse11(data);
                            ReadDatagroupCommand readDg12 = new ReadDatagroupCommand();
                            readDg12.datagroupNumber = 12;
                            readDg12.execute(null);
                        }
                        else if(datagroupNumber == 12){
                            parse12(data);
                            mCIE.setSesso(mMRZ.getGender());
                            mCIE.setNumDoc(mMRZ.getDocumentNumber());
                            mCIE.setCittadinanza(mMRZ.getNationality());
                            mCIE.setDataScadenza(mMRZ.getExpiryDate());
                            if(mCIEReaderCallback != null)
                                mCIEReaderCallback.onSuccess(mCIE);
                            reset();
                        }
                    }

                    isFirstDataRequest = false;
                }
                catch(Exception e)
                {
                    Log.i(TAG, "ERRORE " + e.getMessage());
                    Log.i(TAG, Log.getStackTraceString(e));
                    tryRemoveCard();
                }
            }
        }
    }

    private void doSendAPDUMessage(String iCommand, String iAPDUMessage, String iEndCommand, final Command iNextCommand)
    {
        String apdu = AppUtil.apduWithLength(iAPDUMessage);
        Log.d(TAG, "doSendAPDUMessage vasCommandMessage = " + iCommand + apdu + iEndCommand);

        PromptRequest promptRequest = new PromptRequest(iCommand + apdu + iEndCommand);
        new PosAPI().getPrompt(promptRequest, new APICallback() {
            @Override
            public void onFinish(Result result) {
                if(result.getCode() == Errors.ERROR_OK)
                {
                    PosResult<PromptResponseData> data = (PosResult<PromptResponseData>) result.getData();
                    String adpuResponse = data.getData().getHexData();
                    int adpuResponseSW = AppUtil.getStatusWord(adpuResponse);
                    if(adpuResponseSW == 0x9000) // APDU response is OK <--> status word == 0x9000
                    {
                        if (iNextCommand != null) {
                            Log.d(TAG, "doSendAPDUMessage [ next command (" + iNextCommand.toString() + ") ]: " + adpuResponse);
                            iNextCommand.execute(adpuResponse);
                        }
                    }
                    else
                    {
                        Log.e(TAG, "doSendAPDUMessage APDU response with error: " + adpuResponseSW);
                        tryRemoveCard(adpuResponseSW, "APDU response with error " + adpuResponseSW);
                    }
                }
                else
                {
                    Log.e(TAG, "doSendAPDUMessage ERROR: " + result.toJsonString());
                    tryRemoveCard(result.getCode(), result.getDescription());
                }
            }
        });
    }

    private byte[] secureMessage(byte[] iKeyEnc, byte[] iKeyMac, byte[] iApdu) throws Exception
    {
        AppUtil.increment(seq);

        byte[] calcMac = AppUtil.getIsoPad(AppUtil.appendByteArray(seq, AppUtil.getLeft(iApdu, 4)));

        byte[] smMac = null;
        byte[] dataField = null;
        byte[] doob = null;

        if(iApdu[4] != 0 && iApdu.length > 5)
        {
            byte[] enc = Algorithms.desEnc(iKeyEnc, AppUtil.getIsoPad(AppUtil.getSub(iApdu, 5, iApdu[4])));
            if(iApdu[1] % 2 == 0)
            {
                doob = AppUtil.asn1Tag(AppUtil.appendByteArray(new byte[]{ 0x001 }, enc), 0x87);
            }
            else
            {
                doob = AppUtil.asn1Tag(enc, 0x85);
            }

            calcMac = AppUtil.appendByteArray(calcMac, doob);
            dataField = AppUtil.appendByteArray(dataField, doob);
        }

        if(iApdu.length == 5 || iApdu.length == iApdu[4] + 6)
        {
            doob = new byte[] {(byte)0x97, (byte)0x01, iApdu[iApdu.length - 1]};
            calcMac = AppUtil.appendByteArray(calcMac,doob);
            if(dataField == null)
                dataField = doob.clone();
            else
                dataField = AppUtil.appendByteArray(dataField, doob);
        }

        smMac = Algorithms.macEnc(iKeyMac, AppUtil.getIsoPad(calcMac));
        dataField = AppUtil.appendByteArray(dataField, AppUtil.appendByteArray(new byte[] { (byte)0x8e, 0x08 }, smMac));
        byte[] retValue = AppUtil.appendByte(AppUtil.appendByteArray(AppUtil.appendByteArray(AppUtil.getLeft(iApdu, 4),new byte[]{ (byte)dataField.length} ), dataField), (byte)0x00);
        return retValue;
    }

    private byte[] responseSecureMessage(byte[] iKeyEnc, byte[] iKeySig, byte[] iResp) throws Exception
    {
        return responseSecureMessage(iKeyEnc, iKeySig, iResp,  false);
    }

    private byte[] responseSecureMessage(byte[] iKeyEnc, byte[] iKeySig, byte[] iResp, boolean iOdd) throws Exception
    {
        AppUtil.increment(seq);

        setIndex(0);
        byte[] encData = null;
        byte[] encObj = null;
        byte[] dataObj = null;

        do
        {
            if(Byte.compare(iResp[index], (byte)0x99) == 0)
            {
                if(Byte.compare(iResp[index+1], (byte) 0x02) != 0 )
                {
                    Log.e(TAG, "Verify secure message error: length of data object");
                    return null;
                }
                dataObj = AppUtil.getSub(iResp, index, 4);
                setIndex(index, 4);
                continue;
            }
            if(Byte.compare(iResp[index], (byte)0x8e) == 0)
            {
                byte[] calcMac = Algorithms.macEnc(iKeySig, AppUtil.getIsoPad(AppUtil.appendByteArray(AppUtil.appendByteArray(seq, encObj), dataObj)));
                setIndex(index, 1);//index++;
                if(Byte.compare(iResp[index], (byte)0x08) != 0)
                {
                    Log.e(TAG, "Verify secure message error: wrong length of MAC");
                    return null;
                }
                setIndex(index, 1);
                if(!Arrays.equals(calcMac, AppUtil.getSub(iResp, index, 8)))
                {
                    Log.e(TAG, "Verify secure message error: wrong MAC");
                    return null;
                }
                setIndex(index, 8);//index += 8;
                continue;
            }
            if(iResp[index] == (byte)0x87)
            {
                if(unsignedToBytes(iResp[index+1]) > unsignedToBytes((byte)0x80))
                {
                    int lgn = 0;
                    int llen = unsignedToBytes(iResp[index + 1]) -  0x80;
                    if(llen == 1)
                        lgn = unsignedToBytes(iResp[index + 2]);
                    if (llen == 2)
                        lgn = (iResp[index + 2] << 8) | iResp[index + 3];
                    encObj = AppUtil.getSub(iResp, index, llen + lgn + 2);
                    encData = AppUtil.getSub(iResp, index + llen + 3, lgn - 1);
                    setIndex(index, llen, lgn, 2);
                }
                else
                {
                    encObj = AppUtil.getSub(iResp, index, iResp[index + 1] + 2);
                    encData = AppUtil.getSub(iResp, index + 3, iResp[index + 1] - 1);
                    setIndex(index, iResp[index + 1], 2);
                }
                continue;
            }
            else
            {
                if(Byte.compare(iResp[index], (byte)0x85) == 0)
                {
                    if(Byte.compare(iResp[index+1], (byte)0x80) > 0)
                    {
                        int lgn = 0;
                        int llen = iResp[index + 1] - 0x80;
                        if(llen == 1) lgn = iResp[index + 2];
                        if(llen == 2) lgn = (iResp[index + 2] << 8) | iResp[index + 3];
                        encObj = AppUtil.getSub(iResp, index, llen + lgn + 2);
                        encData = AppUtil.getSub(iResp, index + llen + 2, lgn);
                        setIndex(index,llen,lgn,2);
                    }
                    else
                    {
                        encObj = AppUtil.getSub(iResp, index, iResp[index + 1] + 2);
                        encData = AppUtil.getSub(iResp,index + 2, iResp[index + 1]);
                        setIndex(index, iResp[index + 1],2);
                    }
                    continue;
                }
                else
                {
                    Log.e(TAG, "Unexpected Tag in secure message");
                    return null;
                }
            }
        } while(index < iResp.length);

        if(encData != null)
        {
            if(!iOdd)
            {
                return isoRemove(Algorithms.desDec(iKeyEnc, encData));
            }
        }

        return null;
    }

    private void setIndex(int... iArgs)
    {
        int tmpIndex = 0;
        int tmpSignum = 0;
        for(int i = 0; i < iArgs.length; ++i)
        {
            if(Math.signum(iArgs[i]) < 0)
            {
                tmpSignum = iArgs[i] & 0xFF;
                tmpIndex += tmpSignum;
            }
            else
            {
                tmpIndex += iArgs[i];
            }
        }
        this.index = tmpIndex;
    }

    private static int unsignedToBytes(byte iByte)
    {
        return iByte & 0xFF;
    }

    private byte[] isoRemove(byte[] iData) throws Exception
    {
        int i;
        for (i = iData.length - 1; i >= 0; i--)
        {
            if (iData[i] == (byte)0x80)
                break;
            if (iData[i] != 0x00)
            {
                Log.e(TAG, "ISO padding not present");
                return null;
            }
        }
        return AppUtil.getLeft(iData, i);
    }

    private void parse11(byte[] iData)
    {
        if(iData != null && iData.length > 0)
        {
            String[] fullName = ParseLib.parseFullName(ParseLib.icaoGetValueFromKey(ParseLib.KEY_FULL_NAME, iData));

            if(fullName.length == 2)
            {
                String surname = fullName[0];
                surname = surname.replace("<", " ");
                mCIE.setCognome(surname);
                String name = fullName[1];
                name = name.replace("<", " ");
                mCIE.setNome(name);
            }

            String[] address = ParseLib.parseAddress(ParseLib.icaoGetValueFromKey(ParseLib.KEY_ADDRESS, iData));
            if(address.length == 3)
            {
                String a = address[0] + ", " + address[1] + " (" + address[2] + ")";
                mCIE.setIndirizzoResidenza(address[0]);
                mCIE.setResidenza(address[1]);
                mCIE.setProvincia(address[2]);
            }

            String cf = ParseLib.icaoGetValueFromKey(ParseLib.KEY_CF, iData);
            mCIE.setCf(cf);

            String[] birthAddress = ParseLib.parseAddress(ParseLib.icaoGetValueFromKey(ParseLib.KEY_BIRTH_ADDRESS, iData));
            if(birthAddress.length == 2)
            {
                String birthCity = birthAddress[0];
                String birthProv = birthAddress[1];
                mCIE.setLuogoDiNascita(birthCity);
                mCIE.setProvinciaDiNascita(birthProv);
            }

            String dateOfBirth = ParseLib.icaoGetValueFromKey(ParseLib.KEY_BIRTH_DATE, iData);
            if(dateOfBirth != null && dateOfBirth.length() == 8)
            {
                String y = dateOfBirth.substring(0, 4);
                String m = dateOfBirth.substring(4, 6);
                String d = dateOfBirth.substring(6, 8);
                mCIE.setAnnoNascita(y);
                mCIE.setMeseNascita(m);
                mCIE.setGiornoNascita(d);
            }

            Log.i(TAG, "parse11 CIE object = " + mCIE.toString());
        }
    }

    private void parse12(byte[] iData)
    {
        String issuingAuthority = ParseLib.icaoGetValueFromKey(ParseLib.KEY_ISSUING_AUTHORITY, iData);
        mCIE.setEnteRilascio(issuingAuthority);
        String dateOfIssue = ParseLib.icaoGetValueFromKey(ParseLib.KEY_DATE_OF_ISSUE, iData);
        mCIE.setDataEmissione(dateOfIssue);
        Log.i(TAG, "parse12 CIE object = " + mCIE.toString());
    }
}
