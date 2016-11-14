package com.lockr.cse535team.lockr;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bcgdv.asia.lib.connectpattern.ConnectPatternView;
import com.takwolf.android.lock9.Lock9View;

/**
 * Created by rkotwal2 on 11/13/2016.
 */

public class PasswordSetActivity extends AppCompatActivity {
    ConnectPatternView view;;
    Button confirmButton, retryButton;
    TextView textView;
    boolean isEnteringFirstTime = true;
    boolean isEnteringSecondTime = false;
    String enteredPassword;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    Context context;
    boolean password_set = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        System.out.println("-------Entered onCreate()------------------");
        super.onCreate(savedInstanceState);
        context = getApplicationContext();
        setContentView(R.layout.activity_password_set);
        view = (ConnectPatternView) findViewById(R.id.connect);
        confirmButton = (Button) findViewById(R.id.confirm);
        retryButton = (Button) findViewById(R.id.retry);
        textView = (TextView) findViewById(R.id.textView);
        confirmButton.setEnabled(false);
        retryButton.setEnabled(false);
        sharedPreferences = getSharedPreferences(AppLockConstants.MyPREFERENCES, MODE_PRIVATE);
        editor = sharedPreferences.edit();


        /*Google Analytics
        Tracker t = ((AppLockApplication) getApplication()).getTracker(AppLockApplication.TrackerName.APP_TRACKER);
        t.setScreenName(AppLockConstants.FIRST_TIME_PASSWORD_SET_SCREEN);
        t.send(new HitBuilders.AppViewBuilder().build());*/

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                System.out.println("-------Entered Confirmed------------------");
                editor.putString(AppLockConstants.PASSWORD, enteredPassword);
                editor.putString(AppLockConstants.IS_PASSWORD_SET, "Yes");
                editor.commit();



                /*Intent i = new Intent(PasswordSetActivity.this, PasswordRecoverSetActivity.class);
                startActivity(i);
                finish();
                AppLockLogEvents.logEvents(AppLockConstants.FIRST_TIME_PASSWORD_SET_SCREEN, "Confirm Password", "confirm_password", "");*/
            }
        });
        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isEnteringFirstTime = true;
                isEnteringSecondTime = false;
                textView.setText("Draw Pattern");
                confirmButton.setEnabled(false);
                retryButton.setEnabled(false);
               // AppLockLogEvents.logEvents(AppLockConstants.FIRST_TIME_PASSWORD_SET_SCREEN, "Retry Password", "retry_password", "");
            }
        });

//        view.setCallBack(new Lock9View.CallBack() {
//            @Override
//            public void onFinish(String password) {
//                retryButton.setEnabled(true);
//                if (isEnteringFirstTime) {
//                    enteredPassword = password;
//                    isEnteringFirstTime = false;
//                    isEnteringSecondTime = true;
//                    textView.setText("Re-Draw Pattern");
//                } else if (isEnteringSecondTime) {
//                    if (enteredPassword.matches(password)) {
//                        confirmButton.setEnabled(true);
//                    } else {
//                        Toast.makeText(getApplicationContext(), "Both Pattern did not match - Try again", Toast.LENGTH_SHORT).show();
//                        isEnteringFirstTime = true;
//                        isEnteringSecondTime = false;
//                        textView.setText("Draw Pattern");
//                        retryButton.setEnabled(false);
//                    }
//                }
//            }
//        });
    }


    }
