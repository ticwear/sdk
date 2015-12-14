由于Ticwear在开发时，最大限度保证了与Android Wear（简称AW）API的兼容，所以开发者可以开发出同时兼容Android Wear和Ticwear的应用。

<!-- break -->

## 打包、兼容、以及各类概念的解释

**打包**， 手表App不能独立发布，因此手表App都一定对应有一个手机App，发布时，必须将手表App包装到对应的手机App中，才可以发布。
AW国际版和中国版，打包的配置是一样的，Ticwear平台，需要做不同的配置，详见[如何打包](#package)。

**GMS & MMS**， 手机、手表的通讯API。
GMS (Google Mobile Service) 是Google原生的通讯API，MMS (Mobvoi Mobile Service) 是出门问问为Ticwear研发的通讯API。
MMS API除名称不一样外，其他的使用方式都是follow Google的GMS，以减少开发者的学习成本。

**兼容模式**（通讯兼容）， Ticwear的SDK，提供了一种叫做兼容模式的东西。
这个兼容，针对的是手机、手表的通讯，与其他的概念，如打包、语音语义，没有关系。
兼容模式，使得一个应用，不需要修改代码，就能自动适配Ticwear（使用MMS通讯）和AW（使用GMS通讯）平台上的通讯API。

**AW中国版、国际版**， AW国际版依赖Google Play Service的wearable部分 （GMS），在中国大陆无法使用。
因此Google推出了专门针对中国大陆的Android Wear系统。使用了独立的GMS。
中国开发者只需要替换掉对 Google Play Service 的依赖就好，不需要修改代码。

## 根据你的应用类型判断要做什么

不同类型的应用，使用到的API不一样，在兼容时所需要做的工作也不一样。
这里大概列举几种典型的应用，帮助你分析需要做哪些部分的兼容。

### 未使用特殊API的应用

这类应用，并未使用任何特殊API（如通讯、语音语义、天气计步等），手表上的应用可独立运行在Ticwear和AndroidWear系统上。
比较典型的应用是表盘应用和手表上的游戏等小应用。
这类应用的开发者，只需要关心打包的兼容就可以了。请参看[如何打包](#package)。

### 使用了MMS或GMS与手机通讯的应用

这类应用，使用了MMS或GMS进行通讯。手表App需依赖手机App，或需要做些同步之类的工作。
这是绝大部分应用属于的类型。
这类开发者，除打包外，还需要关心[如何通讯](#message-api)。

### 使用了语音语义等特殊API的应用

这类应用，使用了Ticwear或AW上的一些特殊API。如各自的语音、语义接口，或者Ticwear的天气、健康、挠挠等API。
一部分的表盘应用，和一些工具、社交类应用会用到这些API。
这类开发，除打包外，还需要关心[如何使用语音语义等特殊API](#specail-api)

## 如何打包<a name="package"></a>

不同手表平台的打包方式，其区别主要在于manifest文件中的手表App标记。有了这个标记，各家手表平台就能识别出来，进行安装。

对于Ticwear的打包，参考[快速入门](http://developer.ticwear.com/doc/getting-started)的应用打包流程来打包。
对于Android Wear的打包，如果使用的是[Android Studio][as]，你的手表应用应该已经使用 `wearApp('yourWearApp')` 依赖起来了。
那么，你不需要做任何事情，只需要使用release 模式打包，就能输出符合AW标准的应用。
详情请参考[Packaging Wearable Apps][aw-pkg]

但如果你仍然希望使用Eclipse，你需要在配置好Ticwear打包之后，在manifest里面添加下面的meta-data：

``` xml
<meta-data android:name="com.google.android.wearable.beta.app"
            android:resource="@xml/wearable_app_desc"/>
```

这与Ticwear的配置非常类似：

``` xml
<meta-data android:name="com.mobvoi.ticwear.app"
            android:resource="@xml/wearable_app_desc"/>
```

其中， `wearable_app_desc` 是你的手表App描述文件的位置。

如果你想同时兼容AW和Ticwear的打包，可以同时保留这两个 `meta-data`。


## 如何通讯<a name="message-api"></a>

在 Ticwear 上进行通讯，需要使用 Mobvoi SDK，利用 MMS 通讯。
Mobvoi SDK 实现了 Google Play Services (GMS) 中手表和手机之间数据传输的接口，包含Node API、Message API和Data API的全部功能。
Mobvoi SDK 的接口名称、方法名以及语意与 Android Wear 的实现完全一致。

在AW国际版上通讯，需要使用GMS，在AW中国版上通讯，需要使用 GMS standalone。

Mobvoi API (MMS)、 Google Play Service (GMS) 和 Google Play Service Standalone 之间的关系见下图：

![MMS & GMS Relationship](/art/gms-mms-relationship.png)

不同的SDK，对各自的通讯协议进行了不同的封装。Mobvoi API则可以兼容这几种不同协议。

使用我们提供的Mobvoi API，有三种兼容方案可供选择。
使你可以快速将Ticwear应用兼容AW，或者将应用从AW迁移到Ticwear上。
推荐使用以下第一种方法，因为用这种方法打包的Apk可以同时兼容Ticwear和Android Wear平台，而不用为每个平台单独打包。

### 自适应兼容模式

1. 引入[mobvoi-api.jar][mobvoi-jar]，同时保留[google-play-services.jar][gms-jar]。
2. 将代码中的Google Mobile Services (GMS) API替换为仅包名不同的Mobvoi Mobile Services (MMS) API，并将 `GoogleApiClient` 替换为 `MobvoiApiClient`。在 `AndroidManifest.xml` 里面把 `com.google.android.gms.wearable.BIND_LISTENER` 替换为 `com.mobvoi.android.wearable.BIND_LISTENER`。
3. 在App启动时调用 `MobvoiApiManager.getInstance().adaptService(context)`， 该方法必须在任何可能的API调用操作前调用，它将会自动探测当前系统情况，选择底层是使用MMS或GMS。如果想自己决定使用哪种API，可以通过调用 `MobvoiApiManager.getInstance().loadService(context, group)` 来指定使用Ticwear或Android Wear的API，以取代上面的 `adaptService` 方法。如果这两个方法都没有被调用，API会变成仅Ticwear系统能使用的方式。
4. 在AndroidManifest.xml中注册GMS Wearable Listener Service的代理服务：

``` xml
<service android:name=
      "com.mobvoi.android.wearable.WearableListenerServiceGoogleImpl">
         <intent-filter>
                    <action android:name=
                "com.google.android.gms.wearable.BIND_LISTENER" />
        </intent-filter>
</service>
```

5. 重新编译打包。
可以参看Github开源项目中[兼容模式][demo-compact]的使用样例。

### 仅Ticwear运行方式

这种方式的优点是当Apk不需要支持Android Wear运行环境时，可以无需引入Google Play Services包。
1. 将项目引用的[google-play-services.jar][gms-jar]从项目中移除，引入[mobvoi-api.jar][mobvoi-jar]。
2. 将代码中的Google Mobile Services (GMS) API替换为仅包名不同的Mobvoi Mobile Services (MMS) API，并将 `GoogleApiClient` 替换为 `MobvoiApiClient`。在 `AndroidManifest.xml` 里面把 `com.google.android.gms.wearable.BIND_LISTENER` 替换为 `com.mobvoi.android.wearable.BIND_LISTENER`。
3. 重新编译打包。

### 直接替换API lib方式（仅限已有AW应用）
这种方式的优点是无需修改代码，直接替换API library即可，可以实现快速迁移。
但是缺点是无法得到Ticwear专有API支持，而且无法使同一个Apk运行在Ticwear和Android Wear两个平台上。
所以一般不推荐使用这种方案，但是可以很方便用于早期测试。

使用这种方法需要下载专用的替换SDK(mobvoi-api-gms-replaceable.jar)在项目中引用，而不是引用mobvoi-api.jar。[点此下载][mobvoi-replace]专用SDK。
1. 将项目引用的google-play-services.jar从项目中移除，引入mobvoi-api-gms-replaceable.jar。
2. 重新编译打包。



## 如何使用语音语义等特殊API<a name="specail-api"></a>

语音语义等API，都包含在了[mobvoi-api.jar][mobvoi-jar]中，只要依赖了这个jar包，即可使用[Ticwear专有API][ticwear-dev]。

使用了这些API的应用。在Android Wear 平台无法使用相关的功能。使用不当也可能造成应用崩溃。
所以这类应用在迁移到 Android Wear时，需要做更多测试和检查。
某些API，在Android Wear 上也有相应的实现，可以在应用内做检查来兼容。




[aw]: https://www.android.com/wear/
[ticwear]: http://ticwear.com/
[ticwear-dev]: http://developer.ticwear.com/doc/getting-started
[wenwen]: http://chumenwenwen.com/
[as]: http://developer.android.com/sdk/index.html
[aw-pkg]: http://developer.android.com/training/wearables/apps/packaging.html
[mobvoi-jar]: https://github.com/ticwear/sdk/raw/master/lib/mobvoi-api.jar
[mobvoi-replace]: https://github.com/ticwear/sdk/raw/master/lib/mobvoi-api-gms-replaceable.jar
[gms-jar]: https://developers.google.com/android/guides/setup
[demo-compact]: https://github.com/ticwear/sdk/tree/master/sample/eclipse/CompatModeDemo
