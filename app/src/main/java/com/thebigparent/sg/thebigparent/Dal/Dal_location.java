package com.thebigparent.sg.thebigparent.Dal;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.thebigparent.sg.thebigparent.Classes.MapLocation;
import com.thebigparent.sg.thebigparent.DB.Constants_location;
import com.thebigparent.sg.thebigparent.DB.MyDbHelper;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Guy on 05/02/15.
 */
public class Dal_location
{
    Dal_time dal_time;
    public Dal_location()
    {
        dal_time = new Dal_time();
    }

//    Add Location
    public void addNewLocation(MapLocation location, Context context)
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

//    Return list of latLng of all location
    public List<LatLng> getAllLocationsMarker(Context context)
    {
        List<LatLng> allLocations = new ArrayList<LatLng>();
        MyDbHelper dbHelper = new MyDbHelper(context);
        SQLiteDatabase db1 = dbHelper.getReadableDatabase();
        String[] cols={Constants_location.COLUMN_NAME_LONGITUDE, Constants_location.COLUMN_NAME_LATITUDE};
        Cursor c = db1.query(
                Constants_location.TABLE_NAME,    // The table to query
                cols,                    // The columns to return
                null,                    // The columns for the WHERE clause
                null,                    // The values for the WHERE clause
                null,                    // don't group the rows
                null,                    // don't filter by row groups
                null                     // The sort order
        );
        while (c.moveToNext())
        {
            String lng = c.getString((0)).trim();
            String lat = c.getString((1)).trim();
            double latitude = Double.parseDouble(lat);
            double longitude = Double.parseDouble(lng);
            LatLng latLng = new LatLng(latitude, longitude);
            allLocations.add(latLng);
        }

        Log.w("allLocations", allLocations.toString());

        db1.close();

        return allLocations;
    }

//    return list of all the locations from the DB
    public List<MapLocation> getAllLocations(Context context)
    {
        List<MapLocation> allLocations = new ArrayList<MapLocation>();
        MyDbHelper dbHelper = new MyDbHelper(context);
        SQLiteDatabase db1 = dbHelper.getReadableDatabase();
        String[] cols={Constants_location.COLUMN_NAME_LATITUDE, Constants_location.COLUMN_NAME_LONGITUDE, Constants_location.COLUMN_NAME_LOCATION_NAME, Constants_location.COLUMN_NAME_CONTACT, Constants_location.COLUMN_NAME_RADIUS};
        Cursor c = db1.query(
                Constants_location.TABLE_NAME,    // The table to query
                cols,                    // The columns to return
                null,                    // The columns for the WHERE clause
                null,                    // The values for the WHERE clause
                null,                    // don't group the rows
                null,                    // don't filter by row groups
                null                     // The sort order
        );
        while (c.moveToNext())
        {
            String lat = c.getString((0)).trim();
            String lng = c.getString((1)).trim();
            String locationName = c.getString((2)).trim();
            String contact = c.getString((3)).trim();
            String radius = c.getString((4)).trim();

            double latitude = Double.parseDouble(lat);
            double longitude = Double.parseDouble(lng);

            MapLocation location = new MapLocation(locationName, lng, lat, radius, contact);
            allLocations.add(location);
        }

        Log.w("allLocations", allLocations.toString());

        db1.close();

        return allLocations;
    }

//    Get location details using lat-lng
    public MapLocation getLocation(String latitude, String longitude, Context context)
    {
        MapLocation location;
        MyDbHelper dbHelper = new MyDbHelper(context);
        SQLiteDatabase db1 = dbHelper.getReadableDatabase();

        // SELECT COLUMN_NAME_ENTRY_ID, COLUMN_NAME_PASSWORD FROM TABLE_NAME
        // WHERE COLUMN_NAME_ENTRY_ID = username AND COLUMN_NAME_PASSWORD = password
        String[] cols={Constants_location.COLUMN_NAME_LATITUDE, Constants_location.COLUMN_NAME_LONGITUDE, Constants_location.COLUMN_NAME_LOCATION_NAME, Constants_location.COLUMN_NAME_CONTACT, Constants_location.COLUMN_NAME_RADIUS};
        Cursor c = db1.query(
                Constants_location.TABLE_NAME,
                cols,
                Constants_location.COLUMN_NAME_LATITUDE + "=? AND " + Constants_location.COLUMN_NAME_LONGITUDE + "=?",
                new String[]{latitude, longitude},
                null,null,null);

        if (!(c.moveToFirst()) || c.getCount() == 0)
        {
            //cursor is empty
            db1.close();
            return null;
        }
        String lat = c.getString((0)).trim();
        String lng = c.getString((1)).trim();
        String locationName = c.getString((2)).trim();
        String contact = c.getString((3)).trim();
        String radius = c.getString((4)).trim();

        location = new MapLocation(locationName, lng, lat, radius, contact);
        db1.close();
        return location;
    }

//    Return all Markers in DB
    public List<String> getAllMarkers(Context context)
    {
        List<String> allMarkers = new ArrayList<String>();
        MyDbHelper dbHelper = new MyDbHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

//        The columns to return
        String[] cols={Constants_location._ID,
                Constants_location.COLUMN_NAME_LATITUDE,
                Constants_location.COLUMN_NAME_LONGITUDE,
                Constants_location.COLUMN_NAME_LOCATION_NAME,
                Constants_location.COLUMN_NAME_CONTACT};


//      SQL query
        Cursor c = db.query(
                Constants_location.TABLE_NAME,    // The table to query
                cols,                    // The columns to return
                null,
                null,
                null,                    // don't group the rows
                null,                    // don't filter by row groups
                Constants_location.COLUMN_NAME_LOCATION_NAME + " ASC"    // The sort order
        );

        while (c.moveToNext())
        {
            String id_marker = c.getString(0).trim();
            String latitude = c.getString(1).trim();
            String longitude = c.getString(2).trim();
            String location = c.getString(3).trim();
            String contact = c.getString(4).trim();
            String marker = id_marker + "," + latitude + "," + longitude + "," + location + "," + contact;
            allMarkers.add(marker);
        }

        Log.w("allMarkers", allMarkers.toString());

        db.close();

        return allMarkers;
    }

//    Delete location using lat-lng
    public void deleteLocation(String latitude, String longitude, Context context) throws SQLException
    {
        MyDbHelper dbHelper = new MyDbHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

//        Delete all tracking times of location if there is
        if(dal_time.getAllTimeByLatLng(latitude, longitude, context).size() != 0)
        {
            dal_time.deleteAllTimes(latitude, longitude, context);
        }
//        Delete location
        db.delete(Constants_location.TABLE_NAME, Constants_location.COLUMN_NAME_LATITUDE + " = ? AND " + Constants_location.COLUMN_NAME_LONGITUDE + " = ?",
                new String[] { latitude, longitude});
        db.close();
    }
}
