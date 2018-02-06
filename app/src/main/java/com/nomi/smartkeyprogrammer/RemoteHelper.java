package com.nomi.smartkeyprogrammer;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.nomi.smartkeyprogrammer.model.Remote;
import com.nomi.smartkeyprogrammer.utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Formatter;

/**
 * Created by nomi on 2/5/2018.
 */

public class RemoteHelper {

    private static RemoteHelper INSTANCE = null;

    private static final int R1_ROW30 = 30;
    private static final int R1_ROW30_START = 10;
    private static final int R1_ROW30_END = 12;

    private static final int R1_ROW21 = 21;
    private static final int R1_ROW21_START = 16;
    private static final int R1_ROW21_END = 22;

    private static final int R1_ROW7 = 7;
    private static final int R1_ROW7_START = 16;
    private static final int R1_ROW7_END = 24;


    private static final int R2_ROW30 = 30;
    private static final int R2_ROW30_START = 26;
    private static final int R2_ROW30_END = 28;

    private static final int R2_ROW22 = 22;
    private static final int R2_ROW22_START = 8;
    private static final int R2_ROW22_END = 14;

    private static final int R2_ROW8 = 8;
    private static final int R2_ROW8_START = 8;
    private static final int R2_ROW8_END = 16;

    Context context;

    private RemoteHelper(Context context) {
        this.context = context;
    }

    public static RemoteHelper getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new RemoteHelper(context);
        }
        return INSTANCE;
    }

    public Remote getRemoteOutput(Remote remoteInput) {

        if(remoteInput == null)
            return null;

        String hex = readInputFile();
        if(hex == null)
            return null;

        ArrayList<String> rows = convertHexStringIntoRows(hex);
        if(rows == null || rows.size() != 128) {
            Toast.makeText(context, "Invalid file data", Toast.LENGTH_LONG).show();
            return null;
        }

        Remote remote1= createRemote(rows.get(R1_ROW30).substring(R1_ROW30_START, R1_ROW30_END),
                rows.get(R1_ROW21).substring(R1_ROW21_START, R1_ROW21_END),
                rows.get(R1_ROW7).substring(R1_ROW7_START, R1_ROW7_END));

        Remote remote2= createRemote(rows.get(R2_ROW30).substring(R2_ROW30_START, R2_ROW30_END),
                rows.get(R2_ROW22).substring(R2_ROW22_START, R2_ROW22_END),
                rows.get(R2_ROW8).substring(R2_ROW8_START, R2_ROW8_END));

        Remote remoteOutput = new Remote();

        for(int i = 0; i < 4; i++)
        {
            if(remote1.getPage8().get(i).equalsIgnoreCase(remote2.getPage8().get(i)))
                remoteOutput.getPage8().add(i, remoteInput.getPage8().get(i));
            else
                remoteOutput.getPage8().add(i, getXOR(remote1.getPage8().get(i),
                        remote2.getPage8().get(i), remoteInput.getPage8().get(i)));
        }

        for(int i = 0; i < 3; i++)
        {
            if(remote1.getPage3().get(i).equalsIgnoreCase(remote2.getPage3().get(i)))
                remoteOutput.getPage3().add(i, remoteInput.getPage3().get(i));
            else
                remoteOutput.getPage3().add(i, getXOR(remote1.getPage3().get(i),
                        remote2.getPage3().get(i), remoteInput.getPage3().get(i)));
        }

        remoteOutput.getPage2().add(remoteInput.getPage2().get(0));

        // Create a new File named Output
        createOutputFile(rows, remoteInput, remoteOutput);
        return remoteOutput;
    }

    public void createOutputFile(ArrayList<String> inputRows, Remote remoteInput, Remote remoteOutput) {

        ArrayList<String> outputRows = (ArrayList<String>)inputRows.clone();
        outputRows.set(R1_ROW30, replace(outputRows.get(R1_ROW30), R1_ROW30_START, R1_ROW30_END, remoteInput.getPage2InString()));
        outputRows.set(R1_ROW21, replace(outputRows.get(R1_ROW21), R1_ROW21_START, R1_ROW21_END, remoteInput.getPage3InString()));
        outputRows.set(R1_ROW7, replace(outputRows.get(R1_ROW7), R1_ROW7_START, R1_ROW7_END, remoteInput.getPage8InString()));
        outputRows.set(R2_ROW30, replace(outputRows.get(R2_ROW30), R2_ROW30_START, R2_ROW30_END, remoteOutput.getPage2InString()));
        outputRows.set(R2_ROW22, replace(outputRows.get(R2_ROW22), R2_ROW22_START, R2_ROW22_END, remoteOutput.getPage3InString()));
        outputRows.set(R2_ROW8, replace(outputRows.get(R2_ROW8), R2_ROW8_START, R2_ROW8_END, remoteOutput.getPage8InString()));

        StringBuilder updatedHex = new StringBuilder();
        for(String s : outputRows) {
            updatedHex.append(s);
        }

        writeOutputFile(updatedHex.toString());

    }

    private String replace(String originalPage, int start, int end, String newPage) {
        StringBuilder stringBuilder = new StringBuilder(originalPage);
        stringBuilder.replace(start ,end, newPage);
        return stringBuilder.toString();
    }

    private String getXOR(String s1, String s2, String s3) {
        int dec1= Integer.valueOf(s1, 16).intValue();
        int dec2= Integer.valueOf(s2, 16).intValue();
        int dec3= Integer.valueOf(s3, 16).intValue();

        int result = dec1 ^ dec2 ^ dec3;
        String xor = Integer.toHexString(result);
        if(xor.length() == 1)
            xor = "0" + xor;
        return xor;
    }

    public Remote createRemote(String page2, String page3, String page8) {
        ArrayList<String> page2List = new ArrayList<>();
        page2List.add(page2.substring(0, 2));
        ArrayList<String> page3List = new ArrayList<>();
        page3List.add(page3.substring(0, 2));
        page3List.add(page3.substring(2, 4));
        page3List.add(page3.substring(4, 6));
        ArrayList<String> page8List = new ArrayList<>();
        page8List.add(page8.substring(0, 2));
        page8List.add(page8.substring(2, 4));
        page8List.add(page8.substring(4, 6));
        page8List.add(page8.substring(6, 8));
        Remote remote = new Remote(page2List, page3List, page8List);
        return remote;
    }

    private ArrayList<String> convertHexStringIntoRows(String hex) {
        ArrayList<String> rows = new ArrayList<>();
        int divisor = 32;
        if(hex.length() % divisor == 0) {
            while(hex.length() > 0) {
                String row = hex.substring(0,divisor);
                // store the chunk.
                rows.add(row);
                hex = hex.substring(divisor,hex.length());
            }
        }
        return rows;
    }

    private String readInputFile() {
        String hex = null;
        try {
            File inputFile = FileUtils.getInputFile(context);
            if(inputFile == null)
                Toast.makeText(context, "Input file not found", Toast.LENGTH_LONG).show();
            else {
                byte[] bytes = FileUtils.getBytesFromFile(inputFile);
                hex = convertByteArrayToHex(bytes);
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, "Error reading file", Toast.LENGTH_LONG).show();
        }
        return hex;
    }

    private void writeOutputFile(String updatedHex) {

        byte[] bytes = convertHexToByteArray(updatedHex.toString());
        File outputFile = FileUtils.getOutputFile(context);
        try {
            FileUtils.writeBytesToFile(outputFile, bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String convertByteArrayToHex(byte[] bytes) {
        Formatter formatter = new Formatter();
        for (byte b : bytes) {
            formatter.format("%02x", b);
        }
        String hex = formatter.toString();
        Log.i("TAG", "hex = " + hex);
        return hex;
    }

    private byte[] convertHexToByteArray(String hex) {
        int len = hex.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                    + Character.digit(hex.charAt(i+1), 16));
        }
        return data;
    }
}
