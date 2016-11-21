package com.lockr.cse535team.lockr;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.Dialog;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.provider.*;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageView;

import com.lockr.cse535team.lockr.Singleton.MyApplication;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.SortedMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

/**
 * Created by rkotwal2 on 11/13/2016.
 */

public class LockApp extends Service {

    private static final String TAG = LockApp.class.getSimpleName();

    private Context context = null;
    private Timer timer;
    private WindowManager windowManager;
    private Dialog dialog;
    public static String currentApp = "";
    public static String previousApp = "";
    ArrayList<String> pakageName;
    MyApplication myApplication;
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: ");
        context = getApplicationContext();
        myApplication = MyApplication.getInstance();
        pakageName = myApplication.readFromPreferences(getApplicationContext(),"Locked", "Null");
        Log.d(TAG, "onCreate: "+pakageName);
        timer = new Timer(TAG);
        timer.schedule(updateTask, 1000L, 1000L);


    }

    @Override
    public void onDestroy() {
        timer.cancel();
    }

    private TimerTask updateTask = new TimerTask() {
        @Override
        public void run() {
            if (!pakageName.equals("Null")) {
                if (isConcernedAppIsInForeground(pakageName,getTopAppName(context))) {
                    Log.d(TAG, "run: will never be called");
                }
            }

        }
    };
    public static String getTopAppName(Context context) {
        ActivityManager mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        String strName = "";
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                strName = getLollipopFGAppPackageName(context);
            } else {
                strName = mActivityManager.getRunningTasks(1).get(0).topActivity.getClassName();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return strName;
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private static String getLollipopFGAppPackageName(Context ctx) {
        try {
            //noinspection WrongConstant
            UsageStatsManager usageStatsManager = (UsageStatsManager) ctx.getSystemService("usagestats");
            long milliSecs = 60 * 1000;
            Date date = new Date();
            List<UsageStats> queryUsageStats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, date.getTime() - milliSecs, date.getTime());
            if (queryUsageStats.size() > 0) {
                Log.i("LPU", "queryUsageStats size: " + queryUsageStats.size());
            }
            long recentTime = 0;
            String recentPkg = "";
            for (int i = 0; i < queryUsageStats.size(); i++) {
                UsageStats stats = queryUsageStats.get(i);
                if (i == 0 && !"org.pervacio.pvadiag".equals(stats.getPackageName())) {
                    Log.i("LPU", "PackageName: " + stats.getPackageName() + " " + stats.getLastTimeStamp());
                }
                if (stats.getLastTimeStamp() > recentTime) {
                    recentTime = stats.getLastTimeStamp();
                    recentPkg = stats.getPackageName();
                }
            }
            return recentPkg;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }


    private boolean isConcernedAppIsInForeground(ArrayList<String> pakageName, String topAppName) {
        Log.d(TAG, "isConcernedAppIsInForeground: Packges to compare = "+topAppName);

        if(pakageName.contains(topAppName)){
            Log.d(TAG, "isConcernedAppIsInForeground: NOOOOOOOOOOOOOO... DO NOT OPENNNNNN" );
            Intent lockapp = new Intent(this, ScreenLock.class);
            lockapp.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            lockapp.putExtra("BLU",true);
            startActivity(lockapp);
        }
        return false;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind: ");
        return null;
    }


}


