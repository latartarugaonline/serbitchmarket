/**
 Utility class
 **/

package it.ltm.scp.module.android.managers.cie;

import android.util.Log;

import java.security.MessageDigest;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AppUtil
{
    private static final String TAG = "AppUtil";

    public static byte[] hexStringToByteArray(String s) throws Exception
    {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for(int i = 0; i < len; i += 2)
        {
            data[i / 2] = (byte)((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    public static String bytesToHex(byte[] bytes) throws Exception
    {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for(int i = 0; i < bytes.length; i++)
        {
            sb.append(String.format("%02x", bytes[i]));
        }
        return sb.toString();
    }

    public static byte checkdigit(byte[] data) throws Exception
    {
        int i;
        int tot = 0;
        int curval = 0;
        int[] weight = new int[] { 7, 3, 1 };
        for (i = 0; i < data.length; i++)
        {
            char ch = Character.toUpperCase(((char)data[i]));
            if (ch >= 'A' && ch <= 'Z')
                curval = ch - 'A' + 10;
            else
            {
                if (ch >= '0' && ch <= '9')
                    curval = ch - '0';
                else
                {
                    if (ch == '<')
                        curval = 0;
                    else
                        throw new Exception("errore nel calcolo della check digit");
                }
            }
            tot += curval * weight[i % 3];
        }
        tot = tot % 10;
        return (byte)('0' + tot);
    }

    public static byte[] appendByte(byte[] a, byte b)throws Exception
    {
        byte[] c = new byte[a.length + 1];
        System.arraycopy(a, 0, c, 0, a.length);
        c[a.length] = b;
        return c;
    }

    public static byte[] getLeft(byte[] array,int num)throws Exception
    {
        if(num > array.length)
            return array.clone();
        byte data[] = new byte[num];
        System.arraycopy(array, 0, data, 0, num);
        return data;
    }

    public static byte[] getSha1(byte[] array) throws  Exception
    {
        MessageDigest md = MessageDigest.getInstance("SHA1");
        return md.digest(array);
    }

    public static byte[] appendByteArray(byte[] a, byte[]b)throws Exception
    {
        byte[] c = new byte[a.length + b.length];
        System.arraycopy(a, 0, c, 0, a.length);
        System.arraycopy(b, 0, c, a.length, b.length);
        return c;
    }

    public static void getRandomByte(byte[] array)throws Exception
    {
        Random r = new Random();
        r.nextBytes(array);
    }

    public static byte[] getIsoPad(byte[] data)throws Exception
    {
        int padLen;
        if((data.length & 0x7) == 0)
            padLen = data.length + 8;
        else
            padLen = data.length - (data.length & 0x7) + 0x08;
        byte[] padData = new byte[padLen];
        System.arraycopy(data, 0, padData, 0, data.length);
        padData[data.length] = (byte)0x80;
        for(int i = data.length + 1; i<padData.length;i++)
            padData[i] = 0;
        return padData;
    }

    public static byte[] getSub(byte[] array, int start,int num)throws Exception
    {
        if(Math.signum(num) < 0)
            num = num & 0xff;
        byte[] data = new byte[num];
        System.arraycopy(array, start, data, 0, data.length);
        return data;
    }

    public static byte[] getRight(byte[] array,int num)throws Exception
    {
        if(num > array.length)
            return array.clone();
        byte data[] = new byte[num];
        System.arraycopy(array, array.length - num, data, 0, num);

        return data;
    }

    public static byte[] stringXor(byte[] b1, byte[] b2) throws Exception
    {
        if(b1.length != b2.length)
            throw new Exception("String length are not equal.");

        byte[] data = new byte[b1.length];

        for(int i=0;i<b1.length;i++){
            data[i] = (byte)(b1[i]^b2[i]);
        }

        return data;
    }

    public static void increment (byte[] array) { increment(array,array.length-1); }

    public static void increment (byte[] array, int indice)
    {
        if(Byte.compare(array[indice],(byte)0xff) == 0)
        {
            array[indice] = 0x00;
            increment(array, (indice - 1));
        }
        else
        {
            array[indice] = (byte) (array[indice] + 1);
        }
    }

    public static byte[] asn1Tag(byte[] array,int tag)
    {
        byte[] _tag = tagToByte(tag);//1

        byte[] _len = lenToBytes(array.length);

        byte[] data = new byte[_tag.length+_len.length+array.length];

        System.arraycopy(_tag,0,data,0,_tag.length);
        System.arraycopy(_len,0,data,_tag.length,_len.length);
        System.arraycopy(array,0,data,_tag.length+_len.length,array.length);
        return data;
    }

    public static byte[] tagToByte(int value)
    {
        if(value<=0xff)
            return new byte[] { unsignedToBytes(value) };
        else if(value<=0xffff)
            return new byte[] { (byte)(value >> 8),(byte)(value & 0xff) };
        else if (value<=0xffffff)
            return new byte[] { (byte)(value>> 16),(byte)((value>> 8) & 0xff),(byte)(value & 0xff) };
        else if(value<=0xffffffff)
            return new byte[] { (byte)(value>>24),(byte)((value>> 16) & 0xff),(byte)((value>> 8) & 0xff),(byte)(value & 0xff) };
        return null;
    }

    static byte[] lenToBytes(int value)
    {
        if(value < 0x80)
            return new byte[] {(byte)value};
        else if(value <= 0xff)
            return new byte[] {(byte)0x81, (byte)value};
        else if(value<=0xffff)
            return new byte[] {(byte)0x82,(byte)(value >> 8), (byte)(value & 0xff)};
        else if(value<=0xffffff)
            return new byte[] {(byte)0x83,(byte)(value>> 16),(byte)((value>> 8) & 0xff),(byte)(value & 0xff)};
        else if(value<=0xffffffff)
            return new byte[] {(byte)0x84,(byte)(value>>24),(byte)((value>> 16) & 0xff),(byte)((value>> 8) & 0xff),(byte)(value & 0xff)};
        return null;
    }

    public static  int unsignedToBytes(byte b)
    {
        return b & 0xFF;
    }

    public static  byte unsignedToBytes(int b)
    {
        return (byte) (b & 0xFF);
    }

    public static int toUint(byte[] dataB)
    {
        if(dataB == null)
            return 0;
        int val = 0;
        for(int i = 0 ; i < dataB.length; i++)
            val = val << 8 | dataB[i];
        return val;
    }

    private static String calculateSingleApduLenght(String apdu){
        String apduSizeStr="000";
        int apduSize = apdu.length();
        if(apduSize < 10){
            apduSizeStr = "00" + apduSize;
        } else if(apduSize >= 10 && apduSize < 100){
            apduSizeStr = "0" + apduSize;
        } else if(apduSize >= 100 && apduSize < 1000){
            apduSizeStr = String.valueOf(apduSize);
        }
        return apduSizeStr + apdu;
    }

    private static String calculateTotalApduLenght(String apdu){
        String totalApduSizeStr="0000";
        int apduSize = apdu.length();
        if(apduSize < 10){
            totalApduSizeStr = "000" + apduSize;
        } else if(apduSize >= 10 && apduSize < 100){
            totalApduSizeStr = "00" + apduSize;
        } else if(apduSize >= 100 && apduSize < 1000){
            totalApduSizeStr = "0" + apduSize;
        } else if(apduSize >= 1000 && apduSize < 10000){
            totalApduSizeStr = String.valueOf(apduSize);
        }
        return totalApduSizeStr + apdu;
    }

    public static String apduWithLength(String iAPDU)
    {
        String singleApduLenght = calculateSingleApduLenght(iAPDU);
        return calculateTotalApduLenght(singleApduLenght);

        /*String retValue = null;
        if(iAPDU != null)
        {
            retValue = iAPDU;
            int apduLength = iAPDU.length();
            String apduLengthStr = Integer.toString(apduLength);

            if(apduLength == 0)
            {
                retValue = null;
            }
            else if(apduLength < 10)
            {
                retValue = "00" + apduLengthStr + retValue;
            }
            else if(apduLength < 100)
            {
                retValue = "0" + apduLengthStr + retValue;
            }

            //Log.d(TAG, "apduWithLength retValue_1 = " + retValue);

            int msgLength = apduLength + apduLengthStr.length();
            String msgLengthStr = Integer.toString(msgLength);

            if(msgLength == 0)
            {
                retValue = null;
            }
            else if(msgLength < 10)
            {
                retValue = "000" + msgLengthStr + retValue;
            }
            else if(apduLength < 100)
            {
                retValue = "00" + msgLengthStr + retValue;
            }
            else if(apduLength < 1000)
            {
                retValue = "0" + msgLengthStr + retValue;
            }

            //Log.d(TAG, "apduWithLength retValue_2 = " + retValue);
        }
        return retValue;*/
    }

    public static byte[] getApduData(String iApduResponse) throws Exception
    {
        final int APDU_PREAMBLE_LENGTH = 7;
        final int APDU_STATUS_WORD_LENGTH = 4;
        if(iApduResponse.length() >= APDU_PREAMBLE_LENGTH + APDU_STATUS_WORD_LENGTH)
        {
            String hexDataAdpu = iApduResponse.substring(APDU_PREAMBLE_LENGTH, iApduResponse.length() - APDU_STATUS_WORD_LENGTH);
            return AppUtil.hexStringToByteArray(hexDataAdpu);
        }
        else
        {
            Log.e(TAG, "(getApduData): Unexpected adpu length error.");
            return null;
        }
    }

    public static int getStatusWord(String iApduResponse)
    {
        final int APDU_STATUS_WORD_LENGTH = 4;
        String hexDataAdpuStatusWord = iApduResponse.substring(iApduResponse.length() - APDU_STATUS_WORD_LENGTH, iApduResponse.length());
        return Integer.parseInt(hexDataAdpuStatusWord, 16);
    }
}
