package com.thebigparent.sg.thebigparent.Activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.telephony.SmsManager;
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

        setNumberPickers();


        mainLayout.setLayoutDirection(View.LAYOUT_DIRECTION_LOCALE);

        contactName.setText(location.getContact().toString());
        address.setText("Latitude: "+location.getLatitude()+" Longitude: "+location.getLongitude());
        locationName.setText(location.getLocationName().toString());
        radius.setText(location.getRadius().toString());
    }

    private void setNumberPickers()
    {
        np_hour_start.setMinValue(00);
        np_hour_start.setMaxValue(23);

        np_min_start.setMinValue(00);
        np_min_start.setMaxValue(59);

        np_hour_end.setMinValue(00);
        np_hour_end.setMaxValue(23);

        np_min_end.setMinValue(00);
        np_min_end.setMaxValue(59);

        np_hour_start.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        np_min_start.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

        np_hour_end.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        np_min_end.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

        np_hour_start.setFormatter(new NumberPicker.Formatter()
        {
            @Override
            public String format(int value)
            {
                return String.format("%02d", value);
            }
        });
        np_hour_end.setFormatter(new NumberPicker.Formatter()
        {
            @Override
            public String format(int value)
            {
                return String.format("%02d", value);
            }
        });
        np_min_start.setFormatter(new NumberPicker.Formatter()
        {
            @Override
            public String format(int value)
            {
                return String.format("%02d", value);
            }
        });
        np_min_end.setFormatter(new NumberPicker.Formatter()
        {
            @Override
            public String format(int value)
            {
                return String.format("%02d", value);
            }
        });
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

    public void onClick_delete_location(View view)
    {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();

        // Setting Dialog Title
        alertDialog.setTitle("Delete Location");

        // Setting Dialog Message
        alertDialog.setMessage("Are you sure you want to delete this location?");
        //alertDialog.setIcon(R.drawable.ic_error);
        // Setting OK Button
        alertDialog.setButton(alertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int which)
            {
                deleteLocationAndReturn();

            }
        });

        // Showing Alert Message
        alertDialog.show();

    }

    public void  deleteLocationAndReturn()
    {
        dal_location.deleteLocation(latitude, longitude, this);
        this.finish();

    }

    private void sendSMS(String phoneNumber, String message)
    {
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, null, null);
    }

    public void onClick_add_tracking_time(View view)
    {
        Intent i = new Intent(this, AddTimeActivity.class);
        startActivity(i);

    }

    public void onClick_tracking_time(View view)
    {
        Intent i = new Intent(this, TimeActivity.class);
        startActivity(i);
    }
}
