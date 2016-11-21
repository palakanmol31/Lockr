package com.lockr.cse535team.lockr;


import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.app.FragmentTransaction;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ToggleButton;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.lockr.cse535team.lockr.fragments.AllApplicationFragment;
import com.lockr.cse535team.lockr.fragments.LockedApplicationFragment;
import com.lockr.cse535team.lockr.fragments.ServiceFragment;
import com.lockr.cse535team.lockr.fragments.UnlockedApplicationFragment;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private static int navItemIndex = 0;
    private String[] activityTitles;

    DrawerLayout drawer;
    private static final String TAG_SERVICE_STATUS = "SERVICE";
    private static final String TAG_LOCKED_APPS = "LOCKED";
    private static final String TAG_UNLOCKED_APPS = "UNLOCKED";
    private static final String TAG_ALL_APPLICATIONS = "ALL";
    private static final String TAG_LOGOUT = "LOGOUT";
    Toolbar toolbar;
    public static String CURRENT_TAG = TAG_SERVICE_STATUS;


    protected static final int REQUEST_ENABLE = 0;
    SessionClass sessionClass;
    SharedPreference check;
    DevicePolicyManager mDPM;
    ComponentName mDeviceAdminSample;
    private Context context = null;
    NavigationView navigationView;
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        activityTitles = getResources().getStringArray(R.array.nav_item_activity_titles);

        sessionClass = new SessionClass(this);
        if(sessionClass.checkLogin()) {
            check = new SharedPreference();
            check.getPassword(getApplicationContext());
        }

        mDeviceAdminSample = new ComponentName(MainActivity.this, DeviceAdminSample.class);
        mDPM = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);

        if (!mDPM.isAdminActive(mDeviceAdminSample)) {
            Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mDeviceAdminSample);
            startActivityForResult(intent, REQUEST_ENABLE);
        } else {
//            mDPM.lockNow();
        }
        AppOpsManager appOps = (AppOpsManager) context
                .getSystemService(Context.APP_OPS_SERVICE);
        int mode = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            mode = appOps.checkOpNoThrow("android:get_usage_stats",
                    android.os.Process.myUid(), context.getPackageName());
        }
        boolean granted = mode == AppOpsManager.MODE_ALLOWED;
        Log.d(TAG, "onCreate: Usage Acess permission granted = " + granted);
        if(!granted){
            Intent intent = new Intent(android.provider.Settings.ACTION_USAGE_ACCESS_SETTINGS);
            startActivity(intent);
        }

        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        service =new LockApp(getApplicationContext());



        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "CURRENTY, I AM USELESS", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        mHandler = new Handler();
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        //drawer.setDrawerListener(toggle);
        toggle.syncState();
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        setupNavigationView();

    }

    private void setupNavigationView() {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.nav_service:
                        navItemIndex = 0;
                        CURRENT_TAG = TAG_SERVICE_STATUS;
                        break;
                    case R.id.nav_allApps:
                        navItemIndex = 1;
                        CURRENT_TAG = TAG_ALL_APPLICATIONS;
                        break;
                    case R.id.nav_lockedApps:
                        navItemIndex = 2;
                        CURRENT_TAG = TAG_LOCKED_APPS;
                        break;
                    case R.id.nav_unlockedApps:
                        navItemIndex = 3;
                        CURRENT_TAG = TAG_UNLOCKED_APPS;
                        break;
                    default:
                        navItemIndex = 0;
                }

                if (item.isChecked()){
                    item.setChecked(false);
                }else{
                    item.setChecked(true);
                }
                item.setChecked(true);

                loadServiceFragment();

                return true;
            }
        });

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawer,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close){
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };
        drawer.setDrawerListener(actionBarDrawerToggle);

        actionBarDrawerToggle.syncState();

    }

    private void loadServiceFragment() {
        selectNavMenu();

        setToolbarTitle();

        if (getSupportFragmentManager().findFragmentByTag(CURRENT_TAG) != null) {
            drawer.closeDrawers();

            // show or hide the fab button
            return;
        }
        Runnable mPendingRunnable = new Runnable() {
            @Override
            public void run() {
                // update the main content by replacing fragments
                Fragment fragment = getHomeFragment();
                android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                        android.R.anim.fade_out);
                fragmentTransaction.replace(R.id.frame, fragment, CURRENT_TAG);
                fragmentTransaction.commitAllowingStateLoss();
            }
        };

        if (mPendingRunnable != null) {
            mHandler.post(mPendingRunnable);
        }

        drawer.closeDrawers();
    }

    private Fragment getHomeFragment() {
        switch (navItemIndex) {
            case 0:
                // Start Service
                return new ServiceFragment();
            case 1:
                // All Application Fragment
                return new AllApplicationFragment();
            case 2:
                //Locked Application Fragment
                return new LockedApplicationFragment();
            case 3:
                //Unlocked Application Fragment
                return new UnlockedApplicationFragment();
            default:
                return  new ServiceFragment();
        }
    }

    private void selectNavMenu() {
        Log.d(TAG, "selectNavMenu: " + navigationView.getMenu().toString());
        navigationView.getMenu().getItem(navItemIndex).setChecked(true);
    }

    private void setToolbarTitle() {
        getSupportActionBar().setTitle(activityTitles[navItemIndex]);
    }



    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (REQUEST_ENABLE == requestCode) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


    @Override
    public void onBackPressed() {

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
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



//    @SuppressWarnings("StatementWithEmptyBody")
//    @Override
//    public boolean onNavigationItemSelected(MenuItem item) {
//        // Handle navigation view item clicks here.
//        int id = item.getItemId();
//        FragmentTransaction transaction;
//
//        if (id == R.id.nav_allApps){
//            AppListing applistFragListing = new AppListing();
//            transaction.replace(R.id.content_main, applistFragListing, String.valueOf(R.id.listall));
//            transaction.addToBackStack(null);
//            transaction.commit();
//
//        } else if (id == R.id.nav_lockedApps) {
//
//        } else if (id == R.id.nav_unlockedApps) {
//
//        }
//        else if (id == R.id.nav_share) {
//
//        } else if (id == R.id.nav_send) {
//
//        }
//        else if(id == R.id.nav_logout) {
//            newFragment = new LogoutActivity();
//            transaction.replace(R.id.content_main, newFragment);
//            transaction.addToBackStack(null);
//            transaction.commit();
//        }
//
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        drawer.closeDrawer(GravityCompat.START);
//        return true;
//    }
    @Override
    protected void onStart() {
        GoogleAnalytics.getInstance(getApplicationContext()).reportActivityStart(this);
        super.onStart();
    }

    @Override
    protected void onStop() {
        GoogleAnalytics.getInstance(getApplicationContext()).reportActivityStop(this);
        super.onStop();
        super.onStop();
    }

}

