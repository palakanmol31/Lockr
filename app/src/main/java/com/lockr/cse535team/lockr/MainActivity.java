package com.lockr.cse535team.lockr;


import android.app.Activity;
        import android.app.Dialog;
import android.app.FragmentManager;
import android.content.Intent;
        import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
        import android.widget.EditText;
        import android.widget.Toast;

import com.mikepenz.iconics.typeface.FontAwesome;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.Nameable;
import com.mikepenz.materialdrawer.util.KeyboardUtil;

import java.io.Console;

import static com.lockr.cse535team.lockr.R.id.editTextPassword;
        import static com.lockr.cse535team.lockr.R.id.editTextUserName;

public class MainActivity extends AppCompatActivity{
   SessionClass sessionClass;
    android.support.v4.app.FragmentManager fragmentManager;
    private Drawer.Result result = null;
    int i= 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sessionClass = new SessionClass(this);
        super.onCreate(savedInstanceState);
        sessionClass.checkLogin();
        setContentView(R.layout.activity_main);
        fragmentManager = getSupportFragmentManager();
        // Handle Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        result = new Drawer()
                .withActivity(this)
                .withToolbar(toolbar)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName("All Applications").withIcon(FontAwesome.Icon.faw_home),
                        new PrimaryDrawerItem().withName("Locked Applications").withIcon(FontAwesome.Icon.faw_lock),
                        new PrimaryDrawerItem().withName("Unlocked Applications").withIcon(FontAwesome.Icon.faw_unlock),
                        new PrimaryDrawerItem().withName("Change Password").withIcon(FontAwesome.Icon.faw_exchange),
                        new PrimaryDrawerItem().withName("Allow Access").withIcon(FontAwesome.Icon.faw_share)
                ) // add the items we want to use with our Drawer
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id, IDrawerItem drawerItem) {
                        if (drawerItem != null && drawerItem instanceof Nameable) {

                            if (position == 0) {

                            }

                            if (position == 1) {
                                                            }

                            if (position == 2) {

                            }

                            if (position == 3) {

                            }

                            if (position == 4) {

                            }

                        }
                    }
                })
                .withOnDrawerListener(new Drawer.OnDrawerListener() {
                    @Override
                    public void onDrawerOpened(View drawerView) {
                        KeyboardUtil.hideKeyboard(MainActivity.this);
                    }


                    @Override
                    public void onDrawerClosed(View drawerView) {


                    }
                })
                .withFireOnInitialOnClick(true)
                .withSavedInstance(savedInstanceState)
                .build();


        //react on the keyboard
        result.keyboardSupportEnabled(this, true);
        }
    }




