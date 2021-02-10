package com.example.chatnwork.Model;

import com.google.firebase.database.Exclude;

import java.util.List;

public class Task {
    private String title,due,status;
    private UserAccount requester;
    private List<UserAccount> assignees;
    private String key;
    public Task(){

    }

    public Task(String title, String due, String status, UserAccount requester, List<UserAccount> assignees) {
        this.title = title;
        this.due = due;
        this.status = status;
        this.requester = requester;
        this.assignees = assignees;
    }
    @Exclude
    public String getKey() {
        return key;
    }
    @Exclude
    public void setKey(String key) {
        this.key = key;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDue() {
        return due;
    }

    public void setDue(String due) {
        this.due = due;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public UserAccount getRequester() {
        return requester;
    }

    public void setRequester(UserAccount requester) {
        this.requester = requester;
    }

    public List<UserAccount> getAssignees() {
        return assignees;
    }

    public void setAssignees(List<UserAccount> assignees) {
        this.assignees = assignees;
    }
}
