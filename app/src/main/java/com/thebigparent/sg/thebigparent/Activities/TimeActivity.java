package com.thebigparent.sg.thebigparent.Activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.thebigparent.sg.thebigparent.BL.Bl_app;
import com.thebigparent.sg.thebigparent.DB.MyTimeAdapter;
import com.thebigparent.sg.thebigparent.Dal.Dal_location;
import com.thebigparent.sg.thebigparent.Dal.Dal_time;
import com.thebigparent.sg.thebigparent.R;

import java.text.ParseException;
import java.util.Calendar;
import java.util.List;

public class TimeActivity extends ActionBarActivity implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {
    final static public int REQUEST_CODE_ADD_TIME = 108;
    private ListView listView;
    private MyTimeAdapter timeAdapter;
    private Dal_time dal_time;
    private Dal_location dal_location;
    private Bl_app bl_app;

    private String latitude, longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time);
        //ActionBar actionBar = getActionBar();
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        dal_time = new Dal_time();
        dal_location = new Dal_location();

        //Date date = new Date()
        try
        {
            Calendar now = Calendar.getInstance();

            dal_time.getSwitchOnLocationByDateAndTime(now.get(Calendar.DAY_OF_WEEK), String.format("%02d", now.get(Calendar.HOUR_OF_DAY))+":"+String.format("%02d", now.get(Calendar.MINUTE)), this);
        }
        catch (ParseException e) {
            e.printStackTrace();
        }

        try
        {
            bl_app.deleteExpiredNoRepeatTimes(this);
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }
        listView = (ListView)findViewById(R.id.list_time);

        Intent i = getIntent();

        latitude = i.getStringExtra("latitude");
        longitude = i.getStringExtra("longitude");

        List<String> allHours = dal_time.getAllHoursAndDayByLatLng(latitude, longitude, this);
        timeAdapter = new MyTimeAdapter(this, android.R.layout.activity_list_item, R.id.list_time, allHours);

        listView.setAdapter(timeAdapter);
        listView.setOnItemClickListener(this);
        listView.setOnItemLongClickListener(this);

    }

    @Override
    protected void onResume()
    {
        super.onResume();

        List<String> allHours = dal_time.getAllHoursAndDayByLatLng(latitude, longitude, this);
        try
        {
            bl_app.deleteExpiredNoRepeatTimes(this);
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }
        timeAdapter = new MyTimeAdapter(this, android.R.layout.activity_list_item, R.id.list_time, allHours);
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


    public void onClick_menu_add_time(MenuItem item)
    {
        Intent i = new Intent(this, AddTimeActivity.class);
        i.putExtra("latitude", latitude);
        i.putExtra("longitude", longitude);
        startActivity(i);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id)
    {

        final TextView hours = (TextView)view.findViewById(R.id.hours);
        final TextView day = (TextView)view.findViewById(R.id.all_days);
        final LinearLayout layout = (LinearLayout)view.findViewById(R.id.layout_list_view);
        final Drawable backgroundColor = layout.getBackground();
        layout.setBackgroundColor(Color.DKGRAY);
        new AlertDialog.Builder(this)
                .setTitle("Delete Time")
                .setMessage("Are you sure you want to delete this Tracking time?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        // continue with delete
                        dal_time.deleteTime(latitude, longitude, hours.getText().toString(), day.getText().toString(), getApplicationContext());
                        Toast.makeText(getApplicationContext(), "Deleted time: " + hours.getText().toString() + ", " + day.getText().toString(), Toast.LENGTH_LONG).show();
                        onResume();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        // do nothing
                        layout.setBackground(backgroundColor);
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();

        return false;
    }

    public void onClick_menu_delete_all_times(MenuItem item)
    {
        new AlertDialog.Builder(this)
                .setTitle("Delete All Times")
                .setMessage("Are you sure you want to delete all Times? You cannot undo it!")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        // continue with delete
                        dal_time.deleteAllTimes(latitude, longitude, getBaseContext());

                        Toast.makeText(getApplicationContext(), "All times deleted", Toast.LENGTH_LONG).show();
                        onResume();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
//                        layout.setBackground(backgroundColor);
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();

    }

    public void onClick_menu_home(MenuItem item)
    {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }

    public void onClick_menu_map(MenuItem item)
    {
        Intent i = new Intent(this, MapsActivity.class);
        startActivity(i);
    }
}
