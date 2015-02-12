package com.thebigparent.sg.thebigparent.Dal;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.thebigparent.sg.thebigparent.Classes.Time;
import com.thebigparent.sg.thebigparent.DB.Constants_time;
import com.thebigparent.sg.thebigparent.DB.MyDbHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Sarah on 11-Feb-15.
 */
public class Dal_time
{
    public Dal_time()
    {
    }

    public boolean addNewTime(Time time, Context context) throws ParseException
    {
        if(isTimeExists(time, context))
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

    private boolean isTimeExists(Time time, Context context) throws ParseException
    {
        List<Time> timesByDay = getTimesByDay(time.getDay(), context);
        if(timesByDay!=null) {

            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

            Date startHour_toCheck = sdf.parse(time.getHourStart());
            Date endHour_toCheck = sdf.parse(time.getHourEnd());


            for (Time timeByDay : timesByDay) {
                Date startHour_checkWith = sdf.parse(timeByDay.getHourStart());
                Date endHour_checkWith = sdf.parse(timeByDay.getHourEnd());


                if ((startHour_toCheck.after(startHour_checkWith) || startHour_toCheck.equals(startHour_checkWith)) && startHour_toCheck.before(startHour_checkWith)) {/* if the start_hour of tracking time is in range of an existing tracking time
              * if(startHour_toCheck >= startHour_checkWith && startHour_toCheck < startHour_checkWith)*/
                    return true;
                } else if (endHour_toCheck.after(startHour_checkWith) && (endHour_toCheck.before(endHour_checkWith) || endHour_toCheck.equals(endHour_checkWith))) {
                /* if the end_hour is conflicting with an other start_hour
                *  if(endHour_toCheck > startHour_checkWith && endHour_toCheck <= endHour_checkWith)*/
                    return true;
                }

            }
        }
        return false;
    }

    public List<Time> getTimesByDay(int day, Context context)
    {
        //String stringDay = convertIntDayToString(day);
        List<Time> timesByDay = new ArrayList<Time>();

        MyDbHelper dbHelper = new MyDbHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] cols={Constants_time.COLUMN_NAME_DAY,
                Constants_time.COLUMN_NAME_HOUR_START,
                Constants_time.COLUMN_NAME_HOUR_END,
                Constants_time.COLUMN_NAME_LATITUDE,
                Constants_time.COLUMN_NAME_LONGITUDE,
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

        if (!(c.moveToFirst()) || c.getCount() == 0)
        {
            //cursor is empty
            return null;
        }
        while (c.moveToNext())
        {
            Log.w("SWITCH", c.getString(6));
            Time timeByDay = new Time(day, c.getString(1), c.getString(2), c.getString(3), c.getString(4), Integer.parseInt(c.getString(5).trim()), Integer.parseInt(c.getString(6).trim()));
            Log.w("addTimeByDay", timeByDay.toString());
            timesByDay.add(timeByDay);
        }
        Log.w("allTimesByDay", timesByDay.toString());

        db.close();

        return timesByDay;
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
            int no_repeat = Integer.parseInt(c.getString(5).trim());
            int switcher = Integer.parseInt(c.getString(6).trim());
            Time time = new Time(day, hour_start, hour_end, latitude, longitude, no_repeat, switcher);
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
                Constants_time.COLUMN_NAME_HOUR_END,
                Constants_time.COLUMN_NAME_SWITCHER};
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
            String switcher = c.getString(3).trim();
            String time = day + "," + hour_start + " - " + hour_end + "," + switcher + "," + latitude + "," + longitude;
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

    public void changeSwitchOn(String day, String hour_start, String hour_end, String latitude, String longitude, Context context)
    {
        MyDbHelper dbHelper = new MyDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String[] cols={Constants_time.COLUMN_NAME_DAY,
                Constants_time.COLUMN_NAME_HOUR_START,
                Constants_time.COLUMN_NAME_HOUR_END,
                Constants_time.COLUMN_NAME_SWITCHER};

        ContentValues values = new ContentValues();
        values.put(Constants_time.COLUMN_NAME_SWITCHER, 1);

//        db.update(Constants_time.TABLE_NAME, values, Constants_time.COLUMN_NAME_DAY + " = ? AND " +
//                Constants_time.COLUMN_NAME_HOUR_START + " = ? AND " +
//                Constants_time.COLUMN_NAME_HOUR_END + " = ? AND " +
//                Constants_time.COLUMN_NAME_LATITUDE + " = ? AND " +
//                Constants_time.COLUMN_NAME_LONGITUDE + " = ?", new String[] {day, hour_start, hour_end, latitude, longitude});

        db.update(Constants_time.TABLE_NAME, values, Constants_time.COLUMN_NAME_LATITUDE + " = ? AND " +
                        Constants_time.COLUMN_NAME_LONGITUDE + " = ? AND " +
                        Constants_time.COLUMN_NAME_DAY + " = ? AND " +
                        Constants_time.COLUMN_NAME_HOUR_START + " = ? AND " +
                        Constants_time.COLUMN_NAME_HOUR_END + " = ?",
                new String[]{latitude, longitude, day, hour_start, hour_end});

        db.close();
    }

    public void changeSwitchOff(String day, String hour_start, String hour_end, String latitude, String longitude, Context context)
    {
        MyDbHelper dbHelper = new MyDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Constants_time.COLUMN_NAME_SWITCHER, 0);

        db.update(Constants_time.TABLE_NAME, values, Constants_time.COLUMN_NAME_LATITUDE + " = ? AND " +
                        Constants_time.COLUMN_NAME_LONGITUDE + " = ? AND " +
                        Constants_time.COLUMN_NAME_DAY + " = ? AND " +
                        Constants_time.COLUMN_NAME_HOUR_START + " = ? AND " +
                        Constants_time.COLUMN_NAME_HOUR_END + " = ?",
                new String[] { latitude, longitude, day, hour_start, hour_end});

        db.close();
    }
}
