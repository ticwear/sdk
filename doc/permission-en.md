
## Which permissions should be requested.
- Manifest.permission.ACCESS_FINE_LOCATION
- Manifest.permission.ACCESS_COARSE_LOCATION
- Manifest.permission.RECORD_AUDIO
- Manifest.permission.CAMERA
- Manifest.permission.CALL_PHONE
- Manifest.permission.MODIFY_PHONE_STATE
- Manifest.permission.CHANGE_WIFI_STATE
- Manifest.permission.READ_CONTACTS
- Manifest.permission.WRITE_CONTACTS
- Manifest.permission.READ_CALL_LOG
- Manifest.permission.WRITE_CALL_LOG
- Manifest.permission.SEND_SMS
- Manifest.permission.READ_SMS
- Manifest.permission.WRITE_SMS
- Manifest.permission.BLUETOOTH_ADMIN
- Manifest.permission.BODY_SENSORS
- Manifest.permission.INTERNET

** Notice: When requesting data by mobile network, you should request Internet permission. And other networks is always granted.**

## Request permissions

### １. Check For Permissions

```
PermissionCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
```

### 2. Request Permissions

```
    if (PermissionCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
        if (PermissionCompat.shouldShowRequestPermissionRationale(
                this, Manifest.permission.ACCESS_FINE_LOCATION)) {

            // Show an expanation to the user *asynchronously* -- don't block
            // this thread waiting for the user's response! After the user
            // sees the explanation, try again to request the permission.

        } else {
            PermissionCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}
                　　, MY_PERMISSIONS_REQUEST);
        }
    }
```

### 3. Handle the permissions request response

Activity or Fragment should implement PermissionCompat.OnRequestPermissionsResultCallback
```
@Override
public void onRequestPermissionsResult(int requestCode,
        String permissions[], int[] grantResults) {
    switch (requestCode) {
        case MY_PERMISSIONS_REQUEST: {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                // permission was granted, yay! Do the
                // gps-related task you need to do.

            } else {

                // permission denied, boo! Disable the
                // functionality that depends on this permission.
            }
            return;
        }

        // other 'case' lines to check for other
        // permissions this app might request
    }
}
```

## Special things about mobile network:

If the mobile data permission isn't granted, the active networkInfo return by getActiveNetworkInfo() is in DISCONNECTED state. You should check the detailed state to see whether it's blocked. The sample code is shown as following:

```
ConnectivityManager connectivityManager =
        (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

    NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
    if (networkInfo != null) {
        boolean isConnected = networkInfo.isConnected();
        boolean isBlocked = (networkInfo.getDetailedState()
                == NetworkInfo.DetailedState.BLOCKED);
            Log.d("TAG", "Active NetworkInfo= " + networkInfo +
                         isConnected + " " + isBlocked);
    }
```

#### References
https://developer.android.com/training/permissions/requesting.html
