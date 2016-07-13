## Sensor API

### Brief introduction
On Ticwear systems, you can use gravity sensor, gyroscope, accelerometer, heart rate sensor, pedometer, magnetometer, and other sensors.

All sensor types listed here:

- **ACCELEROMETER**: Sensor.TYPE_ACCELEROMETER 
- **MAGNETOMETER**: Sensor.TYPE_MAGNETIC_FIELD
- **GYROSCOPE**: Sensor.TYPE_GYROSCOPE
- **GRAVITY SENSOR**: Sensor.TYPE_GRAVITY
- **LINEAR ACCELEROMETER**: Sensor.TYPE_LINEAR_ACCELEROMETER
- **HEART RATE SENSOR**: Sensor.TYPE_HEART_RATE
- **PEDOMETER**: Sensor.TYPE_STEP_COUNTER

To use the acceleration, for example, the generally sensor code is as follows:

``` Java
public class SensorActivity extends Activity implements SensorEventListener {
    private final SensorManager mSensorManager;
    private final Sensor mAccelerometer;

    public SensorActivity() {
         mSensorManager =
                   (SensorManager)getSystemService(SENSOR_SERVICE);
         mAccelerometer = mSensorManager.getDefaultSensor(
                   Sensor.TYPE_ACCELEROMETER);
     }   

     protected void onResume() {
          super.onResume();
          mSensorManager.registerListener(this, mAccelerometer,
               SensorManager.SENSOR_DELAY_NORMAL);
     }   

     protected void onPause() {
         super.onPause();
         mSensorManager.unregisterListener(this);
     }   

     public void onAccuracyChanged(Sensor sensor, int accuracy) {
     }   

     public void onSensorChanged(SensorEvent event) {
     }   
}
```
    
On your watch the sensors with more features are the heart rate sensor and pedometer sensor, their features are highlighted below.

### Heart rate sensor

You can obtain the wearer's current heart rate through the heart rate sensor.

You first need to declare permissions:

``` xml
<uses-permission android:name="android.permission.BODY_SENSORS" />
```

In the procedure above, use `Sensor.TYPE_HEART_RATE`. After being called, the `onSensorChanged()` function will receive a `sensorEvent` event, `sensorEvent` value[0] will contain the number of beats per minute. The return value may be measured 0 at the beginning, only 1-2 minutes after measuring the heart rate can you get the current value. 

``` Java
public void onSensorChanged(SensorEvent sensorEvent) {
        Log.d("Test", "Got the heart rate (beats per minute) : " +
                 String.valueOf(sensorEvent.values[0]));
}
```

### Pedometer

Using the pedometer, you can obtain info about the wearer's steps.

In the procedure above, use `Sensor.TYPE_STEP_COUNTER`. After being called, `onSensorChanged()` function will receive a `sensorEvent` event, `sensorEvent` value [0] will contain the current number of steps. You need to walk about ten steps before the  pedometer can determine the current state, `onSensorChanged()` function will be called back.

``` Java
public void onSensorChanged(SensorEvent sensorEvent) {
       Log.d("Test", "Got the step count : " +
                String.valueOf(sensorEvent.values[0]));
}
```
