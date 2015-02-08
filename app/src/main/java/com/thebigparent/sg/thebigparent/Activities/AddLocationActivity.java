package com.thebigparent.sg.thebigparent.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.thebigparent.sg.thebigparent.Classes.MapLocation;
import com.thebigparent.sg.thebigparent.Dal.Dal_location;
import com.thebigparent.sg.thebigparent.R;

import java.util.Locale;

public class AddLocationActivity extends ActionBarActivity
{
    final static public int REQUEST_CODE = 109;
    private LinearLayout mainLinearLayout;
    private EditText locationName_editText, radius_editText;

    private String contactName;
    private Dal_location dal_location;
    private String latitude, longitude;

    private boolean backFromActivity = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_location);

        Intent i = getIntent();
        latitude = i.getStringExtra("latitude");
        longitude = i.getStringExtra("longitude");

        dal_location = new Dal_location();

        mainLinearLayout = (LinearLayout)findViewById(R.id.main_layout_add_location);
        locationName_editText = (EditText)findViewById(R.id.add_location_edit_text_name);
        radius_editText = (EditText)findViewById(R.id.add_location_edit_text_radius);
        setDirectionLayout();

    }

    private void setDirectionLayout()
    {
        String language = Locale.getDefault().getDisplayLanguage();// ---> English
        mainLinearLayout.setLayoutDirection(View.LAYOUT_DIRECTION_LOCALE);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_location, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onClick_add_contact_button(View view)
    {
        Intent i = new Intent(this, ContactListActivity.class);
        startActivityForResult(i, REQUEST_CODE);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {     // Called when Setting activity returns
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE)
        {
            if (resultCode == RESULT_OK)
            {
                contactName = data.getStringExtra("name");
                Log.w("onResult", contactName);
                Log.w("onResultLongitude", longitude);
                Log.w("onResultLatitude", latitude);

                backFromActivity = true;            // Flag returned from Settings activity
            }
        }
    }

    public void onCLick_add_location_button(View view)
    {
        MapLocation location;


        if(backFromActivity)
        {
            location = new MapLocation(locationName_editText.getText().toString(), longitude, latitude, radius_editText.getText().toString(), contactName);
            Log.w("Location",location.toString());
            dal_location.addNewLocation(location, this);
            this.finish();
        }
    }
}
