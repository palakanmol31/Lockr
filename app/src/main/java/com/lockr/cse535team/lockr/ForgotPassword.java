package com.lockr.cse535team.lockr;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
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
    private static final String TAG = ForgotPassword.class.getSimpleName();
    LoginDataBaseAdapter loginDataBaseAdapter;
    EditText userEmail;
    public static final int CONNECTION_TIMEOUT = 10000;
    public static final int READ_TIMEOUT = 15000;
    String fromEmail = "cse535team@gmail.com";
    String fromPassword = "mobilecomputing";
    String emailSubject = "Your password";
    static String tmppassword=Long.toHexString(Double.doubleToLongBits(Math.random()));
    static String to_pass = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotpassword);
        userEmail = (EditText) findViewById(R.id.UserEmailID);
        Button BtnRecover = (Button) findViewById(R.id.BtnRecover);

//        final TextView showText = (TextView) findViewById(R.id.serverResponse);
        BtnRecover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(ForgotPassword.this, userEmail.getText().toString().trim(), Toast.LENGTH_LONG).show();

                new ForgotPassword.AsyncLogin().execute(userEmail.getText().toString(),tmppassword);

            }
        });

    }

    private class AsyncLogin extends AsyncTask<String, String, String> {
        ProgressDialog pdLoading = new ProgressDialog(ForgotPassword.this);
        HttpURLConnection conn;
        URL url = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //this method will be running on UI thread
            pdLoading.setMessage("\tLoading...");
            pdLoading.setCancelable(false);
            pdLoading.show();

        }

        @Override
        protected String doInBackground(String... params) {
            try {

                // Enter URL address where your php file resides
                url = new URL("https://lockrapp.000webhostapp.com/checklogin.inc.php");

            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return "exception";
            }
            try {
                // Setup HttpURLConnection class to send and receive data from php and mysql
                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(READ_TIMEOUT);
                conn.setConnectTimeout(CONNECTION_TIMEOUT);
                conn.setRequestMethod("POST");

                // setDoInput and setDoOutput method depict handling of both send and receive
                conn.setDoInput(true);
                conn.setDoOutput(true);

                // Append parameters to URL
                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("username", params[0])
                        .appendQueryParameter("password", params[1]);
                String query = builder.build().getEncodedQuery();


                // Open connection for sending data
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(query);
                writer.flush();
                writer.close();
                os.close();
                conn.connect();

            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
                return "exception";
            }

            try {

                int response_code = conn.getResponseCode();

                // Check if successful connection made
                if (response_code == HttpURLConnection.HTTP_OK) {
                    // Read data sent from server

                    InputStream input = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    StringBuilder result = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }
                    // Pass data to onPostExecute method
                    return (result.toString());
                } else {

                    return ("unsuccessful");
                }

            } catch (IOException e) {
                e.printStackTrace();

                return "exception";

            } finally {
                conn.disconnect();
            }


        }

        @Override
        protected void onPostExecute(String result) {

            //this method will be running on UI thread
            pdLoading.dismiss();

            if (result.equalsIgnoreCase("false")) {

                // If username and password does not match display a error message
                Toast.makeText(ForgotPassword.this, "Email does not exist. Try another username. If the problem persists, contact our team!", Toast.LENGTH_LONG).show();

            } else if (result.equalsIgnoreCase("exception") || result.equalsIgnoreCase("unsuccessful")) {

                Toast.makeText(ForgotPassword.this, "Email does not exist. Try another username. If the problem persists, contact our team!", Toast.LENGTH_LONG).show();

            }

            else {

                /* Here launching another activity when login successful. If you persist login state
                use sharedPreferences of Android. and logout button to clear sharedPreferences.
                 */
                Mail m = new Mail(fromEmail, fromPassword);
                m.setTo(userEmail.getText().toString());
                m.setSubject(emailSubject);
                m.setBody("Your new password is "+tmppassword);
                m.setFrom(fromEmail);
                try {

                    if (m.send()) {

                        Toast.makeText(ForgotPassword.this, "Email was sent successfully.", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(ForgotPassword.this, "Email was not sent.", Toast.LENGTH_LONG).show();
                    }

                }catch(Exception e) {
                    //Toast.makeText(MailApp.this, "There was a problem sending the email.", Toast.LENGTH_LONG).show();
                    Log.e("MailApp", "Could not send email", e);
                }
                Intent intent = new Intent(ForgotPassword.this, MainActivity.class);
                startActivity(intent);
                ForgotPassword.this.finish();

            }
        }


    }
}
