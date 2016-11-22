package com.lockr.cse535team.lockr;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.andrognito.pinlockview.IndicatorDots;
import com.andrognito.pinlockview.PinLockListener;
import com.andrognito.pinlockview.PinLockView;
import com.bcgdv.asia.lib.connectpattern.ConnectPatternView;

/**
 * Created by rkotwal2 on 11/13/2016.
 */

public class PinSet extends AppCompatActivity {
    private static final String TAG = PinSet.class.getSimpleName();
    ConnectPatternView view;
    TextView textView;
    boolean isEnteringFirstTime = true;
    boolean isEnteringSecondTime = false;
    static String enteredPassword, secondPassword;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    Context context;
    boolean password_set = false;
    PinLockView mPinLockView;
    IndicatorDots mIndicatorDots;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        context = getApplicationContext();
        setContentView(R.layout.activity_pin_set);
        textView = (TextView) findViewById(R.id.SetPinTV);
        mPinLockView = (PinLockView) findViewById(R.id.pin_lock_view);
        mIndicatorDots = (IndicatorDots) findViewById(R.id.indicator_dots);

        mPinLockView.attachIndicatorDots(mIndicatorDots);
        mPinLockView.setPinLockListener(mPinLockListener);


        mPinLockView.setPinLength(6);


        sharedPreferences = getSharedPreferences(AppLockConstants.MyPREFERENCES_pin, MODE_PRIVATE);
        editor = sharedPreferences.edit();

        if(password_set) {
            Intent intent = new Intent(this, ScreenLock.class);
            intent.putExtra("LockType", "Pin");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }

    }

    private PinLockListener mPinLockListener = new PinLockListener() {
        @Override
        public void onComplete(String pin) {
            Log.d(TAG, "Pin complete: " + pin);
            if (isEnteringFirstTime) {
                Log.d("isEnteringFirstTime", "From true value");
                enteredPassword = String.valueOf(pin);
                textView.setText("Reenter Pin");
                isEnteringFirstTime = false;

            } else {
                Log.d("isEnteringFirstTime", "From false value");
                secondPassword = String.valueOf(pin);
                editor.putString(AppLockConstants.PIN, secondPassword);
                Log.d(enteredPassword, secondPassword);
                if (secondPassword.equals(enteredPassword)) {
                    password_set = true;
                    editor.putString("LockingType:","Pin");
                    finish();
                } else {
                    editor.clear();
                    textView.setText("Enter Pin");
                    isEnteringFirstTime = true;
                }
                editor.apply();
            }

        }

        @Override
        public void onEmpty() {

        }

        @Override
        public void onPinChange(int pinLength, String intermediatePin) {

        }
    };
}


