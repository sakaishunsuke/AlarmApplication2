package com.example.sakashun.alarmapplication.Rminder;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;

import com.example.sakashun.alarmapplication.R;

import java.io.IOException;

/**
 * Created by Saka Shun on 2016/10/23.
 */

public class LevelActivity extends Activity {

    TextView level_text[] = new TextView[5];
    MediaPlayer mp;//アラーム曲の格納
    AudioManager am;// AudioManagerのフィールド
    int now_volume;//今現在の音量を格納する

    public void seekbarSet(SeekBar volumeSeekbar){
        am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);// AudioManagerを取得する
        now_volume = am.getStreamVolume(AudioManager.STREAM_MUSIC);//曲再生時の音量を取得
        mp = MediaPlayer.create(this, RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));//通知のデフォルトの曲をセット
        //音量シークバー設定
        volumeSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(
                    SeekBar volumeSeekbar,
                    int progress,
                    boolean fromUser
            ) {
                // シークバーの入力値に変化が生じた段階で音量を変更
                mp.setVolume((float) progress/100.0f,(float) progress/100.0f);
                //System.out.println("シークバーの値は"+progress);
                if (!mp.isPlaying()) {
                    // MediaPlayerの再生
                    mp.seekTo(0); // プレイ中のBGMをスタート位置に戻す
                    mp.start();
                }
            }
            public void onStartTrackingTouch(SeekBar volumeSeekbar) {
                // トグル（シークバーのつまみ）がタッチされたときの動作
                am.setStreamVolume(AudioManager.STREAM_MUSIC,am.getStreamMaxVolume(AudioManager.STREAM_MUSIC),0);//音量をmaxにする
                mp.setLooping(true);//リピート設定
                if(mp!=null)
                    // mp.seekTo(0); // プレイ中のBGMをスタート位置に戻す
                    mp.start();
            }
            public void onStopTrackingTouch(SeekBar volumeSeekbar) {
                // トグル（シークバーのつまみ）がリリースされたときの動作
                am.setStreamVolume(AudioManager.STREAM_MUSIC,now_volume,0);//音量を元に戻す
                if(mp!=null) {
                    mp.stop();
                    try {
                        mp.prepare();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    // リソースの解放
                    //mp.release();
                }
            }
        });
    }

    void SetClick(int number) {
        // カスタムビューを設定
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(
                LAYOUT_INFLATER_SERVICE);
        final View layout = inflater.inflate(R.layout.reminder_level_dial,
                (ViewGroup) findViewById(R.id.layout_rooy));

        final int finalI = number;
        level_text[number-1].setBackgroundColor(new Pref().GetInt(getApplicationContext(), ("level_" + number), "PriColor"));
        level_text[number-1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Switch music_onoff = (Switch) layout.findViewById(R.id.music_onoff);
                final SeekBar volume = (SeekBar) layout.findViewById(R.id.volume);
                final Switch vibration_onoff = (Switch) layout.findViewById(R.id.vibration_onoff);
                final Switch tuti_onoff = (Switch) layout.findViewById(R.id.tuti_onoff);
                music_onoff.setChecked(new Pref().GetBoolean(LevelActivity.this, ("level_" + finalI), "music"));
                volume.setProgress(new Pref().GetInt(LevelActivity.this, ("level_" + finalI), "music_volu"));
                vibration_onoff.setChecked(new Pref().GetBoolean(LevelActivity.this, ("level_" + finalI), "vibration"));
                tuti_onoff.setChecked(new Pref().GetBoolean(LevelActivity.this, ("level_" + finalI), "tuti"));

                //シークバー操作時の音
                seekbarSet(volume);

                // アラーとダイアログ を生成
                AlertDialog.Builder builder = new AlertDialog.Builder(LevelActivity.this);
                builder.setTitle("レベル" + finalI + " 設定");
                builder.setView(layout);
                builder.setPositiveButton("決定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        new Pref(getApplicationContext(), ("level_" + finalI), "music", music_onoff.isChecked());
                        new Pref(getApplicationContext(), ("level_" + finalI), "music_volu", volume.getProgress());
                        new Pref(getApplicationContext(), ("level_" + finalI), "vibration", vibration_onoff.isChecked());
                        new Pref(getApplicationContext(), ("level_" + finalI), "tuti", tuti_onoff.isChecked());
                        SetClick(finalI);
                    }
                });
                builder.setNegativeButton("戻る", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Cancel ボタンクリック処理
                        SetClick(finalI);
                    }
                });
                builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                                // キャンセルされたときの処理
                                SetClick(finalI);
                            }
                        });
                // 表示
                builder.create().show();
            }
        });
    }



    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reminder_level_activity);

        Toolbar toolbar;
        toolbar= (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("各レベルの設定");
        //setSupportActionBar(toolbar);

        level_text[0] = (TextView)findViewById(R.id.level1_text);
        level_text[1] = (TextView)findViewById(R.id.level2_text);
        level_text[2] = (TextView)findViewById(R.id.level3_text);
        level_text[3] = (TextView)findViewById(R.id.level4_text);
        level_text[4] = (TextView)findViewById(R.id.level5_text);


        for(int i=1;i<6;i++) {
            SetClick(i);
        }
    }

    @Override
    public void onStop(){
        super.onStop();
        //もし曲がセットされていたら
        if (mp != null) {
            // 再生終了
            mp.stop();
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        //もし曲がセットされていたら
        if (mp != null) {
            // リソースの解放
            mp.release();
        }
    }
}
