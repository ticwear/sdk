## Health API

For details, refer to the Github inside Ticwear SDK open source projects such as [Eclipse-based pedometer weather and sample programs][health-example].

### Brief introduction

Health Data API is the Ticwear health data interface, providing current cumulative number of steps and walking distance to facilitate developer access to these data to develop more diversified applications.
The built-in brightly colored pedometer dial is the interface used currently by the health data. If the developer directly uses the pedometer sensor to obtain data, you need to deal with a lot of business logic.

### Steps

Health data is provided by a Content Provider, URI is `com.mobvoi.ticwear.steps`. Developers can define a `ContentResolver` (content resolver), real-time data query method. The query interface only needs the URI parameter to check the days cumulative number of steps and walking distance. When health data is updated the monitor will inform the content of this URI observer. If developers need to refresh the data, they can define a `ContentObserver` (content viewer) to observe changes in listening content data. When health data is updated, you can use `ContentResolver` in `onChange` callback to get the latest data.

After the data changes in order to update the UI, you can define a Handler passed as a parameter to ContentObserver, and update the UI by callback Handler's handleMessage.

Code Example:

``` Java
// Access to health data ContentProvider
private static final Uri STEP_URI = Uri.parse("content://com.mobvoi.ticwear.steps");
// Define the content resolver access to health data
private ContentResolver mResolver = this.getContentResolver();
private int fetchSteps() {
    int steps = 0;
    int distance = 0;
    // Query interface only receives URI, does not receive its parameters, it can be set to null.
    Cursor cursor = mResolver.query(STEP_URI, null, null, null, null);
    if (cursor != null) {
        try {
            if (cursor.moveToNext()) {
                steps = cursor.getInt(0);
                // Unit distance in meters
                distance = cursor.getInt(1);
            }
        } finally {
            cursor.close();
        }
    }
    return steps;
}

// Define the content viewer to monitor pedometer data changes
ContentObserver mObserver = new ContentObserver(mHandler) {
        @Override
        public boolean deliverSelfNotifications() {
            return super.deliverSelfNotifications();
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            int steps = fetchSteps();
            Message.obtain(mHandler, steps).sendToTarget();
        }
    };

// Define Handler, update the UI when data changes
private Handler mHandler = new Handler() {

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        mStepTv.setText(getString(R.string.step_count) + msg.what);
    }

};

// Registration and cancellation of content viewer is recommended() registeration onCreate, cancellation onDestroy() 
private void registerContentObserver() {
    mResolver.registerContentObserver(STEP_URI, true, mObserver);
}

private void unregisterContentObserver() {
    mResolver.unregisterContentObserver(mObserver);
}

```

[health-example]: https://github.com/ticwear/sdk/tree/master/sample/eclipse/StepAndWeatherDemo
