package com.rileystech.gurpay;
import android.content.Context;
import android.content.SharedPreferences;
import java.text.SimpleDateFormat;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.provider.Settings.Secure;

import java.util.Date;
import java.util.HashMap;


public class Util {


    public static String getUUID(Context ctx){
        String android_id = Secure.getString(ctx.getContentResolver(),
                Secure.ANDROID_ID);
        return android_id;
    }

    public static String displayDate(Date date) {
        if(date == null)
            return  "";
        SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yy");

        return formatter.format(date);
    }

    public static String dateForAPI(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        return formatter.format(date);
    }

    public static HashMap<Boolean,Date> parseDate(String date){
        date = date.replaceAll("\\/","-");
        SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yy");
        HashMap<Boolean,Date> map = new HashMap<>();
        try {
            Date d = formatter.parse(date);
            if( d == null)
                map.put(false,null);
            else
                map.put(true,d);
            return map;
        }
        catch (Exception e) {
            map.put(false,null);
            return map;
        }
    }
}
