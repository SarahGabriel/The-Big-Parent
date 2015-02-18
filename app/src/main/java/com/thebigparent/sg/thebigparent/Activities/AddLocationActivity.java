package com.thebigparent.sg.thebigparent.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.thebigparent.sg.thebigparent.Classes.MapLocation;
import com.thebigparent.sg.thebigparent.Dal.Dal_location;
import com.thebigparent.sg.thebigparent.R;

import java.util.Locale;

public class AddLocationActivity extends Activity
{
    final static public int REQUEST_CODE = 109;
    private LinearLayout mainLinearLayout;
    private EditText locationName_editText, radius_editText;
    private String contactName;
    private String phone;
    private int numOfContacts = 1;
    private Dal_location dal_location;
    private String latitude, longitude;

    private boolean backFromActivity = false;

    static final int PICK_CONTACT_REQUEST = 123;



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

        Intent pickContactIntent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
//        Intent pickContactIntent = new Intent(Intent.ACTION_PICK, Uri.parse("content://contacts"));
        pickContactIntent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE); // Show user only contacts w/ phone numbers
        startActivityForResult(pickContactIntent, PICK_CONTACT_REQUEST);


//        Intent i = new Intent(this, ContactListActivity.class);
//        startActivityForResult(i, REQUEST_CODE);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {     // Called when Setting activity returns
        super.onActivityResult(requestCode, resultCode, data);

        TextView contact = (TextView) findViewById(R.id.contact_name);

        if (requestCode == PICK_CONTACT_REQUEST)
        {
            backFromActivity = true;

            if (resultCode == RESULT_OK)
            {
                String name = "";

                Uri contactData = data.getData();
                ContentResolver cr = getContentResolver();
                Cursor cur = cr.query(contactData, null, null, null, null);


                if (cur.moveToFirst())
                {

                    name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    contactName= name;
                    contact.setText(name);
                    String id2 = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));


                    if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0)
                    {
                        String phoneNo = cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));

                        phone = phoneNo;
                        Log.w("HAS_PHONE_NUMBER",name + " " + phoneNo + " " + id);


                    }
                }

            }
        }

    }

    public void onCLick_add_location_button(View view)
    {
        MapLocation location;

        if(radius_editText.getText().equals("") || locationName_editText.getText().equals("") || !backFromActivity)
        {
            new AlertDialog.Builder(this)
                    .setTitle("Fields missing!")
                    .setMessage("Please fill all the fields")
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
        else
        {
            location = new MapLocation(locationName_editText.getText().toString(), longitude, latitude, radius_editText.getText().toString(), contactName ,phone , numOfContacts);
            Log.w("Location", location.toString());
            dal_location.addNewLocation(location, this);
            this.finish();
        }
    }


    public void onClick_editRadius_button(View view)
    {
        Button edit_button = (Button)findViewById(R.id.edit_radius_button);
        radius_editText = (EditText)findViewById(R.id.add_location_edit_text_radius);
        if(edit_button.getText().equals(view.getContext().getResources().getString(R.string.edit_radius)))
        {

            radius_editText.setEnabled(true);
            //editRadius.setText("");
            edit_button.setText(view.getContext().getResources().getString(R.string.save));
        }
        else
        {
            radius_editText.setEnabled(false);
            //editRadius.setText("100");
            edit_button.setText(view.getContext().getResources().getString(R.string.edit_radius));
           // dal_location.updateRadius(latitude, longitude, editRadius.getText().toString(), view.getContext());
        }


    }
}
