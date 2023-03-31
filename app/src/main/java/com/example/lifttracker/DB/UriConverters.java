package com.example.lifttracker.DB;

import android.net.Uri;

import androidx.room.TypeConverter;

public class UriConverters {

    @TypeConverter
    public static Uri fromString(String value) {
        if(value != null)
            return Uri.parse(value);
        return Uri.parse("no_path");
    }

    @TypeConverter
    public static String fromUri(Uri uri) {
        return uri == null ? null : uri.toString();
    }
}
