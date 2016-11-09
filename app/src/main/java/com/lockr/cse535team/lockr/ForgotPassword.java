package com.lockr.cse535team.lockr;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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
public class ForgotPassword extends Activity{
    LoginDataBaseAdapter loginDataBaseAdapter;
    EditText userEmail;
    final String emailPort = "587";
    final String smtpAuth = "true";
    final String starttls = "true";
    final String emailHost = "smtp.gmail.com";

    String fromEmail = "cse535team@gmail.com";
    String fromPassword = "mobilecomputing";
    String toEmailList;
    String emailSubject = "Your password";
    String emailBody = "Hi";

    Properties emailProperties;
    Session mailSession;
    MimeMessage emailMessage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotpassword);
        loginDataBaseAdapter = new LoginDataBaseAdapter(this);
        loginDataBaseAdapter = loginDataBaseAdapter.open();
        userEmail = (EditText) findViewById(R.id.userEmail);
        Button BtnRecover = (Button) findViewById(R.id.BtnRecover);
        final TextView showText = (TextView) findViewById(R.id.serverResponse);
        BtnRecover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendMailTask sendMailTask = new SendMailTask((Activity) getApplicationContext());
                sendMailTask.doInBackground();
                showText.setVisibility(View.VISIBLE);
            }
        });
    }
    public ForgotPassword() {
        this.toEmailList = userEmail.toString();

        emailProperties = System.getProperties();
        emailProperties.put("mail.smtp.port", emailPort);
        emailProperties.put("mail.smtp.auth", smtpAuth);
        emailProperties.put("mail.smtp.starttls.enable", starttls);
        Log.i("GMail", "Mail server properties set.");
    }

    public MimeMessage createEmailMessage() throws AddressException,
            MessagingException, UnsupportedEncodingException {

        mailSession = Session.getDefaultInstance(emailProperties, null);
        emailMessage = new MimeMessage(mailSession);

        emailMessage.setFrom(new InternetAddress(fromEmail, fromEmail));

            Log.i("GMail","toEmail: "+userEmail);
            emailMessage.addRecipient(Message.RecipientType.TO,
                    new InternetAddress(fromEmail, fromEmail));


        emailMessage.setSubject(emailSubject);
        emailMessage.setContent(emailBody, "text/html");// for a html email
        // emailMessage.setText(emailBody);// for a text email
        Log.i("GMail", "Email Message created.");
        return emailMessage;
    }

    public void sendEmail() throws AddressException, MessagingException {

        Transport transport = mailSession.getTransport("smtp");
        transport.connect(emailHost, fromEmail, fromPassword);
        Log.i("GMail","allrecipients: "+emailMessage.getAllRecipients());
        transport.sendMessage(emailMessage, emailMessage.getAllRecipients());
        transport.close();
        Log.i("GMail", "Email sent successfully.");
    }

}
