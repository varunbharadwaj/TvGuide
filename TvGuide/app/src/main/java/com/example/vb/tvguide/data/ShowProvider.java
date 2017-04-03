package com.example.vb.tvguide.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import com.example.vb.tvguide.data.ShowContract.ShowEntry;

import java.util.HashMap;

/**
 * Created by vb on 2/25/2017.
 */

public class ShowProvider extends ContentProvider {

    public static final String PROVIDER_NAME = "com.example.vb.tvguide.provider";
    public static final String URL = "content://" + PROVIDER_NAME + "/shows";
    public static final Uri CONTENT_URI = Uri.parse(URL);
    public static final int SHOW = 1;
    public static final int SHOW_ID = 2;
    static final UriMatcher uriMatcher;
    private static HashMap<String, String> MOVIE_PROJECTION_MAP;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(PROVIDER_NAME, "shows", SHOW);
        uriMatcher.addURI(PROVIDER_NAME, "shows/#", SHOW_ID);
    }

    private SQLiteDatabase db;

    @Override
    public boolean onCreate() {
        Context context = getContext();
        ShowDbHelper dbHelper = new ShowDbHelper(context);

        db = dbHelper.getWritableDatabase();
        return db != null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {

        final int match = uriMatcher.match(uri);
        long rowID;
        Uri _uri;

        switch (match) {
            case SHOW:
                rowID = db.insertOrThrow(ShowEntry.TABLE_NAME, "", values);
                _uri = ContentUris.withAppendedId(CONTENT_URI, rowID);
                getContext().getContentResolver().notifyChange(_uri, null);
                return _uri;
        }
        throw new SQLException("Failed to add a record into " + uri);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int count = 0;

        switch (uriMatcher.match(uri)) {
            case SHOW:
                count = db.delete(ShowEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        switch (uriMatcher.match(uri)) {
            case SHOW:
                qb.setTables(ShowEntry.TABLE_NAME);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, null);

        /**
         * register to watch a content URI for changes
         */
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int count = 0;

        switch (uriMatcher.match(uri)) {
            case SHOW:
                count = db.update(ShowEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case SHOW:
                return "vnd.android.cursor.dir/vnd.tvguide.shows";

            case SHOW_ID:
                return "vnd.android.cursor.item/vnd.tvguide.shows";

            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

}
