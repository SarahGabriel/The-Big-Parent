package com.thebigparent.sg.thebigparent.Dal;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.google.android.gms.maps.model.LatLng;
import com.thebigparent.sg.thebigparent.Classes.MapLocation;
import com.thebigparent.sg.thebigparent.DB.Constants_location;
import com.thebigparent.sg.thebigparent.DB.MyDbHelper;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Dal_location
 *
 * DAL of the location in app - all the access to DB for location is through this class
 */
public class Dal_location
{
    Dal_time dal_time;
    public Dal_location()
    {
        dal_time = new Dal_time();
    }

    public void addNewLocation(MapLocation location, Context context)       // add location
    {
        MyDbHelper dbHelper = new MyDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Constants_location.COLUMN_NAME_LOCATION_NAME, location.getLocationName());
        values.put(Constants_location.COLUMN_NAME_LONGITUDE, location.getLongitude());
        values.put(Constants_location.COLUMN_NAME_LATITUDE, location.getLatitude());
        values.put(Constants_location.COLUMN_NAME_RADIUS, location.getRadius());
        values.put(Constants_location.COLUMN_NAME_CONTACT, location.getContact());
        values.put(Constants_location.COLUMN_NAME_PHONE, location.getPhone());
        values.put(Constants_location.COLUMN_NAME_NUMBER_OF_CONTACTS, location.getNumOfContacts());

        db.insertOrThrow(Constants_location.TABLE_NAME, null, values);
        db.close();
    }

    public List<LatLng> getAllLocationsMarker(Context context)      //    Return list of latLng of all location
    {
        List<LatLng> allLocations = new ArrayList<>();
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
        db1.close();
        return allLocations;
    }


    public List<MapLocation> getAllLocations(Context context)   //    return list of all the locations from the DB
    {
        List<MapLocation> allLocations = new ArrayList<>();
        MyDbHelper dbHelper = new MyDbHelper(context);
        SQLiteDatabase db1 = dbHelper.getReadableDatabase();
        String[] cols={Constants_location.COLUMN_NAME_LATITUDE, Constants_location.COLUMN_NAME_LONGITUDE, Constants_location.COLUMN_NAME_LOCATION_NAME, Constants_location.COLUMN_NAME_CONTACT, Constants_location.COLUMN_NAME_RADIUS , Constants_location.COLUMN_NAME_PHONE, Constants_location.COLUMN_NAME_NUMBER_OF_CONTACTS};
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
            String phone = c.getString((5)).trim();
            int numOfContacts = Integer.parseInt(c.getString((6)).trim());

            MapLocation location = new MapLocation(locationName, lng, lat, radius, contact, phone, numOfContacts);
            allLocations.add(location);
        }
        db1.close();
        return allLocations;
    }

    public MapLocation getLocation(String latitude, String longitude, Context context)      //    Get location details using lat-lng
    {
        MapLocation location;
        MyDbHelper dbHelper = new MyDbHelper(context);
        SQLiteDatabase db1 = dbHelper.getReadableDatabase();

        /* SELECT COLUMN_NAME_ENTRY_ID, COLUMN_NAME_PASSWORD FROM TABLE_NAME
         WHERE COLUMN_NAME_ENTRY_ID = username AND COLUMN_NAME_PASSWORD = password */
        String[] cols={Constants_location.COLUMN_NAME_LATITUDE, Constants_location.COLUMN_NAME_LONGITUDE, Constants_location.COLUMN_NAME_LOCATION_NAME, Constants_location.COLUMN_NAME_CONTACT, Constants_location.COLUMN_NAME_RADIUS, Constants_location.COLUMN_NAME_PHONE, Constants_location.COLUMN_NAME_NUMBER_OF_CONTACTS};
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
        String phone = c.getString((5)).trim();
        int numOfContacts = Integer.parseInt(c.getString((6)).trim());

        location = new MapLocation(locationName, lng, lat, radius, contact , phone, numOfContacts);
        db1.close();
        return location;
    }


    public List<String> getAllMarkers(Context context)      //    Return all Markers in DB
    {
        List<String> allMarkers = new ArrayList<>();
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
        db.close();
        return allMarkers;
    }

    public void updateRadius(String latitude, String longitude, String newRadius, Context context)      // update radius with lat-lng of the radius and its size
    {
        MyDbHelper dbHelper = new MyDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Constants_location.COLUMN_NAME_RADIUS, newRadius);

       db.update(Constants_location.TABLE_NAME, values,
                        Constants_location.COLUMN_NAME_LATITUDE + " = ? AND " +
                        Constants_location.COLUMN_NAME_LONGITUDE + " =?",
                new String[]{latitude, longitude});

        db.close();
    }


    public void deleteLocation(String latitude, String longitude, Context context) throws SQLException      //    Delete location using lat-lng
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
