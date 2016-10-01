package com.example.sakashun.alarmapplication;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Switch;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.Calendar;
import java.io.IOException;

/**\data
 * Created by Saka Shun on 2016/09/05.
 */
public class AlarmEdit extends ActionBarActivity {

    int alarm_list_number=0;//アラームの番号
    EditText alarm_name;//アラームの名前(タイトル)
    TextView alarm_time_text;//アラームの時間
    RelativeLayout alarm_time_layout;//アラームの時間のレイアウト
    SeekBar volumeSeekbar;//音量シークバー
    Switch vibrator_switch;//バイブレーション
    RelativeLayout vibrator_layout;//バイブレーションのレイアウト
    Switch light_switch;//LEDの点減
    RelativeLayout light_layout;//LED点減のレイアウト
    MediaPlayer mp = new MediaPlayer();//アラーム曲の格納
    Uri music_uri=RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);//アラーム曲のUriフィールド
    private final static int MUSIC_FILE_CODE = 12345;// 曲選択intentの識別用のコード
    TextView music_name_text;//アラーム曲の名前
    RelativeLayout music_layout;//アラーム曲のレイアウト
    TextView sunuzu_text;//スヌーズの時間または文字のテキスト
    RelativeLayout sunuzu_layout;//スヌーズのレイアウト

    InputMethodManager inputMethodManager;// キーボード表示を制御するためのオブジェク
    private LinearLayout mainLayout;// 背景のレイアウト
    AudioManager am;// AudioManagerのフィールド
    int now_volume;//今現在の音量を格納する

    public void linkSet(){
        alarm_name = (EditText)findViewById(R.id.alarm_name);//アラームの名前(タイトル)
        alarm_time_text = (TextView)findViewById(R.id.alarm_time_text);//アラームの時間
        alarm_time_layout=(RelativeLayout)findViewById(R.id.alarm_time_layout);//アラームの時間のレイアウト
        volumeSeekbar = (SeekBar)findViewById(R.id.volumeSeekbar);//音量シークバー
        vibrator_switch = (Switch)findViewById(R.id.vibrator_switch);//バイブレーション
        vibrator_layout = (RelativeLayout)findViewById(R.id.vibrator_layout);//バイブレーションのレイアウト
        light_switch = (Switch)findViewById(R.id.light_switch);//LEDの点減
        light_layout = (RelativeLayout)findViewById(R.id.light_layout);//LED点減のレイアウト
        music_name_text = (TextView)findViewById(R.id.music_name_text);//アラーム曲の名前
        music_layout = (RelativeLayout)findViewById(R.id.music_layout);//アラーム曲のレイアウト
        sunuzu_text = (TextView)findViewById(R.id.sunuzu_text); //スヌーズの時間または文字のテキスト
        sunuzu_layout = (RelativeLayout)findViewById(R.id.sunuzu_layout); //スヌーズのレイアウト

        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);//キーボードのオブジェクト設定
        mainLayout = (LinearLayout) findViewById(R.id.setting_liner);//このアクティビティのレイアウト取得

        mp=MediaPlayer.create(this,music_uri);//アラームのデフォルトの曲をセット
        mp.setLooping(true);//リピート設定
        System.out.println("uriだよ　"+music_uri.toString());
        //↓曲名取得
        RingtoneManager manager = new RingtoneManager(AlarmEdit.this);// マネージャを作成
        manager.setType(RingtoneManager.TYPE_ALARM);//アラーム音のセット
        //カーソルを取得して、moveToNextしていく
        Cursor cursor = manager.getCursor();
        music_name_text.setText(cursor.getString(RingtoneManager.TITLE_COLUMN_INDEX));
        System.out.println("曲名:"+music_name_text.getText());
        while (cursor.moveToNext()) {
            String title = cursor.getString(RingtoneManager.TITLE_COLUMN_INDEX);    // 着信音などの名前
            System.out.println("曲名:" + title);
            /*
            String uriPrefix = cursor.getString(RingtoneManager.URI_COLUMN_INDEX);
            String index = cursor.getString(RingtoneManager.ID_COLUMN_INDEX);
            String uri = uriPrefix + "/" + index;                                   // ※URIはuriPrefixとindexをつなげる必要あり
           */
        }
        //Toast.makeText(this, "初期設定の曲の名前が取得できませんでした", Toast.LENGTH_SHORT).show();
        am = (AudioManager)getSystemService(Context.AUDIO_SERVICE);// AudioManagerを取得する
        now_volume = am.getStreamVolume(AudioManager.STREAM_MUSIC);//曲再生時の音量を取得
    }
    public void numberGet(){
        //アラームの番号取得
        Intent intent = getIntent();
        alarm_list_number = intent.getIntExtra("number",20);
        if(alarm_list_number==20){
            Toast.makeText(AlarmEdit.this,"編集ファイルのオープンに失敗", Toast.LENGTH_SHORT).show();
            alarm_name.setText("失敗");
            finish();
            return;
        }else {
            alarm_name.setText("アラーム" + (alarm_list_number + 1));
        }
    }
    public void alarm_make() {
        if (alarm_time_text.getText().toString().matches(".*:.*") == false) {
            Toast.makeText(AlarmEdit.this, "時間を設定してください", Toast.LENGTH_SHORT).show();
            return;
        }
        //フォーカスを背景にもっていく
        mainLayout.requestFocus();

        OutputStream out;
        try {
            out = openFileOutput("alarm_data" + alarm_list_number + ".txt", MODE_PRIVATE);
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(out, "UTF-8"));

            //追記する
            writer.append("name," + alarm_name.getText() + "\n");
            writer.append("time," + alarm_time_text.getText() + "\n");
            writer.append("volume," + volumeSeekbar.getProgress() + "\n");
            writer.append("vibrator," + vibrator_switch.isChecked() + "\n");
            writer.append("light," + light_switch.isChecked() + "\n");
            writer.append("music," + music_uri.toString() + "\n");
            writer.append("music_name," + music_name_text.getText() + "\n");
            writer.append("sunuzu," + sunuzu_text.getText() + "\n");
            writer.close();
        } catch (IOException e) {
            // TODO 自動生成された catch ブロック
            e.printStackTrace();
            Toast.makeText(AlarmEdit.this, "登録に失敗しました", Toast.LENGTH_SHORT).show();
            finish();
        }
        //無事保存に成功したら

        //現在のファイル内容を読み取る
        System.out.println("現在のアラームファイルを読み取る");
        int alarm_number_list[] = new int[20];//アラームの番号のチェックリスト
        Arrays.fill(alarm_number_list, 0);//0で初期化
        String alarm_time_list[] = new String[20];//時間の内容を入れる部分
        try {
            InputStream in = openFileInput("alarm_list_data.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            String s;
            while ((s = reader.readLine()) != null) {
                //現在のアラームの書いてある内容を読み取る
                if (s != "\n") {
                    //alarm_list_number= (int) Long.parseLong(s,0);
                    String[] strs = s.split(",");
                    for (int i = 0; i < strs.length; i++) {
                        System.out.println(String.format("分割後 %d 個目の文字列 -> %s", i + 1, strs[i]));
                    }
                    alarm_number_list[Integer.parseInt(strs[0])] = 1;//チェックしていく
                    alarm_time_list[Integer.parseInt(strs[0])] = strs[1];//時間を保存
                } else {
                    System.out.println("改行が入りました");
                }
            }
            reader.close();
        } catch (IOException e) {
            //もし番号の取得に失敗つまりは、最初だった場合はファイルだけ新しく作る
            e.printStackTrace();
            System.out.println("error code 1");
        }

        //現在のファイル内容をさっきのデータをもとに上書き
        System.out.println("アラーム管理ファイルを上書き");
        //今回の設定を加える
        alarm_number_list[alarm_list_number] = 1;
        alarm_time_list[alarm_list_number] = (String) alarm_time_text.getText();
        try {
            out = openFileOutput("alarm_list_data.txt", MODE_PRIVATE);
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(out, "UTF-8"));
            //追記する
            for (int i = 0; i < 20; i++) {
                if (alarm_number_list[i] == 1) {
                    writer.append(i + "," + alarm_time_list[i] + "\n");//管理ファイルに書き込んでいく
                }
            }
            writer.close();
        } catch (IOException ee) {
            // TODO 自動生成された catch ブロック
            ee.printStackTrace();
            System.out.println("error code 2");
            //Toast.makeText(AlarmEdit.this,"アラームリスト番号の更新に失敗", Toast.LENGTH_SHORT).show();
        }

        System.out.println("内容確認↓");
        try {
            InputStream in = openFileInput("alarm_data" + alarm_list_number + ".txt");
            //InputStream in = openFileInput("alarm_list_data.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            String s;
            while ((s = reader.readLine()) != null) {
                //内容をどんどん表示
                System.out.println(s);
            }
            reader.close();
        } catch (IOException e) {
            // TODO 自動生成された catch ブロッ
            e.printStackTrace();
            System.out.println("error code 3");
        }
        finish();
    }
    public void nameSet(){
        //アラームの名前のフォーカス設定
        alarm_name.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    //受け取った時
                    if(alarm_name.getText().toString().equals( ("アラーム"+ (alarm_list_number+1) ) ) ) {
                        alarm_name.setText("");
                    }
                }else{
                    //離れた時
                    System.out.println("離れフォーカスメソッド起動！");
                    if(alarm_name.getText().toString().equals("")) {
                        alarm_name.setText("アラーム" + (alarm_list_number + 1));
                    }else {
                        //空白のみで埋めていないか
                        String[] strs = alarm_name.getText().toString().split(" ");
                        boolean flag = true;
                        for (int i = 0; i < strs.length && flag; i++) {
                            if(!strs[i].matches("")){
                                flag = false;
                            }
                        }
                        if(flag) {
                            alarm_name.setText("アラーム" + (alarm_list_number + 1));
                        }
                    }
                    // キーボードを隠す
                    inputMethodManager.hideSoftInputFromWindow(mainLayout.getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
                }
            }
        });
        //EditTextにリスナーをセット（要はキーボードで決定した時に↑のフォーカス離れの機能が動くようにする文）
        alarm_name.setOnKeyListener(new View.OnKeyListener() {
            //コールバックとしてonKey()メソッドを定義
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                //イベントを取得するタイミングには、ボタンが押されてなおかつエンターキーだったときを指定
                if((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)){
                    // 背景にフォーカスを移す
                    mainLayout.requestFocus();
                    return true;
                }
                return false;
            }
        });
    }
    public void timeSet(){
        //アラーム時間のボタン
        alarm_time_layout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                TimePickerDialogFragment timePicker = new TimePickerDialogFragment();
                //timePicker.getShowsDialog();
                //アラームの時間を決める時に時間のダイアログを出す
                timePicker.show(getFragmentManager(), "timePicker");
            }
        });

    }
    public void seekbarSet(){
        //音量シークバー設定
        volumeSeekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(
                    SeekBar volumeSeekbar,
                    int progress,
                    boolean fromUser
            ) {
                // シークバーの入力値に変化が生じた段階で音量を変更
                mp.setVolume((float) progress/100f,(float) progress/100f);
                //System.out.println("シークバーの値は"+progress);
                if (!mp.isPlaying()) {
                    // MediaPlayerの再生
                    mp.seekTo(0); // プレイ中のBGMをスタート位置に戻す
                    mp.start();
                }
            }
            public void onStartTrackingTouch(SeekBar volumeSeekbar) {
                // トグル（シークバーのつまみ）がタッチされたときの動作
                am.setStreamVolume(AudioManager.STREAM_MUSIC,100,0);//音量をmaxにする
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
    public void vibrator_ledSet() {
        //追われたら反転するようにする
        vibrator_layout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                vibrator_switch.setChecked(!vibrator_switch.isChecked());
            }
        });
        light_layout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                light_switch.setChecked(!light_switch.isChecked());
            }
        });
    }
    public void musicSet(){
        //アラーム曲設定ボタン
        music_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //サウンドピッカー系を起動して受け取る
                //Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                //intent.setType("audio/*");
                //startActivityForResult(intent, MUSIC_FILE_CODE);

                //Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
                Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
                //intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT,false);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM);
                startActivityForResult(intent,MUSIC_FILE_CODE);
            }
        });
    }
    public void sunuxuSet(){
        //スヌーズ設定
        sunuzu_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String item_list[] = new String[] {
                        "なし",
                        "5分",
                        "10分",
                        "15分",
                        "20分",
                        "25分",
                        "30分" };

                int chec_number = -1;
                for (int i=0; i<7 && chec_number==-1; i++) {
                    if(sunuzu_text.getText().toString().matches(item_list[i]))
                        chec_number = i;
                }
                new AlertDialog.Builder(AlarmEdit.this)
                        //.setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("スヌーズ間隔")
                        .setSingleChoiceItems(item_list, chec_number, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                //⇒アイテムを選択した時のイベント処理
                                sunuzu_text.setText(item_list[whichButton]);
                                dialog.dismiss();
                            }
                        })
                        .show();
            }
        });
    }
    public void alarm_contentSet(){
        //各種内容を反映させる
        try {
            InputStream in = openFileInput("alarm_data"+ alarm_list_number+".txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            String s;
            System.out.println("中身を読み取り反映させる");
            while ((s = reader.readLine()) != null) {
                //現在のアラームの番号を受け取る
                System.out.println("中身は" + s + "←");
                //alarm_list_number= (int) Long.parseLong(s,0);
                String[] strs = s.split(",");
                if(strs[0].matches("name")){
                    alarm_name.setText(strs[1]);
                }
                else if(strs[0].matches("time")){
                    alarm_time_text.setText(strs[1]);
                    alarm_time_text.setTextSize(30);
                }
                else if(strs[0].matches("volume")){
                    volumeSeekbar.setProgress(Integer.parseInt(strs[1]));;
                }
                else if(strs[0].matches("vibrator")){
                    vibrator_switch.setChecked(strs[1].matches("true"));
                }
                else if(strs[0].matches("light")){
                    light_switch.setChecked(strs[1].matches("true"));
                }
                else if(strs[0].matches("music")){
                    music_uri = Uri.parse(strs[1]);
                    mp=MediaPlayer.create(this,music_uri);
                }
                else if(strs[0].matches("music_name")){
                    music_name_text.setText(strs[1]);//曲名取得
                }
                else if(strs[0].matches("sunuzu")){
                    sunuzu_text.setText(strs[1]);
                }
            }
            reader.close();
        }catch(IOException e){
            //アラーム内容の取得に失敗
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarm_setting_activity_main);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("アラーム変更");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        linkSet();//各フィールドとレイアウトをつなげる
        numberGet();//この新規アラームの番号を取得する

        alarm_contentSet();//編集画面専用メソッド

        nameSet();//アラームの名前系の内容
        timeSet();//アラームの時刻系の内容
        seekbarSet();//シークバーの内容
        vibrator_ledSet();//バイブとLEDの内容
        musicSet();//アラーム曲の内容
        sunuxuSet();//スヌーズの設定


    }

    // 画面タップ時の処理 フォーカスを背景に移すため(あんま意味ない)
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // キーボードを隠す
        inputMethodManager.hideSoftInputFromWindow(mainLayout.getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
        // 背景にフォーカスを移す
        mainLayout.requestFocus();
        return true;
    }

    //intentでの結果を受け取る
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MUSIC_FILE_CODE && resultCode == RESULT_OK) {
            // アラームの曲intentの結果の処理
            String filePath = "null";
            filePath = data.getDataString();
            System.out.println("ファイルパスだよ！　"+filePath);

            Uri uri = (Uri) data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
            if(uri!=null) {
                Cursor personCursor = managedQuery(uri, null, null, null, null);
                if (personCursor.moveToFirst()) {
                    int albumimageIndex =
                            personCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME);
                    String albumart = personCursor.getString(albumimageIndex);
                    String[] strs = albumart.split("\\.");//「 .」は特殊文字だから注意
                    music_name_text.setText(strs[0]);
                }else{
                    Toast.makeText(this, "senntakuした曲の名前が取得できませんでした", Toast.LENGTH_SHORT).show();
                }

                music_uri = (Uri) data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
                //alarm_music_list_button.setText( music_uri.toString() );
                System.out.println("ファイルパスだよ！２　" + (filePath = music_uri.toString()));
                music_uri = Uri.parse(filePath);
                mp = MediaPlayer.create(this, music_uri);
                //mp.start();
            }else {
                Toast.makeText(this, "その曲は設定できませんでした", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //時計ダイアログの処理
    @SuppressLint("ValidFragment")
    public class TimePickerDialogFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), this, hour, minute, true);

            return timePickerDialog;
        }

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            //時刻が選択されたときの処理
            //alarm_time.setFormat24Hour(String.valueOf(hourOfDay)+":"+String.format("%02d", minute));
            alarm_time_text.setText(String.format("%02d", hourOfDay)+":"+String.format("%02d", minute));
            alarm_time_text.setTextSize(30);
        }
    }


    // 右上の追加の部分
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.alarm_setting_main, menu);
        MenuItem item = menu.findItem(R.id.action_save);
        item.setTitle("保存");
        return true;
    }

    //ツールバーのボタンの内容
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        boolean result = true;

        switch (id) {
            case android.R.id.home:
                finish();
                break;
            case R.id.action_save:
                alarm_make();//追加(保存)ボタンの内容
                break;
            default:
                result = super.onOptionsItemSelected(item);
        }

        return result;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //もし曲がセットされていたら
        if (mp != null) {
            // 再生終了
            mp.stop();
            // リソースの解放
            mp.release();
        }
    }
}
