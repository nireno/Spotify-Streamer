/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.nirenorie.spotifystreamer.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Defines table and column names for the weather database.
 */
public class DataContract {
    public static final String CONTENT_AUTHORITY = "com.nirenorie.spotifystreamer";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    //    public static final String PATH_ARTIST = "artist";
    public static final String PATH_TRACK = "track";
    public static final String PATH_ARTIST = "artist";

    /* Inner class that defines the table contents of the location table */
    public static final class ArtistEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_ARTIST).build();
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ARTIST;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ARTIST;

        public static final String TABLE_NAME = "artist";
        public static final String COLUMN_ARTIST_ID = "id";
        public static final String COLUMN_ARTIST_NAME = "name";
        public static final String COLUMN_ARTIST_IMAGE_URL = "image_url";

        public static Uri buildArtistUriWithId(String id) {
            return ArtistEntry.CONTENT_URI.buildUpon().appendPath(id).build();
        }

        public static String getArtistIdFromUri(Uri uri) {
            return uri.getLastPathSegment();
        }
    }

    public static final class TrackEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_TRACK).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TRACK;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TRACK;

        public static final String TABLE_NAME = "track";

        // Column with the foreign key into the location table.
        public static final String COLUMN_TRACK_NAME = "name";
        public static final String COLUMN_ARTIST_ID = "artist_id"; /* Artist who has this as a top track */
        public static final String COLUMN_ARTIST_NAME = "artist_name"; /* Main artist for the track, whose id may differ from our artist_id */
        public static final String COLUMN_PREVIEW_URL = "preview_url";
        public static final String COLUMN_ALBUM_NAME = "album_name";
        public static final String COLUMN_ALBUM_IMAGE_URL = "album_image_url";

        public static Uri buildTrackUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildTrackUriWithArtistId(String id) {
            return TrackEntry.CONTENT_URI.buildUpon().appendPath(id).build();
        }

        public static Long getArtistIdFromUri(Uri uri) {
            return ContentUris.parseId(uri);
        }

    }
}
