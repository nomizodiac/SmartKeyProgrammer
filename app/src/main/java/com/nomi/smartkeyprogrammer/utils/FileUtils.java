package com.nomi.smartkeyprogrammer.utils;

import android.content.Context;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileUtils {

    private static final String TAG = FileUtils.class.getSimpleName();

    public static void createSKPDirectory(Context context) {
        String dirPath = getSKPDirectoryPath((context));
        if(isFileExist(dirPath))
            Log.i(TAG, "SKP directory created");
        else
            Log.i(TAG, "Error in creating SKP directory");
    }

    public static String getSKPDirectoryPath(Context context) {
        String externalStoragePath, skpDirectoryPath;
        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
            externalStoragePath = android.os.Environment.getExternalStorageDirectory().getAbsolutePath();
        else {
            externalStoragePath = context.getFilesDir().getAbsolutePath();
        }

        String dirName = "SmartKeyProgrammer";
        File skpDirectory = new File(externalStoragePath + "/" + dirName);
        if (!skpDirectory.exists())
            skpDirectory.mkdirs();

        skpDirectoryPath = skpDirectory.getPath();
        return skpDirectoryPath;
    }

    public static File getInputFile(Context context) {
        File outputFile = new File(getSKPDirectoryPath(context) + "/" + "input.bin");
        if(isFileExist(outputFile.getPath()))
            return outputFile;
        return null;
    }

    public static File getOutputFile(Context context) {
        File outputFile = new File(getSKPDirectoryPath(context), "output.bin");
        if (outputFile.exists()) {
            outputFile.delete();
        }
        return outputFile;
    }

    public static byte[] getBytesFromFile(File file) throws IOException {
        // Get the size of the file
        long length = file.length();
        // You cannot create an array using a long type.
        // It needs to be an int type.
        // Before converting to an int type, check
        // to ensure that file is not larger than Integer.MAX_VALUE.
        if (length > Integer.MAX_VALUE) {
            // File is too large
            throw new IOException("File is too large!");
        }
        // Create the byte array to hold the data
        byte[] bytes = new byte[(int)length];

        // Read in the bytes
        int offset = 0;
        int numRead = 0;
        InputStream is = new FileInputStream(file);
        try {
            while (offset < bytes.length
                    && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
                offset += numRead;
            }
        } finally {
            is.close();
        }
        // Ensure all the bytes have been read in
        if (offset < bytes.length) {
            throw new IOException("Could not completely read file "+file.getName());
        }
        return bytes;
    }

    public static void writeBytesToFile(File file, byte[] bytes) throws IOException {
        FileOutputStream fos = new FileOutputStream(file.getPath());
        BufferedOutputStream bos = new BufferedOutputStream(fos);
        bos.write(bytes);
        bos.flush();
        bos.close();
    }

    public static boolean isFileExist(String filePath) {
        File file = new File(filePath);
        if(file.exists())
            return true;
        return false;
    }
}