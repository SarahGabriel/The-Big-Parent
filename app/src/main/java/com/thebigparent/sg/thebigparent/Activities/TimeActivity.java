package com.thebigparent.sg.thebigparent.Activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.thebigparent.sg.thebigparent.BL.Bl_app;
import com.thebigparent.sg.thebigparent.DB.MyTimeAdapter;
import com.thebigparent.sg.thebigparent.Dal.Dal_time;
import com.thebigparent.sg.thebigparent.R;

import java.text.ParseException;
import java.util.List;

/**
 * TimeActivity
 *
 * Activity that shows on list all tracking time of a specific marker
 */
public class TimeActivity extends ActionBarActivity implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener
{
    //final static public int REQUEST_CODE_ADD_TIME = 108;
    private ListView listView;
    private MyTimeAdapter timeAdapter;
    private Dal_time dal_time;
    private String latitude, longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time);

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);     //disable go back arrow

        dal_time = new Dal_time();
        listView = (ListView)findViewById(R.id.list_time);
//        try
//        {
//            Calendar now = Calendar.getInstance();      // get calendar
////           Get all location where
//            dal_time.getSwitchOnLocationByDateAndTime(now.get(Calendar.DAY_OF_WEEK), String.format("%02d", now.get(Calendar.HOUR_OF_DAY))+":"+String.format("%02d", now.get(Calendar.MINUTE)), this);
//        }
//        catch (ParseException e) {
//            e.printStackTrace();
//        }

        try
        {
            Bl_app.deleteExpiredNoRepeatTimes(this);        // delete expired no_repeat tracking times if needed
        }
        catch (ParseException e){e.printStackTrace();}

        Intent i = getIntent();         // get information from intent
        latitude = i.getStringExtra("latitude");
        longitude = i.getStringExtra("longitude");

        List<String> allTrackingTimes = dal_time.getAllHoursAndDayByLatLng(latitude, longitude, this);      // get all hours of tracking times

//        go to adapter in order to set the listView
        timeAdapter = new MyTimeAdapter(this, android.R.layout.activity_list_item, R.id.list_time, allTrackingTimes);
        listView.setAdapter(timeAdapter);
        listView.setOnItemClickListener(this);
        listView.setOnItemLongClickListener(this);

    }

    @Override
    protected void onResume()
    {
        super.onResume();

        List<String> allTrackingTimes = dal_time.getAllHoursAndDayByLatLng(latitude, longitude, this); // get all hours of tracking times
        try
        {
            Bl_app.deleteExpiredNoRepeatTimes(this);        // delete expired no_repeat tracking times if needed
        }
        catch (ParseException e){e.printStackTrace();}

        // set adapter to the listView
        timeAdapter = new MyTimeAdapter(this, android.R.layout.activity_list_item, R.id.list_time, allTrackingTimes);
        listView.setAdapter(timeAdapter);
        listView.setOnItemClickListener(this);
        listView.setOnItemLongClickListener(this);

    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        this.finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_time, menu);
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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {

    }


    public void onClick_menu_add_time(MenuItem item)        // on click addTime icon
    {
        Intent i = new Intent(this, AddTimeActivity.class);
        i.putExtra("latitude", latitude);
        i.putExtra("longitude", longitude);
        startActivity(i);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id)     // delete time on long click
    {

        String title = getString(R.string.alertTitle_delete_time);
        String message = getString(R.string.alertMessage_delete_time);
        final String messageToast = getString(R.string.alertToast_deleted_time);

        final TextView hours = (TextView)view.findViewById(R.id.hours);
        final TextView day = (TextView)view.findViewById(R.id.all_days);
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        // continue with delete
                        dal_time.deleteTime(latitude, longitude, hours.getText().toString(), day.getText().toString(), getApplicationContext());
                        Toast.makeText(getApplicationContext(), messageToast+ " " + hours.getText().toString() + ", " + day.getText().toString(), Toast.LENGTH_LONG).show();
                        onResume();
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

        return false;
    }

    public void onClick_menu_delete_all_times(MenuItem item)        // delete all tracking times
    {
        String title = getString(R.string.alertTitle_delete_all_times);
        String message = getString(R.string.alertMessage_delete_all_times);
        final String messageToast = getString(R.string.alertToast_all_times_deleted);

        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                        dal_time.deleteAllTimes(latitude, longitude, getBaseContext());

                        Toast.makeText(getApplicationContext(), messageToast, Toast.LENGTH_LONG).show();
                        onResume();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();

    }

    public void onClick_menu_home(MenuItem item)        // go to home page
    {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }

    public void onClick_menu_map(MenuItem item)
    {
        Intent i = new Intent(this, MapsActivity.class);        // go to map
        startActivity(i);
    }
}
