package com.example.marttbbb.stormy.ui;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;


import com.example.marttbbb.stormy.R;
import com.example.marttbbb.stormy.adapters.DayAdapter;
import com.example.marttbbb.stormy.weather.Day;

import java.util.Arrays;

public class DailyForecastActivity2 extends ListActivity {
    private Day[] mDays;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_forecast2);

        Intent intent = getIntent();
        Parcelable[] parcelables = intent.getParcelableArrayExtra(MainActivity.DAILY_FORECAST);
        mDays = Arrays.copyOf(parcelables,parcelables.length,Day [].class);

        DayAdapter adapter = new DayAdapter(this,mDays);
        setListAdapter(adapter);
    }

    //View is any textView,imageView,Layout, etc
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        String dayOfTheWeek = mDays[position].getDayOfTheWeek();
        String conditions = mDays[position].getSummary();
        String highTemp = mDays[position].getTemperatureMax()+"";
        String message  =String.format("On %s the high will be %s and it will be %s", dayOfTheWeek, highTemp,conditions);
        Toast.makeText(this,message,Toast.LENGTH_LONG).show();


    }
}
