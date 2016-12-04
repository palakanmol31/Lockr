package com.lockr.cse535team.lockr;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.lockr.cse535team.lockr.fragments.ServiceFragment;

import java.util.Random;

/**
 * Created by sdshah10 on 11/14/2016.
 */
public class SimpleWidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        final int count = appWidgetIds.length;

        for (int i = 0; i < count; i++) {
            int widgetId = appWidgetIds[i];


            RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                    R.layout.simple_widget);

            Intent intent = new Intent(context, SimpleWidgetProvider.class);
            intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                    0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setOnClickPendingIntent(R.id.actionButton, pendingIntent);
            appWidgetManager.updateAppWidget(widgetId, remoteViews);

        }

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
            ServiceFragment serviceFragment = new ServiceFragment();
            // Create a fresh intent
            Intent serviceIntent = new Intent(context, LockApp.class);
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                    R.layout.simple_widget);
            if(serviceFragment.isRunning) {
                context.stopService(serviceIntent);
                remoteViews.setViewVisibility(R.id.actionButton, View.VISIBLE);
                remoteViews.setViewVisibility(R.id.actionButton, View.INVISIBLE);
                Toast.makeText(context, "service Stopped", Toast.LENGTH_SHORT).show();
            } else {
                context.startService(serviceIntent);
                remoteViews.setViewVisibility(R.id.actionButton, View.VISIBLE);
                remoteViews.setViewVisibility(R.id.actionButton, View.INVISIBLE);
                Toast.makeText(context, "service Started", Toast.LENGTH_SHORT).show();
            }
            ComponentName componentName = new ComponentName(context, LockApp.class);
            AppWidgetManager.getInstance(context).updateAppWidget(componentName, remoteViews);

        super.onReceive(context, intent);
    }
}