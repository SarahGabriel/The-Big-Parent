package com.thebigparent.sg.thebigparent.Dal;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.thebigparent.sg.thebigparent.Classes.Time;
import com.thebigparent.sg.thebigparent.DB.Constants_time;
import com.thebigparent.sg.thebigparent.DB.MyDbHelper;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Dal_time
 *
 * DAL of the time in app - all the access to DB for tracking time is through this class
 */
public class Dal_time
{
    public static final int SWITCH_ON = 1;
    public static final int SWITCH_OFF = 0;

    public boolean addNewTime(Time time, Context context) throws ParseException     // add new tracking time
    {
        if(isTimeExists(time, context))     // if time already exists
        {
            showDialogMessage("Error in adding tracking time", time.getDay(), context);
            return false;
        }
        MyDbHelper dbHelper = new MyDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Constants_time.COLUMN_NAME_DAY, time.getDay());
        values.put(Constants_time.COLUMN_NAME_HOUR_START, time.getHourStart());
        values.put(Constants_time.COLUMN_NAME_HOUR_END, time.getHourEnd());
        values.put(Constants_time.COLUMN_NAME_LATITUDE, time.getLatitude());
        values.put(Constants_time.COLUMN_NAME_LONGITUDE, time.getLongitude());
        values.put(Constants_time.COLUMN_NAME_NO_REPEAT, time.isNoRepeat());
        values.put(Constants_time.COLUMN_NAME_SWITCHER, time.getIsSwitchOn());
        values.put(Constants_time.COLUMN_NAME_DATE, time.getDate());


        db.insertOrThrow(Constants_time.TABLE_NAME, null, values);


        db.close();
        return true;
    }

    private void showDialogMessage(String title, int day, Context context)
    {
        String stringDay = convertIntDayToString(day);
        String message = "You already have a tracking time for those hours on " + stringDay;

        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        // continue with delete
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private boolean isTimeExists(Time time_toCheck, Context context) throws ParseException
    {
        Log.w("isTimeExists", "IN IT");
        List<Time> timesByDay = getTimesByDay(time_toCheck.getDay(), context);
        if(timesByDay!=null)
        {

            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

            Date startHour_toCheck = sdf.parse(time_toCheck.getHourStart());
            Date endHour_toCheck = sdf.parse(time_toCheck.getHourEnd());


            for (Time timeByDay : timesByDay)
            {
                Date startHour_checkWith = sdf.parse(timeByDay.getHourStart());
                Date endHour_checkWith = sdf.parse(timeByDay.getHourEnd());

                Log.i("start_toCheck", startHour_toCheck.toString());
                Log.i("start_checkWith", startHour_checkWith.toString());
                Log.i("end_toCheck", endHour_toCheck.toString());
                Log.i("end_checkWith", endHour_checkWith.toString());

                if(startHour_toCheck.after(startHour_checkWith) && startHour_toCheck.before(endHour_checkWith))
                {
                    Log.i("if 1", startHour_toCheck + " after? " + startHour_checkWith + " && " + startHour_toCheck + " before? " + endHour_checkWith);
                    return true;
                }
                else if(endHour_toCheck.after(startHour_checkWith) && endHour_toCheck.before(endHour_checkWith))
                {
                    Log.i("if 2", endHour_toCheck + " after? " + startHour_checkWith + " && " + endHour_toCheck + " before? " + endHour_checkWith);
                    return true;
                }
            }
        }
        return false;
    }

    public List<Time> getTimesByDay(int day, Context context)       // get all times by day
    {
        List<Time> timesByDay = new ArrayList<>();

        MyDbHelper dbHelper = new MyDbHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] cols={Constants_time.COLUMN_NAME_DAY,
                Constants_time.COLUMN_NAME_HOUR_START,
                Constants_time.COLUMN_NAME_HOUR_END,
                Constants_time.COLUMN_NAME_LATITUDE,
                Constants_time.COLUMN_NAME_LONGITUDE,
                Constants_time.COLUMN_NAME_DATE,
                Constants_time.COLUMN_NAME_NO_REPEAT,
                Constants_time.COLUMN_NAME_SWITCHER};

        Cursor c = db.query(
                Constants_time.TABLE_NAME,    // The table to query
                cols,                    // The columns to return
                Constants_time.COLUMN_NAME_DAY + "=?",
                new String[]{String.valueOf(day)},
                null,                    // don't group the rows
                null,                    // don't filter by row groups
                null                     // The sort order
        );

        if (c.getCount() == 0)
        {
            //cursor is empty
            db.close();
            return null;
        }
        while (c.moveToNext())
        {
            Time timeByDay = new Time(day, c.getString(1), c.getString(2), c.getString(3), c.getString(4), c.getString(5), Integer.parseInt(c.getString(6).trim()), Integer.parseInt(c.getString(7).trim()));
            timesByDay.add(timeByDay);
        }
        db.close();
        return timesByDay;
    }

    public List<Time> getAllTimeByLatLng(String latitude, String longitude, Context context)        // get all time by lat-lng
    {
        List<Time> allTimes = new ArrayList<>();
        MyDbHelper dbHelper = new MyDbHelper(context);
        SQLiteDatabase db1 = dbHelper.getReadableDatabase();

        String[] cols={Constants_time.COLUMN_NAME_DAY,
                       Constants_time.COLUMN_NAME_HOUR_START,
                       Constants_time.COLUMN_NAME_HOUR_END,
                       Constants_time.COLUMN_NAME_LATITUDE,
                       Constants_time.COLUMN_NAME_LONGITUDE,
                       Constants_time.COLUMN_NAME_DATE,
                       Constants_time.COLUMN_NAME_NO_REPEAT,
                       Constants_time.COLUMN_NAME_SWITCHER};

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
            String date = c.getString(5);
            int no_repeat = Integer.parseInt(c.getString(6).trim());
            int switcher = Integer.parseInt(c.getString(7).trim());
            Time time = new Time(day, hour_start, hour_end, latitude, longitude, date, no_repeat, switcher);
            allTimes.add(time);
        }

        db1.close();
        return allTimes;
    }

    public Time getCurrentTime(int dayOfWeek, String hourOfDay, Context context)        // get current racking time by real hour of day
    {
        MyDbHelper dbHelper = new MyDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Time time = null;

        String selectQuery = "SELECT * FROM " + Constants_time.TABLE_NAME +
                " WHERE " + Constants_time.COLUMN_NAME_DAY + " = ? AND HourStart <= ? AND HourEnd >= ?";
        Cursor c = db.rawQuery(selectQuery, new String[] { String.valueOf(dayOfWeek), hourOfDay, hourOfDay });
        if(c.getCount() == 0)
        {
            db.close();
            return null;
        }
        if (c.moveToFirst())
        {
            String lat = c.getString(c.getColumnIndex("Latitude"));
            String lng = c.getString(c.getColumnIndex("Longitude"));
            String hour_start = c.getString(c.getColumnIndex("HourStart"));
            String hour_end = c.getString(c.getColumnIndex("HourEnd"));
            int noRepeat = Integer.parseInt(c.getString(c.getColumnIndex("NoRepeat")));
            int switcher = Integer.parseInt(c.getString(c.getColumnIndex("Switcher")));
            String date = c.getString(c.getColumnIndex("Date"));

            time = new Time(dayOfWeek, hour_start, hour_end, lat, lng, date, noRepeat, switcher);
        }

        db.close();
        return time;
    }

    public Time getCurrentTimeSwitchOn(int dayOfWeek, String hourOfDay, Context context)        // get all time which are on
    {
        MyDbHelper dbHelper = new MyDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Time time = null;

        String selectQuery = "SELECT * FROM " + Constants_time.TABLE_NAME +
                " WHERE " + Constants_time.COLUMN_NAME_DAY + " = ? AND " +
                Constants_time.COLUMN_NAME_HOUR_START + " <= ? AND " +
                Constants_time.COLUMN_NAME_HOUR_END + " >= ? AND " +
                Constants_time.COLUMN_NAME_SWITCHER + "= ?";
        Cursor c = db.rawQuery(selectQuery, new String[] { String.valueOf(dayOfWeek), hourOfDay, hourOfDay, String.valueOf(SWITCH_ON) });
        if(c.getCount() == 0)
        {
            db.close();
            return null;
        }
        if (c.moveToFirst())
        {
            String lat = c.getString(c.getColumnIndex("Latitude"));
            String lng = c.getString(c.getColumnIndex("Longitude"));
            String hour_start = c.getString(c.getColumnIndex("HourStart"));
            String hour_end = c.getString(c.getColumnIndex("HourEnd"));
            int noRepeat = Integer.parseInt(c.getString(c.getColumnIndex("NoRepeat")));
            int switcher = Integer.parseInt(c.getString(c.getColumnIndex("Switcher")));
            String date = c.getString(c.getColumnIndex("Date"));

            time = new Time(dayOfWeek, hour_start, hour_end, lat, lng, date, noRepeat, switcher);
        }

        db.close();
        return time;
    }

    public List<String> getAllHoursAndDayByLatLng(String latitude, String longitude, Context context)       // get all hours in string by date and lat-lng
    {
        List<String> allHoursAndDays = new ArrayList<>();
        MyDbHelper dbHelper = new MyDbHelper(context);
        SQLiteDatabase db1 = dbHelper.getReadableDatabase();

        String[] cols={Constants_time.COLUMN_NAME_DAY,
                Constants_time.COLUMN_NAME_HOUR_START,
                Constants_time.COLUMN_NAME_HOUR_END,
                Constants_time.COLUMN_NAME_SWITCHER,
                Constants_time.COLUMN_NAME_NO_REPEAT};

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
            String switcher = c.getString(3).trim();
            String no_repeat = c.getString(4).trim();
            String time = day + "," + hour_start + " - " + hour_end + "," + switcher + "," + latitude + "," + longitude + "," + no_repeat;
            allHoursAndDays.add(time);
        }

        db1.close();

        return allHoursAndDays;
    }
    public void deleteTime(String latitude, String longitude, String hours, String day, Context context)        // delete time
    {
        MyDbHelper dbHelper = new MyDbHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String hour_start, hour_end;
        String[] parser = hours.split("-");
        hour_start = parser[0].trim();
        hour_end = parser[1].trim();
        int day_int = convertStringDayToInt(day);
        db.delete(Constants_time.TABLE_NAME, Constants_time.COLUMN_NAME_LATITUDE + " = ? AND " +
                                             Constants_time.COLUMN_NAME_LONGITUDE + " = ? AND " +
                                             Constants_time.COLUMN_NAME_DAY + " = ? AND " +
                                             Constants_time.COLUMN_NAME_HOUR_START + " = ? AND " +
                                             Constants_time.COLUMN_NAME_HOUR_END + " = ?",
                new String[] { latitude, longitude, String.valueOf(day_int), hour_start, hour_end});
        db.close();
    }

    public void deleteTime(Time time, Context context)      // delete time
    {
        MyDbHelper dbHelper = new MyDbHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        db.delete(Constants_time.TABLE_NAME, Constants_time.COLUMN_NAME_LATITUDE + " = ? AND " +
                        Constants_time.COLUMN_NAME_LONGITUDE + " = ? AND " +
                        Constants_time.COLUMN_NAME_DAY + " = ? AND " +
                        Constants_time.COLUMN_NAME_HOUR_START + " = ? AND " +
                        Constants_time.COLUMN_NAME_HOUR_END + " = ?",
                new String[] { time.getLatitude(), time.getLongitude(), String.valueOf(time.getDay()), time.getHourStart(), time.getHourEnd()});
        db.close();
    }

    public void deleteAllTimes(String latitude, String longitude, Context context)      // delete all times by lat-lng
    {
        MyDbHelper dbHelper = new MyDbHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        List<Time> allTimes = getAllTimeByLatLng(latitude, longitude, context);
        for(Time time : allTimes)
        {
            String hours = time.getHourStart() + "-" + time.getHourEnd();
            String day_string = convertIntDayToString(time.getDay());
            deleteTime(time.getLatitude(), time.getLongitude(), hours, day_string, context);
        }
        db.close();
    }

    public void changeSwitchOn(String day, String hour_start, String hour_end, String latitude, String longitude, Context context) throws SQLException
    {// update time to be on
        MyDbHelper dbHelper = new MyDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        int day_int = convertStringDayToInt(day);

        ContentValues values = new ContentValues();
        values.put(Constants_time.COLUMN_NAME_SWITCHER, SWITCH_ON);

       db.update(Constants_time.TABLE_NAME, values, Constants_time.COLUMN_NAME_LATITUDE + " = ? AND " +
                        Constants_time.COLUMN_NAME_LONGITUDE + " = ? AND " +
                        Constants_time.COLUMN_NAME_DAY + " = ? AND " +
                        Constants_time.COLUMN_NAME_HOUR_START + " = ? AND " +
                        Constants_time.COLUMN_NAME_HOUR_END + " = ?",
                new String[]{latitude, longitude, String.valueOf(day_int), hour_start, hour_end});
        db.close();
    }

    public void changeSwitchOff(String day, String hour_start, String hour_end, String latitude, String longitude, Context context) throws SQLException
    { // update time to be off

        MyDbHelper dbHelper = new MyDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        int day_int = convertStringDayToInt(day);

        ContentValues values = new ContentValues();
        values.put(Constants_time.COLUMN_NAME_SWITCHER, SWITCH_OFF);

        db.update(Constants_time.TABLE_NAME, values,
                        Constants_time.COLUMN_NAME_LATITUDE + " = ? AND " +
                        Constants_time.COLUMN_NAME_LONGITUDE + " = ? AND " +
                        Constants_time.COLUMN_NAME_DAY + " = ? AND " +
                        Constants_time.COLUMN_NAME_HOUR_START + " = ? AND " +
                        Constants_time.COLUMN_NAME_HOUR_END + " = ?",
                new String[]{latitude, longitude, String.valueOf(day_int), hour_start, hour_end});
        db.close();
    }

    public LatLng getSwitchOnLocationByDateAndTime(int dayOfWeek, String hourOfDay, Context context) throws ParseException  // get all location on by date and time
    {
        MyDbHelper dbHelper = new MyDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        LatLng latLng = null;

        String selectQuery = "SELECT * FROM " + Constants_time.TABLE_NAME +
                " WHERE " + Constants_time.COLUMN_NAME_DAY + " = ? AND " +
                Constants_time.COLUMN_NAME_HOUR_START + " <= ? AND " +
                Constants_time.COLUMN_NAME_HOUR_END + " >= ? AND " +
                Constants_time.COLUMN_NAME_SWITCHER + " = ?";
        Cursor c = db.rawQuery(selectQuery, new String[] { String.valueOf(dayOfWeek), hourOfDay, hourOfDay, String.valueOf(SWITCH_ON) });
        if(c.getCount() == 0)
        {
            db.close();
            return null;
        }
        if (c.moveToFirst())
        {
            String lat = c.getString(c.getColumnIndex("Latitude"));
            String lng = c.getString(c.getColumnIndex("Longitude"));

            latLng = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
        }

        db.close();
       return latLng;
    }

    public List<String> getAllTime(Context context)     // get all tracking times
    {
        List<String> allHoursAndDays = new ArrayList<>();
        MyDbHelper dbHelper = new MyDbHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] cols={Constants_time.COLUMN_NAME_DAY,
                Constants_time.COLUMN_NAME_HOUR_START,
                Constants_time.COLUMN_NAME_HOUR_END,
                Constants_time.COLUMN_NAME_SWITCHER,
                Constants_time.COLUMN_NAME_LATITUDE,
                Constants_time.COLUMN_NAME_LONGITUDE,
                Constants_time.COLUMN_NAME_NO_REPEAT};


        Cursor c = db.query(
                Constants_time.TABLE_NAME,    // The table to query
                cols,                    // The columns to return
                null,
                null,
                null,                    // don't group the rows
                null,                    // don't filter by row groups
                Constants_time.COLUMN_NAME_DAY + " ASC, " + Constants_time.COLUMN_NAME_HOUR_START + " ASC"     // The sort order
        );

        while (c.moveToNext())
        {
            String day = c.getString(0).trim();
            String hour_start = c.getString(1).trim();
            String hour_end = c.getString(2).trim();
            String switcher = c.getString(3).trim();
            String latitude = c.getString(4).trim();
            String longitude = c.getString(5).trim();
            String no_repeat = c.getString(6).trim();
            String time = day + "," + hour_start + " - " + hour_end + "," + switcher + "," + latitude + "," + longitude + "," + no_repeat;
            allHoursAndDays.add(time);
        }
        db.close();

        return allHoursAndDays;
    }

    public List<Time> getTimesByNoRepeat(Context context)       // get all no_repeat times
    {
        List<Time> times = new ArrayList<>();

        MyDbHelper dbHelper = new MyDbHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] cols={Constants_time.COLUMN_NAME_DAY,
                Constants_time.COLUMN_NAME_HOUR_START,
                Constants_time.COLUMN_NAME_HOUR_END,
                Constants_time.COLUMN_NAME_LATITUDE,
                Constants_time.COLUMN_NAME_LONGITUDE,
                Constants_time.COLUMN_NAME_DATE,
                Constants_time.COLUMN_NAME_NO_REPEAT,
                Constants_time.COLUMN_NAME_SWITCHER};

        Cursor c = db.query(
                Constants_time.TABLE_NAME,    // The table to query
                cols,                    // The columns to return
                Constants_time.COLUMN_NAME_NO_REPEAT + "=?",
                new String[]{String.valueOf(1)},
                null,                    // don't group the rows
                null,                    // don't filter by row groups
                null                     // The sort order
        );

        if (c.getCount() == 0)
        {
            //cursor is empty
            db.close();
            return null;
        }
        while (c.moveToNext())
        {
            String day = c.getString(0).trim();
            String hour_start = c.getString(1).trim();
            String hour_end = c.getString(2).trim();
            String latitude = c.getString(3).trim();
            String longitude = c.getString(4).trim();
            String date = c.getString(5).trim();
            String no_repeat = c.getString(6).trim();
            String switcher = c.getString(7).trim();

            Time timeByDay = new Time(Integer.parseInt(day), hour_start, hour_end, latitude, longitude, date, Integer.parseInt(no_repeat), Integer.parseInt(switcher));
            times.add(timeByDay);
        }
        db.close();
        return times;
    }

    private int convertStringDayToInt(String day)     //  convert string date to int
    {
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
    private String convertIntDayToString(int intDay)
    {
        // int int_day = Integer.parseInt(intDay);
        String day = "";
        switch (intDay) {
            case 1:
                day = "Sunday";break;
            case 2:
                day = "Monday";break;
            case 3:
                day = "Tuesday";break;
            case 4:
                day = "Wednesday";break;
            case 5:
                day = "Thursday";break;
            case 6:
                day = "Friday";break;
            case 7:
                day = "Saturday";break;
        }
        return day;
    }



}
