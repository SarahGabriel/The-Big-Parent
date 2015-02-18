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
 * Created by Guy on 12/02/15.
 */
public class Bl_app
{



    public static void clearSmsPrefs(Context context)
    {
        final String TIME = "time";
        final String EMPTY = "empty";
        final String END_TIME = "endTime";
        final String DAY = "empty";

        SharedPreferences settings = context.getSharedPreferences("MyPrefsFile", context.MODE_PRIVATE);
        SharedPreferences.Editor editor;
        editor = settings.edit();
        editor.putString(TIME, EMPTY);
        editor.putString(END_TIME, "");
        editor.putInt(DAY , 0);
        editor.commit();
    }

    public static void clearSmsPrefsIfSwitchOff(String latitude, String longitude, Context context)
    {
        Dal_time dal_time = new Dal_time();

        Calendar now = Calendar.getInstance();

        int dayOfWeek = now.get(Calendar.DAY_OF_WEEK);
        int hour = now.get(Calendar.HOUR_OF_DAY);
        int minute = now.get(Calendar.MINUTE);
        String hourOfDay = String.format("%02d", hour) + ":" + String.format("%02d", minute);


        Time curTime = dal_time.getCurrentTime(dayOfWeek, hourOfDay, context);

        if(curTime != null)
        {
            if(curTime.getLatitude().equals(latitude) && curTime.getLongitude().equals(longitude))
            {
                clearSmsPrefs(context);
            }
        }
    }

    public static void makeSound(Context context, int id)
    {
        MediaPlayer mediaPlayer = MediaPlayer.create(context, id);
        try
        {
            mediaPlayer.prepare();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        catch (IllegalStateException e)
        {
            e.printStackTrace();
        }
        mediaPlayer.start();
    }

    public static void deleteExpiredNoRepeatTimes(Context context) throws ParseException
    {

        Dal_time dal_time = new Dal_time();
        List<Time> times = dal_time.getTimesByNoRepeat(context);

        if(times == null)
        {
            return;
        }
        //Toast.makeText(context, times.toString(), Toast.LENGTH_LONG).show();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        Date today = new Date();        // day of today
        String date = dateFormat.format(today);     // formatting date to string

        Calendar now = Calendar.getInstance();
        int hour = now.get(Calendar.HOUR_OF_DAY);
        int minute = now.get(Calendar.MINUTE);
        String hourOfDay = String.format("%02d", hour) + ":" + String.format("%02d", minute);

        Date hour_today = sdf.parse(hourOfDay);
        Date date_today = dateFormat.parse(date);    // back from string to date with format


        for (Time time : times) {
            String hour_start = time.getHourStart();
            String hour_end = time.getHourEnd();
            String date_time = time.getDate();

            Date dateTime = dateFormat.parse(date_time);
            Date hourEnd = sdf.parse(hour_end);

            if (date_today.after(dateTime)) {
                dal_time.deleteTime(time, context);

            } else if (hour_today.after(hourEnd)) {
                dal_time.deleteTime(time, context);
            }
            // Date hour_time = sdf.parse(hour_start + ":" + hour_end);
        }

    }

}
