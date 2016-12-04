package com.lockr.cse535team.lockr;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.fingerprint.FingerprintManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.andrognito.pinlockview.IndicatorDots;
import com.andrognito.pinlockview.PinLockListener;
import com.andrognito.pinlockview.PinLockView;
import com.bcgdv.asia.lib.connectpattern.ConnectPatternView;
import com.takwolf.android.lock9.Lock9View;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

/**
 * Created by rkotwal2 on 11/13/2016.
 */

public class ScreenLock extends Activity {
    private static final String TAG = ScreenLock.class.getSimpleName();
    ConnectPatternView view;
    Context context;
    SharedPreferences passwordPref;
    public static final int CONNECTION_TIMEOUT = 10000;
    public static final int READ_TIMEOUT = 15000;
    static String email_address;
    static String patternset="setdefault";
    Boolean isBlu = false;
    LinearLayout screenlockLayout;
    PinLockView mPinLockView;
    IndicatorDots mIndicatorDots;
    SharedPreferences sharedPreferences;
    String fromEmail = "cse535team@gmail.com";
    String fromPassword = "mobilecomputing";
    String emailSubject = "Your pattern";
    //classes for fingerprint access
    private FingerprintManager mFingerprintManager;
    private KeyguardManager mKeyguardManager;
    private KeyStore mKeyStore;
    private KeyGenerator mKeyGenerator;
    private Cipher mCipher;
    private FingerprintManager.CryptoObject cryptoObject;
    FingerprintAuthenticationDialogFragment mFragment;
    private static final String DIALOG_FRAGMENT_TAG = "myFragment";
    private static final String SECRET_MESSAGE = "Very secret message";
    /** Alias for our key in the Android Key Store */
    private static final String KEY_NAME = "my_key";
    Button forgotPin ;
    int no_of_attempts=0;
    int no_of_attempts_pin = 0 ;
    static int j=0;
    static String patterntomail="";
    static String pintomail="";
    static String patternsetted;
    SessionClass sessionClass;
    protected void onCreate(Bundle savedInstanceState) {
        context = getApplicationContext();
        super.onCreate(savedInstanceState);
        sessionClass = new SessionClass(getApplicationContext());
        ArrayList<String> user = sessionClass.getUserDetails();
        email_address = user.get(0);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String locktype = sharedPreferences.getString("LockingType:", "pattern");
        Log.d(TAG, "onCreate: Locktype is" +locktype);
        Toast.makeText(ScreenLock.this, locktype, Toast.LENGTH_LONG).show();
        isBlu = getIntent().hasExtra("BLU");
        if(locktype.equals("pattern")) {
            setContentView(R.layout.popup_unlock);
            LinearLayout linlay = (LinearLayout) findViewById(R.id.screenLockLayout);

            linlay.setBackgroundColor(Color.WHITE);
            if(changebg.color.equals("Red")){

                linlay.setBackgroundColor(Color.RED);
            }

            if(changebg.color.equals("Green")){

                linlay.setBackgroundColor(Color.GREEN);
            }

            forgotPin = (Button) findViewById(R.id.forget_password);
            forgotPin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                Toast.makeText(ForgotPassword.this, userEmail.getText().toString().trim(), Toast.LENGTH_LONG).show();
                    try {
                        j=0;
                        patterntomail=new AsyncLogin().execute(email_address).get();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                    Mail m = new Mail(fromEmail, fromPassword);
                    m.setTo(email_address);
                    m.setSubject(emailSubject);
                    m.setBody("Your new password is "+patterntomail);
                    m.setFrom(fromEmail);
                    try {

                        if (m.send()) {

                            Toast.makeText(ScreenLock.this, "Email was sent successfully.", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(ScreenLock.this, "Email was not sent.", Toast.LENGTH_LONG).show();
                        }

                    }catch(Exception e) {
                        //Toast.makeText(MailApp.this, "There was a problem sending the email.", Toast.LENGTH_LONG).show();
                        Log.e("MailApp", "Could not send email", e);
                    }
                }
            });
            screenlockLayout = (LinearLayout) findViewById(R.id.screenLockLayout);
            view = (ConnectPatternView) findViewById(R.id.connect);
            patternData();
        }
        else if(locktype.equals("Pin")){
            setContentView(R.layout.pin_unlock);
            LinearLayout linlay = (LinearLayout) findViewById(R.id.screenLockLayout);

            linlay.setBackgroundColor(Color.WHITE);
            if(changebg.color.equals("Red")){

                linlay.setBackgroundColor(Color.RED);
            }

            if(changebg.color.equals("Green")){

                linlay.setBackgroundColor(Color.GREEN);
            }

            mPinLockView = (PinLockView) findViewById(R.id.pin_lock_view);
            mIndicatorDots = (IndicatorDots) findViewById(R.id.indicator_dots);

            mPinLockView.attachIndicatorDots(mIndicatorDots);
            mPinLockView.setPinLockListener(mPinLockListener);


            mPinLockView.setPinLength(6);
            forgotPin = (Button) findViewById(R.id.forget_password);
            forgotPin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                Toast.makeText(ForgotPassword.this, userEmail.getText().toString().trim(), Toast.LENGTH_LONG).show();
                    try {
                        j=1;
                        pintomail=new AsyncLogin().execute(email_address).get();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                    Mail m = new Mail(fromEmail, fromPassword);
                    m.setTo(email_address);
                    emailSubject = "Your pin";
                    m.setSubject(emailSubject);
                    m.setBody("Your new pin is "+pintomail);
                    m.setFrom(fromEmail);
                    try {

                        if (m.send()) {

                            Toast.makeText(ScreenLock.this, "Email was sent successfully.", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(ScreenLock.this, "Email was not sent.", Toast.LENGTH_LONG).show();
                        }

                    }catch(Exception e) {
                        //Toast.makeText(MailApp.this, "There was a problem sending the email.", Toast.LENGTH_LONG).show();
                        Log.e("MailApp", "Could not send email", e);
                    }
                }
            });

        }
        else if(locktype.equals("fingerprint")){
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    checkFingerprints();
                }
            } catch (NoSuchPaddingException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (KeyStoreException e) {
                e.printStackTrace();
            }
        }

    }



    @Override
    public void onBackPressed() {
        Snackbar.make(screenlockLayout,"Enter the password First",Snackbar.LENGTH_LONG).show();
    }

    public void patternData(){

        view.animateIn();
        view.setOnConnectPatternListener(new ConnectPatternView.OnConnectPatternListener() {
            @Override

            public void onPatternEntered(ArrayList<Integer> result) {
                ArrayList<String> user;
                SessionClass sessionClass = new SessionClass(context);
                user=sessionClass.getUserDetails();
                email_address = user.get(0);
                try {
                    patternsetted = new AsyncLogin().execute(email_address).get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                Log.d("hello", "getPassword: "+patternsetted);
//                passwordPref = context.getSharedPreferences(AppLockConstants.MyPREFERENCES, Context.MODE_PRIVATE);
//                Log.d("entered value", String.valueOf(result));
//                Log.d("stored value", passwordPref.getString(AppLockConstants.PASSWORD,""));
                if(patternsetted.equals(String.valueOf(result))) {
                    if(isBlu){
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            finishAffinity();
                        }
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
                    {
                        Intent i = new Intent(context, CameraActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(i);
                        Toast.makeText(getApplicationContext(),
                            "Limit Exceeded!", Toast.LENGTH_LONG)
                            .show();
                        no_of_attempts = 0 ;
                    }
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

    private PinLockListener mPinLockListener = new PinLockListener() {
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public void onComplete(String pin) {
            String pinsetted="";
            j=1;
            try {
                pinsetted= new AsyncLogin().execute(email_address).get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            //passwordPref = context.getSharedPreferences(AppLockConstants.MyPREFERENCES_pin, Context.MODE_PRIVATE);
            if(pinsetted.equals(String.valueOf(pin))) {
                if(isBlu){
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        finishAffinity();
                    }
                }else{
                    finish();
                }
            }
            else {
                no_of_attempts_pin++;
                if(no_of_attempts_pin<5)
                {
                    Toast.makeText(getApplicationContext(),
                            "Password Mismatch!", Toast.LENGTH_LONG)
                            .show();}
                else
                {
                    Intent i = new Intent(context, CameraActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(i);
                    Toast.makeText(getApplicationContext(),
                            "Limit Exceeded!", Toast.LENGTH_LONG)
                            .show();
                    no_of_attempts_pin=0;
                }
            }
        }


        @Override
        public void onEmpty() {
            Log.d(TAG, "Pin empty");
        }

        @Override
        public void onPinChange(int pinLength, String intermediatePin) {
            Log.d(TAG, "Pin changed, new length " + pinLength + " with intermediate pin " + intermediatePin);
        }
    };

    private class AsyncLogin extends AsyncTask<String, String, String> {
        //ProgressDialog pdLoading = new ProgressDialog(PasswordSetActivity.this);
        HttpURLConnection conn;
        URL url = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //this method will be running on UI thread
            // pdLoading.setMessage("\tLoading...");
            // pdLoading.setCancelable(false);
            // pdLoading.show();

        }

        @Override
        protected String doInBackground(String... params) {
            try {

                // Enter URL address where your php file resides
                url = new URL("https://lockrapp.000webhostapp.com/patternretrieve.inc.php");

                if(j==1){

                    url = new URL("https://lockrapp.000webhostapp.com/pinretrieve.inc.php");
                }

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
                        .appendQueryParameter("username", params[0]);
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
                    patternset=result.toString();
                    //Log.d("hello", "getPassword: "+patternset);
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

            // pdLoading.dismiss();
            patternset=result.toString();

            Log.d("hello", "getPasswordexecutre: "+patternset);
            if (result.equalsIgnoreCase("true")) {
                /*Here launching another activity when login successful. If you persist login state
                use sharedPreferences of Android. and logout button to clear sharedPreferences.*/

                // finish();

            } else if (result.equalsIgnoreCase("false")) {

                // If username and password does not match display a error message
                //Toast.makeText(PasswordSetActivity.this, "Invalid email or password", Toast.LENGTH_LONG).show();

            } else if (result.equalsIgnoreCase("exception") || result.equalsIgnoreCase("unsuccessful")) {

                //Toast.makeText(PasswordSetActivity.this, "OOPs! Something went wrong. Connection Problem.", Toast.LENGTH_LONG).show();

            }
        }

    }
















    @TargetApi(Build.VERSION_CODES.M)
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void checkFingerprints() throws NoSuchPaddingException, NoSuchAlgorithmException, KeyStoreException {
        mKeyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
        mFingerprintManager = (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);

        mKeyStore = KeyStore.getInstance("AndroidKeyStore");
        mCipher = Cipher.getInstance(
                KeyProperties.KEY_ALGORITHM_AES + "/"
                        + KeyProperties.BLOCK_MODE_CBC + "/"
                        + KeyProperties.ENCRYPTION_PADDING_PKCS7);
        mFragment = new FingerprintAuthenticationDialogFragment();

        if (!mKeyguardManager.isKeyguardSecure()) {
            // Show a message that the user hasn't set up a fingerprint or lock screen.
            Toast.makeText(this,
                    "Secure lock screen hasn't set up.\n"
                            + "Go to 'Settings -> Security -> Fingerprint' to set up a fingerprint",
                    Toast.LENGTH_LONG).show();
            return;
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        if (!mFingerprintManager.hasEnrolledFingerprints()) {
            // This happens when no fingerprints are registered.
            Toast.makeText(this,
                    "Go to 'Settings -> Security -> Fingerprint' and register at least one fingerprint",
                    Toast.LENGTH_LONG).show();
            return;
        }
        createKey();

        if (initCipher()) {

            // Show the fingerprint dialog. The user has the option to use the fingerprint with
            // crypto, or you can fall back to using a server-side verified password.

            cryptoObject =
                    new FingerprintManager.CryptoObject(mCipher);
            mFragment.setCryptoObject(new FingerprintManager.CryptoObject(mCipher));
            boolean useFingerprintPreference = sharedPreferences
                    .getBoolean(getString(R.string.use_fingerprint_to_authenticate_key),
                            true);
            if (useFingerprintPreference) {
                mFragment.setStage(
                        FingerprintAuthenticationDialogFragment.Stage.FINGERPRINT);
            } else {
                mFragment.setStage(
                        FingerprintAuthenticationDialogFragment.Stage.PASSWORD);
            }
            mFragment.show(getFragmentManager(), DIALOG_FRAGMENT_TAG);
        } else {
            // This happens if the lock screen has been disabled or or a fingerprint got
            // enrolled. Thus show the dialog to authenticate with their password first
            // and ask the user if they want to authenticate with fingerprints in the
            // future
            mFragment.setCryptoObject(new FingerprintManager.CryptoObject(mCipher));
            mFragment.setStage(
                    FingerprintAuthenticationDialogFragment.Stage.NEW_FINGERPRINT_ENROLLED);
            mFragment.show(getFragmentManager(), DIALOG_FRAGMENT_TAG);
        }

    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean initCipher() {
        try {

            mKeyStore.load(null);
            SecretKey key = (SecretKey) mKeyStore.getKey(KEY_NAME, null);
            mCipher.init(Cipher.ENCRYPT_MODE, key);
            return true;
        } catch (KeyPermanentlyInvalidatedException e) {
            return false;
        } catch (KeyStoreException | CertificateException | UnrecoverableKeyException | IOException
                | NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("Failed to init Cipher", e);
        }
    }

    public void onPurchased(boolean withFingerprint) {
        if (withFingerprint) {
            // If the user has authenticated with fingerprint, verify that using cryptography and
            // then show the confirmation message.
            tryEncrypt();
        } else {
            // Authentication happened with backup password. Just show the confirmation message.
            showConfirmation(null);
        }
    }

    // Show confirmation, if fingerprint was used show crypto information.
    private void showConfirmation(byte[] encrypted) {
        if (encrypted != null) {
            Toast.makeText(this, "Congratulations", Toast.LENGTH_LONG).show();
            if(isBlu){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    finishAffinity();
                }
            }else{
                finish();
            }
        }
    }
    private void tryEncrypt() {
        try {
            byte[] encrypted = mCipher.doFinal(SECRET_MESSAGE.getBytes());
            showConfirmation(encrypted);
        } catch (BadPaddingException | IllegalBlockSizeException e) {
            Toast.makeText(this, "Failed to encrypt the data with the generated key. "
                    + "Retry the purchase", Toast.LENGTH_LONG).show();
            Log.e(TAG, "Failed to encrypt the data with the generated key." + e.getMessage());
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void createKey() {
        // The enrolling flow for fingerprint. This is where you ask the user to set up fingerprint
        // for your flow. Use of keys is necessary if you need to know if the set of
        // enrolled fingerprints has changed.
        try {
            mKeyStore.load(null);
            // Set the alias of the entry in Android KeyStore where the key will appear
            // and the constrains (purposes) in the constructor of the Builder
            mKeyGenerator = KeyGenerator.getInstance(
                    KeyProperties.KEY_ALGORITHM_AES,
                    "AndroidKeyStore");
            mKeyGenerator.init(new KeyGenParameterSpec.Builder(KEY_NAME,
                    KeyProperties.PURPOSE_ENCRYPT |
                            KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    // Require the user to authenticate with a fingerprint to authorize every use
                    // of the key
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .build());
            mKeyGenerator.generateKey();
        } catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException
                | CertificateException | IOException e) {
            throw new RuntimeException(e);
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        }
    }
}




