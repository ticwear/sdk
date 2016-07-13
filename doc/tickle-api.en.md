## Tickle API

### Tickle API defines

``` java
// Long press event
public boolean onLongPressSidePanel (MotionEvent e) {// no need to concern parameters
    return false;
}

// Scroll event
public boolean onScrollSidePanel (MotionEvent e1, MotionEvent e2, float distanceX,
        float distanceY) {// distanceY: moving from the Y-axis direction, positive or negative number indicates the direction
    return false;
}

// After quick swipe lift
public boolean onFlingSidePanel (MotionEvent e1, MotionEvent e2, float velocityX,
        float velocityY) {// velocityY: Y-axis direction acceleration, negative number indicates a direction, opposite to the direction distanceY
    return false;
}

// Double click event
public boolean onDoubleTapSidePanel (MotionEvent e) {// no need to concern parameters
    return false;
}

// Click event
public boolean onSingleTapSidePanel (MotionEvent e) {// no need to concern parameters
    return false;
}
```

### Implementation Notes

System distribution and processing of Tickle event is similar to TouchEvent and KeyEvent. Specific process are as follows (in a scrolling example):

* First activity `onScrollSidePanel` method is called, if the return value is true, the distribution process will stop; if the return value is false, proceed to Step 2.

* Root View’s `onScrollSidePanel` is first called, if the return value is true, the distribution process will stop; if the return value is false, it continues to be distributed. If the current view is `ViewGroup`, traverse its sub-view. If the sub-view is visible, then the sub-view of `onScrollSidePanel` is called. If the return value is true, the distribution process will stop, otherwise continue to distribute, and so forth, until all of the View nodes are traversed.

### How to use

Activity and View are default implemented Tickle API methods. Therefore, developers only need their own App logic in the Activity View or rewrite Tickle API can (do not add Override). For example, in the Activity processing Tickle:

``` java
public class ScrollActivity extends Activity {
    public final static String TAG = "SidePanelDemo:Activity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scorll);
    }

    public boolean onLongPressSidePanel(MotionEvent e) {
        Log.d(TAG, "onLongPressSidePanel");
        return true;
    }

    public boolean onScrollSidePanel(MotionEvent e1, MotionEvent e2, float distanceX,
            float distanceY) {
        Log.d(TAG, "onScrollSidePanel " + distanceY);
        return true;
    }

    public boolean onFlingSidePanel(MotionEvent e1, MotionEvent e2, float velocityX,
            float velocityY) {
        Log.d(TAG, "onFlingSidePanel " + velocityY);
        return true;
    }

    public boolean onDoubleTapSidePanel(MotionEvent e) {
        Log.d(TAG, "onDoubleTapSidePanel");
        return true;
    }

    public boolean onSingleTapSidePanel(MotionEvent e) {
        Log.d(TAG, "onSingleTapSidePanel");
        return true;
    }
}
```

If you add proguard confusion, we need to add the following configuration to ensure Tickle API method is not confused:

``` java
-keepclassmembers class * {
    public boolean on*SidePanel(...);
}
```
