## Weather API

For details, refer to Github inside Ticwear SDK open source project [Eclipse-based weather sample program][weather-example].

### Brief introduction

Weather data API is a Ticwear system provided weather data interface to facilitate developers to call on this data to develop more diversified applications.

Currently, the system has built-in quick cards: date, settings, weather, pedometer and music controls. The third quick card, weather uses the weather API data.

### Use

In order to use of the weather API, we need to define a `ContentResolver` and a `ContentObserver` in the watch applications. `ContentResolver` is used to obtain the corresponding URI data; `ContentObserver` is used to observe changes in listening content data, after data changes you use `ContentResolver` to get the latest data.

In order to update the UI after the data changes, you can define a `Handler` passed as a parameter to `ContentObserver`, to update the UI.

### Code Example

The frequency of weather data updates: automatically updated every hour, and every time the weather card drops down, if the last update time was more than 10 minutes ago, it will also update.

``` Java
// Weather data format example:
{
  "time": "1442105837697",         // time
  "temp": "19",                    // temperature
  "address": "Wayne St, Boalsburg, PA 16827, USA",  // specific location
  "location": "Brooklyn",               // region
  "maxtemp": "29",                 // maximum temperature
  "mintemp": "14",                 // minimum temperature
  "pm25": "50",                    // PM2.5
  "weather": "cloudy",                 // weather
  "sunset": "18:28",               // sunset time
  "sunrise": "05:53"               // sunrise time
}

// Get the weather data ContentProvider
private static final Uri WEATHER_URI = Uri.parse("content://com.mobvoi.provider.weather");
private static final String[] COLUMN_NAMES = { "time", "temp", "address", "location", "maxtemp", "mintemp", "pm25",
        "weather", "sunset", "sunrise" };
// Define the content resolver get weather data URI's
private ContentResolver mResolver = this.getContentResolver();
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

// Define the content of an observer to monitor weather data changes
private WeatherInfo mInfo;
ContentObserver mObserver = new ContentObserver(mHandler) {
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

// Define Handler result to process data changes
private Handler mHandler = new Handler() {

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        if (mInfo != null) {
            mWeatherTv.setText(mInfo.toString());
        }
    }

};

// Creation and deletion of content viewer is recommended
// Create is onCreate() delete is onDestroy() 
private void registerContentObserver() {
    mResolver.registerContentObserver(WEATHER_URI, true, mObserver);
}

private void unregisterContentObserver() {
    mResolver.unregisterContentObserver(mObserver);
}
```

[weather-example]: https://github.com/ticwear/sdk/tree/master/sample/eclipse/StepAndWeatherDemo 
