package com.lockr.cse535team.lockr;

/**
 * Created by palak on 11/13/16.
 */

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.Locale;

public class SettingsLocation extends Activity
        implements OnClickListener {
    SessionClass sessionClass;
    private LocationManager locationMangaer = null;
    static String email_address = "";
    private LocationListener locationListener = null;
    private Button btnGetLocation = null;
    private EditText editLocation = null;
    private ProgressBar pb = null;
    CheckBox checkBox;
    TextView enableText;
    public static final int CONNECTION_TIMEOUT = 10000;
    public static final int READ_TIMEOUT = 15000;
  //  View.OnClickListener checkBoxListener;
    public static int location_check = 0;
    private static final String TAG = "Debug";
    private Boolean flag = false;
  TextView getLocation ;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_location);
        ArrayList<String> user;
        sessionClass = new SessionClass(getApplicationContext());
        user = sessionClass.getUserDetails();
        email_address = user.get(0);

        //if you want to lock screen for always Portrait mode
        setRequestedOrientation(ActivityInfo
                .SCREEN_ORIENTATION_PORTRAIT);
        enableText= (TextView) findViewById(R.id.lockText);
        checkBox = (CheckBox) findViewById(R.id.timeLock);
        pb = (ProgressBar) findViewById(R.id.progressBar1);
        pb.setVisibility(View.INVISIBLE);
        getLocation = (TextView) findViewById(R.id.getLocation);

        checkBox.setOnClickListener(this);
        editLocation = (EditText) findViewById(R.id.editTextLocation);

        btnGetLocation = (Button) findViewById(R.id.btnLocation);
        btnGetLocation.setOnClickListener(this);

        locationMangaer = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.timeLock :
                if(checkBox.isChecked()) {
                    location_check = 1;
                    btnGetLocation.setVisibility(View.VISIBLE);
                    editLocation.setVisibility(View.VISIBLE);
                    getLocation.setVisibility(View.VISIBLE);
                }
                    else
                    {
                        location_check = 0;
                        btnGetLocation.setVisibility(View.INVISIBLE);
                        editLocation.setText("");
                        editLocation.setVisibility(View.INVISIBLE);
                        getLocation.setVisibility(View.INVISIBLE);
                        String address="0";
                        new SettingsLocation.AsyncLogin().execute(address,email_address);
                    }
                break;

            case R.id.btnLocation :

            flag = displayGpsStatus();
            if (flag) {

                Log.v(TAG, "onClick");

                editLocation.setText("Wait..");

                pb.setVisibility(View.VISIBLE);
                locationListener = new MyLocationListener();

                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                locationMangaer.requestLocationUpdates(LocationManager
                        .GPS_PROVIDER, 5000, 10, locationListener);

            } else {
                alertbox("Gps Status!!", "Your GPS is: OFF");
            }
                break ;
        }

    }

    /*----Method to Check GPS is enable or disable ----- */
    private Boolean displayGpsStatus() {
        ContentResolver contentResolver = getBaseContext()
                .getContentResolver();
        boolean gpsStatus = Settings.Secure
                .isLocationProviderEnabled(contentResolver,
                        LocationManager.GPS_PROVIDER);
        if (gpsStatus) {
            return true;

        } else {
            return false;
        }
    }

    /*----------Method to create an AlertBox ------------- */
    protected void alertbox(String title, String mymessage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your Device's GPS is Disable")
                .setCancelable(false)
                .setTitle("** Gps Status **")
                .setPositiveButton("Gps On",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // finish the current activity
                                // AlertBoxAdvance.this.finish();
                                Intent myIntent = new Intent(
                                        Settings.ACTION_SECURITY_SETTINGS);
                                startActivity(myIntent);
                                dialog.cancel();
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // cancel the dialog box
                                dialog.cancel();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    /*----------Listener class to get coordinates ------------- */
    private class MyLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location loc) {

            editLocation.setText("");
            pb.setVisibility(View.INVISIBLE);
           // Toast.makeText(getBaseContext(),"Location changed : Lat: " +
               //             loc.getLatitude()+ " Lng: " + loc.getLongitude(),
             //       Toast.LENGTH_SHORT).show();
            String longitude = "Longitude: " +loc.getLongitude();
            Log.v(TAG, longitude);
            String latitude = "Latitude: " +loc.getLatitude();
            Log.v(TAG, latitude);

    /*----------to get City-Name from coordinates ------------- */
            String cityName=null;
            String address = null;
            Geocoder gcd = new Geocoder(getBaseContext(),
                    Locale.getDefault());
            List<Address>  addresses;
            try {
                addresses = gcd.getFromLocation(loc.getLatitude(), loc
                        .getLongitude(), 1);
                if (addresses.size() > 0)
                    System.out.println(addresses.get(0).getLocality());
                cityName=addresses.get(0).getLocality();
                address = addresses.get(0).getAddressLine(0);
            } catch (IOException e) {
                e.printStackTrace();
            }

            String s = "\n\nMy Currrent City is: "+cityName + "  \nAddress is: "+address;
            // @ Saaketh store only address variable

            editLocation.setText(s);
            new SettingsLocation.AsyncLogin().execute(address,email_address);
        }

        @Override
        public void onProviderDisabled(String provider) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onProviderEnabled(String provider) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onStatusChanged(String provider,
                                    int status, Bundle extras) {
            // TODO Auto-generated method stub
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
                url = new URL("https://lockrapp.000webhostapp.com/locset.inc.php");

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
                        .appendQueryParameter("location", params[0])
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
                Toast.makeText(SettingsLocation.this, "Invalid email or password", Toast.LENGTH_LONG).show();

            } else if (result.equalsIgnoreCase("exception") || result.equalsIgnoreCase("unsuccessful")) {

                Toast.makeText(SettingsLocation.this, "OOPs! Something went wrong. Connection Problem.", Toast.LENGTH_LONG).show();

            }
        }

    }
}