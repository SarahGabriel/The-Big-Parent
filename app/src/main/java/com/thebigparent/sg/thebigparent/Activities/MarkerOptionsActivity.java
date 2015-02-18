package com.thebigparent.sg.thebigparent.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.thebigparent.sg.thebigparent.Classes.MapLocation;
import com.thebigparent.sg.thebigparent.Dal.Dal_location;
import com.thebigparent.sg.thebigparent.Dal.Dal_time;
import com.thebigparent.sg.thebigparent.R;

import java.sql.SQLException;

/**
 * MarkerOptionsActivity
 *
 * Activity tha shows the details of the specific marker
 */
public class MarkerOptionsActivity extends Activity
{
    //final static public int REQUEST_CODE_ALL_TIMES = 107;

    private String latitude, longitude;
    private Dal_location dal_location;
    private Dal_time dal_time;

    private MapLocation location;

    private TextView contactName;
    private TextView radius;
    private Button allTrackingTime;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marker_options);

        Intent i = getIntent();     // get information from previous activity

        latitude = i.getStringExtra("latitude");
        longitude = i.getStringExtra("longitude");

        dal_location = new Dal_location();
        dal_time = new Dal_time();

//        find views
        TextView locationName = (TextView) findViewById(R.id.location_name);
        contactName = (TextView) findViewById(R.id.contact_name_field);
        radius = (TextView) findViewById(R.id.radius_field);
        LinearLayout mainLayout = (LinearLayout) findViewById(R.id.main_layout_marker_options);
        allTrackingTime = (Button)findViewById(R.id.all_tracking_time_button);

        mainLayout.setLayoutDirection(View.LAYOUT_DIRECTION_LOCALE);        // set direction layout if language is changed

        location = dal_location.getLocation(latitude, longitude, this);     // get location of marker

//        setting textViews using location details
        locationName.setText(location.getLocationName());
        contactName.setText(location.getContact());
        radius.setText(location.getRadius());

        if(dal_time.getAllTimeByLatLng(latitude, longitude, this).size() == 0)
        {
            allTrackingTime.setEnabled(false);      // set button disabled if no tracking time for this marker
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        if(dal_time.getAllTimeByLatLng(latitude, longitude, this).size() == 0)
        {
            allTrackingTime.setEnabled(false);      // disable button
        }
        else
        {
            allTrackingTime.setEnabled(true);       // enable button
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
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();       // create alert dialog

        // Setting Dialog Title
        alertDialog.setTitle("Delete Location");

        // Setting Dialog Message
        alertDialog.setMessage("Are you sure you want to delete this location?");
        alertDialog.setIcon(android.R.drawable.ic_dialog_alert);
        // Setting OK Button
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                try
                {
                    deleteLocationAndReturn();      // delete location if pressed OK
                }
                catch (SQLException e) {e.printStackTrace();}
            }
        });

        // Showing Alert Message
        alertDialog.show();

    }

    private void deleteLocationAndReturn() throws SQLException       // delete location
    {
        dal_location.deleteLocation(latitude, longitude, this);
        this.finish();

    }

    public void onClick_tracking_time(View view)        // on click "TRACKING TIME"
    {
        Intent i = new Intent(this, TimeActivity.class);        // go to TimeActivity
        i.putExtra("latitude", latitude);       // put information on latLng to intent
        i.putExtra("longitude", longitude);
        startActivity(i);
    }

    public void onClick_add_tracking_time(View view)
    {
        Intent i = new Intent(this, AddTimeActivity.class);
        i.putExtra("latitude", latitude);
        i.putExtra("longitude", longitude);
        startActivity(i);

    }

// todo DELETE!
//    protected void onActivityResult(int requestCode, int resultCode, Intent data)
//    {     // Called when Setting activity returns
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == REQUEST_CODE_ALL_TIMES)
//        {
//            if (resultCode == RESULT_OK)
//            {
//                latitude = data.getStringExtra("lat");
//                longitude = data.getStringExtra("lng");
//
//                location = dal_location.getLocation(latitude, longitude, this);
//
//                contactName.setText(location.getContact().toString());
//                locationName.setText(location.getLocationName().toString());
//                radius.setText(location.getRadius().toString());
//                if(dal_time.getAllTimeByLatLng(latitude, longitude, this).size() == 0)
//                {
//                    allTrackingTime.setEnabled(false);
//                }
//                else
//                {
//                    allTrackingTime.setEnabled(true);
//                }
//            }
//        }
//    }

    public void onClick_menu_delete_marker(MenuItem item)       // on click Delete Marker
    {
        new AlertDialog.Builder(this)
                .setTitle("Delete Marker")      // todo change string
                .setMessage("Are you sure you want to delete this Marker? It will also erase all your tracking time!") // todo change string
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        // continue with delete
                        try
                        {
                            dal_location.deleteLocation(latitude, longitude, getBaseContext()); // delete location
                        }
                        catch (SQLException e) { e.printStackTrace();}

                        Toast.makeText(getApplicationContext(), "Deleted marker: " + location.getLocationName(), Toast.LENGTH_LONG).show();

                        onBackPressed();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();

    }

    public void onClick_menu_home(MenuItem item)        // go to home
    {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }

    public void onClick_menu_map(MenuItem item)         // go to map
    {
        Intent i = new Intent(this, MapsActivity.class);
        startActivity(i);
    }
}