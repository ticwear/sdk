# 初始化
```cpp
/**
 * 对SDK进行初始化。
 *
 * @return 成功返回0， 失败返回-1
 */

int sdk_init();

/**
 * 清理SDK使用资源。
 */
void sdk_cleanup();
```
# 热词唤醒
```cpp
/**
 * 热词唤醒回调。
 */
struct hotword_handler_vtable {
  void (*on_hotword_detected)();
};

/**
 * 开启热词唤醒。
 *
 * @return 成功返回0， 失败返回-1
 */
int start_hotword();

/**
 * 关闭热词唤醒。
 *
 * @return 成功返回0， 失败返回-1
 */
int stop_hotword();

/**
 * 添加热词唤醒回调。
 *
 * @param handlers 热词唤醒回调
 */
void add_hotword_handler(struct hotword_handler_vtable* handlers);

/**
 * 去掉热词唤醒回调。
 *
 * @param handlers 热词唤醒回调
 */
void remove_hotword_handler(struct hotword_handler_vtable* handlers);
```
# 语音识别
```cpp
/**
 * 语音识别回调。
 */
struct speech_client_handler_vtable {
  /**
   * 当云端检测到说话结束时回调。
   */
  void (*on_remote_silence_detected)();

  /**
   * 最终识别结果. 例如： “今天的天气”是“今天的天气怎么样”这句话的部分识别结果
   * ，而“今天的天气怎么样”就是最终识别结果。
   *
   * @param result 最终返回结果
   */
  void (*on_final_transcription)(const char * result);

 /**
  * 返回搜索结果。
  * @param result 搜索结果，通常是一个json字符串
  * @param tts 在线tts播放url
  */
  void (*on_result)(const char * result, const char* tts);

  /**
   * 返回错误信息。
   *
   * @param error_code The error code.
   */
  void (*on_error)(int error_code);

  /**
   * 当本地检测到说话结束时回调。
   */
  void (*on_local_silence_detected)();
};

/**
 * 发送音频数据流到SDK。
 *
 * @param frame 音频数据缓冲区头指针
 * @param size 缓冲区数据长度
 * @return 实际写入SDK的数据长度，失败返回-1
 */
int send_speech_frame(char * frame, int size);

/**
 * 设置语音识别回调。
 *
 * @param handlers 语音识别回调
 */
void set_recognizer_handler(struct speech_client_handler_vtable* handlers);

/**
 * 开始在线语音识别。
 *
 * @return 成功返回0， 失败返回-1
 */
int start_recognizer();

/**
 * 停止在线语音识别，之后会有结果返回。
 *
 * @return 成功返回0， 失败返回-1
 */
int stop_recognizer();

/**
 * 取消在线语音识别，无任何结果返回。
 *
 * @return 成功返回0， 失败返回-1
 */
int cancel_recognizer();
```
# 其他
```cpp
typedef struct {
  double latitude;
  double longitude;
} simple_location;

/**
 * 设置当前位置经纬度。
 *
 * @param latitude 纬度
 * @param longitude 经度
 */
void set_location(double latitude, double longitude);

/**
 * 设置log打印级别。
 *
 * @param level [1, 3]，level越高打印越详细
 */
void set_vlog_level(int level);
```
# 错误码
```cpp
enum error_code {
  /**
   * 没有错误
   */
  NO_ERROR = 0,
  /**
   * 服务器错误
   */
  SERVER_ERROR = 1,
  /**
   * 网络错误
   */
  NETWORK_ERROR = 2,
  /**
   * 本地没有说话
   */
  NO_SPEECH = 3,
  /**
   * SDK内部错误
   */
  INTERNAL_ERROR = 4,
};
```
