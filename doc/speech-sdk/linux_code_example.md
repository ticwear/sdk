热词唤醒＋语音识别Demo
===============================

**完整示例代码如下：**
```cpp
// Copyright 2016 Mobvoi Inc. All Rights Reserved.

#include <string>
#include <iostream>
#include "speech_sdk.h"

using std::cout;
using std::endl;

pthread_mutex_t mutex;
pthread_cond_t cond;
volatile bool in_the_session = true;

void on_remote_silence_detected() {
  cout << "--------> dummy on_remote_silence_detected" << endl;
}

void on_final_transcription(const char* result) {
  cout << "--------> dummy on_final_transcription: " << result << endl;
  pthread_mutex_lock(&mutex);
  in_the_session = false;
  pthread_cond_signal(&cond);
  pthread_mutex_unlock(&mutex);
}

void on_result(const char* result, const char* tts) {
  cout << "--------> dummy on_result: tts: " << tts << endl;
}

void on_error(int error_code) {
  cout << "--------> dummy on_error with error code: " << error_code << endl;
  pthread_mutex_lock(&mutex);
  in_the_session = false;
  pthread_cond_signal(&cond);
  pthread_mutex_unlock(&mutex);
}

void on_local_silence_detected() {
  cout << "--------> dummy on_local_silence_detected" << endl;
  stop_recognizer();
}

void on_hotword_detected() {
  cout << "--------> dummy on_hotword_detected" << endl;
  stop_hotword();
  start_recognizer();
}

int main(int argc, char** argv) {
  // Initialize mutex and condition variable objects
  pthread_mutex_init(&mutex, NULL);
  pthread_cond_init (&cond, NULL);
  sdk_init();
  speech_client_handler_vtable* speech_handlers =
      new speech_client_handler_vtable;
  assert(speech_handlers != NULL);
  speech_handlers->on_error = &on_error;
  speech_handlers->on_final_transcription = &on_final_transcription;
  speech_handlers->on_local_silence_detected = &on_local_silence_detected;
  speech_handlers->on_remote_silence_detected = &on_remote_silence_detected;
  speech_handlers->on_result = &on_result;
  set_recognizer_handler(speech_handlers);

  // the latitude and longitude
  set_location(39.98817674084273, 116.31168647658279);

  hotword_handler_vtable* hotword_handler = new hotword_handler_vtable;
  hotword_handler->on_hotword_detected = &on_hotword_detected;
  add_hotword_handler(hotword_handler);

  // Try 10 times hotword + recognition
  int num = 10;
  while (num--) {
    cout << "--------> start hotword" << endl;
    start_hotword();
    cout << "ready to streaming feed data" << endl;
    pthread_mutex_lock(&mutex);
    while (in_the_session) {
      pthread_cond_wait(&cond,&mutex);
    }
    pthread_mutex_unlock(&mutex);
  }

  cout << "start sdk cleanup";
  sdk_cleanup();
  cout << "end sdk cleanup";
  delete speech_handlers;
  cout << "end dummy sender";
  pthread_mutex_destroy(&mutex);
  pthread_cond_destroy(&cond);
  return 0;
}
```
