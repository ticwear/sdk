package glance_demo.mobvoi.com.glancedemo;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.widget.RemoteViews;

import java.text.SimpleDateFormat;
import java.util.Date;


public class WidgetService extends Service implements  Handler.Callback{
    private boolean mIsStart = false;
    private static int DURATION = 1000;

    private Handler mHandler;

    @Override
    public boolean handleMessage(Message msg) {
        mHandler.sendEmptyMessageDelayed(0,DURATION);
        updateWidget();
        return true;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mIsStart = false;
        mHandler.removeMessages(0);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!mIsStart) {
            mIsStart = true;
            mHandler = new Handler(this);
            mHandler.sendEmptyMessage(0);

        }
        return super.onStartCommand(intent, flags, startId);
    }

    public void updateWidget() {
        Context context = getApplicationContext();
        AppWidgetManager mAppWidgetManager = AppWidgetManager.getInstance(context);
        RemoteViews remoteView = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
        Intent startActivityIntent = new Intent();
        startActivityIntent.setClass(context, MainActivity.class);
        startActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 101, startActivityIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        remoteView.setOnClickPendingIntent(R.id.root, pendingIntent);
        remoteView.setTextViewText(R.id.hello_word, getDate());

        final ComponentName mComponentName = new ComponentName(context, WidgetProvider.class);
        int[] appWidgetIds = mAppWidgetManager.getAppWidgetIds(mComponentName);
        mAppWidgetManager.updateAppWidget(appWidgetIds, remoteView);
    }

    private String getDate() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd\n HH:mm:ss");
        String date = df.format(new Date());
        return date;
    }

}
