package com.example.sakashun.alarmapplication;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.balysv.materialripple.MaterialRippleLayout;
import com.example.sakashun.alarmapplication.R;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    void AlarmStart(){
        Intent intent = new Intent(getApplication()
                ,com.example.sakashun.alarmapplication.AlarmConfig.class);
        startActivity(intent);
    }
    void ReminderStart(){
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
        Intent intent = new Intent(MainActivity.this,AlarmReceiver.class);
        //ntent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        //普通のintentと同じように、KEYとの組み合わせで値を受け渡しできるよ
        //intent.putExtra("KEY", 123);
        //ブロードキャストを投げるPendingIntentの作成
        PendingIntent sender = PendingIntent.getBroadcast(MainActivity.this, REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        //AlramManager取得
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        //AlramManagerにPendingIntentを登録
        am.set(AlarmManager.RTC_WAKEUP, init_alarm, sender);
    }
    void LockSet(){
        Intent intent = new Intent(getApplication()
                ,com.example.sakashun.alarmapplication.SetActivity.class);
        startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity_main);

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
                LockSet();
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

    /* 右上のSettingの描写および処理の部分
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    */

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_alarm) {
            // Handle the camera action
            Toast.makeText(MainActivity.this,"メニューで"+item.toString()+"が押されました", Toast.LENGTH_SHORT).show();
            AlarmStart();
        } else if (id == R.id.nav_reminder) {

            Toast.makeText(MainActivity.this,"メニューで"+item.toString()+"が押されました", Toast.LENGTH_SHORT).show();
            ReminderStart();
        } else if (id == R.id.nav_setting) {

            Toast.makeText(MainActivity.this,"メニューで"+item.toString()+"が押されました", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_help) {

            Toast.makeText(MainActivity.this,"メニューで"+item.toString()+"が押されました", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_version) {

            Toast.makeText(MainActivity.this,"メニューで"+item.toString()+"が押されました", Toast.LENGTH_SHORT).show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
