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

import com.thebigparent.sg.thebigparent.Dal.Dal_time;
import com.thebigparent.sg.thebigparent.R;

import java.util.List;

/**
 * Created by Sarah on 09-Feb-15.
 */
public class MyTimeAdapter extends ArrayAdapter<String> implements CompoundButton.OnCheckedChangeListener {
    private LayoutInflater inflater;
    //private String[] times;
    private List<String> times;
    private String[] parser, parserHours;
    private String day, latitude, longitude, hour_start, hour_end;

    Dal_time dal_time;

    public MyTimeAdapter(Context context, int resource, int textViewResourceId, List<String> items)
    {
        super(context, resource, textViewResourceId, items);
        this.times = items;

        dal_time = new Dal_time();
        inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View view = inflater.inflate(R.layout.row_time, null);

        String time = times.get(position);
        parser = time.split(",");
        convertView = view;

        TextView hours_textView = (TextView) view.findViewById(R.id.start_hour);
        TextView day_textView = (TextView)view.findViewById(R.id.all_days);
        Switch switcher = (Switch)view.findViewById(R.id.switcher);

        switcher.setOnCheckedChangeListener(this);
        parserHours = parser[1].split("-");

        day = parser[0].trim();
        hour_start = parserHours[0].trim();
        hour_end = parserHours[1].trim();
        latitude = parser[3].trim();
        longitude = parser[4].trim();


//        Log.i("parser0", parser[0]);
//        Log.i("parser1", parser[1]);
        day_textView.setText(dayToString(day));
        hours_textView.setText(parser[1]);

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
        if(isChecked)
        {
            dal_time.changeSwitchOn(day, hour_start, hour_end, latitude, longitude, getContext());
        }
        else
        {
            dal_time.changeSwitchOff(day, hour_start, hour_end, latitude, longitude, getContext());
        }
    }
}
