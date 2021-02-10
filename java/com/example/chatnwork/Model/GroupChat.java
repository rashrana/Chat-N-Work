package com.example.chatnwork.Model;

import java.util.Date;

public class GroupChat {
    private String sender, messege, groupname;
    private Date times;

    public GroupChat() {

    }

    public GroupChat(String sender, String groupname, String messege, Date times) {
        this.sender = sender;
        this.messege = messege;
        this.groupname = groupname;
        this.times = times;

    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getGroupname() {
        return groupname;
    }

    public void setGroupname(String groupname) {
        this.groupname = groupname;
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
