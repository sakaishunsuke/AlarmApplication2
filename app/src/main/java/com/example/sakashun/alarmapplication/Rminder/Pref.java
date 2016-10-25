package com.example.sakashun.alarmapplication.Rminder;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Saka Shun on 2016/10/23.
 */
public class Pref {
    public Pref(Context context,String gul,String key,int a){
        SharedPreferences pref =
                context.getSharedPreferences(gul,context.MODE_WORLD_READABLE|context.MODE_WORLD_WRITEABLE);
        SharedPreferences.Editor e = pref.edit();
        e.putInt(key,a);
        e.commit();
    }
    public Pref(Context context,String gul,String key,String a){
        SharedPreferences pref =
                context.getSharedPreferences(gul,context.MODE_WORLD_READABLE|context.MODE_WORLD_WRITEABLE);
        SharedPreferences.Editor e = pref.edit();
        e.putString(key,a);
        e.commit();
    }
    public Pref(Context context,String gul,String key,boolean a){
        SharedPreferences pref =
                context.getSharedPreferences(gul,context.MODE_WORLD_READABLE|context.MODE_WORLD_WRITEABLE);
        SharedPreferences.Editor e = pref.edit();
        e.putBoolean(key,a);
        e.commit();
    }

    public Pref() {

    }


    public int GetInt(Context context,String gul,String key){
        SharedPreferences pref =
                context.getSharedPreferences(gul,context.MODE_WORLD_READABLE|context.MODE_WORLD_WRITEABLE);
        return pref.getInt(key,0);
    }
    public String GetString(Context context,String gul,String key){
        SharedPreferences pref =
                context.getSharedPreferences(gul,context.MODE_WORLD_READABLE|context.MODE_WORLD_WRITEABLE);
        return pref.getString(key,"");
    }
    public boolean GetBoolean(Context context,String gul,String key){
        SharedPreferences pref =
                context.getSharedPreferences(gul,context.MODE_WORLD_READABLE|context.MODE_WORLD_WRITEABLE);
        return pref.getBoolean(key,false);
    }
}
