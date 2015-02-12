package com.thebigparent.sg.thebigparent.Classes;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import com.thebigparent.sg.thebigparent.Activities.MainActivity;
import com.thebigparent.sg.thebigparent.Activities.MapsActivity;

public class TouchableWrapper extends FrameLayout {

    long downTime;

    public TouchableWrapper(Context context) {
        super(context);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {

        if(event.getAction() == MotionEvent.ACTION_DOWN)
        {
            startRadius();
            MapsActivity.mMapIsTouched = true;
        }
        else if(event.getAction() == MotionEvent.ACTION_UP)
        {
            MapsActivity.mMapIsTouched = false;
        }

        return super.dispatchTouchEvent(event);
    }


    public void startRadius()
    {
        Thread t = new Thread()
        {
            public void run()
            {
                boolean start = true;

                while (MapsActivity.mMapIsTouched)
                {
                    try
                    {
                        if(start) {
                            Thread.sleep(3000);
                            start = false;
                        }
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }

                    if(MapsActivity.mMapIsTouched)
                    {
                        try
                        {
                                Thread.sleep(100);
                        }
                        catch (InterruptedException e)
                        {
                            e.printStackTrace();
                        }

                        Log.e("3 seconds", "3 seconds!!!!!!!!!!!!!!!!!!!!");
                    }
                }

            }
        };

        t.start();
    }

}
