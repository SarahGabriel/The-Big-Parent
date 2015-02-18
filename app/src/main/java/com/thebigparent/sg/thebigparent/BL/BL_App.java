package com.thebigparent.sg.thebigparent.BL;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;

import com.thebigparent.sg.thebigparent.Classes.Time;
import com.thebigparent.sg.thebigparent.Dal.Dal_time;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Bl_app
 *
 * Main functions of the project - in order to use them at any moment from every class
 */
public class Bl_app
{
    public static void clearSmsPrefs(Context context)           // clear preferences in order to allow the app to send again a message if needed
    {
        final String TIME = "time";
        final String EMPTY = "empty";
        final String END_TIME = "endTime";
        final String DAY = "empty";

        SharedPreferences settings = context.getSharedPreferences("MyPrefsFile", context.MODE_PRIVATE);     // get settings from file
        SharedPreferences.Editor editor;        // edit file
        editor = settings.edit();
        editor.putString(TIME, EMPTY);
        editor.putString(END_TIME, "");
        editor.putInt(DAY , 0);
        editor.commit();
    }

    public static void clearSmsPrefsIfSwitchOff(String latitude, String longitude, Context context)     // if tracking time is off, clear sharePreferences
    {
        Dal_time dal_time = new Dal_time();

        Calendar now = Calendar.getInstance();      // get actual hour and date

        int dayOfWeek = now.get(Calendar.DAY_OF_WEEK);
        int hour = now.get(Calendar.HOUR_OF_DAY);
        int minute = now.get(Calendar.MINUTE);
        String hourOfDay = String.format("%02d", hour) + ":" + String.format("%02d", minute);

        Time curTime = dal_time.getCurrentTime(dayOfWeek, hourOfDay, context);      // get current tracking time by actual hour

        if(curTime != null)
        {
            if(curTime.getLatitude().equals(latitude) && curTime.getLongitude().equals(longitude))
            {
                clearSmsPrefs(context);     // clear preferences
            }
        }
    }

    public static void makeSound(Context context, int id)       // make sound when clicking
    {
        MediaPlayer mediaPlayer = MediaPlayer.create(context, id);
        try
        {
            mediaPlayer.prepare();
        }
        catch (IOException | IllegalStateException e)
        {
            e.printStackTrace();
        }
        mediaPlayer.start();
    }

    public static void deleteExpiredNoRepeatTimes(Context context) throws ParseException        // delete expired tracking times if they are no_repeat and if time passed
    {

        Dal_time dal_time = new Dal_time();
        List<Time> times = dal_time.getTimesByNoRepeat(context);

        if(times == null)
        {
            return;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");       // format hours
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");     // format date

        Date today = new Date();        // day of today
        String date = dateFormat.format(today);     // formatting date to string

        Calendar now = Calendar.getInstance();      // get actual time
        int hour = now.get(Calendar.HOUR_OF_DAY);       // get hours
        int minute = now.get(Calendar.MINUTE);      // get minute
        String hourOfDay = String.format("%02d", hour) + ":" + String.format("%02d", minute);       // get time

        Date hour_today = sdf.parse(hourOfDay);
        Date date_today = dateFormat.parse(date);    // back from string to date with format


        for (Time time : times)
        {
            String hour_end = time.getHourEnd();
            String date_time = time.getDate();

            Date dateTime = dateFormat.parse(date_time);
            Date hourEnd = sdf.parse(hour_end);

            if (date_today.after(dateTime))     //  if day passed, remove tracking time
            {
                dal_time.deleteTime(time, context);

            } else if (hour_today.after(hourEnd))  // if end_hour passed, remove tracking time
            {
                dal_time.deleteTime(time, context);
            }
        }

    }

}
