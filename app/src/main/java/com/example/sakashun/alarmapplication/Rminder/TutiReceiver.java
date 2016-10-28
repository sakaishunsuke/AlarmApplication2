package com.example.sakashun.alarmapplication.Rminder;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.sakashun.alarmapplication.Alarm.AlarmDataController;
import com.example.sakashun.alarmapplication.IntentGetNumber;
import com.example.sakashun.alarmapplication.MyNotif;

/**
 * Created by Saka Shun on 2016/09/08.
 */
public class TutiReceiver extends BroadcastReceiver {
    private static final int SOUND_FLAG= Notification.DEFAULT_SOUND;//通知の音flag
    private static final int VIBRATE_FLAG= Notification.DEFAULT_VIBRATE;//バイブのflag
    private static final int LED_FLAG= Notification.DEFAULT_LIGHTS;//LEDのflag

    // notifications
    @Override
    public void onReceive(Context context, Intent data) {
        ReminderFileController r = new ReminderFileController(context);
        int number = new IntentGetNumber(data,r).GetNumber();
        System.out.println("受け取り番号"+number+"  "+r);
        int level = Integer.parseInt(r.level[number].split("レベル")[1]);
        if( new Pref().GetBoolean(context, ("level_" + level), "tuti") ){
            /// /通知がonだったら通知を作る
            int type = 0;
            if(new Pref().GetBoolean(context, ("level_" + level), "music") ){
                //音あり
                type = type | SOUND_FLAG;
            }
            if(new Pref().GetBoolean(context, ("level_" + level), "vibration")){
                //バイブあり
                type = type | VIBRATE_FLAG;
            }
            if(new Pref().GetBoolean(context, ("level_" + level), "led")){
                //バイブあり
                type = type | LED_FLAG;
            }
            MyNotif myNotif = new MyNotif(context);
            myNotif.PushNotif_noIntent(r.name[number],r.memo_content[number],r.my_number[number],type);
        }else{
            //通知がoff設定だったらおしまい（何もしない）
            return;
        }
    }
}