## Quick Start

### Get Ticwear SDK

Ticwear SDK has been uploaded to [bintray.com/ticwear/maven](https://bintray.com/ticwear/maven) and can be integrated by Gradle or Maven.

Ticwear SDK has two versions:

* mobvoi-api

  * The general version for developing Ticwear applications or Android Wear compatible applications.

* mobvoi-api-gms-china

  * The special version for developing Android Wear China compatible applications which can be integrated with GMS China special version (GMS 7.8.87).

How to integrate Ticwear SDK by Gradle:

1. Import the Ticwear SDK maven repository.

    ```gradle
    allprojects {
        repositories {
            jcenter()
            maven {
                url 'https://dl.bintray.com/ticwear/maven'
            }
        }
    }
    ```
2. Declare Ticwear SDK dependency

    ```gradle
    dependencies {
        compile "com.ticwear:mobvoi-api:1.1.1"
    }
    ```

    or

    ```gradle
    dependencies {
        compile "com.ticwear:mobvoi-api-gms-china:1.0.3"
    }
    ```

For developers who are more familiar with Eclipse, the jar library can be downloaded from [bintray.com/ticwear/maven](https://bintray.com/ticwear/maven).

### Create a Ticwear App

A Ticwear app consists of two parts: 1) APK running on the watch; 2) APK running on the phone. Normally, the watch end APK will be packaged in the phone end APK, and on installation it will be automatically pushed to the watch. So, when uploading an application to Ticwear store you only need to upload the mobile phone APP. Here, we will teach you step by step how to create a simple Ticwear app.

### Download SDK

Before developing an application, upgrade to the latest Android SDK version, so you can get the [latest API support](https://developer.android.com/sdk/index.html).

The Ticwear watch system is compatible with Android 5.1's API, so developers can base watch-side applications on Android 5.1 SDK, and the application can be run directly on the Ticwear system. But during the development of the mobile application same as when developing ordinary Android Apps, you need to take into account the users supported Android version to determine the API you should use.

### Configuring Developer Environment

Download the latest version of Ticwatch Companion, and install on your phone. Android phone system must be Android 4.3 or later. Connect watch and mobile phone to your computer. If the watch can not be directly connected to the computer via USB, use [WiFi](#wifi-debug) or [Bluetooth](#bt-debug) debugging to connect the watch.

### Create Project

Using Android Studio or Eclipse you can create development projects. In Android Studio Description for example, to create a project, click File -> New Project. Follow the prompts to complete the following steps:

1. In the "Configure your new project" window, enter the application name and the package name.

2. In the "Form factors" window:

	1. Select Phone and Tablet and select the API 18: Android 4.3 (Jelly Bean ) as Minimum SDK.
	2. Select Wear and select API 20: Android 4.4 (KitKat Wear) as Minimum SDK.

3. In the first window "Add an activity to Mobile", for mobile device add a blank Activity.

4. In the second "Add an activity to Wear" window, for watch device add a blank Activity.

5. Add dependency to Ticwear SDK in project.

At the end of the creation wizard, Android Studio creates a new project that contains two modules. You can now create application Activity, Service, UI and so on for the phone side and the watch side. In the mobile applications, generally do more difficult tasks such as network connections, complex calculations or tasks requiring complex user interaction. After the phone side to completes the task, you can transfer results using [data transfer API][wearable-api] to the watch.

### Installing Watch-side Application

You can use `adb install` to install watch-side APK. You can also use Android Studio or Eclipse to directly install watch-side APK to watch.

### Packaging Applications

Phone-side and watch-side applications can be installed independently on the phone and watch. But if you want to upload to the Ticwear App Store you need the watch-side of the application to be automatically sent and installed on your watch. To do this the mobile client application needs to contain a packaged watch-side APK. After you have written the two applications, mobile-side and phone-side, use one of the following two ways to package (we recommended using Android Studio to automatically package to avoid errors caused by manual configuration):

#### Android Studio automatically packaged

1. Make sure your project's root directory `build.gradle` file contains jcenter Code Library:  

	``` gradle
    repositories {
        jcenter()
    }
    ```
		 
2. In the root directory of the project build.gradle add Ticwear Packaged plug-reliance:  

	``` gradle
    dependencies {
        classpath 'com.ticwear.tools.build:gradle:1.1.0'
    }
    ```

3. In the Module `build.gradle` used Ticwear packaged plug-ins:  

    ``` gradle
    apply plugin: 'com.ticwear.application'
    // or
    apply plugin: 'ticwear'
    ```

4. Use the **release** mode.

[Application Packaging Tutorial](http://ask.ticwear.com/?/article/20)

#### Eclipse Manual Packaging

1. All of the watch-side Manifest permissions are in phone-side APK Manifest permissions
2. Ensure watch-side and mobile-side APK **has the same package name and version number**
3. After signing the watch-side application copy to the phone side project `res/raw` directory, name the client watch-side app `wearable_app.apk`
4. Create a `res/xml/wearable_app_desc.xml` document, including watch-side version information and path. For example:  

    ``` xml
    <wearableApp package="wearable.app.package.name">
            <versionCode>1</versionCode>
            <versionName>1.0</versionName>

            <!-- If you are using Android Studio to automatically package, the value is android_wear_micro_apk -->
            <rawPathResId>wearable_app</rawPathResId>
    </wearableApp>
    ```

    > **Note**:The XML file is very important，please ensure:

    > 1. The name in `rawPathResId` is the same as the watch side apk name in `res/raw`
    > 2. `versionCode`、`versionName` is the same with the watch side apk.
    > 3. `package` is the same between the phone side app and the watch side app.

5. In mobile app's <application> `meta-data`, add a `wearable_app_desc.xml` path.

    ``` xml
    <meta-data android:name="com.mobvoi.ticwear.app"
                    android:resource="@xml/wearable_app_desc"/>
    ```

6. Packaged, the watch-side app should end with the same signature as the phone-side APK signature.

7. The resulting mobile terminal APK should comply with the requirements to upload to the app store.

Alternatively, you can use Android Studio general wearable application packaging method to automatically package. But you will need to manually add the Ticwear identifier in step 5.

### Proguard Configuration

If you need to add proguard to your app, please add the following configuration in your proguard file:

``` Proguard
-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}
```

### <a id="wifi-debug"></a>WiFi Debugging

If the watch can not connect to a computer via USB, Ticwear supports the direct use of WiFi for debugging, this debugging is faster than Bluetooth debugging. Instructions:

1. Open watch settings, go to "About", repeatedly click on the Ticwear version number to open the Developer's options.

2. In the watch Developer's options find "ADB debugging" and " debugging via WiFi."

3. Connect the watch to WiFi, and find the watches ip.

4. The computer should be connected to the same WiFi.

5. Execute the following command:  

    ``` shell
    adb connect <watch ip>:7272
    ```

6. Then if the command line returns "connected to watch ip: 7272". You can use adb command operations on the watch:

    ``` shell
    adb -s <watch ip>:7272 <command>
    ```

Note, if during WiFi debugging errors occur such as `unable to connect to <watch ip>: 7272: Operation timed out`, the problem may be your WiFi router settings, use Bluetooth debugging mode.

### <a id="bt-debug"></a>Bluetooth Debugging

If the watch can not connect to a computer via USB or can not use WiFi to debug, Ticwear supports Bluetooth debugging. Instructions:

1. Open watch settings, enter "About", repeatedly click on the version number to open the Developer's options.

2. In the watch Developer's options find "ADB debugging" and "via Bluetooth debugging."

3. Open the Ticwear Companion's side panel, click the  version number on bottom multiple times to open developer options.

4. Enter "Advanced Setting" and open the Bluetooth debugging.

5. Execute the following command in terminal:

    ``` shell
    adb forward tcp:4444 localabstract:/adb-hub
    adb connect localhost:4444
    ```

6. At this point you should see target and host status become connected. You can now use adb command operations on the watch. 

    ``` shell
    adb -s localhost:4444 <command>
    ```

Note: If you encounter `unable to connect to localhost:4444: Connection refused` the problem may be that the port number is occupied, try switching the port number to resolve.

### Send and Synchronize Data

MMS API provides a range of communication interfaces between the watch and phone applications. In practice, you should use the API to ensure that your application can communicate effectively, without the need to build and manage Bluetooth connectivity and data transmission. Mainly includes the following three API:

1. **Message API**: Usually simple messaging without guaranteed syncing. A good candidate for remote calls. For example, you can send some control instructions from the watch to the phone music player. Requesting one-way information is also useful. If the phone and watch have been connected via Bluetooth, the message will be successfully sent and a success code will be returned. If Bluetooth is not connected, the message sending will fail and returns an error code.

2. **DataAPI**: Synchronization between the phone and watch data storage. When the phone and watch are connected, the data will be synchronized immediately. If left unconnected, the daty is temporarily stored on one side, and then synchronize when reconnected. Conveniently ensures consistent data transmission. When the data synchronization is complete, the receiver will receive a message notification. In order to avoid possible conflicts in data synchronization, it is best not to modify data on the watch and phone at the same time.

3. **NodeAPI**: Node connection status API, you can get a collection of nodes currently connected. You can also monitor the connection and disconnection, to make the appropriate action.

Remember, when using Wearable API to communicate, please ensure that the phone-side and watch-side of the application package name have the same signature, otherwise it will not receive the message.

Here are some simple concepts:

- **Data Items**: it provides a DataItem that can synchronize data stored between your phone and watch.
- **Asset**: Asset is the object used to send binary data such as pictures and the like. The asset attaches data items inside, MMS will automatically guarantee asset data transmission and synchronization, and automatically cache some asset to avoid unwanted transmission through Bluetooth.
-  **WearableListenerService** (for Service): After implementing WearableListenerService, you can listen to and receive messages related to the event data items inside Service.
- **DataListener, MessageListener** (for Activity): After implementing DataListener, you can listen to DataItem, change or delete events. After implementing MessageListener can receive messages sent.

### <a id="wear-ui-library"></a>Create a Suitable Watch Interface

General Android interface can run on your watch, but it is not particularly suited for watches. So developers can use the watch-side UI library to add style. Android Studio comes with a useful watch UI library. If you use Eclipse developer, you can click to download the [UI library][eclipse-ui-lib]. After downloading, create an Android library project, and then declare the project dependent on an official project, please note that this UI library depends on android-support-v7-recyclerview. For your convenience, we have included the packaged the dependent libraries, and copied the root directory android-support-v7-recyclerview.jar into main libs.

The following are some of the main categories:

- **BoxInsetLayout**: is a screen shape-aware FrameLayout that can box its children in the center square of a round screen by using the layout_box attribute.
- **CardFragment**: Contains expandable card, while in the vertical direction slidable container.
- **CircledImageView**: A circle-shaped image view.
- **ConfirmationActivity**: Displays confirmation animations after the user completes an action on the wearable.
- **CrossFadeDrawable**: Contains two child drawables and provides methods to directly adjust the blend between the two.
- **DelayedConfirmationView**: A delayed confirmation view. Provides confirmation countdown function.
- **DismissOverlayView**: A view for implementing long-press-to-dismiss in an app.
- **DotsPageIndicator**: A page indicator for GridViewPager which identifies the current page in relation to all available pages on the current row.
- **GridViewPager**: Layout manager that allows the user to navigate both vertically and horizontally through pages of content.
- **GridPagerAdapter**: Provides a page of data to GridViewPager adapter.
- **FragmentGridPagerAdapter**: A GridPagerAdapter implementation.
- **WatchViewStub**: According to the screen shape adaptive layout.
- **WearableListView**: A wristwatch optimized ListView.

### Example

For the details and demo of Mobvoi-api, please refer to the [project][mobvoi-api-demo].

[mobvoi-jar]: https://github.com/ticwear/sdk/raw/master/lib/mobvoi-api.jar
[wearable-api]: https://developer.chumenwenwen.com/en/doc/ticwear.html#doc/5/17
[eclipse-ui-lib]: https://github.com/ticwear/sdk/raw/master/lib/eclipse-UI-lib.zip
[mobvoi-api-demo]: https://github.com/ticwear/TicwearApiDemo
