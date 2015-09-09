package com.mobvoi.ticwear.stepdemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        Button stepBtn = (Button) findViewById(R.id.step_btn);
        Button weatherBtn = (Button) findViewById(R.id.weather_btn);
        stepBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent stepIntent = new Intent(MainActivity.this, StepActivity.class);
                startActivity(stepIntent);
            }
        });
        weatherBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent weatherIntent = new Intent(MainActivity.this, WeatherActivity.class);
                startActivity(weatherIntent);
            }
        });
    }
}
