## 开发兼容Ticwear和Android Wear的应用


由于Ticwear在开发时，最大限度保证了与Android Wear（简称AW）API的兼容，所以开发者可以开发出同时兼容Android Wear和Ticwear的应用。

<!-- break -->

### 打包、兼容、以及各类概念的解释

**打包**， 手表App不能独立发布，因此手表App都一定对应有一个手机App，发布时，必须将手表App包装到对应的手机App中，才可以发布。AW国际版和中国版，打包的配置是一样的，Ticwear平台，需要做不同的配置，详见[如何打包](#如何打包)。

**GMS & MMS**， 手机、手表的通讯API。GMS (Google Mobile Service) 是Google原生的通讯API，MMS (Mobvoi Mobile Service) 是出门问问为Ticwear研发的通讯API。MMS API除名称不一样外，其他的使用方式都是follow Google的GMS，以减少开发者的学习成本。

**兼容模式**（通讯兼容）， Ticwear的SDK，提供了一种叫做兼容模式的东西。这个兼容，针对的是手机、手表的通讯，与其他的概念，如打包、语音语义，没有关系。兼容模式，使得一个应用，不需要修改代码，就能自动适配Ticwear（使用MMS通讯）和AW（使用GMS通讯）平台上的通讯API。

**AW中国版、国际版**， AW国际版依赖Google Play Service的wearable部分 （GMS），在中国大陆无法使用。因此Google推出了专门针对中国大陆的Android Wear系统。使用了独立的GMS。中国开发者只需要替换掉对 Google Play Service 的依赖就好，不需要修改代码。

### 根据你的应用类型判断要做什么

不同类型的应用，使用到的API不一样，在兼容时所需要做的工作也不一样。这里大概列举几种典型的应用，帮助你分析需要做哪些部分的兼容。

#### 未使用特殊API的应用

这类应用，并未使用任何特殊API（如通讯、语音语义、天气计步等），手表上的应用可独立运行在Ticwear和AndroidWear系统上。比较典型的应用是表盘应用和手表上的游戏等小应用。这类应用的开发者，只需要关心打包的兼容就可以了。请参看[如何打包](#如何打包)。

#### 使用了MMS或GMS与手机通讯的应用

这类应用，使用了MMS或GMS进行通讯。手表App需依赖手机App，或需要做些同步之类的工作。这是绝大部分应用属于的类型。这类开发者，除打包外，还需要关心[如何通讯](#如何通讯)。如果你需要兼容的是AW中国版，而不是国际版，你需要关心[AW中国版的通讯兼容](#aw中国版的通讯兼容)

#### 使用了语音语义等特殊API的应用

这类应用，使用了Ticwear或AW上的一些特殊API。如各自的语音、语义接口，或者Ticwear的天气、健康、挠挠等API。一部分的表盘应用，和一些工具、社交类应用会用到这些API。这类开发，除打包外，还需要关心[如何使用语音语义等特殊API](#如何使用语音语义等特殊api)

### 如何打包

不同手表平台的打包方式，其区别主要在于manifest文件中的手表App标记。有了这个标记，各家手表平台就能识别出来，进行安装。

对于Ticwear的打包，参考[快速入门][ticwear-dev]的应用打包流程来打包。 对于Android Wear的打包，如果使用的是[Android Studio][as]，你的手表应用应该已经使用 `wearApp('yourWearApp')` 依赖起来了。那么，你不需要做任何事情，只需要使用release 模式打包，就能输出符合AW标准的应用。详情请参考[Packaging Wearable Apps][aw-pkg]

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


### 如何通讯

在 Ticwear 上进行通讯，需要使用 Mobvoi SDK，利用 MMS 通讯。Mobvoi SDK 实现了 Google Play Services (GMS) 中手表和手机之间数据传输的接口，包含Node API、Message API和Data API的全部功能。Mobvoi SDK 的接口名称、方法名以及语意与 Android Wear 的实现完全一致。

在AW国际版上通讯，需要使用GMS，在AW中国版上通讯，需要使用 GMS standalone。

Mobvoi API (MMS)、 Google Play Service (GMS) 和 Google Play Service Standalone 之间的关系见下图：

![MMS & GMS Relationship](/assets/img/gms-mms-relationship.png "MMS & GMS Relationship")

不同的SDK，对各自的通讯协议进行了不同的封装。Mobvoi API则可以兼容这几种不同协议。

使用我们提供的Mobvoi API，有三种兼容方案可供选择。使你可以快速将Ticwear应用兼容AW，或者将应用从AW迁移到Ticwear上。推荐使用以下第一种方法，因为用这种方法打包的Apk可以同时兼容Ticwear和Android Wear平台，而不用为每个平台单独打包。

#### 自适应兼容模式

1. 引入[mobvoi-api.jar][mobvoi-jar]，同时添加或保留[google-play-services][gms-jar]。

    * 注1：建议使用 Android Studio 环境。如果是Eclipse用户，需手动添加 `Google Play Services` 的 meta-data 和 jar包，详见：[Setting Up Google Play Services][gms-jar]。
    * 注2：我们提供了兼容模式的 Sample code ([Eclipse][demo-compact-eclipse]/[Android Studio][demo-compact-as]) 供参考
    * 注3：目前我们仅测试支持了 `7.3.0` 和 `7.5.0` 版本，其他版本可能会出现兼容性问题。

2. 使用 Mobvoi API。如果你已经有AW的代码，可以通过下面的步骤来切换：
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

1. 将项目引用的[`google-play-services`][gms-jar]从项目中移除，引入[`mobvoi-api.jar`][mobvoi-jar]。
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


### AW中国版的通讯兼容

国际版的 Android Wear 应用无法直接在中国版 Android Wear 手表操作系统上通讯。包含通讯功能的AW应用需要更换手机端依赖的GMS库才能在中国版 Android Wear 系统上通讯，但不需要改任何代码（包括混淆配置等都保持与[`GMS`][gms-jar]一致）。手表端的应用仍需使用原来的GMS。

打包中国版Android Wear应用需要进行以下四个步骤：

1. 配置 Android Wear SDK 的仓库。由于 Android Wear 官方还未提供中国版的SDK自动下载，开发者可以暂时使用我们提供的[中国版 Android Wear SDK][awc-sdk]，手动配置代码仓库。
    1. 方法一，增加本地代码库。将下载的 `wearable-api-client-repository.zip` 文件解压到某个文件目录下，例如 `/home/wearable/`，在 Android Studio 的顶级project的 `build.gradle` 中添加这个m2repository目录。

        ``` gradle
        allprojects {
          repositories {
            // ... other repositories may go here
            maven {
              url '/home/wearable/m2repository'
            }
          }
        }
        ```

    2. 方法二，添加到 Android SDK 代码库。（这种方法不需要添加额外的maven库依赖，但可能在更新 Android SDK 时被删除，请注意备份）。将下载的 `wearable-api-client-repository.zip` 文件解压，再将解压出来的 `m2repository` 覆盖（合并）到 **Android SDK** 目录中，保证合并以后的目录结构为：

        ```
        ${Android-sdk}/extras/google/m2repository/com/google/android/wearable/play-services-wearable-standalone/
        ```

2. 更新手机端apk使用的client库。官方只提供 Android Studio （基于Gradle） 打包方式。在build.gradle文件中将依赖

    ``` gradle
    compile 'com.google.android.gms:play-services:7.5.0'
    ```

    替换为新添加的库

    ``` gradle
    compile 'com.google.android.wearable:play-services-wearable-standalone:7.5.0'
    ```

3. 手表端apk使用的client库不用变，仍然依赖

    ``` gradle
    'com.google.android.gms:play-services-wearable'
    ```

4. 使用 Android Studio 打包应用然后在装有中国版 Android Wear 助手的手机上部署即可。

若你的手表端和手机端有一个公用的库依赖 Wearable API 的话，需要进行一些额外的配置。

1. 保证公用模块依赖中国版的client库，在公用模块的build.gradle中改成如下代码：

    ``` gradle
    dependencies {
      // ..
      compile 'com.google.android.wearable:play-services-wearable-standalone:7.5.0'
    }
    ```

2. 更新手表端的build.gradle。将对公用模块的依赖修改如下：

    ``` gradle
    dependencies {
      // ...
      compile (project(':common')) {
        exclude module: 'play-services-wearable-standalone'
      }
      compile 'com.google.android.gms:play-services-wearable:7.5.0'
    }
    ```

3. 手机端的build.gradle正常依赖公用模块。

### 如何使用语音语义等特殊API

语音语义等API，都包含在了[mobvoi-api.jar][mobvoi-jar]中，只要依赖了这个jar包，即可使用[Ticwear专有API][ticwear-dev]。

使用了这些API的应用。在Android Wear 平台无法使用相关的功能。使用不当也可能造成应用崩溃。所以这类应用在迁移到 Android Wear时，需要做更多测试和检查。某些API，在Android Wear 上也有相应的实现，可以在应用内做检查来兼容。



## 遇到问题？

开发兼容模式时遇到的一些常见问题，我们会在这里列举出来。

### 手表或手机程序crash

* 如果出现 `java.lang.IncompatibleClassChangeError: The method 'void com.google.android.gms.common.api.GoogleApiClient.connect()' was expected to be of type interface but instead was found to be of type virtual`。可能是以下原因：

  1. adaptService调用顺序错误，建议放到Application的onCreate中调用。
  2. GMS版本太高，8.1.0以上版本修改了实现方式，MobvoiAPI不支持。建议使用经过测试的 7.3.0 和 7.5.0 版本。

* 如果出现找不到 `isNearby` 方法。可能是 GMS版本太低。建议使用经过测试的 7.3.0 和 7.5.0 版本。

### 手表或手机接收不到消息

检查 `WearableListenerServiceGoogleImpl` 的配置是否正确。详见 [自适应兼容模式](#自适应兼容模式)。

[aw]: https://www.android.com/wear/
[ticwear]: http://ticwear.com/
[ticwear-dev]: http://developer.ticwear.com/doc/getting-started
[wenwen]: http://chumenwenwen.com/
[as]: http://developer.android.com/sdk/index.html
[aw-pkg]: http://developer.android.com/training/wearables/apps/packaging.html
[mobvoi-jar]: https://github.com/ticwear/sdk/raw/master/lib/mobvoi-api.jar
[mobvoi-replace]: https://github.com/ticwear/sdk/raw/master/lib/mobvoi-api-gms-replaceable.jar
[gms-jar]: https://developers.google.com/android/guides/setup
[awc-sdk]: https://github.com/ticwear/sdk/raw/master/android-wear-lib/wearable-api-client-repository.zip
[demo-compact-eclipse]: https://github.com/ticwear/sdk/tree/master/sample/eclipse/CompatModeDemo
[demo-compact-as]: https://github.com/ticwear/sdk/tree/master/sample/android-studio/CompatModeDemo

