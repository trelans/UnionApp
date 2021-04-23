package com.union.unionapp;

import java.util.ArrayList;

public class ModelUser {

    String username, email, search, profileImage, ui, password, notifications;
    int accountType;
    ArrayList<String> tags;
    ArrayList<Integer> achievements, lastActivities, upcomingActivities;
    boolean isClosed;


    public ArrayList<Integer> getAchievements() { return achievements; }

    public ArrayList<Integer> getUpcomingActivities() { return upcomingActivities; }

    public void setUpcomingActivities(ArrayList<Integer> upcomingActivities) { this.upcomingActivities = upcomingActivities; }

    public boolean isClosed() { return isClosed; }

    public void setClosed(boolean closed) { isClosed = closed; }

    public String getNotifications() { return notifications; }

    public void setNotifications(String notifications) { this.notifications = notifications; }

    public ArrayList<Integer> getLastActivities() { return lastActivities; }

    public void setLastActivities(ArrayList<Integer> lastActivities) { this.lastActivities = lastActivities; }

    public void setAchievements(ArrayList<Integer> achievements) { this.achievements = achievements; }

    public String getPassword() { return password; }

    public void setPassword(String password) { this.password = password; }

    public ArrayList<String> getTags() { return tags; }

    public void setTags(ArrayList<String> tags) { this.tags = tags; }

    public int getAccountType() { return accountType; }

    public void setAccountType(int accountType) { this.accountType = accountType; }

    public  ModelUser() { }

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

    public  ModelUser(String username, String email , String search, String profileImage, String ui,
                      String password, int accountType, ArrayList<String> tags, ArrayList<Integer> achievements,
                      ArrayList<Integer> lastActivities, String notifications, boolean isClosed, ArrayList<Integer> upcomingActivities) {
        this.username = username;
        this.email = email;
        this.search = search;
        this.profileImage = profileImage;
        this.ui = ui;
        this.password = password;
        this.accountType = accountType;
        this.tags = tags;
        this.achievements = achievements;
        this.lastActivities = lastActivities;
        this.notifications = notifications;
        this.isClosed = isClosed;
        this.upcomingActivities = upcomingActivities;
    }
}
