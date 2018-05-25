package com.rileystech.gurpay;

import android.app.LauncherActivity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MessageReceiver extends FirebaseMessagingService {
    private static final int REQUEST_CODE = 1;
    private static final int NOTIFICATION_ID = 6587;


    public MessageReceiver(){
        super();
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Map<String,String> data = remoteMessage.getData();
        Set<String> k = data.keySet();
        final String title = remoteMessage.getData().get("title");
        final String message = remoteMessage.getData().get("message");

        showNotifications("Gurpay",message);
    }

    private void showNotifications(String title, String msg) {
        //Intent i = new Intent(this, LauncherActivity.class);

        //PendingIntent pendintIntent = PendingIntent.getActivity(this,REQUEST_CODE,i,PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = new Notification.Builder(this)
                .setContentText(msg)
                .setContentTitle(title)
        //        .setContentIntent(pendintIntent)
                .setSmallIcon(R.mipmap.ic_launcher)
                .build();

        NotificationManager manager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        manager.notify(NOTIFICATION_ID, notification);
    }
}
