## 数据传输API

我们参考Google的GMS (Google Mobile Service)，开发了一套适用于Ticwear的移动设备服务，MMS (Mobvoi Mobile Service)，其主要部件是手机、手表间的数据通讯。

开发者可以使用MobvoiAPI中的数据传输API来连接MMS，进行手机、手表间的通讯。

细节请参考我们的开源项目 [Ticwear SDK][ticwear-sdk] 中的[基于Eclipse的数据传输样例程序][mms-demo-eclipse]，以及[基于Android Studio的样例程序][mms-demo-as]。

### 开始使用

为了调用API，需要先创建一个`MobvoiApiClient`的实例，作为API的调用入口。

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
        .addOnConnectionFailedListener(new OnCoionFailedListener() {
            @Override
            public void onConnectionFailed(ConnectionResult result) {
                Log.d(TAG, "onConnectionFailed: " + result);
            }
        })
        // Request access only to the Wearable API
        .addApi(Wearable.API)
        .build();
```

需要先调用`connect()`，等到`onConnected()`回调以后client才能正常使用。

注意： onConnected并不代表手表和手机之间已经建立起数据连接，只表示已经连接上MMS服务，可以开始进行通讯API调用。

### 同步数据

一个`DataItem`定义了一个数据接口，该数据在手机端和手表端之间进行同步。一个`DataItem`通常包含以下内容：

* Payload: 一个字节数组。可以在里面放置任意数据，并采用自己的序列化和反序列化方法。payload的大小上限为100KB。

* Path: 一个唯一的字符串。必须以"/"开头，例如：`/path/data`。

通常并不需要自己实现`DataItem`，使用`DataItem`的步骤如下：

1. 创建一个`PutDataRequest`对象，定义一个唯一的路径作为标识。

2. 调用`setData()`来设置payload

3. 调用`DataApi.putDataItem()`来请求创建一个data item。

4. 当调用`DataApi.getDataItem()`请求一个data item时，会得到一个已经实现了对应接口的`DataItem`。

相比于直接使用`setData()`放置字节数组的方式，更建议使用data map，可以用类似`Bundle`的方式来操作data item，更加方便。使用`DataMap`的方法如下：

1. 创建一个`PutDataMapRequest`对象，设置data item的路径。该路径是一个唯一标识符，可以在手机端和手表端均使用该路径操作data item。

2. 调用`PutDataMapRequest.getDataMap()`获得一个data map，并可以往里面设值。

3. 使用`put...()`函数对data map进行设值。

4. 调用`PutDataMapRequest.asPutDataRequest()`生成一个`PutDataRequest`对象。

5. 调用`DataApi.putDataItem()`来创建一个data item。

监听data item事件：

如果连接的一方有数据的更改，那么这个更改会被通知到另一方。可以实现一个监听器来监听这些更改。

### 传输asset

`Asset`相关的函数是一系列通过蓝牙连接发送大的二进制文件的方法。可以在data item中附带asset来发送图片之类的二进制文件。asset的机制自动处理了数据的缓存，以避免重新传输浪费蓝牙带宽。一个通用的用法是手机端应用下载一个图像，压缩到一个适合在手表上显示的尺寸，并把图像通过asset传输到手表端的app。

Data item的大小限制在100KB以下，asset的大小基本不受限制。可是，传输非常大的asset会影响用户体验，所以需要测试以确保不影响用户体验。

创建asset代码：

``` Java
private static Asset createAssetFromBitmap(Bitmap bitmap) {
    final ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteStream);
    return Asset.createFromBytes(byteStream.toByteArray());
}
```

获得一个asset以后，可以使用`DataMap`或者`PutDataRequest`中的`putAsset()`方法将asset附加到一个data item上。

### 发送和接收消息

可以通过`MessageApi`发送消息，一条消息包含以下内容：

* Payload: 一个任意的字节数组。

* Path: 一个唯一标识这条消息操作的字符串。

和`DataItem`不一样，消息在手机端和手表端没有同步。消息只是单向通信机制，比较适合远程调用。下面举例如何发送一条消息到手表端启动一个activity。

发送消息代码：

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

接收消息代码：

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

你可以继承并实现`WearableListenerService`的各个接口，来保证程序运行在后台时仍然能够接收message、data和node的事件。

继承的Service需要在`AndroidManifest.xml`里面进行声明，需要注意的是，Service不能声明exported为false。例如为`DataLayerService`，则添加声明如下：

``` xml
<service android:name=".DataLayerListenerService">
       <intent-filter>
             <action oid:name="com.mobvoi.android.wearable.BIND_LISTENER" />
       </intent-filter>
</service>
```

### <a id="debug-wearable-api"></a>如何调试通讯问题

首先需要明确通讯的原理。手机、手表是通过MMS来进行通讯的（[自适应兼容模式][compat-mode]下，MMS会转发消息到GMS来实现通讯）

在发送消息前，需要先连接MMS，然后将消息传输给MMS，MMS传输消息到连接的节点后，通过 `WearableListenerService` 通知应用程序消息已收到。

调试通讯问题，就需要在每个可能出问题的地方加上检查代码（一般就是打log），定位错误的原因。

应用内部可以通过以下几处来检查问题：

1. 调用`MobvoiApiManager.getInstance().isMmsAvailable(context)`来确保MMS服务可以正常使用。

  > 如果不可正常使用，需要检查你的手机是否安装了最新版Ticwear助手。

2. 查看`ConnectionCallbacks`的返回结果是否正常，表明是否与MMS成功建立了连接。
2. 通过`NodeApi.getConnectedNodes()`来判断是否有连接的节点（即手机手表是否通过蓝牙建立了MMS的连接）。
3. 通过`MessageApi.sendMessage()`、`DataApi.putDataItem()` 等函数的回调结果，判断消息是否已经成功发送给了MMS。

除此之外，可以通过下面的命令来打开手机、手表的MMS底层的日志，来获取到更多的调试信息。

``` shell
adb shell setprop log.tag.MobvoiMobileService VERBOSE
```


[ticwear-sdk]: https://github.com/ticwear/sdk
[mms-demo-eclipse]: https://github.com/ticwear/sdk/tree/master/sample/eclipse/DataLayer
[mms-demo-as]: https://github.com/ticwear/sdk/tree/master/sample/android-studio/DataLayer
[compat-mode]: http://developer.ticwear.com/doc/gms-compat

