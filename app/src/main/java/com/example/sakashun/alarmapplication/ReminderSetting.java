package com.example.sakashun.alarmapplication;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.sakashun.alarmapplication.Rminder.ReminderAlarmController;
import com.example.sakashun.alarmapplication.Rminder.ReminderFileController;

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
public class ReminderSetting extends ActionBarActivity {

    EditText title_edit;//リマインダーの名前(タイトル)
    TextView hiduke_text;//リマインダーの日付
    RelativeLayout hiduke_layout;//リマインダーの時間のレイアウト
    TextView time_text;//リマインダーの時間
    RelativeLayout time_layout;//リマインダーの時間のレイアウト
    TextView level_text;//通知レベル
    RelativeLayout level_layout;//リマインダーの通知レベルのレイアウト
    EditText memo_content_edit;//リマインダーの名前(タイトル)

    InputMethodManager inputMethodManager;// キーボード表示を制御するためのオブジェク
    private LinearLayout mainLayout;// 背景のレイアウト
    AudioManager am;// AudioManagerのフィールド

    AlarmController alarmController;//アラームセットの準備
    //実際にセットしているのはalarm_makeの中の上書き直後

    ReminderFileController reminderFileController;

    int edit_number = -1;//編集中か判断する　-1＝新規。-1！=編集

    public void linkSet() {
        title_edit = (EditText) findViewById(R.id.titleText);//リマインダーの名前(タイトル)
        hiduke_text = (TextView) findViewById(R.id.hiduke_text);//リマインダーの日付
        hiduke_layout = (RelativeLayout) findViewById(R.id.hiduke_layout);//日付のレイアウト
        time_text = (TextView) findViewById(R.id.time_text);//リマインダーの時間
        time_layout = (RelativeLayout) findViewById(R.id.time_layout);//時間のレイアウト
        level_text = (TextView) findViewById(R.id.level_text);//リマインダーのレベル
        level_layout = (RelativeLayout) findViewById(R.id.level_layout);//レベルのレイアウト
        memo_content_edit = (EditText) findViewById(R.id.memo_content_edit);//リマインダーの内容

        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);//キーボードのオブジェクト設定
        mainLayout = (LinearLayout) findViewById(R.id.setting_liner);//このアクティビティのレイアウト取得

        reminderFileController = new ReminderFileController(getApplicationContext());
    }

    public void reminder_make() {
        if (time_text.getText().toString().matches(".*:.*") == false) {
            Toast.makeText(ReminderSetting.this, "時間を設定してください", Toast.LENGTH_SHORT).show();
            return;
        }
        //フォーカスを背景にもっていく
        mainLayout.requestFocus();

        //編集中ならばいちどどのデータを消す
        int my_numner = 0;
        if(edit_number!=-1){
            my_numner = reminderFileController.my_number[edit_number];
            reminderFileController.DeleteFile(edit_number);
        }

        reminderFileController.SaveFile(
                title_edit.getText().toString(),
                hiduke_text.getText().toString(),
                time_text.getText().toString(),
                level_text.getText().toString(),
                memo_content_edit.getText().toString(),
                my_numner);

        edit_number = reminderFileController.reminder_kazu-1;

        //通知はonデストロイでセット
        finish();
    }

    public void nameSet() {
        //アラームの名前のフォーカス設定
        title_edit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    //受け取った時
                    if (title_edit.getText().toString().equals("メモ")) {
                        title_edit.setText("");
                    }
                } else {
                    //離れた時
                    System.out.println("離れフォーカスメソッド起動！");
                    if (title_edit.getText().toString().equals("")) {
                        title_edit.setText("メモ");
                    } else {
                        //空白のみで埋めていないか
                        String[] strs = title_edit.getText().toString().split(" ");
                        boolean flag = true;
                        for (int i = 0; i < strs.length && flag; i++) {
                            if (!strs[i].matches("")) {
                                flag = false;
                            }
                        }
                        if (flag) {
                            title_edit.setText("メモ");
                        }
                    }
                    // キーボードを隠す
                    inputMethodManager.hideSoftInputFromWindow(mainLayout.getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
                }
            }
        });
        //EditTextにリスナーをセット（要はキーボードで決定した時に↑のフォーカス離れの機能が動くようにする文）
        title_edit.setOnKeyListener(new View.OnKeyListener() {
            //コールバックとしてonKey()メソッドを定義
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                //イベントを取得するタイミングには、ボタンが押されてなおかつエンターキーだったときを指定
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // 背景にフォーカスを移す
                    mainLayout.requestFocus();
                    return true;
                }
                return false;
            }
        });
    }

    public void hidukeSet() {
        final String strs[] = hiduke_text.getText().toString().split("/");
        //リマインダー日付のボタン
        hiduke_layout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                DatePickerDialog dateDialog = new DatePickerDialog(
                        ReminderSetting.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                hiduke_text.setText(year + "/" + (monthOfYear + 1) + "/" + dayOfMonth);
                            }
                        },
                        Integer.parseInt(strs[0]),         //textに入っている日付をセット
                        Integer.parseInt(strs[1])-1,
                        Integer.parseInt(strs[2])
                );
                dateDialog.show();
            }
        });

    }

    public void timeSet() {
        //リマインダー日付のボタン
        time_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String strs[] = time_text.getText().toString().split(":");
                // 時間選択ダイアログの生成
                TimePickerDialog timepick = new TimePickerDialog(
                        ReminderSetting.this,
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

    //レベルのトグルボタン設定
    public void levelSet() {
        //スヌーズ設定
        level_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String item_list[] = new String[]{
                        "レベル1",
                        "レベル2",
                        "レベル3",
                        "レベル4",
                        "レベル5"};

                int chec_number = -1;
                for (int i = 0; i < item_list.length && chec_number == -1; i++) {
                    if (level_text.getText().toString().matches(item_list[i]))
                        chec_number = i;
                }
                new AlertDialog.Builder(ReminderSetting.this)
                        //.setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("通知レベル")
                        .setSingleChoiceItems(item_list, chec_number, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                //⇒アイテムを選択した時のイベント処理
                                level_text.setText(item_list[whichButton]);
                                dialog.dismiss();
                            }
                        })
                        .show();
            }
        });
    }

    void defaultSet(){
        // 現在時刻を取得
        Calendar calendar = Calendar.getInstance();
        //10分後にする
        calendar.add(Calendar.MINUTE, 10);
        //今日の日付をセット
        hiduke_text.setText(calendar.get(Calendar.YEAR)+"/"+
                (calendar.get(Calendar.MONTH)+1)+"/"+
                calendar.get(Calendar.DAY_OF_MONTH));
        //今の時間を設定
        time_text.setText(String.format("%02d:%02d",calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE) ));
    }

    void editSet(){
        //各値をファイルからとってくる
        ReminderFileController reminderFileController = new ReminderFileController(ReminderSetting.this);
        reminderFileController.OpenFile();
        //タイトルをセット
        title_edit.setText(reminderFileController.name[edit_number]);
        //日付をセット
        hiduke_text.setText(reminderFileController.hiduke[edit_number]);
        //時間をセット
        time_text.setText(reminderFileController.time[edit_number]);
        //通知レベルをセット
        level_text.setText(reminderFileController.level[edit_number]);
        //内容をセット
        memo_content_edit.setText(reminderFileController.memo_content[edit_number]);
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reminder_setting_activity_main);

        //ツールバーの設定
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("リマインダーの追加");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        linkSet();//各フィールドとレイアウトをつなげる
        //編集で鈍できたか確認
        Intent intent = getIntent();
        if(intent.getBooleanExtra("Edit",false)){
            toolbar.setTitle("リマインダーの編集");
            if( (edit_number = intent.getIntExtra("number",-1)) == -1){
                finish();
            }
            System.out.println("受け取り番号 "+edit_number);
            if(edit_number > 101 || edit_number < -101){
                edit_number = reminderFileController.SearchDataNumber(edit_number);
                System.out.println("通知バーからの起動 "+edit_number);
            }
            editSet();
        }else{
            defaultSet();
        }
        nameSet();//アラームの名前系の内容
        hidukeSet();//アラームの日付系の内容
        timeSet();//アラームの時刻系の内容
        levelSet();//レベルの設定

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
                reminder_make();//追加(保存)ボタンの内容
                break;
            default:
                result = super.onOptionsItemSelected(item);
        }

        return result;
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();

        if(edit_number != -1) {
            //編集中、もしくは、保存後
            //通知表示実験
            ReminderAlarmController reminderAlarmController = new ReminderAlarmController(this);
            reminderAlarmController.AlarmOneSet(edit_number);
        }
    }


}
