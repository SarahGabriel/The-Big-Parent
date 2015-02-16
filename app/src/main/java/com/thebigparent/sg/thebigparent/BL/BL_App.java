package com.thebigparent.sg.thebigparent.BL;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;

import com.thebigparent.sg.thebigparent.Classes.Time;
import com.thebigparent.sg.thebigparent.Dal.Dal_time;

import java.io.IOException;
import java.util.Calendar;

/**
 * Created by Guy on 12/02/15.
 */
public class Bl_app {



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

}
