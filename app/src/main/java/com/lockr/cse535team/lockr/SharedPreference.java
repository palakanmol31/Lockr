package com.lockr.cse535team.lockr;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by rkotwal2 on 11/13/2016.
 */

public class SharedPreference {
    public static final String LOCKED_APP = "locked_app";


    public SharedPreference() {
        super();
    }

    // This four methods are used for maintaining favorites.
    public void saveLocked(Context context, List<String> lockedApp) {
        SharedPreferences settings;
        SharedPreferences.Editor editor;

        settings = context.getSharedPreferences(AppLockConstants.MyPREFERENCES,
                Context.MODE_PRIVATE);
        editor = settings.edit();
        Gson gson = new Gson();
        String jsonLockedApp = gson.toJson(lockedApp);
        editor.putString(LOCKED_APP, jsonLockedApp);
        editor.commit();
    }

    public void addLocked(Context context, String app) {
        List<String> lockedApp = getLocked(context);
        if (lockedApp == null)
            lockedApp = new ArrayList<String>();
        lockedApp.add(app);
        saveLocked(context, lockedApp);
    }

    public void removeLocked(Context context, String app) {
        ArrayList<String> locked = getLocked(context);
        if (locked != null) {
            locked.remove(app);
            saveLocked(context, locked);
        }
    }

    public ArrayList<String> getLocked(Context context) {
        SharedPreferences settings;
        List<String> locked;

        settings = context.getSharedPreferences(AppLockConstants.MyPREFERENCES,
                Context.MODE_PRIVATE);

        if (settings.contains(LOCKED_APP)) {
            String jsonLocked = settings.getString(LOCKED_APP, null);
            Gson gson = new Gson();
            String[] lockedItems = gson.fromJson(jsonLocked,
                    String[].class);

            locked = Arrays.asList(lockedItems);
            locked = new ArrayList<String>(locked);
        } else
            return null;
        return (ArrayList<String>) locked;
    }

    public Boolean getPassword(Context context) {
        SharedPreferences passwordPref;
        passwordPref = context.getSharedPreferences(AppLockConstants.MyPREFERENCES, Context.MODE_PRIVATE);
        if (passwordPref.contains(AppLockConstants.PASSWORD)) {
            Intent i = new Intent(context, ScreenLock.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
            return true;
        }
        else{
            Intent i = new Intent(context, PasswordSetActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
            return false;
        }

    }
}

