package com.example.sakashun.alarmapplication.Rminder;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.example.sakashun.alarmapplication.Alarm.AlarmDataController;
import com.example.sakashun.alarmapplication.Rminder.TutiReceiver;
import com.example.sakashun.alarmapplication.IntentGetNumber;
import com.example.sakashun.alarmapplication.IntentPutNumber;

import java.util.Calendar;

/**
 * Created by Saka Shun on 2016/10/02.
 */
public class ReminderAlarmController {
    Context context;
    ReminderFileController reminderFileController = null;

    public ReminderAlarmController(Context c){
        //もし受けっとていなければ自分で開くファイルを開く
        context = c;
        reminderFileController = new ReminderFileController(c);
        reminderFileController.OpenFile();
    }

    public boolean AlarmOneSet(int number) {

        //アラームを識別するコード、任意なので重複しない好きな数値を設定
        //int REQUEST_CODE = 140625;

        //まず現在の時刻を取得する
        Calendar cal = Calendar.getInstance();  //オブジェクトの生成

        //アラーム用のカレンダーを用意する
        Calendar alarm_cal = Calendar.getInstance();  //オブジェクトの生成
        alarm_cal.set(
                Integer.parseInt(reminderFileController.hiduke[number].split("/")[0]),
                Integer.parseInt(reminderFileController.hiduke[number].split("/")[1])-1,
                Integer.parseInt(reminderFileController.hiduke[number].split("/")[2]),
                Integer.parseInt(reminderFileController.time[number].split(":")[0]),
                Integer.parseInt(reminderFileController.time[number].split(":")[1]),
                0);

        if(cal.getTimeInMillis() >= alarm_cal.getTimeInMillis()){
            //現在の時間がアラーム時間より後の時、セットしない
            System.out.println("リマインダーの設定時間が過去でした");
            return false;
        }

        //設定された日時を表示
        System.out.println("アラームがセットされました↓");
        System.out.println(alarm_cal.get(Calendar.YEAR)+"/"+(alarm_cal.get(Calendar.MONTH)+1)+"/"+alarm_cal.get(Calendar.DATE));
        System.out.println(alarm_cal.get(Calendar.HOUR_OF_DAY)+":"+alarm_cal.get(Calendar.MINUTE));

        //指定の時間になったら起動するクラス
        Intent intent = new Intent(context,TutiReceiver.class);
        //ntent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        //普通のintentと同じように、KEYとの組み合わせで値を受け渡しできるよ

        //個体番号をintentに記録する
        new IntentPutNumber(intent,number,reminderFileController);
        //固有番号に変える
        number = reminderFileController.my_number[number];

        //ブロードキャストを投げるPendingIntentの作成
        PendingIntent sender = PendingIntent.getBroadcast(context, number, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        //AlramManager取得
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        //AlramManagerにPendingIntentを登録
        am.set(AlarmManager.RTC_WAKEUP, alarm_cal.getTimeInMillis(), sender);
        return true;
    }

    public boolean AlarmOneCancel(int number) {
        System.out.println("アラームのキャンセル処理をします");

        // 不要になった過去のアラームを削除する
        // requestCodeを0から登録していたとする
        Intent intent = new Intent(context,TutiReceiver.class);

        //固有番号に変える
        number = reminderFileController.my_number[number];

        //ブロードキャストを投げるPendingIntentの作成
        PendingIntent sender = PendingIntent.getBroadcast(context, number, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        //AlramManager取得
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        sender.cancel();
        am.cancel(sender);

        return true;
    }

}
