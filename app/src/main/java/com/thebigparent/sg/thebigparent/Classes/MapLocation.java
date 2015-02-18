package com.thebigparent.sg.thebigparent.Classes;

/**
 * MapLocation
 *
 * Class of location - using when adding location to DB
 */
public class MapLocation
{
    private String locationName;
    private String longitude;
    private String latitude;
    private String radius;
    private String contact;
    private String phone;
    private int numOfContacts;

    /**
     *
     * @param locationName  - name of location
     * @param longitude  - longitude of location
     * @param latitude  - latitude of location
     * @param radius    - radius which the user shouldn't pass when its tracking time
     * @param contact   - contact to send the message if out of radius
     * @param phone - phone number of contact
     * @param numOfContacts - num of contacts to send the message
     */
    public MapLocation(String locationName, String longitude, String latitude, String radius, String contact ,String phone,int numOfContacts)
    {
        this.locationName = locationName;
        this.longitude = longitude;
        this.latitude = latitude;
        this.radius = radius;
        this.contact = contact;
        this.phone = phone;
        this.numOfContacts = numOfContacts;
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

    public String getPhone()
    {
        return phone;
    }

    public void setPhone(String phone)
    {
        this.phone = phone;
    }

    public int getNumOfContacts()
    {
        return numOfContacts;
    }

    public void setNumOfContacts(int numOfContacts)
    {
        this.numOfContacts = numOfContacts;
    }

    @Override
    public String toString() {
        return "Location\n{" +
                "locationName='" + locationName + '\'' +
                ", longitude='" + longitude + '\'' +
                ", latitude='" + latitude + '\'' +
                ", radius='" + radius + '\'' +
                ", contact='" + contact + '\'' +
                ", phone='" + phone + '\'' +
                ", numOfContacts='" + numOfContacts + '\'' +
                '}';
    }
}
