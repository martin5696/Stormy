package com.example.marttbbb.stormy.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.marttbbb.stormy.R;
import com.example.marttbbb.stormy.weather.Current;
import com.example.marttbbb.stormy.weather.Day;
import com.example.marttbbb.stormy.weather.Forecast;
import com.example.marttbbb.stormy.weather.Hour;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = MainActivity.class.getSimpleName();
    public static final String DAILY_FORECAST = "DAILY_FORECAST";
    public static final String HOURLY_FORECAST = "HOURLY_FORECAST";
    //private Current mCurrent;
    private Forecast mForecast;

   // @BindView(R.id.timeLabel) TextView mTimeLabel;
    //@BindView(R.id.temperatureLabel) TextView mTemperatureLabel;
    private TextView mTemperatureLabel;
    private TextView mTimeLabel;
    private TextView mHumidityValue;
    private TextView mSummaryLabel;
    private TextView mPrecipValue;
    private ImageView mIconImageView;
    private ImageView mRefreshImageView;
    private ProgressBar mProgressBar;
    private Button mDailyButton;
    private Button mHourlyButton;
    //@BindView(R.id.humidityValue) TextView mHumidityValue;
    //@BindView(R.id.precipValue) TextView mPrecipValue;
    //@BindView(R.id.summaryLabel) TextView mSummaryLabel;
    //@BindView(R.id.iconImageView) ImageView mIconImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);//attaches layout to this activity


       // ButterKnife.bind(this);
        mTemperatureLabel= (TextView) findViewById(R.id.temperatureLabel);
        mTimeLabel = (TextView) findViewById(R.id.timeLabel);
        mHumidityValue = (TextView) findViewById(R.id.humidityValue);
        mSummaryLabel = (TextView) findViewById(R.id.summaryLabel);
        mPrecipValue = (TextView) findViewById(R.id.precipValue);
        mIconImageView = (ImageView) findViewById(R.id.iconImageView);
        mRefreshImageView = (ImageView) findViewById(R.id.refreshImageView);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mDailyButton = (Button) findViewById(R.id.dailyButton);
        mHourlyButton = (Button) findViewById(R.id.hourlyButton);

        mProgressBar.setVisibility(View.INVISIBLE);
        mRefreshImageView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                getForecast();

            }
        });

        mDailyButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                startDaily();

            }
        });

        mHourlyButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                startHourly();

            }
        });






        getForecast();
        Log.d(TAG, "Main UI code is running");

    }

    private void getForecast() {
        String apiKey = "3c179f4e86866453f9351913980252f6";
        //Yonge and College
        double latitude = 43.661365;
        double longitude = -79.383102;
        String forecastUrl = "https://api.forecast.io/forecast/" + apiKey +
                "/" + latitude + "," + longitude;

        if (isNetworkAvailable()) {
            toggleRefresh();

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(forecastUrl)
                    .build();

            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            toggleRefresh();
                        }
                    });
                    alertUserAboutError();

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            toggleRefresh();
                        }
                    });

                    try {
                        String jsonData = response.body().string();
                        Log.v(TAG, jsonData);

                        if (response.isSuccessful()) {
                            mForecast = parseForecastDetails(jsonData);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    updateDisplay();
                                }
                            });


                        } else {
                            alertUserAboutError();
                        }
                    }catch (IOException e){
                        Log.e(TAG,"Exception caught: ", e);
                    }
                    catch(JSONException e){
                        Log.e(TAG,"Exception caught: ", e);
                    }

                }
            });

        }

        else {
            Toast.makeText(this, R.string.network_unavailable_message,Toast.LENGTH_LONG).show();
        }
    }

    private void toggleRefresh() {
        //progress bar currently invisible
        if (mProgressBar.getVisibility()== View.INVISIBLE) {
            mProgressBar.setVisibility(View.VISIBLE);
            mRefreshImageView.setVisibility(View.INVISIBLE);
        }
        else{
            mProgressBar.setVisibility(View.INVISIBLE);
            mRefreshImageView.setVisibility(View.VISIBLE);

        }
    }

    private void updateDisplay() {
        Current current = mForecast.getCurrent();
        mTemperatureLabel.setText(current.getTemperature()+"");
        mTimeLabel.setText("At "+ current.getFormattedTime()+" it will be");
        mHumidityValue.setText(current.getHumidity()+"");
        mPrecipValue.setText(current.getPrecipChance()+"%");
        mSummaryLabel.setText(current.getSummary());

        Drawable drawable = getResources().getDrawable(current.getIconId());
        mIconImageView.setImageDrawable(drawable);


    }

    private Forecast parseForecastDetails(String jsonData)throws JSONException{
        Forecast forecast = new Forecast();
        forecast.setCurrent(getCurrentDetails(jsonData));
        forecast.setHourlyForecast(getHourlyForecast(jsonData));
        forecast.setDayForecast(getDailyForecast(jsonData));

        return forecast;
    }

    private Hour[] getHourlyForecast(String jsonData)throws JSONException {
        JSONObject forecast = new JSONObject(jsonData);
        String timezone = forecast.getString("timezone");
        JSONObject hourly = forecast.getJSONObject("hourly");
        JSONArray data = hourly.getJSONArray("data");

        Hour [] hours = new Hour[data.length()];    //initializing the array. not instantiating each object yet.
        for (int i=0;i<data.length();i++){
            JSONObject jsonHour = data.getJSONObject(i);
            Hour hour = new Hour();

            hour.setIcon(jsonHour.getString("icon"));
            hour.setSummary(jsonHour.getString("summary"));
            hour.setTemperature(jsonHour.getDouble("temperature"));
            hour.setTime(jsonHour.getLong("time"));
            hour.setTimezone(timezone);

            hours[i]=hour;

        }
        return hours;
    }

    private Day[] getDailyForecast(String jsonData) throws JSONException{
        JSONObject forecast = new JSONObject(jsonData);
        String timezone = forecast.getString("timezone");
        JSONObject daily = forecast.getJSONObject("daily");
        JSONArray data = daily.getJSONArray("data");

        Day [] days = new Day[data.length()];
        for (int i=0;i<data.length();i++){
            JSONObject jsonDay = data.getJSONObject(i);
            Day day = new Day();
            day.setTimezone(timezone);
            day.setIcon(jsonDay.getString("icon"));
            day.setTime(jsonDay.getLong("time"));
            day.setSummary(jsonDay.getString("summary"));
            day.setTemperatureMax(jsonDay.getDouble("temperatureMax"));

            days[i]=day;
        }
        return days;
    }

    private Current getCurrentDetails(String jsonData)throws JSONException {
            JSONObject forecast = new JSONObject(jsonData);
            String timezone = forecast.getString("timezone");
            Log.i(TAG,"From JSON: "+timezone);
            JSONObject currently = forecast.getJSONObject("currently");

            Current current = new Current();
            current.setHumidity(currently.getDouble("humidity"));
            current.setTime(currently.getLong("time"));
            current.setIcon(currently.getString("icon"));
            current.setPrecipChance(currently.getDouble("precipProbability"));
            current.setSummary(currently.getString("summary"));
            current.setTemperature(currently.getDouble("temperature"));
            current.setTimeZone(timezone);

            return current;

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        boolean isAvailable = false;
        //if network is present and connected
        if (networkInfo!=null&&networkInfo.isConnected()){
            isAvailable=true;
        }
        return isAvailable;
    }

    private void alertUserAboutError() {
        AlertDialogFragment dialog = new AlertDialogFragment();
        dialog.show(getFragmentManager(),"error_dialog");

    }

    public void startDaily(){
        Intent intent = new Intent(this, DailyForecastActivity2.class);
        intent.putExtra(DAILY_FORECAST,mForecast.getDailyForecast());
        startActivity(intent);
    }

    public void startHourly(){
        Intent intent = new Intent(this, HourlyForecastActivity.class);
        intent.putExtra(HOURLY_FORECAST,mForecast.getHourlyForecast());
        startActivity(intent);
    }

}
