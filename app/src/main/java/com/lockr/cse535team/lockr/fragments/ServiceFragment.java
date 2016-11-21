package com.lockr.cse535team.lockr.fragments;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ToggleButton;

import com.lockr.cse535team.lockr.LockApp;
import com.lockr.cse535team.lockr.MainActivity;
import com.lockr.cse535team.lockr.R;
import com.lockr.cse535team.lockr.SharedPreference;

/**
 * A simple {@link Fragment} subclass.
 */
public class ServiceFragment extends Fragment {

    private static final String TAG = ServiceFragment.class.getSimpleName();
    ToggleButton tg;
    public boolean isRunning=false;
    int count;
    int defaultValue = 0;

    SharedPreferences sharedPreferences;
    public ServiceFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_service, container, false);
        final Intent lockService = new Intent(getActivity(), LockApp.class);

        tg=(ToggleButton) view.findViewById(R.id.btnToggle);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        count = sharedPreferences.getInt("service-count", defaultValue);
        Log.d(TAG, "onCreateView: Value of count is:"+count);
        if(count >0){
            tg.setChecked(true);
        }
        else {
            tg.setChecked(false);
        }

        tg.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(!isRunning) {
                    getActivity().startService(lockService);
                    isRunning = true;
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt("service-count",1);
                    editor.apply();
                }
                else
                {
                    getActivity().stopService(lockService);
                    isRunning = false;
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt("service-count",0);
                    editor.apply();
                }
            }
        });

        return view;
    }

}
