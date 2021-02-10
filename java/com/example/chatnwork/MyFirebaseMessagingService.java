package com.example.chatnwork;

import android.app.Service;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        try{
            getFirebaseMessage(remoteMessage.getNotification().getTitle(),remoteMessage.getNotification().getBody());
        }catch (Exception e){
            Log.d("now",e.getMessage());
        }

    }

    public void getFirebaseMessage(String title, String msg){
        NotificationCompat.Builder builder= new NotificationCompat.Builder(this, "ChatnWork")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(title)
                .setContentText(msg)
                .setAutoCancel(true);
        NotificationManagerCompat manager= NotificationManagerCompat.from(this);
        manager.notify(101,builder.build());
    }
}
