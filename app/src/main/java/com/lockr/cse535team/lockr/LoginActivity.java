package com.lockr.cse535team.lockr;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by sdshah10 on 11/3/2016.
 */

public class LoginActivity extends Activity {
    Button btnSignIn, btnSignUp;
    LoginDataBaseAdapter loginDataBaseAdapter;
    SessionClass session;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loginscreen);
        session = new SessionClass(getApplicationContext());
        loginDataBaseAdapter = new LoginDataBaseAdapter(this);
        loginDataBaseAdapter = loginDataBaseAdapter.open();

        btnSignIn = (Button) findViewById(R.id.buttonSignIn);
        btnSignUp = (Button) findViewById(R.id.buttonSignUP);

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intentSignUP = new Intent(getApplicationContext(), SignUPActivity.class);
                startActivity(intentSignUP);
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                EditText editTextUsername = (EditText) findViewById(R.id.editTextUserNameToLogin);
                EditText editTextPassword = (EditText) findViewById(R.id.editTextPasswordToLogin);
                String userName = editTextUsername.getText().toString();
                String password = editTextPassword.getText().toString();
                String storedPassword = loginDataBaseAdapter
                        .getSinlgeEntry(userName);
                if (password.equals(storedPassword)) {
                    Toast.makeText(getApplicationContext(),
                            "Login Successfull, Your settings would be imported shortly!", Toast.LENGTH_LONG)
                            .show();
                    session.createLoginSession(userName, password);
                    Intent main = new Intent(getApplicationContext(), AllAppsActivity.class);
                    startActivity(main);
                } else {
                    Toast.makeText(getApplicationContext(),
                            "User Name or Password does not match",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}