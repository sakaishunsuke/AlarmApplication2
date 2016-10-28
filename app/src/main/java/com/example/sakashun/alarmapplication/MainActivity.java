package com.example.sakashun.alarmapplication;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.balysv.materialripple.MaterialRippleLayout;
import com.example.sakashun.alarmapplication.Alarm.AlarmDataController;
import com.example.sakashun.alarmapplication.Alarm.AlarmSort;
import com.example.sakashun.alarmapplication.Alarm.LockScreenAlarmSet;
import com.example.sakashun.alarmapplication.R;
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
import java.util.Calendar;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    void FastInit(){
        //各レベルの設定
        new Pref(getApplicationContext(), ("level_2"), "tuti", true);

        new Pref(getApplicationContext(), ("level_3"), "tuti", true);
        new Pref(getApplicationContext(), ("level_3"), "led" , true);

        new Pref(getApplicationContext(), ("level_4"), "vibration", true);
        new Pref(getApplicationContext(), ("level_4"), "led" , true);
        new Pref(getApplicationContext(), ("level_4"), "tuti", true);

        new Pref(getApplicationContext(), ("level_5"), "music", true);
        new Pref(getApplicationContext(), ("level_5"), "vibration", true);
        new Pref(getApplicationContext(), ("level_5"), "led" , true);
        new Pref(getApplicationContext(), ("level_5"), "tuti", true);

        new Pref(getApplicationContext(), "level_1", "PriColor", getResources().getColor(R.color.colorReminderList1Primary));
        new Pref(getApplicationContext(), "level_1", "SecoColor", getResources().getColor(R.color.colorReminderList1Secondary));

        new Pref(getApplicationContext(), "level_2", "PriColor", getResources().getColor(R.color.colorReminderList2Primary));
        new Pref(getApplicationContext(), "level_2", "SecoColor", getResources().getColor(R.color.colorReminderList2Secondary));

        new Pref(getApplicationContext(), "level_3", "PriColor", getResources().getColor(R.color.colorReminderList3Primary));
        new Pref(getApplicationContext(), "level_3", "SecoColor", getResources().getColor(R.color.colorReminderList3Secondary));

        new Pref(getApplicationContext(), "level_4", "PriColor", getResources().getColor(R.color.colorReminderList4Primary));
        new Pref(getApplicationContext(), "level_4", "SecoColor", getResources().getColor(R.color.colorReminderList4Secondary));

        new Pref(getApplicationContext(), "level_5", "PriColor", getResources().getColor(R.color.colorReminderList5Primary));
        new Pref(getApplicationContext(), "level_5", "SecoColor", getResources().getColor(R.color.colorReminderList5Secondary));
    }

    void AlarmStart(){
        Intent intent = new Intent(getApplication()
                ,com.example.sakashun.alarmapplication.AlarmConfig.class);
        startActivity(intent);
    }

    void ReminderStart(){
        Intent intent = new Intent(getApplication(),ReminderConfigActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity_main);

        Intent intent = new Intent(getApplication(), LockScreenAlarmSet.class);
        startActivity(intent);

        //初回のみ実行
        if (new Pref().GetBoolean(getApplicationContext(),"init","init")==false){
            FastInit();
            new Pref(getApplicationContext(),"init","init3",true);
            //初回起動が終わったことを保存
        }

        /*
        new Pref(getApplicationContext(),
                getApplication().getString(R.string.reminder_list_name),
                getApplication().getString(R.string.reminder_list_number),0 );
        new Pref(getApplicationContext(),"init","init",false);
        */

        RelativeLayout alarm_all_list = (RelativeLayout)findViewById(R.id.alarm_all_list);
        RelativeLayout reminder_all_list = (RelativeLayout)findViewById(R.id.reminder_all_lisst);

        //アラーム画面へのボタン
        alarm_all_list.setOnClickListener(new View.OnClickListener(){
            @Override
            public  void onClick(View v){
                AlarmStart();
            }
        });

        //カレンダー画面へのボタン
        reminder_all_list.setOnClickListener(new View.OnClickListener(){
            @Override
            public  void onClick(View v){
                ReminderStart();
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

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

    //onStartで使用するメソッド

    void alarm_list_make(){//ファイルコントローらーの中でファイルを開く
        final AlarmDataController alarmDataController = new AlarmDataController(getApplicationContext());
        alarmDataController.OpenFile();

        AlarmSort alarmSort = new AlarmSort(getApplicationContext(),
                alarmDataController.time,
                alarmDataController.hindo,
                alarmDataController.alarm_kazu
        );

        //使用頻度順にする
        alarmSort.UsedSort();


        //LayoutInflaterの準備
        LinearLayout incLayout = null;
        LinearLayout mainLayout = (LinearLayout) findViewById(R.id.alarm_list_linear);

        //重複をなくすために、一度全部消去
        mainLayout.removeAllViews();
        for(int i=0;i<alarmSort.sort_kauz && i<5;i++) {
            final int number = alarmSort.sort_number[i];

            //log_text.xmlの内容をメインレイアウトのlog_listに１つ追加
            final LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            //LayoutInfkaterからViewのインスタンスを取得
            incLayout = (LinearLayout) inflater.inflate(R.layout.home_alarm_list, null);

            //各部分のアクセスの処理を書く
            final TextView list_time = (TextView) incLayout.findViewById(R.id.list_time);//アラーム時間
            final TextView list_name = (TextView) incLayout.findViewById(R.id.list_name);//アラーム名
            LinearLayout alarm_list_layout = (LinearLayout) incLayout.findViewById(R.id.alarm_list_layout);//ひな形全体のレイアウト

            list_time.setText(alarmDataController.time[number]);//時間のセット
            list_name.setText(alarmDataController.name[number]);//名前のセット

            //タッチされてアラームがonoffになるようにする
            alarm_list_layout.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    AlarmController alarmController = new AlarmController(MainActivity.this);
                    if(!alarmController.AlarmOneCancel(number))//アラームをセットをいったん消去
                        System.out.println("編集前のいったん消去に失敗");
                    // 編集 ボタンクリック処理
                    Intent intent = new Intent(getApplication()
                            ,com.example.sakashun.alarmapplication.Alarm.AlarmSetting.class);
                    intent.putExtra("Edit",true);
                    intent.putExtra("number",number);
                    startActivity(intent);

                }
            });

            mainLayout.addView(incLayout);//追加したlogを反映させる
        }
    }

    void reminder_list_make(){
        //リマインダー一覧を取得
        //ファイルコントローらーの中でファイルを開く
        final ReminderFileController reminderFileController = new ReminderFileController(getApplicationContext());
        reminderFileController.OpenFile();

        ReminderSort reminderSort = new ReminderSort(getApplicationContext(),
                reminderFileController.hiduke,
                reminderFileController.time,
                reminderFileController.level,
                reminderFileController.reminder_kazu
        );

        //今日のリマインダーを取り出す
        reminderSort.TodaySort();

        //LayoutInflaterの準備
        LinearLayout incLayout = null;
        LinearLayout mainLayout = (LinearLayout) findViewById(R.id.reminder_list_linear);
        //重複をなくすために、一度全部消去
        mainLayout.removeAllViews();

        for(int i=0;i<reminderSort.sort_kauz;i++) {
            final int number = reminderSort.sort_number[i];

            //log_text.xmlの内容をメインレイアウトのlog_listに１つ追加
            final LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            //LayoutInfkaterからViewのインスタンスを取得
            incLayout = (LinearLayout) inflater.inflate(R.layout.home_reminder_list, null);

            //各部分のアクセスの処理を書く
            final TextView list_time = (TextView) incLayout.findViewById(R.id.list_time);//アラーム時間
            final TextView list_name = (TextView) incLayout.findViewById(R.id.list_name);//アラーム名
            LinearLayout reminder_list_layout = (LinearLayout) incLayout.findViewById(R.id.reminder_list_layout);//ひな形全体のレイアウト

            String s[] = reminderFileController.hiduke[number].split("/");
            list_time.setText(s[1]+"/"+s[2]+" "+reminderFileController.time[number]);//時間のセット
            list_name.setText(reminderFileController.name[number]);//名前のセット

            // 背景色を変更
            //斜め部分の準備
            int level_number = Integer.parseInt(reminderFileController.level[number].split("レベル")[1]);
            reminder_list_layout.setBackgroundColor(new Pref().GetInt(getApplicationContext(),("level_"+level_number),"SecoColor"));

            //タッチされて編集画面になるようにする
            reminder_list_layout.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    // 内容確認ダイアログの生成
                    final AlertDialog.Builder alertDlg = new AlertDialog.Builder(MainActivity.this);
                    alertDlg.setTitle(list_name.getText());
                    alertDlg.setMessage(reminderFileController.memo_content[number]);
                    alertDlg.setNegativeButton(
                            "もどる",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });
                    // 表示
                    alertDlg.create().show();

                }
            });
            reminder_list_layout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    // 確認ダイアログの生成
                    final AlertDialog.Builder alertDlg = new AlertDialog.Builder(MainActivity.this);
                    alertDlg.setTitle(list_name.getText());
                    alertDlg.setMessage(reminderFileController.memo_content[number]);
                    alertDlg.setPositiveButton(
                            "編集",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(MainActivity.this,ReminderSetting.class);
                                    intent.putExtra("Edit",true);
                                    intent.putExtra("number",number);
                                    startActivity(intent);
                                    reminder_list_make();//再起させてリストを作り直す
                                }
                            });
                    alertDlg.setNegativeButton(
                            "もどる",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });
                    // 表示
                    alertDlg.create().show();
                    return true;//ここをfalesにするとタップの処理も実行される
                }
            });
            /*
            reminder_list_layout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    // 確認ダイアログの生成
                    final AlertDialog.Builder alertDlg = new AlertDialog.Builder(MainActivity.this);
                    alertDlg.setTitle(list_name.getText());
                    alertDlg.setMessage(reminderFileController.memo_content[number]);
                    alertDlg.setPositiveButton(
                            "消去",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    reminderFileController.DeleteFile(number);
                                    reminder_list_make();//再起させてリストを作り直す
                                }
                            });
                    alertDlg.setNegativeButton(
                            "もどる",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });
                    // 表示
                    alertDlg.create().show();
                    return true;//ここをfalesにするとタップの処理も実行される
                }
            });
            */
            mainLayout.addView(incLayout);//追加したlogを反映させる
        }
    }
    //リストの生成
    @Override
    public void onStart(){
        super.onStart();
        alarm_list_make();
        reminder_list_make();
    }

    //ナビゲーション部分
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_alarm) {
            AlarmStart();
        } else if (id == R.id.nav_reminder) {
            ReminderStart();
        } else if (id == R.id.nav_setting) {

        } else if (id == R.id.nav_help) {

        } else if (id == R.id.nav_version) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
