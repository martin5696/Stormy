package com.example.marttbbb.stormy.weather;

import com.example.marttbbb.stormy.R;

/**
 * Created by Marttbbb on 5/17/2016.
 */
public class Forecast {
    private Current mCurrent;
    private Hour [] mHourlyForecast;
    private Day [] mDayForecast;

    public Current getCurrent() {
        return mCurrent;
    }

    public void setCurrent(Current current) {
        mCurrent = current;
    }

    public Hour[] getHourlyForecast() {
        return mHourlyForecast;
    }

    public void setHourlyForecast(Hour[] hourlyForecast) {
        mHourlyForecast = hourlyForecast;
    }

    public Day[] getDailyForecast() {
        return mDayForecast;
    }

    public void setDayForecast(Day[] dayForecast) {
        mDayForecast = dayForecast;
    }

    //helper method. don't need an instance to call
    public static int getIconId(String iconString){
        int iconId = R.drawable.clear_day;//default icon
        if (iconString.equals("clear-day"))
            iconId=R.drawable.clear_day;
        else if (iconString.equals("clear-night"))
            iconId=R.drawable.clear_night;
        else if (iconString.equals("rain")) {
            iconId = R.drawable.rain;
        }
        else if (iconString.equals("snow")) {
            iconId = R.drawable.snow;
        }
        else if (iconString.equals("sleet")) {
            iconId = R.drawable.sleet;
        }
        else if (iconString.equals("wind")) {
            iconId = R.drawable.wind;
        }
        else if (iconString.equals("fog")) {
            iconId = R.drawable.fog;
        }
        else if (iconString.equals("cloudy")) {
            iconId = R.drawable.cloudy;
        }
        else if (iconString.equals("partly-cloudy-day")) {
            iconId = R.drawable.partly_cloudy;
        }
        else if (iconString.equals("partly-cloudy-night")) {
            iconId = R.drawable.cloudy_night;
        }
        return iconId;
    }
}
