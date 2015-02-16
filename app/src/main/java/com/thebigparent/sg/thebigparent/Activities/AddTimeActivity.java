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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.Switch;

import com.thebigparent.sg.thebigparent.Classes.Time;
import com.thebigparent.sg.thebigparent.Dal.Dal_time;
import com.thebigparent.sg.thebigparent.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class AddTimeActivity extends ActionBarActivity// implements CompoundButton.OnCheckedChangeListener
{
    private String latitude, longitude;

    private LinearLayout linearNoRepeat, linearDaysOfWeek;
    private CheckBox checkBox_noRepeat, checkBox_sunday, checkBox_monday, checkBox_tuesday, checkBox_wednesday, checkBox_thursday, checkBox_friday, checkBox_saturday;
    private NumberPicker np_hour_start, np_min_start, np_hour_end, np_min_end;
    private Switch switcher;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_time);

        Intent i = getIntent();

        latitude = i.getStringExtra("latitude");
        longitude = i.getStringExtra("longitude");
        Log.i("Latitude", latitude);
        Log.i("Longitude", longitude);
        linearNoRepeat = (LinearLayout)findViewById(R.id.linear_no_repeat);
        linearDaysOfWeek = (LinearLayout)findViewById(R.id.linear_days_of_week);
        checkBox_noRepeat = (CheckBox)findViewById(R.id.no_repeat);
        checkBox_sunday = (CheckBox)findViewById(R.id.sunday);
        checkBox_monday = (CheckBox)findViewById(R.id.monday);
        checkBox_tuesday = (CheckBox)findViewById(R.id.tuesday);
        checkBox_wednesday = (CheckBox)findViewById(R.id.wednesday);
        checkBox_thursday = (CheckBox)findViewById(R.id.thursday);
        checkBox_friday = (CheckBox)findViewById(R.id.friday);
        checkBox_saturday = (CheckBox)findViewById(R.id.saturday);
        np_hour_start = (NumberPicker) findViewById(R.id.number_picker_hour_start);
        np_min_start = (NumberPicker) findViewById(R.id.number_picker_min_start);
        np_hour_end = (NumberPicker) findViewById(R.id.number_picker_hour_end);
        np_min_end = (NumberPicker) findViewById(R.id.number_picker_min_end);
        switcher = (Switch)findViewById(R.id.switcher);

        setNumberPickers();

        if(checkBox_noRepeat.isChecked())
        {
            checkBox_sunday.setEnabled(false);
            checkBox_monday.setEnabled(false);
            checkBox_tuesday.setEnabled(false);
            checkBox_wednesday.setEnabled(false);
            checkBox_thursday.setEnabled(false);
            checkBox_friday.setEnabled(false);
            checkBox_saturday.setEnabled(false);
        }

        //checkBox_noRepeat.onCheckedChanged();
        checkBox_noRepeat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if ( isChecked )
                {
                    // perform logic
                    checkBox_sunday.setEnabled(false);
                    checkBox_monday.setEnabled(false);
                    checkBox_tuesday.setEnabled(false);
                    checkBox_wednesday.setEnabled(false);
                    checkBox_thursday.setEnabled(false);
                    checkBox_friday.setEnabled(false);
                    checkBox_saturday.setEnabled(false);
                }
                if(!isChecked)
                {
                    checkBox_sunday.setEnabled(true);
                    checkBox_monday.setEnabled(true);
                    checkBox_tuesday.setEnabled(true);
                    checkBox_wednesday.setEnabled(true);
                    checkBox_thursday.setEnabled(true);
                    checkBox_friday.setEnabled(true);
                    checkBox_saturday.setEnabled(true);
                }

            }
        });

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_time, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setNumberPickers()
    {
        np_hour_start.setMinValue(00);
        np_hour_start.setMaxValue(23);

        np_min_start.setMinValue(00);
        np_min_start.setMaxValue(59);

        np_hour_end.setMinValue(00);
        np_hour_end.setMaxValue(23);

        np_min_end.setMinValue(00);
        np_min_end.setMaxValue(59);

        np_hour_start.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        np_min_start.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

        np_hour_end.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        np_min_end.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

        np_hour_start.setFormatter(new NumberPicker.Formatter() {
            @Override
            public String format(int value) {
                return String.format("%02d", value);
            }
        });
        np_hour_end.setFormatter(new NumberPicker.Formatter() {
            @Override
            public String format(int value) {
                return String.format("%02d", value);
            }
        });
        np_min_start.setFormatter(new NumberPicker.Formatter() {
            @Override
            public String format(int value) {
                return String.format("%02d", value);
            }
        });
        np_min_end.setFormatter(new NumberPicker.Formatter() {
            @Override
            public String format(int value) {
                return String.format("%02d", value);
            }
        });
    }

    public void onClick_add_tracking_time(View view)
    {
        Dal_time dal_time = new Dal_time();
        int day;
        String hour_start, hour_end, hour_hour_start, hour_min_start, hour_hour_end, hour_min_end;
        int no_repeat;

        hour_hour_start = String.format("%02d", np_hour_start.getValue());
        hour_min_start = String.format("%02d", np_min_start.getValue());
        hour_hour_end = String.format("%02d", np_hour_end.getValue());
        hour_min_end = String.format("%02d", np_min_end.getValue());

        hour_start = hour_hour_start + ":" + hour_min_start;
        hour_end = hour_hour_end + ":" + hour_min_end;

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        try
        {
            Date date_start = sdf.parse(hour_start);
            Date date_end = sdf.parse(hour_end);
            Log.i("Hours", date_start.toString() + " - " + date_end.toString());

            if(isDateInOrder(date_start,  date_end))
            {
                DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                Date today = new Date();        // day of today
                String date = dateFormat.format(today);     // formatting date

                Log.i("FORMAT DATE", date);
                if(checkBox_noRepeat.isChecked())
                {
                    Calendar calendar = GregorianCalendar.getInstance();
                    day = calendar.get(Calendar.DAY_OF_WEEK);  // If current day is Sunday, day=1. Saturday, day=7.

                    Time time = new Time(day, hour_start, hour_end, latitude, longitude, date ,1, 1);
                    Log.w("Time_noRepeat", time.toString());
                    if(dal_time.addNewTime(time, this))
                    {
                        this.finish();
                    }
                }
                else
                {
                    List<Time> addRepeatTime = new ArrayList<Time>();
                    if(checkBox_sunday.isChecked())
                    {
                        Time time = new Time(1, hour_start, hour_end, latitude, longitude, date, 0, 1);
                        Log.w("Time_sunday", time.toString());
                        addRepeatTime.add(time);
                    }
                    if(checkBox_monday.isChecked())
                    {
                        Time time = new Time(2, hour_start, hour_end, latitude, longitude, date, 0, 1);
                        Log.w("Time_monday", time.toString());
                        addRepeatTime.add(time);
                    }
                    if(checkBox_tuesday.isChecked())
                    {
                        Time time = new Time(3, hour_start, hour_end, latitude, longitude, date, 0, 1);
                        Log.w("Time_tuesday", time.toString());
                        addRepeatTime.add(time);
                    }
                    if(checkBox_wednesday.isChecked())
                    {
                        Time time = new Time(4, hour_start, hour_end, latitude, longitude, date, 0, 1);
                        Log.w("Time_wednesday", time.toString());
                        addRepeatTime.add(time);
                    }
                    if(checkBox_thursday.isChecked())
                    {
                        Time time = new Time(5, hour_start, hour_end, latitude, longitude, date, 0, 1);
                        Log.w("Time_thursday", time.toString());
                        addRepeatTime.add(time);
                    }
                    if(checkBox_friday.isChecked())
                    {
                        Time time = new Time(6, hour_start, hour_end, latitude, longitude, date, 0, 1);
                        Log.w("Time_friday", time.toString());
                        addRepeatTime.add(time);
                    }
                    if(checkBox_saturday.isChecked())
                    {
                        Time time = new Time(7, hour_start, hour_end, latitude, longitude, date, 0, 1);
                        Log.w("Time_saturday", time.toString());
                        addRepeatTime.add(time);
                    }
                    boolean wasError = false;
                    for(Time eachTime : addRepeatTime)
                    {
                        if(!dal_time.addNewTime(eachTime, this))
                        {
                            wasError = true;
                        }
                    }
                    if(!wasError)
                    {
                        this.finish();
                    }
                }
                List<Time> times = dal_time.getAllTimeByLatLng(latitude, longitude, this);
            }
            else
            {
                new AlertDialog.Builder(this)
                        .setTitle("Wrong Tracking Hours")
                        .setMessage("Start Hour needs to be before End Hour")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which)
                            {
                                // continue with delete
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }

    }

    private boolean isDateInOrder(Date date_start, Date date_end)
    {
        if(date_start.after(date_end))
        {
            return false;
        }
        return true;
    }


}
