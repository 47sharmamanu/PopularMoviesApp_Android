package com.ink.popularmoviesapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.StringRes;

/**
 * Created & Developed by Manu Sharma on 10/27/2016.
 */

public class Utility {

    public static final String LOG_TAG = Utility.class.getSimpleName();

    public static final String LIFE_CYCLE_TAG = "Lifecycle method";

    public static String getPreferedFilterForMovie(Context context){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString(getString(context,R.string.pref_units_key), "");
    }

    /**
     * Return a localized string from the application's package's
     * default string table.
     *
     * @param context Context for which the resource string has to be fetched
     * @param resId Resource id for the string
     */
    public static final String getString(Context context,@StringRes int resId) {
        return context.getResources().getString(resId);
    }
}
