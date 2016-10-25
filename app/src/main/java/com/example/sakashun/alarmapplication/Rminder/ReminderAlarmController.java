package com.example.sakashun.alarmapplication.Rminder;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.example.sakashun.alarmapplication.Alarm.AlarmDataController;
import com.example.sakashun.alarmapplication.AlarmReceiver;

import java.util.Calendar;

/**
 * Created by Saka Shun on 2016/10/02.
 */
public class ReminderAlarmController {
    Context context;
    ReminderFileController reminderFileController = null;

    public ReminderAlarmController(Context c){
        context = c;
        reminderFileController = new ReminderFileController(c);
        reminderFileController.OpenFile();
    }
/*
    public boolean AlarmOneSet(int number) {
        //もし受けっとていなければ自分で開くファイルを開く
        alarmDataController.OpenFile();

        //アラームを識別するコード、任意なので重複しない好きな数値を設定
        //int REQUEST_CODE = 140625;

        //まず現在の時刻を取得する
        Calendar cal = Calendar.getInstance();  //オブジェクトの生成

        //アラーム用のカレンダーを用意する
        Calendar alarm_cal = Calendar.getInstance();  //オブジェクトの生成
        alarm_cal.set(Calendar.HOUR_OF_DAY,
                Integer.parseInt(alarmDataController.time[number].split(":")[0]));
        alarm_cal.set(Calendar.MINUTE,
                Integer.parseInt(alarmDataController.time[number].split(":")[1]));
        alarm_cal.set(Calendar.SECOND, 0);
        alarm_cal.set(Calendar.MILLISECOND, 0);

        if(cal.getTimeInMillis()>=alarm_cal.getTimeInMillis()){
            //現在の時間がアラーム時間より後の時、一日後にする
            alarm_cal.add(Calendar.DATE,1);
        }

        //設定された日時を表示
        System.out.println("アラームがセットされました↓");
        System.out.println(alarm_cal.get(Calendar.YEAR)+"/"+(alarm_cal.get(Calendar.MONTH)+1)+"/"+alarm_cal.get(Calendar.DATE));
        System.out.println(alarm_cal.get(Calendar.HOUR_OF_DAY)+":"+alarm_cal.get(Calendar.MINUTE));

        //指定の時間になったら起動するクラス
        Intent intent = new Intent(context,AlarmReceiver.class);
        //ntent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        //普通のintentと同じように、KEYとの組み合わせで値を受け渡しできるよ
        intent.putExtra("NUMBER",number);
        //ブロードキャストを投げるPendingIntentの作成
        PendingIntent sender = PendingIntent.getBroadcast(context, number, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        //AlramManager取得
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        //AlramManagerにPendingIntentを登録
        am.set(AlarmManager.RTC_WAKEUP, alarm_cal.getTimeInMillis(), sender);

        if(alarmDataController.OnoffCheced(number,true) && alarmDataController.onoff[number]){
            //アラームのonoffの保存場所ここでしかonのきろくをしない
            return true;
        }else{
            return false;
        }
    }

    public boolean AlarmOneCancel(int number) {
        System.out.println("アラームのキャンセル処理をします");

        //もし受けっとていなければ自分で開くファイルを開く
        alarmDataController.OpenFile();


        // 不要になった過去のアラームを削除する
        // requestCodeを0から登録していたとする
        Intent intent = new Intent(context,AlarmReceiver.class);
        //ブロードキャストを投げるPendingIntentの作成
        PendingIntent sender = PendingIntent.getBroadcast(context, number, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        //AlramManager取得
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        sender.cancel();
        am.cancel(sender);

        if(alarmDataController.OnoffCheced(number,false) && !alarmDataController.onoff[number]){
            //アラームのonoffの保存場所ここでしかoffのきろくをしない
            return true;
        }else{
            return false;
        }
    }
*/


}
