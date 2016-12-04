package com.lockr.cse535team.lockr;

import android.app.Fragment;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;

/**
 * Created by sdshah10 on 11/7/2016.
 */
public class LogoutActivity extends Fragment {

    SessionClass sessionClass;
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sessionClass = new SessionClass(getContext());
        sessionClass.logoutUser();
    }
}
