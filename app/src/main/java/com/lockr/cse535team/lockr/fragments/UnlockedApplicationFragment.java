package com.lockr.cse535team.lockr.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lockr.cse535team.lockr.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class UnlockedApplicationFragment extends Fragment {


    public UnlockedApplicationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_unlocked_application, container, false);
    }

}
