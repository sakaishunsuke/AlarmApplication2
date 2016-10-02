package com.example.sakashun.alarmapplication;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Saka Shun on 2016/09/08.
 */
public class AlarmNotification extends Activity {
    private PowerManager.WakeLock wakelock;
    private KeyguardManager.KeyguardLock keylock;

    MediaPlayer mp = null;//曲をながすため
    Uri music_uri = null;//曲のuri
    int sunuzu = 0;//スヌーズの時間保持
    int volume = 50;
    boolean vibrator = false;
    boolean light = false;
    private LinearLayout mainLayout;// 背景のレイアウト
    Timer timer;//背景を徐々に変化させるためのもの
    int hsv_h = 100;//HSVのSの値
    int updown = 5;//HSVの変化量
    int updown_time = 200;//HSVの変化時間(ms)
    AudioManager am;// AudioManagerのフィールド
    int now_volume;//今現在の音量を格納する
    AlarmController alarmController;//アラームをキャンセルまたはスヌーズするため

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
        //アラーム番号の取得
        final int number = getIntent().getIntExtra("NUMBER",-1);

        //名前の部分のリンク
        TextView alarm_name = (TextView)findViewById(R.id.alarm_name);
        mainLayout = (LinearLayout) findViewById(R.id.alarm_screen_layout);

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
                if(number!=-1) {
                    alarmController = new AlarmController();
                    alarmController.AlarmOneCancel(AlarmNotification.this, number);
                }
                finish();
            }
        });

        //アラームの音などの設定
        if(number!=-1) {
            //各種内容を取得
            try {
                InputStream in = openFileInput("alarm_data" + number + ".txt");
                BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
                String s;
                System.out.println("中身を読み取り、アラーム画面に反映させる");
                while ((s = reader.readLine()) != null) {
                    //現在のアラームの番号を受け取る
                    System.out.println("中身は" + s + "←");
                    //alarm_list_number= (int) Long.parseLong(s,0);
                    String[] strs = s.split(",");
                    if (strs[0].matches("name")) {
                        alarm_name.setText(strs[1]);
                    } else if (strs[0].matches("volume")) {
                        volume = Integer.parseInt(strs[1]);
                    } else if (strs[0].matches("vibrator")) {
                        vibrator = strs[1].matches("true");
                    } else if (strs[0].matches("light")) {
                        light = strs[1].matches("true");
                    } else if (strs[0].matches("music")) {
                        music_uri = Uri.parse(strs[1]);
                        mp = MediaPlayer.create(this, music_uri);
                    } else if (strs[0].matches("sunuzu")) {
                        if(strs[1].matches("なし")){
                            sunuzu = 0;
                        }else {
                            sunuzu = Integer.parseInt(strs[1]);
                        }
                    }
                }
                reader.close();
            } catch (IOException e) {
                //アラーム内容の取得に失敗
                e.printStackTrace();
            }

        }

    }

    //HSV→RGB変換関数
    public int[] HSVtoRGB(int h, int s, int v){
        float f;
        int i, p, q, t;
        int[] rgb = new int[3];

        i = (int)Math.floor(h / 60.0f) % 6;
        f = (float)(h / 60.0f) - (float)Math.floor(h / 60.0f);
        p = (int)Math.round(v * (1.0f - (s / 255.0f)));
        q = (int)Math.round(v * (1.0f - (s / 255.0f) * f));
        t = (int)Math.round(v * (1.0f - (s / 255.0f) * (1.0f - f)));

        switch(i){
            case 0 : rgb[0] = v; rgb[1] = t; rgb[2] = p; break;
            case 1 : rgb[0] = q; rgb[1] = v; rgb[2] = p; break;
            case 2 : rgb[0] = p; rgb[1] = v; rgb[2] = t; break;
            case 3 : rgb[0] = p; rgb[1] = q; rgb[2] = v; break;
            case 4 : rgb[0] = t; rgb[1] = p; rgb[2] = v; break;
            case 5 : rgb[0] = v; rgb[1] = p; rgb[2] = q; break;
        }

        return rgb;
    }

    //色が変わっていくようにHSVの値を変化させてRGBにする
    public void LayoutColorChange() {
        System.out.println("色の定期更新起動");
        int rgb[];
        hsv_h += updown;
        if (updown > 0 && hsv_h >= 215) {
            updown *= -1;
        } else if (updown < 0 && hsv_h <= 35) {
            updown *= -1;
        }
        rgb = HSVtoRGB(hsv_h, 255, 255);
        mainLayout.setBackgroundColor(Color.rgb(rgb[0], rgb[1], rgb[2]));
        //ついでに曲が止まってないか確認
        if (mp!=null && !mp.isPlaying()) {
            try {
                mp.prepare();
            } catch (IllegalStateException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (java.lang.ArrayIndexOutOfBoundsException e) {
                e.printStackTrace();
            }
            // MediaPlayerの再生
            mp.seekTo(0); // プレイ中のBGMをスタート位置に戻す
            mp.start();
        }
    }

    //戻るボタンを無効化
    @Override
    public void onBackPressed() {}

    @Override
    public void onStart() {
        super.onStart();
        Log.v("通知ログ", "start");
        //バックの色が徐々に変化するように定期更新タスクを生成
        timer = new Timer();
        TimerTask timerTask = new AlarmScreenTimarTask(this);
        timer.scheduleAtFixedRate(timerTask, 0, updown_time);

        // 再生準備、再生可能状態になるまでブロック
        //mp.prepare();

        //音量の取得
        am = (AudioManager)getSystemService(Context.AUDIO_SERVICE);// AudioManagerを取得する
        now_volume = am.getStreamVolume(AudioManager.STREAM_MUSIC);//曲再生時の音量を取得
        am.setStreamVolume(AudioManager.STREAM_MUSIC,volume,0);//音量をsetにする
        // 再生開始
        if(mp==null){
            System.out.println("mpがnullでした");
            if(music_uri != null){
                mp = MediaPlayer.create(this, music_uri);
            }else {
                mp = MediaPlayer.create(AlarmNotification.this, RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM));
            }
            mp.setLooping(true);//リピート設定
            // MediaPlayerの再生
            mp.seekTo(0); // プレイ中のBGMをスタート位置に戻す
            mp.start();
        }
        if (!mp.isPlaying()) {
            try {
                mp.prepare();
            } catch (IllegalStateException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (java.lang.ArrayIndexOutOfBoundsException e){
                e.printStackTrace();
            }
            // MediaPlayerの再生
            mp.seekTo(0); // プレイ中のBGMをスタート位置に戻す
            mp.start();
        }
    }


    @Override
    public void onStop() {
        super.onStop();
        Log.v("通知ログ", "stop");
        am.setStreamVolume(AudioManager.STREAM_MUSIC,now_volume,0);//音量を元に戻す
        if (timer != null)//定期更新をやめさせる
            timer.cancel();
        stopAndRelaese();//曲を止める
    }

    @Override
    public void onDestroy() {
        Log.v("通知ログ", "destroy");
        super.onDestroy();
    }

    private void stopAndRelaese() {
        if (mp != null&&mp.isPlaying()==true) {
            // 再生終了
            mp.stop();
            // リソースの解放
            mp.release();
            //nullをセット
            mp = null;
        }
    }
}

