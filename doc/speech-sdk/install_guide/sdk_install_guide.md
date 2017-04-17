##Android版本
###步骤一：
下载SpeechSDK.zip。解压后，得到一个文件夹


###步骤二：
在Android Studio中打开这个文件夹


注意：SpeechSDK的库在SpeechSDKLib文件夹下。
在全量版本的SpeechSDK中，我们提供两个样例工程demo和robot；
在轻量版本的SpeechSDK中，我们提供一个样例工程robot；
在超轻量版本的SpeechSDK中，我们提供一个样例工程demo-mini。

###步骤三：
选择一：编译安装demo或robot到android手机，可进行测试。打开样例工程，修改样例代码，可进行开发。


选择二：重新创建工程，将SpeechSDKLib下的库，以及SpeechSDK用到的第三方库导入，可开始开发。


##Linux版本

仅需引用speech_sdk.h头文件，编译时链接上libmobvoisdk.so即可。


