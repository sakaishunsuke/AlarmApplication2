package com.example.sakashun.alarmapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

/**
 * Created by Saka Shun on 2016/10/04.
 */
public class WidgetIntentReceiver extends BroadcastReceiver {
    public static int clickCount = 0;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("UPDATE_WIDGET")) {
            System.out.println("反応してますよ！");
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.alarm_widget);

            // テキストをクリック回数を元に更新
            remoteViews.setTextViewText(R.id.title, "クリック回数: " + WidgetIntentReceiver.clickCount);

            // もう一回クリックイベントを登録(毎回登録しないと上手く動かず)
            remoteViews.setOnClickPendingIntent(R.id.button, Widget.clickButton(context));

            Widget.pushWidgetUpdate(context.getApplicationContext(), remoteViews);
        }
    }
}