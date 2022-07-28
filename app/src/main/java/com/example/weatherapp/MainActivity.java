package com.example.weatherapp;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    private EditText etUserCityName;
    private Button bShowWeather;
    private TextView tvCityName, tvTemperature, tvDescription, tvFeelsLike, tvPressure, tvHumidity;

    private String defaultLink;
    private String cityName;
    private String temperature;
    private String tempFeelsLike;
    private String description;
    private String pressure;
    private String humidity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        init();
        setWeather(defaultLink);
        onClickButtonShowWeather();

    }

    void onClickButtonShowWeather() {
        bShowWeather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userInput = etUserCityName.getText().toString();
                if (!userInput.isEmpty()) {
                    String linkFormat = String.format(getString(R.string.link_format), userInput);
                    setWeather(linkFormat);
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.Enter_city_name), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    void setWeather(String link) {
        DownloadJSONTask task = new DownloadJSONTask();
        try {
            String strJSON = task.execute(link).get();
            JSONObject mainJSONObject = new JSONObject(strJSON);
            cityName = mainJSONObject.getString("name");

            JSONObject main = mainJSONObject.getJSONObject("main");
            String tempKelvin = main.getString("temp");
            long tempC = (Math.round(Double.parseDouble(tempKelvin))) - 273;
            temperature = String.valueOf(tempC);

            String tempFeels = main.getString("feels_like");
            long tempFeelsC = (Math.round(Double.parseDouble(tempFeels))) - 273;
            tempFeelsLike = String.valueOf(tempFeelsC);

            humidity = main.getString("humidity");

            pressure = main.getString("pressure");

            JSONArray arrayJSON = mainJSONObject.getJSONArray("weather");
            JSONObject weatherJSON = arrayJSON.getJSONObject(0);
            description = weatherJSON.getString("description");

        } catch (ExecutionException e) {
            Toast.makeText(getApplicationContext(), getString(R.string.Cant_found_this_city), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } catch (InterruptedException e) {
            Toast.makeText(getApplicationContext(), getString(R.string.Cant_found_this_city), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } catch (JSONException e) {
            Toast.makeText(getApplicationContext(), getString(R.string.Cant_found_this_city), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        tvCityName.setText(cityName);
        tvTemperature.setText(getString(R.string.Temperature) + temperature + getString(R.string.Celsius));
        tvFeelsLike.setText(getString(R.string.Feels_like) + tempFeelsLike + getString(R.string.Celsius));
        tvDescription.setText(getString(R.string.Outside) + description);
        tvHumidity.setText(getString(R.string.Humidity_) + humidity + getString(R.string.per_cent));
        tvPressure.setText(getString(R.string.Pressure_) + pressure);
    }

    void init() {
        etUserCityName = findViewById(R.id.edit_text_city_name);
        bShowWeather = findViewById(R.id.button_show_weather);
        tvCityName = findViewById(R.id.text_view_city_name);
        tvTemperature = findViewById(R.id.text_view_temperature);
        tvDescription = findViewById(R.id.text_view_description);
        tvFeelsLike = findViewById(R.id.text_view_temperature_feel);
        tvHumidity = findViewById(R.id.text_view_humidity);
        tvPressure = findViewById(R.id.text_view_pressure);

        defaultLink = getString(R.string.link);
    }


    private static class DownloadJSONTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {

            StringBuilder strBuilder = new StringBuilder();
            URL url = null;
            HttpURLConnection httpURLConnection = null;

            try {
                url = new URL(strings[0]);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream in = httpURLConnection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(in);
                BufferedReader reader = new BufferedReader(inputStreamReader);
                String line = reader.readLine();
                while (line != null) {
                    strBuilder.append(line);
                    line = reader.readLine();
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }
            }

            return strBuilder.toString();
        }
    }

}