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

public class Moon extends Fragment {
    private TextView mTextViewMoonriseTime;
    private TextView mTextViewMoonsetTime;
    private TextView mTextViewNewMoonDate;
    private TextView mTextViewFullMoonDate;
    private TextView mTextViewPhaseOfTheMoon;
    private TextView mTextViewMoonAge;
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

    public Moon (double latitude, double longitude, int waitTime)
    {
        this.latitude = latitude;
        this.longitude = longitude;
        this.waitTime = waitTime;
    }
    public Moon ()
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
        View rootView = inflater.inflate(R.layout.moon, container, false);

        mTextViewMoonriseTime = rootView.findViewById(R.id.textViewMoonriseTime);
        mTextViewMoonsetTime = rootView.findViewById(R.id.textViewMoonsetTime);
        mTextViewNewMoonDate = rootView.findViewById(R.id.textViewNewMoonDate);
        mTextViewFullMoonDate = rootView.findViewById(R.id.textViewFullMoonDate);
        mTextViewPhaseOfTheMoon = rootView.findViewById(R.id.textViewPhaseOfTheMoon);
        mTextViewMoonAge = rootView.findViewById(R.id.textViewMoonAge);

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
        mTextViewMoonriseTime.setText(String.format("%02d : %02d : %02d", astroCalculator.getMoonInfo().getMoonrise().getHour(), astroCalculator.getMoonInfo().getMoonrise().getMinute(), astroCalculator.getMoonInfo().getMoonrise().getSecond()));
        mTextViewMoonsetTime.setText(String.format("%02d : %02d : %02d", astroCalculator.getMoonInfo().getMoonset().getHour(), astroCalculator.getMoonInfo().getMoonset().getMinute(), astroCalculator.getMoonInfo().getMoonset().getSecond()));
        mTextViewNewMoonDate.setText(String.format("%02d : %02d : %04d", astroCalculator.getMoonInfo().getNextNewMoon().getDay(), astroCalculator.getMoonInfo().getNextNewMoon().getMonth(), astroCalculator.getMoonInfo().getNextNewMoon().getYear()));
        mTextViewFullMoonDate.setText(String.format("%02d : %02d : %04d", astroCalculator.getMoonInfo().getNextFullMoon().getDay(), astroCalculator.getMoonInfo().getNextFullMoon().getMonth(), astroCalculator.getMoonInfo().getNextFullMoon().getYear()));
        mTextViewPhaseOfTheMoon.setText(String.format("%02.2f", astroCalculator.getMoonInfo().getIllumination()) + "%");
        mTextViewMoonAge.setText(String.format("%02.2f",astroCalculator.getMoonInfo().getAge()));
    }


}

