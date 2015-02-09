package com.thebigparent.sg.thebigparent.Activities;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.thebigparent.sg.thebigparent.DB.MyTimeAdapter;
import com.thebigparent.sg.thebigparent.R;

public class TimeActivity extends ActionBarActivity implements AdapterView.OnItemClickListener
{
    private ListView listView;
    private MyTimeAdapter timeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time);

        listView = (ListView)findViewById(R.id.list_time);
        String[] times = {"10:00-12:00", "08:00-16:00"};

        timeAdapter = new MyTimeAdapter(this, android.R.layout.activity_list_item, R.id.list_time, times);

        listView.setAdapter(timeAdapter);
        listView.setOnItemClickListener(this);

        //contacts = contactList();

       // ListAdapter listAdapter = new ArrayAdapter<String>(this, android.R.layout.row_time, contacts);
//        listView.setAdapter(listAdapter);
//        listView.setOnItemClickListener(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_time, menu);
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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }
}
