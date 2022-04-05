package it.ltm.scp.module.android.managers.secure;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;
import android.util.Log;

import it.ltm.scp.module.android.BuildConfig;
import it.ltm.scp.module.android.exceptions.AppSignatureException;
import it.ltm.scp.module.android.model.Result;
import it.ltm.scp.module.android.utils.Constants;
import it.ltm.scp.module.android.utils.Properties;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.interfaces.RSAPublicKey;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by HW64 on 14/09/2016.
 */
public class SecureManager {

    private static final String TAG = SecureManager.class.getSimpleName();
    private static final String ALIAS = "serviceMarket";
    private static final String ALIAS_SYM = "serviceMarket_sym";
    public static final int OP_SUCCESS = 0;
    public static final int OP_IGNORED = 1;
    public static final int OP_EXCEPTION = 2;
    public static final int OP_VERIFY_FAIL = 3;

    private final int AES_CBC_IV_SIZE = 16;


    public static final String OP_EXCEPTION_MESSAGE = "Errore di sicurezza, riprovare o contattare il supporto";

    private static SecureManager mInstance;

    private SecureManager() {
    }

    public static synchronized SecureManager getInstance() {
        if (mInstance == null) {
            mInstance = new SecureManager();
        }
        return mInstance;
    }

    public Result initKeys(boolean shouldReset) {
        try {
            KeyStore store = getKeyStore();

            Log.d(TAG, "Keys: init procedure");

            if (shouldReset) {
                deleteKeys();
                Log.d(TAG, "Keys: reset = true, deleting..");
            }

            if (store.containsAlias(ALIAS)) {
                // chiavi gia' esistenti.
                Log.d(TAG, "Keys: already exits, skip creation");
                return new Result(OP_IGNORED);
            }

            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_RSA, "AndroidKeyStore");
            keyPairGenerator.initialize(new KeyGenParameterSpec.Builder(
                    ALIAS,
                    KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                    .setDigests(KeyProperties.DIGEST_SHA256, KeyProperties.DIGEST_SHA512)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1)
                    .setKeySize(1024)
                    .build());
            keyPairGenerator.genKeyPair();
            Log.d(TAG, "Keys: asymmetric keys generated into KeyStore");
            return new Result(OP_SUCCESS);

        } catch (Exception e) {
            Log.e(TAG, "", e);
            return new Result(OP_EXCEPTION);
        }
    }

    private SecretKey getSecuredSymKey() throws Exception {
        KeyStore keyStore = getKeyStore();
        Log.d(TAG, "getSymKeys: initialize process");
        if (keyStore.containsAlias(ALIAS_SYM)) {
            Log.d(TAG, "getSymKeys: key already exists, skip create process.");
            SecretKey key = (SecretKey) keyStore.getKey(ALIAS_SYM, null);
            return key;
        }
        Log.d(TAG, "getSymKeys: key not found, creating symmetric AES key.");
        KeyGenerator keyGenerator = KeyGenerator.getInstance(
                KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
        keyGenerator.init(
                new KeyGenParameterSpec.Builder(ALIAS_SYM,
                        KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                        .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                        .setRandomizedEncryptionRequired(false)
                        .build());
        SecretKey key = keyGenerator.generateKey();
        Log.d(TAG, "getSymKeys: symmetric AES key generated into KeyStore");
        return key;
    }

    public boolean validateKeys() {
        Log.d(TAG, "Keys: validating keys..");
        String expected = "test";
        String encrypted = encryptString(expected);
        String decrypted = decryptString(encrypted);
        return expected.equals(decrypted);
    }

    public void symmetricTest() {
        Log.d(TAG, "symmetricTest: init test");
        try {
            String expected = "sym_test";
            byte[] encrypted = encryptFileBytes(expected.getBytes());
            byte[] decrypted = decryptFileBytes(encrypted);
            String decryptedStr = new String(decrypted);
            if (expected.equals(decryptedStr)) {
                Log.d(TAG, "symmetricTest: test successful");
            } else {
                Log.e(TAG, "symmetricTest: test KO: " +
                        "expected: " + expected
                        + "decrypted: " + decryptedStr);
            }
        } catch (Exception e) {
            //TODO InvalidKeyException resettare?
            Log.e(TAG, "symmetricTest: ", e);
        }
    }

    public void deleteKeys() throws Exception {
        getKeyStore().deleteEntry(ALIAS);
    }

    public String getModulus() throws Exception {
        RSAPublicKey key = (RSAPublicKey) getKeyStore().getCertificate(ALIAS).getPublicKey();
        return key.getModulus().toString(16);
    }

    public String getExponent() throws Exception {
        RSAPublicKey key = (RSAPublicKey) getKeyStore().getCertificate(ALIAS).getPublicKey();
        return key.getPublicExponent().toString(16);
    }

    private KeyStore getKeyStore() throws Exception {
        KeyStore keyStore = null;
        keyStore = KeyStore.getInstance("AndroidKeyStore");
        keyStore.load(null);
        return keyStore;
    }

    private Cipher getCipherEncryptInstance() throws Exception {
        PublicKey publicKey = getKeyStore().getCertificate(ALIAS).getPublicKey();
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher;
    }

    private Cipher getCipherDecryptInstance() throws Exception {
        PrivateKey privateKey = (PrivateKey) getKeyStore().getKey(ALIAS, null);
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return cipher;
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

    private Cipher getSymmetricCipherInstance(int mode, byte[] iv) throws Exception {
        SecretKey key = getSecuredSymKey();
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        cipher.init(mode, key, ivSpec);
        return cipher;
    }

    public String encryptString(String toEncrypt) {
        try {
            Cipher cipher = getCipherEncryptInstance();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            CipherOutputStream cipherOutputStream = new CipherOutputStream(outputStream, cipher);
            cipherOutputStream.write(toEncrypt.getBytes("UTF-8"));
            cipherOutputStream.close();
            byte[] vals = outputStream.toByteArray();
            return Base64.encodeToString(vals, Base64.DEFAULT);
        } catch (Exception e) {
            Log.e(TAG, "", e);
            return "";
        }
    }

    public String decryptString(String toDecrypt) {
        try {
            Cipher cipher = getCipherDecryptInstance();
            return new String(cipher.doFinal(Base64.decode(toDecrypt, Base64.DEFAULT)), "UTF-8");
        } catch (Exception e) {
            Log.e(TAG, "", e);
            return "";
        }
    }

    public byte[] encryptFileBytes(byte[] input) throws Exception {
        SecureRandom secureRandom = new SecureRandom();
        byte[] ivBytes = new byte[AES_CBC_IV_SIZE];
        secureRandom.nextBytes(ivBytes);
        Cipher cipher = getSymmetricCipherInstance(Cipher.ENCRYPT_MODE, ivBytes);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        CipherOutputStream cipherOutputStream = new CipherOutputStream(outputStream, cipher);

        // NOTE: workaround. too bit bytes doesn't writted correctly.
        // see: https://stackoverflow.com/questions/43371597/java-android-aes-decrypt-result-is-truncated-if-only-with-long-string
        byte[] bytes = input;

        int oneBulkSize = 4096;// temp value for proof of concept. might be bigger one.
        int numOfBulk = (bytes.length / oneBulkSize);
        for (int i = 0; i < numOfBulk; i++) {
            cipherOutputStream.write(bytes, oneBulkSize * i, oneBulkSize);
        }

        if ((bytes.length % oneBulkSize) != 0) {
            cipherOutputStream.write(bytes, oneBulkSize * numOfBulk, bytes.length % oneBulkSize);
        }

        cipherOutputStream.close();

        byte[] encrypted = outputStream.toByteArray();
        byte[] ivAndEcnrypted = new byte[ivBytes.length + encrypted.length];
        Log.d(TAG, "encryptFileBytes: sizes: \n"
                + "iv: " + ivBytes.length
                + "\n encrypt: " + encrypted.length
                + "\n iv+encr: " + ivAndEcnrypted.length);
        System.arraycopy(ivBytes, 0, ivAndEcnrypted, 0, ivBytes.length);
        System.arraycopy(encrypted, 0, ivAndEcnrypted, ivBytes.length, encrypted.length);
        return ivAndEcnrypted;

    }

    public byte[] decryptFileBytes(byte[] input) throws Exception {
        byte[] ivBytes = Arrays.copyOfRange(input, 0, AES_CBC_IV_SIZE);
        byte[] encrypted = Arrays.copyOfRange(input, AES_CBC_IV_SIZE, input.length);
        Log.d(TAG, "decryptFileBytes: sizes: \n"
                + "iv: " + ivBytes.length
                + "\n encrypt: " + encrypted.length
                + "\n input (iv+encr): " + input.length);
        Cipher cipher = getSymmetricCipherInstance(Cipher.DECRYPT_MODE, ivBytes);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(encrypted);
        CipherInputStream cipherInputStream = new CipherInputStream(byteArrayInputStream, cipher);

        int b;
        while ((b = cipherInputStream.read()) != -1) {
            outputStream.write(b);
        }
        outputStream.close();
        return outputStream.toByteArray();
    }

    public String decryptProperty(String property) {
        try {
            Cipher cipher = getPropertiesCipherInstance(Cipher.DECRYPT_MODE);
            byte[] decrypt = cipher.doFinal(Base64.decode(property, Base64.DEFAULT));
            return new String(decrypt, "UTF-8");
        } catch (Exception e) {
            Log.e(TAG, "decryptProperty: ", e);
            return null;
        }
    }


    public String generateToken(String timestamp, String phrase) {
        String combined = timestamp + phrase;
        MessageDigest digest = null;
        String token = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
            byte[] hashed = digest.digest(combined.getBytes(StandardCharsets.UTF_8));

            StringBuffer hexString = new StringBuffer();

            for (int i = 0; i < hashed.length; i++) {
                String hex = Integer.toHexString(0xff & hashed[i]);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            token = hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, "", e);
        }
        return token;
    }

    public void checkSignature(Context context) throws AppSignatureException {
        String message = "La firma attuale non coincide con la firma originale";
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo appInfo = manager.getPackageInfo(
                    BuildConfig.APPLICATION_ID, PackageManager.GET_SIGNATURES);

            Signature signature = appInfo.signatures[0];
            int hash = signature.hashCode();
            if (hash != Integer.parseInt(Properties.get(Constants.PROP_APP_SIGNATURE_HASH))) {
                throw new AppSignatureException(message);
            }

        } catch (PackageManager.NameNotFoundException e) {
            throw new AppSignatureException(e.getMessage());
        }
    }
}
