package com.lockr.cse535team.lockr;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.RadioButton;
import android.widget.RadioGroup;

/**
 * Created by sdshah10 on 11/14/2016.
 */

public class lockType extends Activity{
    RadioGroup radioGroup;
    RadioButton radioButton,radioButton1, radioButton2,radioButton3;
    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock_type);
        radioGroup = (RadioGroup) findViewById(R.id.lockType);

        radioButton1 = (RadioButton) findViewById(R.id.radioPattern);
        radioButton2 = (RadioButton) findViewById(R.id.radioPin);
        radioButton3 = (RadioButton) findViewById(R.id.radioFingerPrint);

        radioButton1.setChecked(true);
        //If pattern in DB keep radio button checked
    radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup radioGroup, int i) {
           Log.d("From on Check changed", "Of radio button");
            int selectedID = radioGroup.getCheckedRadioButtonId();
            radioButton = (RadioButton) findViewById(selectedID);

            if(radioButton.getText().equals("Pin")){
                //First check if pin is there in db
                //if not set the pin
                Log.d("Pin", "Intent shuld start");
                intent = new Intent(lockType.this, PinSet.class);
                startActivity(intent);

            }
        }
    });

    }
}
