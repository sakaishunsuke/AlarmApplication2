package com.example.sakashun.alarmapplication.Alarm;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
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
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.sakashun.alarmapplication.AlarmController;
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

/**\data
 * Created by Saka Shun on 2016/09/05.
 */
public class AlarmSetting extends ActionBarActivity{

    EditText name;//アラームの名前(タイトル)
    TextView time_text;//アラームの時間
    RelativeLayout alarm_time_layout;//アラームの時間のレイアウト
    SeekBar volumeSeekbar;//音量シークバー
    Switch vibrator_switch;//バイブレーション
    RelativeLayout vibrator_layout;//バイブレーションのレイアウト
    Switch light_switch;//LEDの点減
    RelativeLayout light_layout;//LED点減のレイアウト
    MediaPlayer mp = new MediaPlayer();//アラーム曲の格納
    Uri music_uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);//アラーム曲のUriフィールド
    private final static int MUSIC_FILE_CODE = 12345;// 曲選択intentの識別用のコード
    TextView music_name_text;//アラーム曲の名前
    RelativeLayout music_layout;//アラーム曲のレイアウト
    TextView sunuzu_text;//スヌーズの時間または文字のテキスト
    RelativeLayout sunuzu_layout;//スヌーズのレイアウト

    InputMethodManager inputMethodManager;// キーボード表示を制御するためのオブジェク
    private LinearLayout mainLayout;// 背景のレイアウト
    AudioManager am;// AudioManagerのフィールド
    int now_volume;//今現在の音量を格納する

    AlarmController alarmController;//アラームセットの準備
    //実際にセットしているのはalarm_makeの中の上書き直後

    AlarmDataController alarmDataController;//アラームのデータ管理クラス

    int edit_number = -1;//編集中か判断する　-1＝新規。-1！=編集

    public void linkSet() {
        name = (EditText) findViewById(R.id.alarm_name);//アラームの名前(タイトル)
        time_text = (TextView) findViewById(R.id.alarm_time_text);//アラームの時間
        alarm_time_layout = (RelativeLayout) findViewById(R.id.alarm_time_layout);//アラームの時間のレイアウト
        volumeSeekbar = (SeekBar) findViewById(R.id.volumeSeekbar);//音量シークバー
        vibrator_switch = (Switch) findViewById(R.id.vibrator_switch);//バイブレーション
        vibrator_layout = (RelativeLayout) findViewById(R.id.vibrator_layout);//バイブレーションのレイアウト
        light_switch = (Switch) findViewById(R.id.light_switch);//LEDの点減
        light_layout = (RelativeLayout) findViewById(R.id.light_layout);//LED点減のレイアウト
        music_name_text = (TextView) findViewById(R.id.music_name_text);//アラーム曲の名前
        music_layout = (RelativeLayout) findViewById(R.id.music_layout);//アラーム曲のレイアウト
        sunuzu_text = (TextView) findViewById(R.id.sunuzu_text); //スヌーズの時間または文字のテキスト
        sunuzu_layout = (RelativeLayout) findViewById(R.id.sunuzu_layout); //スヌーズのレイアウト

        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);//キーボードのオブジェクト設定
        mainLayout = (LinearLayout) findViewById(R.id.setting_liner);//このアクティビティのレイアウト取得

        am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);// AudioManagerを取得する
        now_volume = am.getStreamVolume(AudioManager.STREAM_MUSIC);//曲再生時の音量を取得

        alarmDataController = new AlarmDataController(AlarmSetting.this);//データ管理クラスを使用できるようにする
    }

    void defaultSet(){
        // 現在時刻を取得
        Calendar calendar = Calendar.getInstance();
        //10分後にする
        //calendar.add(Calendar.MINUTE, 10);
        //今の時間を設定
        time_text.setText(String.format("%02d:%02d",calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE) ));
        
        mp = MediaPlayer.create(this, music_uri);//アラームのデフォルトの曲をセット
        mp.setLooping(true);//リピート設定
        System.out.println("uriだよ　" + music_uri.toString());
        //↓曲名取得
        RingtoneManager manager = new RingtoneManager(AlarmSetting.this);// マネージャを作成
        manager.setType(RingtoneManager.TYPE_ALARM);//アラーム音のセット
        //カーソルを取得して、moveToNextしてい
        Cursor cursor = manager.getCursor();
        music_name_text.setText(cursor.getString(RingtoneManager.TITLE_COLUMN_INDEX));
        System.out.println("曲名:" + music_name_text.getText());
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
    }
    
    void editSet(){
        //各値をファイルからとってくる
        AlarmDataController alarmDataController = new AlarmDataController(AlarmSetting.this);
        alarmDataController.OpenFile();
        //タイトルをセット
        name.setText(alarmDataController.name[edit_number]);
        //時間をセット
        time_text.setText(alarmDataController.time[edit_number]);
        volumeSeekbar.setProgress(alarmDataController.volume[edit_number]);
        vibrator_switch.setChecked(alarmDataController.vibrator[edit_number]);
        light_switch.setChecked(alarmDataController.light[edit_number]);
        music_uri = Uri.parse(alarmDataController.music_uri[edit_number]);
            mp=MediaPlayer.create(this,music_uri);
        music_name_text.setText(alarmDataController.music_name[edit_number]);
        sunuzu_text.setText(alarmDataController.sunuzu[edit_number]);
        
    }

    void alarm_make() {
        if (time_text.getText().toString().matches(".*:.*") == false) {
            Toast.makeText(AlarmSetting.this, "時間を設定してください", Toast.LENGTH_SHORT).show();
            return;
        }
        //フォーカスを背景にもっていく
        mainLayout.requestFocus();

        int hindo = 0;
        if(edit_number != -1){
            hindo = alarmDataController.hindo[edit_number];
            alarmDataController.DeleteFile(edit_number);
        }
        alarmDataController.SaveFile(
                name.getText().toString(),
                time_text.getText().toString(),
                volumeSeekbar.getProgress(),
                vibrator_switch.isChecked(),
                light_switch.isChecked(),
                music_uri.toString(),
                music_name_text.getText().toString(),
                sunuzu_text.getText().toString(),
                false,
                hindo
        );
        //無事保存に成功したら

        //アラームのセット
        alarmController = new AlarmController(AlarmSetting.this);
        alarmController.AlarmOneSet(alarmDataController.alarm_kazu-1);

        finish();
    }

    //名前のフォーカス時の処理
    public void nameSet(){
        //アラームの名前のフォーカス設定
        name.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    //受け取った時
                    if(name.getText().toString().equals("アラーム") ) {
                        name.setText("");
                    }
                }else{
                    //離れた時
                    System.out.println("離れフォーカスメソッド起動！");
                    if(name.getText().toString().equals("")) {
                        name.setText("アラーム");
                    }else {
                        //空白のみで埋めていないか
                        String[] strs = name.getText().toString().split(" ");
                        boolean flag = true;
                        for (int i = 0; i < strs.length && flag; i++) {
                            if(!strs[i].matches("")){
                                flag = false;
                            }
                        }
                        if(flag) {
                            name.setText("アラーム");
                        }
                    }
                    // キーボードを隠す
                    inputMethodManager.hideSoftInputFromWindow(mainLayout.getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
                }
            }
        });
        //EditTextにリスナーをセット（要はキーボードで決定した時に↑のフォーカス離れの機能が動くようにする文）
        name.setOnKeyListener(new View.OnKeyListener() {
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

    //時計ダイアログの処理
    public void timeSet() {
        //リマインダー日付のボタン
        alarm_time_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String strs[] = time_text.getText().toString().split(":");
                // 時間選択ダイアログの生成
                TimePickerDialog timepick = new TimePickerDialog(
                        AlarmSetting.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            public void onTimeSet(TimePicker view,int hourOfDay, int minute) {
                                // 設定 ボタンクリック時の処理
                                //時刻が選択されたときの処理
                                time_text.setText(String.format("%02d", hourOfDay) + ":" + String.format("%02d", minute));
                            }
                        },
                        Integer.parseInt(strs[0]),  //textに入っている日付をセット
                        Integer.parseInt(strs[1]),
                        true);
                timepick.show();// 表示
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

    public void vibrator_ledSet() {
        //おされたら反転するようにする
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
                new AlertDialog.Builder(AlarmSetting.this)
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarm_setting_activity_main);
        
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("アラーム追加");
        setSupportActionBar(toolbar);
        
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        
        linkSet();//各フィールドとレイアウトをつなげる
        //編集できたかチェック
        Intent intent = getIntent();
        if(intent.getBooleanExtra("Edit",false) && (edit_number = intent.getIntExtra("number",-1)) != -1){
            toolbar.setTitle("アラームの編集");
            editSet();
        }else{
            defaultSet();
        }
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

    // 右上の追加の部分
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.alarm_setting_main, menu);
        MenuItem item = menu.findItem(R.id.action_save);
        if(edit_number == -1){
            item.setTitle("追加");
        }else{
            item.setTitle("保存");
        }
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
