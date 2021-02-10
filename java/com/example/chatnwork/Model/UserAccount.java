package com.example.chatnwork.Model;

import com.google.firebase.database.Exclude;

public class UserAccount {
    private  String key,id,username,password,email,dob,imageurl, status, phoneno, sex, address,search;

    public UserAccount() {
    }

    public UserAccount(String username, String password, String email, String dob, String imageurl,String status) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.dob = dob;
        this.imageurl = imageurl;
        this.status= status;
        this.search= username.toLowerCase();
    }

    public UserAccount(String username, String password, String email, String dob, String imageurl,
                       String status,String phoneno, String sex,String address) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.dob = dob;
        this.imageurl = imageurl;
        this.status= status;
        this.phoneno= phoneno;
        this.sex= sex;
        this.address= address;
    }

    @Exclude
    public void setKey(String key) {
        this.key = key;
    }
    @Exclude
    public String getKey() {
        return key;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPhoneno() {
        return phoneno;
    }

    public void setPhoneno(String phoneno) {
        this.phoneno = phoneno;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Exclude
    public String getId() {
        return id;
    }

    @Exclude
    public void setId(String id) {
        this.id = id;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }
}
