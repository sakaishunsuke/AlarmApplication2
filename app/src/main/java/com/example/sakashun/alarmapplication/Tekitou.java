package com.example.sakashun.alarmapplication;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.Calendar;

public class Tekitou extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button alarm_button = (Button) findViewById(R.id.alarm_button);
        Button calender_button= (Button) findViewById(R.id.calendar_button);
        Button config_button = (Button) findViewById(R.id.application_config_button);

        //アラーム画面へのボタン
        alarm_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public  void onClick(View v){
                //deleteFile("alarm_list_data.txt");
                Intent intent = new Intent(getApplication()
                        ,com.example.sakashun.alarmapplication.AlarmConfig.class);
                startActivity(intent);

            }
        });

        //カレンダー画面へのボタン
        calender_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public  void onClick(View v){
                //アラームを識別するコード、任意なので重複しない好きな数値を設定
                int REQUEST_CODE = 140625;

                //通知を鳴らしたい時間をセットする
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(System.currentTimeMillis());
                //cal.set(2016, 9, 8, 1, 38, 0);  //年、月、日、時、分、秒
                //cal.set(Calendar.MINUTE,50);    //ミリ秒

                //ミリ秒で通知時間を設定する
                System.out.println("REQUEST_CODE" + REQUEST_CODE + " -> init_alarm:" +cal.getTimeInMillis());
                long init_alarm = cal.getTimeInMillis()+5000;
                System.out.println("REQUEST_CODE" + REQUEST_CODE + " -> init_alarm:" + init_alarm);

                //指定の時間になったら起動するクラス
                Intent intent = new Intent(Tekitou.this,AlarmReceiver.class);
                //ntent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                //普通のintentと同じように、KEYとの組み合わせで値を受け渡しできるよ
                //intent.putExtra("KEY", 123);
                //ブロードキャストを投げるPendingIntentの作成
                PendingIntent sender = PendingIntent.getBroadcast(Tekitou.this, REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                //AlramManager取得
                AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                //AlramManagerにPendingIntentを登録
                am.set(AlarmManager.RTC_WAKEUP, init_alarm, sender);
            }
        });

        //アプリ設定画面へのボタン
        config_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public  void onClick(View v){

            }
        });

    }
}
