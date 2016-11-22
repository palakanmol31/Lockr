package com.lockr.cse535team.lockr;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.andrognito.pinlockview.IndicatorDots;
import com.andrognito.pinlockview.PinLockListener;
import com.andrognito.pinlockview.PinLockView;
import com.bcgdv.asia.lib.connectpattern.ConnectPatternView;
import com.takwolf.android.lock9.Lock9View;

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
import java.util.concurrent.ExecutionException;

/**
 * Created by rkotwal2 on 11/13/2016.
 */

public class ScreenLock extends Activity {
    private static final String TAG = ScreenLock.class.getSimpleName();
    ConnectPatternView view;
    Context context;
    SharedPreferences passwordPref;
    int no_of_attempts=0;
    static String patternsetted;
    public static final int CONNECTION_TIMEOUT = 10000;
    public static final int READ_TIMEOUT = 15000;
    static String email_address;
    static String patternset="setdefault";
    Boolean isBlu = false;
    LinearLayout screenlockLayout;
    PinLockView mPinLockView;
    IndicatorDots mIndicatorDots;
    SharedPreferences sharedPreferences;
    protected void onCreate(Bundle savedInstanceState) {
        context = getApplicationContext();
        super.onCreate(savedInstanceState);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String locktype = sharedPreferences.getString("LockingType:", "pattern");

        if(locktype.equals("pattern")) {
            setContentView(R.layout.popup_unlock);
            screenlockLayout = (LinearLayout) findViewById(R.id.screenLockLayout);
            view = (ConnectPatternView) findViewById(R.id.connect);
            patternData();
        }
        else if(locktype.equals("Pin")){
            setContentView(R.layout.pin_unlock);
            mPinLockView = (PinLockView) findViewById(R.id.pin_lock_view);
            mIndicatorDots = (IndicatorDots) findViewById(R.id.indicator_dots);

            mPinLockView.attachIndicatorDots(mIndicatorDots);
            mPinLockView.setPinLockListener(mPinLockListener);


            mPinLockView.setPinLength(6);

        }
        isBlu = getIntent().hasExtra("BLU");
    }



    @Override
    public void onBackPressed() {
        Snackbar.make(screenlockLayout,"Enter the password First",Snackbar.LENGTH_LONG).show();
    }

    public void patternData(){

        view.animateIn();
        view.setOnConnectPatternListener(new ConnectPatternView.OnConnectPatternListener() {
            @Override

            public void onPatternEntered(ArrayList<Integer> result) {
                ArrayList<String> user;
                SessionClass sessionClass = new SessionClass(context);
                user=sessionClass.getUserDetails();
                email_address = user.get(0);
                try {
                    patternsetted= new AsyncLogin().execute(email_address).get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                Log.d("hello", "getPassword: "+patternsetted);
                passwordPref = context.getSharedPreferences(AppLockConstants.MyPREFERENCES, Context.MODE_PRIVATE);
//                Log.d("entered value", String.valueOf(result));
//                Log.d("stored value", passwordPref.getString(AppLockConstants.PASSWORD,""));
                if(patternsetted.equals(String.valueOf(result))) {
                    finish();
                }
                else {
                    no_of_attempts++;
                    if(no_of_attempts<5)
                    {Toast.makeText(getApplicationContext(),
                            "Password Mismatch!", Toast.LENGTH_LONG)
                            .show();}
                    else
                    {Toast.makeText(getApplicationContext(),
                            "Limit Exceeded!", Toast.LENGTH_LONG)
                            .show();}
                }


            }

            @Override
            public void onPatternAbandoned() {

            }

            @Override
            public void animateInStart() {

            }

            @Override
            public void animateInEnd() {
            }

            @Override
            public void animateOutStart() {

            }

            @Override
            public void animateOutEnd() {
            }
        });
    }

        private PinLockListener mPinLockListener = new PinLockListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onComplete(String pin) {
                passwordPref = context.getSharedPreferences(AppLockConstants.MyPREFERENCES_pin, Context.MODE_PRIVATE);
                if(passwordPref.getString(AppLockConstants.PIN,"").equals(String.valueOf(pin))) {
                    if(isBlu){
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            finishAffinity();
                        }
                    }else{
                        finish();
                    }
                }
                else {
                    no_of_attempts++;
                    if(no_of_attempts<5)
                    {
                        Toast.makeText(getApplicationContext(),
                                "Password Mismatch!", Toast.LENGTH_LONG)
                                .show();}
                    else
                    {
                        Intent i = new Intent(context, CameraActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(i);
                        Toast.makeText(getApplicationContext(),
                                "Limit Exceeded!", Toast.LENGTH_LONG)
                                .show();}
                }
            }


            @Override
            public void onEmpty() {
                Log.d(TAG, "Pin empty");
            }

            @Override
            public void onPinChange(int pinLength, String intermediatePin) {
                Log.d(TAG, "Pin changed, new length " + pinLength + " with intermediate pin " + intermediatePin);
            }
        };

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
                url = new URL("https://lockrapp.000webhostapp.com/patternretrieve.inc.php");

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




