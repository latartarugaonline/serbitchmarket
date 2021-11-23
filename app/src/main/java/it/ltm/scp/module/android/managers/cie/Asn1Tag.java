package it.ltm.scp.module.android.managers.cie;

import android.util.Log;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.io.ByteArrayInputStream;
import java.lang.Math;

public class Asn1Tag
{
    private static final String TAG = "Asn1Tag";

    static class Ref<T>
    {
        public T value;

        public Ref(T value)
        {
            this.value = value;
        }
    }

    public enum Asn1TagType
    {
        TAG_MASK(31),
        BOOLEAN(1),
        INTEGER(2),
        BIT_STRING(3),
        OCTET_STRING(4),
        TAG_NULL(5),
        OBJECT_IDENTIFIER(6),
        OBJECT_DESCRIPTOR(7),
        EXTERNAL(8),
        REAL(9),
        ENUMERATED(10),
        UTF8_STRING(12),
        RELATIVE_OID(13),
        SEQUENCE(16),
        SET(17),
        NUMERIC_STRING(18),
        PRINTABLE_STRING(19),
        T61_STRING(20),
        VIDEOTEXT_STRING(21),
        IA5_STRING(22),
        UTC_TIME(23),
        GENERALIZED_TIME(24),
        GRAPHIC_STRING(25),
        VISIBLE_STRING(26),
        GENERAL_STRING(27),
        UNIVERSAL_STRING(28),
        BMPSTRING(30);

        private int value;

        private Asn1TagType(int value)
        {
            this.value = value;
        }

        public int getValue()
        {
            return value;
        }
    }

    public enum Asn1TagClasses
    {
        CLASS_MASK(192),
        UNIVERSAL(0),
        CONSTRUCTED(32),
        APPLICATION(64),
        CONTEXT_SPECIFIC(128),
        PRIVATE(192),
        UNKNOWN(255);

        private int value;

        private Asn1TagClasses(int value)
        {
            this.value = value;
        }

        public int getValue()
        {
            return value;
        }
    }

    byte[] data;

    byte unusedBits = 0;
    long startPos, endPos;

    byte[] tag;

    List<Asn1Tag> children;

    public byte[] getData()
    {
        return data;
    }

    public long tagRawNumber()
    {
        long num = tag[0];
        for (int i = 1; i < tag.length; i++)
        {
            num = (long)(num << 8) | tag[i];
        }
        return Math.abs(num);
    }

    public long tagNumber()
    {
        long num = 0;
        num |= (long)(tag[0] & 0x1f);
        for(int i = 1; i < tag.length; i++)
        {
            int shift;
            if (i == 1) shift = 5;
            else shift = 7;
            num = (long)(num << shift) | tag[i];
        }
        return num;
    }

    public boolean tagConstructed()
    {
        return (tag[0] & 0x20) != 0;
    }

    public Asn1TagClasses tagClass()
    {
        switch (tag[0] & 0xc0)
        {
            case 0x00:
                return Asn1TagClasses.UNIVERSAL;
            case 0x40:
                return Asn1TagClasses.APPLICATION;
            case 0x80:
                return Asn1TagClasses.CONTEXT_SPECIFIC;
            case 0xc0:
                return Asn1TagClasses.PRIVATE;
        };
        return Asn1TagClasses.UNKNOWN;
    }

    public Asn1Tag checkTag(long tagCheck)
    {
        if(tagRawNumber() != tagCheck)
            return null;
        return this;
    }

    public Asn1Tag checkTag(byte[] tagCheck)
    {
        if(!Arrays.equals(tag, tagCheck))
            return null;
        return this;
    }

    public Asn1Tag child(int tagNum)
    {
        return children.get(tagNum);
    }

    public Asn1Tag child(int tagNum, long tagCheck)
    {
        Asn1Tag tag = children.get(tagNum);
        if(tag.tagRawNumber() != tagCheck)
            return null;
        return tag;
    }

    public boolean verify(byte[] dataCheck)
    {
        return Arrays.equals(data, dataCheck);
    }

    public Asn1Tag child(int tagNum, byte[] tagCheck)
    {
        Asn1Tag subTag = children.get(tagNum);
        if(!Arrays.equals(subTag.tag, tagCheck))
            return null;
        return subTag;
    }

    public Asn1Tag(int tag, byte[] data)
    {
        this.tag = new byte[] { (byte)(tag) };
        this.data = data;
        this.children = null;
    }

    public Asn1Tag(byte[] tag, byte[] data)
    {
        this.tag = tag;
        this.data = data;
        this.children = null;
    }

    public Asn1Tag(byte[] tag)
    {
        this.tag = tag;
        this.data = null;
        this.children = null;
    }

    public Asn1Tag(byte[] tag, List<Asn1Tag> children)
    {
        this.tag = tag;
        this.data = null;
        this.children = children;
    }

    public Asn1Tag(int tag, List<Asn1Tag> children)
    {
        this.tag = new byte[] { (byte)(tag) };
        this.data = null;
        this.children = children;
    }

    public static int parseLength(ByteArrayInputStream b, long length)
    {
        return parseLength(b, 0, length);
    }

    public static int parseLength(byte[] data)
    {
        ByteArrayInputStream b = new ByteArrayInputStream(data);
        return parseLength(b, data.length);
    }

    static int parseLength(ByteArrayInputStream s, long start, long length)
    {
        long size = 0;
        long readPos = 0;
        if(readPos == length)
            return -1;

        List<Byte> tagVal = new ArrayList<Byte>();
        int tag = s.read();

        readPos++;
        tagVal.add((byte)tag);

        if((tag & 0x1f) == 0x1f)
        {
            while(true)
            {
                if(readPos == length)
                    return -1;

                tag = s.read();

                readPos++;
                tagVal.add((byte)tag);
                if((tag & 0x80) != 0x80)
                {
                    break;
                }
            }
        }

        if(readPos == length)
            return -1;

        int len = s.read();
        Log.d(TAG, "parseLength: @@@@@@ len init: " + len);
        readPos++;

        if(len > 0x80)
        {
            int lenlen = len - 0x80;
            len = 0;
            for(int i = 0; i < lenlen; i++)
            {
                if(readPos == length)
                    return -1;

                int value = s.read();
                Log.d(TAG, "parseLength: value: " + value);
                int shiftedLen = (int)(len << 8);
                Log.d(TAG, "parseLength: shiftedLen: " + shiftedLen);
                len = (int)(shiftedLen | value);
                Log.d(TAG, "parseLength: @@@@@@ len[" + i + "]: " + len);
                readPos++;
            }
        }
        Log.d(TAG, "parseLength: readPos: " + readPos);
        size = Math.abs((int)(readPos + len));
        return (int)size;
    }

    public static Asn1Tag parse(byte[] data, boolean reparse)
    {
        ByteArrayInputStream b = new ByteArrayInputStream(data);
        Asn1Tag r = parse(b, data.length, reparse);
        return r;
    }

    public static Asn1Tag parse(byte[] data)
    {
        ByteArrayInputStream b = new ByteArrayInputStream(data);
        return parse(b, data.length, true);
    }

    public static Asn1Tag parse(ByteArrayInputStream s, long length)
    {
        return parse(s, length, true);
    }

    public static Asn1Tag parse(ByteArrayInputStream s, long length, boolean reparse)
    {
        Ref<Long> size = new Ref<Long>(0L);
        Ref<Boolean> hasErrors = new Ref<Boolean>(false);
        Asn1Tag r = parse(s, 0, length, size, reparse, hasErrors);
        return r;
    }

    static Asn1Tag parse(ByteArrayInputStream s, long start, long length, Ref<Long> size, boolean reparse, Ref<Boolean> hasErrors)
    {
        long readPos = 0;

        if(readPos == length)
        {
            hasErrors.value = true;
            return null;
        }

        List<Byte> tagVal = new ArrayList<Byte>();
        int tag = s.read();

        readPos++;
        tagVal.add((byte)tag);
        if((tag & 0x1f) == 0x1f)
        {
            while (true)
            {
                if(readPos == length)
                {
                    hasErrors.value = true;
                    return null;
                }

                tag = s.read();

                readPos++;
                tagVal.add((byte)tag);
                if((tag & 0x80) != 0x80)
                {
                    break;
                }
            }
        }
        if(readPos == length)
        {
            hasErrors.value = true;
            return null;
        }

        int len = s.read();

        readPos++;
        if(len > 0x80)
        {
            int lenlen = len - 0x80;
            len = 0;
            for (int i = 0; i < lenlen; i++)
            {
                if(readPos == length)
                {
                    hasErrors.value = true;
                    return null;
                }
                len = (int)((len << 8) | (byte)s.read());
                readPos++;
            }
        }
        else if(len == 0x80)
        {
            hasErrors.value = true;
            return null;
        }

        size.value = (long)(readPos + len);
        if(size.value > length)
        {
            hasErrors.value = true;
            return null;
        }

        if(tagVal.size() == 1 && tagVal.get(0) == 0 && len == 0)
        {
            return null;
        }

        byte[] data = new byte[len];
        s.read(data, 0, (int)len);

        ByteArrayInputStream ms = new ByteArrayInputStream(data);

        byte[] tagArr = new byte[tagVal.size()];
        for(int i = 0; i < tagVal.size(); i++)
            tagArr[i] = (byte)tagVal.get(i);

        Asn1Tag newTag = new Asn1Tag(tagArr);
        List<Asn1Tag> children = null;
        int parsedLen = 0;
        boolean parseSubTags = false;

        if(newTag.tagConstructed())
            parseSubTags = true;
        else if(reparse && knownTag(newTag.tag) == "OCTET STRING")
            parseSubTags = true;
        else if(reparse && knownTag(newTag.tag) == "BIT STRING")
        {
            parseSubTags = true;
            newTag.unusedBits = (byte)ms.read();
            parsedLen++;
        }

        if(parseSubTags)
        {
            children = new ArrayList<Asn1Tag>();
            while(true)
            {
                Ref<Long> childSize = new Ref<Long>(0L);
                Ref<Boolean> childHasErrors = new Ref<Boolean>(false);

                Asn1Tag child = parse(ms, start + readPos + parsedLen, (int)(len - parsedLen), childSize, reparse, childHasErrors);

                if(childHasErrors.value)
                {
                    children = null;
                    break;
                }

                if(child != null)
                    children.add(child);

                parsedLen += childSize.value;
                if(parsedLen > len)
                {
                    children = null;
                    break;
                }
                else if (parsedLen == len)
                {
                    data = null;
                    break;
                }
            }
        }

        newTag.startPos = start;
        newTag.endPos = start + size.value;
        if(children == null)
        {
            newTag.data = data;
        }
        else
        {
            newTag.children = children;
        }
        return newTag;
    }

    static String knownTag(byte[] tag)
    {
        if(tag.length == 1)
        {
            switch(tag[0])
            {
                case 2: return "INTEGER";
                case 3: return "BIT STRING";
                case 4: return "OCTET STRING";
                case 5: return "NULL";
                case 6: return "OBJECT IDENTIFIER";
                case 0x30: return "SEQUENCE";
                case 0x31: return "SET";
                case 12: return "UTF8 String";
                case 19: return "PrintableString";
                case 20: return "T61String";
                case 22: return "IA5String";
                case 23: return "UTCTime";
            }
        }
        return null;
    }

    public String toString()
    {
        String val = knownTag(tag);
        if(val == null)
            val = tagClass().toString() + " " + tagNumber();
        if (tagConstructed()) val += " Constructed ";
        val += " (" + new String(tag)+ ") ";
        return val;
    }
}
