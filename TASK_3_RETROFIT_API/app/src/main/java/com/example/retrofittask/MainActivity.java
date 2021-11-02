package com.example.retrofittask;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private TextView totalConfirm,totalActive,totalRecovered,totalDeath,totalTest,todayConfirm,todayRecovered,todayDeath,date;
    private List<Data> list;
    private String selectecCountry;
    private Spinner staticSpinner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        totalConfirm=findViewById(R.id.totalConfirm);
        totalActive=findViewById(R.id.totalActive);
        totalRecovered=findViewById(R.id.totalRecovered);
        totalDeath=findViewById(R.id.totalDeath);
        totalTest=findViewById(R.id.totalTest);
        todayConfirm=findViewById(R.id.todayConfirm);
        todayDeath=findViewById(R.id.todayDeath);
        todayRecovered=findViewById(R.id.todayRecovered);
        date=findViewById(R.id.date);

        staticSpinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<String> staticAdapter =new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
        staticAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        staticSpinner.setAdapter(staticAdapter);


        if (haveNetworkConnection() == false){

            SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("covidData", Context.MODE_PRIVATE);
            String tconfimr = sharedPref.getString("tconfirm", null);
            totalConfirm.setText(tconfimr);
            String tactive = sharedPref.getString("tactive", "123");
            totalActive.setText(tactive);
            String trecovered = sharedPref.getString("trecovered", null);
            totalRecovered.setText(trecovered);
            String tdeath = sharedPref.getString("tdeath", null);
            totalDeath.setText(tdeath);
            String test = sharedPref.getString("test", null);
            totalTest.setText(test);
            String toDeath = sharedPref.getString("todayDeath", null);
            todayDeath.setText(toDeath);
            String toConfirm = sharedPref.getString("todayConfirm", null);
            todayConfirm.setText(toConfirm);
            String toRecovered = sharedPref.getString("todayRecovered", null);
            todayRecovered.setText(toRecovered);
            String sdate = sharedPref.getString("date", null);
            date.setText(sdate);
            String scountry = sharedPref.getString("country", null);
            staticAdapter.add(scountry);
            staticAdapter.notifyDataSetChanged();


        }
        else {
            list=new ArrayList<>();

            ApiUtilities.apiInterface().getData()
                    .enqueue(new Callback<List<Data>>() {
                        @Override
                        public void onResponse(Call<List<Data>> call, Response<List<Data>> response) {
                            list.addAll(response.body());
                            for (int j=0; j<list.size(); j++){
                                String sp=list.get(j).getCountry();
                                staticAdapter.add(sp);
                                staticAdapter.notifyDataSetChanged();
                            }

                            staticSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> adapterView, View view, int k, long l) {
                                    selectecCountry= staticSpinner.getSelectedItem().toString();
                                    for (int i=0; i<list.size(); i++){
                                        if (list.get(i).getCountry().equals(selectecCountry)){
                                            int confirm=Integer.parseInt(list.get(i).getCases());
                                            int active=Integer.parseInt(list.get(i).getActive());
                                            int recovered=Integer.parseInt(list.get(i).getRecovered());
                                            int death=Integer.parseInt(list.get(i).getDeaths());
                                            int tests=Integer.parseInt(list.get(i).getTests());


                                            totalActive.setText(NumberFormat.getInstance().format(active));
                                            totalConfirm.setText(NumberFormat.getInstance().format(confirm));
                                            totalRecovered.setText(NumberFormat.getInstance().format(recovered));
                                            totalDeath.setText(NumberFormat.getInstance().format(death));
                                            totalTest.setText(NumberFormat.getInstance().format(tests));

                                            todayDeath.setText("(+"+NumberFormat.getInstance().format(Integer.parseInt(list.get(i).getTodayDeaths()))+")");
                                            todayConfirm.setText("(+"+NumberFormat.getInstance().format(Integer.parseInt(list.get(i).getTodayCases()))+")");
                                            todayRecovered.setText("(+"+NumberFormat.getInstance().format(Integer.parseInt(list.get(i).getTodayRecovered()))+")");

                                            setText(list.get(i).getUpdated());

                                        }
                                    }

                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> adapterView) {

                                }
                            });

                        }

                        @Override
                        public void onFailure(Call<List<Data>> call, Throwable t) {
                            Toast.makeText(MainActivity.this, "Cant fetch data,Showing previously saved data", Toast.LENGTH_SHORT).show();
                            SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("covidData", Context.MODE_PRIVATE);
                            String tconfimr = sharedPref.getString("tconfirm", null);
                            totalConfirm.setText(tconfimr);
                            String tactive = sharedPref.getString("tactive", "123");
                            totalActive.setText(tactive);
                            String trecovered = sharedPref.getString("trecovered", null);
                            totalRecovered.setText(trecovered);
                            String tdeath = sharedPref.getString("tdeath", null);
                            totalDeath.setText(tdeath);
                            String test = sharedPref.getString("test", null);
                            totalTest.setText(test);
                            String toDeath = sharedPref.getString("todayDeath", null);
                            todayDeath.setText(toDeath);
                            String toConfirm = sharedPref.getString("todayConfirm", null);
                            todayConfirm.setText(toConfirm);
                            String toRecovered = sharedPref.getString("todayRecovered", null);
                            todayRecovered.setText(toRecovered);
                            String sdate = sharedPref.getString("date", null);
                            date.setText(sdate);
                            String scountry = sharedPref.getString("country", null);
                            int spinnerpos=staticAdapter.getPosition(scountry);
                            staticSpinner.setSelection(spinnerpos);

                        }
                    });

        }

    }

    private boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

    private void setText(String updated) {
        DateFormat format=new SimpleDateFormat("MMM dd,yyyy");

        long milisecond=Long.parseLong(updated);

        Calendar calendar=Calendar.getInstance();
        calendar.setTimeInMillis(milisecond);

        date.setText("Updated at "+format.format(calendar.getTime()));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferences sharedPref = getSharedPreferences("covidData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("tconfirm", totalConfirm.getText().toString());
        editor.putString("tactive", totalActive.getText().toString());
        editor.putString("trecovered", totalRecovered.getText().toString());
        editor.putString("tdeath", totalDeath.getText().toString());
        editor.putString("test", totalTest.getText().toString());
        editor.putString("todayDeath", todayDeath.getText().toString());
        editor.putString("todayConfirm", todayConfirm.getText().toString());
        editor.putString("todayRecovered", todayRecovered.getText().toString());
        editor.putString("date", date.getText().toString());
        editor.putString("country", staticSpinner.getSelectedItem().toString());

        editor.apply();
    }

}