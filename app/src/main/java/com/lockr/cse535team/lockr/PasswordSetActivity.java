package com.lockr.cse535team.lockr;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bcgdv.asia.lib.connectpattern.ConnectPatternView;
import com.takwolf.android.lock9.Lock9View;

import java.util.ArrayList;

/**
 * Created by rkotwal2 on 11/13/2016.
 */

public class PasswordSetActivity extends AppCompatActivity {
    ConnectPatternView view;;
    Button confirmButton, retryButton;
    TextView textView;
    boolean isEnteringFirstTime = true;
    boolean isEnteringSecondTime = false;
    String enteredPassword = "", secondPassword="";
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    Context context;
    boolean password_set = false;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

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
        patternData();

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("From", "Confirm entering");
                if(!password_set) {
                    Log.d("password set", "false");
                    textView.setText("Re Draw Pattern");
                    patternData();
                }
                else {
                    Log.d("password set", "true");
                    finish();
                }

            }
        });
        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isEnteringFirstTime = true;
                isEnteringSecondTime = false;
                editor.clear();
                textView.setText("Draw Pattern");
                confirmButton.setEnabled(false);
                retryButton.setEnabled(false);
            }
        });

    }
    public void patternData(){
        view.animateIn();
        view.setOnConnectPatternListener(new ConnectPatternView.OnConnectPatternListener() {
            @Override
            public void onPatternEntered(ArrayList<Integer> result) {
                if(isEnteringFirstTime) {
                    Log.d("isEnteringFirstTime", "From true value");
                    enteredPassword = String.valueOf(result);
                    editor.putString("enteredpassword", enteredPassword);
                    editor.commit();
                    isEnteringFirstTime = false;
                }
                else {
                    Log.d("isEnteringFirstTime", "From false value");
                    secondPassword = String.valueOf(result);
                    editor.putString("secondpassword", secondPassword);
                    editor.commit();
                    password_set = true;
                }
            }

            @Override
            public void onPatternAbandoned() {
                confirmButton.setEnabled(true);
                retryButton.setEnabled(true);
            }

            @Override
            public void animateInStart() {

            }

            @Override
            public void animateInEnd() {
                confirmButton.setEnabled(true);
                retryButton.setEnabled(true);
            }

            @Override
            public void animateOutStart() {

            }

            @Override
            public void animateOutEnd() {
                confirmButton.setEnabled(true);
                retryButton.setEnabled(true);
            }
        });
    }

    }
