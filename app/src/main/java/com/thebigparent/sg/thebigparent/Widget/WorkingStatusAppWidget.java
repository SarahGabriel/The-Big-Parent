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
public class WorkingStatusAppWidget extends AppWidgetProvider {

    private Button turnOnButton;
    final static String WIDGET_ACTION = "WIDGET_ACTION";
    final static String turnOnButtonText = "Turn on locations";
    final static String turnOffButtonText = "Turn off locations";
    private static RemoteViews views;


    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        final int N = appWidgetIds.length;
        for (int i = 0; i < N; i++) {
            updateAppWidget(context, appWidgetManager, appWidgetIds[i]);
        }
    }


    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created


    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        CharSequence widgetText = context.getString(R.string.appwidget_text);
        // Construct the RemoteViews object
        views = new RemoteViews(context.getPackageName(), R.layout.working_status_app_widget);
        views.setTextViewText(R.id.appwidget_text, widgetText);


        SharedPreferences settings = context.getSharedPreferences("MyPrefsFile", context.MODE_PRIVATE);
        boolean on = settings.getBoolean("On", false);

        if(on && canGetLocation(context))
        {
            views.setInt(R.id.widgetLayout, "setBackgroundResource", R.drawable.on_background);
            views.setTextViewText(R.id.appwidget_text, "The Big Parent: ON");
        }
        else
        {
            views.setInt(R.id.widgetLayout, "setBackgroundResource", R.drawable.off_background);
            views.setTextViewText(R.id.appwidget_text, "The Big Parent: OFF");
        }

        Intent intent = new Intent(context, MainActivity.class);
        intent.setAction(WIDGET_ACTION);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        views.setOnClickPendingIntent(R.id.appwidget_text, pendingIntent);

//        Intent intentServiceGps = new Intent(context,GpsService.class);
//        intentServiceGps.setAction("STOPy");
//
//        PendingIntent pendingIntent2 = PendingIntent.getService(context, 0, intentServiceGps, 0);
//        views.setOnClickPendingIntent(R.id.turnOnButton, pendingIntent2);


//        if(canGetLocation(context))
//        {
//            views.setInt(R.id.turnOnButton, "setBackgroundColor", Color.GREEN);
//            views.setTextViewText(R.id.turnOnButton, "Yeaaaa");
//        }
//        else
//        {
//            views.setInt(R.id.turnOnButton, "setBackgroundColor", Color.RED);
//        }

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);


    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        if(intent.getAction().equals(WIDGET_ACTION))
        {
//            Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS );
//            myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            context.startActivity(myIntent);
//
//
//            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
//            ComponentName thisWidget = new ComponentName(context, WorkingStatusAppWidget.class);
//
//
//            appWidgetManager.updateAppWidget(thisWidget, views);
//
//
        }


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



}


