package com.lockr.cse535team.lockr;

/**
 * Created by palak on 11/13/16.
 */

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.util.SparseBooleanArray;

import java.util.List;
import android.net.wifi.WifiConfiguration;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Wifi extends Activity implements AdapterView.OnItemClickListener {
    ListView lv;
    WifiManager wifi;
    String wifis[];
    WifiScanReceiver wifiReciever;
    TextView enableText;
    CheckBox checkBox;
    View.OnClickListener checkBoxListener;
   // LinearLayout lin ;
    //CheckBox checkbox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wifi);

        enableText= (TextView) findViewById(R.id.lockText);
        checkBox = (CheckBox) findViewById(R.id.timeLock);
        checkBoxListener =new View.OnClickListener() {

            @Override
            public void onClick(View v) {


                if(checkBox.isChecked()) {
                    lv.setVisibility(View.VISIBLE);
                }
                else
                {
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

    protected void onPause() {
        unregisterReceiver(wifiReciever);
        super.onPause();
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
        String a = (String) adapterView.getItemAtPosition(position);
        //System.out.println("Clicked Position := "+position +" Value: "+sparseBooleanArray.get(position)+"DATA"+a);

        System.out.println("Clicked Network" + a) ;

        ///// @Saaketh add variable a to the database . Variable a is the name of wifi and can have multiple values .
    }


    private class WifiScanReceiver extends BroadcastReceiver {
        public void onReceive(Context c, Intent intent) {
           // List<ScanResult> wifiScanList = wifi.getScanResults();
            List<WifiConfiguration> l = wifi.getConfiguredNetworks();
           // wifis = new String[wifiScanList.size()];
            wifis = new String[l.size()];

            //for(int i = 0; i < wifiScanList.size(); i++){
              //  wifis[i] = ((wifiScanList.get(i)).SSID.toString());
            //}
            for(int i = 0; i < l.size(); i++){
              wifis[i] = ((l.get(i)).SSID.toString());
            }

            lv.setAdapter(new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_list_item_checked,wifis));
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