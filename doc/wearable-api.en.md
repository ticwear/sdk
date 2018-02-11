## Data Transmission API

We develop a data transmission service called MMS (Mobvoi Mobile Service) which is similar to GMS (Google Mobile Service) for the data transfered between the phone and watch.

For details, refer to [Ticwear SDK][ticwear-sdk] open source project in Github inside: [Eclipse-based data transfer sample program][mms-demo-eclipse], and [Android Studio based sample program][mms-demo-as].

### Start using

In order to call the API, we need to create a `MobvoiApiClient`

``` Java
MobvoiApiClient mClient = new MobvoiApiClient.Builder(this)
        .addConnectionCallbacks(new ConnectionCallbacks() {
            @Override
            public void onConnected(Bundle connectionHint) {
                Log.d(TAG, "onConnected: " + connectionHint);
                // Now you can use the Data Layer API
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
        // Request access only to the Wearable API
        .addApi(Wearable.API)
        .build();
```

You need to invoke `connect()`, and wait until `onConnected()` for the callback client to work properly.

Note: onConnected does not mean that connection has been established between the watch and the phone, only that Wearable API service has been connected and inter-process calls can begin.

### Synchronize Data

A `DataItem` defines a data interface, the data between the mobile terminal and the terminal to synchronize watches. A `DataItem` usually contains the following:

* Payload: an array of bytes. Any data can be placed in it, and adopt its own serialization and de-serialization method. Payload size limit is 100KB.
* Path: a unique string. Must have "/" at the beginning, for example: `/path/data`.

Usually it’s not necessary to create a `DataItem` on your own, to use `DataItem` the steps are as follows:

1. Create a `PutDataRequest` object that defines a unique path as identification.
2. call `setData()` to set the payload
3. call `DataApi.putDataItem()` to request the creation of a data item.
4. When you call `DataApi.getDataItem()` to request a data item, you will get a `DataItem` has implemented the corresponding interface.

Compared to the direct use of `setData()` for mode byte array placement, `DataMap` is recommended. With data map, you can use a similar approach to the operation Bundle data item, but more convenient. Use `DataMap` as follows:

1. Create a `PutDataMapRequest` object, and set the path of the data item. The path is a unique identifier, you can use the operating data item in the path and watch mobile terminal end.
2. Call `PutDataMapRequest.getDataMap()` to obtain a data map, here you can set values.
3. Use the `put...()` function to perform data map set value.
4. call `PutDataMapRequest.asPutDataRequest()` to generate a `PutDataRequest` object.
5. call `DataApi.putDataItem()` to create a data item.

Event monitor data item:

If either sides data changes, then the changes will be notified to the other party. You can implement a listener to monitor these changes.

### Asset transfer
Asset-related functions use a number of ways to send large binary files via Bluetooth. `Asset` can be attached to the data item in binaries like sending pictures. Automatic processing of the asset data cache avoids wasting retransmission and saves Bluetooth bandwidth. A common usage is to have a mobile application to download images, compress to a size suitable for display on the watch, and then transfer images to watch the end of the app through the asset transfer.

Data item size is limited to 100KB or less, asset size is basically unrestricted. However, a very large asset transfer will affect the user experience, so you need to test to make sure that it does not affect the user experience negatively.

How to create asset Code:
``` Java
private static Asset createAssetFromBitmap(Bitmap bitmap) {
    final ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteStream);
    return Asset.createFromBytes(byteStream.toByteArray());
}
```
After getting an asset, you can use `DataMap.PutDataRequest` in `putAsset()` method to attach the asset to a data item.

### Send and receive messages

Send a message which composed of two fields via `MessageApi`.

* Payload: an arbitrary array of bytes.
* Path: a string that uniquely identifies this message operation.

Not the same as a `DataItem`, the message in the phone side and watch are not synchronized. Message is a one-way communication mechanism, more suitable for long-distance calls. Here's an example of how to send a message to start a watch end activity.

Send Message Code:

``` Java
MobvoiApiClient mClient;
public static final String START_ACTIVITY_PATH = "/start/MainActivity";
...

private void sendStartActivityMessage(String nodeId) {
    Wearable.MessageApi.sendMessage(
        mClient, nodeId, START_ACTIVITY_PATH, new byte[0]).setResultCallback(
            new ResultCallback<SendMessageResult>() {
                @Override
                public void onResult(SendMessageResult sendMessageResult) {
                    if (!sendMessageResult.getStatus().isSuccess()) {
                        Log.e(TAG, "Failed to send message with status code: "
                            + sendMessageResult.getStatus().getStatusCode());
                    }
                }
            }
    );
}
```

Receive message code:

``` Java
@Override
public void onMessageReceived(MessageEvent messageEvent) {
     if (messageEvent.getPath().equals(START_ACTIVITY_PATH)) {
          Intent startIntent = new Intent(this, MainActivity.class);
          startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
          startActivity(startIntent);
     }
}
```

### WearableListenerService

Inherits and implements the various services of WearableListenerService, and ensures that the programs running in the background can still receive message, data and node events.

Inherited Service needs to be declared in AndroidManifest.xml. Note, Service exported can not be declared as false. For example, to use DataLayerService, add the following statement:

``` xml
<service android:name=".DataLayerListenerService">
       <intent-filter>
             <action oid:name="com.mobvoi.android.wearable.BIND_LISTENER" />
       </intent-filter>
</service>
```

### <a id="debug-wearable-api"></a>How to debug the communication issue

Need to be very clear about the principle of communication between the phone and the watch. The phone and the watch are communicated via MMS (In [Adaptive compatible mode][compat-mode], MMS will forward the message to GMS for communication.)

Before sending message, MMS must be connected firstly. Then the message will be sent to MMS, and after MMS pass the message to the other node, the app will receive the notification of message via `WearableListenerService`.

It is important to add check code (aka, add some logs) in every critical path to debug the communication problem.

Some check points as below:

1. Use `MobvoiApiManager.getInstance().isMmsAvailable(context)` to check whether MMS service is available.

  > If not available, please check whether the latest Ticwear Companion has been installed on the phone.

2. Check the result of `ConnectionCallbacks` which indicates whether the connection to the MMS has been established.

3. Use `NodeApi.getConnectedNodes()` to check whether some nodes has been connected. (If the connection between the phone and the watch has been established by MMS.)

4. Use the callback of `MessageApi.sendMessage()`, `DataApi.putDataItem()` and so on to check whether the message has been sent to MMS successfully.

Besides, more details for debugging can be found if you run the following commands to turn on the MMS debugging log.

``` shell
adb shell setprop log.tag.MobvoiMobileService VERBOSE
```

[ticwear-sdk]: https://github.com/ticwear/sdk
[mms-demo-eclipse]: https://github.com/ticwear/sdk/tree/master/sample/eclipse/DataLayer
[mms-demo-as]: https://github.com/ticwear/sdk/tree/master/sample/android-studio/DataLayer
[compat-mode]: https://developer.chumenwenwen.com/en/doc/ticwear.html#doc/4/16
