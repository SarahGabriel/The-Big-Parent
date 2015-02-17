package com.thebigparent.sg.thebigparent.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.thebigparent.sg.thebigparent.BL.Bl_app;
import com.thebigparent.sg.thebigparent.Classes.Time;
import com.thebigparent.sg.thebigparent.Dal.Dal_location;
import com.thebigparent.sg.thebigparent.Dal.Dal_time;
import com.thebigparent.sg.thebigparent.R;
import com.thebigparent.sg.thebigparent.Services.GpsService;
import com.thebigparent.sg.thebigparent.Widget.WorkingStatusAppWidget;

import java.util.Calendar;
import java.util.List;


public class MainActivity extends Activity
{

    private Intent i;
    private Dal_time dal_time;
    private Dal_location dal_location;

    private Button allMarkerButton, allTrackingButton;
    private TextView trackingTime_textView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dal_time = new Dal_time();
        dal_location = new Dal_location();

        allTrackingButton = (Button)findViewById(R.id.all_tracking_time);
        allMarkerButton = (Button)findViewById(R.id.all_marker);
        trackingTime_textView = (TextView)findViewById(R.id.tracker_time);


        List<String> trackingList = dal_time.getAllTime(this);

        List<String> markersList = dal_location.getAllMarkers(this);

        if(trackingList.size() == 0)
        {
            //allTrackingButton.setClickable(false);
            allTrackingButton.setEnabled(false);
        }
        else
        {

            Calendar now = Calendar.getInstance();

            int dayOfWeek = now.get(Calendar.DAY_OF_WEEK);
            int hour = now.get(Calendar.HOUR_OF_DAY);
            int minute = now.get(Calendar.MINUTE);
            String hourOfDay = String.format("%02d", hour) + ":" + String.format("%02d", minute);
            Log.w("Tracking HOUR", hourOfDay);

            Time trackingTime = dal_time.getCurrentTimeSwitchOn(dayOfWeek, hourOfDay, this);
            if(trackingTime != null)
            {
                trackingTime_textView.setText(trackingTime.getHourStart() + " - " + trackingTime.getHourEnd());

            }
        }

        if(markersList.size() == 0)
        {
            allMarkerButton.setEnabled(false);
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        List<String> trackingList = dal_time.getAllTime(this);
        List<String> markersList = dal_location.getAllMarkers(this);

        if(trackingList.size() == 0)
        {
            allTrackingButton.setEnabled(false);
        }
        else
        {
            allTrackingButton.setEnabled(true);
            Calendar now = Calendar.getInstance();

            int dayOfWeek = now.get(Calendar.DAY_OF_WEEK);
            int hour = now.get(Calendar.HOUR_OF_DAY);
            int minute = now.get(Calendar.MINUTE);
            String hourOfDay = String.format("%02d", hour) + ":" + String.format("%02d", minute);
            Time trackingTime = dal_time.getCurrentTimeSwitchOn(dayOfWeek, hourOfDay, this);
            if(trackingTime != null)
            {
                trackingTime_textView.setText(trackingTime.getHourStart() + " - " + trackingTime.getHourEnd());
            }
            else
            {
                trackingTime_textView.setText(this.getResources().getString(R.string.no_tracker_time));
            }
        }

        if(markersList.size() == 0)
        {
            allMarkerButton.setEnabled(false);
        }
        else
        {
            allMarkerButton.setEnabled(true);
        }


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onClick_map_button(View view)
    {
        Intent i = new Intent(this, MapsActivity.class);
        startActivity(i);


    }


    public void onClick_startGps(View view) {


        Bl_app.makeSound(this, R.raw.app_interactive_alert_tone_on);
        Bl_app.clearSmsPrefs(this);
        if(!canGetLocation(this))
        {
            Bl_app.makeSound(this, R.raw.multimedia_pop_up_alert_tone_1);

            showSettingsAlert();

        }


        i = new Intent(this, GpsService.class);
        startService(i);


        SharedPreferences settings = getSharedPreferences("MyPrefsFile", MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("On", true);
        editor.commit();



        Context context = this;
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.working_status_app_widget);

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        ComponentName thisWidget = new ComponentName(context, WorkingStatusAppWidget.class);

//        remoteViews.setInt(R.id.widgetLayout, "setBackgroundColor", Color.GREEN);
        remoteViews.setInt(R.id.widgetLayout, "setBackgroundResource", R.drawable.on_background);
        remoteViews.setTextViewText(R.id.appwidget_text, "The Big Parent: ON");
        appWidgetManager.updateAppWidget(thisWidget, remoteViews);

    }


    public void onClick_stopGps(View view)
    {

        Bl_app.makeSound(this, R.raw.app_interactive_alert_tone_off);



        i = new Intent(this, GpsService.class);
        stopService(i);


        Context context = this;
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.working_status_app_widget);

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        ComponentName thisWidget = new ComponentName(context, WorkingStatusAppWidget.class);

       // remoteViews.setInt(R.id.widgetLayout, "setBackgroundColor", Color.RED);
        remoteViews.setInt(R.id.widgetLayout, "setBackgroundResource", R.drawable.off_background);
        remoteViews.setTextViewText(R.id.appwidget_text, "The Big Parent: OFF");
        appWidgetManager.updateAppWidget(thisWidget, remoteViews);


        SharedPreferences settings = getSharedPreferences("MyPrefsFile", MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("On", false);
        editor.commit();

    }



    public static boolean canGetLocation(Context context)
    {
        boolean result = true;
        LocationManager lm = null;
        boolean gps_enabled = false;
        boolean network_enabled = false;
        if (lm == null)
        {
            lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        }
        // exceptions will be thrown if provider is not permitted.
        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {

        }
        try
        {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        }
        catch (Exception ex) {
        }
        if (gps_enabled == false || network_enabled == false)
        {
            result = false;
        }
        else
        {
            result = true;
        }

        return result;
    }

    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        // Setting Dialog Title
        alertDialog.setTitle("Locations are OFF!");

        // Setting Dialog Message
        alertDialog.setMessage("Please Turn on locations");

        // On pressing Settings button
        alertDialog.setPositiveButton(
                "Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(
                                Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);
                    }
                });

        alertDialog.show();
    }


    public void onClick_allTrackingTime(View view)
    {
        Intent i = new Intent(this, AllTrackingTimeActivity.class);
        startActivity(i);
    }

    public void onClick_allMarker(View view)
    {
        Intent i = new Intent(this, AllMarkerActivity.class);
        startActivity(i);
    }
}
