package com.lockr.cse535team.lockr.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ToggleButton;

import com.lockr.cse535team.lockr.LockApp;
import com.lockr.cse535team.lockr.MainActivity;
import com.lockr.cse535team.lockr.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ServiceFragment extends Fragment {

    ToggleButton tg;
    boolean isRunning=false;

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

        tg.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(!isRunning) {
                    getActivity().startService(lockService);
                    isRunning = true;
                }
                else
                {
                    getActivity().stopService(lockService);
                    isRunning = false;
                }
            }
        });

        return view;
    }

}
