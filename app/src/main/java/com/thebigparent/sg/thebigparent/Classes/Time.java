package com.thebigparent.sg.thebigparent.Classes;

/**
 * Created by Sarah on 11-Feb-15.
 */
public class Time
{
    private int day;
    private String hourStart;
    private String hourEnd;
    private String longitude;
    private String latitude;
    private int noRepeat;
    private int isSwitchOn;
    private String date;

    public Time(int day, String hourStart, String hourEnd, String latitude, String longitude, String date, int noRepeat, int isSwitchOn)

    {
        this.day = day;
        this.hourStart = hourStart;
        this.hourEnd = hourEnd;
        this.longitude = longitude;
        this.latitude = latitude;
        this.date = date;
        this.noRepeat = noRepeat;
        this.isSwitchOn = isSwitchOn;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public String getHourStart() {
        return hourStart;
    }

    public void setHourStart(String hourStart) {
        this.hourStart = hourStart;
    }

    public String getHourEnd() {
        return hourEnd;
    }

    public void setHourEnd(String hourEnd) {
        this.hourEnd = hourEnd;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int isNoRepeat() {
        return noRepeat;
    }

    public void setNoRepeat(int noRepeat) {
        this.noRepeat = noRepeat;
    }

    public int getIsSwitchOn() {
        return isSwitchOn;
    }

    public void setIsSwitchOn(int isSwitchOn) {
        this.isSwitchOn = isSwitchOn;
    }

    @Override
    public String toString() {
        return "Time{" +
                "day=" + day +
                ", hourStart='" + hourStart + '\'' +
                ", hourEnd='" + hourEnd + '\'' +
                ", longitude='" + longitude + '\'' +
                ", latitude='" + latitude + '\'' +
                ", noRepeat=" + noRepeat +
                ", isSwitchOn=" + isSwitchOn +
                ", date='" + date + '\'' +
                '}';
    }
}
