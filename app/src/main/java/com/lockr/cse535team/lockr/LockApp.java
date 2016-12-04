package com.lockr.cse535team.lockr;

import android.Manifest;
import android.app.ActivityManager;
import android.app.Dialog;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.lockr.cse535team.lockr.Singleton.MyApplication;

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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

/**
 * Created by panmol on 11/13/2016.
 */

public class LockApp extends Service {

    private static final String TAG = LockApp.class.getSimpleName();

    private Context context = null;
    private Timer timer;
    SessionClass sessionClass;
    static int t1;
    static int varcheck;
    private WindowManager windowManager;
    private Dialog dialog;
    public static final int CONNECTION_TIMEOUT = 10000;
    static String email_address = "";
    public static final int READ_TIMEOUT = 15000;
    public static String currentApp = "";
    public static String previousApp = "";
    ArrayList<String> pakageName;
    MyApplication myApplication;
    private LocationManager locationMangaer = null;

    @Override
    public void onCreate() {
        ArrayList<String> user;
        sessionClass = new SessionClass(getApplicationContext());
        user = sessionClass.getUserDetails();
        email_address = user.get(0);

        super.onCreate();
        String str="";
        Log.d(TAG, "onCreate: ");
        context = getApplicationContext();
        myApplication = MyApplication.getInstance();
        pakageName = myApplication.readFromPreferences(getApplicationContext(), "Locked", "Null");
        Log.d(TAG, "onCreate: " + pakageName);
        timer = new Timer(TAG);
        timer.schedule(updateTask, 1000L, 1000L);
        /*try {
            str = new AsyncLogin().execute(email_address).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        Log.d(TAG,"FROM DATABASE Start Time") ;
        Log.d(TAG,"FROM DATABASE Start Time" + str) ;*/


    }

    @Override
    public void onDestroy() {
        timer.cancel();
    }

    private TimerTask updateTask = new TimerTask() {
        @Override
        public void run() {
            if (!pakageName.equals("Null")) {
                if (pakageName.contains(getTopAppName(context)) &&  !isUserWifiConnected()  && !isTimeCheck() && !isUserLocationConnected() ) {
                    Log.d(TAG, "isConcernedAppIsInForeground: NOOOOOOOOOOOOOO... DO NOT OPENNNNNN");
                    Intent lockapp = new Intent(getApplicationContext(), ScreenLock.class);
                    lockapp.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    lockapp.putExtra("BLU", true);
                    startActivity(lockapp);
                }
            }

        }
    };

    public static String getTopAppName(Context context) {
        ActivityManager mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        String strName = "";
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                strName = getLollipopFGAppPackageName(context);
            } else {
                strName = mActivityManager.getRunningTasks(1).get(0).topActivity.getClassName();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return strName;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private static String getLollipopFGAppPackageName(Context ctx) {
        try {
            //noinspection WrongConstant
            UsageStatsManager usageStatsManager = (UsageStatsManager) ctx.getSystemService("usagestats");
            long milliSecs = 60 * 1000;
            Date date = new Date();
            List<UsageStats> queryUsageStats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, date.getTime() - milliSecs, date.getTime());
            if (queryUsageStats.size() > 0) {
                Log.i("LPU", "queryUsageStats size: " + queryUsageStats.size());
            }
            long recentTime = 0;
            String recentPkg = "";
            for (int i = 0; i < queryUsageStats.size(); i++) {
                UsageStats stats = queryUsageStats.get(i);
                if (i == 0 && !"org.pervacio.pvadiag".equals(stats.getPackageName())) {
                    Log.i("LPU", "PackageName: " + stats.getPackageName() + " " + stats.getLastTimeStamp());
                }
                if (stats.getLastTimeStamp() > recentTime) {
                    recentTime = stats.getLastTimeStamp();
                    recentPkg = stats.getPackageName();
                }
            }
            return recentPkg;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }


//    private boolean isConcernedAppIsInForeground(ArrayList<String> pakageName, String topAppName) {
//        Log.d(TAG, "isConcernedAppIsInForeground: Packges to compare = "+topAppName);
//
//        if(pakageName.contains(topAppName)){
//            Log.d(TAG, "isConcernedAppIsInForeground: NOOOOOOOOOOOOOO... DO NOT OPENNNNNN" );
//            Intent lockapp = new Intent(this, ScreenLock.class);
//            lockapp.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            lockapp.putExtra("BLU",true);
//            startActivity(lockapp);
//        }
//        return false;
//    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind: ");
        return null;
    }

    public boolean isUserWifiConnected() {
        if(Wifi.wifi_check == 1) {
            Log.d(TAG, "Is in WIFI");
            String curr = getCurrentSsid(getApplicationContext());
            Log.d(TAG, "isUserWifiConnected: " + curr);
            try {
                String getDatabaseWifi = getwifi();
                if (getDatabaseWifi.equals(null)) {
                    Log.d(TAG, "getting from database this wifi " + getDatabaseWifi);
                    return false;
                }
                if (curr.equals(getDatabaseWifi)) {
                    Log.d(TAG, "isUserWifiConnected: FROM TRUE");
                    return true;
                } else {
                    Log.d(TAG, "isUserWifiConnected: FROM FALSE");
                    return false;
                }
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return false;
        }
        else {
            return false;
        }
    }

    public boolean isUserLocationConnected() {
        if(SettingsLocation.location_check == 1) {
            Log.d(TAG, "Is in Location");
            String curr = getCurrentLocation(getApplicationContext());
            Log.d(TAG, "isUserLocationConnected: " + curr);
            try {
                String location = getloc();
                Log.d(TAG, "from database: " + location);
                if (curr.equals(location)) {
                    Log.d(TAG, "isUserLocationConnected: FROM TRUE");
                    return true;
                } else {
                    Log.d(TAG, "isUserLocationConnected: FROM FALSE");
                    return false;
                }
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return false;
        }
        else {
            return false;
        }
    }

    public static String getCurrentSsid(Context context) {
        String ssid = null;
        Log.d(TAG, "getCurrentSsid: From inside");
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (networkInfo.isConnected()) {
            final WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            final WifiInfo connectionInfo = wifiManager.getConnectionInfo();
            if (connectionInfo != null && !TextUtils.isEmpty(connectionInfo.getSSID())) {
                ssid = connectionInfo.getSSID();
            }
        }
        return ssid;
    }

    public String getCurrentLocation(Context context) {
        // final String[] cityName = {null};
        // final String[] address = {null};
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        Criteria crit = new Criteria();

        String  towers = locationManager.getBestProvider(crit, false);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return null;
        }
        Location location = locationManager.getLastKnownLocation(towers);

        //if(location != null){
//
        //          Double glat = location.getLatitude();
        //        Double glon = location.getLongitude();

//        }
//
        String address = null;
        if(location!=null) {
            String cityName = null;

            Geocoder gcd = new Geocoder(getBaseContext(),
                    Locale.getDefault());
            List<Address> addresses;
            try {
                addresses = gcd.getFromLocation(location.getLatitude(), location
                        .getLongitude(), 1);
                if (addresses.size() > 0)
                    System.out.println(addresses.get(0).getLocality());
                cityName = addresses.get(0).getLocality();
                address = addresses.get(0).getAddressLine(0);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //String s = "\n\nMy Currrent City is: "+cityName + "  \nAddress is: "+address;
        // @ Saaketh store only address variable
        //Log.d(TAG,"This is the current lovation" + s);

        return address ;

    }





    public boolean isTimeCheck(){
        t1=1;
        varcheck=0;
        if(TimeUnlocking.time_check == 1) {
            String str = null;
            try {
                str = new AsyncLogin().execute(email_address).get();
                Log.d(TAG, "FROM DATABASE Start Time");
                Log.d(TAG, "FROM DATABASE Start Time" + str);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            Date time1 = null;
            try {
                time1 = new SimpleDateFormat("HH::mm").parse(str);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Calendar calendar1 = Calendar.getInstance();
            calendar1.setTime(time1);
            t1 = 2;
            String str2 = null;
            try {
                varcheck=0;
                t1=2;
                str2 = new AsyncLogin().execute(email_address).get();
                Log.d(TAG, "FROM DATABASE END TIME " + str2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            Date time2 = null;
            try {
                time2 = new SimpleDateFormat("HH::mm").parse(str2);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Calendar calendar2 = Calendar.getInstance();
            calendar2.setTime(time2);
            // calendar2.add(Calendar.DATE, 1);
            Date current = Calendar.getInstance().getTime();

            SimpleDateFormat localDateFormat = new SimpleDateFormat("HH:mm");
            String cur_time = localDateFormat.format(current);
            Date curre = null;
            try {
                curre = new SimpleDateFormat("HH:mm").parse(cur_time);

            } catch (ParseException e) {
                e.printStackTrace();

            }
            Log.d(TAG, "Current time is this " + curre.toString());
            if ((curre.after(calendar1.getTime())) && curre.before(calendar2.getTime())) {
                Log.d(TAG, "time is in between");
                return true;
            } else {
                Log.d(TAG, "time is not in between");
                return false;
            }
        }
        else {
            return false;
        }

    }

    public String getwifi() throws ExecutionException, InterruptedException {

        varcheck=1;
        String str = new LockApp.AsyncLogin().execute(email_address).get();
        Log.d(TAG, "getwifi: wifi is "+str);
        return str ;


    }

    public String getloc() throws ExecutionException, InterruptedException {

        varcheck=2;
        String str = new LockApp.AsyncLogin().execute(email_address).get();
        return str ;

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
                if(varcheck==0) {
                    if (t1 == 1) {
                        url = new URL("https://lockrapp.000webhostapp.com/startime.inc.php");
                    } else {
                        url = new URL("https://lockrapp.000webhostapp.com/endtime.inc.php");
                    }
                }

                if(varcheck==2){
                    url = new URL("https://lockrapp.000webhostapp.com/getloc.inc.php");
                }

                if(varcheck==1){
                    url = new URL("https://lockrapp.000webhostapp.com/getwifi.inc.php");
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
                Toast.makeText(LockApp.this, "Invalid email or password", Toast.LENGTH_LONG).show();

            } else if (result.equalsIgnoreCase("exception") || result.equalsIgnoreCase("unsuccessful")) {

                Toast.makeText(LockApp.this, "OOPs! Something went wrong. Connection Problem.", Toast.LENGTH_LONG).show();

            }
        }

    }
}