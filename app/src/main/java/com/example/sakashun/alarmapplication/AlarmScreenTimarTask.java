package com.example.sakashun.alarmapplication;

import android.content.Context;
import android.os.Handler;

import java.util.TimerTask;

/**
 * Created by Saka Shun on 2016/10/03.
 */
public class AlarmScreenTimarTask extends TimerTask {
    private Handler handler;
    private Context context;
    int state = 0;

    public AlarmScreenTimarTask(Context context) {
        handler = new Handler();
        this.context = context;
    }

    public AlarmScreenTimarTask(Context context, int a) {
        handler = new Handler();
        this.context = context;
        state = a;
    }

    @Override
    public void run() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                switch (state) {
                    case 0://マッチングの定期更新
                        ((AlarmNotification) context).LayoutColorChange();
                        break;
                }
            }
        });

    }
}
