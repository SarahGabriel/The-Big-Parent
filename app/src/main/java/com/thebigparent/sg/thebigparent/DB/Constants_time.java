package com.thebigparent.sg.thebigparent.DB;

import android.provider.BaseColumns;

/**
 * Created by Sarah on 11-Feb-15.
 */
public class Constants_time implements BaseColumns
{                        // Constants for "bestResults" table

    public static final String TABLE_NAME = "Time" ;
    public static final String COLUMN_NAME_DAY = "Day";
    public static final String COLUMN_NAME_HOUR_START = "HourStart";
    public static final String COLUMN_NAME_HOUR_END = "HourEnd";
    public static final String COLUMN_NAME_LATITUDE = "Latitude";
    public static final String COLUMN_NAME_LONGITUDE = "Longitude";
    public static final String COLUMN_NAME_NO_REPEAT = "NoRepeat";
}
