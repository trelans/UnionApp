package com.union.unionapp.controllers;

import android.content.Context;
import android.content.SharedPreferences;

public class LocalDataManager {
    public void setSharedPreference( Context context, String key, String value ) {
        SharedPreferences sharedPref = context.getSharedPreferences( context.getPackageName(), Context.MODE_PRIVATE );
        SharedPreferences.Editor edit = sharedPref.edit();
        edit.putString( key, value );
        edit.apply();
    }

    public String getSharedPreference( Context context, String key, String defaultValue ) {
        return context.getSharedPreferences( context.getPackageName(), Context.MODE_PRIVATE ).getString( key, defaultValue );
    }

    public void clearSharedPreference( Context context ) {
        SharedPreferences sharedPref = context.getSharedPreferences( context.getPackageName(), Context.MODE_PRIVATE );
        SharedPreferences.Editor edit = sharedPref.edit();
        edit.clear();
        edit.apply();
    }

    public void removeSharedPreference( Context context, String key ) {
        SharedPreferences sharedPref = context.getSharedPreferences( context.getPackageName(), Context.MODE_PRIVATE );
        SharedPreferences.Editor edit = sharedPref.edit();
        edit.remove( key );
        edit.apply();
    }
}
