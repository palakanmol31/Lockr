package com.lockr.cse535team.lockr;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Looper;

import java.util.List;

/**
 * Created by rkotwal2 on 11/13/2016.
 */

class LockApp extends Thread{
    ActivityManager am = null;
    Context context = null;

    public LockApp(Context con){
        context = con;
        am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
    }

    public void run(){
        Looper.prepare();

        while(true){
            // Return a list of the tasks that are currently running,
            // with the most recent being first and older ones after in order.
            // Taken 1 inside getRunningTasks method means want to take only
            // top activity from stack and forgot the olders.
            List< ActivityManager.RunningTaskInfo > taskInfo = am.getRunningTasks(1);

            String currentRunningActivityName = taskInfo.get(0).topActivity.getClassName();
            System.out.println("---------Activity Running-----------");
            System.out.println(currentRunningActivityName);
            if (currentRunningActivityName.equals("com.lockr.cse535team.lockr") || currentRunningActivityName.equals("com.android.settings.Settings$UsageAccessSettingsActivity")) {
                // show your activity here on top of PACKAGE_NAME.ACTIVITY_NAME
                break;
            }
        }
        Intent i = new Intent(context, ScreenLock.class);
        context.startActivity(i);
        Looper.loop();
    }
}
