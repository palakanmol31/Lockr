package com.lockr.cse535team.lockr;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

/**
 * Created by sdshah10 on 11/14/2016.
 */

public class lockType extends Activity {
    RadioGroup radioGroup;
    RadioButton radioButton, radioButton1, radioButton2, radioButton3;
    Intent intent;
    private final int REQUEST_PERMISSION_FINGERPRINT = 1;
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock_type);
        context = getApplicationContext();
        radioGroup = (RadioGroup) findViewById(R.id.lockType);

        radioButton1 = (RadioButton) findViewById(R.id.radioPattern);
        radioButton2 = (RadioButton) findViewById(R.id.radioPin);
        radioButton3 = (RadioButton) findViewById(R.id.radioFingerPrint);

        checkFingerprintVisibility();
        radioButton1.setChecked(true);
        //If pattern in DB keep radio button checked
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                Log.d("From on Check changed", "Of radio button");
                int selectedID = radioGroup.getCheckedRadioButtonId();
                radioButton = (RadioButton) findViewById(selectedID);

                if (radioButton.getText().equals("Pin")) {
                    //First check if pin is there in db
                    //if not set the pin
                    Log.d("Pin", "Intent shuld start");
                    intent = new Intent(lockType.this, PinSet.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            }
        });

    }

    public void checkFingerprintVisibility() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //Fingerprint API only available on from Android 6.0 (M)
            FingerprintManager fingerprintManager = (FingerprintManager) this.getSystemService(Context.FINGERPRINT_SERVICE);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.USE_FINGERPRINT}, REQUEST_PERMISSION_FINGERPRINT);
                return;

            }

            if (!fingerprintManager.isHardwareDetected()) {
                radioButton3.setVisibility(View.GONE);
                // Device doesn't support fingerprint authentication
            } else if (!fingerprintManager.hasEnrolledFingerprints()) {
                // User hasn't enrolled any fingerprints to authenticate with
                radioButton3.setVisibility(View.GONE);
                Toast.makeText(this,
                        "Go to 'Settings -> Security -> Fingerprint' and register at least one fingerprint",
                        Toast.LENGTH_LONG).show();
                return;
            } else {
                // Everything is ready for fingerprint authentication
                radioButton3.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_PERMISSION_FINGERPRINT &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Please give fingerprint permissions", Toast.LENGTH_LONG).show();
            return;
        }
        //checkFingerprints();
    }
}
