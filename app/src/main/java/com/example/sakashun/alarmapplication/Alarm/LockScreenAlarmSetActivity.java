package com.example.sakashun.alarmapplication.Alarm;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.example.sakashun.alarmapplication.AlarmConfig;
import com.example.sakashun.alarmapplication.AlarmController;
import com.example.sakashun.alarmapplication.MyNotif;
import com.example.sakashun.alarmapplication.R;
import com.example.sakashun.alarmapplication.Rminder.Pref;

import java.io.IOException;

/**
 * Created by Saka Shun on 2016/10/23.
 */

public class LockScreenAlarmSetActivity extends Activity {

    private PowerManager.WakeLock wakelock;
    private KeyguardManager.KeyguardLock keylock;
    TextView level_text[] = new TextView[5];

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rock_screen_set_dial);

        System.out.println("アクティビティまで行ってる");

        // スクリーンロックを解除する
        getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        KeyguardManager keyguard = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        keylock = keyguard.newKeyguardLock("disableLock");
        keylock.disableKeyguard();

        //通知の消去
        MyNotif myNotif = new MyNotif(this);
        myNotif.DeleteNotif(334);

        //log_text.xmlの内容をメインレイアウトのlog_listに１つ追加
        final LayoutInflater inflater2 = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //ダイアログのレイアウトを持ってくる
        LinearLayout dial_list = (LinearLayout) inflater2.inflate(R.layout.rock_screen_set_dial, null);

        //LayoutInflaterの準備
        LinearLayout incLayout = null;
        //アラームリストの入れる場所をダイアログの中の部分にする
        LinearLayout mainLayout =(LinearLayout)dial_list.findViewById(R.id.alarm_list_linear);
        //重複をなくすために、一度全部消去
        mainLayout.removeAllViews();

        AlarmDataController alarmDataController = new AlarmDataController(LockScreenAlarmSetActivity.this);

        AlarmSort alarmSort = new AlarmSort(getApplicationContext(),
                alarmDataController.time,
                alarmDataController.hindo,
                alarmDataController.alarm_kazu
        );
        alarmSort.TimeSort();
        System.out.println(alarmSort.sort_kauz);
        for(int i=0;i<alarmSort.sort_kauz;i++) {

            final int number = alarmSort.sort_number[i];
            //log_text.xmlの内容をメインレイアウトのlog_listに１つ追加
            final LayoutInflater inflater3 = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            //LayoutInfkaterからViewのインスタンスを取得
            incLayout = (LinearLayout) inflater3.inflate(R.layout.lock_alarm_set_list, null);

            //各部分のアクセスの処理を書く
            final TextView list_name = (TextView) incLayout.findViewById(R.id.list_name);//アラーム名前
            final TextView list_time = (TextView) incLayout.findViewById(R.id.list_time);//アラーム時間
            LinearLayout lock_set_list_layout = (LinearLayout) incLayout.findViewById(R.id.alarm_list_layout);//ひな形全体のレイアウト
            final Switch alarm_list_switch = (Switch)incLayout.findViewById(R.id.alarm_list_switch);//アラームonoffスイッチ

            list_name.setText(alarmDataController.name[number]);
            list_time.setText(alarmDataController.time[number]);
            alarm_list_switch.setChecked(alarmDataController.onoff[number]);

            //タッチされてアラームがonoffになるようにする
            lock_set_list_layout.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    AlarmController alarmController = new AlarmController(LockScreenAlarmSetActivity.this);
                    if(alarm_list_switch.isChecked()==false) {
                        //チェックがonにしたならアラームをセット
                        if(alarmController.AlarmOneSet(number)){
                            //無事にセットできたらチェックボタンを変更
                            alarm_list_switch.setChecked(true);
                        }
                    }else {
                        if(alarmController.AlarmOneCancel(number)){
                            //無事にキャンセルできたらチェックボタンを変更
                            alarm_list_switch.setChecked(false);
                        }
                    }
                }
            });

            mainLayout.addView(incLayout);//追加したlistを反映させる
        }

        // カスタムビューを設定
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(
                LAYOUT_INFLATER_SERVICE);
        //final View layout = inflater.inflate(R.layout.rock_screen_set_dial,(ViewGroup) findViewById(R.id.alarm_list_linear));
        final View layout = inflater.inflate(R.layout.rock_screen_set_dial,(ViewGroup) dial_list);

        // アラーとダイアログ を生成
        AlertDialog.Builder builder = new AlertDialog.Builder(LockScreenAlarmSetActivity.this);
        builder.setTitle( "アラーム設定");
        builder.setView(layout);
        builder.setNegativeButton("完了", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Cancel ボタンクリック処理
                finish();
            }
        });
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                // キャンセルされたときの処理
                finish();
            }
        });
        // 表示
        builder.create().show();

    }

    @Override
    public void onStop(){
        super.onStop();
        finish();
    }
}
