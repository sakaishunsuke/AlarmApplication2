package com.example.sakashun.alarmapplication;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sakashun.alarmapplication.Rminder.Pref;
import com.example.sakashun.alarmapplication.Rminder.ReminderAlarmController;
import com.example.sakashun.alarmapplication.Rminder.ReminderFileController;
import com.example.sakashun.alarmapplication.Rminder.ReminderSort;

public class ReminderConfigActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    int sort_tipy=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reminder_config_activity_main);

        NavigationView navigationView = (NavigationView) findViewById(R.id.reminder_nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorReminderFloatingActionButton)));
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplication(),com.example.sakashun.alarmapplication.ReminderSetting.class));
            }
        });
    }

    //右上のSettingの描写および処理の部分
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
        //各レベルのせてチ画面へ飛ぶ
        if (id == R.id.action_settings) {
            Intent intent = new Intent(getApplication()
                    ,com.example.sakashun.alarmapplication.Rminder.LevelActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
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

    @Override
    public void onStart(){
        super.onStart();
        reminder_list_make();
    }

    void ToolberTitelSet(String name){
        Toolbar toolbar;
        toolbar= (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(name);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
    }

    public void reminder_list_make(){
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

        //titleを変更
        switch (sort_tipy){
            case 0:
                ToolberTitelSet(getString(R.string.reminder_config_title_time));
                reminderSort.TimeSort();
                break;
            case 1:
                ToolberTitelSet(getString(R.string.reminder_config_title_level));
                reminderSort.LevelSort();
                break;
            case 2:
                ToolberTitelSet(getString(R.string.reminder_config_title_old));
                reminderSort.OldSort();
                break;
        }

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
            incLayout = (LinearLayout) inflater.inflate(R.layout.reminder_config_list, null);

            //各部分のアクセスの処理を書く
            final TextView list_time = (TextView) incLayout.findViewById(R.id.list_time);//アラーム時間
            final TextView list_name = (TextView) incLayout.findViewById(R.id.list_name);//アラーム名
            RelativeLayout reminder_list_layout = (RelativeLayout) incLayout.findViewById(R.id.reminder_list_layout);//ひな形全体のレイアウト

            String s[] = reminderFileController.hiduke[number].split("/");
            list_time.setText(s[1]+"/"+s[2]+" "+reminderFileController.time[number]);//時間のセット
            list_name.setText(reminderFileController.name[number]);//名前のセット

            // 背景色を変更
            //斜め部分の準備
            int level_number = Integer.parseInt(reminderFileController.level[number].split("レベル")[1]);
            Drawable bgShape = (Drawable)incLayout.findViewById(R.id.view3).getBackground();
            bgShape.setColorFilter(new Pref().GetInt(getApplicationContext(),("level_"+level_number),"PriColor"), PorterDuff.Mode.SRC_IN);
            incLayout.findViewById(R.id.view1).setBackgroundColor(new Pref().GetInt(getApplicationContext(),("level_"+level_number),"PriColor"));
            incLayout.findViewById(R.id.view2).setBackgroundColor(new Pref().GetInt(getApplicationContext(),("level_"+level_number),"PriColor"));
            reminder_list_layout.setBackgroundColor(new Pref().GetInt(getApplicationContext(),("level_"+level_number),"SecoColor"));


            //タッチされてアラームがonoffになるようにする
            reminder_list_layout.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    //いったん通知のセットを消す
                    new ReminderAlarmController(ReminderConfigActivity.this).AlarmOneCancel(number);
                    //編集への処理
                    Intent intent = new Intent(getApplication()
                            ,com.example.sakashun.alarmapplication.ReminderSetting.class);
                    intent.putExtra("Edit",true);
                    intent.putExtra("number",number);
                    startActivity(intent);

                }
            });
            reminder_list_layout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    // 確認ダイアログの生成
                    final AlertDialog.Builder alertDlg = new AlertDialog.Builder(ReminderConfigActivity.this);
                    alertDlg.setTitle(list_name.getText());
                    alertDlg.setMessage(reminderFileController.memo_content[number]);
                    alertDlg.setPositiveButton(
                            "消去",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    //通知のセットを消す
                                    new ReminderAlarmController(ReminderConfigActivity.this).AlarmOneCancel(number);
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
            mainLayout.addView(incLayout);//追加したlogを反映させる
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_alarm) {
            finish();
            // Handle the camera action
            Intent intent = new Intent(getApplication()
                    ,com.example.sakashun.alarmapplication.AlarmConfig.class);
            startActivity(intent);
        }
        else if (id == R.id.nav_bay_reminder) {
            sort_tipy = 0;
            reminder_list_make();
        } else if (id == R.id.nav_level_reminder) {
            sort_tipy = 1;
            reminder_list_make();
        } else if (id == R.id.nav_old_reminder) {
            sort_tipy = 2;
            reminder_list_make();
        }

        else if (id == R.id.nav_setting) {

        } else if (id == R.id.nav_help) {

        } else if (id == R.id.nav_version) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}
