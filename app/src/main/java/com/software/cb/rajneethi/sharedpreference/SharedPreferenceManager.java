package com.software.cb.rajneethi.sharedpreference;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Monica on 09-03-2017.
 */

public class SharedPreferenceManager {


    private Context context;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    private static final String PREF_NAME = "Rajneethi";
    private String KEEP_LOGIN = "keep_login";
    private String USER_ID = "user_id";
    private String PROJECT_ID = "project_id";
    private String IS_QUS_DOWNLOAD = "is_qus_download";
    private String USERNAME = "username";
    private String BOOTH = "booth";
    private String CONSTITUENCY = "constituency";
    private String KEEPLOGINROLE = "keep_login_role";
    private String PASSWORD = "password";
    private String USER_TYPE = "userType";
    private String LANGUAGE = "language";
    private String FCMKEY = "fcmKey";
    private String LAST_TIME = "last_time";
    private String EXPIRY_DATE = "expiry_date";
    private String SENDERID = "senderId";
    private String SENDER_DETAILS = "senderDetails";

    private String LAST_LAT = "lats_lat";
    private String LAST_LNG = "last_lng";


    public SharedPreferenceManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
        editor.apply();
    }

    public void setLastTime(String time) {
        editor.putString(LAST_TIME, time);
        editor.apply();
    }

    public String getLastTime() {
        return pref.getString(LAST_TIME, "");
    }

    public void set_keep_login(boolean status) {
        editor.putBoolean(KEEP_LOGIN, status);
        editor.apply();
    }

    public void set_keep_login_role(String status) {
        editor.putString(KEEPLOGINROLE, status);
        editor.apply();
    }

    public void set_username(String username) {
        editor.putString(USERNAME, username);
        editor.apply();
    }

    public void set_booth(String booth) {
        editor.putString(BOOTH, booth);
        editor.apply();
    }


    public void set_Constituency_id(String constituencyIds) {
        editor.putString(CONSTITUENCY, constituencyIds);
        editor.apply();
    }

    public void set_user_id(String id) {
        editor.putString(USER_ID, id);
        editor.apply();
    }

    public void set_project_id(String project_id) {
        editor.putString(PROJECT_ID, project_id);
        editor.apply();
    }

    public void setPassword(String password) {
        editor.putString(PASSWORD, password);
        editor.apply();
    }

    public String getPasssword() {
        return pref.getString(PASSWORD, "");
    }

    //set fcm key
    public void setFCMKey(String key) {
        editor.putString(FCMKEY, key);
        editor.apply();
    }

    public void setLastLat(String lat){
        editor.putString(LAST_LAT,lat);
        editor.apply();
    }

    public void setLastLng(String lng){
        editor.putString(LAST_LNG,lng);
        editor.apply();
    }


    public String getLastLat (){
        return pref.getString(LAST_LAT,"");
    }

    public String getLastLng(){
        return pref.getString(LAST_LNG,"");
    }

    //get fcm key
    public String getFCMKey() {
        return pref.getString(FCMKEY, "");
    }

    //set user type
    public void set_user_type(String user_type) {
        editor.putString(USER_TYPE, user_type);
        editor.apply();
    }

    //change language
    public void set_Language(String language) {
        editor.putString(LANGUAGE, language);
        editor.apply();
    }

    public void setExpiryDate(String date) {
        editor.putString(EXPIRY_DATE, date);
        editor.apply();
    }

    public void setSenderId(String senderId) {
        editor.putString(SENDERID, senderId);
        editor.apply();
    }

    public void setSenderDetails(String details) {
        editor.putString(SENDER_DETAILS, details);
        editor.apply();
    }

    public String getSenderDetails() {
        return pref.getString(SENDER_DETAILS, "empty");
    }

    public String getSenderId() {
        return pref.getString(SENDERID, "empty");
    }

    public String getExpiryDate() {
        return pref.getString(EXPIRY_DATE, "2019-04-26");
    }

    //get language
    public String getLanguage() {
        return pref.getString(LANGUAGE, "en");
    }

    //get user type
    public String getUserType() {
        return pref.getString(USER_TYPE, "");
    }

    public String get_username() {
        return pref.getString(USERNAME, "");
    }


    public String get_booth() {
        return pref.getString(BOOTH, "");
    }


    public String get_project_id() {
        return pref.getString(PROJECT_ID, "");
    }

    public String get_constituency_id() {
        return pref.getString(CONSTITUENCY, "");
    }

    public void set_is_qus_download(boolean val) {

        editor.putBoolean(IS_QUS_DOWNLOAD, val);
        editor.apply();
    }

    public boolean get_is_qus_downloaded() {
        return pref.getBoolean(IS_QUS_DOWNLOAD, false);
    }

    public boolean get_keep_login() {
        return pref.getBoolean(KEEP_LOGIN, false);
    }

    public String get_keep_login_role() {
        return pref.getString(KEEPLOGINROLE, "");
    }

    public String get_user_id() {
        return pref.getString(USER_ID, "");
    }


}
