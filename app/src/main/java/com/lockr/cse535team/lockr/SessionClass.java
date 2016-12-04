package com.lockr.cse535team.lockr;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by sdshah10 on 11/3/2016.
 */

public class SessionClass extends Activity {
    SharedPreferences pref;
    SharedPreferences.Editor editor;

    Context _context;

    int PRIVATE_MODE = 0;
    private static final String PREF_NAME = "Session";

    private static final String IS_LOGIN = "IsLoggedIn";
    private static final String KEY_PATTERN = "pattern";
    public static final String KEY_NAME = "name";
    ArrayList<String> user;
    public static final String KEY_PASSWORD = "password";
    public SessionClass(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
        user = new ArrayList<>();
    }

    public void createLoginSession(String name, String password){
        editor.putBoolean(IS_LOGIN, true);
        editor.putString(KEY_NAME, name);
        editor.putString(KEY_PASSWORD, password);
        editor.commit();
    }

    public boolean checkLogin(){
        if(!this.isLoggedIn()) {
            Intent i = new Intent(_context, LoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            _context.startActivity(i);
            return false;
        }
        return true;
    }

    public ArrayList<String> getUserDetails(){
        user.add(pref.getString(KEY_NAME, null));
        return user;
    }

    public void logoutUser(){
        // Clearing all data from Shared Preferences
        editor.clear();
        editor.commit();

        // After logout redirect user to Loing Activity
        Intent i = new Intent(_context, LoginActivity.class);
        // Closing all the Activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Add new Flag to start new Activity
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Staring Login Activity
        _context.startActivity(i);
    }

    public boolean isLoggedIn(){
        return pref.getBoolean(IS_LOGIN, false);
    }
}
