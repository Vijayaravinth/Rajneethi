package com.software.cb.rajneethi.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.RemoteViews;

import com.software.cb.rajneethi.R;
import com.software.cb.rajneethi.activity.AddEventsActivity;
import com.software.cb.rajneethi.activity.TimeTableActivity;
import com.software.cb.rajneethi.adapter.EventListAdapter;
import com.software.cb.rajneethi.database.MyDatabase;

import java.util.ArrayList;

/**
 * Created by DELL on 26-01-2018.
 */

public class MyWidgetProvider extends AppWidgetProvider {


    private static final String ACTION_SIMPLEAPPWIDGET = "ACTION_BROADCASTWIDGETSAMPLE";
    private static int mCounter = 0;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        Log.w("WidgetProvider", "On Update Called");
        for (int appWidgetId : appWidgetIds) {
            updateWidget(context, appWidgetManager, appWidgetId);
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.eventListView);
        }
    }


    private void updateWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {

        RemoteViews view = new RemoteViews(context.getPackageName(), R.layout.widget_layout);


        try {

            MyDatabase db = new MyDatabase(context);
            Cursor c = db.getEventsForWidget();
            if (c.getCount() > 0) {

                view.setViewVisibility(R.id.tvWidget, View.GONE);
                view.setViewVisibility(R.id.eventListView, View.VISIBLE);
                Intent listIntent = new Intent(context, MywidgetListviewService.class);
                listIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);

                listIntent.setData(Uri.parse(listIntent.toUri(Intent.URI_INTENT_SCHEME)));
                view.setRemoteAdapter(R.id.eventListView, listIntent);
            } else {

                view.setViewVisibility(R.id.tvWidget, View.VISIBLE);
                view.setViewVisibility(R.id.eventListView, View.GONE);
            }
            Log.w("WidgetProvider", "Cursor count : " + c.getCount());

        } catch (CursorIndexOutOfBoundsException e) {
            e.printStackTrace();
        }


        //go to the particular activity
        Intent intent = new Intent(context, TimeTableActivity.class);
        // In widget we are not allowing to use intents as usually. We have to use PendingIntent instead of 'startActivity'
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        // Here the basic operations the remote view can do.
        view.setOnClickPendingIntent(R.id.tvWidget, pendingIntent);

        Intent appIntent = new Intent(context, AddEventsActivity.class);
        PendingIntent appPendingIntent = PendingIntent.getActivity(context, 0, appIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        view.setPendingIntentTemplate(R.id.eventListView, appPendingIntent);

        //view.setOnClickPendingIntent(R.id.eventListView, pendingIntent);

        //change content in widget
     /*   Intent intent = new Intent(context, MyWidgetProvider.class);
        intent.setAction(ACTION_SIMPLEAPPWIDGET);
        // And this time we are sending a broadcast with getBroadcast
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        view.setOnClickPendingIntent(R.id.tvWidget, pendingIntent);
*/


        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, view);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        Log.w("WidgetProvider", "On Receive working");

        if (AppWidgetManager.ACTION_APPWIDGET_UPDATE.equals(intent.getAction())) {

            RemoteViews view = new RemoteViews(context.getPackageName(), R.layout.widget_layout);


            Bundle extras = intent.getExtras();

            if (extras != null) {

                int[] widgetIds = extras.getIntArray(AppWidgetManager.EXTRA_APPWIDGET_ID);

                if (widgetIds != null) {
                    ComponentName appWidget = new ComponentName(context, MyWidgetProvider.class);
                    AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                    onUpdate(context, appWidgetManager, widgetIds);

                }
            }
        }

    }
}
