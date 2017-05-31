热词唤醒＋语音识别Demo
===============================

**完整示例代码如下：**
```cpp
// Copyright 2016 Mobvoi Inc. All Rights Reserved.

#include <iostream>
#include <fstream>
#include <vector>

#include <assert.h>
#include <pthread.h>
#include <unistd.h>
#include "speech_sdk.h"

pthread_mutex_t mutex;
pthread_cond_t cond;
volatile bool in_the_session = true;
std::vector<char> frame_data;

void show_usage() {
  std::cout << "usage: speech_sdk_demo audio_file_path" << std::endl; 
}

bool read_file_bytes(const char* file_path, std::vector<char>* out) {
  out->clear();
  std::ifstream inf(file_path, std::ios::binary|std::ios::ate);
  if (!inf) return false;

  int file_size = inf.tellg();
  out->resize(file_size);

  inf.seekg(0, std::ios::beg);
  inf.read(&(out->front()), file_size);

  std::cout << "--------> read " << out->size() << " bytes from input audio file:" << file_path << std::endl;
  return true;
}

void on_remote_silence_detected() {
  std::cout << "--------> dummy on_remote_silence_detected" << std::endl;
}

void on_final_transcription(const char* result) {
  std::cout << "--------> dummy on_final_transcription: " << result << std::endl;
}

void on_result(const char* result, const char* tts) {
  std::cout << "--------> dummy on_result: tts: " << tts << std::endl;
  pthread_mutex_lock(&mutex);
  in_the_session = false;
  pthread_cond_signal(&cond);
  pthread_mutex_unlock(&mutex);
}

void on_error(int error_code) {
  std::cout << "--------> dummy on_error with error code: " << error_code << std::endl;
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
  while (in_the_session) {
    send_speech_frame(&frame_data[0], frame_data.size());
    usleep(1000 * 200);
  }
}

int main(int argc, const char* argv[]) {
  if (argc != 2) {
    show_usage();
    return 1;
  }

  // Read the audio file specified by commandline argument
  if (!read_file_bytes(argv[1], &frame_data)) {
    std::cout << "Failed to read bytes from file " << argv[1] << std::endl;
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
  pthread_mutex_lock(&mutex);

  // Send the audio data in a separated thread.
  std::cout << "ready to streaming feed data" << std::endl;
  pthread_t tid;
  pthread_create(&tid, NULL, send_audio_thread, NULL);

  // Wait for finish
  while (in_the_session) {
    pthread_cond_wait(&cond, &mutex);
  }
  pthread_mutex_unlock(&mutex);

  // SDK Clean up
  std::cout << "start sdk cleanup";
  sdk_cleanup();
  std::cout << "end sdk cleanup";
  delete speech_handlers;
  std::cout << "end dummy sender";
  pthread_mutex_destroy(&mutex);
  pthread_cond_destroy(&cond);
  return 0;
}
```
