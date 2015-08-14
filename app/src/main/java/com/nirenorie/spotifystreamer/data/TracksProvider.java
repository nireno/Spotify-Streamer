package com.nirenorie.spotifystreamer.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class TracksProvider extends ContentProvider {
    static final int TRACK = 100;
    static final int TRACK_URI_WITH_ID = 101;
    static final int ARTIST_URI_WITH_ID = 200;
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private DbHelper mOpenHelper;

    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = DataContract.CONTENT_AUTHORITY;
        matcher.addURI(authority, DataContract.PATH_TRACK, TRACK);
        matcher.addURI(authority, DataContract.PATH_ARTIST + "/*", ARTIST_URI_WITH_ID);
        matcher.addURI(authority, DataContract.PATH_TRACK + "/#", TRACK_URI_WITH_ID);
        return matcher;
    }

    private Cursor getTrackById(Uri uri, String[] projection){
        long id = ContentUris.parseId(uri);
        String[] selectionArgs = new String[]{Long.toString(id)};
        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        return db.query(DataContract.TrackEntry.TABLE_NAME,
                projection, DataContract.TrackEntry._ID + " = ?", selectionArgs, null, null, null);
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new DbHelper(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {

        final int match = sUriMatcher.match(uri);

        switch (match) {
            case ARTIST_URI_WITH_ID:
                return DataContract.TrackEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        Cursor retCursor;
        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        switch (sUriMatcher.match(uri)) {
            case TRACK_URI_WITH_ID:
                retCursor = getTrackById(uri, projection);
                break;
            case TRACK:
                return db.query(DataContract.TrackEntry.TABLE_NAME,
                        projection, selection, selectionArgs, null, null, sortOrder);
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case TRACK: {
                long _id = db.insert(DataContract.TrackEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = DataContract.TrackEntry.buildTrackUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(
            Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
