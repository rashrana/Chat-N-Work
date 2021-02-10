package com.example.chatnwork.Notification;

import android.provider.ContactsContract;

public class NotificationSender {
    public String to;
    public Data data;

    public NotificationSender() {
    }

    public NotificationSender(String to, Data data) {
        this.to = to;
        this.data = data;
    }
}
