package com.example.vb.tvguide.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.vb.tvguide.data.ShowContract.ShowEntry;

/**
 * Created by vb on 2/25/2017.
 */

public class ShowDbHelper extends SQLiteOpenHelper {

    static final String SHOW_DATABASE_NAME = "tvshow.db";
    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 3;

    public ShowDbHelper(Context context) {
        super(context, SHOW_DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_SHOW_TABLE = "CREATE TABLE " + ShowEntry.TABLE_NAME +
                "(" + ShowEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ShowEntry.ID + " INEGER, " +
                ShowEntry.SEASON + " TEXT NOT NULL, " +
                ShowEntry.EPISODE + " TEXT NOT NULL, " +
                ShowEntry.NAME + " TEXT NOT NULL, " +
                ShowEntry.LANGUAGE + " TEXT NOT NULL, " +
                ShowEntry.STATUS + " TEXT NOT NULL, " +
                ShowEntry.RUNTIME + " TEXT NOT NULL, " +
                ShowEntry.TYPE + " TEXT NOT NULL, " +
                ShowEntry.SUMMARY + " TEXT NOT NULL, " +
                ShowEntry.CHANNEL + " TEXT NOT NULL, " +
                ShowEntry.COUNTRY + " TEXT NOT NULL, " +
                ShowEntry.URL + " TEXT NOT NULL, " +
                ShowEntry.IMAGE + " TEXT NOT NULL, " +
                ShowEntry.DAYS + " TEXT NOT NULL, " +
                ShowEntry.TIME + " TEXT NOT NULL); ";

        sqLiteDatabase.execSQL(SQL_CREATE_SHOW_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ShowEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

}
