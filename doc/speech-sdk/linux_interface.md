## Linux版SDK接口
Linux版语音SDK提供了**C**语言接口绑定，本文详述接口规范

### 约定
 - 任意接口中若使用C风格字符串作为入参，如无明确大小指定，均为零结尾字符串
    ```c  
    void foo(char* str)               // 'str'是'\0'结尾字符串
    void foo(char* str, size_t size)  // 'str'在[0, size)内访问有效
    ```
 - 任意接口如无特殊说明，均非线程安全
    ```c
    // 若多线程访问，需使用额外同步措施，如pthread_mutex_lock
    void foo(void* arg)  
    ```
 - SDK中某功能调用方式统一采用**start_xx**和**stop_xx**形式，若该功能可接收返回信息，则会提供**cancel_xx**接口，**stop_xx**表示停止但仍有结果返回，**cancel_xx**表示中断且无结果返回
     ```c
    void start_foo()    // 启动'foo'
    void stop_foo()     // 关闭'foo'
    ```
    ```c
    void start_foo()    // 启动'foo'
    void stop_foo()     // 关闭'foo',但仍能接收之前发送的语音响应消息
    void cancel_foo()   // 取消'foo',功能中断,不会有结果返回
    ```
 
### 接口
#### SDK启动与关闭
- 方法  
    ```c
    /**
     * 对SDK进行初始化
     *
     * @return 成功返回0， 失败返回-1
     */
    int sdk_init();
    ```  
    ```c
    /**
     * 清理SDK使用资源
     */
    void sdk_cleanup();
    ```  
- 备注　
    > *sdk_init*在应用程序启动时只需调用一次，相应的，在应用程序退出时调用*sdk_cleanup*释放资源

#### 热词唤醒功能
- 方法  
    ```c
    /**
     * 开启热词唤醒
     *
     * @return 成功返回0， 失败返回-1
     */
    int start_hotword();
    ```  
    ```c
    /**
     * 关闭热词唤醒。
     *
     * @return 成功返回0， 失败返回-1
     */
    int stop_hotword();
    ```  
    ```c
    /**
     * 添加热词唤醒回调。
     *
     * @param handlers 热词唤醒回调
     */
    void add_hotword_handler(hotword_handler_vtable* handlers);
    ```  
    ```c
    /**
     * 去掉热词唤醒回调。
     *
     * @param handlers 热词唤醒回调
     */
    void remove_hotword_handler(hotword_handler_vtable* handlers);
    ```  

#### 语音识别功能
- 方法  
    ```c
    /**
     * 发送音频数据流到SDK
     *
     * @param frame 音频数据缓冲区头指针
     * @param size 缓冲区数据长度
     * @return 实际写入SDK的数据长度，失败返回-1
     */
    int send_speech_frame(const char * frame, int size);
    ```  
    ```
    /**
     * 设置语音识别回调。
     *
     * @param handlers 语音识别回调
     */
    void set_recognizer_handler(speech_client_handler_vtable* handlers);
    ```  
    ```c
    /**
     * 开始在线语音识别。
     *
     * @return 成功返回0， 失败返回-1
     */
    int start_recognizer();
    ```  
    ```c
    /**
     * 停止在线语音识别，之后会有结果返回。
     *
     * @return 成功返回0， 失败返回-1
     */
    int stop_recognizer();
    ```  
    ```c
    /**
     * 取消在线语音识别，无任何结果返回。
     *
     * @return 成功返回0， 失败返回-1
     */
    int cancel_recognizer();
    ```

#### 其他
- 方法
    ```cpp
    /**
     * 设置当前位置经纬度
     *
     * @param latitude 纬度
     * @param longitude 经度
     */
    void set_location(double latitude, double longitude);
    ```
    ```c
    /**
     * 设置log打印级别
     *
     * @param level [1, 3]，level越高打印越详细
     */
    void set_vlog_level(int level);
    ```
- 备注　
    > SDK中日志会打印到到标准输出，例如*stdout,stderr*

### 类型
- hotword_handler_vtable
    ```c
     typedef struct hotword_handler_vtable {
       void (*on_hotword_detected)();
     }hotword_handler_vtable;
    ```

- speech_client_handler_vtable
    ```c
    /**
      * 语音识别回调
      */
    typedef struct speech_client_handler_vtable {
       /**
        * 当云端检测到说话结束时回调
        */
       void (*on_remote_silence_detected)();

       /**
        * 最终识别结果. 例如： “今天的天气”是“今天的天气怎么样”这句话的部分识别结果
        * ，而“今天的天气怎么样”就是最终识别结果
        *
        * @param result 最终返回结果
        */
       void (*on_final_transcription)(const char * result);

      /**
       * 返回搜索结果
       * @param result 搜索结果，通常是一个json字符串
       * @param tts 在线tts播放url
       */
       void (*on_result)(const char * result, const char* tts);

       /**
        * 返回错误信息
        *
        * @param error_code The error code.
        */
       void (*on_error)(int error_code);

       /**
        * 当本地检测到说话结束时回调
        */
       void (*on_local_silence_detected)();
    };
    ```

### 错误码
|类型　　　　　 |值 | 定义         |
| --------------|---|--------------|
| NO_ERROR      | 0 | 没有错误     |
| SERVER_ERROR  | 1 | 服务器错误   |
| NETWORK_ERROR | 2 | 网络错误     |
| NO_SPEECH     | 3 | 本地没有说话 |
| INTERNAL_ERROR| 4 | SDK内部错误  |
>**注意**: 该错误码仅用于语音识别回调，即speech_client_handler_vtable中的
 void (*on_error)(int error_code);

