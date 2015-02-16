package com.thebigparent.sg.thebigparent.DB;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.thebigparent.sg.thebigparent.BL.Bl_app;
import com.thebigparent.sg.thebigparent.Classes.MapLocation;
import com.thebigparent.sg.thebigparent.Dal.Dal_location;
import com.thebigparent.sg.thebigparent.Dal.Dal_time;
import com.thebigparent.sg.thebigparent.R;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by Sarah on 12-Feb-15.
 */
public class AllTimeAdapter extends ArrayAdapter<String> implements CompoundButton.OnCheckedChangeListener
{
    private LayoutInflater inflater;
    //private String[] times;
    private List<String> times;
    private String[] parser, parserHours;
    private String day, latitude, longitude, hour_start, hour_end;

    Dal_time dal_time;
    Dal_location dal_location;

    public AllTimeAdapter(Context context, int resource,  int textViewResourceId, List<String> items)
    {
        super(context, resource, textViewResourceId, items);
        this.times = items;

        dal_time = new Dal_time();
        dal_location = new Dal_location();

        inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View view = inflater.inflate(R.layout.row_time, null);
        convertView = view;

        String time = times.get(position);

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

        day_textView.setText(dayToString(day));
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
        switcher.setOnCheckedChangeListener(this);
        return convertView;
    }

    private String dayToString(String intDay)
    {

        int int_day = Integer.parseInt(intDay);
        String day = "";
        switch (int_day) {
            case 1:
                day = "Sunday";break;
            case 2:
                day = "Monday";break;
            case 3:
                day = "Tuesday";break;
            case 4:
                day = "Wednesday";break;
            case 5:
                day = "Thursday";break;
            case 6:
                day = "Friday";break;
            case 7:
                day = "Saturday";break;
        }
        return day;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
    {

        View viewParentRoot = buttonView.getRootView();
        TextView location_name_textView = (TextView)viewParentRoot.findViewById(R.id.location);
        TextView location_lat_textView = (TextView)viewParentRoot.findViewById(R.id.location_lat);
        TextView location_lng_textView = (TextView)viewParentRoot.findViewById(R.id.location_lng);
        TextView day_textView = (TextView)viewParentRoot.findViewById(R.id.all_days);
        TextView hours_textView = (TextView)viewParentRoot.findViewById(R.id.hours);

        String[] parser = hours_textView.getText().toString().split("-");

        String location_name = location_name_textView.getText().toString().trim();
        String location_lat = location_lat_textView.getText().toString().trim();
        String location_lng = location_lng_textView.getText().toString().trim();
        String day = day_textView.getText().toString().trim();
        String hour_start = parser[0].trim();
        String hour_end = parser[1].trim();


        if(isChecked)      // if turn off clear sms prefs
        {
            Bl_app.clearSmsPrefsIfSwitchOff(location_lat, location_lng, getContext());
        }



        Log.i("ON CHECK", location_name);
        Log.i("ON CHECK latitude", location_lat);
        Log.i("ON CHECK longitude", location_lng);
        Log.i("ON CHECK day", day);
        Log.i("ON CHECK hour_start", hour_start);
        Log.i("ON CHECK hour_end", hour_end);

        if(isChecked)
        {
            Log.i("SWITCH ON", location_name);
            Log.i("SWITCH CONTEXT", viewParentRoot.getContext().toString());
            try
            {
                dal_time.changeSwitchOn(day, hour_start, hour_end, location_lat, location_lng, getContext());
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
        }
        else
        {
            Log.i("SWITCH OFF", location_name);
            Log.i("SWITCH CONTEXT", viewParentRoot.getContext().toString());
            try
            {
                dal_time.changeSwitchOff(day, hour_start, hour_end, location_lat, location_lng, getContext());
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


}
