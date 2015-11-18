package com.mobvoi.ttsdemo;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.mobvoi.android.speech.synthesizer.SpeechSynthesizerApi;
import com.mobvoi.android.speech.synthesizer.internal.DefaultSpeechSynthesizerCallback;

public class MainActivity extends ActionBarActivity {
    private Button button_play_on_phone;
    private Button button_stop_play_on_phone;
    private Button show_milliseconds_to_play;
    private TextView mTextView;
    private String text = "出门问问成立于2012年，" +
            "是目前市场上唯一一家拥有自主语音识别、语义分析、垂直搜索、语音合成技术的人工智能创业公司。" +
            "目前面向广大开发者免费开放语音合成技术，并提供语音识别和移动搜索等丰富接口，" +
            "致力于提供领先的移动语音搜索服务。";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTextView = (TextView)findViewById(R.id.text_view);
        button_play_on_phone = (Button)findViewById(R.id.button_play_on_phone);
        button_stop_play_on_phone = (Button)findViewById(R.id.button_stop_play_on_phone);
        show_milliseconds_to_play = (Button)findViewById(R.id.show_milliseconds_to_play);
        button_play_on_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTextView.setText("button_play_on_phone clicked.");
                SpeechSynthesizerApi.startSynthesizer(getApplicationContext(),
                        new DefaultSpeechSynthesizerCallback(), text,
                        5000);
            }
        });
        button_stop_play_on_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTextView.setText("button_stop_play_on_phone clicked");
                SpeechSynthesizerApi.stopSynthesizer(1);
            }
        });
        show_milliseconds_to_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTextView.setText("show_milliseconds_to_play clicked " +
                        SpeechSynthesizerApi.getMilliSecondsToPlay());
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
