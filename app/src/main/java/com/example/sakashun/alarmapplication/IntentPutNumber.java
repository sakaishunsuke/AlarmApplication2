package com.example.sakashun.alarmapplication;

import android.content.Intent;

import com.example.sakashun.alarmapplication.Alarm.AlarmDataController;
import com.example.sakashun.alarmapplication.Rminder.ReminderFileController;

/**
 * Created by Saka Shun on 2016/10/28.
 */
public class IntentPutNumber {
    public IntentPutNumber(Intent intent, int number,AlarmDataController alarmDataController) {
        if(number < 1000 && number > -1000) {
            number = alarmDataController.my_number[number];
        }
        intent.putExtra("Number",number);
    }
    public IntentPutNumber(Intent intent, int number,ReminderFileController reminderFileController) {
        if(number < 1000 && number > -1000) {
            number = reminderFileController.my_number[number];
        }
        intent.putExtra("Number",number);
    }
}
