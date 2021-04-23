package com.union.unionapp;

public class ModelUsers {
         String accountState, accountType, achievements, email, pp , tags, uid, username;

    public ModelUsers(String accountState, String accountType, String achievements, String email, String pp, String tags, String uid, String username) {
        this.accountState = accountState;
        this.accountType = accountType;
        this.achievements = achievements;
        this.email = email;
        this.pp = pp;
        this.tags = tags;
        this.uid = uid;
        this.username = username;
    }
    public ModelUsers () {

    }

    public String getAccountState() {
        return accountState;
    }

    public void setAccountState(String accountState) {
        this.accountState = accountState;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public String getAchievements() {
        return achievements;
    }

    public void setAchievements(String achievements) {
        this.achievements = achievements;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPp() {
        return pp;
    }

    public void setPp(String pp) {
        this.pp = pp;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
