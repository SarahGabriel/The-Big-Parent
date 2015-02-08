package com.thebigparent.sg.thebigparent.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.thebigparent.sg.thebigparent.Classes.MapLocation;
import com.thebigparent.sg.thebigparent.Dal.Dal_location;
import com.thebigparent.sg.thebigparent.R;

public class MarkerOptionsActivity extends ActionBarActivity
{
    private String latitude, longitude;
    private Dal_location dal_location;

    private TextView contactName, address, locationName, radius;
    private LinearLayout mainLayout;
    private NumberPicker np_hour_start, np_min_start, np_hour_end, np_min_end;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marker_options);

        Intent i = getIntent();

        latitude = i.getStringExtra("latitude");
        longitude = i.getStringExtra("longitude");

        dal_location = new Dal_location();

        MapLocation location = dal_location.getLocation(latitude, longitude, this);

        contactName = (TextView)findViewById(R.id.contact_name);
        address = (TextView)findViewById(R.id.address_field);
        locationName = (TextView)findViewById(R.id.locationName_field);
        radius = (TextView)findViewById(R.id.radius_field);
        mainLayout = (LinearLayout)findViewById(R.id.main_layout_marker_options);
        np_hour_start = (NumberPicker)findViewById(R.id.number_picker_hour_start);
        np_min_start = (NumberPicker)findViewById(R.id.number_picker_min_start);
        np_hour_end = (NumberPicker)findViewById(R.id.number_picker_hour_end);
        np_min_end = (NumberPicker)findViewById(R.id.number_picker_min_end);

        np_hour_start.setMinValue(00);
        np_hour_start.setMaxValue(23);

        np_min_start.setMinValue(00);
        np_min_start.setMaxValue(59);

        np_hour_end.setMinValue(00);
        np_hour_end.setMaxValue(23);

        np_min_end.setMinValue(00);
        np_min_end.setMaxValue(59);

        mainLayout.setLayoutDirection(View.LAYOUT_DIRECTION_LOCALE);

        contactName.setText(location.getContact().toString());
        address.setText("Latitude: "+location.getLatitude()+" Longitude: "+location.getLongitude());
        locationName.setText(location.getLocationName().toString());
        radius.setText(location.getRadius().toString());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_marker_options, menu);
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

    public void onClick_delete_location(View view) {
    }
}
