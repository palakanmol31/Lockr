package com.lockr.cse535team.lockr.Singleton;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lockr.cse535team.lockr.SharedPreference;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * Created by sdshah10 on 11/20/2016.
 */

public class MyApplication extends Application{

    private static MyApplication sInstance;
    private Context context;
    private static final String TAG = MyApplication.class.getSimpleName();

    public static MyApplication getInstance() {
        return sInstance;
    }

    public static Context getAppContext() {
        return sInstance.getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        Log.d("TAG", "onCreate: ");
    }


    public static void saveToPreferences(Context context, String preferenceName, ArrayList<String> preferenceValue) {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Set<String> set = new HashSet<>();
        if (preferenceValue == null){
            Log.d(TAG, "saveToPreferences: NULL");
        }
        set.addAll(preferenceValue);
        editor.putStringSet(preferenceName,set);
        editor.apply();
    }

    public static ArrayList<String> readFromPreferences(Context context, String preferenceName, String defaultValue) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        Set<String> set = sharedPreferences.getStringSet(preferenceName , null);
        ArrayList<String> data = new ArrayList<>(set);
        Log.d("TAG", "readFromPreferences: " + data);
        return data;
    }
}
