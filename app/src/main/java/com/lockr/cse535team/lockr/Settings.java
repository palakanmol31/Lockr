package com.lockr.cse535team.lockr;

/**
 * Created by palak on 11/13/16.
 */

import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.app.Activity ;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class Settings extends ListActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // storing string resources into Array
        String[] settings_item = getResources().getStringArray(R.array.settings_list);

        // Binding resources Array to ListAdapter
        this.setListAdapter(new ArrayAdapter<String>(this, R.layout.list_item, R.id.label, settings_item));

        ListView lv = getListView();

        // listening to single list item on click
        lv.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                // selected item
                String product = ((TextView) view).getText().toString();

                if(product.equals("WIFI based Unlocking"))
                {
                    Intent i = new Intent(getApplicationContext(), Wifi.class);
                    // sending data to new activity
                    //  i.putExtra("product", product);
                    startActivity(i);
                }

                else if (product.equals("Location based Unlocking"))
                {
                    Intent i = new Intent(getApplicationContext(), SettingsLocation.class);
                    // sending data to new activity
                    //  i.putExtra("product", product);
                    startActivity(i);
                }
                else if(product.equals("Time based Unlocking"))
                {
                    Intent i = new Intent(getApplicationContext(), TimeUnlocking.class);
                    // sending data to new activity
                    //  i.putExtra("product", product);
                    startActivity(i);
                }


            }
        });
    }
}
