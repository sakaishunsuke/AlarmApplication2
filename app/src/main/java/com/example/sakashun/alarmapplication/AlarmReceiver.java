package com.example.sakashun.alarmapplication;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

/**
 * Created by Saka Shun on 2016/09/08.
 */
public class AlarmReceiver extends BroadcastReceiver {
    // notifications
    @Override
    public void onReceive(Context context, Intent data) {

        System.out.println("torimaok");
        //アクションを受け取るメソッド
        Intent intent = new Intent(context
                , com.example.sakashun.alarmapplication.AlarmNotification.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("ALARM_FLAG", data.getIntExtra("ALARM_FLAG", 0));

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        try {
            pendingIntent.send();
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }
    }
}