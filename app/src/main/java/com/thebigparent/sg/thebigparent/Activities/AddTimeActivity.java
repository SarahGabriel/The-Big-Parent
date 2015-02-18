package com.thebigparent.sg.thebigparent.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.NumberPicker;

import com.thebigparent.sg.thebigparent.Classes.AlertDialogManager;
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

/**
 * Class AddTimeActivity
 *
 * Add tracking time to specific marker in map
 */
public class AddTimeActivity extends ActionBarActivity implements CompoundButton.OnCheckedChangeListener
{
    private String latitude, longitude;     // LatLng of the marker
    private AlertDialogManager alertDialogManager;  // Make Alert Dialog
    private CheckBox checkBox_noRepeat, checkBox_sunday, checkBox_monday, checkBox_tuesday, checkBox_wednesday, checkBox_thursday, checkBox_friday, checkBox_saturday;
    private NumberPicker np_hour_start, np_min_start, np_hour_end, np_min_end;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_time);

        alertDialogManager = new AlertDialogManager();

//        Get intent and its information from the previous activity
        Intent i = getIntent();

        latitude = i.getStringExtra("latitude");
        longitude = i.getStringExtra("longitude");

//        Get views of the activity
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

        setNumberPickers();     // set values of number picker

        if(checkBox_noRepeat.isChecked())           // set checkboxes disabled if needs
        {
            checkBox_sunday.setEnabled(false);
            checkBox_monday.setEnabled(false);
            checkBox_tuesday.setEnabled(false);
            checkBox_wednesday.setEnabled(false);
            checkBox_thursday.setEnabled(false);
            checkBox_friday.setEnabled(false);
            checkBox_saturday.setEnabled(false);
        }

        checkBox_noRepeat.setOnCheckedChangeListener(this);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_time, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

//  On button click - add tracking time to DB
    public void onClick_add_tracking_time(View view) throws ParseException
    {
        Dal_time dal_time = new Dal_time();         // reference to all functions that handle the DB

        String hour_start, hour_end, hour_hour_start, hour_min_start, hour_hour_end, hour_min_end;

//        Get numbers from the numberPickers
        hour_hour_start = String.format("%02d", np_hour_start.getValue());
        hour_min_start = String.format("%02d", np_min_start.getValue());
        hour_hour_end = String.format("%02d", np_hour_end.getValue());
        hour_min_end = String.format("%02d", np_min_end.getValue());

//        Making hours from the numbers of numberPickers
        hour_start = hour_hour_start + ":" + hour_min_start;
        hour_end = hour_hour_end + ":" + hour_min_end;

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        try
        {
//            Convert hours from String type to Date type
            Date date_start = sdf.parse(hour_start);
            Date date_end = sdf.parse(hour_end);

//            Check if the hours are correct (Begin hour < End hour)
            if(isDateInOrder(date_start,  date_end))
            {
                DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");     // format date
                Date today = new Date();        // day of today
                String date = dateFormat.format(today);     // formatting date using format

//                if no_repeat checkBox is checked
                if(checkBox_noRepeat.isChecked())
                {
                    Calendar calendar = GregorianCalendar.getInstance();        // get calendar

                    int day = calendar.get(Calendar.DAY_OF_WEEK);  // get day of today - if current day is Sunday, day=1. Saturday, day=7.

                    int hour_now = calendar.get(Calendar.HOUR_OF_DAY);     // get current hour
                    int minute_now = calendar.get(Calendar.MINUTE);
                    String hourOfDay = String.format("%02d", hour_now) + ":" + String.format("%02d", minute_now);
                    Date current_hour = sdf.parse(hourOfDay);    // parse hour to be Date type

//                    Checking that the hours haven't passed yet
                    if(date_end.before(current_hour))
                    {

                        String title = getString(R.string.alertTitle_wrong_tracking_hours);
                        String message = getString(R.string.alertMessage_hours_passed);
                        alertDialogManager.showAlertDialog(this, title, message);
                    }
                    else
                    {
//                        Adding new tracking time
                        Time time = new Time(day, hour_start, hour_end, latitude, longitude, date, 1, 1);
                        if (dal_time.addNewTime(time, this))
                        {
                            this.finish();      // go back to previous activity if succeed
                        }
                    }
                }
                else
                { //                if no_repeat checkBox is not checked

                    List<Time> addRepeatTime = new ArrayList<>();       // list of times - if more than one day is checked
                    /**
                     * Class Time = tracking time
                     * @param day - type:int, day of time in integer - 1 for Sunday, 2 for Monday...
                     * @param hour_start - hour start of time
                     * @param hour_end - hour end of time
                     * @param latitude - latitude of time
                     * @param longitude - longitude of time
                     * @param date - current date
                     * @param noRepeat - if no_repeat checkbox is checked - 0:false, 1:true
                     * @param isSwitchOn - if time is on - if switcher is checked - 0:false, 1:true
                     *
                     */
                    if(checkBox_sunday.isChecked())
                    {
                        Time time = new Time(1, hour_start, hour_end, latitude, longitude, date, 0, 1);     // create new tracking time
                        addRepeatTime.add(time);    // add time to list
                    }
                    if(checkBox_monday.isChecked())
                    {
                        Time time = new Time(2, hour_start, hour_end, latitude, longitude, date, 0, 1);
                        addRepeatTime.add(time);
                    }
                    if(checkBox_tuesday.isChecked())
                    {
                        Time time = new Time(3, hour_start, hour_end, latitude, longitude, date, 0, 1);
                        addRepeatTime.add(time);
                    }
                    if(checkBox_wednesday.isChecked())
                    {
                        Time time = new Time(4, hour_start, hour_end, latitude, longitude, date, 0, 1);
                        addRepeatTime.add(time);
                    }
                    if(checkBox_thursday.isChecked())
                    {
                        Time time = new Time(5, hour_start, hour_end, latitude, longitude, date, 0, 1);
                        addRepeatTime.add(time);
                    }
                    if(checkBox_friday.isChecked())
                    {
                        Time time = new Time(6, hour_start, hour_end, latitude, longitude, date, 0, 1);
                        addRepeatTime.add(time);
                    }
                    if(checkBox_saturday.isChecked())
                    {
                        Time time = new Time(7, hour_start, hour_end, latitude, longitude, date, 0, 1);
                        addRepeatTime.add(time);
                    }
                    if(addRepeatTime.size() == 0)
                    {
                        //empty fields error
                        String title = getString(R.string.alertTitle_empty_fields);
                        String message = getString(R.string.alertMessage_empty_days);
                        alertDialogManager.showAlertDialog(this, title, message);
                    }
                    else
                    {
                        boolean wasError = false;
                        for (Time eachTime : addRepeatTime)
                        {
                            if (!dal_time.addNewTime(eachTime, this))
                            {
                                wasError = true;
                            }
                        }
                        if (!wasError) {
                            this.finish();
                        }
                    }
                }
            }
            else
            {
                //Start tracking hour needs to be before end tracking hour - error
                String title = getString(R.string.alertTitle_wrong_tracking_hours);
                String message = getString(R.string.alertMessage_wrong_tracking_hours);
                alertDialogManager.showAlertDialog(this, title, message);
            }
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }

    }



    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
    {
        if (isChecked)
        {
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



    //    Private function - sets numbers of the numberPicker
    private void setNumberPickers()
    {
        np_hour_start.setMinValue(0);
        np_hour_start.setMaxValue(23);

        np_min_start.setMinValue(0);
        np_min_start.setMaxValue(59);

        np_hour_end.setMinValue(0);
        np_hour_end.setMaxValue(23);

        np_min_end.setMinValue(0);
        np_min_end.setMaxValue(59);

        np_hour_start.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        np_min_start.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

        np_hour_end.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        np_min_end.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

//        Set formatters so the numbers will appear with 2 digits: 1 = 01, 2 = 02 ...
        np_hour_start.setFormatter(new NumberPicker.Formatter()
        {
            @Override
            public String format(int value) {
                return String.format("%02d", value);
            }
        });
        np_hour_end.setFormatter(new NumberPicker.Formatter()
        {
            @Override
            public String format(int value) {
                return String.format("%02d", value);
            }
        });
        np_min_start.setFormatter(new NumberPicker.Formatter()
        {
            @Override
            public String format(int value) {
                return String.format("%02d", value);
            }
        });
        np_min_end.setFormatter(new NumberPicker.Formatter()
        {
            @Override
            public String format(int value) {
                return String.format("%02d", value);
            }
        });
    }


    //    check if start hour is before end hour
    private boolean isDateInOrder(Date date_start, Date date_end)
    {
        return !date_start.after(date_end);
    }
}
