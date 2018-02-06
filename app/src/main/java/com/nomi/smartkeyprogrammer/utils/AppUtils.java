package com.nomi.smartkeyprogrammer.utils;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;

import com.nomi.smartkeyprogrammer.R;

public class AppUtils {

    public static final int REQUEST_CODE_APP_DETAILS_PERMISSION_SETTING = 6791;

    public static void dialogReasonPermissionToSettings(final Activity activity) {

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(R.string.permission_required);
        builder.setMessage(R.string.permission_message)
                .setPositiveButton(R.string.go_to_settings, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        goToAppDetailsForPermissionSettings(activity);
                    }
                })
                .setNegativeButton(R.string.exit, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        activity.finish();
                    }
                });
        builder.setCancelable(false);
        builder.create().show();
    }

    private static void goToAppDetailsForPermissionSettings(Activity activity) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
        intent.setData(uri);
        activity.startActivityForResult(intent, REQUEST_CODE_APP_DETAILS_PERMISSION_SETTING);
    }

    public static void dialogReasonStoragePermission(final Activity activity) {

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(R.string.permission_required);
        builder.setMessage(R.string.permission_message_non_rationale)
                .setPositiveButton(R.string.go_to_settings, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        PermissionUtils.requestStoragePermission(activity);
                        dialog.dismiss();
                    }
                });
        builder.setCancelable(false);
        builder.create().show();
    }
}
