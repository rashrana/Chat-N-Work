package com.example.chatnwork.Notification;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;

public class MyFirebaseIdService extends FirebaseMessagingService {
    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        String refreshToken= FirebaseInstanceId.getInstance().getToken();

        if(user!=null){
            Token token= new Token(refreshToken);
            FirebaseDatabase.getInstance().getReference("Tokens")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .setValue(token);
        }

    }
}
