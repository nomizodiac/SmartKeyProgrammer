package com.nomi.smartkeyprogrammer.utils;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;

public class PermissionUtils {

    public static final int REQUEST_CODE_STORAGE_PERMISSION = 11;
    public static final String PERMISSION_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE;

    public static void requestStoragePermission(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (isStoragePermissionGranted(activity)) {
                return;
            }

            // Fire off an async request to actually get the permission
            // This will show the standard permission request dialog UI
            activity.requestPermissions(new String[]{PERMISSION_STORAGE},
                    REQUEST_CODE_STORAGE_PERMISSION);
        }
    }

    public static boolean isStoragePermissionGranted(Activity activity) {
        boolean flag = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            flag = activity.checkSelfPermission(PERMISSION_STORAGE) == PackageManager.PERMISSION_GRANTED;
        }
        return flag;
    }
}
