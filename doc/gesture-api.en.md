## Gesture API

### Brief introduction

On the Ticwear system, user operations can be realized through a single-handed gesture. For example, users can wake up voice by turning their wrist twice, in notifications users can remove notification by turning their wrist twice, and in onebox cards you can flip up and down to switch cards.

These gesture APIs can also be used by third-party developers, for example, you can develop a music player, and have the function of turning wrist twice to switch the song.

For ease of administration, we have divided gestures into groups, before we only had one group `com.mobvoi.android.gesture.GestureType.GROUP_TURN_WRIST` (later package name omitted), here are three gestures: `GestureType.TYPE_TWICE_TURN_WRIST`; `GestureType.TYPE_TURN_WRIST_DOWN` and `GestureType .TYPE_TURN_WRIST_UP`.

### Use

First we need to get a MobvoiGestureClient instance, which can be obtained through a line of code.

``` Java
client = MobvoiGestureClient.getInstance(GestureType.GROUP_TURN_WRIST);
```

Then we need to create a callback, the code that will be called when the user makes a gesture:

``` Java
client.register(MainActivity.this, new MobvoiGestureClient.IGestureDetectedCallback() {
    @Override
    public void onGestureDetected(final int type) {
        Log.d("callback", "onGestureDetected type: " + type);
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                String s = "";
                if (type == GestureType.TYPE_TWICE_TURN_WRIST) {
                    s = "two palming";
                } else if (type == GestureType.TYPE_TURN_WRIST_UP) {
                    s = "Page Up";
                } else if (type == GestureType.TYPE_TURN_WRIST_DOWN) {
                    s = "Down";
                } else {
                    s = "unknown";
                }
                Toast.makeText(getApplicationContext(), "onGestureDetected " + s, Toast.LENGTH_SHORT).show();
            }});
     }});
```

Note: The callback method is not in the main thread, so if you need an operation ui please use  Handler or runOnUiThread. Additionally, you do not want a time-consuming operation in the callback function, if there is a long operation use a separate thread.

Note that this object is a one-off, and is no longer available after deletion.

When to call the above code? Generally, in our app running status we need to detect gestures, you can in `onResume()` method create and register an instance; use `onPause()` method to unregister:

``` Java
@Override
protected void onResume(){
    super.onResume();
    client = MobvoiGestureClient.getInstance(GestureType.GROUP_TURN_WRIST);
    client.register(MainActivity.this, new MobvoiGestureClient.IGestureDetectedCallback() {
        @Override
        public void onGestureDetected(final int type) {
        	//your codes here
        }
    });
}

@Override
protected void onPause(){
    super.onPause();
    client.unregister(this);
}
```
