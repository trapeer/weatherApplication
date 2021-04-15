package com.example.astrocalculator;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.astrocalculator.AstroCalculator;
import com.astrocalculator.AstroDateTime;

import java.util.Calendar;
import java.util.Date;

public class Sun extends Fragment {
    private TextView mTextViewSunriseTime;
    private TextView mTextViewSunriseAzimuth;
    private TextView mTextViewSunsetTime;
    private TextView mTextViewSunsetAzimuth;
    private TextView mTextViewDuskTime;
    private TextView mTextViewDawnTime;
    private double latitude;
    private double longitude;
    private int waitTime;
    private Thread refresh;


    private Context context;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    public Sun (double latitude, double longitude, int waitTime)
    {
        this.latitude = latitude;
        this.longitude = longitude;
        this.waitTime = waitTime;
    }
    public Sun ()
    {
        this.latitude = ParametersForFragments.latitude;
        this.longitude = ParametersForFragments.longitude;
        this.waitTime = ParametersForFragments.waitTime;
    }

    @Override
    public void onPause() {
        refresh.interrupt();
        super.onPause();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);


        View rootView = inflater.inflate(R.layout.sun, container, false);


        mTextViewSunriseTime = rootView.findViewById(R.id.textViewSunriseTime);
        mTextViewSunriseAzimuth = rootView.findViewById(R.id.textViewSunriseAzimuth);
        mTextViewSunsetTime = rootView.findViewById(R.id.textViewSunsetTime);
        mTextViewSunsetAzimuth = rootView.findViewById(R.id.textViewSunsetAzimuth);
        mTextViewDuskTime = rootView.findViewById(R.id.textViewDuskTime);
        mTextViewDawnTime = rootView.findViewById(R.id.textViewDawnTime);

        refresh();

        refresh = new Thread(){
            @NonNull
            @Override
            public void run(){
                while(!isInterrupted()){
                    try {
                        Thread.sleep(waitTime*60000);
                        ((MainActivity)context).runOnUiThread(new Runnable(){
                            @Override
                            public void run(){
                                refresh();
                            }
                        });
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        break;
                    }
                }
            }
        };
        refresh.start();

        return rootView;
}

    public void refresh()
    {
        long timestamp = System.currentTimeMillis();
        Date date = new Date(timestamp);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        AstroDateTime astroDateTime = new AstroDateTime(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), cal.get(Calendar.SECOND), cal.get(Calendar.ZONE_OFFSET), true);
        AstroCalculator.Location location = new AstroCalculator.Location(latitude,longitude);
        AstroCalculator astroCalculator = new AstroCalculator(astroDateTime, location);
        mTextViewSunriseTime.setText(String.format("%02d : %02d : %02d",astroCalculator.getSunInfo().getSunrise().getHour(), astroCalculator.getSunInfo().getSunrise().getMinute(), astroCalculator.getSunInfo().getSunrise().getSecond()));
        mTextViewSunriseAzimuth.setText(String.format("%.2f", astroCalculator.getSunInfo().getAzimuthRise()));
        mTextViewSunsetTime.setText(String.format("%02d : %02d : %02d",astroCalculator.getSunInfo().getSunset().getHour(), astroCalculator.getSunInfo().getSunset().getMinute(), astroCalculator.getSunInfo().getSunset().getSecond()));
        mTextViewSunsetAzimuth.setText(String.format("%.2f", astroCalculator.getSunInfo().getAzimuthSet()));
        mTextViewDuskTime.setText(String.format("%02d : %02d : %02d", astroCalculator.getSunInfo().getTwilightEvening().getHour(), astroCalculator.getSunInfo().getTwilightEvening().getMinute(), astroCalculator.getSunInfo().getTwilightEvening().getSecond()));
        mTextViewDawnTime.setText(String.format("%02d : %02d : %02d", astroCalculator.getSunInfo().getTwilightMorning().getHour(), astroCalculator.getSunInfo().getTwilightMorning().getMinute(), astroCalculator.getSunInfo().getTwilightMorning().getSecond()));
    }

}
