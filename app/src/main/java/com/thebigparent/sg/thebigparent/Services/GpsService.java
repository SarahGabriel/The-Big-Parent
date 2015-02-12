package com.thebigparent.sg.thebigparent.Services;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.RemoteViews;

import com.google.android.gms.maps.model.LatLng;
import com.thebigparent.sg.thebigparent.BL.Bl_app;
import com.thebigparent.sg.thebigparent.Classes.MapLocation;
import com.thebigparent.sg.thebigparent.Dal.Dal_location;
import com.thebigparent.sg.thebigparent.Dal.Dal_time;
import com.thebigparent.sg.thebigparent.R;
import com.thebigparent.sg.thebigparent.Widget.WorkingStatusAppWidget;

import java.text.ParseException;
import java.util.Calendar;

public class GpsService extends Service {

    final long MIN_TIME_FOR_UPDATE = 0;   // 300000 = 5 minutes
    final long MIN_DIS_FOR_UPDATE = 1;
    LocationManager locationManager;

    LocationListener locationListener = new LocationListener()
    {
        public void onLocationChanged(Location realLocation)
        {
            // Called when a new location is found by the network location provider.
            //makeUseOfNewLocation(location);


            Dal_time dal_time = new Dal_time();
            String radius;
            Dal_location dal_location = new Dal_location();
            LatLng latLngSupposedToBeIn = null;
            MapLocation locationSupposedToBeIn;
            Calendar now = Calendar.getInstance();
            int dayOfWeek = now.get(Calendar.DAY_OF_WEEK);
            int hour = now.get(Calendar.HOUR_OF_DAY);
            int minute = now.get(Calendar.MINUTE);
            String hourOfDay = String.format("%02d", hour) + ":" + String.format("%02d", minute);
            try
            {
                latLngSupposedToBeIn = dal_time.getSwitchOnLocationByDateAndTime(dayOfWeek, hourOfDay, getApplicationContext());

            }
            catch (ParseException e)
            {
                e.printStackTrace();
            }
            if(latLngSupposedToBeIn != null)
            {
                locationSupposedToBeIn = dal_location.getLocation(String.valueOf(latLngSupposedToBeIn.latitude), String.valueOf(latLngSupposedToBeIn.longitude), getApplicationContext());
                radius = locationSupposedToBeIn.getRadius();

                Location locationSupposedToBe = new Location("School point");

                locationSupposedToBe.setLatitude(latLngSupposedToBeIn.latitude);
                locationSupposedToBe.setLongitude(latLngSupposedToBeIn.longitude);

                float distance = locationSupposedToBe.distanceTo(realLocation);
                if(distance > Float.valueOf(radius))
                {
                    Bl_app.makeSound(getApplicationContext(), R.raw.cat);
                    SmsManager sms = SmsManager.getDefault();
                    sms.sendTextMessage("0525234316", null, "Liora n'est pas a l'ecole!!!!", null, null);
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



}
