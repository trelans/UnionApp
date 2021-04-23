package com.union.unionapp;

public class ModelUser {
    String username, email, search, profileImage , ui;

    public  ModelUser() {

    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getSearch() {
        return search;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public String getUi() {
        return ui;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public void setUi(String ui) {
        this.ui = ui;
    }

    public  ModelUser(String username, String email , String search, String profileImage, String ui ) {
        this.username = username;
        this.email = email;
        this.search = search;
        this.profileImage = profileImage;
        this.ui = ui;
    }
}
