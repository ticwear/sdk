## Speech Recognition API

For details, refer to Github inside Ticwear SDK open source projects [speech recognition sample program][speech-example].

### Brief introduction

Ticwear provides two APIs: voice query and speech input. Developers can easily access the speech recognition engine, activate voice recognition and use voice to text in applications. Voice query API provides daily information query terms to text function, and speech input API provides common functions like voice input input method. The difference between voice query API and speech input API is that speech input API has a customized message content class, longer voice detection end time limit, and automatically adds punctuation. For example, when the user is silent for one second then a comma is automatically added, 5 seconds of silence is considered the end and a period is added at the end.

### Voice query API

You only need to write an ｀Activity｀, inherit the abstract class `SpeechRecognitionApi.SpeechRecogActivity`, and call `startRecognition` inside the ｀Activity｀ to initiate speech recognition. Then follow the prompts to complete the two callback functions ｀onRecognitionSuccess(String result)｀ and ｀onRecognitionFailed()｀, to receive speech recognition result.

Sample code:

``` Java
import com.mobvoi.android.speech.SpeechRecognitionApi;

public class SpeechRecogTestActivity extends
    SpeechRecognitionApi.SpeechRecogActivity {
  private static final String TAG = "SpeechRecogTest";
  private TextView mTextView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_speech_recog);
  }

  public void btnOnClick(View view) {
    // Press the button to start voice recognition
    startRecognition();
  }

  @Override
  public void onRecognitionSuccess(String speechRslt) {
    // Get the speech recognition result
    Log.d(TAG, "Get speech recognition result: " + 
        speechRslt);
    TextView txtRslt = (TextView)
        findViewById(R.id.txtMain);
    txtRslt.setText(speechRslt);
  }

  @Override
  public void onRecognitionFailed() {
    // When calling the speech recognition fails
    Log.e(TAG, "Speech recognition failed");
  }
}
```

### Speech input API

You only need to write an `Activity`, inherit the abstract class `SpeechRecognitionApi.SpeechRecogActivity`, and call `startVoiceInput` inside the `Activity` to initiate speech recognition. Then follow the prompts to complete the two callback functions `onRecognitionSuccess(String result)` and `onRecognitionFailed()`, to receiving speech recognition result.

Sample code:

``` Java
package com.mobvoi.ticwear;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.mobvoi.android.speech.SpeechRecognitionApi;

public class VoiceInputDemoActivity extends
SpeechRecognitionApi.SpeechRecogActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.dication_demo_activity);
    findViewById(R.id.test_button).setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        startVoiceInput();
      }
    });
  }

  @Override
  public void onRecognitionSuccess(String text) {
    TextView txtRslt = (TextView) findViewById(R.id.speak_tip);
    txtRslt.setText(text);
  }

  @Override
  public void onRecognitionFailed() {
    TextView txtRslt = (TextView) findViewById(R.id.speak_tip);
    txtRslt.setText("onRecognitionFailed");
  }
}
```

[speech-example]: https://github.com/ticwear/sdk/tree/master/sample/eclipse/VoiceInputWearable
