package com.lockr.cse535team.lockr;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.bcgdv.asia.lib.connectpattern.ConnectPatternView;

import java.util.ArrayList;

/**
 * Created by rkotwal2 on 11/13/2016.
 */

public class PasswordSetActivity extends AppCompatActivity {
    ConnectPatternView view;;
//    Button confirmButton, retryButton;
    TextView textView;
    boolean isEnteringFirstTime = true;
    boolean isEnteringSecondTime = false;
    static String enteredPassword, secondPassword;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    Context context;
    static boolean password_set = false;
    String pinpassword, secondpassword;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        context = getApplicationContext();
        setContentView(R.layout.activity_password_set);
        view = (ConnectPatternView) findViewById(R.id.connect);
        textView = (TextView) findViewById(R.id.textView);
        sharedPreferences = getSharedPreferences(AppLockConstants.MyPREFERENCES, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        patternData();

        if(password_set){
            Intent intent = new Intent(this,ScreenLock.class);
            intent.putExtra("LockType","pattern");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }


    }
    public void patternData(){
        view.animateIn();
        view.setOnConnectPatternListener(new ConnectPatternView.OnConnectPatternListener() {
            @Override
            public void onPatternEntered(ArrayList<Integer> result) {
                if(isEnteringFirstTime) {
                    Log.d("isEnteringFirstTime", "From true value");
                    enteredPassword = String.valueOf(result);
                    textView.setText("Redraw Pattern");
                    isEnteringFirstTime = false;
                    patternData();
                }
                else {
                    Log.d("isEnteringFirstTime", "From false value");
                    secondPassword = String.valueOf(result);
                    editor.putString(AppLockConstants.PASSWORD, secondPassword);
                    Log.d(enteredPassword, secondPassword);
                    if(secondPassword.equals(enteredPassword)){
                        password_set = true;
                        finish();
                    }
                    else {
                        editor.clear();
                        textView.setText("Draw Pattern");
                        isEnteringFirstTime = true;
                    }
                editor.commit();
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
