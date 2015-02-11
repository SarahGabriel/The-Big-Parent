package com.thebigparent.sg.thebigparent.DB;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.thebigparent.sg.thebigparent.R;

import java.util.List;

/**
 * Created by Sarah on 09-Feb-15.
 */
public class MyTimeAdapter extends ArrayAdapter<String>
{
    private LayoutInflater inflater;
    //private String[] times;
    private List<String> times;

    public MyTimeAdapter(Context context, int resource, int textViewResourceId, List<String> items)
    {
        super(context, resource, textViewResourceId, items);
        this.times = items;

        inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View view = inflater.inflate(R.layout.row_time, null);

        String time = times.get(position);
        String[] parser = time.split(",");
        convertView = view;
        TextView hours = (TextView) view.findViewById(R.id.start_hour);
        TextView day = (TextView)view.findViewById(R.id.all_days);

        Log.i("parser0", parser[0]);
        Log.i("parser1", parser[1]);
       day.setText(dayToString(parser[0]));

        hours.setText(parser[1]);

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
}
