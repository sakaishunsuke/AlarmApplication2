package com.example.sakashun.alarmapplication;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
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
import com.example.sakashun.alarmapplication.R;

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

    void AlarmStart(){
        Intent intent = new Intent(getApplication()
                ,com.example.sakashun.alarmapplication.AlarmConfig.class);
        startActivity(intent);
    }

    void alarm_list_make(){
        //Toast.makeText(this, "リスト作成", Toast.LENGTH_LONG).show();
        //アラームの番号一覧取得
        int number_list[] = new int[20];
        String time_list[] = new String[20];
        int up_count_list[] = new int[20];
        Arrays.fill(number_list, 0);//0で初期化
        try{
            InputStream in = openFileInput("alarm_list_data.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(in,"UTF-8"));
            String s;
            while((s = reader.readLine())!= null) {
                //現在のアラームの番号を受け取る
                //alarm_list_number= (int) Long.parseLong(s,0);
                String[] strs = s.split(",");
                if(strs.length==4 && Integer.parseInt(strs[3]) != 0){
                    number_list[Integer.parseInt(strs[0])] = 1;//チェックしていく
                    time_list[Integer.parseInt(strs[0])] = strs[1];//時間を保存
                    up_count_list[Integer.parseInt(strs[0])] = Integer.parseInt(strs[3]);//使われた回数を保存
                }
            }
            reader.close();
        }catch(IOException e){
            //もし番号の取得に失敗つまりは、最初だった場合はファイルだけ新しく作る
            e.printStackTrace();
            System.out.println("アラーム一覧ファイルがありません");
        }


        //時間の若い順番にソートする(バブルかな)
        int kosuu=0;
        int aru_list[] = new int[20];
        for(int i=0;i<20;i++){
            if(number_list[i]==1){
                aru_list[kosuu++]=i;
            }
        }
        boolean flag  = true;
        while(flag){
            flag = false;
            for(int i=1;i<kosuu;i++){
                //回数をそのまま移す
                int front_time = up_count_list[i-1];
                int back_time = up_count_list[i];

                //要素が0に近い方が後ろのより時間が大きい場合は入れ替える
                if(front_time>back_time){
                    flag = true;
                    int a = aru_list[i-1];
                    aru_list[i-1] = aru_list[i];
                    aru_list[i] = a;
                }
            }
        }


        //LayoutInflaterの準備
        LinearLayout incLayout = null;
        LinearLayout mainLayout = (LinearLayout) findViewById(R.id.alarm_list_linear);
        //重複をなくすために、一度全部消去
        mainLayout.removeAllViews();

        for(int i=0;i<kosuu && i<3;i++) {
            final int number = aru_list[i];

            //log_text.xmlの内容をメインレイアウトのlog_listに１つ追加
            final LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            //LayoutInfkaterからViewのインスタンスを取得
            incLayout = (LinearLayout) inflater.inflate(R.layout.home_alarm_list, null);

            //各部分のアクセスの処理を書く
            final TextView list_time = (TextView) incLayout.findViewById(R.id.list_time);//アラーム時間
            final TextView list_name = (TextView) incLayout.findViewById(R.id.list_name);//アラーム名
            LinearLayout alarm_list_layout = (LinearLayout) incLayout.findViewById(R.id.alarm_list_layout);//ひな形全体のレイアウト

            String vibrator = null;
            String led = null;
            String sunuzu = null;

            list_time.setText(time_list[number]);//時間のセット
            try {
                InputStream in = openFileInput("alarm_data" + number + ".txt");
                BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
                String s;
                while ((s = reader.readLine()) != null) {
                    //現在のアラームの番号を受け取る
                    System.out.println("中身は" + s + "←");
                    //alarm_list_number= (int) Long.parseLong(s,0);
                    String[] strs = s.split(",");
                    if (strs[0].matches("name")) {
                        list_name.setText(strs[1]);
                    }
                }
                reader.close();
            } catch (IOException e) {
                //もし番号の取得に失敗つまりは、最初だった場合はファイルだけ新しく作る
                e.printStackTrace();
                System.out.println("アラームデータ" + number + "がありません");
            }

            //タッチされてアラームがonoffになるようにする
            alarm_list_layout.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    AlarmController alarmController = new AlarmController();
                    if(!alarmController.AlarmOneCancel(MainActivity.this,number))//アラームをセットをいったん消去
                        System.out.println("編集前のいったん消去に失敗");
                    // 編集 ボタンクリック処理
                    Intent intent = new Intent(getApplication()
                            ,com.example.sakashun.alarmapplication.AlarmEdit.class);
                    intent.putExtra("number",number);
                    startActivity(intent);

                }
            });


            mainLayout.addView(incLayout);//追加したlogを反映させる
        }
    }

    void ReminderStart(){
        startActivity(new Intent(getApplication(),com.example.sakashun.alarmapplication.ReminderConfigActivity.class));
    }

    void LockSet(){
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

        //アラームのリスト作成(0~3)
        alarm_list_make();

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
