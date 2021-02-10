package com.example.chatnwork.Notification;

import android.app.NotificationManager;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.example.chatnwork.R;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    String title,message;

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        title= remoteMessage.getData().get("title");
        message= remoteMessage.getData().get("message");

        NotificationCompat.Builder builder= new NotificationCompat.Builder(getApplicationContext())
                .setSmallIcon(R.drawable.chaticon)
                .setContentTitle(title)
                .setContentText(message);
        NotificationManager manager= (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0,builder.build());
    }
}
