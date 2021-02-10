package com.example.chatnwork;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Icon;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Random;

public class MyFirebaseMessagingServices extends FirebaseMessagingService {
    private String adminChannel="ChatNWork";
    public MyFirebaseMessagingServices() {
    }

   @Override
    public void onMessageReceived(RemoteMessage rm){
        super.onMessageReceived(rm);
        Intent i= new Intent(this,MainActivity.class);
       NotificationManager notificationManager= (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
       int notificationId= new Random().nextInt(3000);

       if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
           setupChannels(notificationManager);
       }

       i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
       PendingIntent pendingIntent= PendingIntent.getActivity(this,0,i,PendingIntent.FLAG_ONE_SHOT);
       Uri notificationSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

       Bitmap largeIcon= BitmapFactory.decodeResource(getResources(),R.drawable.chaticon);

       NotificationCompat.Builder notificationBuilder= new NotificationCompat.Builder(this,adminChannel)
               .setSmallIcon(R.drawable.chaticon)
               .setLargeIcon(largeIcon)
               .setContentTitle(rm.getData().get("title"))
               .setContentText(rm.getData().get("message"))
               .setAutoCancel(true)
               .setSound(notificationSoundUri)
               .setContentIntent(pendingIntent);

       notificationManager.notify(notificationId,notificationBuilder.build());

   }

   @RequiresApi(api= Build.VERSION_CODES.O)
    private void setupChannels(NotificationManager notificationManager) {
        String ChannelName= "New Notification";
        String channelDescription="Device to Device Notification";

       NotificationChannel notificationChannel= new NotificationChannel(adminChannel,ChannelName,NotificationManager.IMPORTANCE_HIGH);
       notificationChannel.setDescription(channelDescription);
       notificationChannel.enableLights(true);
       notificationChannel.setLightColor(Color.RED);
       notificationChannel.enableVibration(true);
       notificationManager.createNotificationChannel(notificationChannel);
    }
}