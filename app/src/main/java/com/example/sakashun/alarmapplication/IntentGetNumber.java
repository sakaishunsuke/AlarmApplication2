package com.example.sakashun.alarmapplication;

import android.content.Intent;

import com.example.sakashun.alarmapplication.Alarm.AlarmDataController;
import com.example.sakashun.alarmapplication.Rminder.ReminderFileController;

/**
 * Created by Saka Shun on 2016/10/28.
 */
public class IntentGetNumber {
    Intent intent;
    ReminderFileController reminderFileController = null;
    AlarmDataController alarmDataController =null;

    public IntentGetNumber(Intent i,ReminderFileController r){
        intent = i;
        reminderFileController = r;
    }
    public IntentGetNumber(Intent i,AlarmDataController a){
        intent = i;
        alarmDataController = a;
    }
    public int GetNumber(){
        int number = intent.getIntExtra("Number",-1);
        if(number > 1000 || number < -1000) {
            if(alarmDataController != null) {
                number = alarmDataController.SearchDataNumber(number);
            }else if(reminderFileController != null){
                number = reminderFileController.SearchDataNumber(number);
            }
        }
        return number;
    }
}
