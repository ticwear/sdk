## 开发兼容Ticwear和Android Wear的应用


由于Ticwear在开发时，最大限度保证了与Android Wear（简称AW）API的兼容，所以开发者可以开发出同时兼容Android Wear和Ticwear的应用。

<!-- break -->

### 打包、兼容、以及各类概念的解释

**打包**， 手表App不能独立发布，因此手表App都一定对应有一个手机App，发布时，必须将手表App包装到对应的手机App中，才可以发布。AW国际版和中国版，打包的配置是一样的，Ticwear平台，需要做不同的配置，详见[如何打包](#packaging)。

**GMS & MMS**， 手机、手表的通讯API。GMS (Google Mobile Service) 是Google原生的通讯API，MMS (Mobvoi Mobile Service) 是出门问问为Ticwear研发的通讯API。MMS API除名称不一样外，其他的使用方式都是follow Google的GMS，以减少开发者的学习成本。

**兼容模式**（通讯兼容）， Ticwear的SDK，提供了一种叫做兼容模式的东西。这个兼容，针对的是手机、手表的通讯，与其他的概念，如打包、语音语义，没有关系。兼容模式，使得一个应用，不需要修改代码，就能自动适配Ticwear（使用MMS通讯）和AW（使用GMS通讯）平台上的通讯API。

**AW中国版、国际版**， AW国际版依赖Google Play Service的wearable部分 （GMS），在中国大陆无法使用。因此Google推出了专门针对中国大陆的Android Wear系统。使用了独立的GMS。中国开发者只需要替换掉对 Google Play Service 的依赖就好，不需要修改代码。

### 根据你的应用类型判断要做什么

不同类型的应用，使用到的API不一样，在兼容时所需要做的工作也不一样。这里大概列举几种典型的应用，帮助你分析需要做哪些部分的兼容。

#### 未使用特殊API的应用

这类应用，并未使用任何特殊API（如通讯、语音语义、天气计步等），手表上的应用可独立运行在Ticwear和AndroidWear系统上。比较典型的应用是表盘应用和手表上的游戏等小应用。这类应用的开发者，只需要关心打包的兼容就可以了。请参看[如何打包](#packaging)。

#### 使用了MMS或GMS与手机通讯的应用

这类应用，使用了MMS或GMS进行通讯。手表App需依赖手机App，或需要做些同步之类的工作。这是绝大部分应用属于的类型。这类开发者，除打包外，还需要关心[如何通讯](#communication)。如果你需要兼容的是AW中国版，而不是国际版，你需要关心[AW中国版的通讯兼容](#aw-china)

#### 使用了语音语义等特殊API的应用

这类应用，使用了Ticwear或AW上的一些特殊API。如各自的语音、语义接口，或者Ticwear的天气、健康、挠挠等API。一部分的表盘应用，和一些工具、社交类应用会用到这些API。这类开发，除打包外，还需要关心[如何使用语音语义等特殊API](#special-api)

### <a id="packaging"></a>如何打包

不同手表平台的打包方式，其区别主要在于manifest文件中的手表App标记。有了这个标记，各家手表平台就能识别出来，进行安装。

对于Ticwear的打包，参考[快速入门][ticwear-dev]的应用打包流程。

对于Android Wear的打包，如果使用的是 [Android Studio][as]（强烈建议你使用AS，Android官方已经不维护Eclipse插件了），你的手表应用应该已经使用 `wearApp('yourWearApp')` 依赖起来了。那么，你不需要做任何事情，只需要使用release 模式打包，就能输出符合AW标准的应用。详情请参考[Packaging Wearable Apps][aw-pkg]。

但如果你仍然希望使用Eclipse：

1. 你需要在配置好Ticwear打包之后，在manifest里面添加下面的meta-data：

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

2. 如果需要为中国版AW打包，还需要添加下面的meta-data：

    ``` xml
    <meta-data android:name="com.google.android.wearable.local_edition_compatible"
                android:value="true"/>
    ```


### <a id="communication"></a>如何通讯

在 Ticwear 上进行通讯，需要使用 Mobvoi SDK，利用 MMS 通讯。Mobvoi SDK 实现了 Google Play Services (GMS) 中手表和手机之间数据传输的接口，包含Node API、Message API和Data API的全部功能。Mobvoi SDK 的接口名称、方法名以及语意与 Android Wear 的实现完全一致。

在AW国际版上通讯，需要使用GMS，在AW中国版上通讯，需要使用[特定的 GMS](#aw-china)。

Mobvoi API (MMS)、 Google Play Service (GMS) 和 Google Play Service Standalone 之间的关系见下图：

![MMS & GMS Relationship](/assets/img/gms-mms-relationship.png "MMS & GMS Relationship")

不同的SDK，对各自的通讯协议进行了不同的封装。Mobvoi API则可以兼容这几种不同协议。

使用我们提供的Mobvoi API，有三种兼容方案可供选择。使你可以快速将Ticwear应用兼容AW，或者将应用从AW迁移到Ticwear上。推荐使用以下第一种方法，因为用这种方法打包的Apk可以同时兼容Ticwear和Android Wear平台，而不用为每个平台单独打包。

#### <a id="adapt-compat"></a>自适应兼容模式

1. 引入[mobvoi-api.jar][mobvoi-jar]，同时添加或保留[google-play-services][gms-jar]（AW中国版需使用[特定的 GMS](#aw-china)）。

    * 注1：建议使用 Android Studio 环境。如果是Eclipse用户，需手动添加 `Google Play Services` 的 meta-data 和 jar包，详见：[Setting Up Google Play Services][gms-jar]。
    * 注2：我们提供了兼容模式的 Sample code ([Eclipse][demo-compact-eclipse]/[Android Studio][demo-compact-as]) 供参考
    * 注3：目前我们仅测试支持了 `7.3 ~ 7.8` 版本，其他版本可能会出现兼容性问题。

2. 使用 Mobvoi API。详情参考[快速入门][ticwear-dev]。如果你已经有AW的代码，可以通过下面的步骤来切换：
    1. 将代码中的Google Mobile Services (GMS) API替换为仅包名不同的Mobvoi Mobile Services (MMS) API
    2. 将 `GoogleApiClient` 替换为 `MobvoiApiClient`。
    3. 如果使用了WearableListenerService，在 `AndroidManifest.xml` 里面把 `com.google.android.gms.wearable.BIND_LISTENER` 替换为 `com.mobvoi.android.wearable.BIND_LISTENER`。
3. 在手机、手表App启动时，都需要调用 `MobvoiApiManager.getInstance().adaptService(context)`， 该方法必须在任何可能的API调用操作前调用（建议在Application.onCreate中调用），它将会自动探测当前系统情况，选择底层是使用MMS或GMS。如果想自己决定使用哪种API，可以通过调用 `MobvoiApiManager.getInstance().loadService(context, group)` 来指定使用Ticwear或Android Wear的API，以取代上面的 `adaptService` 方法。如果这两个方法都没有被调用，API会变成仅Ticwear系统能使用的方式。调用形式如下：

    ``` Java
    if (!MobvoiApiManager.getInstance().isInitialized()) {
        try {
            MobvoiApiManager.getInstance().adaptService(this);
        } catch (NoAvailableServiceException e) {
            Log.e(TAG, "no avaliable service.", e);
            return;
        }
    }
    ```
4. 如果使用了WearableListenerService，在AndroidManifest.xml中注册GMS Wearable Listener Service的代理服务：

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

可以参看Github开源项目中兼容模式 ([Eclipse][demo-compact-eclipse]/[Android Studio][demo-compact-as])的使用样例。

#### 仅Ticwear运行方式

这种方式的优点是当Apk不需要支持Android Wear运行环境时，可以无需引入Google Play Services包。

1. 将项目引用的`google-play-services`从项目中移除，引入[`mobvoi-api.jar`][mobvoi-jar]。
2. 使用 Mobvoi API。如果你已经有AW的代码，可以通过下面的步骤来切换：
    1. 将代码中的Google Mobile Services (GMS) API替换为仅包名不同的Mobvoi Mobile Services (MMS) API
    2. 将 `GoogleApiClient` 替换为 `MobvoiApiClient`。
    3. 如果使用了WearableListenerService，在 `AndroidManifest.xml` 里面把 `com.google.android.gms.wearable.BIND_LISTENER` 替换为 `com.mobvoi.android.wearable.BIND_LISTENER`。
3. 重新编译打包。

#### 直接替换API lib方式（仅限已有AW应用）

这种方式的优点是无需修改代码，直接替换 API library 即可，可以实现快速迁移。但是缺点是无法得到Ticwear专有API支持，而且无法使同一个Apk运行在Ticwear和Android Wear两个平台上。所以一般不推荐使用这种方案，但是可以很方便用于早期测试。

使用这种方法需要下载专用的替换SDK(mobvoi-api-gms-replaceable.jar)在项目中引用，而不是引用mobvoi-api.jar。[点此下载][mobvoi-replace]专用SDK。

1. 将项目引用的[`google-play-services`][gms-jar]从项目中移除，引入[`mobvoi-api-gms-replaceable.jar`][mobvoi-replace]。
2. 重新编译打包。


### <a id="aw-china"></a>AW中国版的通讯兼容

国际版的 Android Wear 应用无法直接在中国版 Android Wear 手表操作系统上通讯。包含通讯功能的AW应用需要更换手机、手表端依赖的GMS库才能在中国版 Android Wear 系统上通讯，但不需要改任何代码（包括混淆配置等都保持与[`GMS`][gms-jar]一致）。详情可参看[英文官方文档][awc-doc]，或者参考我们的 [CompatModeChinaDemo][demo-compact-china]。

打包中国版Android Wear应用需要进行以下四个步骤：

1. 下载[中国版 Android Wear SDK][awc-sdk]到本地，文件名为 `google-play-services-7-8-87.zip`。
2. 创建本地Maven仓库。将下载的文件解压到工程根目录下。
3. 在项目根目录的 `build.gradle` 中添加这个Maven仓库的依赖。

    ``` gradle
    allprojects {
      repositories {
        // ... other repositories may go here
        maven {
          url "${rootProject.projectDir}/google-play-services-7-8-87"
        }
      }
    }
    ```

4. 更新手机、手表两端的APK使用的 gms client 库。在 APK module的build.gradle文件中将依赖

    ``` gradle
    compile 'com.google.android.gms:play-services-wearable:{$gmsVersion}'
    ```

    替换为新添加的库

    ``` gradle
    compile 'com.google.android.gms:play-services-wearable:7.8.87'
    ```
    
    注意，版本号只能是 `7.8.87`。

5. 使用 Android Studio 打包应用然后在装有中国版 Android Wear 助手的手机上部署即可。

这样配置以后，你的应用将同时支持 Android Wear 国际版手表和 Android Wear 中国版手表。

#### 必须使用Eclipse？

首先建议你使用 Android Studio，能自动完成大量的配置工作。但如果由于某些原因，必须使用 Eclipse，你可以参考我们的[CompatModeChinaDemo][awc-demo-eclipse]。

请注意libs内的play-services版本，以及`AndroidManifest.xml`文件中的`meta-data`。

### <a id="special-api"></a>如何使用语音语义等特殊API

语音语义等API，都包含在了[mobvoi-api.jar][mobvoi-jar]中，只要依赖了这个jar包，即可使用[Ticwear专有API][ticwear-dev]。

使用了这些API的应用。在Android Wear 平台无法使用相关的功能。使用不当也可能造成应用崩溃。所以这类应用在迁移到 Android Wear时，需要做更多测试和检查。某些API，在Android Wear 上也有相应的实现，可以在应用内做检查来兼容。



## <a id="qa"></a>遇到问题？

开发兼容模式时遇到的一些常见问题，我们会在这里列举出来。

### 如何判断当前是使用GMS还是MMS，是否支持这些协议？

利用如下API，可以获取当前正在使用哪个协议通讯。

```
MobvoiApiManager.getInstance().getGroup();
```

其返回值可能是如下类型：

```
public enum ApiGroup {
    MMS, GMS, NONE
}
```

其中，NONE，表示没有初始化，或者初始化出错。请按照[自适应兼容模式](#adapt-compat)流程来使用。

通过以下API，可以检测当前环境支持哪些通讯协议。

```
MobvoiApiManager.getInstance().isGmsAvailable(context);
MobvoiApiManager.getInstance().isMmsAvailable(context);
```


### 手表或手机程序crash

- 如果出现 `java.lang.IncompatibleClassChangeError: The method 'void com.google.android.gms.common.api.GoogleApiClient.connect()' was expected to be of type interface but instead was found to be of type virtual`。可能是以下原因：

  1. adaptService调用顺序错误，建议放到Application的onCreate中调用。
  2. GMS版本太高，8.1.0以上版本修改了实现方式，MobvoiAPI不支持。建议使用经过测试的 7.3 ~ 7.8 版本。

- 如果出现找不到 `isNearby` 方法。可能是 GMS版本太低。建议使用经过测试的 7.3 ~ 7.8 版本。

### 手表或手机接收不到消息

- 手机是否同时安装了 Ticwear 助手和 Android Wear 助手？

  MobvoiAPI 需要在应用启动时选择一个通讯线路，如果同时存在两个线路（安装了两个助手），将优先选择 Ticwear，所以测试 Android Wear 的通讯时，需要先卸载 Ticwear助手，或者临时选择 `MobvoiApiManager.loadService()` 来指定 GMS 通讯。

- 检查 `WearableListenerServiceGoogleImpl` 的配置是否正确。详见 [自适应兼容模式](#adapt-compat)。

### connect 失败

如果看到类似这样的日志：

```
GooglePlayServicesUtil: Google Play services out of date.  Requires 7895000 but found 7887534
```

请确认你的 GMS 版本是否太低。或者，是否没有同时修改手机、手表两侧的 GMS 依赖（必须修改 GMS 版本为`7.8.87`）。

### 如何调试 GMS 通讯

类似 [MMS 的调试][wearable-debug]，我们也可以打开 GMS 的调试日志来帮助我们定位问题。

在需要调试的设备上（手机或手表），进入 `adb shell`，然后执行下面的命令即可打开 GMS 的调试日志：

``` shell
setprop log.tag.WearableService VERBOSE
setprop log.tag.WearableConn VERBOSE
```


[aw]: https://www.android.com/wear/
[ticwear]: http://ticwear.com/
[ticwear-dev]: http://developer.ticwear.com/doc/getting-started
[wenwen]: http://chumenwenwen.com/
[as]: http://developer.android.com/sdk/index.html
[aw-pkg]: http://developer.android.com/training/wearables/apps/packaging.html
[mobvoi-jar]: https://github.com/ticwear/sdk/raw/master/lib/mobvoi-api.jar
[mobvoi-replace]: https://github.com/ticwear/sdk/raw/master/lib/mobvoi-api-gms-replaceable.jar
[gms-jar]: https://developers.google.com/android/guides/setup
[awc-sdk]: https://github.com/ticwear/sdk/raw/master/android-wear-lib/google-play-services-7-8-87.zip
[awc-doc]: http://developer.android.com/intl/es/training/wearables/apps/creating-app-china.html
[demo-compact-eclipse]: https://github.com/ticwear/sdk/tree/master/sample/eclipse/CompatModeDemo
[demo-compact-as]: https://github.com/ticwear/sdk/tree/master/sample/android-studio/CompatModeDemo
[demo-compact-china]: https://github.com/ticwear/sdk/tree/master/sample/android-studio/CompatModeChinaDemo
[awc-demo-eclipse]: https://github.com/ticwear/sdk/tree/master/sample/eclipse/CompatModeChinaDemo
[wearable-debug]: /doc/wearable-api.md#debug-wearable-api

