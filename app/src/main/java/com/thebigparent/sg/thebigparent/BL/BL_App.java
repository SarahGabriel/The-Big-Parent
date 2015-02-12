package com.thebigparent.sg.thebigparent.BL;

import android.content.Context;
import android.media.MediaPlayer;

import java.io.IOException;

/**
 * Created by Guy on 12/02/15.
 */
public class BL_App {

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
