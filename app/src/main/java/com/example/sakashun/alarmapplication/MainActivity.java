package com.example.sakashun.alarmapplication;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button alarm_button = (Button) findViewById(R.id.alarm_button);
        Button calender_button= (Button) findViewById(R.id.calendar_button);
        Button config_button = (Button) findViewById(R.id.application_config_button);

        //アラーム画面へのボタン
        alarm_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public  void onClick(View v){

            }
        });

        //カレンダー画面へのボタン
        calender_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public  void onClick(View v){

            }
        });

        //アプリ設定画面へのボタン
        config_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public  void onClick(View v){

            }
        });

    }
}
