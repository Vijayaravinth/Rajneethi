package com.software.cb.rajneethi.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.software.cb.rajneethi.models.BoothStats;
import com.software.cb.rajneethi.models.EventsDetails;
import com.software.cb.rajneethi.models.Questions;
import com.software.cb.rajneethi.models.VoterAttribute;

/**
 * Created by monika on 3/31/2017.
 */

public class MyDatabase extends SQLiteOpenHelper {

    private Context context;
    private SQLiteDatabase db;

    private static final String TAG = "Mydatabase";

    private static final String DATABASE_NAME = "rajneethi";
    private static final int DATABASE_VERSION = 6;

    public long last_insert_id;

    //table name and fields
    private static final String VOTER_TABLE_NAME = "votersData";
    private static final String CONSTITUENCY_ID = "constituencyId";
    private static final String HIERARCHY_ID = "hierarchyId";
    private static final String HIERARCHY_HASH = "hierarchyHash";
    private static final String VOTER_CARD_NUMBER = "voterCardNumber";
    private static final String NAME_ENGLISH = "nameEnglish";
    private static final String NAME_REGIONAL = "nameRegional";
    private static final String GENDER = "gender";
    private static final String AGE = "age";
    private static final String RELATED_ENGLISH = "relatedEnglish";
    private static final String RELATED_REGIONAL = "relatedRegional";
    private static final String RELATIONSHIP = "relationship";
    private static final String ADDRESS_ENGLISH = "addressEnglish";
    private static final String ADDRESS_REGIONAL = "addressRegional";
    private static final String HOUSE_NO = "houseNo";
    private static final String SERIAL_NO = "serialNo";
    private static final String IS_UPDATED = "isUpdated";
    private static final String ID_PATH = "idPath";
    private static final String PHOTO_PATH = "photoPath";
    private static final String ADDRESS_PATH = "addressPath";

    //Question table
    private static final String QUESTION_TABLE = "questions";
    private static final String ID = "id";
    private static final String NAME = "name";
    private static final String DATA = "data";

    //polling details table
    private static final String POLL_DETAILS_TABLE = "pollDetails";
    private static final String WARD_DETAILS = "wardDetails";
    private static final String BOOTH_DETAILS = "boothDetails";

    //VoterAttribute table
    private static final String VOTER_ATTRIBUTE_TABLE = "voterAttribute";
    private static final String ATTRIBUTE_NAME = "attributeName";
    private static final String ATTRIBUTE_VALUE = "attributeValue";
    private static final String SYNCED = "synced";
    private static final String DATE = "date";
    private static final String SURVEYID = "surveyId";

    //Event table
    private static final String EVENTS_TABLE = "eventsTable";
    private static final String EVENT_TITLE = "eventTitle";
    private static final String EVENT_DATE = "eventDate";
    private static final String EVENT_TIME = "eventTime";
    private static final String CONTACTS = "contacts";
    private static final String REMARKS = "remarks";
    private static final String CONTACTWITHNAME = "contactWithName";
    private static final String PLACE = "place";

    /*create table for add check box values*/
    private static final String CHECKBOX_TABLE = "check_box_table";
    private static final String TITLE = "title";
    private static final String VALUE = "value";

    //mobile number table
    private static final String MOBILE_TABLE = "mobile_numbers";
    private static final String MOBILE_NUMBER = "mobileNumber";
    private static final String LATITUDE = "latitude";
    private static final String LONGITUDE = "longitude";

    //booth table
    private static final String BOOTH_TABLE = "boothData";
    private static final String BOOTH_NAME = "boothName";

    //booth stats table
    private static final String BOOTHSTATS = "boothStats";
    private static final String PID = "pid";
    private static final String SURVEY_TYPE = "surveyType";
    private static final String USER_TYPE = "usrType";

    //notification table
    private static final String NOTIFICATION_TABLE = "notification";
    private static final String MSG = "message";

    //live track count table
    private static final String USER_COUNT_TABLE = "user_count_table";
    private static final String USERID = "userid";
    private static final String LAST_DATE = "last_date";
    private static final String CURRENT_DATE = "current_date";
    private static final String COUNT = "count";

    String createUserCount = "CREATE TABLE " + USER_COUNT_TABLE + "(" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + USERID + " TEXT," + LAST_DATE + " DATE," +
            CURRENT_DATE + " DATE," + COUNT + " INTEGER" + ")";

    //create family segregation
    private static final String FAMILY_SEGREGATION = "family_segregation";
    private static final String FAMILY_ID = "familyId";

    //family head
    private static final String FAMILY_HEAD = "family_head";

    String createFamilyHead = "CREATE TABLE " + FAMILY_HEAD + "(" + VOTER_CARD_NUMBER + " TEXT PRIMARY KEY)";


    //google direction url
    private static final String DIRECTION_URL = "direction_url";
    private static final String PROJECT_ID = "project_id";
    private static final String URl = "url";
    private static final String LOCATION = "location";

    private String createDirectionTable = "CREATE TABLE " + DIRECTION_URL + "(" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + PROJECT_ID + " TEXT," + USERID + " TEXT," +
            URl + " TEXT," + LOCATION + " TEXT," + DATE + " DATE" + ")";

    private String createFamilySegregation = "CREATE TABLE " + FAMILY_SEGREGATION + "(" + VOTER_CARD_NUMBER + " TEXT PRIMARY KEY," + FAMILY_ID + " TEXT)";

    //tele calling details
    private static final String TELECALLING = "telecalling";
    private static final String STATUS = "status";

    private String createTeleCalling = "CREATE TABLE " + TELECALLING + "(" + MOBILE_NUMBER + " TEXT PRIMARY KEY," + STATUS + " TEXT)";

    public MyDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {


        String CREATE_QUESTION_TABLE = "CREATE TABLE " + QUESTION_TABLE + "(" + ID + " TEXT," + CONSTITUENCY_ID + " TEXT," + NAME + " TEXT," + DATA + " TEXT" + ")";
        String CREATE_POLL_DETAILS_TABLE = "CREATE TABLE " + POLL_DETAILS_TABLE + "(" + HIERARCHY_HASH + " TEXT," + WARD_DETAILS + " TEXT," + BOOTH_DETAILS + " TEXT" + ")";
        String CREATE_TABLE_VOTER_ATTRIBUTE = "CREATE TABLE " + VOTER_ATTRIBUTE_TABLE + "(" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + VOTER_CARD_NUMBER + " TEXT," + ATTRIBUTE_NAME + " TEXT," + ATTRIBUTE_VALUE + " TEXT," + SYNCED + " TEXT," + DATE + " DATE," + SURVEYID + " TEXT" + ")";
        String CREATE_EVENT = "CREATE TABLE " + EVENTS_TABLE + "(" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + EVENT_TITLE + " TEXT," + EVENT_DATE + " DATE," + EVENT_TIME + " TEXT," + CONTACTS + " TEXT," + REMARKS + " TEXT," + CONTACTWITHNAME + " TEXT," + PLACE + " TEXT" + ")";
        String CREATE_CHECKBOX_TABLE = "CREATE TABLE " + CHECKBOX_TABLE + "(" + TITLE + " TEXT," + VALUE + " TEXT" + ")";
        String CREATE_MOBILE_TABLE = "CREATE TABLE " + MOBILE_TABLE + "(" + MOBILE_NUMBER + " TEXT,latitude text,longitude text" + ")";
        String CREATE_BOOTH_TABLE = "CREATE TABLE " + BOOTH_TABLE + "(" + BOOTH_NAME + " TEXT" + ")";

        String CREATE_BOOTH_STATS = "CREATE TABLE " + BOOTHSTATS + "(" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + PID + " TEXT," +
                BOOTH_NAME + " TEXT," + SURVEY_TYPE + " TEXT," + USER_TYPE + " TEXT)";
        db.execSQL(CREATE_BOOTH_STATS);

        String createNotification = "Create table " + NOTIFICATION_TABLE + "(" + MSG + " text" + ")";
        db.execSQL(createNotification);

        db.execSQL(CREATE_QUESTION_TABLE);
        db.execSQL(CREATE_POLL_DETAILS_TABLE);
        db.execSQL(CREATE_TABLE_VOTER_ATTRIBUTE);
        db.execSQL(CREATE_EVENT);
        db.execSQL(CREATE_CHECKBOX_TABLE);
        db.execSQL(CREATE_MOBILE_TABLE);
        db.execSQL(CREATE_BOOTH_TABLE);
        db.execSQL(createFamilyHead);

        db.execSQL(createUserCount);
        db.execSQL(createDirectionTable);
        db.execSQL(createFamilySegregation);
        db.execSQL(createTeleCalling);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        Log.w(TAG, "On upgrade working " + oldVersion + " new version :" + newVersion);

        switch (oldVersion) {
            case 1:
                String CREATE_BOOTH_STATS = "CREATE TABLE " + BOOTHSTATS + "(" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + PID + " TEXT," +
                        BOOTH_NAME + " TEXT," + SURVEY_TYPE + " TEXT," + USER_TYPE + " TEXT)";
                db.execSQL(CREATE_BOOTH_STATS);

            case 2:
                db.execSQL("ALTER TABLE " + MOBILE_TABLE + " ADD COLUMN latitude text DEFAULT 0");
                db.execSQL("ALTER TABLE " + MOBILE_TABLE + " ADD COLUMN longitude text DEFAULT 0");

                String createNotification = "Create table " + NOTIFICATION_TABLE + "(" + MSG + " text" + ")";
                db.execSQL(createNotification);
            case 3:

                db.execSQL(createUserCount);
                db.execSQL(createDirectionTable);

            case 4:
                db.execSQL(createFamilySegregation);
                db.execSQL(createFamilyHead);

            case 5:
                db.execSQL(createTeleCalling);

                break;
            default:
                db.execSQL("DROP TABLE IF EXISTS " + QUESTION_TABLE);
                db.execSQL("DROP TABLE IF EXISTS " + POLL_DETAILS_TABLE);
                db.execSQL("DROP TABLE IF EXISTS " + EVENTS_TABLE);
                db.execSQL("DROP TABLE IF EXISTS " + CHECKBOX_TABLE);
                db.execSQL("DROP TABLE IF EXISTS " + VOTER_ATTRIBUTE_TABLE);
                db.execSQL("DROP TABLE IF EXISTS " + MOBILE_TABLE);
                db.execSQL("DROP TABLE IF EXISTS " + BOOTH_TABLE);
                db.execSQL("DROP TABLE IF EXISTS " + BOOTHSTATS);
                db.execSQL("DROP TABLE IF EXISTS " + NOTIFICATION_TABLE);
                db.execSQL("DROP TABLE IF EXISTS " + USER_COUNT_TABLE);
                db.execSQL("DROP TABLE IF EXISTS " + DIRECTION_URL);
                db.execSQL("DROP TABLE IF EXISTS " + FAMILY_SEGREGATION);
                db.execSQL("DROP TABLE IF EXISTS " + FAMILY_HEAD);
                db.execSQL("DROP TABLE IF EXISTS " + TELECALLING);
                onCreate(db);
                break;

        }
     /*   if (oldVersion == 1) {
            Log.w(TAG, "Creating booth stats table");
            String CREATE_BOOTH_STATS = "CREATE TABLE " + BOOTHSTATS + "(" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + PID + " TEXT," +
                    BOOTH_NAME + " TEXT," + SURVEY_TYPE + " TEXT," + USER_TYPE + " TEXT)";
            db.execSQL(CREATE_BOOTH_STATS);
        }
        if (oldVersion == 2) {
            db.execSQL("ALTER TABLE " + MOBILE_TABLE + " ADD COLUMN latitude text");
            db.execSQL("ALTER TABLE " + MOBILE_TABLE + " ADD COLUMN longitude text");
        }*/


    }

    // insert telecalling
    public void insertTeleCalling(String mobile, String status) {

        db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        if (checkTeleDataExist(mobile)) {
            cv.put(STATUS, status);
            db.update(TELECALLING, cv, MOBILE_NUMBER + "='" + mobile + "'", null);
        } else {
            cv.put(MOBILE_NUMBER, mobile);
            cv.put(STATUS, status);
            db.insert(TELECALLING, null, cv);
        }
    }


    public String getStatusTeleCalling(String mobile){
        String status = "Y2V";
        db = this.getReadableDatabase();
        Cursor c = db.rawQuery("select "+ STATUS + " from "+ TELECALLING+" where "+ MOBILE_NUMBER+"='"+ mobile+"'",null);
        if (c.getCount() > 0){
            c.moveToFirst();
            status = c.getString(0);
            c.close();
            return status;
        }else{
            return status;
        }
    }

    private boolean checkTeleDataExist(String mobile) {
        db = this.getReadableDatabase();
        Cursor c = db.rawQuery("select * from " + TELECALLING + " where " + MOBILE_NUMBER + "='" + mobile + "'", null);
        return c.getCount() > 0;
    }

    //get count
    public Cursor getLiveTrackCount(String userid) {
        Log.w(TAG, "user id :" + userid);
        db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + USER_COUNT_TABLE + " WHERE " + USERID + "='" + userid + "'", null);
        if (c != null) {
            return c;
        } else {
            return null;
        }
    }

    // insert into live track
    public void insertDataForLiveTrackCount(String userid, String date) {
        try {
            db = this.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put(USERID, userid);
            cv.put(LAST_DATE, date);
            cv.put(CURRENT_DATE, date);
            cv.put(COUNT, 0);
            long res = db.insert(USER_COUNT_TABLE, null, cv);
            Log.w(TAG, "Insert result :" + res);
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
    }

    //update user date live track
    public void updateDateLiveTrack(String date, String userid) {
        db = this.getWritableDatabase();
        try {
            ContentValues cv = new ContentValues();
            cv.put(LAST_DATE, date);
            cv.put(CURRENT_DATE, date);
            cv.put(COUNT, 0);
            db.update(USER_COUNT_TABLE, cv, USERID + "='" + userid + "'", null);

        } catch (SQLiteException e) {
            e.printStackTrace();
        }
    }

    //update live track count
    public void updateLiveTrackCount(int count, String userid) {
        db = this.getWritableDatabase();
        try {
            ContentValues cv = new ContentValues();
            cv.put(COUNT, count);
            db.update(USER_COUNT_TABLE, cv, USERID + "='" + userid + "'", null);

        } catch (SQLiteException e) {
            e.printStackTrace();
        }
    }

    //insert the direction url
    public void insertUrl(String pid, String uid, String date, String locations) {
        db = this.getWritableDatabase();
        try {
            ContentValues cv = new ContentValues();
            cv.put(PROJECT_ID, pid);
            cv.put(USERID, uid);
            cv.put(LOCATION, locations);
            cv.put(DATE, date);
            last_insert_id = db.insert(DIRECTION_URL, null, cv);
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
    }

    //update url
    public void updateUrl(String id, String url) {
        db = this.getWritableDatabase();
        try {
            ContentValues cv = new ContentValues();
            cv.put("url", url);
            long res = db.update(DIRECTION_URL, cv, ID + "='" + id + "'", null);

            Log.w(TAG, "url update res = " + res);
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
    }

    //get location last url
    public Cursor getLastLocation(String date, String userid) {
        db = this.getReadableDatabase();
        return db.rawQuery("SELECT " + LOCATION + "," + URl + " FROM " + DIRECTION_URL + " WHERE " + DATE + " ='" + date + "' and " + USERID + "='" + userid + "' ORDER BY " + ID + " DESC LIMIT 1", null);
    }

    // insert notification message
    public void insertNotification(String message) {

        db = this.getWritableDatabase();
        try {
            ContentValues cv = new ContentValues();
            cv.put(MSG, message);
            db.insert(NOTIFICATION_TABLE, null, cv);
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
    }

    // delete notification
    public void deleteNotification() {
        db = this.getReadableDatabase();
        try {
            long id = db.delete(NOTIFICATION_TABLE, null, null);
            Log.w(TAG, "Delete booth : " + id);
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
        db.close();
    }

    //get notification tosend
    public Cursor getNotification() {
        db = this.getReadableDatabase();
        String query = "select * from " + NOTIFICATION_TABLE;
        Log.w(TAG, "Query " + query);
        Cursor c = db.rawQuery(query, null);
        if (c != null) {
            return c;
        } else {
            return null;
        }


    }


    public void insertFamilyDetails(String voterCardNumber, String familyId) {
        try {
            db = this.getReadableDatabase();
            ContentValues cv = new ContentValues();
            cv.put(VOTER_CARD_NUMBER, voterCardNumber);
            cv.put(FAMILY_ID, familyId);
            long res = db.insert(FAMILY_SEGREGATION, null, cv);
            Log.w(TAG, "insert :" + res);
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
    }

    public boolean checkFamilyMemberExist(String voterCardNumber) {

        try {
            db = this.getReadableDatabase();

            Cursor c = db.rawQuery("select * from " + FAMILY_SEGREGATION + " where " + VOTER_CARD_NUMBER + "='" + voterCardNumber + "'", null);
            int count = c.getCount();
            Log.w(TAG, "data exist " + count);
            c.close();
            return count > 0;
        } catch (SQLiteException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean checkFamilyHead(String voterCardNumber) {
        try {
            db = this.getReadableDatabase();

            Cursor c = db.rawQuery("select * from " + FAMILY_HEAD + " where " + VOTER_CARD_NUMBER + "='" + voterCardNumber + "'", null);
            int count = c.getCount();
            Log.w(TAG, "data exist " + count);
            c.close();
            return count > 0;
        } catch (SQLiteException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void insertFamilyHead(String voterCardNumber) {
        try {
            db = this.getReadableDatabase();
            ContentValues cv = new ContentValues();
            cv.put(VOTER_CARD_NUMBER, voterCardNumber);
            long res = db.insert(FAMILY_HEAD, null, cv);
            Log.w(TAG, "insert :" + res);
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
    }


    public void deleteFamilyHead(String voterCardNumber) {
        try {
            db = this.getReadableDatabase();
            long res = db.delete(FAMILY_HEAD, VOTER_CARD_NUMBER + "='" + voterCardNumber + "'", null);
            db.close();
            Log.w(TAG, "delete res :" + res);
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
    }

    public void deleteFamilySegregation(String voterCardNumber) {
        try {
            db = this.getReadableDatabase();
            long res = db.delete(FAMILY_SEGREGATION, VOTER_CARD_NUMBER + "='" + voterCardNumber + "'", null);
            db.close();
            Log.w(TAG, "delete res :" + res);
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
    }

    //insert booth stats table
    public void insertBoothStats(BoothStats stats) {

        db = this.getWritableDatabase();
        try {

            ContentValues cv = new ContentValues();
            cv.put(PID, stats.getPid());
            cv.put(BOOTH_NAME, stats.getBoothName().replaceAll("-", "_"));
            cv.put(SURVEY_TYPE, stats.getSurveyType());
            cv.put(USER_TYPE, stats.getUserType());
            db.insert(BOOTHSTATS, null, cv);

        } catch (SQLiteException e) {
            e.printStackTrace();
        }

    }

    //get booth stats
    public Cursor getBoothStats(String pid) {

        db = this.getReadableDatabase();

        String query = "select distinct " + BOOTH_NAME + ",count(" + BOOTH_NAME + ") from " + BOOTHSTATS + " where " + PID + "='" + pid + "' group by " + BOOTH_NAME;

        Log.w(TAG, "Query " + query);
        Cursor c = db.rawQuery(query, null);
        if (c != null) {
            return c;
        } else {
            return null;
        }
    }

    //get booth survey stats
    public Cursor getBoothSurveyStats(String boothName, String pid) {
        db = this.getReadableDatabase();

        String query = "select " + SURVEY_TYPE + ",count(" + SURVEY_TYPE + ") from " + BOOTHSTATS + " where " + BOOTH_NAME + "='" +
                boothName + "' and " + PID + "='" + pid + "'" + " group by " + SURVEY_TYPE;
        Log.w(TAG, "Query " + query);
        Cursor c = db.rawQuery(query, null);
        if (c != null) {
            return c;
        } else {
            return null;
        }
    }

    //insert poll details
    public void insert_poll_details(String hhash, String ward_details, String booth_details) {
        db = this.getReadableDatabase();
        try {
            ContentValues cv = new ContentValues();
            cv.put(HIERARCHY_HASH, hhash);
            cv.put(WARD_DETAILS, ward_details);
            cv.put(BOOTH_DETAILS, booth_details);
            db.insert(POLL_DETAILS_TABLE, null, cv);
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
    }

    public void delete_event(String id) {
        try {
            String[] args = {id};
            getWritableDatabase().delete(EVENTS_TABLE, "id=?", args);
            db.execSQL("DELETE FROM " + EVENTS_TABLE + " WHERE " + ID + "='" + ID + "'");
        } catch (SQLiteException e) {
            e.printStackTrace();
        }


    }

    //insert booth table
    public void insertBooths(String boothName) {
        try {

            db = this.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put(BOOTH_NAME, boothName);
            db.insert(BOOTH_TABLE, null, cv);
            db.close();

            Log.w(TAG,"data inserted");
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
    }


    public boolean checkBoothExist(String boothName) {
        db = this.getReadableDatabase();
        Cursor c = db.rawQuery("select * from " + BOOTH_TABLE + " where " + BOOTH_NAME + "='" + boothName + "'", null);
        return c.getCount() > 0;
    }

    //get booths
    public Cursor getBooths() {
        db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + BOOTH_TABLE, null);
        if (c != null) {
            return c;
        } else {
            db.close();
            return null;
        }
    }

    //delete booths
    public void deleteBooths() {
        db = this.getReadableDatabase();
        try {
            long id = db.delete(BOOTH_TABLE, null, null);
            Log.w(TAG, "Delete booth : " + id);
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
        db.close();
    }


    /*delete checkbox values*/
    public void delete_checkbox_values() {
        db = this.getReadableDatabase();
        try {
            db.delete(CHECKBOX_TABLE, null, null);
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
        db.close();
    }

    /*insert check box table*/
    public void insert_checkbox_value(String title, String value) {

        db = this.getWritableDatabase();
        try {
            ContentValues cv = new ContentValues();
            cv.put(TITLE, title);
            cv.put(VALUE, value);
            db.insert(CHECKBOX_TABLE, null, cv);
            Log.w(TAG, "inserted successfully");
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
    }

    /*INSERT MOBILE NUMBER*/
    public void insert_mobile_number(String mobile, String latitute, String longitude) {
        db = this.getWritableDatabase();
        try {
            ContentValues cv = new ContentValues();
            cv.put(MOBILE_NUMBER, mobile);
            cv.put("latitude", latitute);
            cv.put("longitude", longitude);
            db.insert(MOBILE_TABLE, null, cv);
        } catch (SQLiteException e) {
            e.printStackTrace();
        }

        db.close();
    }

    /*check ,obile number exist or not*/
    public boolean check_mobile_number_exist(String mobile) {
        db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + MOBILE_TABLE + " WHERE " + MOBILE_NUMBER + "='" + mobile + "'", null);
        if (c.getCount() == 0) {
            c.close();
            db.close();
            return false;
        } else {
            c.close();
            db.close();
            return true;
        }
    }


    // check latitude and longitude
    public boolean check_location(String latitude, String longitude) {
        db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + MOBILE_TABLE + " WHERE " + LATITUDE + "='" + latitude + "'  and " + LONGITUDE + "='" + longitude + "'", null);
        if (c.getCount() == 0) {
            c.close();
            db.close();
            return false;
        } else {
            c.close();
            db.close();
            return true;
        }
    }

    /*get title from check_box table*/
    public Cursor get_title() {
        db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + TITLE + " FROM " + CHECKBOX_TABLE + " GROUP BY " + TITLE, null);
        if (cursor != null) {
            return cursor;
        } else {
            return null;
        }
    }

    /*get check box value based title*/
    public Cursor get_check_box_value(String title) {
        db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + CHECKBOX_TABLE + " WHERE " + TITLE + "='" + title + "'", null);
        if (cursor != null) {
            return cursor;
        } else {
            return null;
        }


    }

    /*delete check box value*/
    public void delet_un_checkbox(String title, String value) {
        db = this.getReadableDatabase();
        try {
            db.execSQL("DELETE FROM " + CHECKBOX_TABLE + " WHERE " + TITLE + "='" + title + "'  and " + VALUE + " ='" + value + "'");

        } catch (SQLiteException e) {
            e.printStackTrace();
        }

        db.close();
    }


    /*delete questions*/
    public void delete_question() {
        db = this.getReadableDatabase();
        try {
            db.delete(QUESTION_TABLE, null, null);
        } catch (SQLiteException e) {
            e.printStackTrace();
        }

        db.close();
    }

    /*delete hierarchy hash*/
    public void delete_hierarchy_hash() {
        db = this.getReadableDatabase();
        try {
            db.delete(POLL_DETAILS_TABLE, null, null);
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
    }

    //insert events
    public void insert_events(EventsDetails details) {
        db = this.getWritableDatabase();
        try {
            Log.w(TAG, "date " + details.getEventDate());
            ContentValues cv = new ContentValues();
            cv.put(EVENT_TITLE, details.getEventTitle());
            cv.put(EVENT_DATE, details.getEventDate());
            cv.put(EVENT_TIME, details.getEventTime());
            cv.put(CONTACTS, details.getContacts());
            cv.put(REMARKS, details.getRemarks());
            cv.put(CONTACTWITHNAME, details.getContactWithName());
            cv.put(PLACE, details.getPlace());
            db.insert(EVENTS_TABLE, null, cv);

        } catch (SQLiteException e) {
            e.printStackTrace();
        }
        db.close();
    }

    //insert voter attribute
    public void insert_voter_attribute(VoterAttribute attribute) {
        db = this.getWritableDatabase();
        try {

            ContentValues cv = new ContentValues();
            cv.put(VOTER_CARD_NUMBER, attribute.getVoterCardNumber());
            cv.put(ATTRIBUTE_NAME, attribute.getAttributeName());
            cv.put(ATTRIBUTE_VALUE, attribute.getAttributeValue());
            cv.put(SYNCED, attribute.isSynced() + "");
            cv.put(DATE, attribute.getDate());
            cv.put(SURVEYID, attribute.getSurveyId());

            db.insert(VOTER_ATTRIBUTE_TABLE, null, cv);

        } catch (SQLiteException e) {
            e.printStackTrace();
        }
    }

    public Cursor get_events() {
        db = this.getReadableDatabase();
        Cursor c = db.rawQuery("select * from " + EVENTS_TABLE + " order by " + EVENT_DATE + " desc", null);
        if (c != null) {
            return c;
        } else {
            return null;
        }
    }

    //get eveents for widget
    public Cursor getEventsForWidget() {
        db = this.getReadableDatabase();
        Cursor c = db.rawQuery("select " + ID + "," + EVENT_TITLE + "," + EVENT_DATE + "," + PLACE + " from " + EVENTS_TABLE + " order by " + EVENT_DATE + " desc", null);
        if (c != null) {
            return c;
        } else {
            return null;
        }
    }


    /*get all events*/
    public Cursor get_all_events(String date) {
        db = this.getReadableDatabase();
        Log.w(TAG, "query " + "select " + EVENT_DATE + " from " + EVENTS_TABLE + " where " + EVENT_DATE + " >'" + date + "'");
        Cursor c = db.rawQuery("select " + EVENT_DATE + " from " + EVENTS_TABLE, null);
        if (c != null) {
            return c;
        } else {
            return null;
        }
    }

    public Cursor get_events_details(String id) {
        db = this.getWritableDatabase();
        Cursor c = db.rawQuery("Select * from " + EVENTS_TABLE + " where " + ID + "='" + id + "'", null);
        if (c != null) {
            return c;
        } else {
            return null;
        }
    }


    //Get voters data
    public Cursor getSearchVotersData(String _votername) {
        db = this.getReadableDatabase();
        String QUERY = "select * from " + VOTER_TABLE_NAME + " where " + NAME_ENGLISH + " like '%" + _votername + "'";
        //String QUERY = "select * from " + VOTER_TABLE_NAME + " limit 20 ";
        Log.w(TAG, QUERY);
        Cursor c = db.rawQuery(QUERY, null);
        if (c != null) {
            return c;
        } else {
            return null;
        }

    }


    //get voter attribute data
    public Cursor get_voter_atrributes() {
        db = this.getReadableDatabase();
        Cursor c = db.rawQuery("select * from " + VOTER_ATTRIBUTE_TABLE + " where " + SYNCED + "!='" + "true'" + "  or " + SYNCED + "= 'intermediate'  Limit 0,50", null);
        if (c != null) {
            return c;
        } else {
            return null;
        }

    }

    //get Re sync data
    public void getResyncData() {

        db = this.getWritableDatabase();

        try {
            db.execSQL("update " + VOTER_ATTRIBUTE_TABLE + " set " + SYNCED + "='false'");
        } catch (SQLiteException e) {
            e.printStackTrace();
        }


    }

    //get voter attribute data
    public Cursor get_unsync_data() {
        db = this.getReadableDatabase();
        Cursor c = db.rawQuery("select * from " + VOTER_ATTRIBUTE_TABLE + " where " + SYNCED + "='" + "false'   Limit 0,10", null);
        if (c != null) {
            return c;
        } else {
            return null;
        }

    }

    //get voter attribute data
    public Cursor get_unsync_minimal_data() {
        db = this.getReadableDatabase();
        Cursor c = db.rawQuery("select * from " + VOTER_ATTRIBUTE_TABLE + " where " + SYNCED + "='" + "false'   Limit 0,10", null);
        if (c != null) {
            return c;
        } else {
            return null;
        }

    }

    //get un sync data count
    //get voter attribute data
    public Cursor get_voter_atrributes_count() {
        db = this.getReadableDatabase();
        Cursor c = db.rawQuery("select * from " + VOTER_ATTRIBUTE_TABLE + " where " + SYNCED + "!='" + "true'", null);
        if (c != null) {
            return c;
        } else {
            return null;
        }

    }

    //get all survey data
    public Cursor get_all_survey_data() {
        db = this.getReadableDatabase();
        Cursor c = db.rawQuery("select * from " + VOTER_ATTRIBUTE_TABLE, null);
        if (c != null) {
            return c;
        } else {
            return null;
        }
    }

    public void insert_questions(Questions questions) {
        db = this.getWritableDatabase();
        try {
            ContentValues cv = new ContentValues();
            cv.put(ID, questions.getId());
            cv.put(CONSTITUENCY_ID, questions.getConstituencyId());
            cv.put(NAME, questions.getName());
            cv.put(DATA, questions.getData());
            db.insert(QUESTION_TABLE, null, cv);
            Log.w(TAG, "inserted question successfully " + questions.getName());
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
    }

    //get question
    public Cursor get_question() {
        db = this.getReadableDatabase();
        Cursor c = db.rawQuery("select * from " + QUESTION_TABLE, null);
        if (c != null) {
            return c;
        } else {
            return null;
        }
    }

    //get particular question
    public Cursor get_particular_question(String question) {
        db = this.getReadableDatabase();
        Cursor c = db.rawQuery("select * from " + QUESTION_TABLE + " where " + NAME + "='" + question + "'", null);
        if (c != null) {
            return c;
        } else {
            return null;
        }
    }

    public int get_survey_count() {
        db = this.getReadableDatabase();
        Cursor c = db.rawQuery(" Select * from " + VOTER_ATTRIBUTE_TABLE, null);
        if (c != null) {
            return c.getCount();
        } else {
            return 0;
        }
    }


    //update events
    public void update_events(EventsDetails details, String id) {
        db = this.getWritableDatabase();
        try {
            ContentValues cv = new ContentValues();
            cv.put(EVENT_TITLE, details.getEventTitle());
            cv.put(EVENT_DATE, details.getEventDate());
            cv.put(EVENT_TIME, details.getEventTime());
            cv.put(CONTACTS, details.getContacts());
            cv.put(REMARKS, details.getRemarks());
            cv.put(CONTACTWITHNAME, details.getContactWithName());
            cv.put(PLACE, details.getPlace());
            db.update(EVENTS_TABLE, cv, ID + "='" + id + "'", null);

        } catch (SQLiteException e) {
            e.printStackTrace();
        }
    }

    //update table
    public void update_attribute(String surveyId) {
        db = this.getWritableDatabase();
        try {
            ContentValues cv = new ContentValues();
            cv.put(SYNCED, "true");
            long res = db.update(VOTER_ATTRIBUTE_TABLE, cv, "surveyId='" + surveyId + "'", null);

            Log.w(TAG, "update status :" + res);
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
    }

    //update status
    public void update_attribute_status(String surveyId) {
        db = this.getWritableDatabase();
        try {
            ContentValues cv = new ContentValues();

            cv.put(SYNCED, "intermediate");
            long res = db.update(VOTER_ATTRIBUTE_TABLE, cv, "surveyId='" + surveyId + "'", null);

            Log.w(TAG, "update status :" + res);
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
    }
}
