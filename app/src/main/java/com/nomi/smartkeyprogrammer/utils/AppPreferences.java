package com.nomi.smartkeyprogrammer.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class AppPreferences {

    private static final String APP_PREF = "SMART_KEY_PROG_PREF";
    private static final String IS_APP_ACTIVATED = "is_app_activated";
    private static AppPreferences mInstance;
    private final SharedPreferences mPreference;
    private final SharedPreferences.Editor mEditor;

    private AppPreferences(Context context) {
        mPreference = context.getSharedPreferences(APP_PREF, 0);;
        mEditor = mPreference.edit();
        mEditor.apply();
    }

    public static AppPreferences getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new AppPreferences(context);
        }
        return mInstance;
    }

    ////////////////////////////////// Location ///////////////////////
    public void activateApp() {
        mEditor.putBoolean(IS_APP_ACTIVATED, true);
        mEditor.commit();
    }

    public boolean isAppActivated() {
        return mPreference.getBoolean(IS_APP_ACTIVATED, false);
    }
}
