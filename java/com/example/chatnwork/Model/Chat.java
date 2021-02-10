package com.example.chatnwork.Model;

import java.util.Date;

public class Chat {
    private  String sender,receiver,messege;
    private Date times;
    public Chat(){

    }
    public Chat(String sender, String receiver, String messege,Date times) {
        this.sender = sender;
        this.receiver = receiver;
        this.messege = messege;
        this.times= times;

    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getMessege() {
        return messege;
    }

    public void setMessege(String messege) {
        this.messege = messege;
    }



    public Date getTimes() {
        return times;
    }

    public void setTimes(Date times) {
        this.times = times;
    }
}
