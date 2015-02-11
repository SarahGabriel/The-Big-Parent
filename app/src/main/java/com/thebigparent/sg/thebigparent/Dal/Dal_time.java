package com.thebigparent.sg.thebigparent.Dal;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.thebigparent.sg.thebigparent.Classes.Time;
import com.thebigparent.sg.thebigparent.DB.Constants_time;
import com.thebigparent.sg.thebigparent.DB.MyDbHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Sarah on 11-Feb-15.
 */
public class Dal_time
{
    public Dal_time()
    {
    }

    public void addNewTime(Time time, Context context)
    {
        MyDbHelper dbHelper = new MyDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Constants_time.COLUMN_NAME_DAY, time.getDay());
        values.put(Constants_time.COLUMN_NAME_HOUR_START, time.getHourStart());
        values.put(Constants_time.COLUMN_NAME_HOUR_END, time.getHourEnd());
        values.put(Constants_time.COLUMN_NAME_LATITUDE, time.getLatitude());
        values.put(Constants_time.COLUMN_NAME_LONGITUDE, time.getLongitude());
        values.put(Constants_time.COLUMN_NAME_NO_REPEAT, time.isNoRepeat());


        db.insertOrThrow(Constants_time.TABLE_NAME, null, values);
        db.close();
    }

    public List<Time> getAllTimeByLatLng(String latitude, String longitude, Context context)
    {
        List<Time> allTimes = new ArrayList<Time>();
        MyDbHelper dbHelper = new MyDbHelper(context);
        SQLiteDatabase db1 = dbHelper.getReadableDatabase();

        String[] cols={Constants_time.COLUMN_NAME_DAY,
                       Constants_time.COLUMN_NAME_HOUR_START,
                       Constants_time.COLUMN_NAME_HOUR_END,
                       Constants_time.COLUMN_NAME_LATITUDE,
                       Constants_time.COLUMN_NAME_LONGITUDE,
                       Constants_time.COLUMN_NAME_NO_REPEAT};

        Cursor c = db1.query(
                Constants_time.TABLE_NAME,    // The table to query
                cols,                    // The columns to return
                Constants_time.COLUMN_NAME_LATITUDE + "=? AND " + Constants_time.COLUMN_NAME_LONGITUDE + "=?",
                new String[]{latitude, longitude},
                null,                    // don't group the rows
                null,                    // don't filter by row groups
                null                     // The sort order
        );

        while (c.moveToNext())
        {
            int day = Integer.parseInt(c.getString((0)).trim());
            String hour_start = c.getString(1).trim();
            String hour_end = c.getString(2).trim();
            int no_repeat = Integer.parseInt(c.getString(5).trim());
            Time time = new Time(day, hour_start, hour_end, latitude, longitude, no_repeat);
            allTimes.add(time);
        }

        Log.w("allTime", allTimes.toString());

        db1.close();

        return allTimes;
    }

    public List<String> getAllHoursByLatLng(String latitude, String longitude, Context context)
    {
        List<String> allHours = new ArrayList<String>();
        MyDbHelper dbHelper = new MyDbHelper(context);
        SQLiteDatabase db1 = dbHelper.getReadableDatabase();

        String[] cols={Constants_time.COLUMN_NAME_HOUR_START,
                Constants_time.COLUMN_NAME_HOUR_END};


        Cursor c = db1.query(
                Constants_time.TABLE_NAME,    // The table to query
                cols,                    // The columns to return
                Constants_time.COLUMN_NAME_LATITUDE + "=? AND " + Constants_time.COLUMN_NAME_LONGITUDE + "=?",
                new String[]{latitude, longitude},
                null,                    // don't group the rows
                null,                    // don't filter by row groups
                null                     // The sort order
        );

        while (c.moveToNext())
        {

            String hour_start = c.getString(0).trim();
            String hour_end = c.getString(1).trim();
            String hour = hour_start + " - " + hour_end;
            allHours.add(hour);
        }

        Log.w("allHours", allHours.toString());

        db1.close();

        return allHours;
    }

    public List<String> getAllHoursAndDayByLatLng(String latitude, String longitude, Context context)
    {
        List<String> allHoursAndDays = new ArrayList<String>();
        MyDbHelper dbHelper = new MyDbHelper(context);
        SQLiteDatabase db1 = dbHelper.getReadableDatabase();

        String[] cols={Constants_time.COLUMN_NAME_DAY,
                Constants_time.COLUMN_NAME_HOUR_START,
                Constants_time.COLUMN_NAME_HOUR_END};
        String[] sort = {Constants_time.COLUMN_NAME_DAY, Constants_time.COLUMN_NAME_HOUR_START};


        Cursor c = db1.query(
                Constants_time.TABLE_NAME,    // The table to query
                cols,                    // The columns to return
                Constants_time.COLUMN_NAME_LATITUDE + "=? AND " + Constants_time.COLUMN_NAME_LONGITUDE + "=?",
                new String[]{latitude, longitude},
                null,                    // don't group the rows
                null,                    // don't filter by row groups
                Constants_time.COLUMN_NAME_DAY + " ASC, " + Constants_time.COLUMN_NAME_HOUR_START + " ASC"     // The sort order
        );

        while (c.moveToNext())
        {
            String day = c.getString(0).trim();
            String hour_start = c.getString(1).trim();
            String hour_end = c.getString(2).trim();
            String time = day + "," + hour_start + " - " + hour_end;
            allHoursAndDays.add(time);
        }

        Log.w("allHoursAndDays", allHoursAndDays.toString());

        db1.close();

        return allHoursAndDays;
    }
    public void deleteTime(String latitude, String longitude, String hours, String day, Context context)
    {
        MyDbHelper dbHelper = new MyDbHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String hour_start, hour_end;
        String[] parser = hours.split("-");
        hour_start = parser[0].trim();
        hour_end = parser[1].trim();
        Log.i("HOUR_BEFORE", hour_start);
        Log.i("HOUR_AFTER", hour_end);
        int day_int = convertStringDayToInt(day);
        Log.i("DAY_BEFORE", day);
        Log.i("DAY_AFTER", String.valueOf(day_int));
        db.delete(Constants_time.TABLE_NAME, Constants_time.COLUMN_NAME_LATITUDE + " = ? AND " +
                                             Constants_time.COLUMN_NAME_LONGITUDE + " = ? AND " +
                                             Constants_time.COLUMN_NAME_DAY + " = ? AND " +
                                             Constants_time.COLUMN_NAME_HOUR_START + " = ? AND " +
                                             Constants_time.COLUMN_NAME_HOUR_END + " = ?",
                new String[] { latitude, longitude, String.valueOf(day_int), hour_start, hour_end});
        db.close();
    }

    public int convertStringDayToInt(String day)
    {
        Calendar date;
        int day_int = 0;
        switch (day)
        {
            case "Sunday":
                day_int = 1;break;
            case  "Monday":
                day_int = 2;break;
            case "Tuesday":
                day_int = 3;break;
            case "Wednesday":
                day_int = 4;break;
            case "Thursday":
                day_int = 5;break;
            case "Friday":
                day_int = 6;break;
            case "Saturday":
                day_int = 7;break;
        }
        return day_int;



    }
}
