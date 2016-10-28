package com.example.sakashun.alarmapplication.Alarm;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import com.example.sakashun.alarmapplication.AlarmNotification;
import com.example.sakashun.alarmapplication.MyNotif;
import com.example.sakashun.alarmapplication.R;

public class LockScreenAlarmSet extends Activity{
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        this.getApplicationContext().registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                System.out.println("スリープ解除");
                Intent i = new Intent(getApplicationContext(),LockScreenAlarmSetActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
                MyNotif myNotif = new MyNotif(getApplicationContext());
                myNotif.PushNotif_Intent("アラーム設定","押すとアラームがセットできます",334,0,i);
            }
        }, filter);

        filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        this.getApplicationContext().registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                MyNotif myNotif = new MyNotif(context);
                myNotif.DeleteNotif(334);
                System.out.println("スリープ");
            }
        }, filter);

        finish();
    }
}