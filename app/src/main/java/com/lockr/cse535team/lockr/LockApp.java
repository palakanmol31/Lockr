package com.lockr.cse535team.lockr;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.Looper;
import android.widget.Toast;

import java.util.List;

/**
 * Created by rkotwal2 on 11/13/2016.
 */

class LockApp extends Thread {

    /*public IBinder onBind(Intent arg0)
    {
        return null;
    }*/

    public String pActivity = "";
    ActivityManager mActivityManager = null;
    Context context = null;
    boolean shouldcontinue=true;

    public LockApp(Context con) {
        context = con;

    }

    /*
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
    }*/
    public void run() {
        if (Looper.myLooper() == null)
        {
            Looper.prepare();
        }
       // Looper.prepare();

        while (shouldcontinue) {
            System.out.println("---------run() Entered-----------");
            String activityOnTop;
            String c = Context.ACTIVITY_SERVICE;
            ActivityManager mActivityManager = (ActivityManager) context.getSystemService(c);
            System.out.println("---------run() still-----------");
            List<ActivityManager.RunningTaskInfo> RunningTask = mActivityManager.getRunningTasks(1);
            ActivityManager.RunningTaskInfo ar = RunningTask.get(0);
            activityOnTop = ar.topActivity.getClassName();
            System.out.println("---------Activity Running-----------");
            System.out.println(activityOnTop);
//            if (activityOnTop.equals("com.lockr.cse535team.lockr")) {
//                pActivity = activityOnTop.toString();
//
//            } else {
//          if (activityOnTop.equals(pActivity) || activityOnTop.equals("com.lockr.cse535team.lockr")) {
//            } else {

            if(activityOnTop.equals("com.android.settings.Settings$UsageAccessSettingsActivity") )//|| activityOnTop.equals("com.google.android.apps.nexuslauncher.NexusLauncherActivity") ){
            { shouldcontinue=false;
                Intent i = new Intent(context, ScreenLock.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);

            }


                //Toast.makeText(context, pActivity, Toast.LENGTH_SHORT).show();
               // pActivity = activityOnTop.toString();
//            }
//        }
        }
        //System.out.println("---------Outside while()-----------");

     //Looper.loop();
    }
}
