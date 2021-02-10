package com.example.chatnwork.Notification;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface APIService {

//    @Header(
//            {
//                    "Content-Type:application/json",
//                    "Authorization:key=AAAAgGV7B8c:APA91bGfQDciGc57rY5Z_8GO0hGqfryfu-O_yFBP2iqIzo1uejF4a_AY_uyoN6rDuFy2o84ofADrw2sGwaNnPExMCiokRJ87mzQsBZHFo4YDD746Y7MTK0Pq_EMdFUWapH7j4gdefsM-"
//            }
//
//    )
    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body NotificationSender body);
}
