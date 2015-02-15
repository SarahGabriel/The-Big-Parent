package com.thebigparent.sg.thebigparent.Activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.thebigparent.sg.thebigparent.Classes.MapLocation;
import com.thebigparent.sg.thebigparent.Dal.Dal_location;
import com.thebigparent.sg.thebigparent.R;

import java.util.ArrayList;
import java.util.List;

// activity that handles locations to be tracked
public class MapsActivity extends FragmentActivity implements GoogleMap.OnMapLongClickListener, GoogleMap.OnMarkerClickListener, GoogleMap.OnInfoWindowClickListener, GoogleMap.InfoWindowAdapter , GoogleMap.OnMapClickListener
{

    private static Circle markerCircle;     // The radius circle of the clicked marker

    Button plus,minus;          // For the radius size
    TextView radiusText;

    List<LatLng> latLngList;        // List of all the markers locations
    List<Circle> circlesRadius;     // List of all the markers radius circles

    public static GoogleMap mMap;   // Might be null if Google Play services APK is not available.
    private Marker myMarker;        // The new added marker
    private Dal_location dal_location;      // To access the db

    @Override
    protected void onCreate(Bundle savedInstanceState) {        // When activity created
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();             // Set the map

        plus = (Button)findViewById(R.id.plus);                 // Get the plus minus Buttons and the text to edit the radius
        minus = (Button)findViewById(R.id.minus);
        radiusText = (TextView)findViewById(R.id.radiusText);

        dal_location = new Dal_location();                      // Access to db calls

        mMap.setOnMapLongClickListener(this);                   // Set map listeners
        mMap.setOnInfoWindowClickListener(this);
        mMap.setOnMarkerClickListener(this);
        mMap.setOnMapClickListener(this);



    }


    @Override
    protected void onResume()           // On activity resume
    {
        super.onResume();

        mMap.clear();                   // Refresh markers on the map
        setUpMapIfNeeded();
        addMarkersOnMap();

        plus.setVisibility(View.INVISIBLE);             // Set radius UI invisible
        minus.setVisibility(View.INVISIBLE);
        radiusText.setVisibility(View.INVISIBLE);

    }

    private void setUpMapIfNeeded()         // Set the map
    {

        mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                .getMap();

        if (mMap != null) {
            setUpMap();
        }


    }


    private void setUpMap()     // Set the map
    {
        // Enable MyLocation Layer of Google Map
        mMap.setMyLocationEnabled(true);

        // Get LocationManager object from System Service LOCATION_SERVICE
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // Get Current Location
        Location mCurrentLocation = getLastKnownLocation();

        // set map type
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        // Get latitude of the current location
        double latitude = mCurrentLocation.getLatitude();

        // Get longitude of the current location
        double longitude = mCurrentLocation.getLongitude();

        // Create a LatLng object for the current location
        LatLng latLng = new LatLng(latitude, longitude);

        // Show the current location in Google Map
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(CameraPosition.fromLatLngZoom(latLng, 14)));

        // Add marker to current location
        mMap.addMarker(new MarkerOptions().position(latLng).title("You are here!").snippet("Consider yourself located").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
    }

    private void addMarkersOnMap()          // Add all locations as markers on the map
    {
       latLngList = dal_location.getAllLocationsMarker(getApplicationContext());            // Get all location Latlng from db
       circlesRadius = new ArrayList<Circle>();     // New list for all markers radii

       Circle c;

       List<MapLocation> locationList = dal_location.getAllLocations(getApplicationContext());      // Get all location from db

        for(int i = 0; i<latLngList.size(); i++)            // Add all locations as markers to the map
       {
           MapLocation location = locationList.get(i);
           double longitude = Double.parseDouble(location.getLongitude());
           double latitude = Double.parseDouble(location.getLatitude());
           LatLng latLng = new LatLng(latitude, longitude);

//           "\u200e" -- in order to support hebrew language in the marker
           mMap.addMarker(new MarkerOptions().position(latLng).title("\u200e"+location.getLocationName()).snippet("\u200e"+location.getContact().toString()));      // Add the marker
           c = mMap.addCircle(new CircleOptions().center(latLng).radius(Integer.parseInt(location.getRadius().toString())));                                        // Add the radius

           circlesRadius.add(c);        // Add the radius to the radii list
       }
    }


    @Override
    public void onMapLongClick(LatLng latLng)           // On map lng click add new location and marker
    {

        // adding marker after long click pressed
        myMarker = mMap.addMarker(new MarkerOptions().position(latLng));

        // Go to activity - adding marker settings
        Intent i = new Intent(this, AddLocationActivity.class);
        i.putExtra("latitude", Double.toString(myMarker.getPosition().latitude));
        i.putExtra("longitude", Double.toString(myMarker.getPosition().longitude));
        startActivity(i);



    }


    @Override
    public boolean onMarkerClick(Marker marker)     // On marker click enable radius editing and go to settings
    {
        if(markerCircle != null)        // Set old marker radius black
        {
            markerCircle.setStrokeColor(Color.BLACK);
        }

        LatLng latLng = marker.getPosition();       // Get marker position

        int i = 0;
        for(LatLng l : latLngList)          // Get the marker radius from thr radii list
        {

            if(l.latitude == latLng.latitude && l.longitude == latLng.longitude)
            {
                markerCircle = circlesRadius.get(i);
                break;
            }
            i++;
        }

        markerCircle.setStrokeColor(Color.RED);     // Set the chosen radius red

        plus.setVisibility(View.VISIBLE);           // Set radius edit UI visible
        minus.setVisibility(View.VISIBLE);
        radiusText.setVisibility(View.VISIBLE);

        radiusText.setText("Radius: " + (int)markerCircle.getRadius() + " m");      // Set thr radius text

        return false;
    }

    @Override
    public void onInfoWindowClick(Marker marker)                // On info window click go to  MarkerOptionsActivity activity
    {
        Intent i = new Intent(this, MarkerOptionsActivity.class);
        i.putExtra("latitude", Double.toString(marker.getPosition().latitude));
        i.putExtra("longitude", Double.toString(marker.getPosition().longitude));
        i.putExtra("caller", getIntent().getComponent().getClassName());
        startActivity(i);
    }


    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }


    private Location getLastKnownLocation()     // Get the last known location
    {
        LocationManager mLocationManager = (LocationManager)getApplicationContext().getSystemService(LOCATION_SERVICE);
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            Location l = mLocationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                // Found best last known location: %s", l);
                bestLocation = l;
            }
        }
        return bestLocation;
    }



    @Override
    public void onMapClick(LatLng latLng) {             // On map click stop radius UI editing

        if(markerCircle != null)        // Set the radius color to black
        {
            markerCircle.setStrokeColor(Color.BLACK);
        }

        plus.setVisibility(View.INVISIBLE);               // Hide the set radius UI
        minus.setVisibility(View.INVISIBLE);
        radiusText.setVisibility(View.INVISIBLE);
    }

    public void plusOnclick(View view) {            // Increase radius

        //markerCircle.remove();
        markerCircle.setRadius(markerCircle.getRadius() * 1.2);
        radiusText.setText("Radius: " + (int)markerCircle.getRadius() + " m");
        //todo: add radius to db

    }

    public void minusOnclick(View view) {           // Decrease radius
        markerCircle.setRadius(markerCircle.getRadius()*0.8);
        radiusText.setText("Radius: " + (int)markerCircle.getRadius()+ " m");
        //todo: add radius to db
    }


}


