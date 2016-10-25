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
import java.util.Stack;

/**
 * Created by Saka Shun on 2016/10/22.
 */
public class ReminderFileController {
    Context context = null;
    public String name[] = new String[100];
    public String hiduke[] = new String[100];
    public String time[] = new String[100];
    public String level[] = new String[100];
    public String memo_content[] = new String[100];
    public int reminder_kazu=0;

    public ReminderFileController(Context applicationContext) {
        context = applicationContext;
    }

    public boolean OpenFile(){
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
    }

    public boolean DeleteFile(int number){
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
    }

    public boolean SaveFile(String r_name,String r_hiduke,String r_time,String r_level,String r_memo_content){
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
    }
}
