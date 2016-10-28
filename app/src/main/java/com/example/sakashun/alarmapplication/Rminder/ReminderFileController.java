package com.example.sakashun.alarmapplication.Rminder;

import android.content.Context;

import com.example.sakashun.alarmapplication.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.Stack;

/**
 * Created by Saka Shun on 2016/10/22.
 */
public class ReminderFileController {
    private final static int N_MAX =100;
    Context context = null;
    public String name[] = new String[100];
    public String hiduke[] = new String[100];
    public String time[] = new String[100];
    public String level[] = new String[100];
    public String memo_content[] = new String[100];
    public int reminder_kazu=0;
    public int my_number[] = new int[N_MAX];

    public ReminderFileController(Context applicationContext) {
        context = applicationContext;
        OpenFile();
    }

    /*public boolean OpenFile(){
        try{
            InputStream in = context.openFileInput(context.getString(R.string.reminder_file_name));
            BufferedReader reader = new BufferedReader(new InputStreamReader(in,"UTF-8"));
            String s;
            for(int i=0;(s = reader.readLine())!= null;i++){
                System.out.println("中身は" + s + "←");
                //,で区切る
                String[] strs = s.split(",");
                System.out.println(",の数" + strs.length + "←");
                name[i] = strs[0];
                hiduke[i] = strs[1];
                time[i] = strs[2];
                level[i] = strs[3];
                if(strs.length > 4) {
                    memo_content[i] = strs[4];
                }else{
                    memo_content[i] = "";
                }
                reminder_kazu = i+1;
            }
            reader.close();
            return true;
        }catch(IOException e){
            //もし番号の取得に失敗つまりは、最初だった場合はファイルだけ新しく作る
            e.printStackTrace();
            return false;
        }
    }*/

    private String Gs(int string_id){
        return context.getString(string_id);
    }

    public boolean OpenFile(){
        reminder_kazu = new Pref().GetInt(context,Gs(R.string.reminder_list_name),Gs(R.string.reminder_list_number));
        for(int i=0;i<reminder_kazu;i++){
            name[i] = new Pref().GetString(context,( Gs(R.string.reminder_data_name)+i),"name");
            hiduke[i] = new Pref().GetString(context,( Gs(R.string.reminder_data_name)+i),"hiduke");
            time[i] = new Pref().GetString(context,( Gs(R.string.reminder_data_name)+i),"time");
            level[i] = new Pref().GetString(context,( Gs(R.string.reminder_data_name)+i),"level");
            memo_content[i] = new Pref().GetString(context,( Gs(R.string.reminder_data_name)+i),"memo_content");
            my_number[i] = new Pref().GetInt(context,(Gs(R.string.reminder_data_name)+i),"my_number");
            System.out.println(i+"番目の内容↓");
            System.out.println(name[i]+","+hiduke[i]+","+time[i]+","+level[i]+","+memo_content[i]+","+my_number[i]);
        }
        return true;
    }

    /*public boolean DeleteFile(int number){
        try {
            OutputStream out = context.openFileOutput(context.getString(R.string.reminder_file_name),context.MODE_PRIVATE);
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(out,"UTF-8"));
            //書き込む
            for(int i=0;i<reminder_kazu;i++) {
                if(i!=number){//numberのとき以外は保存
                    writer.append(name[i]+","+hiduke[i]+","+time[i]+","+level[i]+","+memo_content[i]+ "\n");//内容を書き込む
                }
            }
            writer.close();
            return true;
        } catch (IOException e) {
            // TODO 自動生成された catch ブロック
            e.printStackTrace();
            return false;
        }
    }*/
    public boolean DeleteFile(int number) {
        OpenFile();
        int write_count=0;
        for (int i = 0; i < reminder_kazu; i++) {
            if (number != i) {
                System.out.println(write_count + "番目の内容を保存↓");
                System.out.println(name[i]+","+hiduke[i]+","+time[i]+","+level[i]+","+memo_content[i]+","+my_number[i]);
                new Pref(context, (Gs(R.string.reminder_data_name) + write_count), "name", name[i]);
                new Pref(context, (Gs(R.string.reminder_data_name) + write_count), "hiduke", hiduke[i]);
                new Pref(context, (Gs(R.string.reminder_data_name) + write_count), "time", time[i]);
                new Pref(context, (Gs(R.string.reminder_data_name) + write_count), "level", level[i]);
                new Pref(context, (Gs(R.string.reminder_data_name) + write_count), "memo_content", memo_content[i]);
                new Pref(context, (Gs(R.string.reminder_data_name) + write_count), "my_number", my_number[i]);
                write_count++;
            }
        }
        //ひとつへったことを記録
        new Pref(context,Gs(R.string.reminder_list_name), Gs(R.string.reminder_list_number),reminder_kazu-1 );
        OpenFile();
        return true;
    }

    /*public boolean SaveFile(String r_name,String r_hiduke,String r_time,String r_level,String r_memo_content){
        try {
            OutputStream out = context.openFileOutput(context.getString(R.string.reminder_file_name),context.MODE_APPEND);
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(out,"UTF-8"));
            //書き込む
            writer.append(r_name+","+r_hiduke+","+r_time+","+r_level+","+r_memo_content+ "\n");//内容を書き込む
            writer.close();
            return true;
        } catch (IOException e) {
            // TODO 自動生成された catch ブロック
            e.printStackTrace();
            return false;
        }
    }*/
    public boolean SaveFile(String r_name,String r_hiduke,String r_time,String r_level,String r_memo_content,int r_my_number) {
        OpenFile();
        System.out.println(reminder_kazu + "番目に内容を追加↓");
        System.out.println(r_name + "," + r_hiduke + "," + r_time + "," + r_level + "," + r_memo_content);
        new Pref(context, (Gs(R.string.reminder_data_name) + reminder_kazu ), "name", r_name);
        new Pref(context, (Gs(R.string.reminder_data_name) + reminder_kazu ), "hiduke", r_hiduke);
        new Pref(context, (Gs(R.string.reminder_data_name) + reminder_kazu ), "time", r_time);
        new Pref(context, (Gs(R.string.reminder_data_name) + reminder_kazu ), "level", r_level);
        new Pref(context, (Gs(R.string.reminder_data_name) + reminder_kazu ), "memo_content", r_memo_content);

        if(r_my_number == 0) {
            int now_time = (int) ((Calendar.getInstance().getTimeInMillis() / 1000) & 0xffffffff);
            new Pref(context, (Gs(R.string.reminder_data_name) + reminder_kazu), "my_number", now_time);
        }else{
            new Pref(context, (Gs(R.string.reminder_data_name) + reminder_kazu), "my_number", r_my_number);
        }
        //ひとつ増えたことを記録
        new Pref(context,Gs(R.string.reminder_list_name), Gs(R.string.reminder_list_number),reminder_kazu+1 );
        OpenFile();
        return true;
    }

    public int SearchDataNumber(int search_number){//時間によって割り振った番号から探す
        OpenFile();
        for (int i=0;i<reminder_kazu;i++){
            if(my_number[i]==search_number){
                return i;
            }
        }
        return -1;
    }
}
