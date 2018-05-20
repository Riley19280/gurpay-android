package com.rileystech.gurpay;
import android.content.Context;
import android.provider.Settings.Secure;


public class Util {

    public static String getUUID(Context ctx){
        String android_id = Secure.getString(ctx.getContentResolver(),
                Secure.ANDROID_ID);
        return android_id;
    }

}
