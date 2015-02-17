package com.thebigparent.sg.thebigparent.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.thebigparent.sg.thebigparent.BL.Bl_app;
import com.thebigparent.sg.thebigparent.DB.AllTimeAdapter;
import com.thebigparent.sg.thebigparent.Dal.Dal_location;
import com.thebigparent.sg.thebigparent.Dal.Dal_time;
import com.thebigparent.sg.thebigparent.R;

import java.text.ParseException;
import java.util.List;

public class AllTrackingTimeActivity extends Activity implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener
{
    private AllTimeAdapter timeAdapter;
    private ListView listView;
    private Dal_time dal_time;
    private Dal_location dal_location;
    private Bl_app bl_app;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_tracking_time);
        dal_time = new Dal_time();
        dal_location = new Dal_location();
        listView = (ListView)findViewById(R.id.all_tracking_time);
        try
        {
            bl_app.deleteExpiredNoRepeatTimes(this);
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }
        List<String> allTimes = dal_time.getAllTime(this);

        timeAdapter = new AllTimeAdapter(this, android.R.layout.activity_list_item, R.id.all_tracking_time, allTimes);

        listView.setAdapter(timeAdapter);
        listView.setOnItemClickListener(this);
        listView.setOnItemLongClickListener(this);

    }

    @Override
    protected void onResume()
    {
        super.onResume();

        List<String> allTimes = dal_time.getAllTime(this);
        try
        {
            bl_app.deleteExpiredNoRepeatTimes(this);
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }
        timeAdapter = new AllTimeAdapter(this, android.R.layout.activity_list_item, R.id.all_tracking_time, allTimes);

        listView.setAdapter(timeAdapter);
        listView.setOnItemClickListener(this);
        listView.setOnItemLongClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_all_tracking_time, menu);
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
        Intent i = new Intent(parent.getContext(), MarkerOptionsActivity.class);
        TextView lat = (TextView)view.findViewById(R.id.location_lat);
        TextView lng = (TextView)view.findViewById(R.id.location_lng);
        Log.i("latitude", lat.getText().toString());
        Log.i("longitude", lng.getText().toString());
        i.putExtra("latitude", lat.getText().toString());
        i.putExtra("longitude", lng.getText().toString());
        startActivity(i);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id)
    {
        final TextView latitude = (TextView)view.findViewById(R.id.location_lat);
        final TextView longitude = (TextView)view.findViewById(R.id.location_lng);
        final TextView location = (TextView)view.findViewById(R.id.location);
        final TextView hours = (TextView)view.findViewById(R.id.hours);
        final TextView day = (TextView)view.findViewById(R.id.all_days);

        new AlertDialog.Builder(this)
                .setTitle("Delete Time")
                .setMessage("Are you sure you want to delete this tracking time?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        // continue with delete
                        dal_time.deleteTime(latitude.getText().toString(), longitude.getText().toString(), hours.getText().toString(), day.getText().toString(), getBaseContext());

                        Toast.makeText(getApplicationContext(), "Deleted time: " + location.getText().toString() + ": " + hours.getText().toString(), Toast.LENGTH_LONG).show();
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

        return true;
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
