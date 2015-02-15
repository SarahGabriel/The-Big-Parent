package com.thebigparent.sg.thebigparent.DB;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Switch;
import android.widget.TextView;

import com.thebigparent.sg.thebigparent.Classes.MapLocation;
import com.thebigparent.sg.thebigparent.Dal.Dal_location;
import com.thebigparent.sg.thebigparent.Dal.Dal_time;
import com.thebigparent.sg.thebigparent.R;

import java.util.List;

/**
 * Created by Sarah on 15-Feb-15.
 */
public class MarkerAdapter extends ArrayAdapter<String>
{
    private LayoutInflater inflater;
    //private String[] times;
    private List<String> markers;
    private String[] parser, parserHours;
    private String day, latitude, longitude, hour_start, hour_end;

    Dal_time dal_time;
    Dal_location dal_location;

    public MarkerAdapter(Context context, int resource,  int textViewResourceId, List<String> items)
    {
        super(context, resource, textViewResourceId, items);
        this.markers = items;

        dal_time = new Dal_time();
        dal_location = new Dal_location();

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View view = inflater.inflate(R.layout.row_marker, null);
        convertView = view;

        String time = markers.get(position);

        parser = time.split(",");
        parserHours = parser[1].split("-");

        TextView hours_textView = (TextView) view.findViewById(R.id.hours);
        TextView day_textView = (TextView)view.findViewById(R.id.all_days);
        TextView location_textView = (TextView)view.findViewById(R.id.location);
        TextView location_lat = (TextView)view.findViewById(R.id.location_lat);
        TextView location_lng = (TextView)view.findViewById(R.id.location_lng);
        Switch switcher = (Switch)view.findViewById(R.id.switcher);



        day = parser[0].trim();
        hour_start = parserHours[0].trim();
        hour_end = parserHours[1].trim();
        latitude = parser[3].trim();
        longitude = parser[4].trim();

        Log.i("latitude_adapter", latitude);
        Log.i("longitude_adapter", longitude);

        MapLocation mapLocation = dal_location.getLocation(latitude, longitude, getContext());

        //day_textView.setText(dayToString(day));
        hours_textView.setText(parser[1]);
        location_textView.setText(mapLocation.getLocationName());
        location_lat.setText(latitude);
        location_lng.setText(longitude);

        int isSwitcherOn = Integer.parseInt(parser[2]);

        if(isSwitcherOn == 1)
        {
        switcher.setChecked(true);
        }
        else
        {
        switcher.setChecked(false);
        }

        latitude = parser[3];
        longitude = parser[4];
        return convertView;
    }

}