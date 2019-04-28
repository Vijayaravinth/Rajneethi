package com.software.cb.rajneethi.utility;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.webkit.WebView;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class ServerUtils {
    private static Map<String,String> customHeaders = new HashMap<String, String>();
    private static Map<String,Object> appParameters = new HashMap<String, Object>();

    public static void init(Activity context){
        WebView webview = new WebView(context);
        customHeaders.put("User-Agent", webview.getSettings().getUserAgentString());
        customHeaders.put("Content-Type", "application/json");
        customHeaders.put("accept", "application/json");
        customHeaders.put("accept-encoding", "gzip, deflate");
        customHeaders.put("content-type", "application/json");
    }

    public static String getProperty(String key, Context context) throws IOException {
        Properties properties = new Properties();
        AssetManager assetManager = context.getAssets();
        InputStream inputStream = assetManager.open("server.properties");
        properties.load(inputStream);
        return properties.getProperty(key);
    }

    public static String getServerPath(String scenario, Context context) throws IOException{
        StringBuilder sb = new StringBuilder();
        sb.append(getProperty("method", context));
        sb.append("://");
        sb.append(getProperty("domain", context));
        sb.append(":");
        sb.append(getProperty("port", context));
        sb.append(getProperty("apipath", context));
        sb.append(getProperty(scenario, context));
        return sb.toString();
    }

    public static Map<String, String> getCustomHeaders() {
        return customHeaders;
    }

    public static void addCustomHeaderProperty(String key, String value) {
        customHeaders.put(key, value);
    }

    public static void addAppParameter(String key, Object value) {
        appParameters.put(key, value);
    }

    public static Object getAppParameter(String key) {
        return appParameters.get(key);
    }

    public static void removeAppParameter(String key) {
        appParameters.remove(key);
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager)  context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
