热词唤醒＋语音识别Demo
===============================

**完整示例代码如下：**
```cpp
// Copyright 2016 Mobvoi Inc. All Rights Reserved.

#include <assert.h>
#include <fstream>
#include <iostream>
#include <pthread.h>
#include <unistd.h>
#include <vector>

#include "speech_sdk.h"

pthread_mutex_t mutex;
pthread_cond_t cond;
std::ifstream test_file;

volatile bool in_the_session = true;

void show_usage() {
  std::cout << "usage: speech_sdk_demo audio_file_path" << std::endl;
}

void on_remote_silence_detected() {
  std::cout << "--------> dummy on_remote_silence_detected" << std::endl;
}

void on_final_transcription(const char* result) {
  std::cout << "--------> dummy on_final_transcription: " << result
            << std::endl;
}

void on_result(const char* result, const char* tts) {
  std::cout << "--------> dummy on_result: tts: " << tts << std::endl;
  pthread_mutex_lock(&mutex);
  in_the_session = false;
  pthread_cond_signal(&cond);
  pthread_mutex_unlock(&mutex);
}

void on_error(int error_code) {
  std::cout << "--------> dummy on_error with error code: " << error_code
            << std::endl;
  pthread_mutex_lock(&mutex);
  in_the_session = false;
  pthread_cond_signal(&cond);
  pthread_mutex_unlock(&mutex);
}

void on_local_silence_detected() {
  std::cout << "--------> dummy on_local_silence_detected" << std::endl;
  stop_recognizer();
}

void on_hotword_detected() {
  std::cout << "--------> dummy on_hotword_detected" << std::endl;
  stop_hotword();
  start_recognizer();
}

void* send_audio_thread(void* arg) {
  const int batch_size = 640;
  int pos = 0;
  test_file.seekg(0, test_file.end);
  int length = test_file.tellg() / 2;
  test_file.seekg(0, test_file.beg);
  short in_shorts[batch_size];

  if (test_file.is_open()) {
    while (pos < length) {
      int stride =
          (pos + batch_size < length) ? batch_size : (length - pos);
      test_file.read((char*) &in_shorts, stride * 2);
      send_speech_frame((char*)&in_shorts, stride * 2);
      pos += stride;
      usleep(20 * 1000);
    }
  } else {
    std::cout << "File could not be opened!" << std::endl;
  }
  test_file.close();
}

int main(int argc, const char* argv[]) {
  if (argc != 2) {
    show_usage();
    return 1;
  }

  test_file.open(argv[1]);
  // Read the audio file specified by commandline argument
  if (!test_file.is_open()) {
    std::cout << "Failed to open file " << argv[1] << std::endl;
    return 2;
  }

  // SDK initilalize including callback functions
  pthread_mutex_init(&mutex, NULL);
  pthread_cond_init(&cond, NULL);

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

  // Set the latitude and longitude for Haidian District
  set_location(39.98817674084273, 116.31168647658279);

  hotword_handler_vtable* hotword_handler = new hotword_handler_vtable;
  hotword_handler->on_hotword_detected = &on_hotword_detected;
  add_hotword_handler(hotword_handler);

  // Try hotword
  std::cout << "--------> start hotword" << std::endl;
  start_hotword();

  // Send the audio data in a separated thread.
  pthread_t tid;
  pthread_create(&tid, NULL, send_audio_thread, NULL);

  pthread_mutex_lock(&mutex);
  in_the_session = true;
  while (in_the_session) {
    pthread_cond_wait(&cond, &mutex);
  }
  pthread_mutex_unlock(&mutex);

  // Wait for our send data thread exit
  pthread_join(tid, NULL);

  // SDK Clean up
  std::cout << "start sdk cleanup" << std::endl;
  sdk_cleanup();
  std::cout << "end sdk cleanup" << std::endl;
  delete speech_handlers;
  std::cout << "end dummy sender" << std::endl;
  pthread_mutex_destroy(&mutex);
  pthread_cond_destroy(&cond);
  return 0;
}
```
