package com.thebigparent.sg.thebigparent.Activities;

import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.thebigparent.sg.thebigparent.Classes.MapLocation;
import com.thebigparent.sg.thebigparent.Dal.Dal_location;
import com.thebigparent.sg.thebigparent.R;

import java.util.List;


public class MapsActivity extends FragmentActivity implements GoogleMap.OnMapLongClickListener, GoogleMap.OnMarkerClickListener, GoogleMap.OnInfoWindowClickListener, GoogleMap.InfoWindowAdapter
{


    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private Marker myMarker;
    private Dal_location dal_location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();

        dal_location = new Dal_location();
        mMap.setOnMapLongClickListener(this);
        mMap.setOnInfoWindowClickListener(this);

    }

    @Override
    protected void onResume()
    {
        super.onResume();
        mMap.clear();
        setUpMapIfNeeded();
        addMarkersOnMap();
    }


    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded()
    {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }


    private void setUpMap() {
        //mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));

        //mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker").snippet("Snippet"));

        // Enable MyLocation Layer of Google Map
        mMap.setMyLocationEnabled(true);

        // Get LocationManager object from System Service LOCATION_SERVICE
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // Create a criteria object to retrieve provider
        Criteria criteria = new Criteria();

        // Get the name of the best provider
        String provider = locationManager.getBestProvider(criteria, true);

        // Get Current Location
        Location myLocation = locationManager.getLastKnownLocation(provider);

        // set map type
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        // Get latitude of the current location
        double latitude = myLocation.getLatitude();

        // Get longitude of the current location
        double longitude = myLocation.getLongitude();

        // Create a LatLng object for the current location
        LatLng latLng = new LatLng(latitude, longitude);

        // Show the current location in Google Map
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(CameraPosition.fromLatLngZoom(latLng, 14)));

        //mMap.addCircle(new CircleOptions().center(latLng).radius(100));
        mMap.addMarker(new MarkerOptions().position(latLng).title("You are here!").snippet("Consider yourself located").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
    }

    private void addMarkersOnMap()
    {
       List<LatLng> latLngList = dal_location.getAllLocationsMarker(getApplicationContext());
       List<MapLocation> locationList = dal_location.getAllLocations(getApplicationContext());
       for(int i = 0; i<latLngList.size(); i++)
       {
           MapLocation location = locationList.get(i);
           double longitude = Double.parseDouble(location.getLongitude());
           double latitude = Double.parseDouble(location.getLatitude());
           LatLng latLng = new LatLng(latitude, longitude);
//           "\u200e" -- in order to support hebrew language in the marker
           mMap.addMarker(new MarkerOptions().position(latLng).title("\u200e"+location.getLocationName()).snippet("\u200e"+location.getContact().toString()));
           mMap.addCircle(new CircleOptions().center(latLng).radius(Integer.parseInt(location.getRadius().toString())));
       }
    }


    @Override
    public void onMapLongClick(LatLng latLng)
    {
        mMap.setOnMarkerClickListener(this);

//        // List of all the markers
//        List<LatLng> latLngList = dal_location.getAllLocationsMarker(getApplicationContext());
//        for(LatLng latLng1 : latLngList)
//        {
//            // Check if the long click is near to one of the markers we've added
//            if(Math.abs(latLng1.latitude - latLng.latitude) < 0.00005 && Math.abs(latLng1.longitude - latLng.longitude) < 0.00005)
//            {
//                onMarkerLongClick(latLng1);
//                return;
//            }
//        }

        // adding marker after long click pressed
        myMarker = mMap.addMarker(new MarkerOptions()
        .position(latLng));

        // Go to activity - adding marker settings
        Intent i = new Intent(this, AddLocationActivity.class);
        i.putExtra("latitude", Double.toString(myMarker.getPosition().latitude));
        i.putExtra("longitude", Double.toString(myMarker.getPosition().longitude));
        startActivity(i);



    }

    private void onMarkerLongClick(LatLng latLng)
    {
        Toast.makeText(MapsActivity.this, "got clicked", Toast.LENGTH_SHORT).show(); //do some stuff

    }


    @Override
    public boolean onMarkerClick(Marker marker)
    {
//        if (marker.equals(myMarker))
//        {
//            //handle click here
//            Intent i = new Intent(this, AddLocationActivity.class);
//            i.putExtra("latitude", Double.toString(myMarker.getPosition().latitude));
//            i.putExtra("longitude", Double.toString(myMarker.getPosition().longitude));
//            startActivity(i);
//        }
        return false;
    }

    @Override
    public void onInfoWindowClick(Marker marker)
    {
        Intent i = new Intent(this, MarkerOptionsActivity.class);
        i.putExtra("latitude", Double.toString(marker.getPosition().latitude));
        i.putExtra("longitude", Double.toString(marker.getPosition().longitude));
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

//    @Override
//    public boolean onMarkerClick(final Marker marker)
//    {
//
//        if (marker.equals(myMarker))
//        {
//            //handle click here
//            Intent i = new Intent(this, AddLocationActivity.class);
//            startActivity(i);
//        }
//        return false;
//    }
}


