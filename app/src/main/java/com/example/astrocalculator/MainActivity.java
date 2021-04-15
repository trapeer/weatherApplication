package com.example.astrocalculator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;

import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;

import io.realm.Realm;


public class MainActivity extends AppCompatActivity {

    private SharedPreferences preferences;

    private double latitude;
    private double longitude;
    private int waitTime;
    private String date1;
    private String date2;
    private String date3;
    private String humidity1;
    private String humidity2;
    private String humidity3;
    private String description1;
    private String description2;
    private String description3;
    private String temp1;
    private String temp2;
    private String temp3;
    private String maxWind1;
    private String maxWind2;
    private String maxWind3;
    private String visibility1;
    private String visibility2;
    private String visibility3;
    private Sun sun;
    private Moon moon;
    private Weather1 weather1;
    private Weather2 weather2;
    private Weather3 weather3;
    private Thread time;
    private Button mButtonOptions;
    private TextView mTextViewTime;
    private TextView mTextViewlocalization;
    private String cityName;
    private boolean isImperial;

    @Override
    protected void onPause() {
        time.interrupt();
        super.onPause();
    }


    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean("isImperial",  isImperial);
        savedInstanceState.putDouble("latitude",  latitude);
        savedInstanceState.putDouble("longitude", longitude);
        savedInstanceState.putInt("waitTime", waitTime);
        savedInstanceState.putString("cityName", cityName);
        savedInstanceState.putString("date1", date1);
        savedInstanceState.putString("date2", date2);
        savedInstanceState.putString("date3", date3);
        savedInstanceState.putString("humidity1",humidity1);
        savedInstanceState.putString("humidity2",humidity2);
        savedInstanceState.putString("humidity3",humidity3);
        savedInstanceState.putString("description1",description1);
        savedInstanceState.putString("description2",description2);
        savedInstanceState.putString("description3",description3);
        savedInstanceState.putString("temp1",temp1);
        savedInstanceState.putString("temp2",temp2);
        savedInstanceState.putString("temp3",temp3);
        savedInstanceState.putString("maxWind1",maxWind1);
        savedInstanceState.putString("maxWind2",maxWind2);
        savedInstanceState.putString("maxWind3",maxWind3);
        savedInstanceState.putString("visibility1",visibility1);
        savedInstanceState.putString("visibility2",visibility2);
        savedInstanceState.putString("visibility3",visibility3);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            isImperial = savedInstanceState.getBoolean("isImperial");
            latitude = savedInstanceState.getDouble("latitude");
            longitude = savedInstanceState.getDouble("longitude");
            waitTime = savedInstanceState.getInt("waitTime");
            cityName = savedInstanceState.getString("cityName");
            date1 = savedInstanceState.getString("date1");
            date2 = savedInstanceState.getString("date2");
            date3 = savedInstanceState.getString("date3");
            humidity1 = savedInstanceState.getString("humidity1");
            humidity2 = savedInstanceState.getString("humidity2");
            humidity3 = savedInstanceState.getString("humidity3");
            description1 = savedInstanceState.getString("description1");
            description2 = savedInstanceState.getString("description2");
            description3 = savedInstanceState.getString("description3");
            temp1 = savedInstanceState.getString("temp1");
            temp2 = savedInstanceState.getString("temp2");
            temp3 = savedInstanceState.getString("temp3");
            maxWind1 = savedInstanceState.getString("maxWind1");
            maxWind2 = savedInstanceState.getString("maxWind2");
            maxWind3 = savedInstanceState.getString("maxWind3");
            visibility1 = savedInstanceState.getString("visibility1");
            visibility2 = savedInstanceState.getString("visibility2");
            visibility3 = savedInstanceState.getString("visibility3");
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        preferences = getSharedPreferences("file", MODE_PRIVATE);

        isImperial = getIntent().getBooleanExtra("isImperial", false);
        cityName = getIntent().getStringExtra("cityName");
        waitTime = getIntent().getIntExtra("refresh", 15);
        latitude = getIntent().getDoubleExtra("lat", 0);
        longitude = getIntent().getDoubleExtra("lon", 0);
        date1 = getIntent().getStringExtra("date1");
        date2 = getIntent().getStringExtra("date2");
        date3 = getIntent().getStringExtra("date3");
        humidity1 = getIntent().getStringExtra("humidity1");
        humidity2 = getIntent().getStringExtra("humidity2");
        humidity3 = getIntent().getStringExtra("humidity3");
        description1 = getIntent().getStringExtra("description1");
        description2 = getIntent().getStringExtra("description2");
        description3 = getIntent().getStringExtra("description3");
        temp1 = getIntent().getStringExtra("temp1");
        temp2 = getIntent().getStringExtra("temp2");
        temp3 = getIntent().getStringExtra("temp3");
        maxWind1 = getIntent().getStringExtra("maxWind1");
        maxWind2 = getIntent().getStringExtra("maxWind2");
        maxWind3 = getIntent().getStringExtra("maxWind3");
        visibility1 = getIntent().getStringExtra("visibility1");
        visibility2 = getIntent().getStringExtra("visibility2");
        visibility3 = getIntent().getStringExtra("visibility3");
        mButtonOptions = findViewById(R.id.buttonOptions);
        mTextViewTime = findViewById(R.id.TextViewTime);
        mTextViewlocalization =  findViewById(R.id.TextViewlocalization);

        if(cityName == null)
        {
            pobierzZBazyDanych(preferences.getString("cityName1", "Paris"), preferences.getBoolean("isImperial1", false));
        }


        mTextViewlocalization.setText("latitude: " +  latitude + " longitude: " + longitude + " City: " + cityName);
        ParametersForFragments.latitude = latitude;
        ParametersForFragments.longitude = longitude;
        ParametersForFragments.waitTime = waitTime;
        ParametersForFragments.date1 = date1;
        ParametersForFragments.date2 = date2;
        ParametersForFragments.date3 = date3;
        ParametersForFragments.humidity1 = humidity1;
        ParametersForFragments.humidity2 = humidity2;
        ParametersForFragments.humidity3 = humidity3;
        ParametersForFragments.description1 = description1;
        ParametersForFragments.description2 = description2;
        ParametersForFragments.description3 = description3;
        ParametersForFragments.temp1 = temp1;
        ParametersForFragments.temp2 = temp2;
        ParametersForFragments.temp3 = temp3;
        ParametersForFragments.maxWind1 = maxWind1;
        ParametersForFragments.maxWind2 = maxWind2;
        ParametersForFragments.maxWind3 = maxWind3;
        ParametersForFragments.visibility1 = visibility1;
        ParametersForFragments.visibility2 = visibility2;
        ParametersForFragments.visibility3 = visibility3;

        final ViewPager viewPager = findViewById(R.id.viewPager);
        if (viewPager != null)
            viewPager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager()));

        if(findViewById(R.id.fragment) != null && findViewById(R.id.fragment2) != null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            sun = new Sun(latitude, longitude, waitTime);
            moon = new Moon(latitude, longitude, waitTime);
            weather1 = new Weather1(date1, description1, humidity1, temp1, maxWind1, visibility1);
            weather2 = new Weather2(date2, description2, humidity2, temp2, maxWind2, visibility2);
            weather3 = new Weather3(date3, description3, humidity3, temp3, maxWind3, visibility3);
            transaction.replace(R.id.fragment, sun);
            transaction.replace(R.id.fragment2, moon);
            transaction.replace(R.id.fragment3, weather1);
            transaction.replace(R.id.fragment4, weather2);
            transaction.replace(R.id.fragment5, weather3);
            transaction.addToBackStack(null);
            transaction.commit();
        }


        time = new Thread(){
            @NonNull
            @Override
            public void run(){
                while(!isInterrupted()){
                    try {
                        Thread.sleep(1000);
                        runOnUiThread(new Runnable(){
                            @Override
                               public void run(){
                                long timestamp = System.currentTimeMillis();
                                Date date = new Date(timestamp);
                                Calendar cal = Calendar.getInstance();
                                cal.setTime(date);
                                mTextViewTime.setText(String.format("%02d : %02d : %02d", cal.get(cal.HOUR_OF_DAY), cal.get(cal.MINUTE), cal.get(cal.SECOND)));
                                //Toast.makeText(getApplicationContext(), "godzina", Toast.LENGTH_SHORT).show();
                                }
                            });
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        break;
                    }
                }
            }
        };
        time.start();


        mButtonOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), OptionsActivity.class);
                intent.putExtra("refresh",  waitTime);
                intent.putExtra("cityName", cityName);
                intent.putExtra("isImperial", isImperial);
                startActivity(intent);
            }
        });

    }

    public void pobierzZBazyDanych(String cityName, Boolean czyImperial)
    {
        this.isImperial = czyImperial;
        Realm.init(getApplicationContext());
        Realm realm = Realm.getDefaultInstance();
        DataBaseObject result = realm.where(DataBaseObject.class).equalTo("cityName", cityName).findFirst();
        if(result == null)
        {
            Toast toast = Toast.makeText(getApplicationContext(), "error", Toast.LENGTH_SHORT);
            toast.show();
        }
        else{
            this.cityName = result.cityName;
            latitude = result.latitude;
            longitude = result.longitude;
            date1 = result.date1;
            date2 = result.date2;
            date3 = result.date3;
            humidity1 = result.humidity1;
            humidity2 = result.humidity2;
            humidity3 = result.humidity3;
            description1 = result.description1;
            description2 = result.description2;
            description3 = result.description3;
            if(czyImperial)
            {
                temp1 = result.temp1i;
                temp2 = result.temp2i;
                temp3 = result.temp3i;
                maxWind1 = result.maxWind1i;
                maxWind2 = result.maxWind2i;
                maxWind3 = result.maxWind3i;
                visibility1 = result.visibility1i;
                visibility2 = result.visibility2i;
                visibility3 = result.visibility3i;
            }
            else{
                temp1 = result.temp1m;
                temp2 = result.temp2m;
                temp3 = result.temp3m;
                maxWind1 = result.maxWind1m;
                maxWind2 = result.maxWind2m;
                maxWind3 = result.maxWind3m;
                visibility1 = result.visibility1m;
                visibility2 = result.visibility2m;
                visibility3 = result.visibility3m;
            }
            ParametersForFragments.image1 = new BitmapDrawable(getResources(), BitmapFactory.decodeByteArray(result.image1,0,result.image1.length));
            ParametersForFragments.image2 = new BitmapDrawable(getResources(), BitmapFactory.decodeByteArray(result.image2,0,result.image2.length));
            ParametersForFragments.image3 = new BitmapDrawable(getResources(), BitmapFactory.decodeByteArray(result.image3,0,result.image3.length));
        }
    }
}
