package com.lockr.cse535team.lockr;


import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.List;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
        SessionClass sessionClass;
        LogoutActivity newFragment;
        AppListing Fragment1;
        PasswordSetActivity passwordSetActivity;
        SharedPreference check;
        ToggleButton tg;
        LockApp service;
       // boolean shouldContinue=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);



        Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
        startActivity(intent);
        Toast.makeText(getApplicationContext(), "If you have not allowed , allow App Lock so that it can work properly", Toast.LENGTH_LONG).show();

       sessionClass = new SessionClass(this);
        if(sessionClass.checkLogin()) {
            check = new SharedPreference();
            check.getPassword(getApplicationContext());
        }

       /* final PackageManager pm = getPackageManager();
//get a list of installed apps.
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);

        for (ApplicationInfo packageInfo : packages) {
            Log.d("Installed package :" , packageInfo.packageName);
            Log.d("Launch Activity",  ":"  + pm.getLaunchIntentForPackage(packageInfo.packageName));
        }
        */



       /* LockApp CheckCurrent = new LockApp(this);
        CheckCurrent.start();*/

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        service =new LockApp(getApplicationContext());
        tg=(ToggleButton) findViewById(R.id.toggleButton);

       /* tg.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                @Override
                public void onClick(View v)
                {
                    // your click actions go here
                }
               /* Toast.makeText(getApplicationContext(), "On button clicked", Toast.LENGTH_SHORT).show();
                //Thread background = new Thread(new Runnable() {
                    public void run() {
                        try {
                            Log.d("Thread Calling every 1 sec","YO clicked");
                            service.run();
                            Thread.sleep(1000);
                        } catch (Throwable t) {
                        }
                    }
                });

                background.start();*/

           /* }
        });*/

        tg.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                service.run();
                // your click actions go here
            }
        });









        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        //drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent in = new Intent(getApplicationContext(), com.lockr.cse535team.lockr.Settings.class);
            startActivity(in);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        if (id == R.id.nav_allApps){
            Fragment1 = new AppListing();
            transaction.replace(R.id.content_main, Fragment1, String.valueOf(R.id.listall));
            transaction.addToBackStack(null);
            transaction.commit();

        } else if (id == R.id.nav_lockedApps) {

        } else if (id == R.id.nav_unlockedApps) {

        }
        else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }
        else if(id == R.id.nav_logout) {
            newFragment = new LogoutActivity();
            transaction.replace(R.id.content_main, newFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
