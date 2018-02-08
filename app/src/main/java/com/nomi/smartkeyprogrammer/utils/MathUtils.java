package com.nomi.smartkeyprogrammer.utils;

import java.util.ArrayList;

public class MathUtils {

    public static String getFullInverse(String s) {

        char char1 = s.charAt(0);
        char char2 = s.charAt(1);

        String char1Inv = getInverse(String.valueOf(s.charAt(0)));
        String char2Inv = getInverse(String.valueOf(s.charAt(1)));

        String finalInv= char1Inv + char2Inv;
        return finalInv;
    }

    public static String getInverse(String s) {


        if(s.equalsIgnoreCase("0") || s.equalsIgnoreCase("f"))
            return s.equalsIgnoreCase("0") ? "f" : "0";

        if(s.equalsIgnoreCase("1") || s.equalsIgnoreCase("e"))
            return s.equalsIgnoreCase("1") ? "e" : "1";

        if(s.equalsIgnoreCase("2") || s.equalsIgnoreCase("d"))
            return s.equalsIgnoreCase("2") ? "d" : "2";

        if(s.equalsIgnoreCase("3") || s.equalsIgnoreCase("c"))
            return s.equalsIgnoreCase("3") ? "c" : "3";

        if(s.equalsIgnoreCase("4") || s.equalsIgnoreCase("b"))
            return s.equalsIgnoreCase("4") ? "b" : "4";

        if(s.equalsIgnoreCase("5") || s.equalsIgnoreCase("a"))
            return s.equalsIgnoreCase("5") ? "a" : "5";

        if(s.equalsIgnoreCase("6") || s.equalsIgnoreCase("9"))
            return s.equalsIgnoreCase("6") ? "9" : "6";

        if(s.equalsIgnoreCase("7") || s.equalsIgnoreCase("8"))
            return s.equalsIgnoreCase("7") ? "8" : "7";

        return s;
    }

    public static String getXOR(String s1, String s2, String s3) {
        int dec1= Integer.valueOf(s1, 16).intValue();
        int dec2= Integer.valueOf(s2, 16).intValue();
        int dec3= Integer.valueOf(s3, 16).intValue();

        int result = dec1 ^ dec2 ^ dec3;
        String xor = Integer.toHexString(result);
        if(xor.length() == 1)
            xor = "0" + xor;
        return xor;
    }

    public static String getXOR(ArrayList<String> list) {

        int result = 0;
        for(String s : list)
        {
            int dec= Integer.valueOf(s, 16).intValue();
            result = result ^ dec;
        }

        String xor = Integer.toHexString(result);
        if(xor.length() == 1)
            xor = "0" + xor;
        return xor;
    }
}
