package com.nomi.smartkeyprogrammer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.nomi.smartkeyprogrammer.utils.AppPreferences;

public class AppActivationActivity extends AppCompatActivity {

    EditText etActivateApp;
    Button btnActivateApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_activation);
        btnActivateApp = findViewById(R.id.btn_activate_app);
        etActivateApp = findViewById(R.id.et_activate_app);

        if(AppPreferences.getInstance(this).isAppActivated())
            startMainActivity();

        btnActivateApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activateTask();
            }
        });

        /*etActivateApp.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                activateTask();
                return false;
            }
        });*/
    }

    private void activateTask() {
        String password = etActivateApp.getText().toString();
        if(password == null || password.isEmpty() || password.length() != 10 || !password.equals("Pwd.01@efi")) {
            etActivateApp.setText("");
            Toast.makeText(AppActivationActivity.this, "Invalid Password", Toast.LENGTH_SHORT).show();
            return;
        }
        AppPreferences.getInstance(AppActivationActivity.this).activateApp();
        startMainActivity();
    }

    private void startMainActivity() {
        Intent mainActivityIntent = new Intent(AppActivationActivity.this, MainActivity.class);
        startActivity(mainActivityIntent);
        finish();
    }
}
