package com.mobvoi.ticwear;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.mobvoi.android.speech.SpeechRecognitionApi;


public class VoiceInputDemoActivity  extends SpeechRecognitionApi.SpeechRecogActivity {

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
