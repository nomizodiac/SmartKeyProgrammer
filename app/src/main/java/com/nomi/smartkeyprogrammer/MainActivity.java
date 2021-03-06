package com.nomi.smartkeyprogrammer;

import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.chaos.view.PinView;
import com.codekidlabs.storagechooser.StorageChooser;
import com.nomi.smartkeyprogrammer.model.Remote;
import com.nomi.smartkeyprogrammer.utils.AppUtils;
import com.nomi.smartkeyprogrammer.utils.FileUtils;
import com.nomi.smartkeyprogrammer.utils.PermissionUtils;

import java.util.ArrayList;

import static com.nomi.smartkeyprogrammer.utils.PermissionUtils.REQUEST_CODE_STORAGE_PERMISSION;

public class MainActivity extends AppCompatActivity {

    Button btnRun, btnLoadFile;
    PinView pvPage2, pvPage3, pvPage8;
    TextView lbPage2, lbPage3, lbPage8, lbInputFile, lbOutputFile;
    StorageChooser chooser;
    String filePath= "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PermissionUtils.requestStoragePermission(this);

        btnRun = findViewById(R.id.btn_run);
        btnLoadFile = findViewById(R.id.btn_load_file);
        pvPage2 = findViewById(R.id.pin_page2);
        pvPage3 = findViewById(R.id.pin_page3);
        pvPage8 = findViewById(R.id.pin_page8);
        lbPage2 = findViewById(R.id.tv_output_page2);
        lbPage3 = findViewById(R.id.tv_output_page3);
        lbPage8 = findViewById(R.id.tv_output_page8);
        lbInputFile = findViewById(R.id.tv_input_file);
        lbOutputFile = findViewById(R.id.tv_output_file);

        btnRun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lbOutputFile.setText("Output File: no file found");
                generateOutputRemoteFile();
            }
        });

        btnLoadFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Initialize Builder
                chooser = new StorageChooser.Builder()
                        .withActivity(MainActivity.this)
                        .withFragmentManager(getFragmentManager())
                        .withMemoryBar(true)
                        .allowCustomPath(true)
                        .setType(StorageChooser.FILE_PICKER)
                        .build();
                chooser.show();
                chooser.setOnSelectListener(new StorageChooser.OnSelectListener() {
                    @Override
                    public void onSelect(String path) {
                        Log.i("SELECTED_PATH", path);
                        filePath = path;
                        lbInputFile.setText("Input File: " + filePath);
                    }
                });
            }
        });
        String str = Integer.toHexString(1);
        Log.i("TAG", "hexout = " + str);
    }

    private void generateOutputRemoteFile() {

        if(filePath == null || filePath.isEmpty()) {
            Toast.makeText(this, "Input file not found", Toast.LENGTH_LONG).show();
            return;
        }

        RemoteHelper.getInstance(MainActivity.this).setOnOutputFileCreated(new RemoteHelper.IOutputFileCreated() {
            @Override
            public void onOutputFileCreated(String path) {
                lbOutputFile.setText("Output File: " + path);
            }
        });

        Remote remoteOutput = RemoteHelper.getInstance(MainActivity.this).getRemoteOutput(getRemoteInput(),
                FileUtils.getInputFile(filePath));
        if(remoteOutput == null)
            return;

        printPage(remoteOutput.getPage2(), lbPage2);
        printPage(remoteOutput.getPage3(), lbPage3);
        printPage(remoteOutput.getPage8(), lbPage8);


    }

    private Remote getRemoteInput() {

        /*String page2 = "88";
        String page3 = "0b4a4c";
        String page8 = "fdf0c630";*/
        /*String page2 = "66";
        String page3 = "91d71d";
        String page8 = "a9bd8f30";*/
        String page2 = pvPage2.getText().toString();
        String page3 = pvPage3.getText().toString();
        String page8 = pvPage8.getText().toString();

        if(page2.length() != 2 || page3.length() != 6 || page8.length() != 8) {
            Toast.makeText(this, "Invalid input", Toast.LENGTH_LONG).show();
            return null;
        }

        ArrayList<String> page2List = new ArrayList<>();
        page2List.add(page2);
        RemoteHelper.getInstance(MainActivity.this).divide(page8, 2);
        RemoteHelper.getInstance(MainActivity.this).divide(page3, 2);
        return new Remote(page2List, RemoteHelper.getInstance(MainActivity.this).divide(page3, 2),
                RemoteHelper.getInstance(MainActivity.this).divide(page8, 2));
    }

    private void printPage(ArrayList<String> page, TextView lbPage) {
        StringBuilder stringBuilder = new StringBuilder();
        for(int i=0; i<page.size(); i++)
        {
            stringBuilder.append(page.get(i));
        }
        lbPage.setText(stringBuilder.toString());
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_CODE_STORAGE_PERMISSION:
                if (grantResults.length > 0) {
                    if (grantResults[0] == PackageManager.PERMISSION_DENIED) {

                        boolean shouldShowRationale = false;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            shouldShowRationale = shouldShowRequestPermissionRationale(permissions[0]);
                        }
                        if (!shouldShowRationale) {
                            // user denied flagging NEVER ASK AGAIN, you can either enable some fall back,
                            // disable features of your app or open another dialog explaining again the permission and directing to
                            // the app setting
                            AppUtils.dialogReasonPermissionToSettings(MainActivity.this);
                        } else { //if (PermissionUtils.PERMISSION_COARSE_LOCATION.equals(permissions[0])) {
                            // user denied WITHOUT never ask again, this is a good place to explain the user
                            // why you need the permission and ask if he want to accept it (the rationale)
                            AppUtils.dialogReasonStoragePermission(MainActivity.this);
                        }
                    } else {
                        // Do on permission granted work here
                        FileUtils.createSKPDirectory(this);
                    }
                }
                break;
        }
    }

}
