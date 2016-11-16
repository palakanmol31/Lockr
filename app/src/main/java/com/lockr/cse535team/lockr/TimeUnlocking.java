package com.lockr.cse535team.lockr;

import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;

import java.util.Calendar;

/**
 * Created by palak on 11/14/16.
 */



        import android.app.Activity;
        import android.app.Dialog;
        import android.os.Bundle;
        import android.view.View;
        import android.view.View.OnClickListener;
        import android.widget.Button;
        import android.app.TimePickerDialog;
        import android.widget.TimePicker ;



        import java.util.Calendar;

public class TimeUnlocking extends Activity implements View.OnClickListener {

    private Button mTimeButton;

    private Calendar mCalen;
    private int hourOfDay;
    private int minute;
    private int ampm;

    private Button endmTimeButton;

    private Calendar endmCalen;
    private int endhourOfDay;
    private int endminute;
    private int endampm;

    private static final int Time_PICKER_ID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_picker);
        mTimeButton = (Button) findViewById(R.id.start_button);
        mCalen = Calendar.getInstance();
        hourOfDay = mCalen.get(Calendar.HOUR_OF_DAY);
        minute = mCalen.get(Calendar.MINUTE);
        ampm = mCalen.get(Calendar.AM_PM);
        mTimeButton.setOnClickListener(this);

        endmTimeButton = (Button) findViewById(R.id.end_button);
        endmCalen = Calendar.getInstance();
        endhourOfDay = endmCalen.get(Calendar.HOUR_OF_DAY);
        endminute = endmCalen.get(Calendar.MINUTE);
        endampm = endmCalen.get(Calendar.AM_PM);
        endmTimeButton.setOnClickListener(this);
    }

    @Override
    @Deprecated
    protected Dialog onCreateDialog(int id) {

        switch (id) {
            case Time_PICKER_ID:
                return new TimePickerDialog(this, TimePickerListener,
                        hourOfDay, minute, false);
        }
        return null;
    }

    private TimePickerDialog.OnTimeSetListener TimePickerListener =
            new TimePickerDialog.OnTimeSetListener() {

                // while dialog box is closed, below method is called.
                public void onTimeSet(TimePicker view, int hour, int minute) {

                    mCalen.set(Calendar.HOUR_OF_DAY, hour);
                    mCalen.set(Calendar.MINUTE, minute);

                    int hour12format = mCalen.get(Calendar.HOUR);
                    hourOfDay = mCalen.get(Calendar.HOUR_OF_DAY);
                    minute = mCalen.get(Calendar.MINUTE);
                    ampm = mCalen.get(Calendar.AM_PM);
                    String ampmStr = (ampm == 0) ? "AM" : "PM";
                    // Set the Time String in Button
                    mTimeButton.setText(hour12format + " : " + minute + " / " + ampmStr);
                }
            };

    @Override
    public void onClick(View v) {
        showDialog(Time_PICKER_ID);
    }
}
