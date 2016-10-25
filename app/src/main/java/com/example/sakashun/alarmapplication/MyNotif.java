package com.example.sakashun.alarmapplication;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Saka Shun on 2016/10/26.
 */
public class MyNotif {
    PendingIntent conntentIntent=null;
    Notification.Builder builder;
    Context context=null;
    NotificationManager manager;
    public MyNotif(Context c){
        context = c;
        //実際の通知を作るためのBuilderを作成
        builder = new Notification.Builder(c);
        //実際に通知を発行するmanagerを作成
        manager= (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    private void setNotif(String titel,String content){
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle(titel);
        builder.setContentText(content);
        builder.setDefaults(Notification.PRIORITY_DEFAULT);
        if(conntentIntent != null) {
            builder.setContentIntent(conntentIntent);
        }
    }

    public void PushNotif_Activity(String titel,String content,Intent otificationIntent){
        //通知をクリックしたときのインテントをつくる
        conntentIntent = PendingIntent.getActivity(context,0,otificationIntent,0);
        setNotif(titel,content);
        manager.notify(1,builder.build());
    }

    public void PushNotif_noActivity(String titel,String content){
        setNotif(titel,content);
    }

}
