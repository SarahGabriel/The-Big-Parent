package com.thebigparent.sg.thebigparent.Widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.LocationManager;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.thebigparent.sg.thebigparent.Activities.MainActivity;
import com.thebigparent.sg.thebigparent.Activities.MapsActivity;
import com.thebigparent.sg.thebigparent.R;
import com.thebigparent.sg.thebigparent.Services.GpsService;

/**
 * Implementation of App Widget functionality.
 */
public class WorkingStatusAppWidget extends AppWidgetProvider {     //Widget that displays the tracking status ON OFF

    final static String WIDGET_ACTION = "WIDGET_ACTION";
    private static RemoteViews views;


    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {   // On update
        // There may be multiple widgets active, so update all of them
        final int N = appWidgetIds.length;
        for (int i = 0; i < N; i++) {
            updateAppWidget(context, appWidgetManager, appWidgetIds[i]);
        }
    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId)    // On update
    {

        CharSequence widgetText = context.getString(R.string.appwidget_text);
        // Construct the RemoteViews object
        views = new RemoteViews(context.getPackageName(), R.layout.working_status_app_widget);
        views.setTextViewText(R.id.appwidget_text, widgetText);

        // Get the status state from shared pref
        SharedPreferences settings = context.getSharedPreferences("MyPrefsFile", context.MODE_PRIVATE);
        boolean on = settings.getBoolean("On", false);

        if(on && canGetLocation(context))           // If tracking is ON update widget
        {
            views.setInt(R.id.widgetLayout, "setBackgroundResource", R.drawable.on_background);
            views.setTextViewText(R.id.appwidget_text, "The Big Parent: ON");
        }
        else
        {
            views.setInt(R.id.widgetLayout, "setBackgroundResource", R.drawable.off_background);
            views.setTextViewText(R.id.appwidget_text, "The Big Parent: OFF");
        }


        // Set on click pending intent to go to main activity
        Intent intent = new Intent(context, MainActivity.class);
        intent.setAction(WIDGET_ACTION);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        views.setOnClickPendingIntent(R.id.appwidget_text, pendingIntent);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);


    }


    public static boolean canGetLocation(Context context)  // Check if device locations are ON
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



}


