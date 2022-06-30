package it.ltm.scp.module.android;

import android.util.Base64;
import android.util.Log;

import it.ltm.scp.module.android.devices.printer.DocumentBuilderImpl;
import it.ltm.scp.module.android.exceptions.InvalidArgumentException;

import org.junit.BeforeClass;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by HW64 on 07/09/2016.
 */

public class DocumentBuilderTest {

    private static DocumentBuilderImpl builder;

    @BeforeClass
    public static void setUpBeforeClass(){
        builder = new DocumentBuilderImpl();
    }

    @Test
    public void setFontTest1() throws InvalidArgumentException {
        builder.setFont("a");
    }

    @Test(expected = InvalidArgumentException.class)
    public void setFontTest2() throws InvalidArgumentException {
        builder.setFont("c");
    }

    @Test(expected = InvalidArgumentException.class)
    public void setSizeTest1() throws InvalidArgumentException {
        builder.setSize(-3);
    }

    @Test(expected = InvalidArgumentException.class)
    public void setSizeTest2() throws InvalidArgumentException {
        builder.setSize(15);
    }

    @Test(expected = InvalidArgumentException.class)
    public void setFeedTest1() throws InvalidArgumentException {
        builder.setFeed(0, "f");
    }

    @Test(expected = InvalidArgumentException.class)
    public void setFeedTest2() throws InvalidArgumentException {
        builder.setFeed(256, "b");
    }

    @Test
    public void setFeedTest3() throws InvalidArgumentException {
        builder.setFeed(255, "b");
    }

    @Test
    public void setFeedTest4() throws InvalidArgumentException {
        builder.setFeed(1, "f");
    }

    @Test(expected = InvalidArgumentException.class)
    public void setLineTest1() throws InvalidArgumentException {
        builder.setLine(-1);
    }

    @Test(expected = InvalidArgumentException.class)
    public void setLineTest2() throws InvalidArgumentException {
        builder.setLine(128);
    }

    @Test
    public void setLineTest3() throws InvalidArgumentException {
        builder.setLine(0);
    }

    @Test
    public void setLineTest4() throws InvalidArgumentException {
        builder.setLine(127);
    }

    @Test(expected = InvalidArgumentException.class)
    public void setSpacingTest1() throws InvalidArgumentException {
        builder.setSpacing(-1);
    }

    @Test(expected = InvalidArgumentException.class)
    public void setSpacingTest2() throws InvalidArgumentException {
        builder.setSpacing(256);
    }

    @Test
    public void setSpacingTest3() throws InvalidArgumentException {
        builder.setSpacing(0);
    }

    @Test
    public void setSpacingTest4() throws InvalidArgumentException {
        builder.setSpacing(255);
    }

    @Test(expected = InvalidArgumentException.class)
    public void setAlignTest1() throws InvalidArgumentException {
        builder.setAlign("fuori");
    }

    @Test
    public void setAlignTest2() throws InvalidArgumentException {
        builder.setAlign("center");
        builder.setAlign("left");
        builder.setAlign("right");
    }

    @Test(expected = InvalidArgumentException.class)
    public void setUnderlineTest1() throws InvalidArgumentException {
        builder.setUnderline(-1);
    }



    @Test
    public void setUnderlineTest3() throws InvalidArgumentException {
        builder.setUnderline(0);
        builder.setUnderline(1);
        builder.setUnderline(2);
    }


    @Test(expected = InvalidArgumentException.class)
    public void setBarcodeTest1() throws InvalidArgumentException {
        // wrong code
        builder.setBarcode("1234", "EAN8", 2, 2, "a", "below", "auto");
    }

    @Test(expected = InvalidArgumentException.class)
    public void setBarcodeTest2() throws InvalidArgumentException {
        // wrong code
        builder.setBarcode("peppe", "EAN8", 2, 2, "a", "below", "auto");
    }

    @Test(expected = InvalidArgumentException.class)
    public void setBarcodeTest3() throws InvalidArgumentException {
        // wrong code
        builder.setBarcode("pippopoi", "EAN8", 2, 2, "a", "below", "auto");
    }

    @Test(expected = InvalidArgumentException.class)
    public void setBarcodeTest4() throws InvalidArgumentException {
        // wrong code
        builder.setBarcode("pip89poi", "EAN8", 2, 2, "a", "below", "auto");
    }

    @Test(expected = InvalidArgumentException.class)
    public void setBarcodeTest5() throws InvalidArgumentException {
        // wrong bctype
        builder.setBarcode("01234567", "pasta", 2, 2, "a", "below", "auto");
    }

    @Test(expected = InvalidArgumentException.class)
    public void setBarcodeTest6() throws InvalidArgumentException {
        // wrong width
        builder.setBarcode("01234567", "EAN8", 0, 2, "a", "below", "auto");
    }

    @Test(expected = InvalidArgumentException.class)
    public void setBarcodeTest7() throws InvalidArgumentException {
        // wrong width
        builder.setBarcode("01234567", "EAN8", 7, 2, "a", "below", "auto");
    }

    @Test(expected = InvalidArgumentException.class)
    public void setBarcodeTest8() throws InvalidArgumentException {
        // wrong height
        builder.setBarcode("01234567", "EAN8", 2, 0, "a", "below", "auto");
    }

    @Test(expected = InvalidArgumentException.class)
    public void setBarcodeTest9() throws InvalidArgumentException {
        // wrong height
        builder.setBarcode("01234567", "EAN8", 2, 13, "a", "below", "auto");
    }

    @Test(expected = InvalidArgumentException.class)
    public void setBarcodeTest10() throws InvalidArgumentException {
        // wrong font
        builder.setBarcode("01234567", "EAN8", 2, 2, "f", "below", "auto");
    }

    @Test(expected = InvalidArgumentException.class)
    public void setBarcodeTest11() throws InvalidArgumentException {
        // wrong position
        builder.setBarcode("01234567", "EAN8", 2, 2, "a", "nonsaprei", "auto");
    }

    @Test
    public void setUnderline() throws InvalidArgumentException {
        builder.setUnderline(2);
        builder.setText("Test underline");

    }
    @Test
    public void setBarcodeTest12() throws InvalidArgumentException {
        builder.setBarcode("01234567", "EAN8", 2, 2, "a", "below", "auto");
        builder.setBarcode("01234567", "UPC-E", 2, 2, "b", "off", "auto");
        builder.setBarcode("01234567", "CODE39", 2, 2, "a", "above", "auto");
        builder.setBarcode("01234567", "CODABAR", 2, 2, "a", "both", "auto");
    }

    @Test(expected = InvalidArgumentException.class)
    public void setImageTest1() throws InvalidArgumentException {
        //wrong format
        builder.setImage("nonsaprei", "center", 300, 300, "", 1);
    }

    @Test(expected = InvalidArgumentException.class)
    public void setImageTest2() throws InvalidArgumentException {
        //wrong align
        builder.setImage("normal", "nonsaprei", 300, 300, "", 1);
    }

    @Test(expected = InvalidArgumentException.class)
    public void setImageTest3() throws InvalidArgumentException {
        //wrong width
        builder.setImage("normal", "center", 0, 300, "", 1);
    }

    @Test(expected = InvalidArgumentException.class)
    public void setImageTest4() throws InvalidArgumentException {
        //wrong width
        builder.setImage("normal", "center", 577, 300, "", 1);
    }

    @Test(expected = InvalidArgumentException.class)
    public void setImageTest5() throws InvalidArgumentException {
        //wrong height
        builder.setImage("normal", "center", 300, 0, "", 1);
    }

    @Test(expected = InvalidArgumentException.class)
    public void setImageTest6() throws InvalidArgumentException {
        //wrong height
        builder.setImage("normal", "center", 300, 577, "", 1);
    }

    @Test
    public void setImageTest7() throws InvalidArgumentException {
        builder.setImage("normal", "center", 300, 300, "", 1);
        builder.setImage("double", "left", 300, 300, "", 1);
        builder.setImage("2width", "right", 300, 300, "", 1);
        builder.setImage("2height", "center", 300, 300, "", 1);
    }

    @Test(expected = InvalidArgumentException.class)
    public void setQrCodeTest1() throws InvalidArgumentException {
        //wrong model
        builder.setQrCode("prova1", 0, 3, 5, 10);
    }

    @Test(expected = InvalidArgumentException.class)
    public void setQrCodeTest2() throws InvalidArgumentException {
        //wrong model
        builder.setQrCode("prova1", 3, 3, 5, 10);
    }

    @Test(expected = InvalidArgumentException.class)
    public void setQrCodeTest3() throws InvalidArgumentException {
        //wrong errors
        builder.setQrCode("prova1", 1, -1, 5, 10);
    }

    @Test(expected = InvalidArgumentException.class)
    public void setQrCodeTest4() throws InvalidArgumentException {
        //wrong errors
        builder.setQrCode("prova1", 1, 4, 5, 10);
    }

    @Test(expected = InvalidArgumentException.class)
    public void setQrCodeTest5() throws InvalidArgumentException {
        //wrong mode
        builder.setQrCode("prova1", 1, 3, 0, 10);
    }

    @Test(expected = InvalidArgumentException.class)
    public void setQrCodeTest6() throws InvalidArgumentException {
        //wrong mode
        builder.setQrCode("prova1", 1, 3, 6, 10);
    }

    @Test(expected = InvalidArgumentException.class)
    public void setQrCodeTest7() throws InvalidArgumentException {
        //wrong mode
        builder.setQrCode("prova1", 1, 3, 5, 0);
    }

    @Test(expected = InvalidArgumentException.class)
    public void setQrCodeTest8() throws InvalidArgumentException {
        //wrong mode
        builder.setQrCode("prova1", 1, 3, 5, 11);
    }

    @Test
    public void setQrCodeTest9() throws InvalidArgumentException {
        builder.setQrCode("prova1", 1, 3, 5, 8);
    }

    @Test
    public void decryptProperty() {
        String property="JK3EYqkL9dJKSGPZbxMqrZecybCwAI3Uc+zC9LO2LzbuPkzrsbmO3BPaPMahZhWb";
        try {
            Cipher cipher = getPropertiesCipherInstance(Cipher.DECRYPT_MODE);
            byte[] decrypt = cipher.doFinal(Base64.decode(property, Base64.DEFAULT));
            System.out.println(new String(decrypt, "UTF-8"));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }


    private Cipher getPropertiesCipherInstance(int mode) throws NoSuchAlgorithmException, NoSuchPaddingException, UnsupportedEncodingException, InvalidKeyException, InvalidAlgorithmParameterException {
        byte[] decodedKey = Base64.decode(BuildConfig.PK, Base64.DEFAULT);
        SecretKey key = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
        Cipher cipher = null;
        cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        IvParameterSpec iv = new IvParameterSpec(BuildConfig.KIV.getBytes("UTF-8"));
        cipher.init(mode, key, iv);
        return cipher;
    }


}
