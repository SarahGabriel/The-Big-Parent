package com.thebigparent.sg.thebigparent.Activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.thebigparent.sg.thebigparent.DB.MarkerAdapter;
import com.thebigparent.sg.thebigparent.Dal.Dal_location;
import com.thebigparent.sg.thebigparent.Dal.Dal_time;
import com.thebigparent.sg.thebigparent.R;

import java.sql.SQLException;
import java.util.List;

public class AllMarkerActivity extends ActionBarActivity implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener
{
    private Dal_time dal_time;
    private Dal_location dal_location;
    private MarkerAdapter markerAdapter;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_marker);

//      DAL - to have access to the DB
        dal_time = new Dal_time();
        dal_location = new Dal_location();

//        List view of all the markers
        listView = (ListView)findViewById(R.id.all_marker_listView);

//        Get all markers from DB
        List<String> allMarkers = dal_location.getAllMarkers(this);

//          Create the markerAdapter to put marker on each row of the list
        markerAdapter = new MarkerAdapter(this, android.R.layout.activity_list_item, R.id.all_marker_listView, allMarkers);

//          Call markerAdapter for the list
        listView.setAdapter(markerAdapter);
        listView.setOnItemClickListener(this);
        listView.setOnItemLongClickListener(this);

    }

    @Override
    protected void onResume()
    {
        super.onResume();

        //        List view of all the markers
        listView = (ListView)findViewById(R.id.all_marker_listView);

//        Get all markers from DB
        List<String> allMarkers = dal_location.getAllMarkers(this);

//          Create the markerAdapter to put marker on each row of the list
        markerAdapter = new MarkerAdapter(this, android.R.layout.activity_list_item, R.id.all_marker_listView, allMarkers);

//          Call markerAdapter for the list
        listView.setAdapter(markerAdapter);
        listView.setOnItemClickListener(this);
        listView.setOnItemLongClickListener(this);
        
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_all_marker, menu);
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
// onItemClick go to marker option activity
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        Intent i = new Intent(parent.getContext(), MarkerOptionsActivity.class);
        TextView lat = (TextView)view.findViewById(R.id.lat_marker);
        TextView lng = (TextView)view.findViewById(R.id.lng_marker);
        Log.i("latitude", lat.getText().toString());
        Log.i("longitude", lng.getText().toString());
        i.putExtra("latitude", lat.getText().toString());
        i.putExtra("longitude", lng.getText().toString());
        startActivity(i);
    }

    @Override
//    onItemLongClick delete marker and all its tracking times
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id)
    {

        final TextView latitude = (TextView)view.findViewById(R.id.lat_marker);
        final TextView longitude = (TextView)view.findViewById(R.id.lng_marker);
        final TextView location = (TextView)view.findViewById(R.id.location_marker);

//        final Drawable backgroundColor = layout.getBackground();
//        layout.setBackgroundColor(Color.DKGRAY);
        new AlertDialog.Builder(this)
                .setTitle("Delete Marker")
                .setMessage("Are you sure you want to delete this Marker? It will also erase all your tracking time!")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        // continue with delete
                        try {
                            dal_location.deleteLocation(latitude.getText().toString(), longitude.getText().toString(), getBaseContext());
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        Toast.makeText(getApplicationContext(), "Deleted marker: " + location.getText().toString(), Toast.LENGTH_LONG).show();
                        onResume();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {
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
