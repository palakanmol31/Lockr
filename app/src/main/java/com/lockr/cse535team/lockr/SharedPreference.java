package com.lockr.cse535team.lockr;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by rkotwal2 on 11/13/2016.
 */

public class SharedPreference {
    public static final String LOCKED_APP = "locked_app";
    public static final int CONNECTION_TIMEOUT = 10000;
    public static final int READ_TIMEOUT = 15000;
    static String email_address = "";
    static String patternset="setdefault";
    static SharedPreferences sharedPreferences;


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

    public String getPassword(Context context) throws ExecutionException, InterruptedException {
        SharedPreferences passwordPref;
        passwordPref = context.getSharedPreferences(AppLockConstants.MyPREFERENCES, Context.MODE_PRIVATE);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        ArrayList<String> user;
        SessionClass sessionClass = new SessionClass(context);
        user=sessionClass.getUserDetails();
        email_address = user.get(0);
        String str= new SharedPreference.AsyncLogin().execute(email_address).get();
        patternset = str;

        Log.d("hello", "getPassword: "+patternset);
        if (str.equals("0")) {
            Intent i = new Intent(context, PasswordSetActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);

        }
        else{

            Intent i = new Intent(context, ScreenLock.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }
        return null;
    }

    private class AsyncLogin extends AsyncTask<String, String, String> {
        //ProgressDialog pdLoading = new ProgressDialog(PasswordSetActivity.this);
        HttpURLConnection conn;
        URL url = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //this method will be running on UI thread
            // pdLoading.setMessage("\tLoading...");
            // pdLoading.setCancelable(false);
            // pdLoading.show();

        }

        @Override
        protected String doInBackground(String... params) {

            try {
                // Enter URL address where your php file resides
                String locktype = sharedPreferences.getString("LockingType:", "pattern");
                if(locktype.equals("pattern")) {
                    url = new URL("https://lockrapp.000webhostapp.com/patternretrieve.inc.php");
                }
                else{
                    url = new URL("https://lockrapp.000webhostapp.com/pinretrieve.inc.php");
                }

            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return "exception";
            }
            try {
                // Setup HttpURLConnection class to send and receive data from php and mysql
                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(READ_TIMEOUT);
                conn.setConnectTimeout(CONNECTION_TIMEOUT);
                conn.setRequestMethod("POST");

                // setDoInput and setDoOutput method depict handling of both send and receive
                conn.setDoInput(true);
                conn.setDoOutput(true);

                // Append parameters to URL
                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("username", params[0]);
                String query = builder.build().getEncodedQuery();
                // Open connection for sending data
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(query);
                writer.flush();
                writer.close();
                os.close();
                conn.connect();

            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
                return "exception";
            }

            try {

                int response_code = conn.getResponseCode();

                // Check if successful connection made
                if (response_code == HttpURLConnection.HTTP_OK) {

                    // Read data sent from server
                    InputStream input = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    StringBuilder result = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }

                    // Pass data to onPostExecute method
                    patternset=result.toString();
                    //Log.d("hello", "getPassword: "+patternset);
                    return (result.toString());

                } else {

                    return ("unsuccessful");
                }

            } catch (IOException e) {
                e.printStackTrace();
                return "exception";
            } finally {
                conn.disconnect();
            }


        }

        @Override
        protected void onPostExecute(String result) {

            //this method will be running on UI thread

            // pdLoading.dismiss();
            patternset=result.toString();
            Log.d("hello", "getPasswordexecutre: "+patternset);
            if (result.equalsIgnoreCase("true")) {
                /*Here launching another activity when login successful. If you persist login state
                use sharedPreferences of Android. and logout button to clear sharedPreferences.*/

                // finish();

            } else if (result.equalsIgnoreCase("false")) {

                // If username and password does not match display a error message
                //Toast.makeText(PasswordSetActivity.this, "Invalid email or password", Toast.LENGTH_LONG).show();

            } else if (result.equalsIgnoreCase("exception") || result.equalsIgnoreCase("unsuccessful")) {

                //Toast.makeText(PasswordSetActivity.this, "OOPs! Something went wrong. Connection Problem.", Toast.LENGTH_LONG).show();

            }
        }

    }
}

