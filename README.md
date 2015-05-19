# Ticwear Developer SDK
为了方便第三方开发者迅速使用SDK来开发Ticwear应用，我们提供了以下联系方式来协助开发：  
**QQ群：114947882**  
**微博：Ticwear**  
**社区：http://ask.ticwear.com/**  

## 非Android Wear开发者
我们建议您采用如下步骤
* Step 1：运行sample文件夹里的示例程序来了解SDK提供的基本功能
* Step 2：阅读开发文档深入了解如何使用: http://developer.ticwear.com/doc/getting-started

## Android Wear开发者
您可以很快将自己的应用移植到Ticwear平台，请参考文档：http://developer.ticwear.com/doc/gms-compat

如果您想让您的应用同时兼容Android Wear和Ticwear，请采用如下步骤
* 引入mobvoi-api.jar，同时保留google-play-services.jar
* 将代码中的Google Mobile Services (GMS) API替换为仅包名不同的Mobvoi Mobile Services (MMS) API，GoogleApiClient替换为MobvoiApiClient。在AndroidManifest.xml里面把com.google.android.gms.wearable.BIND_LISTENER替换为com.mobvoi.android.wearable.BIND_LISTENER
* 在App启动时调用MobvoiApiManager.getInstance().adaptService(context)，该方法必须在任何可能的API调用操作前调用，它将会自动探测当前系统情况，选择底层是使用MMS或GMS。如果想自己决定使用哪种API，可以通过调用MobvoiApiManager.getInstance().loadService(context, group)来指定使用Ticwear或Android Wear的API，以取代上面的adaptService方法。如果这两个方法都没有被调用，API会变成仅Ticwear系统能使用的方式。
* 在AndroidManifest.xml中找到Google指定Wear子App位置的配置（可能是自动生成的），如：
  ```java
  <meta-data android:name="com.google.android.wearable.beta.app"
             android:resource="@xml/wearable_app_desc"/>
  ```

  在该行后加入一行配置用来为Ticwear指定子App位置，如：
  ```java
  <meta-data android:name="com.mobvoi.ticwear.app" android:resource="@xml/wearable_app_desc"/>
  ```
* 在AndroidManifest.xml中注册GMS Wearable Listener Service的代理服务：

  ```java
  <service android:name="com.mobvoi.android.wearable.WearableListenerServiceGoogleImpl">
    <intent-filter>
      <action android:name="com.google.android.gms.wearable.BIND_LISTENER" />
    </intent-filter>
  </service> 
  ```
* 重新编译打包
