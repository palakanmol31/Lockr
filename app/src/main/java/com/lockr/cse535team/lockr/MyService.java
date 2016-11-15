package com.lockr.cse535team.lockr;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import android.os.Handler;



/**
 * Created by rkotwal2 on 11/14/2016.
 */

public class MyService extends Service
{
    private static Timer timer = new Timer();
    public Boolean userAuth = false;
    private Context ctx;
    public String pActivity="";

    public IBinder onBind(Intent arg0)
    {
        return null;
    }

    public void onCreate()
    {
        super.onCreate();
        ctx = this;
        startService();
    }

    public void startService()
    {
        System.out.println("---------startService() Entered-----------");
        toastHandler.handleMessage(toastHandler.obtainMessage());
        //timer.scheduleAtFixedRate(new mainTask(), 0, 500);
    }

    private class mainTask extends TimerTask
    {
        public void run()
        {
            toastHandler.sendEmptyMessage(0);
            toastHandler.handleMessage(toastHandler.obtainMessage());
        }
    }

    public void onDestroy()
    {
        super.onDestroy();
        Toast.makeText(this, "Service Stopped ...", Toast.LENGTH_SHORT).show();
    }

    public final Handler toastHandler = new Handler()
    {


        public void handleMessage(Notification.MessagingStyle.Message msg)
        {
            System.out.println("---------handleMessage() Entered-----------");
            String activityOnTop;
            ActivityManager mActivityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningTaskInfo> RunningTask = mActivityManager.getRunningTasks(1);
            ActivityManager.RunningTaskInfo ar = RunningTask.get(0);
            activityOnTop=ar.topActivity.getClassName();
            System.out.println("---------Activity Running-----------");
            System.out.println(activityOnTop);

            if(activityOnTop.equals("com.lockr.cse535team.lockr"))
            {
                pActivity = activityOnTop.toString();
            }
            else
            {
                if(activityOnTop.equals(pActivity) || activityOnTop.equals("com.lockr.cse535team.lockr"))
                {

                }
                else
                {
                    Intent i = new Intent(MyService.this, ScreenLock.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                    Toast.makeText(MyService.this, pActivity, Toast.LENGTH_SHORT).show();
                    pActivity = activityOnTop.toString();

                }
            }


        }
    };
}