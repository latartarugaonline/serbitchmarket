package it.ltm.scp.module.android.managers.cie;

import java.util.Arrays;
import java.util.ArrayList;
import android.util.Log;

public class ParseLib
{
    private static final String TAG = "APP_CIE";

    static class ArrayUtils
    {
        // subarray
        public static<T> T[] subArray(T[] array, int beg, int end)
        {
            return Arrays.copyOfRange(array, beg, end);
        }

        public static byte[] subArray(byte[] array, int beg, int end)
        {
            return Arrays.copyOfRange(array, beg, end);
        }

        // true if array and canditate are not empty and canditate is greater than array
        static boolean isEmptyLocate(byte[] array, byte[] candidate)
        {
            return array == null
                || candidate == null
                || array.length == 0
                || candidate.length == 0
                || candidate.length > array.length;
        }

        // true if candidate is found into array
        static boolean isMatch(byte[] array, int position, byte[] candidate)
        {
            if(candidate.length > (array.length - position))
                return false;

            for(int i = 0; i < candidate.length; i++)
                if (array[position + i] != candidate[i])
                    return false;

            return true;
        }

        // Find the indices of "candidate" in "self"
        public static int[] locate(byte[] self, byte[] candidate)
        {
            if (isEmptyLocate(self, candidate))
                return null;

            ArrayList<Integer> list = new ArrayList<Integer>();

            for(int i = 0; i < self.length; i++)
            {
                if (!isMatch(self, i, candidate))
                    continue;

                list.add(i);
            }

            int[] listArr = new int[list.size()];
            for(int i = 0; i < list.size(); i++)
                listArr[i] = (int)list.get(i);

            return listArr;
        }
    }

    /* ICAO keys */
    public static final byte[] KEY_FULL_NAME     = new byte[] { 0x5F, 0x0E };
    public static final byte[] KEY_BIRTH_ADDRESS = new byte[] { 0x5F, 0x11 };
    public static final byte[] KEY_ADDRESS       = new byte[] { 0x5F, 0x42 };
    public static final byte[] KEY_CF            = new byte[] { 0x5F, 0x10 };
    public static final byte[] KEY_MRZ           = new byte[] { 0x5F, 0x1F };
    public static final byte[] KEY_DATE_ISSUE    = new byte[] { 0x5F, 0x26 };
    public static final byte[] KEY_BIRTH_DATE    = new byte[] { 0x5F, 0x2B };

    // dg12
    public static final byte[] KEY_ISSUING_AUTHORITY    = new byte[] { 0x5F, 0x19 };
    public static final byte[] KEY_DATE_OF_ISSUE        = new byte[] { 0x5F, 0x26 };

    public static final byte[] jpg2kMagicNumber = {0x00, 0x00, 0x00, 0x0C, 0x6A, 0x50, 0x20, 0x20, 0x0D, 0x0A};//, (byte)0x87, 0x0A, 0x00, 0x00, 0x00, 0x14, 0x66, 0x74, 0x79, 0x70, 0x6A, 0x70, 0x32};

    public static byte[] imageRetrive(byte[] blob)
    {
        int loc = ArrayUtils.locate(blob, jpg2kMagicNumber)[0];
        return ArrayUtils.subArray(blob, loc, blob.length - loc);
    }

    public static String icaoGetValueFromKey(byte[] key, byte[] dg)
    {
        int[] index = ArrayUtils.locate(dg, key);

        if (index.length == 0)
            return null;

        int i = index.length == 1 ? index[0] : index[1];

        int sizeOfData = (int)(dg[i + key.length]);

        int start = i + key.length + 1;
        int end = start + sizeOfData;
        return new String(ArrayUtils.subArray(dg, start, end));
    }

    public static String[] parseFullName(String s)
    {
        String[] ret = new String[2];
        ret = s.split("<<", 2);
        return ret;
    }

    public static String[] parseAddress(String s)
    {
        String[] ret = new String[3];
        ret =  s.split("<", 3);
        return ret;
    }
}
