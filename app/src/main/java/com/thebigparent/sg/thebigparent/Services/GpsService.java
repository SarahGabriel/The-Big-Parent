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
import android.util.Log;
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

public class GpsService extends Service {

    final long MIN_TIME_FOR_UPDATE = 0;   // 300000 = 5 minutes
    final long MIN_DIS_FOR_UPDATE = 1;
    private final String TIME = "time";
    private final String EMPTY = "empty";
    private final String END_TIME = "endTime";
    private final String DAY = "empty";

    LocationManager locationManager;

    LocationListener locationListener = new LocationListener()
    {
        public void onLocationChanged(Location realLocation) // Called when a new location is found by the network location provider.
        {

            Dal_time dal_time = new Dal_time();
            Dal_location dal_location = new Dal_location();

            String radius;

            LatLng markerLocationLatLng = null;
            MapLocation markerLocation;

            Calendar now = Calendar.getInstance();

            int dayOfWeek = now.get(Calendar.DAY_OF_WEEK);
            int hour = now.get(Calendar.HOUR_OF_DAY);
            int minute = now.get(Calendar.MINUTE);

            String hourOfDay = String.format("%02d", hour) + ":" + String.format("%02d", minute);


            SharedPreferences settings = getSharedPreferences("MyPrefsFile", MODE_PRIVATE);
            SharedPreferences.Editor editor;

            ifSmsSentClearPrefs(dayOfWeek, hourOfDay); // check if sent sms is still valid

            String lastSmsTime = settings.getString(TIME, EMPTY);

            if(!lastSmsTime.equals(EMPTY))  // if sms still valid return
            {
                return;
            }


            try
            {
                markerLocationLatLng = dal_time.getSwitchOnLocationByDateAndTime(dayOfWeek, hourOfDay, getApplicationContext());

                if(markerLocationLatLng != null)
                {
                    Toast.makeText(getApplicationContext(), markerLocationLatLng.toString(), Toast.LENGTH_LONG).show();
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "markerLocationLatLng NULL", Toast.LENGTH_LONG).show();
                }
            }
            catch (ParseException e)
            {
                e.printStackTrace();
            }



            if(markerLocationLatLng != null)
            {
                Time trackingTime = dal_time.getCurrentTime(dayOfWeek, hourOfDay, getApplicationContext());
                markerLocation = dal_location.getLocation(String.valueOf(markerLocationLatLng.latitude), String.valueOf(markerLocationLatLng.longitude), getApplicationContext());
                radius = markerLocation.getRadius();

                Location copyOfMarkerLocation = new Location("Copy");

                copyOfMarkerLocation.setLatitude(markerLocationLatLng.latitude);
                copyOfMarkerLocation.setLongitude(markerLocationLatLng.longitude);

                float distance = copyOfMarkerLocation.distanceTo(realLocation);

                if(distance > Float.valueOf(radius))
                {
                    Bl_app.makeSound(getApplicationContext(), R.raw.cat);
                    SmsManager sms = SmsManager.getDefault();

                    //todo: get contact phone number and add logical message
                    sms.sendTextMessage("0525234316", null, "Liora n'est pas a l'ecole!!!!", null, null);


                    // Save the send sms time

                    String endTime = trackingTime.getHourEnd();

                    editor = settings.edit();
                    editor.putString(TIME, hourOfDay);
                    editor.putString(END_TIME, endTime);
                    editor.putInt(DAY , dayOfWeek);
                    editor.commit();

                }
            }

            // print on the log screen
            Log.v("MyGPS", "Longitude: " + Double.toString(realLocation.getLongitude()) + ", Latitude: " + Double.toString(realLocation.getLatitude()));
        }


        public void onStatusChanged(String provider, int status, Bundle extras)
        {
        }

        public void onProviderEnabled(String provider)
        {
            RemoteViews remoteViews = new RemoteViews(getApplicationContext().getPackageName(), R.layout.working_status_app_widget);

            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getApplicationContext());
            ComponentName thisWidget = new ComponentName(getApplicationContext(), WorkingStatusAppWidget.class);

//        remoteViews.setInt(R.id.widgetLayout, "setBackgroundColor", Color.GREEN);
            remoteViews.setInt(R.id.widgetLayout, "setBackgroundResource", R.drawable.on_background);
            remoteViews.setTextViewText(R.id.appwidget_text, "The Big Parent: ON");
            appWidgetManager.updateAppWidget(thisWidget, remoteViews);

        }

        public void onProviderDisabled(String provider)
        {

            RemoteViews remoteViews = new RemoteViews(getApplicationContext().getPackageName(), R.layout.working_status_app_widget);

            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getApplicationContext());
            ComponentName thisWidget = new ComponentName(getApplicationContext(), WorkingStatusAppWidget.class);

//        remoteViews.setInt(R.id.widgetLayout, "setBackgroundColor", Color.GREEN);
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
    public int onStartCommand(Intent intent, int flags, int startId)
    {

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_FOR_UPDATE, MIN_DIS_FOR_UPDATE, locationListener);


        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        locationManager.removeUpdates(locationListener);
    }




    private void sendSMS(String phoneNumber, String message)
    {
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, null, null);
    }

    public void ifSmsSentClearPrefs(int dayOfWeek , String hourOfDay)
    {
        // Check if sms was sent
        SharedPreferences settings = getSharedPreferences("MyPrefsFile", MODE_PRIVATE);
        SharedPreferences.Editor editor;
        String lastSmsTime = settings.getString(TIME, EMPTY);
        int lastSmsDay = settings.getInt(DAY, 0);


        if(!lastSmsTime.equals(EMPTY))
        {
            if(dayOfWeek != lastSmsDay)
            {
                editor = settings.edit();
                editor.putString(TIME, EMPTY);
                editor.putString(END_TIME, "");
                editor.putInt(DAY , 0);
                editor.commit();

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


                if(hourOfDayDate.after(endTimeDate))
                {
                    editor = settings.edit();
                    editor.putString(TIME, EMPTY);
                    editor.putString(END_TIME, "");
                    editor.putInt(DAY , 0);
                    editor.commit();
                }
            }
        }
    }
}
