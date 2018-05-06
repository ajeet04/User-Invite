package com.example.raghvendrapandey.appshare;

/**
 * Created by Raghvendra Pandey on 11/1/2017.
 */

public class Users {
    public String name,email,inviteId;
    public Users(){}
    public Users(String name,String email,String inviteId){
        this.email=email;
        this.name=name;
        this.inviteId=inviteId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getInviteId() {
        return inviteId;
    }

    public void setInviteId(String inviteId) {
        this.inviteId = inviteId;
    }
}
