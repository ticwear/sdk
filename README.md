# Ticwear Developer SDK
为了方便第三方开发者迅速使用SDK来开发Ticwear应用，我们提供了以下联系方式来协助开发：  
**QQ群：114947882**  
**微博：Ticwear**  
**官网：http://developer.chumenwenwen.com/**
**社区：http://ask.ticwear.com/**  

## 获取 Ticwear SDK

Ticwear SDK已上传至 [bintray.com/ticwear/maven](https://bintray.com/ticwear/maven)，可通过Gradle或者maven的方式集成到你的应用中。

Ticwear SDK主要有两个版本：

1. mobvoi-api
   * 这个是通用版，可用于开发纯Ticwear应用，也可用于开发与Android Wear（GMS通用版）兼容的应用。
2. mobvoi-api-gms-china
   * 这个是兼容老的Android Wear中国版（GMS 7.8.87）的特殊版本（Android Wear 2.0及更新版本请使用mobvoi-api通用版本）。

使用Gradle方式集成Ticwear SDK的方法如下：

1. 引用Ticwear SDK的maven库

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
2. 使用Ticwear SDK

    ```gradle
    dependencies {
        compile "com.ticwear:mobvoi-api:1.1.1"
    }
    ```

    或者

    ```gradle
    dependencies {
        compile "com.ticwear:mobvoi-api-gms-china:1.0.3"
    }
    ```

对于使用Eclipse的开发人员，可以从 [bintray.com/ticwear/maven](https://bintray.com/ticwear/maven) 上下载JAR包使用。

## 非Android Wear开发者
我们建议您采用如下步骤
* Step 1：运行sample文件夹里的示例程序来了解SDK提供的基本功能，如数据传输和语音输入
* Step 2：阅读开发文档深入了解如何使用: http://developer.chumenwenwen.com/doc/ticwear.html

## Android Wear开发者
Android Wear应用目前分为国际版和中国版，中国版的应用需要使用裁剪版的SDK（在文件夹android-wear-lib中可以找到）。
关于如何开发一个中国版的Android Wear应用，并将此应用移植到Ticwear平台，请参考Android Wear应用兼容文档：  
http://developer.chumenwenwen.com/doc/ticwear.html#doc/1/2

如果您想让您的应用同时兼容Android Wear（国际版或中国版）和Ticwear，请采用如下步骤

1. 添加Ticwear打包方式
  * 确保你的工程根目录的 `build.gradle` 文件中包含了 jcenter 代码库：

    ``` gradle
    repositories {
        jcenter()
    }
    ```

  * 在工程根目录的 `build.gradle` 中添加对 Ticwear 打包插件的依赖：

    ``` gradle
    dependencies {
        classpath 'com.ticwear.tools.build:gradle:1.1.0'
    }
    ```

  * 在 Mobile Module 的 `build.gradle` 中使用 Ticwear 打包插件：

    ``` gradle
    apply plugin: 'com.ticwear.application'
    // or
    apply plugin: 'ticwear'
    ```

  * 使用 release 方式打包
  * 更多应用打包详情，参考开发者文档中的[打包应用](http://developer.chumenwenwen.com/doc/ticwear.html)

2. 如果你使用了GMS通讯，需要替换成 Mobvoi Mobile Services：
  * 在项目中引入Ticwear SDK，同时保留 GMS SDK (Google Mobile Services)
  * 将代码中的GMS API替换为仅包名不同的Mobvoi Mobile Services (MMS) API，GoogleApiClient替换为MobvoiApiClient。在AndroidManifest.xml里面把`com.google.android.gms.wearable.BIND_LISTENER`替换为`com.mobvoi.android.wearable.BIND_LISTENER`
  * 在App启动时调用MobvoiApiManager.getInstance().adaptService(context)，该方法必须在任何可能的API调用操作前调用，它将会自动探测当前系统情况，选择底层是使用MMS或GMS。如果想自己决定使用哪种API，可以通过调用MobvoiApiManager.getInstance().loadService(context, group)来指定使用Ticwear或Android Wear的API，以取代上面的adaptService方法。如果这两个方法都没有被调用，API会变成仅Ticwear系统能使用的方式。
  * 在AndroidManifest.xml中注册GMS Wearable Listener Service的代理服务：

    ```java
    <service android:name="com.mobvoi.android.wearable.WearableListenerServiceGoogleImpl">
      <intent-filter>
        <action android:name="com.google.android.gms.wearable.BIND_LISTENER" />
      </intent-filter>
    </service>
    ```
* 重新编译打包

3. 更多兼容AW的详情，参考开发者文档中[AW应用兼容](http://developer.chumenwenwen.com/doc/ticwear.html#doc/1/2)
