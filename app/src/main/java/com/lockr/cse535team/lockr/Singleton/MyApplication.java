package com.lockr.cse535team.lockr.Singleton;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.preference.PreferenceManager;

import com.lockr.cse535team.lockr.SharedPreference;

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
    static Set<ApplicationInfo> set;

    private SharedPreference mSharedPreference;

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
        set = new HashSet<>();

    }


    public static void saveToPreferences(Context context, String preferenceName, ArrayList<ApplicationInfo> preferenceValue) {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        set.addAll(preferenceValue);
        editor.putString(preferenceName, String.valueOf(set));
        editor.apply();
    }

    public static ArrayList<ApplicationInfo> readFromPreferences(Context context, String preferenceName, String defaultValue) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        Set<String> set =  sharedPreferences.getStringSet(preferenceName, null);
        List<ApplicationInfo> sample=new ArrayList<ApplicationInfo>(set);
        return sample;
    }
}
