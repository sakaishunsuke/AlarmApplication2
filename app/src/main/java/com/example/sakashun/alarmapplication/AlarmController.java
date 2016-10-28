package com.example.sakashun.alarmapplication;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.example.sakashun.alarmapplication.Alarm.AlarmDataController;

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

    Context context;
    AlarmDataController alarmDataController = null;
    /*
    private class AlarmFile{
        boolean open_chec = false;//ファイルが無事に開けたか確認
        boolean alarm_chec = false;//アラームがon設定になっているか
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
                        if(strs[2]!=null && strs[2].matches("true")){
                            alarm_chec = true;
                        }
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
                System.out.println("ファイルが開けませんでした");
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
    */

    public AlarmController(Context c){
        context = c;
        alarmDataController = new AlarmDataController(c);
    }

    public boolean AlarmOneSet(int number) {
        //もし受けっとていなければ自分で開くファイルを開く
        alarmDataController.OpenFile();

        //アラームを識別するコード、任意なので重複しない好きな数値を設定
        //int REQUEST_CODE = 140625;

        //まず現在の時刻を取得する
        Calendar cal = Calendar.getInstance();  //オブジェクトの生成

        //アラーム用のカレンダーを用意する
        Calendar alarm_cal = Calendar.getInstance();  //オブジェクトの生成
        alarm_cal.set(Calendar.HOUR_OF_DAY,
                Integer.parseInt(alarmDataController.time[number].split(":")[0]));
        alarm_cal.set(Calendar.MINUTE,
                Integer.parseInt(alarmDataController.time[number].split(":")[1]));
        alarm_cal.set(Calendar.SECOND, 0);
        alarm_cal.set(Calendar.MILLISECOND, 0);

        if(cal.getTimeInMillis()>=alarm_cal.getTimeInMillis()){
            //現在の時間がアラーム時間より後の時、一日後にする
            alarm_cal.add(Calendar.DATE,1);
        }

        //設定された日時を表示
        System.out.println("アラームがセットされました↓");
        System.out.println(alarm_cal.get(Calendar.YEAR)+"/"+(alarm_cal.get(Calendar.MONTH)+1)+"/"+alarm_cal.get(Calendar.DATE));
        System.out.println(alarm_cal.get(Calendar.HOUR_OF_DAY)+":"+alarm_cal.get(Calendar.MINUTE));

        //指定の時間になったら起動するクラス
        Intent intent = new Intent(context,AlarmReceiver.class);
        //ntent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

        //個体番号をintentに記録する
        new IntentPutNumber(intent,number,alarmDataController);
        //固有番号に変える
        number = alarmDataController.my_number[number];

        //ブロードキャストを投げるPendingIntentの作成
        PendingIntent sender = PendingIntent.getBroadcast(context, number, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        //AlramManager取得
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        //AlramManagerにPendingIntentを登録
        am.set(AlarmManager.RTC_WAKEUP, alarm_cal.getTimeInMillis(), sender);

        //個体番号から、元のファイル番号に戻る
        number = alarmDataController.SearchDataNumber(number);
        if(alarmDataController.OnoffCheced(number,true) && alarmDataController.onoff[number]){
            //アラームのonoffの保存場所ここでしかonのきろくをしない
            return true;
        }else{
            return false;
        }
    }

    public boolean AlarmOneCancel(int number) {
        System.out.println("アラームのキャンセル処理をします");

        //もし受けっとていなければ自分で開くファイルを開く
        alarmDataController.OpenFile();


        // 不要になった過去のアラームを削除する
        // requestCodeを0から登録していたとする
        Intent intent = new Intent(context,AlarmReceiver.class);

        //固有番号に変える
        number = alarmDataController.my_number[number];

        //ブロードキャストを投げるPendingIntentの作成
        PendingIntent sender = PendingIntent.getBroadcast(context, number, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        //AlramManager取得
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        sender.cancel();
        am.cancel(sender);

        //個体番号から、元のファイル番号に戻る
        number = alarmDataController.SearchDataNumber(number);
        if(alarmDataController.OnoffCheced(number,false) && !alarmDataController.onoff[number]){
            //アラームのonoffの保存場所ここでしかoffのきろくをしない
            return true;
        }else{
            return false;
        }
    }
    public boolean AlarmChec(int number){
        //もし受けっとていなければ自分で開くファイルを開く
        alarmDataController.OpenFile();
        return alarmDataController.onoff[number];
    }
    public boolean AlarmSunuzuSet(int number,int sunuzu_time) {

        //アラームを識別するコード、任意なので重複しない好きな数値を設定
        //int REQUEST_CODE = 140625;

        //まず現在の時刻を取得する
        Calendar cal = Calendar.getInstance();  //オブジェクトの生成
        long time = cal.getTimeInMillis();

        //アラーム用のカレンダーを用意する
        Calendar alarm_cal = Calendar.getInstance();  //オブジェクトの生成
        long alarm_time = alarm_cal.getTimeInMillis() + sunuzu_time * 60 * 1000;
        alarm_cal.setTimeInMillis(alarm_time);

        //設定された日時を表示

        //設定された日時を表示
        System.out.println("アラームのスヌーズがセットされました↓");
        System.out.println(alarm_cal.get(Calendar.YEAR)+"/"+(alarm_cal.get(Calendar.MONTH)+1)+"/"+alarm_cal.get(Calendar.DATE));
        System.out.println(alarm_cal.get(Calendar.HOUR_OF_DAY)+":"+alarm_cal.get(Calendar.MINUTE));

        //指定の時間になったら起動するクラス
        Intent intent = new Intent(context,AlarmReceiver.class);
        //ntent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

        //個体番号をintentに記録する
        new IntentPutNumber(intent,number,alarmDataController);
        //固有番号に変える
        number = alarmDataController.my_number[number];

        //ブロードキャストを投げるPendingIntentの作成
        PendingIntent sender = PendingIntent.getBroadcast(context, number, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        //AlramManager取得
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        //AlramManagerにPendingIntentを登録
        am.set(AlarmManager.RTC_WAKEUP, alarm_time, sender);
        //Toast.makeText(context,alarm_file.hour+":"+alarm_file.minute+"にセットしました", Toast.LENGTH_SHORT).show();

        //個体番号から、元のファイル番号に戻る
        number = alarmDataController.SearchDataNumber(number);
        if(alarmDataController.OnoffCheced(number,true) && !alarmDataController.onoff[number]){
            //アラームのonoffの保存場所ここでしかonのきろくをしない
            return true;
        }else{
            return false;
        }
    }



}
