package com.example.sakashun.alarmapplication;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * Created by Saka Shun on 2016/09/04.
 */
public class AlarmTop extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarm_top);

        Button alarm_config_button = (Button) findViewById(R.id.alarm_config_button);
        //アラーム画面へのボタン
        alarm_config_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public  void onClick(View v){

            }
        });

    }
}
