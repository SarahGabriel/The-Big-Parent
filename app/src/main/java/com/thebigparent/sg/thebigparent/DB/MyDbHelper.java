package com.thebigparent.sg.thebigparent.DB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Guy on 05/02/15.
 */
public class MyDbHelper extends SQLiteOpenHelper {            //Helper to create db

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "dataBaseFile.db";


    public MyDbHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL("CREATE TABLE "+ Constants_location.TABLE_NAME+ "("+
                Constants_location._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                Constants_location.COLUMN_NAME_LOCATION_NAME + " TEXT_TYPE, " +
                Constants_location.COLUMN_NAME_LONGITUDE + " TEXT_TYPE, " +
                Constants_location.COLUMN_NAME_LATITUDE + " TEXT_TYPE, " +
                Constants_location.COLUMN_NAME_RADIUS + " TEXT_TYPE, " +
                Constants_location.COLUMN_NAME_CONTACT + " TEXT_TYPE);" );

        db.execSQL("CREATE TABLE "+ Constants_time.TABLE_NAME+ "("+
                Constants_time._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                Constants_time.COLUMN_NAME_DAY + " INTEGER, " +
                Constants_time.COLUMN_NAME_HOUR_START + " TEXT_TYPE, " +
                Constants_time.COLUMN_NAME_HOUR_END + " TEXT_TYPE, " +
                Constants_time.COLUMN_NAME_LONGITUDE + " TEXT_TYPE, " +
                Constants_time.COLUMN_NAME_LATITUDE + " TEXT_TYPE, " +
                Constants_time.COLUMN_NAME_NO_REPEAT + " INTEGER, " +
                Constants_time.COLUMN_NAME_SWITCHER + " INTEGER);" );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("DROP TABLE IF EXISTS " + Constants_location.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + Constants_time.TABLE_NAME);

    }
}
