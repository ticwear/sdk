package com.mobvoi.ticwear.stepdemo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

public class StepActivity extends Activity {
    private static final Uri STEP_URI = Uri.parse("content://com.mobvoi.ticwear.steps");
    private ContentResolver mResolver;
    private int mSteps;
    private ContentObserver mObserver;
    private TextView mStepTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step);
        init();
    }

    private void init() {
        mStepTv = (TextView) findViewById(R.id.step_tv);

        mResolver = this.getContentResolver();
        mObserver = new ContentObserver(mHandler) {
            @Override
            public boolean deliverSelfNotifications() {
                return super.deliverSelfNotifications();
            }

            @Override
            public void onChange(boolean selfChange) {
                super.onChange(selfChange);
                mSteps = fetchSteps();
                Message.obtain(mHandler, mSteps).sendToTarget();
            }
        };
        mSteps = fetchSteps();
        mStepTv.setText(getString(R.string.step_count) + mSteps);
        registerContentObserver();
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mStepTv.setText(getString(R.string.step_count) + msg.what);
        }

    };

    private int fetchSteps() {
        int steps = 0;
        Cursor cursor = mResolver.query(STEP_URI, null, null, null, null);
        if (cursor != null) {
            try {
                if (cursor.moveToNext()) {
                    steps = cursor.getInt(0);
                }
            } finally {
                cursor.close();
            }
        }
        return steps;
    }

    private void registerContentObserver() {
        mResolver.registerContentObserver(STEP_URI, true, mObserver);
    }

    private void unregisterContentObserver() {
        mResolver.unregisterContentObserver(mObserver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterContentObserver();
    }

}
