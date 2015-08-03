package glance_demo.mobvoi.com.glancedemo;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import java.text.SimpleDateFormat;
import java.util.Date;

public class WidgetProvider extends AppWidgetProvider {
    private static final String TAG = "WidgetProvider";

    public WidgetProvider() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        android.util.Log.d(TAG, " onReceive " + intent.getAction());
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        updateWidget(context, appWidgetManager);
        android.util.Log.d(TAG, " onUpdate ");

    }

    private void refreshText(Context context, RemoteViews remoteView) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd\n HH:mm:ss");
        String date = df.format(new Date());
        remoteView.setTextViewText(R.id.hello_word, date);
    }



    public void updateWidget(Context context, AppWidgetManager mAppWidgetManager) {
        RemoteViews remoteView = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
        Intent startActivityIntent = new Intent();
        startActivityIntent.setClass(context, MainActivity.class);
        startActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 101, startActivityIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        remoteView.setOnClickPendingIntent(R.id.root, pendingIntent);
        refreshText(context, remoteView);
        final ComponentName mComponentName = new ComponentName(context, WidgetProvider.class);
        int[] appWidgetIds = mAppWidgetManager.getAppWidgetIds(mComponentName);
        mAppWidgetManager.updateAppWidget(appWidgetIds, remoteView);
    }

}
