package com.thebigparent.sg.thebigparent.Activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.thebigparent.sg.thebigparent.Classes.MapLocation;
import com.thebigparent.sg.thebigparent.Dal.Dal_location;
import com.thebigparent.sg.thebigparent.R;

public class MarkerOptionsActivity extends ActionBarActivity
{
    final static public int REQUEST_CODE_ALL_TIMES = 107;

    private String latitude, longitude;
    private Dal_location dal_location;

    private boolean isActivityBack = false;
    private MapLocation location;

    private TextView contactName, address, locationName, radius;
    private LinearLayout mainLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marker_options);

        Class callerClass;
        Intent i = getIntent();

        latitude = i.getStringExtra("latitude");
        longitude = i.getStringExtra("longitude");
        String caller = i.getStringExtra("caller");
        Log.i("caller", caller);
       // callerClass = Class.forName(caller);
//        try
//        {
//
//        }
//        catch (ClassNotFoundException e)
//        {
//            callerClass = this.getClass();
//            e.printStackTrace();
//        }

        dal_location = new Dal_location();



        locationName = (TextView) findViewById(R.id.location_name);
       // address = (TextView) findViewById(R.id.address_field);
        contactName = (TextView) findViewById(R.id.contact_name_field);
        radius = (TextView) findViewById(R.id.radius_field);
        mainLayout = (LinearLayout) findViewById(R.id.main_layout_marker_options);



        mainLayout.setLayoutDirection(View.LAYOUT_DIRECTION_LOCALE);

        Log.w("MapsActivity", MapsActivity.class.getName().trim().length()+" " + MapsActivity.class.getName().trim());
        Log.w("Caller", caller.trim().length() + " " +caller.trim());
        if(caller.trim().equals(MapsActivity.class.getName().trim()))
        {
            location = dal_location.getLocation(latitude, longitude, this);
            Log.i("equals", "TRUE");
            //contactName.setText(location.getContact().toString());
            locationName.setText(location.getLocationName().toString());
            //address.setText("Latitude: " + location.getLatitude() + " Longitude: " + location.getLongitude());
            //locationName.setText(location.getLocationName().toString());
            contactName.setText(location.getContact().toString());
            radius.setText(location.getRadius().toString());
        }

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_marker_options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
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
        alertDialog.setButton(alertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                deleteLocationAndReturn();

            }
        });

        // Showing Alert Message
        alertDialog.show();

    }

    public void deleteLocationAndReturn()
    {
        dal_location.deleteLocation(latitude, longitude, this);
        this.finish();

    }

    private void sendSMS(String phoneNumber, String message)
    {
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, null, null);
    }

    public void onClick_tracking_time(View view)
    {
        Intent i = new Intent(this, TimeActivity.class);

        i.putExtra("latitude", latitude);
        i.putExtra("longitude", longitude);
        isActivityBack = true;
        //startActivityForResult(i, REQUEST_CODE_ALL_TIMES);
        startActivity(i);
    }

    public void onClick_add_tracking_time(View view)
    {
        Intent i = new Intent(this, AddTimeActivity.class);
        i.putExtra("latitude", latitude);
        i.putExtra("longitude", longitude);
        startActivity(i);

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {     // Called when Setting activity returns
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_ALL_TIMES)
        {
//            if (resultCode == RESULT_OK)
//            {
                latitude = data.getStringExtra("lat");
                longitude = data.getStringExtra("lng");

                location = dal_location.getLocation(latitude, longitude, this);

                contactName.setText(location.getContact().toString());
                address.setText("Latitude: " + location.getLatitude() + " Longitude: " + location.getLongitude());
                locationName.setText(location.getLocationName().toString());
                radius.setText(location.getRadius().toString());

                // backFromActivity = true;            // Flag returned from Settings activity
       //     }
        }
    }
}