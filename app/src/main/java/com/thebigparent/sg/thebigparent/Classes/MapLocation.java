package com.thebigparent.sg.thebigparent.Classes;

/**
 * Created by Guy on 05/02/15.
 */
public class MapLocation
{
    private String locationName;
    private String longitude;
    private String latitude;
    private String radius;
    private String contact;

    public MapLocation(String locationName, String longitude, String latitude, String radius, String contact)
    {
        this.locationName = locationName;
        this.longitude = longitude;
        this.latitude = latitude;
        this.radius = radius;
        this.contact = contact;
    }

    public String getLocationName()
    {
        return locationName;
    }

    public void setLocationName(String locationName)
    {
        this.locationName = locationName;
    }

    public String getLatitude()
    {
        return latitude;
    }

    public void setLatitude(String latitude)
    {
        this.latitude = latitude;
    }

    public String getLongitude()
    {
        return longitude;
    }

    public void setLongitude(String longitude)
    {
        this.longitude = longitude;
    }

    public String getRadius()
    {
        return radius;
    }

    public void setRadius(String radius)
    {
        this.radius = radius;
    }

    public String getContact()
    {
        return contact;
    }

    public void setContact(String contact)
    {
        this.contact = contact;
    }

    @Override
    public String toString() {
        return "Location\n{" +
                "locationName='" + locationName + '\'' +
                ", longitude='" + longitude + '\'' +
                ", latitude='" + latitude + '\'' +
                ", radius='" + radius + '\'' +
                ", contact='" + contact + '\'' +
                '}';
    }
}
