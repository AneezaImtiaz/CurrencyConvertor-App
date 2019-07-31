package com.example.currencyconvertor.Components;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Constant {

    public static final String BASE_URL = "https://revolut.duckdns.org/";

    /********************************
     * SharedPreferences
     ********************************************/

    public static void putSharedPreferencesData(String tag, String value, Context ctx) {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(tag, value);

        editor.commit();
    }

    public static String getSharedPreferencesData(String tag, Context ctx) {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(ctx);
        return prefs.getString(tag, "");
    }


}
