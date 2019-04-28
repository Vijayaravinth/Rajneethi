package com.software.cb.rajneethi.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

import com.software.cb.rajneethi.models.VoterAttribute;

import java.io.File;

/**
 * Created by w7 on 10/10/2017.
 */

public class ExportDatabase extends SQLiteOpenHelper {

    private Context context;
    private SQLiteDatabase db;

    private static final int DB_VERSION = 1;
    private static final String DATABASE_NAME = "exportfile";

    private static final String FILE_DIR = Environment.DIRECTORY_PICTURES + "/Folder";


    //VoterAttribute table
    private static final String ID = "id";
    private static final String VOTER_ATTRIBUTE_TABLE = "voterAttribute";
    private static final String ATTRIBUTE_NAME = "attributeName";
    private static final String VOTER_CARD_NUMBER = "voterCardNumber";
    private static final String ATTRIBUTE_VALUE = "attributeValue";
    private static final String SYNCED = "synced";
    private static final String DATE = "date";
    private static final String SURVEYID = "surveyId";


    public ExportDatabase(Context context, String DBNAME) {
        super(context, Environment.getExternalStorageDirectory()
                + File.separator + FILE_DIR
                + File.separator + DBNAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_TABLE_VOTER_ATTRIBUTE = "CREATE TABLE " + VOTER_ATTRIBUTE_TABLE + "(" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + VOTER_CARD_NUMBER + " TEXT," + ATTRIBUTE_NAME + " TEXT," + ATTRIBUTE_VALUE + " TEXT," + SYNCED + " TEXT," + DATE + " DATE," + SURVEYID + "  TEXT" + ")";
        db.execSQL(CREATE_TABLE_VOTER_ATTRIBUTE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + VOTER_ATTRIBUTE_TABLE);
    }

    //insert voter attribute
    public void insert_voter_attribute(VoterAttribute attribute) {

        try {
            db = this.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put(VOTER_CARD_NUMBER, attribute.getVoterCardNumber());
            cv.put(ATTRIBUTE_NAME, attribute.getAttributeName());
            cv.put(ATTRIBUTE_VALUE, attribute.getAttributeValue());
            cv.put(SYNCED, attribute.isSynced());
            cv.put(DATE, attribute.getDate());
            cv.put(SURVEYID, attribute.getSurveyId());
            db.insert(VOTER_ATTRIBUTE_TABLE, null, cv);

        } catch (SQLiteException e) {
            e.printStackTrace();
        }
    }

}
