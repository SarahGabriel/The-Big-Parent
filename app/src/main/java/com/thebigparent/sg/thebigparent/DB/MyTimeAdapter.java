package com.thebigparent.sg.thebigparent.DB;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.thebigparent.sg.thebigparent.R;

/**
 * Created by Sarah on 09-Feb-15.
 */
public class MyTimeAdapter extends ArrayAdapter<String>
{
    private LayoutInflater inflater;
    private String[] times;

    public MyTimeAdapter(Context context, int resource, int textViewResourceId, String[] items)
    {
        super(context, resource, textViewResourceId, items);
        this.times = items;

        inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View view = inflater.inflate(R.layout.row_time, null);

        String time = times[position];
        String[] parser = time.split("-");

        convertView = view;
        TextView hour_start = (TextView) view.findViewById(R.id.start_hour);
        TextView hour_end = (TextView) view.findViewById(R.id.end_hour);

        hour_start.setText(parser[0]);
        hour_end.setText(parser[1]);

        return convertView;
    }
}


//    public MyTimeAdapter(Context context, Cursor c, boolean autoRequery)
//    {
//        super(context, c, autoRequery);
//        inflater = LayoutInflater.from(context);
//    }
//
//    @Override
//    public View newView(Context context, Cursor cursor, ViewGroup parent)
//    {
//        return inflater.inflate(R.layout.row_time, parent,false);
//    }
//
//    @Override
//    public void bindView(View view, Context context, Cursor cursor)
//    {
//        TextView hour_start = (TextView) view.findViewById(R.id.start_hour);
//        TextView hour_end = (TextView) view.findViewById(R.id.end_hour);
//
//        TextView allDays = (TextView)view.findViewById(R.id.all_days);
//
//        hour_start.setText(cursor.getString(0));
//        hour_end.setText(cursor.getString(1));
//        allDays.setText(cursor.getString(2));
//
//    }
//}
