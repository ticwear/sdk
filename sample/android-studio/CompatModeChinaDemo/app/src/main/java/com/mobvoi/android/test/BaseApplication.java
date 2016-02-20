package com.mobvoi.android.test;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import com.mobvoi.android.common.MobvoiApiManager;
import com.mobvoi.android.common.NoAvailableServiceException;

public class BaseApplication extends Application {

    public static final String TAG = "FunctionTest";

    @Override
    public void onCreate() {
        super.onCreate();
        if (!MobvoiApiManager.getInstance().isInitialized()) {
            try {
                MobvoiApiManager.getInstance().adaptService(this);
            } catch (NoAvailableServiceException e) {
                Log.e(TAG, "no avaliable service.", e);
                Toast.makeText(this, "neither mms nor gms is avaliable.", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        Log.i(TAG, "Application created");
    }

}
