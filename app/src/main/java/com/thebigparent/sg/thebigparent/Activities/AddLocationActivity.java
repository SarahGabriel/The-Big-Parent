package com.thebigparent.sg.thebigparent.Activities;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.thebigparent.sg.thebigparent.Classes.AlertDialogManager;
import com.thebigparent.sg.thebigparent.Classes.MapLocation;
import com.thebigparent.sg.thebigparent.Dal.Dal_location;
import com.thebigparent.sg.thebigparent.R;

/**
 * AddLocationActivity
 *
 * Add location on specific marker on map
 */
public class AddLocationActivity extends Activity
{
    private LinearLayout mainLinearLayout;
    private EditText locationName_editText, radius_editText;
    private String contactName;
    private String phone;
    private static final int NUM_OF_CONTACTS = 1;
    private Dal_location dal_location;
    private String latitude, longitude;

    private boolean backFromActivity = false;

    static final int PICK_CONTACT_REQUEST = 123;



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_location);

        Intent i = getIntent();     // get information from previous activity
        latitude = i.getStringExtra("latitude");
        longitude = i.getStringExtra("longitude");

        dal_location = new Dal_location();

//        find views
        mainLinearLayout = (LinearLayout)findViewById(R.id.main_layout_add_location);
        locationName_editText = (EditText)findViewById(R.id.add_location_edit_text_name);
        radius_editText = (EditText)findViewById(R.id.add_location_edit_text_radius);
        setDirectionLayout();           // set direction layout if language changed

    }

    private void setDirectionLayout()       // set direction layout if language changed
    {
       // String language = Locale.getDefault().getDisplayLanguage();// ---> English
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

    public void onClick_add_contact_button(View view)       // go to contact in device
    {
        Intent pickContactIntent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        pickContactIntent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE); // Show user only contacts w/ phone numbers
        startActivityForResult(pickContactIntent, PICK_CONTACT_REQUEST);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data)  // Called when Setting activity returns
    {
        super.onActivityResult(requestCode, resultCode, data);

        TextView contact = (TextView) findViewById(R.id.contact_name);

        if (requestCode == PICK_CONTACT_REQUEST)
        {
            backFromActivity = true;
            if (resultCode == RESULT_OK)
            {
                Uri contactData = data.getData();       // get data from chosen contact
                ContentResolver cr = getContentResolver();
                Cursor cur = cr.query(contactData, null, null, null, null);     // get in cursor contact data

                if (cur.moveToFirst())
                {
                    contactName = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));   // get name of contact
                    contact.setText(contactName);      // set contact name text view

                    if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0)  // if contact has a phone number
                    {
                        phone = cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));       // get phone number of contact
                    }
                }

            }
        }

    }

    public void onCLick_add_location_button(View view)      // on button click "Add location"
    {
        MapLocation location;
        AlertDialogManager alertDialogManager = new AlertDialogManager();
        if(radius_editText.getText().equals("") || locationName_editText.getText().equals("") || !backFromActivity)     // check if all the fields are fill
        {
            String title = getString(R.string.alertTitle_empty_fields);
            String message = getString(R.string.alertMessage_fill_fields);
            alertDialogManager.showAlertDialog(this, title, message);  // show alert on error
        }
        else
        {
            location = new MapLocation(locationName_editText.getText().toString(), longitude, latitude, radius_editText.getText().toString(), contactName ,phone , NUM_OF_CONTACTS); // create location
            dal_location.addNewLocation(location, this);        // add location to DB
            this.finish();      // go back to activity
        }
    }


    public void onClick_editRadius_button(View view)        // on click "Edit Radius"
    {

        Button edit_button = (Button)findViewById(R.id.edit_radius_button);
        radius_editText = (EditText)findViewById(R.id.add_location_edit_text_radius);
        if(edit_button.getText().equals(view.getContext().getResources().getString(R.string.edit_radius)))      // on click "Edit Radius"
        {
            radius_editText.setEnabled(true);       // make possible to edit the radius
            edit_button.setText(view.getContext().getResources().getString(R.string.save));         // change button text to "Save"
        }
        else
        {
            radius_editText.setEnabled(false);      // on click "Save"
            edit_button.setText(view.getContext().getResources().getString(R.string.edit_radius));   // change back button text to "Edit Radius"
        }


    }
}
