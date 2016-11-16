package com.lockr.cse535team.lockr;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by rkotwal2 on 11/8/2016.
 */

public class AppListing extends Fragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent AList = new Intent(AppListing.this.getActivity(), AllAppsActivity.class);
        startActivity(AList);}

}
