package com.lockr.cse535team.lockr;

import android.app.Application;

import java.util.HashMap;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;


/**
 * Created by rkotwal2 on 11/12/2016.
 */

public class AppLockApplication extends Application {

    public static final String PROPERTY_ID = "UA-62504955-1";
    private static AppLockApplication appInstance;
    HashMap<TrackerName, Tracker> mTrackers = new HashMap<TrackerName, Tracker>();

    @Override
    public void onCreate() {
        super.onCreate();
        appInstance = this;
    }

    public enum TrackerName {
        APP_TRACKER, // Tracker used only in this app.
        GLOBAL_TRACKER // Tracker used by all the apps from a company. eg:
        // roll-up tracking.
    }

    /**
     * Get an instance of application
     *
     * @return
     */
    public static synchronized AppLockApplication getInstance() {
        return appInstance;
    }

    public synchronized Tracker getTracker(TrackerName trackerId) {
        if (!mTrackers.containsKey(trackerId)) {

            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            Tracker t = (trackerId == TrackerName.APP_TRACKER) ? analytics
                    .newTracker(PROPERTY_ID) : analytics
                    .newTracker(R.xml.global_tracker);
            mTrackers.put(trackerId, t);

        }
        return mTrackers.get(trackerId);
    }
}
