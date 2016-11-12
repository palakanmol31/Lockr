package com.lockr.cse535team.lockr;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * Created by sdshah10 on 11/8/16.
 */
public class ForgotPassword extends Activity {
    LoginDataBaseAdapter loginDataBaseAdapter;
    EditText userEmail;

    String fromEmail = "cse535team@gmail.com";
    String fromPassword = "mobilecomputing";
    String emailSubject = "Your password";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotpassword);
        loginDataBaseAdapter = new LoginDataBaseAdapter(this);
        loginDataBaseAdapter = loginDataBaseAdapter.open();
        userEmail = (EditText) findViewById(R.id.UserEmailID);
        Button BtnRecover = (Button) findViewById(R.id.BtnRecover);
//        final TextView showText = (TextView) findViewById(R.id.serverResponse);
        BtnRecover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Mail m = new Mail(fromEmail, fromPassword);
                m.setTo(userEmail.getText().toString());
                m.setSubject(emailSubject);
                m.setBody("Your new password is 1234.");
                m.setFrom(fromEmail);
//                Toast.makeText(ForgotPassword.this, userEmail.getText().toString().trim(), Toast.LENGTH_LONG).show();
                try {
                    if(m.send()) {
                        Toast.makeText(ForgotPassword.this, "Email was sent successfully.", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(ForgotPassword.this, "Email was not sent.", Toast.LENGTH_LONG).show();
                    }
                } catch(Exception e) {
                    //Toast.makeText(MailApp.this, "There was a problem sending the email.", Toast.LENGTH_LONG).show();
                    Log.e("MailApp", "Could not send email", e);
                }
            }
        });

    }
}
