
## 哪些权限需要请求
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

注意：通过移动网络上网时，需要请求internet权限，其它网络不需要。

## 处理权限

### １. 检查权限

```
PermissionCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
```

### 2. 申请权限

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

### 3. 处理请求结果

Activity或者Fragment中实现接口PermissionCompat.OnRequestPermissionsResultCallback
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
**注意：使用support.v4中的framgment暂时无法使用这个接口**

## 移动网络的特殊说明:

如果没有获取移动网络权限，通过connectivityManager.getActiveNetworkInfo()获取的NetworkInfo是DISCONNECTED状态，需要进一步判断是否是被block了，参考代码如下:

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

#### 参考资料
https://developer.android.com/training/permissions/requesting.html
