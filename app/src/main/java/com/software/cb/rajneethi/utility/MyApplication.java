package com.software.cb.rajneethi.utility;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;


/**
 * Created by Vijay on 24-10-2016.
 */

public class MyApplication extends Application {
    private static MyApplication mInstance;

    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;

        AnalyticsTrackers.initialize(this);
        AnalyticsTrackers.getInstance().get(AnalyticsTrackers.Target.APP);

    }

    @Override
    protected void attachBaseContext(Context newBase) {

        super.attachBaseContext(newBase);
        MultiDex.install(this);
    }

    public synchronized Tracker getGoogleAnalyticsTracker() {
        AnalyticsTrackers analyticsTrackers = AnalyticsTrackers.getInstance();
        return analyticsTrackers.get(AnalyticsTrackers.Target.APP);
    }
    public void trackEvent(String category, String action, String label,long value) {
        Tracker t = getGoogleAnalyticsTracker();

        // Build and send an Event.
        t.send(new HitBuilders.EventBuilder().setCategory(category).setAction(action).setLabel(label).setValue(value).setNonInteraction(true).build());
    }



    public static synchronized MyApplication getInstance() {
        return mInstance;
    }

    public void setConnectivityListener(checkInternet.ConnectivityReceiverListener listener) {
        checkInternet.connectivityReceiverListener = listener;
    }




}
