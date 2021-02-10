package com.example.chatnwork.Model;

import java.util.List;

public class Group {
    private String groupname,profileurl,search;
    private List<UserAccount> members;

    public Group(){

    }
//    public Group(String groupname,List<UserAccount> members){
//        this.groupname= groupname;
//        this.members= members;
//    }
    public Group(String groupname,List<UserAccount> members,String profileurl){
        this.groupname= groupname;
        this.members= members;
        this.profileurl= profileurl;
        this.search=groupname.toLowerCase();
    }

    public String getGroupname() {
        return groupname;
    }

    public void setGroupname(String groupname) {
        this.groupname = groupname;
    }

    public String getProfileurl() {
        return profileurl;
    }

    public void setProfileurl(String profileurl) {
        this.profileurl = profileurl;
    }

    public List<UserAccount> getMembers() {
        return members;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public void setMembers(List<UserAccount> members) {
        this.members = members;
    }
}
