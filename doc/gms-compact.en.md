## Develop Smartwatch Apps compatible with both Ticwear and Android Wear


Developers can build an app running on both Ticwear and Android Wear as we try our best to keep a API-level Compatibility with Android Wear when we develop Ticwear. 

<!-- break -->

### Packaging, Compatible Mode and other Terms used below

**Android Wear for China, Android Wear for International**， Android Wear for International can not work in China due to some reasons, thus Google launches Android Wear for China with special GMS, and developers for China only need to change the dependencies of GMS without changing any codes.

**Packaging**， watch apps can not work alone for now and it must be embedded into a corresponding phone app, so developers need to package a watch app inside a phone app. Android Wear for China shares the same configuration with International version, while a litter change is needed for Ticwear, more in [Packaging](#packaging).

**GMS & MMS**，messaging API between watch and phone. GMS (Google Mobile Service) has messaging API introduced by Google, while MMS (Mobvoi Mobile Service) is from Mobvoi. MMS API follows GMS in most cases despite different names.

**Compatible Mode**(Message-Level Compatible), SDK of Ticwear provides a Compatible Mode which allows a App to run on Ticwear and Android Wear without changing any codes, by switch to MMS or GMS automatically. This mode only works for messaging, and others like voice recognition is not supported for now.

### Three Typical Cases

Apps with different APIs need different measures and here comes three typical Cases:

#### Apps without any Special APIs

Apps without any special API(messaging, voice and etc.) can run on Ticwear and Android Wear easily, just need some attention to packaging, such as watchface(without messaging with phone), game, tool and other simple apps. See [Packaging](#packaging).

#### Apps with only MMS/GMS

Apps with only MMS/GMS is the majority, since most watch apps need some information from phone. For those apps, developers need also care about [Communication](#communication) besides [Packaging](#packaging).  is [AWC Communication](#aw-china) for Android Wear for China.

#### Apps with Special APIs besides MMS/GMS

Apps with special APIs besides MMS/GMS, such as voice recognition, location, health data and etc. should also have a look at How to use [Special APIs](#special-api), besides communication and packaging introduced above.

### <a id="packaging"></a>Packaging

The main difference of Packaging for different platforms is the App Tag in manifest file, which can be regarded as a mark for recognizing. An app without correct App Tag won't be installed in watch.

Packaging for Ticwear: [Getting Started ][ticwear-dev].

Packaging for Android Wear: If you use [Android Studio][as], , your app should be already ok with `wearApp('yourWearApp')`, nothing more is needed. More in [Packaging Wearable Apps][aw-pkg].  If you use Eclipse(not recommended), the following meta-data need be added to manifest file after configuring Ticwear packaging.

    ``` xml
    <meta-data android:name="com.google.android.wearable.beta.app"
                android:resource="@xml/wearable_app_desc"/>
    ```

    Very similar to Ticwear:

    ``` xml
    <meta-data android:name="com.mobvoi.ticwear.app"
                android:resource="@xml/wearable_app_desc"/>
    ```

`wearable_app_desc` points to the description file of you watch app.

Yes, if you want to package for both Android Wear and Ticwear, both of the above meta-data are necessary.

If you want to package for Android Wear China, you need to add the meta-data in additional.

    ``` xml
    <meta-data android:name="com.google.android.wearable.local_edition_compatible"
                android:value="true"/>
    ```


### <a id="communication"></a>Communication

Communication in Ticwear should use MMS APIs in Mobvoi SDK, which implements all of messaging APIs in Google Mobile Services(GMS), including Node API, Message API and Data API. Interface and function name are exactly the same with Android Wear.

Communication in Android Wear for International use GMS, while [Special GMS](#aw-china) in Android Wear for China.

Relationship between Mobvoi API (MMS), Google Play Service (GMS) and Google Play Service Standalone is below:

![MMS & GMS Relationship](/assets/img/gms-mms-relationship.png "MMS & GMS Relationship")

Different SDKs has different communication protocols, while Mobvoi API is compatible with all of them.

There are three different solutions for different compatibility scenes using Mobvoi API, with which you can make Ticwear apps work on Android Wear and vice versa. We strongly recommend the first solution, since you can have you app work on both Ticwear and Android Wear with the same package, which is convenient for maintenance.

#### <a id="adapt-compat"></a>Adaptive Mode

1. Include [mobvoi-api.jar][mobvoi-jar], and add or keep [google-play-services][gms-jar] ([special GMS](#aw-china) for Android Wear for China).

    * Note 1: We recommend Android Studio IDE again, and you need add meta-data and jar of Google Play Services manually if you still use Eclipse, more in: [Setting Up Google Play Services][gms-jar]。
    * Note 2: Sample code ([Eclipse][demo-compact-eclipse]/[Android Studio][demo-compact-as])
    * Note 3: 7.3 ~ 7.8 have been well tested and problems may happen if you use other version.

2. Use Mobvoi API, more in [Getting Started][ticwear-dev]. You can follow steps below if you have developed an Android Wear app already:

    1. Replace Google Mobile Services (GMS) API with Mobvoi Mobile Services (MMS) API, which is same with GMS except package name.
    2. Use `MobvoiApiClient` instead of `GoogleApiClient`.
    3. Change `com.google.android.gms.wearable.BIND_LISTENER` to `com.mobvoi.android.wearable.BIND_LISTENER` in `AndroidManifest.xml` if you use `WearableListenerService`.

3. `MobvoiApiManager.getInstance().adaptService(context)` need to be invoked when phone and watch start, and this method must be invoked before any other API method(`Application.onCreate` is a good candidate), and it will automatically decide to use MMS or GMS depending on current system. If you want to control this manually, you can use `MobvoiApiManager.getInstance().loadService(context, group)` instead of `adaptService`. If neither of the above is used, your app will only work on Ticwear. Following is the sample codes:

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
4. Register GMS Wearable Listener Service proxy in `AndroidManifest.xml` if you use `WearableListenerService`:

    ``` xml
    <service android:name=
          "com.mobvoi.android.wearable.WearableListenerServiceGoogleImpl">
             <intent-filter>
                        <action android:name=
                    "com.google.android.gms.wearable.BIND_LISTENER" />
            </intent-filter>
    </service>
    ```

5. Re-compile and package.
You can have a look at sample code in our github: ([Eclipse][demo-compact-eclipse]/[Android Studio][demo-compact-as]).

#### Only-Ticwear Mode

This mode has an advantage that you do not need to include GMS since you will not support Android Wear.

1. Remove `google-play-services` and add [`mobvoi-api.jar`][mobvoi-jar]。
2. Use Mobvoi API. If you already has an Android Wear app: 
    1. Replace Google Mobile Services (GMS) API with Mobvoi Mobile Services (MMS) API, which is same with GMS except package name.
    2. Use `MobvoiApiClient` instead of `GoogleApiClient`.
    3. Change `com.google.android.gms.wearable.BIND_LISTENER` to `com.mobvoi.android.wearable.BIND_LISTENER` in `AndroidManifest.xml` if you use `WearableListenerService`.
3. Re-compile and package.

#### API-Replace Mode

You need only change API library without any code modification, which is a quick and dirty solution since you can not use Ticwear special API or work on Android Wear, so this is only for early test.

You need include a special SDK: mobvoi-api-gms-replaceable.jar ([Download][mobvoi-replace]), which is not mobvoi-api.jar.

1. Remove [`google-play-services`][gms-jar] and add [`mobvoi-api-gms-replaceable.jar`][mobvoi-replace].
2. Re-compile and package.


### <a id="aw-china"></a>Communication of Android Wear for China

Apps of Android Wear for International can not run on Android Wear for China directly. Apps with communication with phone need change GMS library of both phone and watch, which do not need change codes(including obfuscated configuration, which keep same with [`GMS`][gms-jar]), more in [Google Official Doc][awc-doc], and also we have a demo in [CompatModeChinaDemo][demo-compact-china].

Packaging apps of Android Wear for China has following four steps:

1. Download [Android Wear for China SDK][awc-sdk], which is `google-play-services-7-8-87.zip`.
2. Create local Maven repository, unzip zip file of step 1 into root folder of this project.
3. Add dependency to this Maven in `build.gradle` in root folder of this project.

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

4. Update gms client lib of both phone and watch sides. Replace dependency in `build.gradle` of APK module

    ``` gradle
    compile 'com.google.android.gms:play-services-wearable:{$gmsVersion}'
    ```

    to new library

    ``` gradle
    compile 'com.google.android.gms:play-services-wearable:7.8.87'
    ```
    
    Notice that the version must be `7.8.87`。

5. Package using Android Studio and install on phone with companion of Android Wear for China.

Thus your app will support both Android wear for China and Android Wear for International.

### <a id="special-api"></a>How to use Special API of Ticwear

Special APIs of Ticwear, such as voice recognition are all included in [mobvoi-api.jar][mobvoi-jar] you only need to include this jar to use [Ticwear special API][ticwear-dev]。

Obviously, those APIs can not work on Android Wear, so you need do some code protection in case of crash when you want to move to Android Wear. Also, some APIs have Android Wear version, and you can add some branch logic.

## <a id="qa"></a>FAQ

### How to know whether GMS or MMS is working currently, or whether GMS or MMS is supported?

We have provide a specific API for developers to know which is running:

```
MobvoiApiManager.getInstance().getGroup();
```

Return value:

```
public enum ApiGroup {
    MMS, GMS, NONE
}
```

NONE means something wrong with initialization, please see [Adaptive Mode](#adapt-compat).

Another API for support check:

```
MobvoiApiManager.getInstance().isGmsAvailable(context);
MobvoiApiManager.getInstance().isMmsAvailable(context);
```


### Watch app or phone app crash

- If you see `java.lang.IncompatibleClassChangeError: The method 'void com.google.android.gms.common.api.GoogleApiClient.connect()' was expected to be of type interface but instead was found to be of type virtual`, probable causes:

  1. AdaptService invoking problem, we recommend you invoke this method in OnCreate function in Application.
  2. GMS version is not right, only 7.3 ~ 7.8 are tested.

- If error with `isNearby` method, also GMS problem, please use 7.3 ~ 7.8.

### Watch and phone can not receive message

- Both Ticwear companion and Android Wear companion are installed on your phone?

MobvoiAPI need choose a message protocol when start, so if you install two companions, it will use Ticwear. So, you need uninstall Ticwear companion when you test on Android Wear watch, or set GMS manually using `MobvoiApiManager.loadService()` .

- Check configuration of `WearableListenerServiceGoogleImpl`. More in [Adaptive Mode](#adapt-compat).

### connect fail

If you see the log like this:

```
GooglePlayServicesUtil: Google Play services out of date.  Requires 7895000 but found 7887534
```

Please ensure whether your GMS version is out of date, or whether you have modified the dependency of GMS both on the phone and the watch app (Need to ensure the version is `7.8.87`).

### How to debug GMS communication

Similar with the [Debugging MMS][wearable-debug], we can also turn on the GMS debugging log for bug tracking.

Run `adb shell` on target device, and execute the following commands to turn on the debugging log:

``` shell
setprop log.tag.WearableService VERBOSE
setprop log.tag.WearableConn VERBOSE
```


[aw]: https://www.android.com/wear/
[ticwear]: http://ticwear.com/
[ticwear-dev]: http://developer.chumenwenwen.com/en/v2/doc/ticwear/getting-started
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

