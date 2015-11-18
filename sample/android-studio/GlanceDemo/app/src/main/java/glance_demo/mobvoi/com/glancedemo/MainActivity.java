package glance_demo.mobvoi.com.glancedemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;


public class MainActivity extends Activity implements  Handler.Callback{
    Handler mHandler;
    private static int DURATION = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mHandler = new Handler(this);
        mHandler.sendEmptyMessage(0);
    }

    private String getDate() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd\n HH:mm:ss");
        String date = df.format(new Date());
        return date;
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateTime();
        Intent sensorServiceIntent = new Intent(this, WidgetService.class);
        startService(sensorServiceIntent);
    }

    private void updateTime() {
        TextView txt = (TextView)findViewById(R.id.text);
        txt.setText(getDate());
    }

    @Override
    public boolean handleMessage(Message msg) {
        mHandler.sendEmptyMessageDelayed(0, DURATION);
        updateTime();
        return true;
    }


}