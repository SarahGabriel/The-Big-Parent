package com.thebigparent.sg.thebigparent.Dal;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.thebigparent.sg.thebigparent.Classes.Location;
import com.thebigparent.sg.thebigparent.DB.Constants_location;
import com.thebigparent.sg.thebigparent.DB.MyDbHelper;

/**
 * Created by Guy on 05/02/15.
 */
public class Dal_location
{
    public Dal_location()
    {
    }

    public void addNewLocation(Location location, Context context)
    {
        MyDbHelper dbHelper = new MyDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Constants_location.COLUMN_NAME_LOCATION_NAME, location.getLocationName());
        values.put(Constants_location.COLUMN_NAME_LONGITUDE, location.getLongitude());
        values.put(Constants_location.COLUMN_NAME_LATITUDE, location.getLatitude());
        values.put(Constants_location.COLUMN_NAME_RADIUS, location.getRadius());
        values.put(Constants_location.COLUMN_NAME_CONTACT, location.getContact());

        db.insertOrThrow(Constants_location.TABLE_NAME, null, values);
        db.close();
    }
}
