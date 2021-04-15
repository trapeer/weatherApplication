package com.example.astrocalculator;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.media.ImageReader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class OptionsActivity extends AppCompatActivity {

    private SharedPreferences preferences;

    private Button mButtonOK;
    private EditText mEditTextCityName;
    private EditText mEditTextRefresh;
    private Switch switch1;
    private Spinner spinner;
    private Button mButtonUse;
    private Button mButtonModify;

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putString("cityName",  String.valueOf(mEditTextCityName.getText()));
        savedInstanceState.putString("refresh",  String.valueOf(mEditTextRefresh.getText()));
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            mEditTextCityName.setText(savedInstanceState.getString("cityName"));
            mEditTextRefresh.setText(savedInstanceState.getString("refresh"));
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);
        preferences = getSharedPreferences("file", MODE_PRIVATE);

        mButtonOK = findViewById(R.id.buttonOK);
        mEditTextCityName = findViewById(R.id.editTextCityName);
        mEditTextRefresh = findViewById(R.id.editTextRefresh);
        switch1 = findViewById(R.id.switch1);
        spinner = findViewById(R.id.spinner);
        mButtonUse = findViewById(R.id.use);
        mButtonModify = findViewById(R.id.modify);

        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add(preferences.getString("cityName1", "city1"));
        arrayList.add(preferences.getString("cityName2", "city2"));
        arrayList.add(preferences.getString("cityName3", "city3"));
        arrayList.add(preferences.getString("cityName4", "city4"));
        arrayList.add(preferences.getString("cityName5", "city5"));
         ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arrayList);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);

        mEditTextCityName.setText(getIntent().getStringExtra("cityName"));
        mEditTextRefresh.setText(Integer.toString(getIntent().getIntExtra("refresh",15)));
        if(getIntent().getBooleanExtra("isImperial", false)) switch1.setChecked(true);

        mButtonModify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = spinner.getSelectedItemPosition() + 1;
                if(position > 0 && position < 6 &&  !mEditTextCityName.getText().equals(""))
                {
                    SharedPreferences.Editor preferencesEditor = preferences.edit();
                    preferencesEditor.putString("cityName" + position, mEditTextCityName.getText().toString().substring(0, 1).toUpperCase() + mEditTextCityName.getText().toString().toLowerCase().substring(1));
                    preferencesEditor.putBoolean("isImperial" + position, switch1.isChecked());
                    preferencesEditor.commit();
                    ArrayList<String> arrayList = new ArrayList<>();
                    arrayList.add(preferences.getString("cityName1", "city1"));
                    arrayList.add(preferences.getString("cityName2", "city2"));
                    arrayList.add(preferences.getString("cityName3", "city3"));
                    arrayList.add(preferences.getString("cityName4", "city4"));
                    arrayList.add(preferences.getString("cityName5", "city5"));
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, arrayList);
                    arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner.setAdapter(arrayAdapter);
                }
                    }
                });

        mButtonUse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int position = spinner.getSelectedItemPosition() + 1;
                String url = "https://api.weatherapi.com/v1/forecast.json?key=eec2cda681494a70861183325202605&q="+preferences.getString("cityName" + position, "")+"&days=3";
                JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null,  new Response.Listener<JSONObject>(){
                    @Override
                    public void onResponse(JSONObject response)
                    {
                        try{
                            if(!isNetworkAvailable()) pobierzZBazyDanych(mEditTextCityName.getText().toString());
                            String location = response.getString("location");
                            JSONObject info = new JSONObject(location);
                            String cityName = info.getString("name");
                            double latitude = Double.parseDouble(info.getString("lat"));
                            double longitude = Double.parseDouble(info.getString("lon"));
                            String current = response.getString("current");
                            JSONObject day1 = new JSONObject(current);
                            String date1 = day1.getString("last_updated");
                            String temp1i =  day1.getString("temp_f");
                            String temp1m =  day1.getString("temp_c");
                            String maxWind1i =  day1.getString("wind_dir") + " " + day1.getString("wind_mph");
                            String maxWind1m =  day1.getString("wind_dir") + " " + day1.getString("wind_kph");
                            String visibility1i = day1.getString("vis_miles");
                            String visibility1m = day1.getString("vis_km");
                            String humidity1 = day1.getString("humidity");
                            final JSONObject condition1 = day1.getJSONObject("condition");
                            final String description1 = condition1.getString("text");
                            String forecast = response.getString("forecast");
                            info = new JSONObject(forecast);
                            JSONArray days = info.getJSONArray("forecastday");
                            JSONObject days2 = days.getJSONObject(1);
                            String date2 = days2.getString("date");
                            JSONObject day2 = days2.getJSONObject("day");
                            String temp2i =  day2.getString("avgtemp_f");
                            String temp2m =  day2.getString("avgtemp_c");
                            String maxWind2i =  day2.getString("maxwind_mph");
                            String maxWind2m =  day2.getString("maxwind_kph");
                            String visibility2i = day2.getString("avgvis_miles");
                            String visibility2m = day2.getString("avgvis_km");
                            String humidity2 = day2.getString("avghumidity");
                            final JSONObject condition2 = day2.getJSONObject("condition");
                            final String description2 = condition2.getString("text");
                            JSONObject days3 = days.getJSONObject(2);
                            String date3 = days3.getString("date");
                            JSONObject day3 = days3.getJSONObject("day");
                            String temp3i =  day3.getString("avgtemp_f");
                            String temp3m =  day3.getString("avgtemp_c");
                            String maxWind3i =  day3.getString("maxwind_mph");
                            String maxWind3m =  day3.getString("maxwind_kph");
                            String visibility3i = day3.getString("avgvis_miles");
                            String visibility3m = day3.getString("avgvis_km");
                            String humidity3 = day3.getString("avghumidity");
                            final JSONObject condition3 = day3.getJSONObject("condition");
                            final String description3 = condition3.getString("text");

                            Thread thread = new Thread() {
                                @Override
                                public void run() {
                                    try {
                                        InputStream is;
                                        is = (InputStream) new URL("https:" + condition1.getString("icon")).getContent();
                                        Drawable image1 = Drawable.createFromStream(is, description1);
                                        ParametersForFragments.image1 = image1;
                                        is = (InputStream) new URL("https:" + condition2.getString("icon")).getContent();
                                        Drawable image2 = Drawable.createFromStream(is, description2);
                                        ParametersForFragments.image2 = image2;
                                        is = (InputStream) new URL("https:" + condition3.getString("icon")).getContent();
                                        Drawable image3 = Drawable.createFromStream(is, description3);
                                        ParametersForFragments.image3 = image3;
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            };
                            thread.start();

                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            intent.putExtra("cityName", cityName);
                            intent.putExtra("lat", latitude);
                            intent.putExtra("lon", longitude);
                            intent.putExtra("refresh", Integer.parseInt(String.valueOf(mEditTextRefresh.getText())));
                            intent.putExtra("date1", date1);
                            intent.putExtra("date2",date2);
                            intent.putExtra("date3",date3);
                            intent.putExtra("humidity1",humidity1 + "%");
                            intent.putExtra("humidity2",humidity2 + "%");
                            intent.putExtra("humidity3",humidity3 + "%");
                            intent.putExtra("description1",description1 );
                            intent.putExtra("description2",description2 );
                            intent.putExtra("description3",description3 );
                            intent.putExtra("isImperial",switch1.isChecked());
                            if(preferences.getBoolean("isImperial" + position, false))
                            {
                                intent.putExtra("temp1",temp1i + " F" );
                                intent.putExtra("temp2",temp2i + " F" );
                                intent.putExtra("temp3",temp3i + " F" );
                                intent.putExtra("maxWind1",maxWind1i + " mph");
                                intent.putExtra("maxWind2",maxWind2i + " mph");
                                intent.putExtra("maxWind3",maxWind3i + " mph");
                                intent.putExtra("visibility1",visibility1i + " miles");
                                intent.putExtra("visibility2",visibility2i + " miles");
                                intent.putExtra("visibility3",visibility3i + " miles");
                            }
                            else{
                                intent.putExtra("temp1",temp1m + " C" );
                                intent.putExtra("temp2",temp2m + " C" );
                                intent.putExtra("temp3",temp3m + " C" );
                                intent.putExtra("maxWind1",maxWind1m + " kph");
                                intent.putExtra("maxWind2",maxWind2m + " kph");
                                intent.putExtra("maxWind3",maxWind3m + " kph");
                                intent.putExtra("visibility1",visibility1m + " km");
                                intent.putExtra("visibility2",visibility2m + " km");
                                intent.putExtra("visibility3",visibility3m + " km");
                            }
                            thread.join();
                            Realm.init(getApplicationContext());
                            Realm realm = Realm.getDefaultInstance();
                            realm.beginTransaction();
                            DataBaseObject result = realm.where(DataBaseObject.class).equalTo("cityName", cityName).findFirst();
                            if(result != null) result.deleteFromRealm();
                            DataBaseObject dataBaseObject = realm.createObject(DataBaseObject.class);
                            dataBaseObject.cityName = cityName;
                            dataBaseObject.longitude = longitude;
                            dataBaseObject.latitude = latitude;
                            dataBaseObject.date1 = date1;
                            dataBaseObject.date2 = date2;
                            dataBaseObject.date3 = date3;
                            dataBaseObject.humidity1 = humidity1 + "%";
                            dataBaseObject.humidity2 = humidity2 + "%";
                            dataBaseObject.humidity3 = humidity3 + "%";
                            dataBaseObject.description1 = description1;
                            dataBaseObject.description2 = description2;
                            dataBaseObject.description3 = description3;
                            dataBaseObject.temp1i = temp1i + " F" ;
                            dataBaseObject.temp2i = temp2i + " F" ;
                            dataBaseObject.temp3i = temp3i + " F" ;
                            dataBaseObject.temp1m = temp1m + " C" ;
                            dataBaseObject.temp2m = temp2m + " C" ;
                            dataBaseObject.temp3m = temp3m + " C" ;
                            dataBaseObject.maxWind1i = maxWind1i + " mph";
                            dataBaseObject.maxWind2i = maxWind2i + " mph";
                            dataBaseObject.maxWind3i = maxWind3i + " mph";
                            dataBaseObject.maxWind1m = maxWind1m + " kph";
                            dataBaseObject.maxWind2m = maxWind2m + " kph";
                            dataBaseObject.maxWind3m = maxWind3m + " kph";
                            dataBaseObject.visibility1i = visibility1i + " miles";
                            dataBaseObject.visibility2i = visibility2i + " miles";
                            dataBaseObject.visibility3i = visibility3i + " miles";
                            dataBaseObject.visibility1m = visibility1m + " km";
                            dataBaseObject.visibility2m = visibility2m + " km";
                            dataBaseObject.visibility3m = visibility3m + " km";
                            Bitmap bitmap = ((BitmapDrawable)ParametersForFragments.image1).getBitmap();
                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.WEBP,100,stream);
                            dataBaseObject.image1 = stream.toByteArray();
                            bitmap = ((BitmapDrawable)ParametersForFragments.image2).getBitmap();
                            stream = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.WEBP,100,stream);
                            dataBaseObject.image2 = stream.toByteArray();
                            bitmap = ((BitmapDrawable)ParametersForFragments.image3).getBitmap();
                            stream = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.WEBP,100,stream);
                            dataBaseObject.image3 = stream.toByteArray();
                            realm.commitTransaction();
                            startActivity(intent);
                        }
                        catch(Exception e){ }
                    }
                }, new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pobierzZBazyDanych(mEditTextCityName.getText().toString());
                    }
                });
                RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                queue.add(jor);
            }
        });

        mButtonOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://api.weatherapi.com/v1/forecast.json?key=eec2cda681494a70861183325202605&q="+String.valueOf(mEditTextCityName.getText())+"&days=3";
                JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null,  new Response.Listener<JSONObject>(){
                    @Override
                    public void onResponse(JSONObject response)
                    {
                        try{
                            if(!isNetworkAvailable()) pobierzZBazyDanych(mEditTextCityName.getText().toString());
                            String location = response.getString("location");
                            JSONObject info = new JSONObject(location);
                            String cityName = info.getString("name");
                            double latitude = Double.parseDouble(info.getString("lat"));
                            double longitude = Double.parseDouble(info.getString("lon"));
                            String current = response.getString("current");
                            JSONObject day1 = new JSONObject(current);
                            String date1 = day1.getString("last_updated");
                            String temp1i =  day1.getString("temp_f");
                            String temp1m =  day1.getString("temp_c");
                            String maxWind1i =  day1.getString("wind_dir") + " " + day1.getString("wind_mph");
                            String maxWind1m =  day1.getString("wind_dir") + " " + day1.getString("wind_kph");
                            String visibility1i = day1.getString("vis_miles");
                            String visibility1m = day1.getString("vis_km");
                            String humidity1 = day1.getString("humidity");
                            final JSONObject condition1 = day1.getJSONObject("condition");
                            final String description1 = condition1.getString("text");
                            String forecast = response.getString("forecast");
                            info = new JSONObject(forecast);
                            JSONArray days = info.getJSONArray("forecastday");
                            JSONObject days2 = days.getJSONObject(1);
                            String date2 = days2.getString("date");
                            JSONObject day2 = days2.getJSONObject("day");
                            String temp2i =  day2.getString("avgtemp_f");
                            String temp2m =  day2.getString("avgtemp_c");
                            String maxWind2i =  day2.getString("maxwind_mph");
                            String maxWind2m =  day2.getString("maxwind_kph");
                            String visibility2i = day2.getString("avgvis_miles");
                            String visibility2m = day2.getString("avgvis_km");
                            String humidity2 = day2.getString("avghumidity");
                            final JSONObject condition2 = day2.getJSONObject("condition");
                            final String description2 = condition2.getString("text");
                            JSONObject days3 = days.getJSONObject(2);
                            String date3 = days3.getString("date");
                            JSONObject day3 = days3.getJSONObject("day");
                            String temp3i =  day3.getString("avgtemp_f");
                            String temp3m =  day3.getString("avgtemp_c");
                            String maxWind3i =  day3.getString("maxwind_mph");
                            String maxWind3m =  day3.getString("maxwind_kph");
                            String visibility3i = day3.getString("avgvis_miles");
                            String visibility3m = day3.getString("avgvis_km");
                            String humidity3 = day3.getString("avghumidity");
                            final JSONObject condition3 = day3.getJSONObject("condition");
                            final String description3 = condition3.getString("text");

                            Thread thread = new Thread() {
                                @Override
                                public void run() {
                                    try {
                                        InputStream is;
                                        is = (InputStream) new URL("https:" + condition1.getString("icon")).getContent();
                                        Drawable image1 = Drawable.createFromStream(is, description1);
                                        ParametersForFragments.image1 = image1;
                                        is = (InputStream) new URL("https:" + condition2.getString("icon")).getContent();
                                        Drawable image2 = Drawable.createFromStream(is, description2);
                                        ParametersForFragments.image2 = image2;
                                        is = (InputStream) new URL("https:" + condition3.getString("icon")).getContent();
                                        Drawable image3 = Drawable.createFromStream(is, description3);
                                        ParametersForFragments.image3 = image3;
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            };
                            thread.start();

                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            intent.putExtra("cityName", cityName);
                            intent.putExtra("lat", latitude);
                            intent.putExtra("lon", longitude);
                            intent.putExtra("refresh", Integer.parseInt(String.valueOf(mEditTextRefresh.getText())));
                            intent.putExtra("date1", date1);
                            intent.putExtra("date2",date2);
                            intent.putExtra("date3",date3);
                            intent.putExtra("humidity1",humidity1 + "%");
                            intent.putExtra("humidity2",humidity2 + "%");
                            intent.putExtra("humidity3",humidity3 + "%");
                            intent.putExtra("description1",description1 );
                            intent.putExtra("description2",description2 );
                            intent.putExtra("description3",description3 );
                            intent.putExtra("isImperial",switch1.isChecked());
                            if(switch1.isChecked())
                            {
                                intent.putExtra("temp1",temp1i + " F" );
                                intent.putExtra("temp2",temp2i + " F" );
                                intent.putExtra("temp3",temp3i + " F" );
                                intent.putExtra("maxWind1",maxWind1i + " mph");
                                intent.putExtra("maxWind2",maxWind2i + " mph");
                                intent.putExtra("maxWind3",maxWind3i + " mph");
                                intent.putExtra("visibility1",visibility1i + " miles");
                                intent.putExtra("visibility2",visibility2i + " miles");
                                intent.putExtra("visibility3",visibility3i + " miles");
                            }
                            else{
                                intent.putExtra("temp1",temp1m + " C" );
                                intent.putExtra("temp2",temp2m + " C" );
                                intent.putExtra("temp3",temp3m + " C" );
                                intent.putExtra("maxWind1",maxWind1m + " kph");
                                intent.putExtra("maxWind2",maxWind2m + " kph");
                                intent.putExtra("maxWind3",maxWind3m + " kph");
                                intent.putExtra("visibility1",visibility1m + " km");
                                intent.putExtra("visibility2",visibility2m + " km");
                                intent.putExtra("visibility3",visibility3m + " km");
                            }
                            thread.join();
                            Realm.init(getApplicationContext());
                            Realm realm = Realm.getDefaultInstance();
                            realm.beginTransaction();
                            DataBaseObject result = realm.where(DataBaseObject.class).equalTo("cityName", cityName).findFirst();
                            if(result != null) result.deleteFromRealm();
                            DataBaseObject dataBaseObject = realm.createObject(DataBaseObject.class);
                            dataBaseObject.cityName = cityName;
                            dataBaseObject.longitude = longitude;
                            dataBaseObject.latitude = latitude;
                            dataBaseObject.date1 = date1;
                            dataBaseObject.date2 = date2;
                            dataBaseObject.date3 = date3;
                            dataBaseObject.humidity1 = humidity1 + "%";
                            dataBaseObject.humidity2 = humidity2 + "%";
                            dataBaseObject.humidity3 = humidity3 + "%";
                            dataBaseObject.description1 = description1;
                            dataBaseObject.description2 = description2;
                            dataBaseObject.description3 = description3;
                            dataBaseObject.temp1i = temp1i + " F" ;
                            dataBaseObject.temp2i = temp2i + " F" ;
                            dataBaseObject.temp3i = temp3i + " F" ;
                            dataBaseObject.temp1m = temp1m + " C" ;
                            dataBaseObject.temp2m = temp2m + " C" ;
                            dataBaseObject.temp3m = temp3m + " C" ;
                            dataBaseObject.maxWind1i = maxWind1i + " mph";
                            dataBaseObject.maxWind2i = maxWind2i + " mph";
                            dataBaseObject.maxWind3i = maxWind3i + " mph";
                            dataBaseObject.maxWind1m = maxWind1m + " kph";
                            dataBaseObject.maxWind2m = maxWind2m + " kph";
                            dataBaseObject.maxWind3m = maxWind3m + " kph";
                            dataBaseObject.visibility1i = visibility1i + " miles";
                            dataBaseObject.visibility2i = visibility2i + " miles";
                            dataBaseObject.visibility3i = visibility3i + " miles";
                            dataBaseObject.visibility1m = visibility1m + " km";
                            dataBaseObject.visibility2m = visibility2m + " km";
                            dataBaseObject.visibility3m = visibility3m + " km";
                            Bitmap bitmap = ((BitmapDrawable)ParametersForFragments.image1).getBitmap();
                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.WEBP,100,stream);
                            dataBaseObject.image1 = stream.toByteArray();
                            bitmap = ((BitmapDrawable)ParametersForFragments.image2).getBitmap();
                            stream = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.WEBP,100,stream);
                            dataBaseObject.image2 = stream.toByteArray();
                            bitmap = ((BitmapDrawable)ParametersForFragments.image3).getBitmap();
                            stream = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.WEBP,100,stream);
                            dataBaseObject.image3 = stream.toByteArray();
                            realm.commitTransaction();
                            startActivity(intent);
                        }
                        catch(Exception e){ }
                    }
                }, new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                            pobierzZBazyDanych(mEditTextCityName.getText().toString());
                    }
                });
                RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                queue.add(jor);
            }
        });

    }

    public void pobierzZBazyDanych(String cityName)
    {
        Realm.init(getApplicationContext());
        Realm realm = Realm.getDefaultInstance();
        DataBaseObject result = realm.where(DataBaseObject.class).equalTo("cityName", cityName).findFirst();
        if(result == null)
        {
            Toast toast = Toast.makeText(getApplicationContext(), "no internet connection", Toast.LENGTH_SHORT);
            toast.show();
        }
        else{
            Toast toast = Toast.makeText(getApplicationContext(), "no internet connection import local data", Toast.LENGTH_SHORT);
            toast.show();
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.putExtra("cityName", result.cityName);
            intent.putExtra("lat", result.latitude);
            intent.putExtra("lon", result.longitude);
            intent.putExtra("refresh", Integer.parseInt(String.valueOf(mEditTextRefresh.getText())));
            intent.putExtra("date1",result.date1);
            intent.putExtra("date2",result.date2 );
            intent.putExtra("date3",result.date3 );
            intent.putExtra("humidity1",result.humidity1);
            intent.putExtra("humidity2",result.humidity2);
            intent.putExtra("humidity3",result.humidity3);
            intent.putExtra("description1",result.description1 );
            intent.putExtra("description2",result.description2 );
            intent.putExtra("description3",result.description3 );
            intent.putExtra("isImperial",switch1.isChecked());
            if(switch1.isChecked())
            {
                intent.putExtra("temp1",result.temp1i);
                intent.putExtra("temp2",result.temp2i);
                intent.putExtra("temp3",result.temp3i);
                intent.putExtra("maxWind1",result.maxWind1i);
                intent.putExtra("maxWind2",result.maxWind2i);
                intent.putExtra("maxWind3",result.maxWind3i);
                intent.putExtra("visibility1",result.visibility1i);
                intent.putExtra("visibility2",result.visibility2i);
                intent.putExtra("visibility3",result.visibility3i);
            }
            else{
                intent.putExtra("temp1",result.temp1m);
                intent.putExtra("temp2",result.temp2m);
                intent.putExtra("temp3",result.temp3m);
                intent.putExtra("maxWind1",result.maxWind1m);
                intent.putExtra("maxWind2",result.maxWind2m);
                intent.putExtra("maxWind3",result.maxWind3m);
                intent.putExtra("visibility1",result.visibility1m);
                intent.putExtra("visibility2",result.visibility2m);
                intent.putExtra("visibility3",result.visibility3m);
            }
            ParametersForFragments.image1 = new BitmapDrawable(getResources(), BitmapFactory.decodeByteArray(result.image1,0,result.image1.length));
            ParametersForFragments.image2 = new BitmapDrawable(getResources(), BitmapFactory.decodeByteArray(result.image2,0,result.image2.length));
            ParametersForFragments.image3 = new BitmapDrawable(getResources(), BitmapFactory.decodeByteArray(result.image3,0,result.image3.length));
            startActivity(intent);
        }
    }

}