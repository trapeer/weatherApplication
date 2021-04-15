package com.example.astrocalculator;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.fragment.app.Fragment;

public class Weather1 extends Fragment {
    private TextView mTextViewDate;
    private TextView mTextViewDescription;
    private TextView mTextViewHumidity;
    private TextView mTextViewTemp;
    private TextView mTextViewMaxWind;
    private TextView mTextViewVisibility;
    private ImageView imageView;
    String date;
    String description;
    String humidity;
    String temp;
    String maxWind;
    String visibility;
    Drawable image;

    private Context context;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    public Weather1(String date, String description, String humidity, String temp, String maxWind, String visibility)
    {
        this.date = date;
        this.description = description;
        this.humidity = humidity;
        this.temp = temp;
        this.maxWind = maxWind;
        this.visibility= visibility;
        this.image = ParametersForFragments.image1;
    }
    public Weather1()
    {
            this.date = ParametersForFragments.date1;
            this.description = ParametersForFragments.description1;
            this.humidity = ParametersForFragments.humidity1;
            this.temp = ParametersForFragments.temp1;
            this.maxWind = ParametersForFragments.maxWind1;
            this.visibility= ParametersForFragments.visibility1;
            this.image = ParametersForFragments.image1;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.weather, container, false);

        mTextViewDate = rootView.findViewById(R.id.date);
        mTextViewDescription = rootView.findViewById(R.id.description);
        mTextViewHumidity = rootView.findViewById(R.id.humidity);
        mTextViewTemp = rootView.findViewById(R.id.temp);
        mTextViewMaxWind = rootView.findViewById(R.id.maxWind);
        mTextViewVisibility = rootView.findViewById(R.id.visibility);
        imageView = rootView.findViewById(R.id.imageView);
        imageView.setImageDrawable(image);
        mTextViewDate.setText(date);
        mTextViewDescription.setText("weather: " + description);
        mTextViewHumidity.setText("Humidity: " + humidity);
        mTextViewTemp.setText("temperature: " + temp);
        mTextViewMaxWind.setText("Wind: " + maxWind);
        mTextViewVisibility.setText("visibility: " + visibility);
        return rootView;
    }
}

