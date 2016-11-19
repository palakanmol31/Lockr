package com.lockr.cse535team.lockr;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Looper;

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




    public void _stop()
    {
        shouldcontinue= false;
    }
    public void run() {
        if (Looper.myLooper() == null) {
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
            System.out.println("Value of shouldcontinue:" + shouldcontinue);
            //if (activityOnTop.equals("com.android.settings.Settings$UsageAccessSettingsActivity")) //|| activityOnTop.equals("com.android.chrome") || activityOnTop.equals("com.android.camera") || activityOnTop.equals("com.google.android.apps.maps")) {
            if (activityOnTop.equals("com.google.android.apps.nexuslauncher.NexusLauncherActivity")){
                shouldcontinue = false;
                Intent i = new Intent(context, ScreenLock.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
                System.out.println("Value of shouldcontinue:" + shouldcontinue);
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


