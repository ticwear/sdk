package com.mobvoi.be.joke;

import android.content.IntentSender;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.mobvoi.android.common.ConnectionResult;
import com.mobvoi.android.common.api.MobvoiApiClient;
import com.mobvoi.android.speech.synthesizer.SpeechSynthesizerCallbackInterface;
import com.mobvoi.android.speech.synthesizer.SpeechSynthesizerApi;
import com.mobvoi.android.wearable.Wearable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * @author Yaguang Hu<huyaguang@mobvoi.com>
 * @date 2015-07-27
 */


public class MainActivity extends ActionBarActivity implements MobvoiApiClient.ConnectionCallbacks,
        MobvoiApiClient.OnConnectionFailedListener {

    private static final String TAG = "MobileMainActivity";
    private static final int REQUEST_RESOLVE_ERROR = 1000;
    private static final String TEXT_PATH = "/text";
    public static final String JOKE_URL = "http://m.mobvoi.com/search/qa/?query=%E7%AC%91%E8%AF%9D" +
            "&appkey=debug&address=&output=json&version=30000%EF%BC%86task=public.joke";

    private String SDPATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/TTS/";

    private boolean mResolvingError = false;
    private MobvoiApiClient mMobvoiApiClient;
    private BlockingQueue<String> jokeQueue = new ArrayBlockingQueue<String>(10, true);
    private TextView textView;
    private Button button1;
    private Button button2;
    private Button button3;
    private String curJoke;

    public static String getContent(String urlStr) {
        Log.i(TAG, "url: " + urlStr);
        StringBuffer sb = new StringBuffer();
        String line = null;
        BufferedReader buffer = null;
        HttpURLConnection urlConn = null;
        try {
            URL url = new URL(urlStr);
            urlConn = (HttpURLConnection) url.openConnection();
            InputStream inputStream = urlConn.getInputStream();
            buffer = new BufferedReader(new InputStreamReader(inputStream));
            while ((line = buffer.readLine()) != null) {
                sb.append(line);
            }
            inputStream.close();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    private void getJokes() {
        String content = getContent(JOKE_URL);
        try {
            JSONObject jb = new JSONObject(content);
            JSONArray ja = jb.getJSONArray("answer");
            for (int i = 0; i < ja.length(); ++i) {
                JSONArray jokeContentArray = ja.getJSONObject(i).getJSONArray("content");
                for (int j = 0; j < jokeContentArray.length(); ++j) {
                    jokeQueue.put(jokeContentArray.getString(j));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mMobvoiApiClient = new MobvoiApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        Log.i(TAG, "mobvoi api client start");
        textView = (TextView) findViewById(R.id.textView);
        button1 = (Button) findViewById(R.id.button);
        button2 = (Button) findViewById(R.id.button2);
        button3 = (Button) findViewById(R.id.button3);

        if (jokeQueue.isEmpty()) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) getJokes();
                }
            }).start();
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final String joke;
                try {
                    joke = jokeQueue.take();
                    textView.setText(joke);
                    curJoke = joke;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });


        button1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (jokeQueue.isEmpty()) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            getJokes();
                        }
                    }).start();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        final String joke = jokeQueue.poll();
                        textView.setText(joke);
                        curJoke = joke;
                    }
                });
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Wearable.MessageApi.sendMessage(mMobvoiApiClient, "other_node", TEXT_PATH, curJoke.getBytes());
            }
        });

        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SpeechSynthesizerApi.startSynthesize(curJoke, mTTSCallback);
            }
        });
    }

    @Override
    protected void onStop() {
        if (!mResolvingError) {
            mMobvoiApiClient.disconnect();
        }
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!mResolvingError) {
            mMobvoiApiClient.connect();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        mResolvingError = false;
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if (mResolvingError) {
            // Already attempting to resolve an error.
            return;
        } else if (result.hasResolution()) {
            try {
                mResolvingError = true;
                result.startResolutionForResult(this, REQUEST_RESOLVE_ERROR);
            } catch (IntentSender.SendIntentException e) {
                // There was an error with the resolution intent. Try again.
                mMobvoiApiClient.connect();
            }
        } else {
            mResolvingError = false;
        }
    }

    private SpeechSynthesizerCallbackInterface mTTSCallback = new SpeechSynthesizerCallbackInterface() {

        @Override
        public void onSynthesizedAudioReceived(InputStream synthesizedAudio) {
            Log.i(TAG, "receive call back");
            PlayerUtil.playAsMp3(synthesizedAudio);
        }

        @Override
        public void onSynthesizerError(int i, String s) {
            Log.i(TAG, "systhesizer error: " + s);
        }
    };
}
