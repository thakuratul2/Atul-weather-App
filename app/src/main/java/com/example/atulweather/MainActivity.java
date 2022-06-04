package com.example.atulweather;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.atulweather.WeatherAdapter.WeatherAdapter;
import com.example.atulweather.WeatherModal.WeatherModal;
import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.sql.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private RelativeLayout Home;
    private ProgressBar loading;
    private TextView cityNameTV,temp,condition;
    private RecyclerView Weather;
    private TextInputEditText cityEdit;
    private ImageView back,icon,search;
    private LocationManager locationManager;
    private int PERMISSION_CODE = 1;
    private ArrayList<WeatherModal> weatherModalArrayList;
    private WeatherAdapter weatherAdapter;
    private String cityName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_main);
        Home = findViewById(R.id.home);
        loading = findViewById(R.id.progrss);
        cityNameTV = findViewById(R.id.cityName);
        temp = findViewById(R.id.Temp);
        condition = findViewById(R.id.condition);
        Weather = findViewById(R.id.Recycler);
        cityEdit = findViewById(R.id.EditCity);
        back = findViewById(R.id.idBlack);
        icon = findViewById(R.id.icon);
        search = findViewById(R.id.search);

        weatherModalArrayList = new ArrayList<>();
        weatherAdapter = new WeatherAdapter(this,weatherModalArrayList);
        Weather.setAdapter(weatherAdapter);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED
        && ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},PERMISSION_CODE);
        }
        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        cityName = getCityName(location.getLongitude(),location.getLatitude());
        getWeatherInfo(cityName);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String city = cityEdit.getText().toString();
                if (city.isEmpty()){
                    Toast.makeText(MainActivity.this,"Please Enter City Name", Toast.LENGTH_SHORT).show();
                }else {
                    cityNameTV.setText(cityName);
                    getWeatherInfo(city);
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode== PERMISSION_CODE){
            if (grantResults.length>0 && grantResults[0]== PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this,"Permission Granted..", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(this,"Please Provide Permission" , Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private String getCityName(double longitude, double latitude){
        String cityName = "Not Found";
        Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
        try {

            List<Address> addresses = gcd.getFromLocation(latitude,longitude,10);
            for (Address adr : addresses){
                if(adr!= null){
                    String city = adr.getLocality();
                    if (city!= null && !city.equals("")){

                        cityName = city;
                    }else{
                        Log.d("TAG","City Not Found");
                        Toast.makeText(this,"User City Not Found",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        return  cityName;
    }

    private void getWeatherInfo(String cityName){
        String url = "http://api.weatherapi.com/v1/forecast.json?key=1b62a7c521d04d3c8a150318212608&q="+ cityName +"&days=1&aqi=yes&alerts=yes";
        cityNameTV.setText(cityName);

        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                loading.setVisibility(View.GONE);
                Home.setVisibility(View.VISIBLE);
                weatherModalArrayList.clear();

                try {
                    String temperture = response.getJSONObject("current").getString("temp_c");
                    temp.setText(temperture+"°C");
                    int isDay = response.getJSONObject("current").getInt("is_day");
                    String condition2 = response.getJSONObject("current").getJSONObject("condition").getString("text");
                    String conditionIcon = response.getJSONObject("current").getJSONObject("condition").getString("icon");

                    Picasso.get().load("https:".concat(conditionIcon)).into(icon);
                    condition.setText(condition2);
                    if (isDay==1){
                        //morning pic
                        Picasso.get().load("https://unsplash.com/photos/QGSD-bfJHIw").into(back);

                    }else{
                        //night pic
                        Picasso.get().load("https://unsplash.com/photos/sbcIAn4Mn14").into(back);
                    }
                    JSONObject forecast = response.getJSONObject("forecast");
                    JSONObject forecastO = forecast.getJSONArray("forecastday").getJSONObject(0);
                    JSONArray hourArray = forecastO.getJSONArray("hour");
                    for (int i = 0; i<hourArray.length(); i++){
                        JSONObject hourObj = hourArray.getJSONObject(i);
                        String time = hourObj.getString("time");
                        String temper = hourObj.getString("temp_c");
                        String img = hourObj.getJSONObject("condition").getString("icon");
                        String wind = hourObj.getString("wind_kph");
                        weatherModalArrayList.add(new WeatherModal(time,temper,img,wind));
                    }
                    weatherAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this,"Please Enter Valid City Name..",Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(jsonObjectRequest);
    }
}