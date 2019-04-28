package com.software.cb.rajneethi.widget;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.graphics.Color;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.software.cb.rajneethi.R;
import com.software.cb.rajneethi.activity.TimeTableActivity;
import com.software.cb.rajneethi.database.MyDatabase;
import com.software.cb.rajneethi.models.EventsDetails;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

/**
 * Created by DELL on 30-01-2018.
 */

public class MywidgetListviewService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new ListRemoteView(this.getApplicationContext(), intent);
    }


    class ListRemoteView implements RemoteViewsService.RemoteViewsFactory {

        private Context context;
        private ArrayList<EventsDetails> eventList;

        public ListRemoteView(Context context, Intent intent) {
            this.context = context;

            Log.w("WidgetListService", "WidgetList Service called");
        }

        @Override
        public void onCreate() {

            eventList = new ArrayList<>();
        }

        @Override
        public void onDataSetChanged() {
            Log.w("WidgetListService", "On Data Changed working");
            eventList.clear();

            MyDatabase db = new MyDatabase(context);
            try {
                Cursor c = db.getEventsForWidget();
                if (c.moveToFirst()) {
                    do {

                        try {
                            // String input_date = "01/08/2012";
                            SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                            Date dt1 = format1.parse(c.getString(2));

                            Log.w("WidgetService", "Date : " + c.getString(2));
                            DateFormat format2 = new SimpleDateFormat("EEEE d MMM,yy", Locale.getDefault());
                            String finalDay = format2.format(dt1);

                            eventList.add(new EventsDetails(c.getString(0), c.getString(1), finalDay, c.getString(3)));

                         /*   c1.setTime(new SimpleDateFormat("dd-M-yyyy").parse(c.getString(2)));
                            int dayOfWeek = c1.get(Calendar.DAY_OF_WEEK_IN_MONTH);*/
                            Log.w("WidgetService", "Day of week : " + finalDay);

                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                    } while (c.moveToNext());

                    c.close();
                }
            } catch (CursorIndexOutOfBoundsException e) {
                e.printStackTrace();
            }
            db.close();

        }

        @Override
        public void onDestroy() {

            eventList.clear();
        }

        @Override
        public int getCount() {
            return eventList.size();
        }

        @Override
        public RemoteViews getViewAt(int i) {

            RemoteViews remoteView = new RemoteViews(context.getPackageName(), R.layout.adapter_evet_list);

            EventsDetails details = eventList.get(i);
            remoteView.setTextViewText(R.id.txtEventDate, details.getEventDate());
            String event = details.getEventTitle() + "\nPlace: " + details.getPlace();
            remoteView.setTextViewText(R.id.txtEventDetails, event);
            Random rnd = new Random();
            int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
            //view.setBackgroundColor(color);
            Intent intent = new Intent();
            intent.putExtra("type", "reschedule");
            intent.putExtra("id", details.getId());
            // In widget we are not allowing to use intents as usually. We have to use PendingIntent instead of 'startActivity'
            //   PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            remoteView.setOnClickFillInIntent(R.id.txtEventDetails, intent);

            remoteView.setOnClickFillInIntent(R.id.txtEventDate,intent);

            remoteView.setInt(R.id.txtEventDetails, "setBackgroundColor", color);
            return remoteView;
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }
    }
}
