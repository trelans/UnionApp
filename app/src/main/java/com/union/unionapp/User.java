package com.union.unionapp;

import java.util.ArrayList;

public class User {

   private String email;
   private String username;
   private ArrayList<Integer> achievements;
   private ArrayList<Integer> activitiesOnCalender;
   private int userID;

   public User(int userID) {
       // bilgileri databaseden assign et
   }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setAchievements(ArrayList<Integer> achievements) {
        this.achievements = achievements;
    }

    public void setActivitiesOnCalender(ArrayList<Integer> activitiesOnCalender) {
        this.activitiesOnCalender = activitiesOnCalender;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public ArrayList<Integer> getAchievements() {
        return achievements;
    }

    public ArrayList<Integer> getActivitiesOnCalender() {
        return activitiesOnCalender;
    }

    public int getUserID() {
        return userID;
    }
}
