package com.nomi.smartkeyprogrammer;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.nomi.smartkeyprogrammer.model.Remote;
import com.nomi.smartkeyprogrammer.utils.FileUtils;
import com.nomi.smartkeyprogrammer.utils.MathUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Formatter;

import static com.nomi.smartkeyprogrammer.utils.Constants.R1_ROW21;
import static com.nomi.smartkeyprogrammer.utils.Constants.R1_ROW21_END;
import static com.nomi.smartkeyprogrammer.utils.Constants.R1_ROW21_START;
import static com.nomi.smartkeyprogrammer.utils.Constants.R1_ROW30;
import static com.nomi.smartkeyprogrammer.utils.Constants.R1_ROW30_END;
import static com.nomi.smartkeyprogrammer.utils.Constants.R1_ROW30_START;
import static com.nomi.smartkeyprogrammer.utils.Constants.R1_ROW7;
import static com.nomi.smartkeyprogrammer.utils.Constants.R1_ROW7_END;
import static com.nomi.smartkeyprogrammer.utils.Constants.R1_ROW7_START;
import static com.nomi.smartkeyprogrammer.utils.Constants.R2_ROW22;
import static com.nomi.smartkeyprogrammer.utils.Constants.R2_ROW22_END;
import static com.nomi.smartkeyprogrammer.utils.Constants.R2_ROW22_START;
import static com.nomi.smartkeyprogrammer.utils.Constants.R2_ROW30;
import static com.nomi.smartkeyprogrammer.utils.Constants.R2_ROW30_END;
import static com.nomi.smartkeyprogrammer.utils.Constants.R2_ROW30_START;
import static com.nomi.smartkeyprogrammer.utils.Constants.R2_ROW8;
import static com.nomi.smartkeyprogrammer.utils.Constants.R2_ROW8_END;
import static com.nomi.smartkeyprogrammer.utils.Constants.R2_ROW8_START;
import static com.nomi.smartkeyprogrammer.utils.HexUtils.convertByteArrayToHex;
import static com.nomi.smartkeyprogrammer.utils.HexUtils.convertHexToByteArray;

/**
 * Created by nomi on 2/5/2018.
 */

public class RemoteHelper {

    private static RemoteHelper INSTANCE = null;

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

    File inputFile;
    public Remote getRemoteOutput(Remote remoteInput, File inputFile) {

        if(remoteInput == null || inputFile == null)
            return null;

        this.inputFile = inputFile;
        String hex = readInputFile(inputFile);
        if(hex == null)
            return null;

        ArrayList<ArrayList<String>> rows = convertHexStringIntoRows(hex);
        if(rows == null || rows.size() != 128) {
            Toast.makeText(context, "Invalid file data", Toast.LENGTH_LONG).show();
            return null;
        }

        ArrayList<String> r1pg1 = new ArrayList<>(rows.get(R1_ROW30).subList(R1_ROW30_START, R1_ROW30_END));
        ArrayList<String> r1pg3 = new ArrayList<>(rows.get(R1_ROW21).subList(R1_ROW21_START, R1_ROW21_END));
        ArrayList<String> r1pg8 = new ArrayList<>(rows.get(R1_ROW7).subList(R1_ROW7_START, R1_ROW7_END));
        ArrayList<String> r2pg1 = new ArrayList<>(rows.get(R2_ROW30).subList(R2_ROW30_START, R2_ROW30_END));
        ArrayList<String> r2pg3 = new ArrayList<>(rows.get(R2_ROW22).subList(R2_ROW22_START, R2_ROW22_END));
        ArrayList<String> r2pg8 = new ArrayList<>(rows.get(R2_ROW8).subList(R2_ROW8_START, R2_ROW8_END));

        Remote remote1 = new Remote(r1pg1, r1pg3, r1pg8);
        Remote remote2 = new Remote(r2pg1, r2pg3, r2pg8);
        Remote remoteOutput = new Remote();

        for(int i = 0; i < 4; i++)
        {
            if(remote1.getPage8().get(i).equalsIgnoreCase(remote2.getPage8().get(i)))
                remoteOutput.getPage8().add(i, remoteInput.getPage8().get(i));
            else
                remoteOutput.getPage8().add(i, MathUtils.getXOR(remote1.getPage8().get(i),
                        remote2.getPage8().get(i), remoteInput.getPage8().get(i)));
        }

        for(int i = 0; i < 3; i++)
        {
            if(remote1.getPage3().get(i).equalsIgnoreCase(remote2.getPage3().get(i)))
                remoteOutput.getPage3().add(i, remoteInput.getPage3().get(i));
            else
                remoteOutput.getPage3().add(i, MathUtils.getXOR(remote1.getPage3().get(i),
                        remote2.getPage3().get(i), remoteInput.getPage3().get(i)));
        }

        remoteOutput.getPage2().add(remoteInput.getPage2().get(0));

        // Create a new File named Output
        createOutputFile(rows, remoteInput, remoteOutput);
        return remoteOutput;
    }

    public void createOutputFile(ArrayList<ArrayList<String>> inputRows, Remote remoteInput, Remote remoteOutput) {

        ArrayList<ArrayList<String>> outputRows = (ArrayList<ArrayList<String>>)inputRows.clone();
        replace(outputRows.get(R1_ROW30), R1_ROW30_START, R1_ROW30_END, remoteInput.getPage2());
        replace(outputRows.get(R1_ROW21), R1_ROW21_START, R1_ROW21_END, remoteInput.getPage3());
        replace(outputRows.get(R1_ROW7), R1_ROW7_START, R1_ROW7_END, remoteInput.getPage8());
        replace(outputRows.get(R2_ROW30), R2_ROW30_START, R2_ROW30_END, remoteOutput.getPage2());
        replace(outputRows.get(R2_ROW22), R2_ROW22_START, R2_ROW22_END, remoteOutput.getPage3());
        replace(outputRows.get(R2_ROW8), R2_ROW8_START, R2_ROW8_END, remoteOutput.getPage8());

        // checksum calculations
        // r1 page 8
        ArrayList<String> r1pg8CheckSum5 = new ArrayList<>(outputRows.get(R1_ROW7).subList(R1_ROW7_START, R1_ROW7_END + 1));
        outputRows.get(R1_ROW7).set(R1_ROW7_END + 1, MathUtils.getXOR(r1pg8CheckSum5));

        String r1pg8Inv1 = MathUtils.getFullInverse(r1pg8CheckSum5.get(0));
        String r1pg8Inv2 = MathUtils.getFullInverse(r1pg8CheckSum5.get(1));
        String r1pg8Inv3 = MathUtils.getFullInverse(r1pg8CheckSum5.get(2));
        String r1pg8Inv4 = MathUtils.getFullInverse(r1pg8CheckSum5.get(3));
        String r1pg8Inv5 = MathUtils.getFullInverse(r1pg8CheckSum5.get(4));

        outputRows.get(R1_ROW7).set(R1_ROW7_END + 2, r1pg8Inv1);
        outputRows.get(R1_ROW7).set(R1_ROW7_END + 3, r1pg8Inv2);
        outputRows.get(R2_ROW8).set(0, r1pg8Inv3);
        outputRows.get(R2_ROW8).set(1, r1pg8Inv4);
        outputRows.get(R2_ROW8).set(2, r1pg8Inv5);

        ArrayList<String> r1pg8InvList = new ArrayList<>();
        r1pg8InvList.add(r1pg8Inv1);
        r1pg8InvList.add(r1pg8Inv2);
        r1pg8InvList.add(r1pg8Inv3);
        r1pg8InvList.add(r1pg8Inv4);
        r1pg8InvList.add(r1pg8Inv5);
        outputRows.get(R2_ROW8).set(R2_ROW8_START - 1, MathUtils.getXOR(r1pg8InvList));

        // r2 page 8
        ArrayList<String> r2pg8CheckSum5 = new ArrayList<>(outputRows.get(R2_ROW8).subList(R2_ROW8_START, R2_ROW8_END + 1));
        outputRows.get(R2_ROW8).set(R2_ROW8_END + 1, MathUtils.getXOR(r2pg8CheckSum5));

        String r2pg8Inv1 = MathUtils.getFullInverse(r2pg8CheckSum5.get(0));
        String r2pg8Inv2 = MathUtils.getFullInverse(r2pg8CheckSum5.get(1));
        String r2pg8Inv3 = MathUtils.getFullInverse(r2pg8CheckSum5.get(2));
        String r2pg8Inv4 = MathUtils.getFullInverse(r2pg8CheckSum5.get(3));
        String r2pg8Inv5 = MathUtils.getFullInverse(r2pg8CheckSum5.get(4));

        outputRows.get(R2_ROW8).set(R2_ROW8_END + 2, r2pg8Inv1);
        outputRows.get(R2_ROW8).set(R2_ROW8_END + 3, r2pg8Inv2);
        outputRows.get(R2_ROW8).set(R2_ROW8_END + 4, r2pg8Inv3);
        outputRows.get(R2_ROW8).set(R2_ROW8_END + 5, r2pg8Inv4);
        outputRows.get(R2_ROW8).set(R2_ROW8_END + 6, r2pg8Inv5);

        ArrayList<String> r2pg8InvList = new ArrayList<>();
        r2pg8InvList.add(r2pg8Inv1);
        r2pg8InvList.add(r2pg8Inv2);
        r2pg8InvList.add(r2pg8Inv3);
        r2pg8InvList.add(r2pg8Inv4);
        r2pg8InvList.add(r2pg8Inv5);
        outputRows.get(R2_ROW8).set(R2_ROW8_END + 7, MathUtils.getXOR(r2pg8InvList));

        // checksum calculations
        // r1 page 3
        ArrayList<String> r1pg3CheckSum5 = new ArrayList<>(outputRows.get(R1_ROW21).subList(R1_ROW21_START, R1_ROW21_END + 2));
        outputRows.get(R1_ROW21).set(R1_ROW21_END + 2, MathUtils.getXOR(r1pg3CheckSum5));

        String r1pg3Inv1 = MathUtils.getFullInverse(r1pg3CheckSum5.get(0));
        String r1pg3Inv2 = MathUtils.getFullInverse(r1pg3CheckSum5.get(1));
        String r1pg3Inv3 = MathUtils.getFullInverse(r1pg3CheckSum5.get(2));
        String r1pg3Inv4 = MathUtils.getFullInverse(r1pg3CheckSum5.get(3));
        String r1pg3Inv5 = MathUtils.getFullInverse(r1pg3CheckSum5.get(4));

        outputRows.get(R1_ROW21).set(R1_ROW21_END + 3, r1pg3Inv1);
        outputRows.get(R1_ROW21).set(R1_ROW21_END + 4, r1pg3Inv2);
        outputRows.get(R2_ROW22).set(0, r1pg3Inv3);
        outputRows.get(R2_ROW22).set(1, r1pg3Inv4);
        outputRows.get(R2_ROW22).set(2, r1pg3Inv5);

        ArrayList<String> r1pg3InvList = new ArrayList<>();
        r1pg3InvList.add(r1pg3Inv1);
        r1pg3InvList.add(r1pg3Inv2);
        r1pg3InvList.add(r1pg3Inv3);
        r1pg3InvList.add(r1pg3Inv4);
        r1pg3InvList.add(r1pg3Inv5);
        outputRows.get(R2_ROW22).set(3, MathUtils.getXOR(r1pg3InvList));

        // r2 page 3
        ArrayList<String> r2pg3CheckSum5 = new ArrayList<>(outputRows.get(R2_ROW22).subList(R2_ROW22_START, R2_ROW22_END + 2));
        outputRows.get(R2_ROW22).set(R2_ROW22_END + 2, MathUtils.getXOR(r2pg3CheckSum5));

        String r2pg3Inv1 = MathUtils.getFullInverse(r2pg3CheckSum5.get(0));
        String r2pg3Inv2 = MathUtils.getFullInverse(r2pg3CheckSum5.get(1));
        String r2pg3Inv3 = MathUtils.getFullInverse(r2pg3CheckSum5.get(2));
        String r2pg3Inv4 = MathUtils.getFullInverse(r2pg3CheckSum5.get(3));
        String r2pg3Inv5 = MathUtils.getFullInverse(r2pg3CheckSum5.get(4));

        outputRows.get(R2_ROW22).set(R2_ROW22_END + 3, r2pg3Inv1);
        outputRows.get(R2_ROW22).set(R2_ROW22_END + 4, r2pg3Inv2);
        outputRows.get(R2_ROW22).set(R2_ROW22_END + 5, r2pg3Inv3);
        outputRows.get(R2_ROW22).set(R2_ROW22_END + 6, r2pg3Inv4);
        outputRows.get(R2_ROW22).set(R2_ROW22_END + 7, r2pg3Inv5);

        ArrayList<String> r2pg3InvList = new ArrayList<>();
        r2pg3InvList.add(r2pg3Inv1);
        r2pg3InvList.add(r2pg3Inv2);
        r2pg3InvList.add(r2pg3Inv3);
        r2pg3InvList.add(r2pg3Inv4);
        r2pg3InvList.add(r2pg3Inv5);
        outputRows.get(R2_ROW22).set(R2_ROW22_END + 8, MathUtils.getXOR(r2pg3InvList));

        // checksum calculations
        // page 2
        ArrayList<String> pg2CheckSum5 = new ArrayList<>(outputRows.get(R1_ROW30).subList(R1_ROW30_START -1, R1_ROW30_END + 1));
        String xorTemp = MathUtils.getXOR(pg2CheckSum5);
        outputRows.get(R1_ROW30).set(R1_ROW30_END + 2, xorTemp);

        String pg3Inv1 = MathUtils.getFullInverse(pg2CheckSum5.get(0));
        String pg3Inv2 = MathUtils.getFullInverse(pg2CheckSum5.get(1));
        String pg3Inv3 = MathUtils.getFullInverse(pg2CheckSum5.get(2));
        String pg3Inv4 = MathUtils.getFullInverse(xorTemp);

        outputRows.get(R1_ROW30).set(8, pg3Inv1);
        outputRows.get(R1_ROW30).set(9, pg3Inv2);
        outputRows.get(R1_ROW30).set(10, pg3Inv3);
        outputRows.get(R1_ROW30).set(11, pg3Inv4);

        outputRows.get(R1_ROW30).set(12, pg2CheckSum5.get(0));
        outputRows.get(R1_ROW30).set(13, pg2CheckSum5.get(1));
        outputRows.get(R1_ROW30).set(14, pg2CheckSum5.get(2));
        outputRows.get(R1_ROW30).set(15, xorTemp);

        outputRows.get(R1_ROW30 + 1).set(0, pg3Inv1);
        outputRows.get(R1_ROW30 + 1).set(1, pg3Inv2);
        outputRows.get(R1_ROW30 + 1).set(2, pg3Inv3);
        outputRows.get(R1_ROW30 + 1).set(3, pg3Inv4);

        StringBuilder updatedHex = new StringBuilder();
        for(ArrayList<String> row : outputRows) {
            for(String s : row) {
                updatedHex.append(s);
            }
        }
        writeOutputFile(updatedHex.toString());
    }

    private void replace(ArrayList<String> originalPage, int start, int end, ArrayList<String> newPage) {
        originalPage.subList(start, end).clear();
        originalPage.addAll(start, newPage);
    }

    private ArrayList<ArrayList<String>> convertHexStringIntoRows(String hex) {
        ArrayList<ArrayList<String>> rows = new ArrayList<>();
        ArrayList<String> dividedRows = divide(hex, 32);
        for(String row : dividedRows)
        {
            rows.add(divide(row, 2));
        }
        return rows;
    }

    public ArrayList<String> divide(String str, int divisor) {
        ArrayList<String> list = new ArrayList<>();
        if(str.length() % divisor == 0) {
            while(str.length() > 0) {
                String row = str.substring(0, divisor);
                // store the chunk.
                list.add(row);
                str = str.substring(divisor, str.length());
            }
        }
        return list;
    }

    private String readInputFile(File inputFile) {
        String hex = null;
        try {
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
        File outputFile = FileUtils.getOutputFile(inputFile);
        try {
            FileUtils.writeBytesToFile(outputFile, bytes);
            if(iOutputFileCreated != null)
                iOutputFileCreated.onOutputFileCreated(outputFile.getPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    IOutputFileCreated iOutputFileCreated;
    public void setOnOutputFileCreated(IOutputFileCreated iOutputFileCreated) {
        this.iOutputFileCreated = iOutputFileCreated;
    }

    public interface IOutputFileCreated {
        void onOutputFileCreated(String path);
    }
}
