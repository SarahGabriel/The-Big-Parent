package com.thebigparent.sg.thebigparent.Activities;
import android.content.Intent;
import android.provider.ContactsContract;


import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.thebigparent.sg.thebigparent.R;

import java.util.Locale;

public class AddLocationActivity extends ActionBarActivity
{
    LinearLayout mainLinearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_location);

        mainLinearLayout = (LinearLayout)findViewById(R.id.main_layout_add_location);
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
        startActivity(i);
    }
}
