package com.example.sakashun.alarmapplication.Rminder;

import android.app.Notification;
import android.content.Context;

import java.util.Calendar;

/**
 * Created by Saka Shun on 2016/10/22.
 */
public class ReminderSort {
    public int sort_number[] = new int[100];//ソート結果
    public int sort_kauz=0;//Sort後に何個表示するか
    long calendar_time[] = new long[100];//元の時間データ
    int level[]= new int[100];
    int reminder_kazu=0;
    Context context;


    private int PreInt(String a) {
        return Integer.parseInt(a);
    }

    public ReminderSort(Context applicationContext, String[] hiduke, String[] time, String[] r_level,int r_reminder_kazu) {
        context = applicationContext;
        reminder_kazu = r_reminder_kazu;
        for(int i=0;i<reminder_kazu;i++){
            String[] hiduke_strs = hiduke[i].split("/");
            String[] time_strs = time[i].split(":");
            //まず要素が０に近い側の時間を文字列から取り出す
            Calendar calendar = Calendar.getInstance();
            calendar.set(PreInt(hiduke_strs[0]),PreInt(hiduke_strs[1])-1,PreInt(hiduke_strs[2]),PreInt(time_strs[0]),PreInt(time_strs[1]));
            calendar_time[i] = calendar.getTimeInMillis();//ミリ秒であらわした時間を格納する
            level[i] = PreInt(r_level[i].split("レベル")[1]);//levelを格納する
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

    //今現在のより後のものだけにして時間順にする
    public void TimeSort(){
        sort_kauz = 0;
        for (int i=0;i<reminder_kazu;i++){
            //今より後のもののみ取り出す
            if (Calendar.getInstance().getTimeInMillis() < calendar_time[i]){
                sort_number[sort_kauz++] = i;
            }
        }
        //ソートの数を教えて時間順にソートさせる。
        TimeBubbleSort(0,sort_kauz,1);
    }

    //レベル順にソート
    public void LevelSort(){
        //今より後のもののみ取り出す
        sort_kauz = 0;
        for (int i=0;i<reminder_kazu;i++){
            if (Calendar.getInstance().getTimeInMillis() < calendar_time[i]){
                sort_number[sort_kauz++] = i;
            }
        }
        //レベル順にする
        boolean flag  = true;
        while(flag){
            flag = false;
            for(int i=0;i<sort_kauz-1;i++){
                //要素が0に近い方が後ろのより時間が大きい場合は入れ替える
                if(level[sort_number[i]]>level[sort_number[i+1]]){
                    flag = true;
                    int a = sort_number[i];
                    sort_number[i] = sort_number[i+1];
                    sort_number[i+1] = a;
                }
            }
        }
        //レベル順の中で時間順にする
        for(int i=0;i<5;i++){
            int level_top=-1,level_bottom=-1;
            for (int j=0;j<sort_kauz;j++){
                if(level[sort_number[j]] == i+1){
                    //このレベル「i」の範囲を調べる
                    level_top = level_top==-1 ? j : level_top;
                    level_bottom = j;
                }
            }
            if (level_top != -1 ){
                TimeBubbleSort(level_top,level_bottom-level_top,1);
            }
            System.out.println("レベル"+i+"ソート中");
        }
    }

    //過去のを新しい順にソート
    public void OldSort(){
        //過去もののみ取り出す
        sort_kauz = 0;
        for (int i=0;i<reminder_kazu;i++){
            if (Calendar.getInstance().getTimeInMillis() >= calendar_time[i]){
                sort_number[sort_kauz++] = i;
            }
        }
        //ソートの数を教えて時間順にソートさせる。
        TimeBubbleSort(0,sort_kauz,-1);
    }

    //今日のリマインダーのみを時間順にソート
    public void TodaySort(){
        sort_kauz = 0;
        Calendar calendar = Calendar.getInstance();
        calendar.set(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DATE));
        calendar.add(Calendar.DATE,1);//明日の日付にする
        for (int i=0;i<reminder_kazu;i++){
            //今より後のもののみ取り出す
            if (Calendar.getInstance().getTimeInMillis() < calendar_time[i] && calendar_time[i] <calendar.getTimeInMillis()){
                sort_number[sort_kauz++] = i;
            }
        }
        //ソートの数を教えて時間順にソートさせる。
        TimeBubbleSort(0,sort_kauz,1);

    }

}
