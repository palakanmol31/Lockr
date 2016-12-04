package com.lockr.cse535team.lockr;

/**
 * Created by palak on 11/13/16.
 */

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.util.SparseBooleanArray;

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
import java.util.List;
import android.net.wifi.WifiConfiguration;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Wifi extends Activity implements AdapterView.OnItemClickListener {
    private static final String TAG =Wifi.class.getSimpleName() ;
    ListView lv;
    WifiManager wifi;
    SessionClass sessionClass;
    public static final int CONNECTION_TIMEOUT = 10000;
    static String email_address = "";
    public static final int READ_TIMEOUT = 15000;
    String wifis[];
    WifiScanReceiver wifiReciever;
    TextView enableText;
    CheckBox checkBox;
    View.OnClickListener checkBoxListener;
    SharedPreferences preferences ;
    public static final String CheckboxValue = "CHK";
    public static final String pos = "positionKey";
    public static int wifi_check = 0;

    // String a;
    int selected_position;
   // LinearLayout lin ;
    //CheckBox checkbox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wifi);

       preferences = getPreferences(MODE_PRIVATE);

        // Set the values of the UI
        //EditText oControl = (EditText)findViewById(R.id.txtName);
        //oControl.setText(preferences.getString("Name", null));

       // oControl = (EditText)findViewById(R.id.txtEmail);
        //oControl.setText(preferences.getString("Email", null));
        ArrayList<String> user;
        sessionClass = new SessionClass(getApplicationContext());
        user = sessionClass.getUserDetails();
        email_address = user.get(0);
        enableText= (TextView) findViewById(R.id.lockText);
        checkBox = (CheckBox)findViewById(R.id.timeLock);

        if(preferences.contains(CheckboxValue))
           checkBox.setChecked(preferences.getBoolean("TandC", false));

        if(preferences.contains(CheckboxValue))
        { lv.setVisibility(View.VISIBLE);
            lv.setItemChecked(preferences.getInt("TandC",0),true);}






       // checkBox = (CheckBox) findViewById(R.id.timeLock);
        checkBoxListener =new View.OnClickListener() {

            @Override
            public void onClick(View v) {


                if(checkBox.isChecked()) {
                    lv.setVisibility(View.VISIBLE);
                    wifi_check = 1;
                    save();
                }
                else
                {
                    wifi_check = 0;
                    lv.setVisibility(View.INVISIBLE);
                }


            }

        };
        checkBox.setOnClickListener(checkBoxListener);
        lv=(ListView)findViewById(R.id.listView);
        lv.setClickable(true);
        lv.setOnItemClickListener(this);
    //    lin = (LinearLayout) findViewById(R.id.lin);

        wifi=(WifiManager)getSystemService(Context.WIFI_SERVICE);
        wifiReciever = new WifiScanReceiver();
        wifi.startScan();
    }

    @Override
    protected void onPause()
    {
        //super.onPause();



        // Commit to storage

        unregisterReceiver(wifiReciever);
        super.onPause();

    }
    public void save() {
        //super.onStop();
        Log.d(TAG,"Inside save");

        // Store values between instances here
        //SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        preferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();  // Put the values from the UI


        CheckBox chkTandC = (CheckBox)findViewById(R.id.timeLock);
        boolean blnTandC = chkTandC.isChecked();

        //editor.putString(“Name”, strName); // value to store
        //editor.putString(“Email”, strEmail); // value to store
        editor.putBoolean("checkbox", blnTandC); // value to store
      editor.putInt("value",selected_position);

        editor.commit();

    }



    protected void onResume() {
        registerReceiver(wifiReciever, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        //SparseBooleanArray sparseBooleanArray = lv.getCheckedItemPositions();
        selected_position = position ;
        String a = (String) adapterView.getItemAtPosition(position);
        //System.out.println("Clicked Position := "+position +" Value: "+sparseBooleanArray.get(position)+"DATA"+a);

        System.out.println("Clicked Network" + a) ;
        new Wifi.AsyncLogin().execute(a,email_address);
        ///// @Saaketh add variable a to the database . Variable a is the name of wifi and can have multiple values .
    }


    private class WifiScanReceiver extends BroadcastReceiver {
        public void onReceive(Context c, Intent intent) {
           // List<ScanResult> wifiScanList = wifi.getScanResults();
            List<WifiConfiguration> l = wifi.getConfiguredNetworks();
            if(l == null){
                Toast.makeText(getApplicationContext(), "Please connect to a wifi to access this", Toast.LENGTH_LONG).show();
            }
            else {
                // wifis = new String[wifiScanList.size()];
                wifis = new String[l.size()];

                //for(int i = 0; i < wifiScanList.size(); i++){
                //  wifis[i] = ((wifiScanList.get(i)).SSID.toString());
                //}
                for (int i = 0; i < l.size(); i++) {
                    wifis[i] = ((l.get(i)).SSID.toString());
                }

                lv.setAdapter(new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_checked, wifis));
                // lv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

                //for(int i = 0 ; i < l.size();i++)
                //{
                //  checkbox = new CheckBox(getApplicationContext());
                // checkbox.setId(i);

//lin.addView(checkbox);
                // }
            }
        }
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

                    url = new URL("https://lockrapp.000webhostapp.com/setwifi.inc.php");


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
                        .appendQueryParameter("ssid", params[0])
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
                //finish();

            } else if (result.equalsIgnoreCase("false")) {

                // If username and password does not match display a error message
                Toast.makeText(Wifi.this, "Invalid email or password", Toast.LENGTH_LONG).show();

            } else if (result.equalsIgnoreCase("exception") || result.equalsIgnoreCase("unsuccessful")) {

                Toast.makeText(Wifi.this, "OOPs! Something went wrong. Connection Problem.", Toast.LENGTH_LONG).show();

            }
        }

    }
}