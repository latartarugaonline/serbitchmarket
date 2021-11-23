package it.ltm.scp.module.android.managers;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import it.ltm.scp.module.android.App;
import it.ltm.scp.module.android.managers.secure.SecureManager;
import it.ltm.scp.module.android.model.MultipartUploadRequestInfo;

public class InternalStorage {

    private final static String TAG = InternalStorage.class.getSimpleName();

    /**
     * Salva su disco un file criptato con la coppia di chiavi pubblica/privata generata da SecureManager.
     *
     * @param content
     * @param fileName
     * @see it.ltm.scp.module.android.managers.secure.SecureManager
     */
    public static void saveFileWithEncryption(String content, String fileName) throws Exception {
        Log.d(TAG, "saveFileWithEncryption() called with: content = " + content.length() + ", fileName = [" + fileName + "]");
        byte[] originalBytes = content.getBytes("UTF-8");
        Log.d(TAG, "saveFileWithEncryption: original Bytes: " +originalBytes.length);
        byte[] encryptedBytes = SecureManager.getInstance().encryptFileBytes(originalBytes);
        Log.d(TAG, "saveFileWithEncryption: encryptedBytes: " +encryptedBytes.length);
        Context context = App.getContext();
        File file = new File(context.getFilesDir(), fileName);
        Log.d(TAG, "saveFileWithEncryption: file path: " + file.getAbsolutePath());
        FileOutputStream fileOutputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE);
        fileOutputStream.write(encryptedBytes);
        fileOutputStream.close();
    }

    public static String loadFileWithEncryption(String fileName) throws Exception {
        Log.d(TAG, "loadFileWithEncryption() called with: fileName = [" + fileName + "]");
        Context context = App.getContext();
        File file = new File(context.getFilesDir(), fileName);

        FileInputStream fileInputStream = context.openFileInput(fileName);
        byte[] bytes = new byte[(int)file.length()];
        Log.d(TAG, "loadFileWithEncryption: loaded file bytes: " + bytes.length);
        fileInputStream.read(bytes);
        fileInputStream.close();
        byte[] decrypted = SecureManager.getInstance().decryptFileBytes(bytes);
        Log.d(TAG, "loadFileWithEncryption: decrypted file bytes: " + decrypted.length);
        return new String(decrypted, "UTF-8");
    }

    public static void saveObjectToFile(Object obj, String fileName) throws Exception {
        ObjectOutputStream objectOutputStream = null;
        try {
            Log.d(TAG, "saveObjectWithEncryption() called with: obj = [" + obj + "], fileName = [" + fileName + "]");
            Context context = App.getContext();
            File file = new File(context.getFilesDir(), fileName);
            Log.d(TAG, "saveFileWithEncryption: file path: " + file.getAbsolutePath());
            FileOutputStream fileOutputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(obj);
        } finally {
            if(objectOutputStream != null)
                objectOutputStream.close();
        }
    }

    public static Object loadObjectFromFile(String fileName) throws Exception {
        ObjectInputStream objectInputStream = null;
        try {
            Log.d(TAG, "loadObjectFromFile() called with: fileName = [" + fileName + "]");
            Context context = App.getContext();
            File file = new File(context.getFilesDir(), fileName);
            FileInputStream fileInputStream = context.openFileInput(fileName);
            objectInputStream = new ObjectInputStream(fileInputStream);
            return objectInputStream.readObject();
        } finally {
            if(objectInputStream != null)
                objectInputStream.close();
        }
    }

    public static void deleteFile(String fileName){
        //debug:
        Context context = App.getContext();

        File file = new File(context.getFilesDir(), fileName);
        Log.d(TAG, "deleteFile: check if file exists: " + file.exists());
        context.deleteFile(fileName);
        Log.d(TAG, "deleteFile: check if file exists: " + file.exists());
    }
}
