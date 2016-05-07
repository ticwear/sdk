## 快速入门

### 创建一个Ticwear应用

一个Ticwear应用由两部分组成：1) 运行在手表端的APK；2) 运行在手机端的应用。通常情况下，运行在手表端的APK会被打包在手机端应用中，在安装时会被自动推送到手表上。所以将Ticwear应用上传到Ticwear应用商店时只需要上传手机端应用即可。在这里，我们将一步步教你如何创建一个简单的Ticwear应用。

### 下载SDK

在开发应用前，将安卓SDK版本升级到[最新版本](https://developer.android.com/sdk/index.html), 可以获取最新的API支持。

手表端Ticwear系统兼容Android 5.1的API，所以开发者可以基于Android 5.1的SDK开发手表端的应用，该应用可以直接在Ticwear系统上运行。但是当开发者在开发手机端的应用时则应和普通的安卓应用一样，依据用户的手机安卓系统版本来确定支持的API。

### 配置开发环境

在应用市场上下载最新版本的Ticwear助手，安装在手机上。手机安卓系统必须为安卓4.3以上版本。将手表和手机均连接到电脑上，若手表无法通过USB直接连接电脑，请参看[WiFi调试](#wifi-debug)或者[蓝牙调试](#bt-debug)来连接手表。

### 创建项目

用 Android Studio 或者 Eclipse 均可以创建开发项目。以Android Studio举例说明，在创建项目过程中，点击 File -> New Project ，按照建立程序向导的提示，进行以下步骤的操作：

1. 在Configure your new project窗口，输入应用名和包名。

2. 在Form factors窗口:

    1. 选择Phone and Tablet并选择API 18: Android 4.3 (Jelly Bean) 作为Minimum SDK。
    2. 选择Wear并选择API 20: Android 4.4 (KitKat Wear) 作为Minimum SDK。

3. 在第一个Add an activity to Mobile窗口，为手机移动设备添加一个空白Activity。

4. 在第二个Add an activity to Wear窗口，为手表设备添加一个空白Activity。

5. 下载[mobvoi-api.jar][mobvoi-jar]，添加到项目的库依赖中。

当结束创建向导以后，Android Studio会创建一个包含两个模块的新工程。你现在可以为手机端和手表端的两个应用创建Activity、Service、UI等等。在手机端应用，一般做一些比较重的任务，例如网络连接，复杂的计算或者一些需要复杂用户交互的任务。当在手机端完成任务以后，可以把手机端的处理结果通过[数据传输API][wearable-api]通知给手表端。

### 安装手表端应用

将生成的手表端APK通过`adb install`的方式即可安装到手表端。也可以通过 Android Studio 或者 Eclipse 直接将手表端APK安装到手表端。

### 打包应用

手机端和手表端的应用可以分别独立安装到手机和手表上。但如果希望上传到应用商店，由Ticwear来将手表端的应用自动推送到手表上安装，需要打包一个包含手表端应用的手机端APK应用。首先分别写出手机端和手表端的两个应用，然后使用如下两种方式进行打包（推荐 Android Studio 自动打包，可以避免手动配置导致的各类错误）：

#### Android Studio 自动打包

1. 确保你的工程根目录的 `build.gradle` 文件中包含了 jcenter 代码库：

    ``` gradle
    repositories {
        jcenter()
    }
    ```

2. 在工程根目录的 `build.gradle` 中添加对 Ticwear 打包插件的依赖：

    ``` gradle
    dependencies {
        classpath 'com.ticwear.tools.build:gradle:1.1.0'
    }
    ```

3. 在 Module 的 `build.gradle` 中使用 Ticwear 打包插件：

    ``` gradle
    apply plugin: 'com.ticwear.application'
    // or
    apply plugin: 'ticwear'
    ```

4. 使用 **release** 方式打包。

[查看更多应用打包图文教程](http://ask.ticwear.com/?/article/20)

#### Eclipse 手动打包

1. 将手表端APK的所有Manifest中的权限声明都在手机端APK的Manifest中声明。
2. 保证手表端APK和手机端APK**有同样的包名和版本号**
3. 将签名以后的手表端应用拷贝到手机端项目的`res/raw`的目录下，命名该手表端应用为`wearable_app.apk`
4. 创建一个 `res/xml/wearable_app_desc.xml` 文件，包括手表端应用的版本和路径信息。例如：

    ``` xml
    <wearableApp package="wearable.app.package.name">
            <versionCode>1</versionCode>
            <versionName>1.0</versionName>

            <!-- 如果使用Android Studio自动打包, 该值为android_wear_micro_apk -->
            <rawPathResId>wearable_app</rawPathResId>
    </wearableApp>
    ```

    > **注**：这个XML文件非常关键，请保证:

    > 1. `rawPathResId`里的名称，与你放到`res/raw`下的手表APK名称一致。
    > 2. `versionCode`、`versionName` 与手表APK一致。
    > 3. `package`与手机、手表一致。

5. 添加一个 `meta-data` 标签在手机端app的 `<application>` 中，声明 `wearable_app_desc.xml` 的路径。

    ``` xml
    <meta-data android:name="com.mobvoi.ticwear.app"
                    android:resource="@xml/wearable_app_desc"/>
    ```

6. 打包，使用与手表端应用相同的签名对最终的手机端APK进行签名。

7. 最后得到的手机端APK即为符合要求可以上传应用商店的应用。

### 混淆配置

如果你对应用进行了混淆，请在包含有 Mobvoi API 的应用中添加如下配置：

``` Proguard
-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}
```

### <a id="wifi-debug"></a>WiFi调试

对于手表无法通过USB连接到电脑的情况，Ticwear支持直接使用WiFi进行调试，调试速度比蓝牙调试快。使用方法：

1. 打开手表端的设置，进入“关于”，多次点击版本号，即可打开开发者选项。

2. 打开手表端开发者选项中的“ADB调试“和“通过WiFi调试”。

3. 将手表连上WiFi，并查看手表的ip。

4. 将电脑连上同一个WiFi。

5. 执行以下命令:

    ``` shell
    adb connect <手表ip>:7272
    ```

6. 这时若命令行中返回“connected to 手表ip:7272”。即可使用adb命令操作手表端。

    ``` shell
    adb -s <手表ip>:7272 <command>
    ```

注，如果WiFi调试时，出现类似 `unable to connect to <手表ip>:7272: Operation timed out` 的问题，可能是你的WiFi路由器设置了屏蔽，请使用蓝牙调试方式。

### <a id="bt-debug"></a>蓝牙调试

对于手表无法通过USB连接到电脑或无法使用WiFi调试的情况，Ticwear支持使用蓝牙进行调试。使用方法：

1. 打开手表端的设置，进入“关于”，多次点击版本号，即可打开开发者选项。

2. 打开手表端开发者选项中的“ADB调试“和“通过蓝牙调试”。

3. 打开手机端Ticwear助手，点击右上角进入“更多”选项，多次点击版本号上的图标，打开开发者选项。

4. 打开手机端开发者蓝牙调试开关。

5. 执行以下命令:

    ``` shell
    adb forward tcp:4444 localabstract:/adb-hub
    adb connect localhost:4444
    ```

6. 这时应该看到手机端蓝牙调试选项中target和host的状态都变为connected。现在即可使用adb命令操作手表端。

    ``` shell
    adb -s localhost:4444 <command>
    ```

注： 如果遇到 "unable to connect to localhost:4444: Connection refused" 之类的问题，可能是端口号被占用，尝试切换端口号来解决。

### 发送和同步数据

MMS的API提供了一系列的接口作为手机端和手表端应用的通信方式。在实际应用中应该使用这些API来保证你的应用可以进行有效的通信，而不需要自己建立和管理蓝牙连接以及数据收发。主要包括以下三种API：

1. **MessageAPI**: 一般简单无需保证同步的消息的收发。比较适用于进行远程调用的场景。例如从手表发送一些控制指令以控制手机端的音乐播放器。消息对于一些单向的请求模型也比较使用。如果手机端和手表端已经通过蓝牙连接上，那么消息会被成功发送并返回一个正确的返回码。如果蓝牙未连接，那么消息发送失败，并返回一个错误的返回码。

2. **DataAPI**: 手机端和手表端之间互相同步的数据存储。当手机端和手表端处于连接状态时，数据会马上进行同步。若处于未连接状态时，会暂时存储在一方，待重新连接上时再进行同步。方便调用者进行复杂的需要保证数据一致的数据传输。当数据同步完成后，数据的接收方会收到对应的消息通知。为了避免数据同步中可能的冲突，调用者最好不要在手机端和手表端对同一份数据进行修改。

3. **NodeAPI**: 节点连接状态的API，可以获取当前连接的节点集合。同时也可以监听连接和断连的事件通知，作出相应操作。

切记，在使用Wearable API进行通信时，请保证手机端和手表端的应用的包名和签名相同，否则会收不到消息。

以下是一些简单的相关概念：

- **Data Items**: 一个DataItem提供了可以在手机端和手表端之间同步的数据存储。

- **Asset**: Asset对象是为了用来发送二进制数据例如图片之类的。将asset附着在data items里面，MMS会自动保证asset数据的发送和同步，并自动缓存了一些asset来避免无用的蓝牙的传输。

- **WearableListenerService** (用于Service): 继承WearableListenerService之后，可以在该Service里面监听到相关的接收消息和data items的事件。

- **DataListener, MessageListener** (用于Activity): 实现DataListener以后，可以监听DataItem改变或者被删除的事件。实现MessageListener以后，可以接收发过来的消息。

### <a id="wear-ui-library"></a>创建适合手表展现的界面

一般的Android界面可以在手表上运行，但并不是特别适合手表端的展现。所以开发者可以选用手表端的UI库来创建更适合在手表端展现的样式。在Android Studio里面自带了适合手表使用的UI库。如果使用Eclipse开发，可以[点击下载][eclipse-ui-lib]这个UI库。下载以后，建立一个Android的library项目，然后在正式项目中声明对该项目的依赖，请注意，此UI库依赖于[android-support-v7-recyclerview](https://developer.android.com/tools/support-library/features.html#v7-recyclerview)。为方便起见，我们也打包了这个依赖库，将根目录下的 `android-support-v7-recyclerview.jar` 复制到主工程libs中并依赖即可。

以下是一些主要的类：

**BoxInsetLayout**: 一个对屏幕形状可以自适应的FrameLayout，并能够把子视图放在圆形屏幕中心。

**CardFragment**: 一个包含可展开的卡片，同时在竖直方向上可滑动的容器。

**CircledImageView**: 一个圆形的image view。

**ConfirmationActivity**: 一个让用户完成确认操作的activity。

**CrossFadeDrawable**: 包含两个drawable，提供一些方法在这两个drawable之间平滑切换。

**DelayedConfirmationView**: 一个延迟确认的视图。提供倒计时确认功能。

**DismissOverlayView**: 一个实现长按消失的视图。

**DotsPageIndicator**: 一个为GridViewPager提供页码的视图。

**GridViewPager**: 一个可以在竖直和水平方向上滑动翻页的视图。

**GridPagerAdapter**: 一个提供页面数据给GridViewPager的adapter。

**FragmentGridPagerAdapter**: 一个GridPagerAdapter的实现。

**WatchViewStub**: 根据屏幕形状自适应的layout。

**WearableListView**: 一个为手表适配优化过的ListView。

### 示例
关于Mobvoi-api的详细用法以及手表的界面实现范例，可以参考项目[mobvoi-api-demo]。


[mobvoi-jar]: https://github.com/ticwear/sdk/raw/master/lib/mobvoi-api.jar
[wearable-api]: http://developer.ticwear.com/doc/wearable-api
[eclipse-ui-lib]: https://github.com/ticwear/sdk/raw/master/lib/eclipse-UI-lib.zip
[mobvoi-api-demo]: https://github.com/ticwear/TicwearApiDemo

