package com.mobvoi.be.joke;

import android.content.IntentSender;
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

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
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
    public static final List<String> JOKE_LIST = Arrays.asList(
            "蜈蚣被蛇咬了，为防毒液扩散必须截肢！蜈蚣想：幸亏偶腿多~！！ 大夫安慰道：兄弟，想开点，你以后就是蚯蚓了~ ",
            "一农户明天杀鸡，晚上喂鸡时说：快吃吧，这是你最后一顿！ 第二日见鸡已躺倒并留遗书：爷已吃老鼠药，你们也别想吃爷，爷他妈也不是好惹的~！",
            "鱼说：我时时刻刻睁开眼睛，就是为了能让你永远在我眼中~ 水说：我时时刻刻流淌不息，就是为了能永远把你拥抱~~ 锅说：都他妈快熟了，还这么贫！！",
            "跟我妈说了，我喜欢你，我要让你去我家，日日夜夜陪伴我，知道吗？通过这些日子的交往，我发现我已经不能没有你，可我妈不肯，她说：家里不准养猪！ ",
            "大象把粪便排在了路中央，一只蚂蚁正好路过，它抬头望了望那云雾缭 绕的顶峰，不禁感叹道：呀啦唆，这就是青藏高原~~",
            "你都老大不小了，有些事该让你知道了！天，是用来刮风的；地，是用 来长草的；我，是用来证明人类伟大的；你，就是用来炖粉条的~~");

    private boolean mResolvingError = false;
    private MobvoiApiClient mMobvoiApiClient;
    private BlockingQueue<String> jokeQueue = new ArrayBlockingQueue<String>(5, true);
    private TextView textView;
    private Button button1;
    private Button button2;
    private Button button3;
    private String curJoke;

    private void getJokes() {
        try {
            for (String jokeStr : JOKE_LIST) {
                jokeQueue.put(jokeStr);
            }
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
