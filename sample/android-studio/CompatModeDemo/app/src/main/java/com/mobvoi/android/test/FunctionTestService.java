//Copyright 2015 Mobvoi Inc. All Rights Reserved
package com.mobvoi.android.test;

import com.mobvoi.android.common.api.MobvoiApiClient;
import com.mobvoi.android.common.api.ResultCallback;
import com.mobvoi.android.wearable.DataApi;
import com.mobvoi.android.wearable.DataApi.GetFdForAssetResult;
import com.mobvoi.android.wearable.DataEvent;
import com.mobvoi.android.wearable.DataEventBuffer;
import com.mobvoi.android.wearable.DataItem;
import com.mobvoi.android.wearable.DataItemAsset;
import com.mobvoi.android.wearable.MessageEvent;
import com.mobvoi.android.wearable.Wearable;
import com.mobvoi.android.wearable.WearableListenerService;

import java.io.InputStream;

public class FunctionTestService extends WearableListenerService {
    
    private MobvoiApiClient client;
    
    @Override
    public void onCreate() {
        super.onCreate();
        client = new MobvoiApiClient.Builder(this).addApi(Wearable.API).build();
        client.connect();
    }
    
    @Override
    public void onDataChanged(DataEventBuffer buffer) {
        if (buffer != null && buffer.getCount() > 0) {
            DataEvent e = buffer.get(0);
            if (e.getDataItem() != null) {
                DataItem item = e.getDataItem();
                if (item.getData() != null && item.getData().length > 0) {
                    Utils.setText(this, "receive",
                            "" + Utils.getHashCode(item.getData()));
                } else if (item.getAssets().containsKey("key")) {
                    DataItemAsset a = item.getAssets().get("key");
                    Wearable.DataApi.getFdForAsset(client, a).setResultCallback(
                            new ResultCallback<DataApi.GetFdForAssetResult>() {
                        @Override
                        public void onResult(GetFdForAssetResult result) {
                            if (result.getStatus().isSuccess()) {
                                InputStream in = result.getInputStream();
                                Utils.setText(FunctionTestService.this, "receive",
                                        "" + Utils.readAll(in));
                            }
                        }
                    });
                }
            }
        }
    }

    @Override
    public void onMessageReceived(MessageEvent messageevent) {
        if (messageevent != null && messageevent.getData() != null) {
            Utils.setText(this, "receive", "" + Utils.getHashCode(messageevent.getData()));
        }
    }
    
}
