package com.nomi.smartkeyprogrammer.utils;

import android.util.Log;

import java.util.Formatter;

/**
 * Created by nomi on 2/8/2018.
 */

public class HexUtils {

    public static String convertByteArrayToHex(byte[] bytes) {
        Formatter formatter = new Formatter();
        for (byte b : bytes) {
            formatter.format("%02x", b);
        }
        String hex = formatter.toString();
        Log.i("TAG", "hex = " + hex);
        return hex;
    }

    public static byte[] convertHexToByteArray(String hex) {
        int len = hex.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                    + Character.digit(hex.charAt(i+1), 16));
        }
        return data;
    }

}
