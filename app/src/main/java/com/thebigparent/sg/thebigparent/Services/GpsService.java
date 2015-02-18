package com.thebigparent.sg.thebigparent.Services;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.thebigparent.sg.thebigparent.BL.Bl_app;
import com.thebigparent.sg.thebigparent.Classes.MapLocation;
import com.thebigparent.sg.thebigparent.Classes.Time;
import com.thebigparent.sg.thebigparent.Dal.Dal_location;
import com.thebigparent.sg.thebigparent.Dal.Dal_time;
import com.thebigparent.sg.thebigparent.R;
import com.thebigparent.sg.thebigparent.Widget.WorkingStatusAppWidget;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class GpsService extends Service {       // Tracking location service


    final long MIN_TIME_FOR_UPDATE = 0;   // 300000 = 5 minutes
    final long MIN_DIS_FOR_UPDATE = 1;
    private final String TIME = "time";             //Key for shared prefs
    private final String EMPTY = "empty";           //Key for shared prefs
    private final String END_TIME = "endTime";      //Key for shared prefs
    private final String DAY = "empty";             //Key for shared prefs


    LocationManager locationManager;   //Android location manager

    LocationListener locationListener = new LocationListener()  // listener to location manager
    {
        public void onLocationChanged(Location realLocation) // Called when a new location is found by the network location provider.
        {

            Dal_time dal_time = new Dal_time();
            Dal_location dal_location = new Dal_location();
            String radius;

            try     // Delete all no repeat expired times
            {
                Bl_app.deleteExpiredNoRepeatTimes(getApplicationContext());
            }
            catch (ParseException e)
            {
                e.printStackTrace();
            }

            LatLng markerLocationLatLng = null;
            MapLocation markerLocation;

            Calendar now = Calendar.getInstance();              // Get the NOW time
            int dayOfWeek = now.get(Calendar.DAY_OF_WEEK);
            int hour = now.get(Calendar.HOUR_OF_DAY);
            int minute = now.get(Calendar.MINUTE);

            String hourOfDay = String.format("%02d", hour) + ":" + String.format("%02d", minute);       // HH:MM


            SharedPreferences settings = getSharedPreferences("MyPrefsFile", MODE_PRIVATE);
            SharedPreferences.Editor editor;

            ifSmsSentClearPrefs(dayOfWeek, hourOfDay); // check if sent sms is still valid of expired

            String lastSmsTime = settings.getString(TIME, EMPTY);           // Get the last sms sent

            if(!lastSmsTime.equals(EMPTY))  // if sms still valid return - not expired time
            {
                return;
            }


            try
            {
                // Get the relevant location in NOW time that tracking time in ON
                markerLocationLatLng = dal_time.getSwitchOnLocationByDateAndTime(dayOfWeek, hourOfDay, getApplicationContext());
            }
            catch (ParseException e)
            {
                e.printStackTrace();
            }

            if(markerLocationLatLng != null)  // If markerLocationLatLng in ON
            {
                Time trackingTime = dal_time.getCurrentTime(dayOfWeek, hourOfDay, getApplicationContext());     // Get the current tacking time
                // Get the current markerLocation
                markerLocation = dal_location.getLocation(String.valueOf(markerLocationLatLng.latitude), String.valueOf(markerLocationLatLng.longitude), getApplicationContext());
                radius = markerLocation.getRadius();  // get the radius

                Location copyOfMarkerLocation = new Location("Copy");      // Copy of location

                copyOfMarkerLocation.setLatitude(markerLocationLatLng.latitude);
                copyOfMarkerLocation.setLongitude(markerLocationLatLng.longitude);

                float distance = copyOfMarkerLocation.distanceTo(realLocation);     // Get the distance between realLocation to the marker

                if(distance > Float.valueOf(radius))        // If realLocation is out of radius
                {

                    // make sound for debug use
                    //Bl_app.makeSound(getApplicationContext(), R.raw.cat);


                    // Toast to screen SMS sent
                    Toast.makeText(getApplicationContext(), "SMS sent to : " + markerLocation.getContact() + " " + markerLocation.getPhone() , Toast.LENGTH_LONG).show();

                    // Send sms to contact
                    sendSMS(markerLocation.getPhone() , "Out of " + markerLocation.getLocationName());

                    // Save the sent sms time
                    String endTime = trackingTime.getHourEnd();
                    editor = settings.edit();
                    editor.putString(TIME, hourOfDay);
                    editor.putString(END_TIME, endTime);
                    editor.putInt(DAY , dayOfWeek);
                    editor.commit();
                }
            }

        }


        public void onStatusChanged(String provider, int status, Bundle extras)
        {
        }

        public void onProviderEnabled(String provider)  // on provider enabled update widget to ON
        {
            RemoteViews remoteViews = new RemoteViews(getApplicationContext().getPackageName(), R.layout.working_status_app_widget);

            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getApplicationContext());
            ComponentName thisWidget = new ComponentName(getApplicationContext(), WorkingStatusAppWidget.class);
            remoteViews.setInt(R.id.widgetLayout, "setBackgroundResource", R.drawable.on_background);
            remoteViews.setTextViewText(R.id.appwidget_text, "The Big Parent: ON");
            appWidgetManager.updateAppWidget(thisWidget, remoteViews);

        }

        public void onProviderDisabled(String provider)     // on provider disabled update widget to OFF
        {
            RemoteViews remoteViews = new RemoteViews(getApplicationContext().getPackageName(), R.layout.working_status_app_widget);
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getApplicationContext());
            ComponentName thisWidget = new ComponentName(getApplicationContext(), WorkingStatusAppWidget.class);
            remoteViews.setInt(R.id.widgetLayout, "setBackgroundResource", R.drawable.off_background);
            remoteViews.setTextViewText(R.id.appwidget_text, "The Big Parent: OFF");
            appWidgetManager.updateAppWidget(thisWidget, remoteViews);
        }

    };


    public GpsService()
    {
        super();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId)        // When service start
    {
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);        //Initialize locationManager
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_FOR_UPDATE, MIN_DIS_FOR_UPDATE, locationListener);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy()     // When service stop
    {
        super.onDestroy();
        locationManager.removeUpdates(locationListener);       // Remove locationListener listener
    }




    private void sendSMS(String phoneNumber, String message)        // Send sms to contact
    {
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, null, null);
    }

    public void ifSmsSentClearPrefs(int dayOfWeek , String hourOfDay)        // If sms was sent if expired clear shared prefs
    {
        // Check if sms was sent
        SharedPreferences settings = getSharedPreferences("MyPrefsFile", MODE_PRIVATE);
        String lastSmsTime = settings.getString(TIME, EMPTY);       // Get last sms time and day
        int lastSmsDay = settings.getInt(DAY, 0);


        if(!lastSmsTime.equals(EMPTY))        // If not empty
        {
            if(dayOfWeek != lastSmsDay)         // If the day expired
            {
                Bl_app.clearSmsPrefs(this);   // Clear sms shared prefs
            }
            else
            {
                String endTime = settings.getString(END_TIME, "");

                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

                Date endTimeDate = null;
                Date hourOfDayDate = null;
                try {
                    endTimeDate = sdf.parse(endTime);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                try {
                    hourOfDayDate = sdf.parse(hourOfDay);
                } catch (ParseException e) {
                    e.printStackTrace();
                }


                assert hourOfDayDate != null;
                if(hourOfDayDate.after(endTimeDate))       // If the tracking time ended
                {
                    Bl_app.clearSmsPrefs(this);             // Clear sms shared prefs
                }
            }
        }
    }
}
