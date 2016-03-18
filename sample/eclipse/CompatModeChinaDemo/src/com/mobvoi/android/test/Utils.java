//Copyright 2015 Mobvoi Inc. All Rights Reserved
package com.mobvoi.android.test;

import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class Utils {
    
    public static final String INTENT_TAG = "com.mobvoi.android.test.FunctionTestActivity";
    
    public static void setText(Context context, String key, String value) {
        Intent intent = new Intent(INTENT_TAG);
        Bundle bundle = new Bundle();
        bundle.putString(key, value);
        intent.putExtras(bundle);
        context.sendBroadcast(intent);
    }
    
    
    public static int readAll(InputStream in) {
        int code = 0;
        byte[] buffer = new byte[1024];
        while (true) {
            try {
                int len = in.read(buffer);
                for (int i = 0; i < len; i++) {
                    code += buffer[i];
                }
                if (len < 0) break;
            } catch (IOException e) {
                Log.e(FunctionTestActivity.TAG, e.getMessage(), e);
            }
        }
        return code;
    }
    
    public static byte[] getData(int len) {
        byte[] b = new byte[len];
        for (int i = 0; i < len; i++) {
            b[i] = (byte)(Math.random() * 256);
        }
        return b;
    }
    
    public static int getHashCode(byte[] data) {
        int code = 0;
        for (byte b : data) {
            code += b;
        }
        return code;
    }

}
