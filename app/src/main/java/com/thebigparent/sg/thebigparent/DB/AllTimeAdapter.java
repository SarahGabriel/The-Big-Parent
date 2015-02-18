package com.thebigparent.sg.thebigparent.DB;

import android.content.Context;
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
 * AllTimeAdapter
 *
 * Adapter to show in list all the tracking times
 */
public class AllTimeAdapter extends ArrayAdapter<String> implements CompoundButton.OnCheckedChangeListener      // Adapter to show all times in layout
{
    private LayoutInflater inflater;
    private List<String> times;

    Dal_time dal_time;              // For time db calls
    Dal_location dal_location;      // For location db calls


    public AllTimeAdapter(Context context, int resource,  int textViewResourceId, List<String> items) // Builder
    {
        super(context, resource, textViewResourceId, items);
        this.times = items;

        dal_time = new Dal_time();
        dal_location = new Dal_location();

        inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent)       // what happens in each row of list
    {
        View view = inflater.inflate(R.layout.row_time, null);
        convertView = view;

        String time = times.get(position);

        String[] parser = time.split(",");      // get all details in parsing the String from the adapter

        TextView hours_textView = (TextView) view.findViewById(R.id.hours);
        TextView day_textView = (TextView)view.findViewById(R.id.all_days);
        TextView location_textView = (TextView)view.findViewById(R.id.location);
        TextView location_lat = (TextView)view.findViewById(R.id.location_lat);
        TextView location_lng = (TextView)view.findViewById(R.id.location_lng);
        TextView no_repeat_textView = (TextView)view.findViewById(R.id.no_repeat_time);
        Switch switcher = (Switch)view.findViewById(R.id.switcher);

        String day = parser[0].trim();
        String latitude = parser[3].trim();
        String longitude = parser[4].trim();
        String no_repeat = parser[5].trim();

        MapLocation mapLocation = dal_location.getLocation(latitude, longitude, getContext());

            // set all the views using the parser
        day_textView.setText(dayToString(day));
        hours_textView.setText(parser[1]);
        location_textView.setText(mapLocation.getLocationName());
        location_lat.setText(latitude);
        location_lng.setText(longitude);

        if(no_repeat.equals("1"))       // if no_repeat, set view
        {
            no_repeat_textView.setText("no repeat");
        }
        else
        {
            no_repeat_textView.setText("");
        }

        int isSwitcherOn = Integer.parseInt(parser[2]);

        if(isSwitcherOn == 1)       // change switch on
        {
            switcher.setChecked(true);
        }
        else
        {
            switcher.setChecked(false);     // change switch off
        }

        switcher.setTag(view);
        switcher.setOnCheckedChangeListener(this);
        return convertView;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)      // on switcher change - from on to off or the opposite
    {
        View view = (View)buttonView.getTag();

        // getting all views from the row
        TextView location_lat_textView = (TextView)view.findViewById(R.id.location_lat);
        TextView location_lng_textView = (TextView)view.findViewById(R.id.location_lng);
        TextView day_textView = (TextView)view.findViewById(R.id.all_days);
        TextView hours_textView = (TextView)view.findViewById(R.id.hours);

        String[] parser = hours_textView.getText().toString().split("-");

        String location_lat = location_lat_textView.getText().toString().trim();
        String location_lng = location_lng_textView.getText().toString().trim();
        String day = day_textView.getText().toString().trim();
        String hour_start = parser[0].trim();
        String hour_end = parser[1].trim();

        if(isChecked) // is switched to be on
        {
            Bl_app.clearSmsPrefsIfSwitchOff(location_lat, location_lng, getContext()); // clear preferences to allow back sending message if needed

            try
            {
                dal_time.changeSwitchOn(day, hour_start, hour_end, location_lat, location_lng, getContext());   // update DB
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
        }
        else    // is switched to be off
        {

            try
            {
                dal_time.changeSwitchOff(day, hour_start, hour_end, location_lat, location_lng, getContext());      // update DB
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private String dayToString(String intDay)       // convert in day to string
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




}
