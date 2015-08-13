package com.nirenorie.spotifystreamer.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.nirenorie.spotifystreamer.data.DataContract.TrackEntry;

public class DbHelper extends SQLiteOpenHelper {
    static final String DATABASE_NAME = "spotify_streamer.db";
    private static final int DATABASE_VERSION = 2;

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_TRACK_TABLE = "CREATE TABLE " + TrackEntry.TABLE_NAME + " (" +
                TrackEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                TrackEntry.COLUMN_TRACK_NAME + " TEXT NOT NULL, " +
                TrackEntry.COLUMN_ARTIST_ID + " INTEGER NOT NULL, " +
                TrackEntry.COLUMN_ARTIST_NAME + " TEXT NOT NULL, " +
                TrackEntry.COLUMN_PREVIEW_URL + " TEXT NOT NULL, " +
                TrackEntry.COLUMN_ALBUM_NAME + " TEXT NOT NULL, " +
                TrackEntry.COLUMN_ALBUM_IMAGE_URL + " TEXT NOT NULL, " +
                String.format("UNIQUE(%s, %s) ON CONFLICT REPLACE);", TrackEntry.COLUMN_ARTIST_ID, TrackEntry.COLUMN_TRACK_NAME);

        sqLiteDatabase.execSQL(SQL_CREATE_TRACK_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TrackEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
