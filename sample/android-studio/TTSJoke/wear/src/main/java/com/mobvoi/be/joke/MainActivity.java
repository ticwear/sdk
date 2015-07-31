package com.mobvoi.be.joke;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.widget.TextView;

import com.mobvoi.android.common.ConnectionResult;
import com.mobvoi.android.common.api.MobvoiApiClient;
import com.mobvoi.android.speech.synthesizer.SpeechSynthesizerApi;
import com.mobvoi.android.speech.synthesizer.SpeechSynthesizerCallbackInterface;
import com.mobvoi.android.wearable.MessageApi;
import com.mobvoi.android.wearable.MessageEvent;
import com.mobvoi.android.wearable.Wearable;

import java.io.InputStream;

/**
 * @author Yaguang Hu<huyaguang@mobvoi.com>
 * @date 2015-07-27
 */

public class MainActivity extends Activity implements MobvoiApiClient.ConnectionCallbacks,
        MobvoiApiClient.OnConnectionFailedListener, MessageApi.MessageListener {

    private static final String TAG = "WearMainActivity";
    private static final String TEXT_PATH = "/text";
    private static final String TEXT_CONTENT = "text_content";

    private MobvoiApiClient mMobvoiApiClient;

    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mMobvoiApiClient = new MobvoiApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mTextView = (TextView) stub.findViewById(R.id.text);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMobvoiApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mMobvoiApiClient.disconnect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Wearable.MessageApi.addListener(mMobvoiApiClient, this);
        Log.i(TAG, "data api connected");
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        String path = messageEvent.getPath();
        if (!TEXT_PATH.equals(path)) {
            return;
        }
        final String text = new String(messageEvent.getData());
        Log.i(TAG, "get text: " + text);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mTextView.setText(text);
            }
        });
        SpeechSynthesizerApi.startSynthesize(text, mTTSCallback);

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(TAG, "onConnectionFailed(): Failed to connect, with result: " + connectionResult);
    }

    private SpeechSynthesizerCallbackInterface mTTSCallback = new SpeechSynthesizerCallbackInterface() {

        @Override
        public void onSynthesizedAudioReceived(InputStream synthesizedAudio) {
            AudioManager mgr = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            mgr.setStreamVolume(AudioManager.STREAM_MUSIC, mgr.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);
            PlayerUtil.playAsMp3(synthesizedAudio);
        }

        @Override
        public void onSynthesizerError(int i, String s) {
            Log.i(TAG, "systhesizer error: " + s);
        }
    };
}
