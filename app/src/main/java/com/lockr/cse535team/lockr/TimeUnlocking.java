package com.lockr.cse535team.lockr;

import android.app.TimePickerDialog;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.Calendar;

public class TimeUnlocking extends AppCompatActivity {

    TextView enableText;
    TextView time;
    TimePicker simpleTimePicker;
    TextView time2;
    TimePicker simpleTimePicker2;
    CheckBox checkBox;
    View.OnClickListener checkBoxListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_picker);
        //  initiate the view's
        enableText= (TextView) findViewById(R.id.lockText);
        checkBox = (CheckBox) findViewById(R.id.timeLock);
        checkBoxListener =new View.OnClickListener() {

            @Override
            public void onClick(View v) {


                if(checkBox.isChecked()) {
                  time.setVisibility(View.VISIBLE);
                    time2.setVisibility(View.VISIBLE);
                    simpleTimePicker.setVisibility(View.VISIBLE);
                    simpleTimePicker2.setVisibility(View.VISIBLE);
                }
                else
                {
                    time.setVisibility(View.INVISIBLE);
                    time2.setVisibility(View.INVISIBLE);
                    simpleTimePicker.setVisibility(View.INVISIBLE);
                    simpleTimePicker2.setVisibility(View.INVISIBLE);
                }


                }

        };
        checkBox.setOnClickListener(checkBoxListener);
        time = (TextView) findViewById(R.id.time);
        simpleTimePicker = (TimePicker) findViewById(R.id.simpleTimePicker);
        simpleTimePicker.setIs24HourView(false); // used to display AM/PM mode
        // perform set on time changed listener event
        simpleTimePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                // display a toast with changed values of time picker
              //  Toast.makeText(getApplicationContext(), hourOfDay + "  " + minute, Toast.LENGTH_SHORT).show();
                time.setText("Start time is :: " + hourOfDay + " : " + minute); // set the current time in text view
            }
        });

        time2 = (TextView) findViewById(R.id.time2);
        simpleTimePicker2 = (TimePicker) findViewById(R.id.simpleTimePicker2);
        simpleTimePicker2.setIs24HourView(false); // used to display AM/PM mode
        // perform set on time changed listener event
        simpleTimePicker2.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                // display a toast with changed values of time picker
              //  Toast.makeText(getApplicationContext(), hourOfDay + "  " + minute, Toast.LENGTH_SHORT).show();
                time2.setText("End time is :: " + hourOfDay + " : " + minute); // set the current time in text view
            }
        });
    }


}
