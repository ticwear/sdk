# 什么是SpeechSDK

SpeechSDK是由出门问问提供的语音技术SDK，包括热词唤醒，语音识别，语义理解，垂直搜索，语音合成几个组成部分。
 
## 热词唤醒

支持设备在亮屏，灭屏状态下被用户语音唤醒，支持中英文热词，唤醒率高而误触发率低。在性能方面，特别针对各种平台嵌入式设备做了优化，具有较低的CPU及内存占用率。
 
## 语音识别

分为在线语音识别，离线语音识别以及混合式语音识别。
 
1. 在线语音识别

   使用HMM（Hidden Markov Model）+TDNN（Time Delay Neural Network）作为声学模型，在架构上优于业界普遍采用的HMM + DNN（Deep Neural Network）。语言模型会根据搜索领域进行针对性的优化，使得语音搜索结果更准确，识别率高达95%。在用户允许的前提下，可以把联系人通讯录上传，使得在线识别联系人达到97%的准确率。同时，作为Android Wear中文语音搜索的提供方以及Ticwear的开发者，我们拥有业界最多的智能硬件采集到的语音数据，从而也使得我们针对智能硬件的语音识别准确率显著高于业界水平。
 
2. 离线语音识别

   和在线语音识别采用类似的算法，但为了适应嵌入式系统的有限资源，针对离线应用场景使用了单独的模型和大量的性能优化。目前支持语音命令识别，通讯录识别，准确率可以达到97%。
 
3. 混合式语音识别

   通过一定的融合策略，混合离线和在线识别的结果，使得语音识别的最终结果可以更快，更准确的返回给用户。在没有网络或者网络质量很差的情况下也能完成语音指令功能，而在网络连接稳定的情况下，可以通过在线获得更通用的语音识别服务。
 
## 语义理解

对语音识别的结果进行语义分析。针对支持的约60个垂直领域进行了大量的优化，从而可以更准确的理解用户的查询需求。另外，还有提供给注册用户的个性化服务，比如语音指令“导航到公司”，后台可以根据用户公布给系统的公司位置信息进行自动补全。
 
## 垂直搜索

对语音识别以及理解后的结果进行垂直领域的搜索，我们有业界最全面的互联网数据库，可以提供给用户最新，最准确的搜索信息。我们自建的搜索引擎支持约60个垂直领域，均实现了多维度的查询，比如餐馆可以支持地点、价位、无线上网等。通过NLP的精准多维度分析，结合切面搜索，能够一次性完成较为复杂的用户查询。同时，技术团队对搜索速度进行了卓有成效的优化，无需等待。

## 语音合成

采用HMM（Hidden Markov Model）+ DNN（Deep Neural Network）相结合的方式进行语音合成，既保证了合成结果的稳定性，同时又提升语音的自然度，实现了业界领先的合成效果。创新性的使用了离在线相结合的方式，即便在网速不理想情况下，也可以及时给用户反馈。在线语音合成使用了独创的ultra-streaming 技术，可以极速合成大量文本，让用户瞬间享受到高质量的播报服务，同时在线合成的流量低至3kB/s, 有效节约了用户的流量资源。离线语音合成针对用户设备进行深度优化，对用户的计算资源占用极低，基本不会影响到用户的电量消耗。目前合成支持多种输出格式，包括mp3, speex格式等, 可以满足应用的多种需求。
 
# 运行平台

Android平台上目前支持所有主流体系结构，如armv8，armv7，mips，x86。
其它平台可以提供Java版SDK，目前暂不向外开放。如有需要，请联系我们。

# 使用说明

## 初始化
``` java
    // 非正式Appkey， 仅提供给开发者Demo使用
    private static final String sAppKey = "com.mobvoi.test";
    // 仅用作统计，请全局使用唯一字符串
    private static final String sClientName = "clientName";
    // 位置信息，格式为 “国家，省，市，区，街道，门牌号，纬度，经度”
    private static final String sLocation = "中国,北京市,北京市,海淀区,苏州街,3号,39.989602,116.316568";
    // 联系人列表，供离线识别使用，语义为“打电话给王斌”，“给熊伟打电话”，“发短信给邓凯”等
    private static final String[] sContacts = {"邓凯", "王斌", "熊伟"};
    // 应用列表，供离线识别使用，语义为“打开支付宝”，“关闭支付宝”等
    private static final String[] sApps = {"支付宝", "微信", "微博"};
    // 命令词列表，供离线识别使用，语义为“关机”，“重启”等
    private static final String[] sVoiceCommands = {"关机", "重启", "飞行模式"};
    // 设置位置信息，最好在每次搜索前设置以提高搜索准确度
    SpeechClient.getInstance().setLocationString(deviceName, sLocation);
    // 设置应用名称列表
    SpeechClient.getInstance().setApps(sApps);
    // 设置联系人列表
    SpeechClient.getInstance().setContacts(sContacts);
    // 设置语音命令词
    SpeechClient.getInstance().setVoiceAction(sVoiceCommands);
    // 设置VAD（静音检测）参数，目前对外提供基于SNR（信噪比）的VAD和基于DNN（深度神经网络）的VAD。
    // 后两个参数的含义分别为：语音检测门限（以毫秒为单位，若检测到的语音长度大于此门限，认为说话人已开始说话）；
    // 静音检测门限（以毫秒为单位，若检测到的静音长度大于此门限，认为说话人已停止说话）
    SpeechClient.getInstance().setVad(sClientName, VadType.DNNBasedVad, 50, 500);
    // 设置回调函数，具体后面有介绍
    SpeechClient.getInstance().setClientListener(sClientName, new SpeechClientListenerImpl());
    // 初始化，后两个参数分别为：是否激活在线识别，是否激活离线识别
    SpeechClient.getInstance().init(context, sAppKey, true, true);
```
 
而SpeechClientListenerImpl就是接口SpeechClientListener的实现类
``` java
private class SpeechClientListenerImpl implements SpeechClientListener {

    // 开始提供录音数据给语音识别引擎时回调
    public void onStartRecord() {
    }

    // 服务器端检测到静音（说话人停止说话）后回调
    void onRemoteSilenceDetected() {
    }

    // 输入语音数据实时的音量回调，范围为[0, 60]
    public void onVolume(double volume) {
    }

    // 语音识别部分结果返回，比如“今天天气怎么样”，会按顺序返回“今天”，“今天天气”，“今天天气怎么样”，前两个就属于Partial Transcription
    public void onPartialTranscription(String fixedContent) {
    }

    // 语音识别最终结果返回，比如“今天天气怎么样”，会按顺序返回“今天”，“今天天气”，“今天天气怎么样”，最后一个就是Final Transcription
    public void onFinalTranscription(final String result) {
    }

    // 语音搜索结果返回, 为JSON格式字符串
    public void onResult(final String result) {
    }

    // 错误码返回
    public void onError(final int errorCode) {
    }

    // 在检测到本地语音之后，又检测到本地静音时回调
    void onLocalSilenceDetected() {
    }

    // 一段时间未检测到本地语音时回调
    void onNoSpeechDetected() {
    }

    // 检测到本地语音时回调
    void onSpeechDetected() {
    }
}
```

## 热词唤醒
### 实现接口

``` java
	public class HotwordListenerImpl implements HotwordListener{
	    @Override
	    public void onHotwordDetected() {
	       // 此时添加热词唤醒后的处理内容
	    }
	}
 
	public interface HotwordListener {
	    void onHotwordDetected();
	}
```

### 打开热词监听
``` java
	SpeechClient.getInstance().addHotwordListener();
	SpeechClient.getInstance().startHotword();
```
 
### 关闭热词监听
``` java
	SpeechClient.getInstance().removeHotwordListener();
	SpeechClient.getInstance().stopHotword();
``` 
##语音识别

Mobvoi支持多种语音识别方式： 
- ASR，仅语音识别，无语义分析，无搜索结果。 
- Semantic, 语音识别，返回语义分析，无搜索结果。
- Onebox，语音识别，并返回搜索结果。 
- Offline，离线语音识别，目前支持命令词识别，如“打电话给王路”，“打开支付宝”等。需要用户提供APP列表，通讯录列表或命令词列表。
- Mix，离线在线混合的语音搜索，返回结果结合二者的优势。当无网络连接时，自动回退到离线。


每种识别方式，调用的接口是非常类似的，均是三个接口：

- startXXXRecognizer，启动语音识别，此时系统会录音，并把录音流式发送到语音服务器或离线模型。 
- stopRecognizer，停止系统录音，等待识别结果返回。
- cancelReconizer，取消此次语音识别，系统不会返回任何结果

例一： ASR（语音识别）
``` java
    // 开始ASR的语音识别
    SpeechClient.getInstance().startAsrRecognizer(sClientName);
```
例二： Semantic（语义理解）
```java
    // 识别之前可设置一下当前位置
    SpeechClient.getInstance().setLocationString(sClientName, sLocation);
    // 开始Semantic语音搜索
    SpeechClient.getInstance().startSemanticRecognizer(sClientName);
```
例三： Onebox（垂直搜索）
```java
    // 识别之前可设置一下当前位置
    SpeechClient.getInstance().setLocationString(sClientName, sLocation);
    // 开始Onebox语音搜索
    SpeechClient.getInstance().startOneboxRecognizer(sClientName);
```
例四： Offline（离线语音识别）
```java
    // 开始离线语音识别
    // 联系人列表，供离线识别使用，语义为“打电话给王斌”，“给熊伟打电话”，“发短信给邓凯”等
    private static final String[] sContacts = {"邓凯", "王斌", "熊伟"};
    // 应用列表，供离线识别使用，语义为“打开支付宝”，“关闭支付宝”等
    private static final String[] sApps = {"支付宝", "微信", "微博"};
    // 命令词列表，供离线识别使用，语义为“关机”，“重启”等
    private static final String[] sVoiceCommands = {"关机", "重启", "飞行模式"};
    SpeechClient.getInstance().setApps(sApps);
    SpeechClient.getInstance().setContacts(sContacts);
    SpeechClient.getInstance().setVoiceAction(sVoiceCommands);
    SpeechClient.getInstance().startOfflineRecognizer(sClientName);
```
例五： Mix（离在线语音搜索）
```java
    // 开始Mix的语音搜索
    SpeechClient.getInstance().startMixRecognizer(sClientName);

    // 停止上一次的语音识别
    SpeechClient.getInstance().stopRecognizer(sClientName);

    // 取消上一次的语音识别
    SpeechClient.getInstance().cancelReconizer(sClientName);
```
 
##热词+语音搜索（Oneshot）
支持热词+语音搜索一次触发（Oneshot）模式。 比如可以直接说“你好问问今天天气怎么样”， 这样在某些场景下， 用户会感觉交流更自然， 反应更快捷。 针对不同的语音搜索， 我们提供了不同的Oneshot接口， 包括： Mix， Onebox， Semantic和Offline, 以下以Mix为例：
###打开Oneshot
``` java
    SpeechClient.getInstance().startOneshotMixRecognizer(deviceName);
``` 
###关闭Oneshot
``` java
    SpeechClient.getInstance().stopOneshotRecognizer(deviceName);
``` 
###取消Oneshot
``` java
    SpeechClient.getInstance().cancelOneshotRecognizer(deviceName); 
``` 
 
##基于文本的语义分析和搜索

基于文本的语义分析

``` java
    SpeechClient.getInstance().startTextSemantic(deviceName, new SearchListener() {
        @Override
        public void onBeginSearch() {
          // 开始进行语义解析
        }
  
        @Override
        public void onResult(String result) {
          // 返回语义解析的结果
        }

        @Override
        public void onError(int error) {
          // 返回错误值
          // NO_ERROR = 0; 无错误
          // HTTP_ERROR = 1; HTTP错误
          // INTERNAL_ERROR = 2; 服务器错误
          // NETWORK_ERROR = 3; 本地网络错误
        }
    });
```
基于文本的垂直搜索
``` java
    SpeechClient.getInstance().startTextSearch(deviceName, sLocation, new SearchListener() {
        @Override
        public void onBeginSearch() {
          // 开始进行文本搜索
        }

        @Override
        public void onResult(String s) {
          // 返回文本搜索的结果
        }

        @Override
        public void onError(int i) {
          // 同startTextSemantic的onError回调
        }
    });
```

## 语音合成

### 开始语音合成
方式一： 不使用回调
``` java
	SpeechClient.getInstance().startTTS("海淀区天气晴朗，气温20到25摄氏度");
```
方式二： 使用回调
``` java
	class TTSListenerImpl implements TTSListener {

            @Override
            public void onStart() {
                // 当TTS开始播放时回调
            }

            @Override
            public void onError() {
                // 当TTS播放出错时回调
            }

            @Override
            public void onDone() {
                // 当TTS播放结束时回调
            }
        }
	SpeechClient.getInstance().startTTS("海淀区天气晴朗，气温20到25摄氏度", new TTSListenerImpl()）；
```

### 关闭语音合成
``` java
	SpeechClient.getInstance().stopTTS();
```
## 静音检测
所谓静音检测（VAD，Voice Activity Detection）就是检测什么时候声音开始，什么时候声音结束（Silence）。
目前端和云各有一个VAD，一旦检测到声音结束就返回结果。

主要接口有以下几个：
设置端上VAD语音开始的门限值，和语音结束的门限值。端上的VAD目前支持两种：
``` java
        // 设置端上VAD的类型为DNN类型，语音开始门限值为100ms，语音结束门限值为1000ms
	SpeechClient.getInstance().setVadParams(sDeviceOne, VadType.DNNBasedVad, 100, 1000);
```
开关端和云上的VAD
``` java
        // 关掉端和云上的VAD
	SpeechClient.getInstance().enableLocalSilence(false);
        SpeechClient.getInstance().enableRemoteSilence(false);

        // 打开端和云上的VAD
	SpeechClient.getInstance().enableLocalSilence(true);
        SpeechClient.getInstance().enableRemoteSilence(true);
```

## 联系人同步
支持SDK自动从通讯录中同步联系人，也支持用户自己获取联系人，并通过setContacts接口设置到SDK内部。
如果用户设置了自动同步，则支持一系列查询接口。

``` java
        // 开启自动同步联系人
        SpeechClient.getInstance().enableAutoSyncContacts();
        // 通过电话号码查询联系人名
        SpeechClient.getInstance().getContactNameByNumber("10086");
        // 通过联系人名查询他的所有电话号码
        SpeechClient.getInstance().getContactsByName("王斌");

```

# 错误码

| 错误码 	| 描述             	|
|--------	|------------------	|
| 0      	| 语音服务器错误   	|
| 1      	| 网络错误         	|
| 2      	| 无网络           	|
| 3      	| 录音设备错误     	|
| 4      	| 识别内容为空     	|
| 5      	| 输入语音过长     	|
| 6      	| 起始静音时间过长 	|
| 7      	| 网络太慢         |
 

## 搜索领域

| 垂直领域               	| Onebox        	| 查询示例                                     	|
|------------------------	|---------------	|----------------------------------------------	|
| 无分类                 	| OTHER         	| 帮我解一个微分方程                           	|
| 天气                   	| WEATHER       	| 今天的天气怎么样（明天上海的呢）             	|
|                        	|               	| 北京明天的天气                               	|
|                        	|               	| 今天的空气质量                               	|
| 打电话                 	| CALL          	| 打电话给李志飞                               	|
|                        	|               	| 打电话给10086                                	|
|                        	|               	| 呼叫13888888888                              	|
| 餐厅                   	| RESTAURANT    	| 附近的川菜馆                                 	|
|                        	|               	| 北京有什么好吃的                             	|
|                        	|               	| 帮我找一下附近人均100元左右的川菜馆，带WI-FI 	|
| 预设答案的查询         	| FAQ           	| 你好问问，你是谁                             	|
| 附近POI地点            	| POI           	| 附近的厕所/酒店/银行                         	|
| 导航                   	| NAVI          	| 帮我导航到北京大学东门                       	|
| 打车                   	| TAXI          	| 我要打车/打车去***/帮我打一个滴滴出租车      	|
| 设置提醒               	| REMINDER      	| 提醒我5分钟后喝水/提醒我晚上8点给家里打电话  	|
| 百度百科               	| BAIKE         	| 刘德华是谁                                   	|
|                        	|               	| 张碧晨的百科                                 	|
|                        	|               	| 习近平的资料                                 	|
| 火车                   	| TRAIN         	| 帮我查一下明天到上海的火车                   	|
| 查询地址               	| WHERE         	| 我在哪                                       	|
| 搜电影                 	| CINEMA        	| 最近有什么好看的电影                         	|
| 查新闻                 	| NEWS          	| 今天有什么新闻                               	|
|                        	|               	| 今天的体育新闻                               	|
|                        	|               	| 热门新闻                                     	|
|                        	|               	| 刘德华的相关新闻                             	|
| 音乐                   	| MUSIC         	| 我要听刘德华的歌                             	|
|                        	|               	| 来首邓紫棋的泡沫                             	|
| 控制手机（开启蓝牙等） 	| CONTROL       	| 关机，重启，重置系统                         	|
| 查日历（农历）         	| CALENDAR      	| 今天是农历几号/今天是农历初几                	|
| 查航班                 	| FLIGHT        	| 帮我查一下明天到上海的航班                   	|
| 查美女帅哥             	| BEAUTY        	| 附近的美女                                   	|
|                        	|               	| 附近的帅哥                                   	|
| 问笑话                 	| JOKE          	| 给我讲一个笑话                               	|
| 软件                   	| APP           	| 帮我下载一个微信                             	|
| 搜酒店                 	| HOTEL         	| 附近的汉庭酒店                               	|
| 百度问答知道           	| WENDA         	| 小孩子发烧怎么办                             	|
|                        	|               	| 肚子疼怎么办                                 	|
| 黄页                   	| YELLOWPAGE    	| 北京工商局的电话                             	|
| 购物 （taobao)         	| SHOPPING      	| 我要买裙子                                   	|
|                        	|               	| 什么值得买                                   	|
| 问时间                 	| TIME          	| 美国时间现在是几点钟                         	|
|                        	|               	| 现在几点                                     	|
|                        	|               	| 纽约现在几点了                               	|
| 发短信                 	| SMS           	| 发短信给***                                  	|
| 影视                   	| VIDEO         	| 太阳的后裔                                   	|
| 计算器                 	| CAL           	| 5+8等于几                                    	|
| 查地铁                 	| SUBWAY        	| 帮我查一下地铁10号线的首末班时间             	|
| 翻译                   	| TRANSLATE     	| 我爱你用韩语怎么说                           	|
| 查公交                 	| BUS           	| 帮我查一下365路公交                          	|
|                        	|               	| 帮我查一下附近的公交站                       	|
| 查星座                 	| CONSTELLATION 	| 今天出生的是什么星座                         	|
| 找文章小说             	| READING       	| 帮我查一下琅琊榜小说                         	|
| 查路况                 	| ROADTRAFFIC   	| 海淀区苏州街的路况                           	|
| 查股票                 	| STOCK         	| 百度的股价                                   	|
| 游记攻略               	| TRAVELGUIDE   	| 北京旅游攻略                                 	|
| 赛事                   	| GAMESCORE     	| 欧洲杯比赛情况                               	|
|                        	|               	| 2016年奥运会                                 	|
| 查电视节目             	| TV            	| 极限挑战                                     	|
| 查快递                 	| DELIVERY      	| 帮我查一下我的快递                           	|
|                        	|               	| 我要寄快递                                   	|
| 搜图片                 	| PICTURE       	| 帮我搜一下狗狗的图片                         	|
| 查网站                 	| WEBSITE       	| 打开百度                                     	|
| 查菜单                 	| RECIPE        	| 红烧肉怎么做                                 	|
| 找工作                 	| JOB           	| 我要找工作                                   	|
| 租房                   	| HOUSING       	| 我要租房                                     	|
| 生活服务               	| LIFESERVICE   	| 帮我找一个保洁                               	|
| 查线下活动             	| LOCALEVENT    	| 北京今天有什么线下活动                       	|
| 查团购                 	| GROUPON       	| 旅游团购                                     	|
| 查找优惠               	| COUPON        	| 最近有什么优惠信息                           	|
| 航班实时信息           	| FLIGHT-INFO   	| 帮我查一下海南航空HU7609起飞了吗             	|
| 设置闹钟               	| ALARM         	| 设置一个10分钟后的闹钟/设置一个9点的闹钟     	|
| 倒计时                 	| COUNTDOWN     	| 设置一个5分钟的倒计时                        	|
| 日程                   	| AGENDA        	| 提醒我晚上9点给家里打电话                    	|
| 查彩票                 	| LOTTERY       	| 大乐透中奖信息                               	|
| 查违章                 	| VIOLATION     	| 帮我查一下我的违章信息                       	|
| 找代驾                 	| DAIJIA        	| 帮我找一个代驾                               	|
| 查询帮助               	| HELP          	| 帮助                                         	|
| 单位换算               	|               	| 1美元等于多少人民币                          	|
|                        	|               	| 1公里等于多少米                              	|



