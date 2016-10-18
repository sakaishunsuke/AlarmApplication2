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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

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
                //アラームの番号取得
                int chec_list[] = new int[20];//アラームの番号のチェックリスト
                try {
                    InputStream in = openFileInput("alarm_list_data.txt");
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
                    String s;
                    Arrays.fill(chec_list, 0);//0で初期化
                    while ((s = reader.readLine()) != null) {
                        //現在のアラームの番号を受け取る
                        System.out.println("中身は" + s + "←");
                        String[] strs = s.split(",");
                        chec_list[Integer.parseInt(strs[0])] = 1;//チェックしていく
                    }
                    reader.close();
                }catch(IOException e){
                    //もし番号の取得に失敗つまりは、最初だった場合はファイルだけ新しく作る
                    e.printStackTrace();
                    OutputStream out;
                    try {
                        out = openFileOutput("alarm_list_data.txt",MODE_PRIVATE);
                        PrintWriter writer = new PrintWriter(new OutputStreamWriter(out,"UTF-8"));
                        //追記する
                        writer.close();
                    } catch (IOException ee) {
                        // TODO 自動生成された catch ブロック
                        ee.printStackTrace();
                        Toast.makeText(AlarmConfig.this,"アラーム番号の初回設定に失敗", Toast.LENGTH_SHORT).show();
                    }
                }
                int i = 0;
                while (i < 20 && chec_list[i] != 0) {
                    i++;//まだ使われていないのを探す。
                }
                if (i < 20) {
                    Intent intent = new Intent(getApplication()
                            ,com.example.sakashun.alarmapplication.AlarmSetting.class);
                    startActivity(intent);
                }else{
                    //20個溜まっていたら
                    Toast.makeText(AlarmConfig.this,"20個までです", Toast.LENGTH_SHORT).show();
                }

            }
        });


    }

    @Override
    public void onStart(){
        super.onStart();
        alarm_list_make();
    }

    public void alarm_list_make(){
        //Toast.makeText(this, "リスト作成", Toast.LENGTH_LONG).show();
        //アラームの番号一覧取得
        int number_list[] = new int[20];
        String time_list[] = new String[20];
        boolean alarm_set_chec[] = new boolean[20];
        Arrays.fill(number_list, 0);//0で初期化
        try{
            InputStream in = openFileInput("alarm_list_data.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(in,"UTF-8"));
            String s;
            while((s = reader.readLine())!= null) {
                //現在のアラームの番号を受け取る
                System.out.println("中身は" + s + "←");
                //alarm_list_number= (int) Long.parseLong(s,0);
                String[] strs = s.split(",");
                for (int i = 0; i < strs.length; i++) {
                    System.out.println(String.format("分割後 %d 個目の文字列 -> %s", i + 1, strs[i]));
                }
                number_list[Integer.parseInt(strs[0])] = 1;//チェックしていく
                time_list[Integer.parseInt(strs[0])] = strs[1];//時間を保存
                if(strs.length==3){
                    alarm_set_chec[Integer.parseInt(strs[0])] = (strs[2].matches("true"));
                }else{
                    alarm_set_chec[Integer.parseInt(strs[0])] = false;
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
                //まず要素が０に近い側の時間を文字列から取り出す
                String[] front_strs = time_list[aru_list[i-1]].split(":");
                String[] back_strs = time_list[aru_list[i]].split(":");
                //時間を分で計算
                int front_time = Integer.parseInt(front_strs[0])*60 + Integer.parseInt(front_strs[1]);
                int back_time = Integer.parseInt(back_strs[0])*60 + Integer.parseInt(back_strs[1]);

                //System.out.println("時間チェック！　"+aru_list[i-1]+":"+front_time+"　"+aru_list[i]+":"+back_time);

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


        for(int i=0;i<kosuu;i++) {
            final int number = aru_list[i];

            //log_text.xmlの内容をメインレイアウトのlog_listに１つ追加
            final LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            //LayoutInfkaterからViewのインスタンスを取得
            incLayout = (LinearLayout) inflater.inflate(R.layout.alarm_config_list, null);

            //各部分のアクセスの処理を書く
            final TextView list_time = (TextView) incLayout.findViewById(R.id.list_time);//アラーム時間
            final TextView list_name = (TextView) incLayout.findViewById(R.id.list_name);//アラーム名
            RelativeLayout alarm_list_layout = (RelativeLayout) incLayout.findViewById(R.id.alarm_list_layout);//ひな形全体のレイアウト
            final Switch alarm_list_switch = (Switch)incLayout.findViewById(R.id.alarm_list_switch);//アラームonoffスイッチ
            alarm_list_switch.setChecked(alarm_set_chec[number]);//onoffの状態を反映させる



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
                        } else if (strs[0].matches("vibrator")) {
                            vibrator = (strs[1].matches("true")) ? "ON" : "OFF";
                        } else if (strs[0].matches("light")) {
                            led = (strs[1].matches("true")) ? "ON" : "OFF";
                        } else if (strs[0].matches("sunuzu")) {
                            sunuzu = (strs[1]);
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
                    alarmController = new AlarmController();
                    if(alarm_list_switch.isChecked()==false) {
                        //チェックがonにしたならアラームをセット
                        if(alarmController.AlarmOneSet(AlarmConfig.this, number)){
                            //無事にセットできたらチェックボタンを変更
                            alarm_list_switch.setChecked(true);
                        }
                    }else {
                        if(alarmController.AlarmOneCancel(AlarmConfig.this, number)){
                            //無事にキャンセルできたらチェックボタンを変更
                            alarm_list_switch.setChecked(false);
                        }
                    }
                }
            });

            final String finalVibrator = vibrator;
            final String finalLed = led;
            final String finalSunuzu = sunuzu;

            final Context alarm_config_context = AlarmConfig.this;//ダイアログ中にconxtを渡すため

            alarm_list_layout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    // 確認ダイアログの生成
                    final AlertDialog.Builder alertDlg = new AlertDialog.Builder(AlarmConfig.this);
                    alertDlg.setTitle(list_name.getText());
                    alertDlg.setMessage("アラーム時間　　　　" + "\t" + list_time.getText()+ "\n" +
                            "バイブレーション　　" + "\t"  + finalVibrator + "\n" +
                            "LEDの点減　　　　　 " + "\t"  + finalLed + "\n" +
                            "スヌーズ　　　　　　" + "\t"  + finalSunuzu + "\n");
                    alertDlg.setPositiveButton(
                            "消去",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // 消去 ボタンクリック処理
                                    alarmController = new AlarmController();
                                    if(!alarmController.AlarmOneCancel(alarm_config_context,number)) {//アラームの消去
                                        System.out.println("消去に失敗　番号:" + number);
                                        return;//アラームの消去に失敗したので表示を消さない
                                    }
                                    //まずはもともと書いてある内容から消したい番号以外を読み取る
                                    String[] copy = new String[20];
                                    int list_count = 0;
                                    try{
                                        InputStream in = openFileInput("alarm_list_data.txt");
                                        BufferedReader reader = new BufferedReader(new InputStreamReader(in,"UTF-8"));
                                        String s;
                                        while((s = reader.readLine())!= null) {
                                            //現在のアラームの番号を受け取る
                                            //alarm_list_number= (int) Long.parseLong(s,0);
                                            String[] strs = s.split(",");
                                            if (Integer.parseInt(strs[0])!=number) {
                                                copy[list_count++]  = s;//消去する番号以外の内容を保存
                                            }
                                        }
                                        reader.close();
                                    }catch(IOException e){
                                        //ファイルオープンに失敗
                                        e.printStackTrace();
                                        System.out.println("error code 1");
                                    }
                                    //次に、保存した内容を上書きする
                                    try {
                                        OutputStream out = openFileOutput("alarm_list_data.txt",MODE_PRIVATE);
                                        PrintWriter writer = new PrintWriter(new OutputStreamWriter(out,"UTF-8"));
                                        //追記する
                                        for(int i=0;i<list_count;i++) {
                                            writer.append(copy[i] + "\n");//保存した内容を書き込む
                                        }
                                        writer.close();
                                    } catch (IOException ee) {
                                        // TODO 自動生成された catch ブロック
                                        ee.printStackTrace();
                                        System.out.println("error code 2");
                                    }
                                    alarm_list_make();//再起させてリストを作り直す
                                }
                            });
                    alertDlg.setNegativeButton(
                            "編集",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    alarmController = new AlarmController();
                                    if(!alarmController.AlarmOneCancel(alarm_config_context,number))//アラームをセットをいったん消去
                                            System.out.println("編集前のいったん消去に失敗");
                                    // 編集 ボタンクリック処理
                                    Intent intent = new Intent(getApplication()
                                            ,com.example.sakashun.alarmapplication.AlarmEdit.class);
                                    intent.putExtra("number",number);
                                    startActivity(intent);
                                }
                            });
                    // 表示
                    alertDlg.create().show();
                    return true;//ここをfalesにするとタップの処理も実行される
                }
            });
            /*
            ImageButton config_button = (ImageButton) incLayout.findViewById(R.id.list_config_button);
            config_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplication()
                            ,com.example.sakashun.alarmapplication.AlarmEdit.class);
                    intent.putExtra("number",number);
                    startActivity(intent);

                }
            });
            config_button.setOnLongClickListener(new View.OnLongClickListener(){
                @Override
                public boolean onLongClick(View v) {
                    //長押しは消去ボタンに設定
                    // 確認ダイアログの生成
                    AlertDialog.Builder alertDlg = new AlertDialog.Builder(AlarmConfig.this);
                    alertDlg.setTitle("アラームの消去");
                    alertDlg.setMessage(list_name.getText()+"を消しますか？");
                    alertDlg.setPositiveButton(
                            "実行",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // OK ボタンクリック処理
                                    //まずはもともと書いてある内容から消したい番号以外を読み取る
                                    String[] copy = new String[20];
                                    int list_count = 0;
                                    try{
                                        InputStream in = openFileInput("alarm_list_data.txt");
                                        BufferedReader reader = new BufferedReader(new InputStreamReader(in,"UTF-8"));
                                        String s;
                                        while((s = reader.readLine())!= null) {
                                            //現在のアラームの番号を受け取る
                                            //alarm_list_number= (int) Long.parseLong(s,0);
                                            String[] strs = s.split(",");
                                            if (Integer.parseInt(strs[0])!=number) {
                                                copy[list_count++]  = s;//消去する番号以外の内容を保存
                                            }
                                        }
                                        reader.close();
                                    }catch(IOException e){
                                        //ファイルオープンに失敗
                                        e.printStackTrace();
                                        System.out.println("error code 1");
                                    }
                                    //次に、保存した内容を上書きする
                                    try {
                                        OutputStream out = openFileOutput("alarm_list_data.txt",MODE_PRIVATE);
                                        PrintWriter writer = new PrintWriter(new OutputStreamWriter(out,"UTF-8"));
                                        //追記する
                                        for(int i=0;i<list_count;i++) {
                                            writer.append(copy[i] + "\n");//保存した内容を書き込む
                                        }
                                        writer.close();
                                    } catch (IOException ee) {
                                        // TODO 自動生成された catch ブロック
                                        ee.printStackTrace();
                                        System.out.println("error code 2");
                                    }
                                    alarm_list_make();//再起させてリストを作り直す
                                }
                            });
                    alertDlg.setNegativeButton(
                            "戻る",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // Cancel ボタンクリック処理
                                }
                            });
                    // 表示
                    alertDlg.create().show();
                    return true;
                }
            });
            */
            mainLayout.addView(incLayout);//追加したlogを反映させる
        }
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
            Toast.makeText(this,"メニューで"+item.toString()+"が押されました", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_reminder) {

            Toast.makeText(this,"メニューで"+item.toString()+"が押されました", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_setting) {

            Toast.makeText(this,"メニューで"+item.toString()+"が押されました", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_help) {

            Toast.makeText(this,"メニューで"+item.toString()+"が押されました", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_version) {

            Toast.makeText(this,"メニューで"+item.toString()+"が押されました", Toast.LENGTH_SHORT).show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}
