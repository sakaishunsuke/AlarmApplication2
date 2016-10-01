package com.example.sakashun.alarmapplication;

import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import java.util.Random;

/**
 * Created by Saka Shun on 2016/09/08.
 */
public class AlarmNotification extends Activity {
    private PowerManager.WakeLock wakelock;
    private KeyguardManager.KeyguardLock keylock;

    MediaPlayer mp = new MediaPlayer();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarm_screen);
        Log.v("通知ログ", "create");
        // スリープ状態から復帰する
        wakelock = ((PowerManager) getSystemService(Context.POWER_SERVICE))
                .newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK
                        | PowerManager.ACQUIRE_CAUSES_WAKEUP
                        | PowerManager.ON_AFTER_RELEASE, "disableLock");
        wakelock.acquire();

        // スクリーンロックを解除する
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        KeyguardManager keyguard = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        keylock = keyguard.newKeyguardLock("disableLock");
        keylock.disableKeyguard();


        //button設定
        Button alarm_stop_button[] = new Button[4];
        alarm_stop_button[0]= (Button) findViewById(R.id.alarm_stop_button1);
        alarm_stop_button[1]= (Button) findViewById(R.id.alarm_stop_button2);
        alarm_stop_button[2]= (Button) findViewById(R.id.alarm_stop_button3);
        alarm_stop_button[3]= (Button) findViewById(R.id.alarm_stop_button4);
        long seed = System.currentTimeMillis(); // 現在時刻のミリ秒
        Random r = new Random(seed);
        int n =(int) ((r.nextDouble()*10)%4);

        alarm_stop_button[n].setOnClickListener(new View.OnClickListener(){
            @Override
            public  void onClick(View v){
                finish();
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        //if (mp == null)
        // 音楽ファイルをmediaplayerに設定1
        /*mediaPlayer.setDataSource(afdescripter.getFileDescriptor(), afdescripter.getStartOffset(),
                afdescripter.getLength());
                */

// 再生準備、再生可能状態になるまでブロック
        //mp.prepare();

// 再生開始
        mp = MediaPlayer.create(this, RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)  );
        mp.start();
    }

    @Override
    public void onStop() {
        super.onStop();
        stopAndRelaese();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //stopAndRelaese();
    }

    private void stopAndRelaese() {
        if (mp != null&&mp.isPlaying()==true) {
// 再生終了
            mp.stop();

// リソースの解放
            mp.release();
        }
    }
}

