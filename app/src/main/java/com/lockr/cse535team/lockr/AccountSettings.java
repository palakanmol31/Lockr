package com.lockr.cse535team.lockr;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Created by sdshah10 on 11/14/2016.
 */
public class AccountSettings extends ListActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String[] settings_item = getResources().getStringArray(R.array.account_settings);

        // Binding resources Array to ListAdapter
        this.setListAdapter(new ArrayAdapter<String>(this, R.layout.account_settings, R.id.account_settings, settings_item));

        ListView lv = getListView();

        // listening to single list item on click
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                String product = ((TextView) view).getText().toString();

                if(product.equals("Change Account Password"))
                {
                    Intent i = new Intent(getApplicationContext(), changePassword.class);
                    startActivity(i);
                }

                else if (product.equals("Lock Type"))
                {
                    Intent i = new Intent(getApplicationContext(), lockType.class);
                    startActivity(i);
                }

            }
        });
    }
}
