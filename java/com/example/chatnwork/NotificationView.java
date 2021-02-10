package com.example.chatnwork;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class NotificationView extends AppCompatActivity {

    private TextView type,body;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_view);

        type= findViewById(R.id.type);
        body= findViewById(R.id.body);

        String message= getIntent().getStringExtra("message");
        String typ= getIntent().getStringExtra("type");

        type.setText("("+typ+")");
        body.setText(message);

    }
}