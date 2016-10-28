package com.example.sakashun.alarmapplication;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

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

    private void setNotif(String titel,String content,int my_number,int type){
        //タイプ　0b0→通知のみ　0b1→音あり　010b→バイブあり
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(),R.mipmap.ic_launcher));
        builder.setContentTitle(titel);
        builder.setContentText(content);
        builder.setDefaults(type);
        builder.setStyle(new Notification.BigTextStyle()
                .bigText(content)
                .setBigContentTitle(titel));
        if(conntentIntent != null) {
            builder.setContentIntent(conntentIntent);
        }
        manager.notify(my_number,builder.build());
    }

    public void PushNotif_Intent(String titel,String content,int my_number,int type,Intent otificationIntent){
        //通知をクリックしたときのインテントをつくる
        conntentIntent = PendingIntent.getActivity(context,0,otificationIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        setNotif(titel,content,my_number,type);
    }

    public void PushNotif_noIntent(String titel,String content,int my_munber,int type){
        setNotif(titel,content,my_munber,type);
    }

    public void DeleteNotif(int number) {
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(number);
    }

}
