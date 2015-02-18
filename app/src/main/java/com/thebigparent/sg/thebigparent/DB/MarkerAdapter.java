package com.thebigparent.sg.thebigparent.DB;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.thebigparent.sg.thebigparent.Dal.Dal_location;
import com.thebigparent.sg.thebigparent.Dal.Dal_time;
import com.thebigparent.sg.thebigparent.R;

import java.util.List;

/**
 * MarkerAdapter
 *
 * Adapter to show in list all the markers in map
 */
public class MarkerAdapter extends ArrayAdapter<String>
{
    private LayoutInflater inflater;
    private List<String> markers;

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
    public View getView(int position, View convertView, ViewGroup parent)       // what happens in each row of the list
    {
        View view = inflater.inflate(R.layout.row_marker, null);
        convertView = view;

        String marker = markers.get(position);

        String[] parser = marker.split(",");

//        Getting all text view
        TextView id_marker_textView = (TextView) view.findViewById(R.id.id_marker_field);
        TextView location_marker_textView = (TextView)view.findViewById(R.id.location_marker);
        TextView contact_marker_textView = (TextView)view.findViewById(R.id.contact_marker);
        TextView lat_marker_textView = (TextView)view.findViewById(R.id.lat_marker);
        TextView lng_marker_textView = (TextView)view.findViewById(R.id.lng_marker);

//        Getting all fields content
        String id_marker = parser[0].trim();
        String lat_marker = parser[1].trim();
        String lng_marker = parser[2].trim();
        String location_marker = parser[3].trim();
        String contact_marker = parser[4].trim();

//        Filling all textView with fields content
        id_marker_textView.setText(id_marker);
        lat_marker_textView.setText(lat_marker);
        lng_marker_textView.setText(lng_marker);
        location_marker_textView.setText(location_marker);
        contact_marker_textView.setText(contact_marker);

        return convertView;
    }

}