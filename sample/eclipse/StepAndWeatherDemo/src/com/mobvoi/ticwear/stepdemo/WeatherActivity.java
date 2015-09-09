package com.mobvoi.ticwear.stepdemo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

public class WeatherActivity extends Activity {
    private static final Uri WEATHER_URI = Uri.parse("content://com.mobvoi.provider.weather");
    /*
     * time 时间 temp 温度 address 具体地点 location 地区 maxtemp 最高温度 mintemp 最低温度 pm25
     * PM2.5 weather 天气情况 sunset 日落时间 sunrise 日出时间
     */
    private static final String[] COLUMN_NAMES = { "time", "temp", "address", "location", "maxtemp", "mintemp", "pm25",
            "weather", "sunset", "sunrise" };

    private ContentResolver mResolver;
    private WeatherInfo mInfo;
    private ContentObserver mObserver;

    private TextView mWeatherTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        init();
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (mInfo != null) {
                mWeatherTv.setText(mInfo.toString());
            }
        }

    };

    private void init() {
        mWeatherTv = (TextView) findViewById(R.id.weather_tv);
        mResolver = this.getContentResolver();
        mObserver = new ContentObserver(mHandler) {
            @Override
            public boolean deliverSelfNotifications() {
                return super.deliverSelfNotifications();
            }

            @Override
            public void onChange(boolean selfChange) {
                super.onChange(selfChange);
                mInfo = fetchWeatherInfo();
                Message.obtain(mHandler, 0).sendToTarget();
            }
        };
        mInfo = fetchWeatherInfo();
        if (mInfo != null) {
            mWeatherTv.setText(mInfo.toString());
        }
        registerContentObserver();
    }

    private WeatherInfo fetchWeatherInfo() {
        Cursor cursor = mResolver.query(WEATHER_URI, COLUMN_NAMES, null, null, null);
        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    WeatherInfo info = new WeatherInfo();
                    info.time = cursor.getString(cursor.getColumnIndex(COLUMN_NAMES[0]));
                    info.temp = cursor.getString(cursor.getColumnIndex(COLUMN_NAMES[1]));
                    info.address = cursor.getString(cursor.getColumnIndex(COLUMN_NAMES[2]));
                    info.location = cursor.getString(cursor.getColumnIndex(COLUMN_NAMES[3]));
                    info.maxtemp = cursor.getString(cursor.getColumnIndex(COLUMN_NAMES[4]));
                    info.mintemp = cursor.getString(cursor.getColumnIndex(COLUMN_NAMES[5]));
                    info.pm25 = cursor.getString(cursor.getColumnIndex(COLUMN_NAMES[6]));
                    info.weather = cursor.getString(cursor.getColumnIndex(COLUMN_NAMES[7]));
                    info.sunset = cursor.getString(cursor.getColumnIndex(COLUMN_NAMES[8]));
                    info.sunrise = cursor.getString(cursor.getColumnIndex(COLUMN_NAMES[9]));
                    return info;
                }
            } finally {
                cursor.close();
            }
        }
        return null;
    }

    private void registerContentObserver() {
        mResolver.registerContentObserver(WEATHER_URI, true, mObserver);
    }

    private void unregisterContentObserver() {
        mResolver.unregisterContentObserver(mObserver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterContentObserver();
    }
}
