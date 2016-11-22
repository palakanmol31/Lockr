package com.lockr.cse535team.lockr;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.andrognito.pinlockview.PinLockListener;
import com.andrognito.pinlockview.PinLockView;
import com.bcgdv.asia.lib.connectpattern.ConnectPatternView;
import com.takwolf.android.lock9.Lock9View;

import java.util.ArrayList;

/**
 * Created by rkotwal2 on 11/13/2016.
 */

public class ScreenLock extends Activity {
    private static final String TAG = ScreenLock.class.getSimpleName();
    ConnectPatternView view;
    Context context;
    SharedPreferences passwordPref;
    int no_of_attempts=0;
    Boolean isBlu = false;
    LinearLayout screenlockLayout;
    PinLockView mPinLockView;
    PinLockListener mPinLockListener;
    SharedPreferences sharedPreferences;
    protected void onCreate(Bundle savedInstanceState) {
        context = getApplicationContext();
        super.onCreate(savedInstanceState);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String locktype = sharedPreferences.getString("LockType", "pattern");

        if(locktype.equals("pattern")) {
            setContentView(R.layout.popup_unlock);
            screenlockLayout = (LinearLayout) findViewById(R.id.screenLockLayout);
            view = (ConnectPatternView) findViewById(R.id.connect);
            patternData();
        }
        else if(locktype.equals("Pin")){
            setContentView(R.layout.pin_unlock);
            mPinLockView = (PinLockView) findViewById(R.id.pin_lock_view);
            mPinLockView.setPinLockListener(mPinLockListener);
            pinData();
        }
        isBlu = getIntent().hasExtra("BLU");
    }



    @Override
    public void onBackPressed() {
        Snackbar.make(screenlockLayout,"Enter the password First",Snackbar.LENGTH_LONG).show();
    }

    public void patternData(){

        view.animateIn();
        view.setOnConnectPatternListener(new ConnectPatternView.OnConnectPatternListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override

            public void onPatternEntered(ArrayList<Integer> result) {
                passwordPref = context.getSharedPreferences(AppLockConstants.MyPREFERENCES, Context.MODE_PRIVATE);
                if(passwordPref.getString(AppLockConstants.PASSWORD,"").equals(String.valueOf(result))) {
                    if(isBlu){
                        finishAffinity();
                    }else{
                        finish();
                    }
                }
                else {
                    no_of_attempts++;
                    if(no_of_attempts<5)
                    {
                        Toast.makeText(getApplicationContext(),
                            "Password Mismatch!", Toast.LENGTH_LONG)
                            .show();}
                    else
                    {
                        Intent i = new Intent(context, CameraActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(i);
                        Toast.makeText(getApplicationContext(),
                            "Limit Exceeded!", Toast.LENGTH_LONG)
                            .show();}
                }


            }

            @Override
            public void onPatternAbandoned() {

            }

            @Override
            public void animateInStart() {

            }

            @Override
            public void animateInEnd() {
            }

            @Override
            public void animateOutStart() {

            }

            @Override
            public void animateOutEnd() {
            }
        });

    }

    public void pinData() {
        mPinLockListener = new PinLockListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onComplete(String pin) {
                passwordPref = context.getSharedPreferences(AppLockConstants.MyPREFERENCES_pin, Context.MODE_PRIVATE);
                if(passwordPref.getString(AppLockConstants.PIN,"").equals(String.valueOf(pin))) {
                    if(isBlu){
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            finishAffinity();
                        }
                    }else{
                        finish();
                    }
                }
                else {
                    no_of_attempts++;
                    if(no_of_attempts<5)
                    {
                        Toast.makeText(getApplicationContext(),
                                "Password Mismatch!", Toast.LENGTH_LONG)
                                .show();}
                    else
                    {
                        Intent i = new Intent(context, CameraActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(i);
                        Toast.makeText(getApplicationContext(),
                                "Limit Exceeded!", Toast.LENGTH_LONG)
                                .show();}
                }
            }


            @Override
            public void onEmpty() {
                Log.d(TAG, "Pin empty");
            }

            @Override
            public void onPinChange(int pinLength, String intermediatePin) {
                Log.d(TAG, "Pin changed, new length " + pinLength + " with intermediate pin " + intermediatePin);
            }
        };
    }

}


