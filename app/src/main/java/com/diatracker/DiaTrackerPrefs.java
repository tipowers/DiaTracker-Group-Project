package com.diatracker;

import android.content.Context;
import android.content.SharedPreferences;

public class DiaTrackerPrefs {
    private SharedPreferences sharedPreferences;
    private static String prefName = "settings";

    public DiaTrackerPrefs() {
    }

    private static SharedPreferences getPrefs(Context context) {
        return context.getSharedPreferences(prefName, Context.MODE_PRIVATE);
    }

    public static String getName(Context context) {
        return getPrefs(context).getString("name", "");
    }

    public static String getEmail(Context context) {
        return getPrefs(context).getString("email", "");
    }

    public static void setPrefs(Context context, String name, String email) {
        SharedPreferences.Editor editor = getPrefs(context).edit();
        editor.putString("name", name);
        editor.putString("email", email);
        editor.commit();
    }

    public static boolean isEmpty(Context context) {
        SharedPreferences.Editor editor = getPrefs(context).edit();
        if(getPrefs(context).getString("name", "").isEmpty() || getPrefs(context).getString("email", "").isEmpty())
            return true;
        else
            return false;
    }

    public static void setDay(Context context, String day) {
        SharedPreferences.Editor editor = getPrefs(context).edit();
        editor.putString("day", day);
        editor.commit();
    }

    public static String getDay(Context context) {
        return getPrefs(context).getString("day", "");
    }
}
