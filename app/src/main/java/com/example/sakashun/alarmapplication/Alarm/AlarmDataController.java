package com.example.sakashun.alarmapplication.Alarm;

import android.content.Context;

import com.example.sakashun.alarmapplication.R;
import com.example.sakashun.alarmapplication.Rminder.Pref;

/**
 * Created by Saka Shun on 2016/10/22.
 */


public class AlarmDataController {
    private final static int N_MAX =20;
    Context context = null;
    
    public String name[] = new String[N_MAX];
    public String time[] = new String[N_MAX];
    public int volume[] = new int[N_MAX];
    public boolean vibrator[] = new boolean[N_MAX];
    public boolean light[] = new boolean[N_MAX];
    public String music_uri[] = new String[N_MAX];
    public String music_name[] = new String[N_MAX];
    public String sunuzu[] = new String[N_MAX];
    public boolean onoff[] = new boolean[N_MAX];
    public int hindo[] = new int[N_MAX];
    
    public int alarm_kazu=0;

    public AlarmDataController(Context applicationContext) {
        context = applicationContext;
    }

    private String Gs(int string_id){
        return context.getString(string_id);
    }

    public boolean OpenFile(){
        alarm_kazu = new Pref().GetInt(context,Gs(R.string.alarm_list_name),Gs(R.string.alarm_list_number));
        for(int i=0;i<alarm_kazu;i++){
            name[i] = new Pref().GetString(context,( Gs(R.string.alarm_data_name)+i),"name");
            time[i] = new Pref().GetString(context,( Gs(R.string.alarm_data_name)+i),"time");
            volume[i] = new Pref().GetInt(context,( Gs(R.string.alarm_data_name)+i),"volume");
            vibrator[i] = new Pref().GetBoolean(context,( Gs(R.string.alarm_data_name)+i),"vibrator");
            light[i] = new Pref().GetBoolean(context,( Gs(R.string.alarm_data_name)+i),"light");
            music_uri[i] = new Pref().GetString(context,( Gs(R.string.alarm_data_name)+i),"music_uri");
            music_name[i] = new Pref().GetString(context,( Gs(R.string.alarm_data_name)+i),"music_name");
            sunuzu[i] = new Pref().GetString(context,( Gs(R.string.alarm_data_name)+i),"sunuzu");
            onoff[i] = new Pref().GetBoolean(context,( Gs(R.string.alarm_data_name)+i),"onoff");
            hindo[i] = new Pref().GetInt(context,( Gs(R.string.alarm_data_name)+i),"hindo");
            System.out.println(i+"番目の内容↓");
            System.out.println(name[i]+","+time[i]+","+volume[i]+","+vibrator[i]+","+light[i]+","+music_uri[i]+","+music_name[i]+","+sunuzu[i]+","+onoff[i]+","+hindo[i]);
        }
        return true;
    }

    public boolean DeleteFile(int number) {
        OpenFile();
        int write_count=0;
        for (int i = 0; i < alarm_kazu; i++) {
            if (number != i) {
                System.out.println(i + "番目の内容を保存↓");
                System.out.println(name[i]+","+time[i]+","+volume[i]+","+vibrator[i]+","+light[i]+","+music_uri[i]+","+music_name[i]+","+sunuzu[i]+","+onoff[i]+","+hindo[i]);
                new Pref(context, (Gs(R.string.alarm_data_name) + write_count), "name", name[i]);
                new Pref(context, (Gs(R.string.alarm_data_name) + write_count), "time", time[i]);
                new Pref(context, (Gs(R.string.alarm_data_name) + write_count), "volume", volume[i]);
                new Pref(context, (Gs(R.string.alarm_data_name) + write_count), "vibrator", vibrator[i]);
                new Pref(context, (Gs(R.string.alarm_data_name) + write_count), "light", light[i]);
                new Pref(context, (Gs(R.string.alarm_data_name) + write_count), "music_uri", music_uri[i]);
                new Pref(context, (Gs(R.string.alarm_data_name) + write_count), "music_name", music_name[i]);
                new Pref(context, (Gs(R.string.alarm_data_name) + write_count), "sunuzu", sunuzu[i]);
                new Pref(context, (Gs(R.string.alarm_data_name) + write_count), "onoff", onoff[i]);
                new Pref(context, (Gs(R.string.alarm_data_name) + write_count), "hindo", hindo[i]);
                write_count++;
            }
        }
        //ひとつへったことを記録
        new Pref(context,Gs(R.string.alarm_list_name), Gs(R.string.alarm_list_number),alarm_kazu-1 );
        return true;
    }

    public boolean SaveFile(String r_name,String r_time,int r_volume,boolean r_vibrator,boolean r_light, String r_music_uri ,String r_music_name,String r_sunuzu,boolean r_onoff,int r_hindo) {
        OpenFile();
        if(alarm_kazu >= N_MAX) {//これ以上保存できない
            return false;
        }
        System.out.println(alarm_kazu + "番目に内容を追加↓");
        System.out.println(r_name + "," + r_time + "," + r_volume + "," + r_vibrator+ "," + r_light+ "," + r_music_uri+ "," + r_music_name+ "," + r_sunuzu+ "," + r_onoff+ "," + r_hindo);
        new Pref(context, (Gs(R.string.alarm_data_name) + alarm_kazu), "name", r_name);
        new Pref(context, (Gs(R.string.alarm_data_name) + alarm_kazu), "time", r_time);
        new Pref(context, (Gs(R.string.alarm_data_name) + alarm_kazu), "volume", r_volume);
        new Pref(context, (Gs(R.string.alarm_data_name) + alarm_kazu), "vibrator", r_vibrator);
        new Pref(context, (Gs(R.string.alarm_data_name) + alarm_kazu), "light", r_light);
        new Pref(context, (Gs(R.string.alarm_data_name) + alarm_kazu), "music_uri", r_music_uri);
        new Pref(context, (Gs(R.string.alarm_data_name) + alarm_kazu), "music_name", r_music_name);
        new Pref(context, (Gs(R.string.alarm_data_name) + alarm_kazu), "sunuzu", r_sunuzu);
        new Pref(context, (Gs(R.string.alarm_data_name) + alarm_kazu), "onoff", r_onoff);
        new Pref(context, (Gs(R.string.alarm_data_name) + alarm_kazu), "hindo", r_hindo);
        //ひとつ増えたことを記録
        alarm_kazu++;
        new Pref(context,Gs(R.string.alarm_list_name), Gs(R.string.alarm_list_number),alarm_kazu );
        return true;
    }

    //それぞれのアラームがオンかオフかを変更するメソッド
    public boolean OnoffCheced(int number,boolean r_onoff){
        new Pref(context, (Gs(R.string.alarm_data_name) + number), "onoff", r_onoff);               //保存
        onoff[number] = new Pref().GetBoolean(context,( Gs(R.string.alarm_data_name)+number),"onoff");  //読み込み
        return  true;
    }

    //使用回数をカウントするメソッド
    public boolean UseCount(int number){
        int now_count = new Pref().GetInt(context,( Gs(R.string.alarm_data_name)+number),"hindo");  //今までのカウントの読み込み
        new Pref(context, (Gs(R.string.alarm_data_name) + number), "hindo", (int)(now_count+1));//保存
        return  true;
    }
}
