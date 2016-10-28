package com.example.sakashun.alarmapplication;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextClock;
import android.widget.TextView;

import com.example.sakashun.alarmapplication.Alarm.AlarmDataController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URI;
import java.util.Arrays;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Saka Shun on 2016/09/08.
 */
public class AlarmNotification extends Activity {
    private PowerManager.WakeLock wakelock;
    private KeyguardManager.KeyguardLock keylock;
    private Vibrator vib = null;
    private long pattern[] = {1000, 300 };

    MediaPlayer mp = null;//曲をながすため
    Uri music_uri = null;//曲のuri
    int sunuzu = 0;//スヌーズの時間保持
    int volume = 0;
    boolean vibrator = false;
    boolean light = false;
    int onoff_chec=1,onoff_chec_old=1;
    private LinearLayout mainLayout;// 背景のレイアウト
    Timer timer;//背景を徐々に変化させるためのもの
    int hsv_h = 100;//HSVのSの値
    int updown = 1;//HSVの変化量
    int updown_time = 40;//HSVの変化時間(ms)
    AudioManager am;// AudioManagerのフィールド
    int now_volume;//今現在の音量を格納する
    AlarmController alarmController;//アラームをキャンセルまたはスヌーズするためまたチェックも含む
    Handler handler = new Handler();//定期処理
    private Camera camera = null;

    boolean sunuzu_set = false;

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

        //ホントにならすべきかチェック
        alarmController = new AlarmController(this);
        if(number != -1  && !alarmController.AlarmChec(number)){
            Intent intent = new Intent(AlarmNotification.this,
                    com.example.sakashun.alarmapplication.MainActivity.class);
            startActivity(intent);
            finish();
        }

        //名前の部分のリンク
        final TextView alarm_name = (TextView)findViewById(R.id.alarm_name);
        mainLayout = (LinearLayout) findViewById(R.id.alarm_screen_layout);

        //button設定
        final Button alarm_stop_button[] = new Button[4];
        alarm_stop_button[0]= (Button) findViewById(R.id.alarm_stop_button1);
        alarm_stop_button[1]= (Button) findViewById(R.id.alarm_stop_button2);
        alarm_stop_button[2]= (Button) findViewById(R.id.alarm_stop_button3);
        alarm_stop_button[3]= (Button) findViewById(R.id.alarm_stop_button4);
        long seed = System.currentTimeMillis(); // 現在時刻のミリ秒
        Random r = new Random(seed);
        int n =(int) ((r.nextDouble()*10)%4);

        //アラームの音などの設定
        if(number!=-1) {
            //各種内容を取得
            AlarmDataController alarmDataControlle = new AlarmDataController(this);
            alarmDataControlle.OpenFile();

            volume = alarmDataControlle.volume[number];
            vibrator = alarmDataControlle.vibrator[number];
            light = alarmDataControlle.light[number];
            music_uri = Uri.parse(alarmDataControlle.music_uri[number]);
            mp = MediaPlayer.create(this, music_uri);
            if(alarmDataControlle.sunuzu[number].matches("なし")){
                sunuzu = 0;
            }else {
                sunuzu = Integer.parseInt(alarmDataControlle.sunuzu[number].split("分")[0]);
            }
        }

        //終了
        alarm_stop_button[n].setOnClickListener(new View.OnClickListener(){
            @Override
            public  void onClick(View v){
                if(number!=-1) {
                    alarmController = new AlarmController(AlarmNotification.this);
                    alarmController.AlarmOneCancel(number);
                    new AlarmDataController(AlarmNotification.this).UseCount(number);//起動したことを記録する
                }
                finish();
            }
        });

        //スヌーズ
        TextClock textClock = (TextClock)findViewById(R.id.textClock);
        textClock.setOnClickListener(new View.OnClickListener(){
            @Override
            public  void onClick(View v){
                if(number!=-1 && sunuzu!=0) {
                    alarmController = new AlarmController(AlarmNotification.this);
                    alarmController.AlarmSunuzuSet(number,sunuzu);
                    sunuzu_set = true;
                    //alarmController.AlarmOneCancel(number);
                    new AlarmDataController(AlarmNotification.this).UseCount(number);//起動したことを記録する
                    finish();
                }
            }
        });

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
        //System.out.println("色の定期更新起動");
        int rgb[];
        hsv_h += updown;
        if (updown > 0 && hsv_h >= 220) {
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

        //ついでにLEDのフラッシュを実行
        if(light && hsv_h%15==0) {
            System.out.println("カメラ "+onoff_chec+":"+onoff_chec_old);
            //パラメータ取得
            Camera.Parameters params = camera.getParameters();
            //フラッシュモードを点灯に設定
            if(params.getFlashMode().matches(Camera.Parameters.FLASH_MODE_OFF)) {
                params.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                System.out.println("カメラのフラッシュOn");
                onoff_chec_old=onoff_chec;
                onoff_chec=1;
            }else if(params.getFlashMode().matches(Camera.Parameters.FLASH_MODE_TORCH)) {
                params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                System.out.println("カメラのフラッシュOff");
                onoff_chec_old=onoff_chec;
                onoff_chec=0;
            }

            if(onoff_chec==onoff_chec_old){
                System.out.println("カメラのフラッシュエラー");
                onoff_chec = onoff_chec_old = 1;
                //カメラデバイス動作停止
                camera.stopPreview();
                //カメラデバイス解放
                camera.release();
                camera = null;

                //カメラデバイス取得
                camera = Camera.open();
                //セット
                Camera.Parameters cp = camera.getParameters();
                cp.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                camera.setParameters(cp);
                //準備
                try {
                    camera.setPreviewTexture(new SurfaceTexture(0));
                } catch (IOException e) {
                }
                //カメラデバイス動作開始
                camera.startPreview();

            }
            //パラメータ設定
            camera.setParameters(params);
        }
    }

    //戻るボタンを無効化
    @Override
    public void onBackPressed() {}

    @Override
    public void onStart() {
        super.onStart();
        Log.v("通知ログ", "start");
        //バックの色が徐々に変化するように定期更新タスクを実行
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                LayoutColorChange();
                handler.postDelayed(this,updown_time);
            }
        },updown_time);

        // Vibratorのパターン実行
        if(vibrator){
            // Vibratorクラスのインスタンス取得
            vib = (Vibrator)getSystemService(VIBRATOR_SERVICE);
            vib.vibrate(pattern,0);
            System.out.println("バイブレーションOn");
        }

        if(light) {
            //カメラデバイス取得
            camera = Camera.open();
            //セット
            Camera.Parameters cp = camera.getParameters();
            cp.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            camera.setParameters(cp);
            //準備
            try {
                camera.setPreviewTexture(new SurfaceTexture(0));
            } catch (IOException e) {
            }
            //カメラデバイス動作開始
            camera.startPreview();
        }
        //スリープ画面にならないように
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // 再生準備、再生可能状態になるまでブロック
        //mp.prepare();

        // 再生準備
        if(mp==null){
            System.out.println("mpがnullでした");
            if(music_uri != null){
                mp = MediaPlayer.create(this, music_uri);
            }else {
                mp = MediaPlayer.create(AlarmNotification.this, RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM));
            }
            mp.setLooping(true);//リピート設定
        }

        //音量の取得
        am = (AudioManager)getSystemService(Context.AUDIO_SERVICE);// AudioManagerを取得する
        now_volume = am.getStreamVolume(AudioManager.STREAM_MUSIC);//曲再生時の音量を取得
        am.setStreamVolume(AudioManager.STREAM_MUSIC,am.getStreamMaxVolume(AudioManager.STREAM_MUSIC),0);//音量をmaxにする
        mp.setVolume((float) volume/100f,(float) volume/100f);//Mediaplayerの音量を変更するにする

        if (!mp.isPlaying()) {//再生開始
            // MediaPlayerの再生
            mp.seekTo(0); // プレイ中のBGMをスタート位置に戻す
            mp.start();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.v("通知ログ", "stop");
        stopAndRelaese();//曲を止める

        am.setStreamVolume(AudioManager.STREAM_MUSIC,now_volume,0);//音量を元に戻す

        //定期更新終了
        handler.removeCallbacksAndMessages(null);

        // Vibratorのパターン停止
        if(vibrator) vib.cancel();

        if(light) {
            //カメラデバイス動作停止
            camera.stopPreview();
            //カメラデバイス解放
            camera.release();
            camera = null;
        }

        //スリープ画面になるようにする
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    public void onDestroy() {
        Log.v("通知ログ", "destroy");
        if(sunuzu_set == false){
            //スヌーズがセットされていないなら消す
            //アラーム番号の取得
            new AlarmController(AlarmNotification.this).AlarmOneCancel(getIntent().getIntExtra("NUMBER",-1));
        }
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

