package com.lockr.cse535team.lockr;

import android.content.Intent;
import android.os.Bundle;

/**
 * Created by rkotwal2 on 11/8/2016.
 */

public class AppListing extends android.support.v4.app.Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent AList = new Intent(AppListing.this.getActivity(), AllAppsActivity.class);
        startActivity(AList);
    }

}
