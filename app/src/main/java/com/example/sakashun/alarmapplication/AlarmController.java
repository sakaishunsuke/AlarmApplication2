package com.example.sakashun.alarmapplication;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Calendar;

/**
 * Created by Saka Shun on 2016/10/02.
 */
public class AlarmController{

    private class AlarmFile{
        boolean open_chec = false;//ファイルが無事に開けたか確認
        //アラームの時間を保存する変数を宣言数
        int hour;       //アラームの時
        int minute;     //アラームの分
        String alarm_file_data[] = new String[20];
        int alarm_list_value = 0;

        public AlarmFile(Context context, int number) {
            try{
                InputStream in = context.openFileInput("alarm_list_data.txt");
                BufferedReader reader = new BufferedReader(new InputStreamReader(in,"UTF-8"));
                String s;
                while((s = reader.readLine())!= null) {
                    //渡されたアラームの番号の時間を探す
                    String[] strs = s.split(",");
                    if(Integer.parseInt(strs[0])==number){
                        open_chec = true;//見つかったので開けたというようにしておく
                        String[] time = strs[1].split(":");
                        hour = Integer.parseInt(time[0]);//時を保存
                        minute = Integer.parseInt(time[1]);//分を保存
                    }
                    //上書きするときの元データを保存する
                    alarm_file_data[alarm_list_value++] = s;

                }
                reader.close();
            }catch(IOException e){
                //もし番号の取得に失敗つまりは、最初だった場合はファイルだけ新しく作る
                e.printStackTrace();
                open_chec = false;
            }
        }
        boolean OpenChec(){
            return open_chec;
        }
        boolean AlarmSetSava(Context context,int number){
            //次に、numberの部分をonに内容を上書きする
            try {
                OutputStream out = context.openFileOutput("alarm_list_data.txt",context.MODE_PRIVATE);
                PrintWriter writer = new PrintWriter(new OutputStreamWriter(out,"UTF-8"));
                //追記する
                for(int i=0;i<alarm_list_value;i++) {
                    String[] strs = alarm_file_data[i].split(",");
                    if(Integer.parseInt(strs[0])==number){//numberのときは保存内容を変える
                        writer.append(strs[0] + ","+strs[1]+","+"true"+"\n");//内容を書き込む
                    }else {
                        writer.append(alarm_file_data[i] + "\n");//内容を書き込む
                    }
                }
                writer.close();
            } catch (IOException ee) {
                // TODO 自動生成された catch ブロック
                ee.printStackTrace();
                return false;
            }
            return true;
        }
        boolean AlarmCancelSave(Context context,int number){
            //次に、numberの部分をonに内容を上書きする
            try {
                OutputStream out = context.openFileOutput("alarm_list_data.txt",context.MODE_PRIVATE);
                PrintWriter writer = new PrintWriter(new OutputStreamWriter(out,"UTF-8"));
                //追記する
                for(int i=0;i<alarm_list_value;i++) {
                    String[] strs = alarm_file_data[i].split(",");
                    if(Integer.parseInt(strs[0])==number){//numberのときは保存内容を変える
                        writer.append(strs[0] + ","+strs[1]+","+"false"+"\n");//内容を書き込む
                    }else {
                        writer.append(alarm_file_data[i] + "\n");//内容を書き込む
                    }
                }
                writer.close();
            } catch (IOException ee) {
                // TODO 自動生成された catch ブロック
                ee.printStackTrace();
                return false;
            }
            return true;
        }
    }





    public boolean AlarmOneSet(Context context,int number) {
        //ファイルを開く
        AlarmFile alarm_file = new AlarmFile(context,number);
        if(alarm_file.OpenChec()==false){//しっかりと開けたか確認
            return false;
        }

        //アラームを識別するコード、任意なので重複しない好きな数値を設定
        //int REQUEST_CODE = 140625;

        //まず現在の時刻を取得する
        Calendar cal = Calendar.getInstance();  //オブジェクトの生成
        int year = cal.get(Calendar.YEAR);        //現在の年を取得
        int month = cal.get(Calendar.MONTH);  //現在の月を取得
        int day = cal.get(Calendar.DATE);         //現在の日を取得
        long time = cal.getTimeInMillis();

        //アラーム用のカレンダーを用意する
        Calendar alarm_cal = Calendar.getInstance();  //オブジェクトの生成
        alarm_cal.set(year,month,day,alarm_file.hour,alarm_file.minute,0);
        int alarm_year = alarm_cal.get(Calendar.YEAR);        //アラームの年を取得
        int alarm_month = alarm_cal.get(Calendar.MONTH);  //アラームの月を取得
        int alarm_day = alarm_cal.get(Calendar.DATE);         //アラームの日を取得
        long alarm_time = alarm_cal.getTimeInMillis();

        if(time>alarm_time){
            //現在の時間がアラーム時間より後の時　日付などを変える
            if(cal.getActualMaximum(Calendar.MONTH)<alarm_day+1){
                alarm_day = 1;
                if(12<alarm_month+1){
                    alarm_month = 1;
                    alarm_year++;
                }else{
                    alarm_month++;
                }
            }else{
                alarm_day++;
            }
            //再度セットしなおす
            alarm_cal.set(alarm_year,alarm_month,alarm_day,alarm_file.hour,alarm_file.minute);
            alarm_time = alarm_cal.getTimeInMillis();
        }

        //設定された日時を表示
        System.out.println("アラームがセットされました");
        System.out.println(alarm_year+"/"+(alarm_month+1)+"/"+alarm_day);
        System.out.println(alarm_file.hour+":"+alarm_file.minute);

        //指定の時間になったら起動するクラス
        Intent intent = new Intent(context,AlarmReceiver.class);
        //ntent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        //普通のintentと同じように、KEYとの組み合わせで値を受け渡しできるよ
        intent.putExtra("NUMBER",number);
        //ブロードキャストを投げるPendingIntentの作成
        PendingIntent sender = PendingIntent.getBroadcast(context, number, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        //AlramManager取得
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        //AlramManagerにPendingIntentを登録
        am.set(AlarmManager.RTC_WAKEUP, alarm_time, sender);

        if(alarm_file.AlarmSetSava(context,number) == false) {
            System.out.println("アラームがセット保存に失敗キャンセル処理をします");
            if (AlarmOneCancel(context, number) == false) {//一応キャンセル処理をする
                //キャンセル処理も失敗
                Toast.makeText(context,"エラー発生\n今すぐ強制終了とデータ消去を行ってください", Toast.LENGTH_SHORT).show();
            }
            return false;
        }
        //Toast.makeText(context,alarm_file.hour+":"+alarm_file.minute+"にセットしました", Toast.LENGTH_SHORT).show();
        return true;
    }
    public boolean AlarmOneCancel(Context context,int number) {
        System.out.println("アラームのキャンセル処理をします");

        //ファイルを開く
        AlarmFile alarm_file = new AlarmFile(context,number);
        if(alarm_file.OpenChec()==false){//しっかりと開けたか確認
            return false;
        }

        // 不要になった過去のアラームを削除する
        // requestCodeを0から登録していたとする
        Intent intent = new Intent(context,AlarmReceiver.class);
        //ブロードキャストを投げるPendingIntentの作成
        PendingIntent sender = PendingIntent.getBroadcast(context, number, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        //AlramManager取得
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        sender.cancel();
        am.cancel(sender);

        if(alarm_file.AlarmCancelSave(context,number) == false) {
            System.out.println("アラームのキャンセル処理に失敗");
            Toast.makeText(context,"ScreenError", Toast.LENGTH_SHORT).show();
            return false;
        }
        //Toast.makeText(context,"キャンセルしました", Toast.LENGTH_SHORT).show();
        return true;
    }
    /*
    public boolean Alarm_all_set() {

        boolean flg = true;
        for(int i=0;i<20;i++){
            if(Alarm_one_set(i)==false){
                flg = false;
            }
        }
        return flg;
    }
    public boolean Alarm_all_cancel() {
        boolean flg = true;
        for(int i=0;i<20;i++){
            if(Alarm_one_cancel(i)==false){
                flg = false;
            }
        }
        return flg;
    }
    public int Alarm_chec() {
        return 0;
    }
    */

}
