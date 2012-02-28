package de.electricdynamite.pasty;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class PastySharedPrefs {
    public final static String PREFS_NAME = "pasty_prefs";

    public static boolean getUsername(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        return prefs.getBoolean(
                context.getString(R.string.about),
                false);
    }

    public static void setUsername(Context context, boolean newValue) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        Editor prefsEditor = prefs.edit();
        prefsEditor.putBoolean(
                context.getString(R.string.about),
                newValue);
        prefsEditor.commit();
    }
}