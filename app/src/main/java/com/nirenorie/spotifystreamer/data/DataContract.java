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

//    /* Inner class that defines the table contents of the location table */
//    public static final class ArtistEntry implements BaseColumns {
//        public static final Uri CONTENT_URI =
//                BASE_CONTENT_URI.buildUpon().appendPath(PATH_ARTIST).build();
//        public static final String CONTENT_TYPE =
//                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ARTIST;
//        public static final String CONTENT_ITEM_TYPE =
//                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ARTIST;
//
//        public static final String TABLE_NAME = "artist";
//        public static final String COLUMN_ARTIST_NAME = "name";
//        public static final String COLUMN_ARTIST_IMAGE_URL = "image_url";
//        public static final String COLUMN_ARTIST_ID = "id";
//
//        public static Uri buildLocationUri(long id) {
//            return ContentUris.withAppendedId(CONTENT_URI, id);
//        }
//    }

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
        public static final String COLUMN_TRACK_ARTIST = "artist";
        public static final String COLUMN_TRACK_PREVIEW_URL = "preview_url";
        public static final String COLUMN_TRACK_ALBUM_NAME = "album_name";
        public static final String COLUMN_TRACK_ALBUM_IMAGE_URL = "album_image_url";

        public static Uri buildTrackUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

//        /*
//            Student: This is the buildWeatherLocation function you filled in.
//         */
//        public static Uri buildWeatherLocation(String locationSetting) {
//            return CONTENT_URI.buildUpon().appendPath(locationSetting).build();
//        }
//
//        public static Uri buildWeatherLocationWithStartDate(
//                String locationSetting, long startDate) {
//            long normalizedDate = normalizeDate(startDate);
//            return CONTENT_URI.buildUpon().appendPath(locationSetting)
//                    .appendQueryParameter(COLUMN_DATE, Long.toString(normalizedDate)).build();
//        }
//
//        public static Uri buildWeatherLocationWithDate(String locationSetting, long date) {
//            return CONTENT_URI.buildUpon().appendPath(locationSetting)
//                    .appendPath(Long.toString(normalizeDate(date))).build();
//        }
//
//        public static String getLocationSettingFromUri(Uri uri) {
//            return uri.getPathSegments().get(1);
//        }
//
//        public static long getDateFromUri(Uri uri) {
//            return Long.parseLong(uri.getPathSegments().get(2));
//        }
//
//        public static long getStartDateFromUri(Uri uri) {
//            String dateString = uri.getQueryParameter(COLUMN_DATE);
//            if (null != dateString && dateString.length() > 0)
//                return Long.parseLong(dateString);
//            else
//                return 0;
//        }
    }
}
