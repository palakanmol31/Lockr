package com.lockr.cse535team.lockr;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.andrognito.pinlockview.IndicatorDots;
import com.andrognito.pinlockview.PinLockListener;
import com.andrognito.pinlockview.PinLockView;
import com.bcgdv.asia.lib.connectpattern.ConnectPatternView;

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

/**
 * Created by rkotwal2 on 11/13/2016.
 */

public class PinSet extends AppCompatActivity {
    private static final String TAG = PinSet.class.getSimpleName();
    ConnectPatternView view;
    TextView textView;
    boolean isEnteringFirstTime = true;
    boolean isEnteringSecondTime = false;
    static String enteredPassword, secondPassword;
    public static final int CONNECTION_TIMEOUT = 10000;
    public static final int READ_TIMEOUT = 15000;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    Context context;
    boolean password_set = false;
    PinLockView mPinLockView;
    SessionClass sessionClass;
    IndicatorDots mIndicatorDots;
    static String email_address = "";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        ArrayList<String> user;
        sessionClass = new SessionClass(getApplicationContext());
        user = sessionClass.getUserDetails();
        email_address = user.get(0);
        context = getApplicationContext();
        setContentView(R.layout.activity_pin_set);
        textView = (TextView) findViewById(R.id.SetPinTV);
        mPinLockView = (PinLockView) findViewById(R.id.pin_lock_view);
        mIndicatorDots = (IndicatorDots) findViewById(R.id.indicator_dots);

        mPinLockView.attachIndicatorDots(mIndicatorDots);
        mPinLockView.setPinLockListener(mPinLockListener);


        mPinLockView.setPinLength(6);


        sharedPreferences = getSharedPreferences(AppLockConstants.MyPREFERENCES_pin, MODE_PRIVATE);
        editor = sharedPreferences.edit();

        if(password_set) {
            Intent intent = new Intent(this, ScreenLock.class);
            intent.putExtra("LockType", "Pin");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }

    }

    private PinLockListener mPinLockListener = new PinLockListener() {
        @Override
        public void onComplete(String pin) {
            Log.d(TAG, "Pin complete: " + pin);
            if (isEnteringFirstTime) {
                Log.d("isEnteringFirstTime", "From true value");
                enteredPassword = String.valueOf(pin);
                textView.setText("Reenter Pin");
                isEnteringFirstTime = false;

            } else {
                Log.d("isEnteringFirstTime", "From false value");
                secondPassword = String.valueOf(pin);
                editor.putString(AppLockConstants.PIN, secondPassword);
                Log.d(enteredPassword, secondPassword);
                if (secondPassword.equals(enteredPassword)) {
                    password_set = true;
                    editor.putString("LockingType:","Pin");
                    new PinSet.AsyncLogin().execute(enteredPassword, email_address);
                    //finish();
                } else {
                    editor.clear();
                    textView.setText("Enter Pin");
                    isEnteringFirstTime = true;
                }
                editor.apply();
            }

        }

        @Override
        public void onEmpty() {

        }

        @Override
        public void onPinChange(int pinLength, String intermediatePin) {

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
                url = new URL("https://lockrapp.000webhostapp.com/pinset.inc.php");

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
                        .appendQueryParameter("pattern", params[0])
                        .appendQueryParameter("username", params[1]);
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
                Log.d(TAG, "onPostExecute: pre connection");
                // Check if successful connection made
                if (response_code == HttpURLConnection.HTTP_OK) {
                    Log.d(TAG, "onPostExecute: connection made");
                    // Read data sent from server
                    InputStream input = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    StringBuilder result = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }

                    // Pass data to onPostExecute method
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
            Log.d(TAG, "onPostExecute: result outside is true");
            // pdLoading.dismiss();
            Log.d(TAG, "onPostExecute: result is true"+result);
            if (result.equalsIgnoreCase("true")) {
                /*Here launching another activity when login successful. If you persist login state
                use sharedPreferences of Android. and logout button to clear sharedPreferences.*/
                Log.d(TAG, "onPostExecute: result is true");
                finish();

            } else if (result.equalsIgnoreCase("false")) {

                // If username and password does not match display a error message
                Toast.makeText(PinSet.this, "Invalid email or password", Toast.LENGTH_LONG).show();

            } else if (result.equalsIgnoreCase("exception") || result.equalsIgnoreCase("unsuccessful")) {

                Toast.makeText(PinSet.this, "OOPs! Something went wrong. Connection Problem.", Toast.LENGTH_LONG).show();

            }
        }

    }
}


