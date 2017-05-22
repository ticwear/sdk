# 1. SDK支持什么平台架构？
Android版本的SDK支持x86，mips，armv7，armv8平台；Linux版本的SDK目前暂时只支持mips平台，后续很快会提供x86，armv7，armv8平台。

# 2. ASR识别支持的采样率和音频格式有哪些？
Android和Linux SDK音频均支持16k采样，16bit位深，pcm格式。

# 3. 唤醒热词如何更改？
目前有两种方式提供唤醒热词，一种是深度定制，一种是动态生成（仅限Android SDK全量版）。深度定制版从功耗，性能，敏感度和误触率上均优于动态生成版，但需要商务联系出门问问进行定制。
动态生成方法可参考Android版SDK代码示例热词部分。

# 4. SDK中的外部音频识别接口是哪个？
类似startOneboxRecognizerWithQueue这样符合startXXXXRecognizerWithQueue格式的接口。需要把音频数据封装成一个BlockingQueue作为参数传入。

# 5. ASR的最长识别时间是多久?
目前在线识别是12秒超时，但使用Voice Input方式的话可以延长至30秒。

# 6. TTS能切换人声吗？TTS能够读出除了中文之外的语言吗？
目前能支持中文，英文和中英文混合，中文效果最好。暂不支持切换人声。

# 7. TTS的长度有限制吗？
中文单次输入最长500字。

# 8. 为什么集成之后说话没有任何反应
请检查onError回调中有没有任何错误码返回。如果没有的话，请联系我们。方便的话抓取一下log，发送至ai-developer@mobvoi.com, 谢谢。
