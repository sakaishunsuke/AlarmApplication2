package com.example.sakashun.alarmapplication.Alarm;

import android.content.Context;

import java.util.Calendar;

/**
 * Created by Saka Shun on 2016/10/22.
 */
public class AlarmSort {
    public int sort_number[] = new int[100];//ソート結果
    public int sort_kauz=0;//Sort後に何個表示するか
    long calendar_time[] = new long[100];//元の時間データ
    int hindo[] = new int[100];//使用頻度データ
    int alarm_kazu=0;
    Context context;


    private int PreInt(String a) {
        return Integer.parseInt(a);
    }

    public AlarmSort(Context applicationContext,String[] time,int[] r_hindo, int r_alarm_kazu) {
        context = applicationContext;
        alarm_kazu = r_alarm_kazu;
        for(int i=0;i<alarm_kazu;i++){
            String[] time_strs = time[i].split(":");
            //まず要素が０に近い側の時間を文字列から取り出す
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY,PreInt(time_strs[0]));
            calendar.set(Calendar.MINUTE,PreInt(time_strs[1]));
            calendar_time[i] = calendar.getTimeInMillis();//ミリ秒であらわした時間を格納する
            hindo[i] = r_hindo[i];
            sort_number[i]=i;
        }
    }

    //バブルソートで昇順にする
    private void TimeBubbleSort(int top_number,int sortnokazu ,int back){
        boolean flag  = true;
        while(flag){
            flag = false;
            for(int i=top_number;i<top_number+sortnokazu-1;i++){
                //要素が0に近い方が後ろのより時間が大きい場合は入れ替える
                if(calendar_time[sort_number[i]] * back >calendar_time[sort_number[i+1]] * back){
                    flag = true;
                    int a = sort_number[i];
                    sort_number[i] = sort_number[i+1];
                    sort_number[i+1] = a;
                }
            }
        }
    }

    //時間順にする
    public void TimeSort(){
        sort_kauz = alarm_kazu;
        //ソートの数を教えて時間順にソートさせる。
        TimeBubbleSort(0,sort_kauz,1);
    }
    
    //今日のリマインダーのみを時間順にソート
    public void UsedSort(){
        sort_kauz = 0;
        for (int i=0;i<alarm_kazu;i++){
            //使用したことが去るのを取り出す
            if (hindo[i] > 0){
                sort_number[sort_kauz++] = i;
            }
        }
        //使用回数の多い順にソートさせる。
        boolean flag  = true;
        while(flag){
            flag = false;
            for(int i=0;i<sort_kauz-1;i++){
                //要素が0に近い方が後ろのより時間が大きい場合は入れ替える
                if(hindo[sort_number[i]]  < calendar_time[sort_number[i+1]]){
                    flag = true;
                    int a = sort_number[i];
                    sort_number[i] = sort_number[i+1];
                    sort_number[i+1] = a;
                }
            }
        }
    }

}
