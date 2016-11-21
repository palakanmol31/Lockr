package com.lockr.cse535team.lockr;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by sdshah10 on 11/14/2016.
 */

public class changePassword extends Activity {
    EditText oldPassword, newPassword1, newPassword2;
    String old ="", new1 ="", new2 = "";
    Button changePass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_changepassword);
        oldPassword = (EditText) findViewById(R.id.oldPassword);
        newPassword1 = (EditText) findViewById(R.id.newPassword1);
        newPassword2 = (EditText) findViewById(R.id.newPassword2);
        old = oldPassword.toString();
        new1 = newPassword1.toString();
        new2 = newPassword2.toString();

        changePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Check if the old password entered is same as that in the db
                //If yes then update the new password

            }
        });

    }
}
