package com.mobvoi.android.test;

import java.io.InputStream;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.mobvoi.android.common.MobvoiApiManager;
import com.mobvoi.android.common.NoAvaliableServiceException;
import com.mobvoi.android.common.api.MobvoiApiClient;
import com.mobvoi.android.common.api.MobvoiApiClient.ConnectionCallbacks;
import com.mobvoi.android.common.api.ResultCallback;
import com.mobvoi.android.wearable.Asset;
import com.mobvoi.android.wearable.DataApi.DataItemResult;
import com.mobvoi.android.wearable.DataApi.GetFdForAssetResult;
import com.mobvoi.android.wearable.DataItemAsset;
import com.mobvoi.android.wearable.MessageApi.SendMessageResult;
import com.mobvoi.android.wearable.PutDataRequest;
import com.mobvoi.android.wearable.Wearable;

public class FunctionTestActivity extends Activity {
    
    public static final String TAG = "FunctionTest";
    
    private int type = 0;
    
    private TextView send, receive;
    
    private View button;
    
    private RadioGroup group;
    
    private MobvoiApiClient client;
    
    private boolean connected = false;
    
    private BroadcastReceiver receiver;
    
    private void initClient() {
        client = new MobvoiApiClient.Builder(this).addApi(Wearable.API)
                .addConnectionCallbacks(new ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle bundle) {
                        /*Wearable.MessageApi.addListener(client, new MessageListener() {
                            @Override
                            public void onMessageReceived(MessageEvent messageevent) {
                                if (messageevent != null && messageevent.getData() != null) {
                                    Utils.setText(FunctionTestActivity.this, "receive",
                                            "" + Utils.getHashCode(messageevent.getData()));
                                }
                            }
                        });
                        Wearable.DataApi.addListener(client, new DataListener() {
                            @Override
                            public void onDataChanged(DataEventBuffer buffer) {
                                if (buffer != null && buffer.getCount() > 0) {
                                    DataEvent e = buffer.get(0);
                                    if (e.getDataItem() != null) {
                                        DataItem item = e.getDataItem();
                                        if (item.getData() != null && item.getData().length > 0) {
                                            Utils.setText(FunctionTestActivity.this, "receive",
                                                    "" + Utils.getHashCode(item.getData()));
                                        } else if (item.getAssets().containsKey("key")) {
                                            DataItemAsset a = item.getAssets().get("key");
                                            Wearable.DataApi.getFdForAsset(client, a).setResultCallback(
                                                    new ResultCallback<DataApi.GetFdForAssetResult>() {
                                                @Override
                                                public void onResult(GetFdForAssetResult result) {
                                                    if (result.getStatus().isSuccess()) {
                                                        InputStream in = result.getInputStream();
                                                        Utils.setText(FunctionTestActivity.this, "receive",
                                                                "" + Utils.readAll(in));
                                                    }
                                                }
                                            });
                                        }
                                    }
                                }
                            }
                        });*/
                        connected = true;
                    }
                    @Override
                    public void onConnectionSuspended(int cause) {}
                }).build();
        client.connect();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_function_test);
        
        send = (TextView)findViewById(R.id.sendText);
        receive = (TextView)findViewById(R.id.receiveText);
        button = findViewById(R.id.startButton);
        group = (RadioGroup)findViewById(R.id.radioGroup1);
        
        if (!MobvoiApiManager.getInstance().isInitialized()) {
            try {
                MobvoiApiManager.getInstance().adaptService(this);
            } catch (NoAvaliableServiceException e) {
                Log.e(TAG, "no avaliable service.", e);
                Toast.makeText(this, "neither mms nor gms is avaliable.", Toast.LENGTH_SHORT).show();
                return ;
            }
        }
        initClient();
        Log.i(TAG, "init client finished.");
        
        Intent startIntent = new Intent(this, FunctionTestService.class);
        startService(startIntent);
        
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Bundle bundle = intent.getExtras();
                if (bundle.containsKey("send")) {
                    send.setText(bundle.getString("send"));
                } else if (bundle.containsKey("receive")) {
                    receive.setText(bundle.getString("receive"));
                }
            }
        };
        IntentFilter mFilter = new IntentFilter(Utils.INTENT_TAG);
        registerReceiver(receiver, mFilter);
        Log.i(TAG, "register receiver finished.");
        
        group.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radio0) type = 0;
                else if (checkedId == R.id.radio1) type = 1;
                else if (checkedId == R.id.radio2) type = 2;
            }
        });
        
        Log.i(TAG, "set radio button listener finished.");
        
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onclick, type = " + type);
                if (!connected) {
                    Log.i(TAG, "discard a request, connect : " + connected);
                    return ;
                }
                byte[] data = null;
                String hashCode = "";
                if (type == 0) {
                    data = Utils.getData(100);
                    hashCode = "" + Utils.getHashCode(data);
                    Utils.setText(FunctionTestActivity.this, "send", hashCode);
                    final String fh = hashCode;
                    Wearable.MessageApi.sendMessage(client, "1", "/function/message", data).setResultCallback(
                            new ResultCallback<SendMessageResult>() {
                        @Override
                        public void onResult(SendMessageResult result) {
                            if (result.getStatus().isSuccess()) {
                                Utils.setText(FunctionTestActivity.this, "send", "S:" + fh);
                            }
                        }
                    });
                    Log.i(TAG, "send a message.");
                } else if (type == 1) {
                    data = Utils.getData(1000);
                    hashCode = "" + Utils.getHashCode(data);
                    Utils.setText(FunctionTestActivity.this, "send", hashCode);
                    final String fh = hashCode;
                    PutDataRequest request = PutDataRequest.create("/function/data");
                    request.setData(data);
                    Wearable.DataApi.putDataItem(client, request).setResultCallback(
                            new ResultCallback<DataItemResult>() {
                        @Override
                        public void onResult(DataItemResult result) {
                            if (result.getStatus().isSuccess()) {
                                String h = "" + Utils.getHashCode(result.getDataItem().getData());
                                if (h.equals(fh)) {
                                    Utils.setText(FunctionTestActivity.this, "send", "S:" + h);
                                }
                            }
                        }
                    });;
                    Log.i(TAG, "put a data item.");
                } else if (type == 2) {
                    data = Utils.getData(10000);
                    hashCode = "" + Utils.getHashCode(data);
                    Utils.setText(FunctionTestActivity.this, "send", hashCode);
                    final String fh = hashCode;
                    PutDataRequest request = PutDataRequest.create("/function/asset");
                    Asset asset = Asset.createFromBytes(data);
                    request.putAsset("key", asset);
                    Wearable.DataApi.putDataItem(client, request).setResultCallback(
                            new ResultCallback<DataItemResult>() {
                        @Override
                        public void onResult(DataItemResult result) {
                            if (result.getStatus().isSuccess()) {
                                DataItemAsset a = result.getDataItem().getAssets().get("key");
                                Wearable.DataApi.getFdForAsset(client, a).setResultCallback(
                                        new ResultCallback<GetFdForAssetResult>() {
                                    @Override
                                    public void onResult(GetFdForAssetResult result) {
                                        if (result.getStatus().isSuccess()) {
                                            InputStream in = result.getInputStream();
                                            String f = "" + Utils.readAll(in);
                                            if (f.equals(fh)) {
                                                Utils.setText(FunctionTestActivity.this, "send", "S:" + f);
                                            }
                                        }
                                    }
                                });
                            }
                        }
                    });
                    Log.i(TAG, "put an asset.");
                }
                Log.i(TAG, "hashcode = " + hashCode);
            }
        });
        Log.i(TAG, "set button listener finished.");
    }

}
