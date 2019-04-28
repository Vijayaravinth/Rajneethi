package com.software.cb.rajneethi.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;

/**
 * Created by MVIJAYAR on 21-06-2017.
 */

public class VotersDatabase {

    private Context context;
    private SQLiteDatabase db;

    private static final String TAG = "Database";

    private static final String DATABASE_NAME = "voters.db";
    private static final int DATABASE_VERSION = 1;
    private String DB_PATH;


    //table name and fields
    private static final String VOTER_TABLE_NAME = "votersData";
    private static final String CONSTITUENCY_ID = "constituencyId";
    private static final String HIERARCHY_ID = "hierarchyId";
    private static final String HIERARCHY_HASH = "heirarchyHash";
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

    String myPath;


    public VotersDatabase(Context context) {
        //  super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
        DB_PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/Folder/";
        myPath = DB_PATH + DATABASE_NAME;
    }

    /*check database file exist or not*/
    private boolean checkDataBase() {
        File dbFile = new File(DB_PATH + DATABASE_NAME);
        return dbFile.exists();
    }


    //check count of the voters
    public Cursor getCountForPC() {
        if (checkDataBase()) {
            db = SQLiteDatabase.openDatabase(myPath, null,
                    SQLiteDatabase.OPEN_READWRITE);
            Cursor c = db.rawQuery("select * from pc_details", null);
            Log.w(TAG, "count :" + c.getCount());


            return c;
        } else {
            return null;
        }
    }


    //get mobileNumbers
    public Cursor getMobileNumbers(String val) {
        if (checkDataBase()) {
            db = SQLiteDatabase.openDatabase(myPath, null,
                    SQLiteDatabase.OPEN_READWRITE);
            Cursor c = db.rawQuery("select mobile from PhoneTable where caste in (" + val + ")", null);
            Log.w(TAG, "count :" + c.getCount());

            return c;
        } else {
            return null;
        }
    }

    //get ac_name
    public Cursor getAcName() {
        if (checkDataBase()) {
            db = SQLiteDatabase.openDatabase(myPath, null,
                    SQLiteDatabase.OPEN_READWRITE);
            Cursor c = db.rawQuery("select distinct(ac_name) from unitTable", null);
            Log.w(TAG, "count :" + c.getCount());
            return c;
        } else {
            return null;
        }
    }

    //get hubli_name
    public Cursor getHubliName(String ac_name) {
        if (checkDataBase()) {
            db = SQLiteDatabase.openDatabase(myPath, null,
                    SQLiteDatabase.OPEN_READWRITE);
            Cursor c = db.rawQuery("select distinct(hubli_name) from unitTable where ac_name='" + ac_name + "'", null);
            Log.w(TAG, "count :" + c.getCount());
            return c;
        } else {
            return null;
        }
    }

    //get caste wise and hubli wise data
    public Cursor getHubliWiseCasteData( String query) {
        if (checkDataBase()) {
            db = SQLiteDatabase.openDatabase(myPath, null,
                    SQLiteDatabase.OPEN_READWRITE);
            Cursor c = db.rawQuery(query, null);
            Log.w(TAG, "count :" + c.getCount());
            return c;
        } else {
            return null;
        }
    }

    //get house wise data
    public Cursor getCasteWiseHouseData() {
        if (checkDataBase()) {
            db = SQLiteDatabase.openDatabase(myPath, null,
                    SQLiteDatabase.OPEN_READWRITE);
            Cursor c = db.rawQuery("select casteWiseHouse from unitTable", null);
            Log.w(TAG, "count :" + c.getCount());
            return c;
        } else {
            return null;
        }
    }

    //get house wise phone
    public Cursor getCasteWisePhoneData() {
        if (checkDataBase()) {
            db = SQLiteDatabase.openDatabase(myPath, null,
                    SQLiteDatabase.OPEN_READWRITE);
            Cursor c = db.rawQuery("select casteWisePhone from unitTable", null);

            return c;
        } else {
            return null;
        }
    }

    //delete voters data
    public void delete_voters_data() {
        db = SQLiteDatabase.openDatabase(myPath, null,
                SQLiteDatabase.OPEN_READWRITE);
        try {
            db.delete(VOTER_TABLE_NAME, null, null);
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
    }

    //get relation details
    public Cursor get_relation_details(String hno, String address, String votercardnumber) {

        if (checkDataBase()) {
            db = SQLiteDatabase.openDatabase(myPath, null,
                    SQLiteDatabase.OPEN_READONLY);

            Cursor c = db.rawQuery("SELECT * FROM " + VOTER_TABLE_NAME + " WHERE (" + HOUSE_NO + "='" + hno + "' AND " + ADDRESS_ENGLISH + "='" + address + "')" + " AND " + VOTER_CARD_NUMBER + "!='" + votercardnumber + "'", null);
            if (c != null) {
                return c;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    //update table
    public void update_survey(String votercard_number) {
        if (checkDataBase()) {
            db = SQLiteDatabase.openDatabase(myPath, null,
                    SQLiteDatabase.OPEN_READWRITE);
            try {
                ContentValues cv = new ContentValues();
                cv.put(IS_UPDATED, "true");
                db.update(VOTER_TABLE_NAME, cv, VOTER_CARD_NUMBER + "='" + votercard_number + "'", null);

            } catch (SQLiteException e) {
                e.printStackTrace();
            }
            db.close();
        }
    }


    public void updateVoted(String voterCardNumber, int vnv) {
        if (checkDataBase()) {

            db = SQLiteDatabase.openDatabase(myPath, null,
                    SQLiteDatabase.OPEN_READWRITE);
            try {
                ContentValues cv = new ContentValues();
                cv.put(VNV, vnv);
                long val = db.update(VOTER_TABLE_NAME, cv, VOTER_CARD_NUMBER + "='" + voterCardNumber + "'", null);
                Log.w(TAG, "update result : " + val);
            } catch (SQLiteException e) {
                e.printStackTrace();
            }
            db.close();
        }
    }

    //update Mobile Number
    public void updateMobileNumber(String voterCardNumber, String mobile) {

        Log.w(TAG, "Mobile : " + mobile);
        if (checkDataBase()) {
            db = SQLiteDatabase.openDatabase(myPath, null,
                    SQLiteDatabase.OPEN_READWRITE);
            try {
                ContentValues cv = new ContentValues();
                cv.put(MOBILE, mobile);
                long val = db.update(VOTER_TABLE_NAME, cv, VOTER_CARD_NUMBER + "='" + voterCardNumber + "'", null);
                Log.w(TAG, "update result : " + val);
            } catch (SQLiteException e) {
                e.printStackTrace();
            }
            db.close();
        }
    }

    //update all survey data
    public void updateAllSurveyData(String voterCardNumber, int support) {
        if (checkDataBase()) {
            db = SQLiteDatabase.openDatabase(myPath, null,
                    SQLiteDatabase.OPEN_READWRITE);
            try {
                ContentValues cv = new ContentValues();
                cv.put("sos", support);
                cv.put(IS_UPDATED, "true");
                long res = db.update(VOTER_TABLE_NAME, cv, VOTER_CARD_NUMBER + "='" + voterCardNumber + "'", null);
                Log.w(TAG, "Update result :" + res);
            } catch (SQLiteException e) {
                e.printStackTrace();
            }
            db.close();
        }
    }

    //Get voters data
    public Cursor getSearchVotersData(String _votername) {
        if (checkDataBase()) {
            db = SQLiteDatabase.openDatabase(myPath, null,
                    SQLiteDatabase.OPEN_READONLY);
            String QUERY = "select * from " + VOTER_TABLE_NAME + " where " + NAME_ENGLISH + " like '%" + _votername + "'" + " or " + VOTER_CARD_NUMBER + " like '%" + _votername + "'" + " or " + RELATED_ENGLISH + " like '%" + _votername + "'";
            //String QUERY = "select * from " + VOTER_TABLE_NAME + " limit 20 ";
            Log.w(TAG, QUERY);
            Cursor c = db.rawQuery(QUERY, null);
            if (c != null) {
                return c;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }


    //get voters fromparticular booth
    public Cursor getVoterFromParticularBooth(String boothName, String _votername) {
        if (checkDataBase()) {
            db = SQLiteDatabase.openDatabase(myPath, null,
                    SQLiteDatabase.OPEN_READONLY);
            String QUERY = "select * from " + VOTER_TABLE_NAME + " where (" + NAME_ENGLISH + " like '%" + _votername + "'" + " or " + VOTER_CARD_NUMBER + " like '%" + _votername + "'" + " or " + RELATED_ENGLISH + " like '%" + _votername + "' ) and boothName in(" + boothName + ")";
            //String QUERY = "select * from " + VOTER_TABLE_NAME + " limit 20 ";
            Log.w(TAG, QUERY);
            Cursor c = db.rawQuery(QUERY, null);
            if (c != null) {
                return c;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    //get count
    public Cursor getCount(String boothNames) {
        try {
            if (checkDataBase()) {
                db = SQLiteDatabase.openDatabase(myPath, null,
                        SQLiteDatabase.OPEN_READONLY);

                Log.w(TAG, "boooth names " + boothNames);
                Log.w(TAG, "query : " + "SELECT * FROM " + VOTER_TABLE_NAME + " WHERE boothName IN(" + boothNames + ")");
                Cursor c = db.rawQuery("SELECT count(*) FROM " + VOTER_TABLE_NAME + " WHERE boothName IN(" + boothNames + ")", null);
                // int count = c.getCount();
                return c;
            } else {
                return null;
            }
        } catch (SQLiteException e) {
            e.printStackTrace();
            return null;
        }
    }

    //get count
    public Cursor getCount() {
        if (checkDataBase()) {
            db = SQLiteDatabase.openDatabase(myPath, null,
                    SQLiteDatabase.OPEN_READONLY);
            Cursor c = db.rawQuery(" SELECT count(*) FROM " + VOTER_TABLE_NAME, null);
            return c;
        } else {
            return null;
        }
    }

    private static final String BOOTH_NO = "boothNo";
    private static final String BOOTH_NAME = "boothName";
    private static final String WARD_NO = "wardNo";
    private static final String WARD_NAME = "wardName";
    private static final String AC1 = "AC1";
    private static final String AC2 = "AC2";
    private static final String SOS = "sos";
    private static final String MOBILE = "mobile";
    private static final String NEW_VOTER = "newVoter";
    private static final String VNV = "VNV";

    //insert new voter data
    public void insertNewVoterData(String voterCardNumber, int sos) {
        if (checkDataBase()) {
            if (!check_already_exists(voterCardNumber)) {
                db = SQLiteDatabase.openDatabase(myPath, null,
                        SQLiteDatabase.OPEN_READWRITE);
                ContentValues cv = new ContentValues();
                cv.put(CONSTITUENCY_ID, "empty");
                cv.put(HIERARCHY_ID, "empty");
                cv.put(HIERARCHY_HASH, "empty");
                cv.put(VOTER_CARD_NUMBER, voterCardNumber);
                cv.put(NAME_ENGLISH, voterCardNumber);
                cv.put(NAME_REGIONAL, "empty");
                cv.put(GENDER, "empty");
                cv.put(AGE, "empty");
                cv.put(RELATED_ENGLISH, "empty");
                cv.put(RELATED_REGIONAL, "empty");
                cv.put(RELATIONSHIP, "empty");
                cv.put(ADDRESS_ENGLISH, "empty");
                cv.put(ADDRESS_REGIONAL, "empty");
                cv.put(HOUSE_NO, "empty");
                cv.put(SERIAL_NO, "0");
                cv.put(BOOTH_NO, "empty");
                cv.put(BOOTH_NAME, "empty");
                cv.put(WARD_NO, "empty");
                cv.put(WARD_NAME, "empty");
                cv.put(AC1, "empty");
                cv.put(AC2, "empty");
                cv.put(IS_UPDATED, "true");
                cv.put(SOS, sos);
                cv.put(MOBILE, 0);
                cv.put(NEW_VOTER, 1);
                cv.put(VNV, 0);
                long res = db.insert(VOTER_TABLE_NAME, null, cv);
                Log.w(TAG, "Insert result : " + res);
                db.close();
            } else {
                Log.w(TAG, "Data already exist");
            }
        }
    }

    private boolean check_already_exists(String voterCardNumber) {
        db = SQLiteDatabase.openDatabase(myPath, null,
                SQLiteDatabase.OPEN_READONLY);
        Cursor c = db.rawQuery("select * from " + VOTER_TABLE_NAME + " where " + VOTER_CARD_NUMBER + "='" + voterCardNumber + "'", null);
        int count = c.getCount();
        c.close();
        Log.w(TAG, " Count of exists data " + count);
        return count > 0;
    }

   /* @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }*/
}
