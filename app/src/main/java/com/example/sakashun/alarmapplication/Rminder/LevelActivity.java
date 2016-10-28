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
                final Switch led_onoff = (Switch) layout.findViewById(R.id.led_onoff);
                final Switch vibration_onoff = (Switch) layout.findViewById(R.id.vibration_onoff);
                final Switch tuti_onoff = (Switch) layout.findViewById(R.id.tuti_onoff);
                music_onoff.setChecked(new Pref().GetBoolean(LevelActivity.this, ("level_" + finalI), "music"));
                led_onoff.setChecked(new Pref().GetBoolean(LevelActivity.this, ("level_" + finalI), "led"));
                vibration_onoff.setChecked(new Pref().GetBoolean(LevelActivity.this, ("level_" + finalI), "vibration"));
                tuti_onoff.setChecked(new Pref().GetBoolean(LevelActivity.this, ("level_" + finalI), "tuti"));

                // アラーとダイアログ を生成
                AlertDialog.Builder builder = new AlertDialog.Builder(LevelActivity.this);
                builder.setTitle("レベル" + finalI + " 設定");
                builder.setView(layout);
                builder.setPositiveButton("決定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        new Pref(getApplicationContext(), ("level_" + finalI), "music", music_onoff.isChecked());
                        new Pref(getApplicationContext(), ("level_" + finalI), "led", led_onoff.isChecked());
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
}
