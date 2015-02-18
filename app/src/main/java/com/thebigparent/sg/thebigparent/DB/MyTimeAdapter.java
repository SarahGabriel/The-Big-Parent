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
import com.thebigparent.sg.thebigparent.Dal.Dal_time;
import com.thebigparent.sg.thebigparent.R;

import java.sql.SQLException;
import java.util.List;

/**
 * MyTimeAdapter
 *
 * Adapter to show in list all the tracking times from marker in map
 */
public class MyTimeAdapter extends ArrayAdapter<String> implements CompoundButton.OnCheckedChangeListener
{
    private LayoutInflater inflater;
    private List<String> times;
    private String latitude;
    private String longitude;

    Dal_time dal_time;

    public MyTimeAdapter(Context context, int resource, int textViewResourceId, List<String> items)
    {
        super(context, resource, textViewResourceId, items);
        this.times = items;

        dal_time = new Dal_time();
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)       // what happens in each row of the list
    {
        View view = inflater.inflate(R.layout.row_time, null);

        String time = times.get(position);
        String[] parser = time.split(",");
        convertView = view;

//        find views
        TextView hours_textView = (TextView) view.findViewById(R.id.hours);
        TextView day_textView = (TextView)view.findViewById(R.id.all_days);
        TextView no_repeat_textView = (TextView)view.findViewById(R.id.no_repeat_time);
        Switch switcher = (Switch)view.findViewById(R.id.switcher);

        String day = parser[0].trim();
        latitude = parser[3].trim();
        longitude = parser[4].trim();
        String no_repeat = parser[5].trim();

//        set views
        day_textView.setText(dayToString(day));
        hours_textView.setText(parser[1]);

        if(no_repeat.equals("1"))
        {
            no_repeat_textView.setText("no repeat");
        }
        else
        {
            no_repeat_textView.setText("");
        }

        int isSwitcherOn = Integer.parseInt(parser[2]);
        if(isSwitcherOn == 1)
        {
            switcher.setChecked(true);      // set switch on
        }
        else
        {
            switcher.setChecked(false);     // set switch off
        }
        latitude = parser[3];
        longitude = parser[4];

        switcher.setTag(view);
        switcher.setOnCheckedChangeListener(this);
        return convertView;
    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)      // when switch is changed
    {
        View view = (View)buttonView.getTag();

        TextView day_textView = (TextView)view.findViewById(R.id.all_days);
        TextView hours_textView = (TextView)view.findViewById(R.id.hours);

        String[] parser = hours_textView.getText().toString().split("-");

        String day = day_textView.getText().toString().trim();
        String hour_start = parser[0].trim();
        String hour_end = parser[1].trim();


        if(isChecked)
        {
            Bl_app.clearSmsPrefsIfSwitchOff(latitude, longitude, getContext());
            try {
                dal_time.changeSwitchOn(day, hour_start, hour_end, latitude, longitude, getContext());
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        else
        {
            try {
                dal_time.changeSwitchOff(day, hour_start, hour_end, latitude, longitude, getContext());
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
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

}
