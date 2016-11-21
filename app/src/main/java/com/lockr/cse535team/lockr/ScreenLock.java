package com.lockr.cse535team.lockr;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bcgdv.asia.lib.connectpattern.ConnectPatternView;
import com.takwolf.android.lock9.Lock9View;

import java.util.ArrayList;

/**
 * Created by rkotwal2 on 11/13/2016.
 */

public class ScreenLock extends Activity {
    ConnectPatternView view;
    Context context;
    SharedPreferences passwordPref;
    int no_of_attempts=0;
    Boolean isBlu = false;
    LinearLayout screenlockLayout;

    protected void onCreate(Bundle savedInstanceState) {
        context = getApplicationContext();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_unlock);
        screenlockLayout = (LinearLayout) findViewById(R.id.screenLockLayout);
        view = (ConnectPatternView) findViewById(R.id.connect);
        patternData();
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
//                Log.d("entered value", String.valueOf(result));
//                Log.d("stored value", passwordPref.getString(AppLockConstants.PASSWORD,""));
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
                    {Toast.makeText(getApplicationContext(),
                            "Password Mismatch!", Toast.LENGTH_LONG)
                            .show();}
                    else
                    {Toast.makeText(getApplicationContext(),
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

}


