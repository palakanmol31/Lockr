package com.lockr.cse535team.lockr;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.w3c.dom.Text;

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
import java.util.Calendar;
import java.util.concurrent.ExecutionException;

import static com.lockr.cse535team.lockr.LockApp.t1;

public class TimeUnlocking extends AppCompatActivity {
    private static final String TAG = TimeUnlocking.class.getSimpleName();
    SessionClass sessionClass;
    TextView enableText;
    TextView time;
    TimePicker simpleTimePicker;
    TextView time2;
    TimePicker simpleTimePicker2;
    public static final int CONNECTION_TIMEOUT = 10000;
    static String email_address = "";
    public static final int READ_TIMEOUT = 15000;
    CheckBox checkBox;
    static String temptime;
    static String temptime2;
    View.OnClickListener checkBoxListener;
    public static int time_check = 0;
  //  View.OnClickListener addTimeListener ;
    Button addTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_picker);
        ArrayList<String> user;
        sessionClass = new SessionClass(getApplicationContext());
        user = sessionClass.getUserDetails();
        email_address = user.get(0);
        //  initiate the view's
        enableText= (TextView) findViewById(R.id.lockText);
        checkBox = (CheckBox) findViewById(R.id.timeLock);
        addTime = (Button) findViewById(R.id.addTime);
        checkBoxListener =new View.OnClickListener() {

            @Override
            public void onClick(View v) {


                if(checkBox.isChecked()) {
                    time_check = 1;
                  time.setVisibility(View.VISIBLE);
                    time2.setVisibility(View.VISIBLE);
                    simpleTimePicker.setVisibility(View.VISIBLE);
                    simpleTimePicker2.setVisibility(View.VISIBLE);
                    addTime.setVisibility(View.VISIBLE);
                }
                else
                {
                    time_check = 0;
                    time.setVisibility(View.INVISIBLE);
                    time2.setVisibility(View.INVISIBLE);
                    simpleTimePicker.setVisibility(View.INVISIBLE);
                    simpleTimePicker2.setVisibility(View.INVISIBLE);
                    addTime.setVisibility(View.INVISIBLE);
                    String t1="0";
                    String t2="0";
                    new TimeUnlocking.AsyncLogin().execute(t1,t2,email_address);
                    // @Saaketh set start and end time to null
                }


                }

        };
        checkBox.setOnClickListener(checkBoxListener);
      //  addTime.setOnClickListener(addTimeListener);
        time = (TextView) findViewById(R.id.time);
        simpleTimePicker = (TimePicker) findViewById(R.id.simpleTimePicker);
        simpleTimePicker.setIs24HourView(true); // used to display AM/PM mode
        // perform set on time changed listener event
        simpleTimePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                // display a toast with changed values of time picker
              //  Toast.makeText(getApplicationContext(), hourOfDay + "  " + minute, Toast.LENGTH_SHORT).show();
                time.setText("Start time is :: " + hourOfDay + " : " + minute); // set the current time in text view
              temptime = hourOfDay + "::" + minute ;
            }
        });

        time2 = (TextView) findViewById(R.id.time2);
        simpleTimePicker2 = (TimePicker) findViewById(R.id.simpleTimePicker2);
        simpleTimePicker2.setIs24HourView(true); // used to display AM/PM mode
        // perform set on time changed listener event
        simpleTimePicker2.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                // display a toast with changed values of time picker
              //  Toast.makeText(getApplicationContext(), hourOfDay + "  " + minute, Toast.LENGTH_SHORT).show();
                time2.setText("End time is :: " + hourOfDay + " : " + minute);
                temptime2 = hourOfDay + "::" + minute ;

            }
        });
        addTime.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new TimeUnlocking.AsyncLogin().execute(temptime,temptime2,email_address);

            }
        });
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

                    url = new URL("https://lockrapp.000webhostapp.com/timeset.inc.php");

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
                        .appendQueryParameter("startime", params[0])
                        .appendQueryParameter("endtime", params[1])
                        .appendQueryParameter("username", params[2]);
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
                Toast.makeText(TimeUnlocking.this, "Invalid email or password", Toast.LENGTH_LONG).show();

            } else if (result.equalsIgnoreCase("exception") || result.equalsIgnoreCase("unsuccessful")) {

                Toast.makeText(TimeUnlocking.this, "OOPs! Something went wrong. Connection Problem.", Toast.LENGTH_LONG).show();

            }
        }

    }

}
