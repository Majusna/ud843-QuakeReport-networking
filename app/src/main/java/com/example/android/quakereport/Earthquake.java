package com.example.android.quakereport;

import java.text.SimpleDateFormat;

/**
 * Created by todor on 4/17/2018.
 */

public class Earthquake {

    Double mMagnitude;
    String mLocation;
    private long mTimeInMilliseconds;
    private String mUrl;


    public Earthquake(Double magnitude, String location, long timeInMilliseconds, String url){
        mMagnitude = magnitude;
        mLocation = location;
        mTimeInMilliseconds = timeInMilliseconds;
        mUrl = url;
    }

    public Double getMagnitude(){
        return mMagnitude;
    }

    public String getLocation(){
        return mLocation;
    }

    public long getTimeInMilliseconds() {
        return mTimeInMilliseconds;
    }

    public String getUrl() {
        return mUrl;
    }


}
