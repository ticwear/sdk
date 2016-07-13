## Location API

### Brief introduction

On a smart watch without GPS and other positioning equipment, often you can not directly obtain the current geographical location of the device through the Android API. So, Ticwear provides a convenient location API for developers to easily access the current user's location for application development.

In order to call the API, we need to create a `MobvoiApiClient` instance, as the entrance to the API call.

``` Java
    MobvoiApiClient mClient = new MobvoiApiClient.Builder(this)
        .addConnectionCallbacks(new ConnectionCallbacks() {
            @Override
            public void onConnected(Bundle connectionHint) {
                Log.d(TAG, "onConnected: " + connectionHint);
                // Now you can use the API
            }
            @Override
            public void onConnectionSuspended(int cause) {
                Log.d(TAG, "onConnectionSuspended: " + cause);
            }
        })
        .addOnConnectionFailedListener(new OnConnectionFailedListener() {
            @Override
            public void onConnectionFailed(ConnectionResult result) {
                Log.d(TAG, "onConnectionFailed: " + result);
            }
        })
        .addApi(LocationServices.API)
        .build();
```
        
You need to call `connect()`, and wait until `onConnect()` callback for the client to work properly.

### Get current location
Usually after LocationServices API binding is successful, the onConnected function will receive the most current location. An example API call code is as follows:    

``` Java
public class MainActivity extends Activity implements
        ConnectionCallbacks, OnConnectionFailedListener {
    ...
    @Override
    public void onConnected(Bundle connectionHint) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mClient);
        if (mLastLocation != null) {
            mLatitudeText.setText(String.valueOf(mLastLocation.getLatitude()));
            mLongitudeText.setText(String.valueOf(mLastLocation.getLongitude()));
        }
    }
}
```

`getLastLocation()` method returns a Location object, where you can get latitude and longitude coordinates and original GPS coordinates. When location is unavailable, location object has a latitude and longitude value of 0.
