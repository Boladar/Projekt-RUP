package com.example.wearapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.wearapp.Notification.AlarmReceiver;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    // widgets
    private EditText adresM;
    private EditText adresP;
    private EditText godzina;
    private EditText editData;
//    private TextView txtShow;

    // for trace api
    private String homeAddress;
    private String workAddress;
    private String hourOfWorkingStart;
    private String dataOfWorkingStart;
    private String patternDate;

    private SimpleDateFormat datetimeFormat;
    private Date timeOfWorkingStart;
    private SimpleDateFormat timeFormat;
    private String timeOfAwaking;
    long epoch;
    String epochS;
    private final List<String> data = new ArrayList<>();

    // for weather api
    private String cloth;
    private int time;


    class WeatherAPI {

        public String getWeather(String... strings) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(strings[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuilder buffer = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null)
                    buffer.append(line).append("\n");

                return buffer.toString();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null)
                    connection.disconnect();
                try {
                    if (reader != null)
                        reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }


        public void parseWeather(String s) {
            JSONObject info = null;
            try {
                info = new JSONObject(s);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            JSONObject day = null;
            try {
                assert info != null;
                day = info.getJSONObject("forecast")
                        .getJSONArray("forecastday")
                        .getJSONObject(0)
                        .getJSONObject("day");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            double avgTemp = 0;
            try {
                assert day != null;
                avgTemp = day.getDouble("avgtemp_c");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            JSONObject condition = null;
            try {
                condition = day.getJSONObject("condition");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String weather = null;
            try {
                assert condition != null;
                weather = condition.getString("text");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            ClothingService clothes = new ClothingService();
            clothes.setClothes(avgTemp);
            assert weather != null;
            clothes.addAccessories(weather);
            cloth = clothes.getClothes();
            time = clothes.getTime();
        }
    }

    class TraceData {

        public void getTraceByJson(String url) throws JSONException {
            //zwraca czas, kiedy trzeba wyjsc z domu i trase
            JSONObject info;
            info = new JSONObject(url);
            String departure_time_value;
            String departure_time_text;
            String line_transport;
            departure_time_value = info.getJSONArray("routes")
                    .getJSONObject(0)
                    .getJSONArray("legs")
                    .getJSONObject(0)
                    .getJSONObject("departure_time")
                    .getString("value");
            departure_time_text = info.getJSONArray("routes")
                    .getJSONObject(0)
                    .getJSONArray("legs")
                    .getJSONObject(0)
                    .getJSONObject("departure_time")
                    .getString("text");
            line_transport = info.getJSONArray("routes")
                    .getJSONObject(0)
                    .getJSONArray("legs")
                    .getJSONObject(0)
                    .getJSONArray("steps")
                    .getJSONObject(1)
                    .getJSONObject("transit_details")
                    .getJSONObject("line")
                    .getString("short_name");

            data.add(departure_time_value);
            data.add(departure_time_text);
            data.add(line_transport);
        }

        public void run() {
            try {

                URL url = new URL("https://maps.googleapis.com/maps/api/directions/json?origin=Os.SobieskiegoPoznan&destination=Dru%C5%BCbickiego2,Pozna%C5%84&key=AIzaSyCNx1cp5ReJvuzJ5XqCBijNxy2B0mAUl_s&mode=transit");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                connection.setRequestMethod("GET");
                connection.connect();

                InputStream stream = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

                StringBuilder buffer = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null)
                    buffer.append(line).append("\n");

                String trace_data = buffer.toString();
                getTraceByJson(trace_data);


            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void createNotification(String title, String message, int hour, int minute) {

        //Alarm/notification data
        final int notificationId = 1;
        Intent alarmIntent = new Intent(MainActivity.this, AlarmReceiver.class);

        //Intent
        alarmIntent.putExtra("notificationId", notificationId);
        alarmIntent.putExtra("title", title);
        alarmIntent.putExtra("message", message);
        alarmIntent.putExtra("homeAddress", homeAddress);
        alarmIntent.putExtra("workAddress", workAddress);

        //PendingIntent
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                MainActivity.this, 0, alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT
        );

        //AlarmManager
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        // Create time.
        Calendar startTime = Calendar.getInstance();
        startTime.set(Calendar.HOUR_OF_DAY, hour);
        startTime.set(Calendar.MINUTE, minute);
        startTime.set(Calendar.SECOND, 0);
        long alarmStartTime = startTime.getTimeInMillis();

        // Set Alarm
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarmStartTime, pendingIntent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        adresM = (EditText) findViewById(R.id.adresM);
        adresP = (EditText) findViewById(R.id.adresP);
        godzina = (EditText) findViewById(R.id.godzina);
        editData = (EditText) findViewById(R.id.data);
//        txtShow = (TextView) findViewById(R.id.textTest);
        Button button = (Button) findViewById(R.id.button);
        Button button2 = (Button) findViewById(R.id.button2);


        //dla uruchomenia api tras -> new TraceData.start(), resultat w trace_json

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {

//                    FileOutputStream fileOut = openFileOutput("test.json",MODE_PRIVATE);
//                    fileOut.write(myTxt.getBytes(StandardCharsets.UTF_8));
//                    fileOut.close();
//                    adresM.setText("");
//                    adresP.setText("");
//                    godzina.setText("");

                    // Information from widgets
                    homeAddress = adresM.getText().toString();
                    workAddress = adresP.getText().toString();
                    hourOfWorkingStart = godzina.getText().toString();
                    dataOfWorkingStart = editData.getText().toString();

                    // Parsing date for TraceData
                    patternDate = dataOfWorkingStart + " " + hourOfWorkingStart + ":00";
                    datetimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    timeOfWorkingStart = datetimeFormat.parse(patternDate);
                    assert timeOfWorkingStart != null;
                    epoch = (timeOfWorkingStart.getTime() / 1000);
                    epochS = Long.toString(epoch);

                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                    StrictMode.setThreadPolicy(policy);

                    new TraceData().run();

                    // Options for WeatherAPI
                    String city = "Poznan";
                    String key = "992e7cab06164165980213921210812";
                    String url = "https://api.weatherapi.com/v1/history.json?key=" + key +
                            "&q=" + city + "&dt=" + dataOfWorkingStart;

                    WeatherAPI weatherAPI = new WeatherAPI();
                    weatherAPI.parseWeather(weatherAPI.getWeather(url));

                    // Parsing time for create notification
                    timeFormat = new SimpleDateFormat("HH:mm");
                    //long millis = Long.parseLong(String.valueOf(Integer.parseInt(data.get(0))-(time*60+20*60)));
                    long millis = System.currentTimeMillis();
                    timeOfAwaking = timeFormat.format(new Date(millis));
                    String hourWake = timeOfAwaking.substring(0, 2);
                    String minuteWake = timeOfAwaking.substring(3, 5);
                    createNotification("Wake Up", "Tramwaj numer: " + data.get(2) + "/Godzina odjazdu:" + data.get(1)
                            + "/Ubranie:" + cloth + "/ Kliknij aby przejśc do map Google", Integer.parseInt(hourWake), Integer.parseInt(minuteWake));

                    Toast.makeText(MainActivity.this, "Powiadomienie nadejdzie o " + timeOfAwaking, Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                try {
//                    FileInputStream fileIn = openFileInput("test.json");
//                    InputStreamReader reader = new InputStreamReader(fileIn);
//                    BufferedReader bufferread = new BufferedReader(reader);
//                    StringBuffer strbuff = new StringBuffer();
//                    String str;
//                    while ((str = bufferread.readLine()) != null) {
//                        strbuff.append(str + "\n");
//                    }
                Toast.makeText(MainActivity.this, "Powiadomienie nadejdzie o " + timeOfAwaking, Toast.LENGTH_LONG).show();
//                } catch (FileNotFoundException e) {
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
            }
        });
    }
}