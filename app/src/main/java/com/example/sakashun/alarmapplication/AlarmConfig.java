package com.example.sakashun.alarmapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sakashun.alarmapplication.Alarm.AlarmDataController;
import com.example.sakashun.alarmapplication.Alarm.AlarmSort;
import com.example.sakashun.alarmapplication.Rminder.Pref;
import com.example.sakashun.alarmapplication.Rminder.ReminderFileController;
import com.example.sakashun.alarmapplication.Rminder.ReminderSort;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Arrays;

/**
 * Created by Saka Shun on 2016/09/05.
 */
public class AlarmConfig extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private AlarmController alarmController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarm_config_activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("アラーム一覧");
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAlarmFloatingActionButton)));
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplication()
                        , com.example.sakashun.alarmapplication.Alarm.AlarmSetting.class);
                startActivity(intent);
            }
        });


    }

    public void alarm_list_make(){
        //ファイルコントローらーの中でファイルを開く
        final AlarmDataController alarmDataController = new AlarmDataController(getApplicationContext());
        alarmDataController.OpenFile();

        AlarmSort alarmSort = new AlarmSort(getApplicationContext(),
                alarmDataController.time,
                alarmDataController.hindo,
                alarmDataController.alarm_kazu
        );

        //時間順にする
        alarmSort.TimeSort();

        //LayoutInflaterの準備
        LinearLayout incLayout = null;
        LinearLayout mainLayout = (LinearLayout) findViewById(R.id.alarm_list_linear);
        //重複をなくすために、一度全部消去
        mainLayout.removeAllViews();

        for(int i=0;i<alarmSort.sort_kauz;i++) {
            final int number = alarmSort.sort_number[i];

            //log_text.xmlの内容をメインレイアウトのlog_listに１つ追加
            final LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            //LayoutInfkaterからViewのインスタンスを取得
            incLayout = (LinearLayout) inflater.inflate(R.layout.alarm_config_list, null);

            //各部分のアクセスの処理を書く
            final TextView list_time = (TextView) incLayout.findViewById(R.id.list_time);//アラーム時間
            final TextView list_name = (TextView) incLayout.findViewById(R.id.list_name);//アラーム名
            RelativeLayout alarm_list_layout = (RelativeLayout) incLayout.findViewById(R.id.alarm_list_layout);//ひな形全体のレイアウト
            final Switch alarm_list_switch = (Switch)incLayout.findViewById(R.id.alarm_list_switch);//アラームonoffスイッチ

            list_name.setText(alarmDataController.name[number]);
            list_time.setText(alarmDataController.time[number]);
            alarm_list_switch.setChecked(alarmDataController.onoff[number]);//onoffの状態を反映させる

            //タッチされてアラームがonoffになるようにする
            alarm_list_layout.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    alarmController = new AlarmController(AlarmConfig.this);
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

            final Context alarm_config_context = AlarmConfig.this;//ダイアログ中にconxtを渡すため

            alarm_list_layout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    // 確認ダイアログの生成
                    // カスタムビューを設定
                    LayoutInflater inflater = (LayoutInflater) AlarmConfig.this.getSystemService(
                            LAYOUT_INFLATER_SERVICE);
                    final View layout = inflater.inflate(R.layout.reminder_setting_dial,
                            (ViewGroup) findViewById(R.id.layout_rooy));
                    final TextView list_time = (TextView) layout.findViewById(R.id.list_time);
                    final TextView list_vib = (TextView) layout.findViewById(R.id.list_vib);
                    final TextView list_led = (TextView) layout.findViewById(R.id.list_led);
                    final TextView list_sunuzu = (TextView) layout.findViewById(R.id.list_sunuzu);
                    list_time.setText(alarmDataController.time[number]);
                    list_vib.setText(alarmDataController.vibrator[number] ? "ON":"OFF");
                    list_led.setText(alarmDataController.light[number] ? "ON":"OFF");
                    list_sunuzu.setText(alarmDataController.sunuzu[number]);

                    // アラーとダイアログ を生成
                    AlertDialog.Builder builder = new AlertDialog.Builder(AlarmConfig.this);
                    builder.setTitle(alarmDataController.name[number]);
                    builder.setView(layout);
                    builder.setPositiveButton("消去", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // 消去 ボタンクリック処理
                            alarmController = new AlarmController(alarm_config_context);
                            if(!alarmController.AlarmOneCancel(number)) {//アラームの消去
                                System.out.println("消去に失敗　番号:" + number);
                                return;//アラームの消去に失敗したので表示を消さない
                            }
                            alarmDataController.DeleteFile(number);
                            alarm_list_make();//再起させてリストを作り直す
                        }
                    });
                    builder.setNegativeButton("編集",new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    alarmController = new AlarmController(alarm_config_context);
                                    if(!alarmController.AlarmOneCancel(number))//アラームをセットをいったん消去
                                        System.out.println("編集前のいったん消去に失敗");
                                    // 編集 ボタンクリック処理
                                    Intent intent = new Intent(getApplication()
                                            ,com.example.sakashun.alarmapplication.Alarm.AlarmSetting.class);
                                    intent.putExtra("Edit",true);
                                    intent.putExtra("number",number);
                                    startActivity(intent);
                                    alarm_list_make();//再起させてリストを作り直す
                        }
                    });
                    builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            // キャンセルされたときの処理
                            alarm_list_make();//再起させてリストを作り直す
                        }
                    });
                    // 表示
                    builder.create().show();
                    return true;//ここをfalesにするとタップの処理も実行される
                }
            });
            mainLayout.addView(incLayout);//追加したlogを反映させる
        }
    }



    @Override
    public void onStart(){
        super.onStart();
        alarm_list_make();
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_alarm) {
            // Handle the camera action
        } else if (id == R.id.nav_reminder) {
            finish();
            Intent intent = new Intent(getApplication(),ReminderConfigActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_setting) {

        } else if (id == R.id.nav_help) {

        } else if (id == R.id.nav_version) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}
