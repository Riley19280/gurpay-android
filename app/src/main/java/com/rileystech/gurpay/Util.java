package com.rileystech.gurpay;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.provider.Settings.Secure;
import android.support.v7.app.AlertDialog;
import android.view.View;

import com.rileystech.gurpay.models.APIError;
import com.rileystech.gurpay.models.User;
import com.rileystech.gurpay.network.APICallResponse;
import com.rileystech.gurpay.network.ServiceBase;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;


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


    public static void payersSelect(final Activity view, final List<User> filter, final APICallResponse response) {

        ServiceBase.group.GetGroupMembers(view.getApplicationContext(), new APICallResponse() {
            @Override
            public void success(Object obj) {
                final List<User> users = (List<User>)obj;

                for (Iterator<User> iter = users.listIterator(); iter.hasNext(); ) {
                    User a = iter.next();
                    for(User u : filter) {
                        if(u.id == a.id)
                            iter.remove();
                    }
                }


                CharSequence usernames[] = new CharSequence[users.size()];
                for(int i = 0; i < users.size(); i++)
                    usernames[i] = users.get(i).name;


                AlertDialog.Builder builder = new AlertDialog.Builder(view);
                builder.setTitle("Add Payer");
                builder.setItems(usernames, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        response.success(users.get(which));
                    }
                });
                builder.show();
            }

            @Override
            public void error(APIError err) {
                response.error(err);
            }
        });
    }

    public static Date parseServerDate(String date) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        try {
            return formatter.parse(date);
        }
        catch (ParseException p1) {
            formatter.applyPattern("yyyy-MM-dd");
            try{
                return formatter.parse(date);
            }
            catch (ParseException p2) {

            }
        }
        return null;
    }
}
