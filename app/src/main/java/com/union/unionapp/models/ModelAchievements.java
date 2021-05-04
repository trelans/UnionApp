package com.union.unionapp.models;

public class ModelAchievements {
    String title, description, point , genre , nId , level;

    public ModelAchievements(String title, String description, String point, String genre, String nId , String level) {
        this.title = title;
        this.description = description;
        this.point = point;
        this.genre = genre;
        this.level = level;
        this.nId = nId;

    }
    public ModelAchievements() {

    }

    public String getnId() {
        return nId;
    }

    public void setnId(String nId) {
        this.nId = nId;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPoint() {
        return point;
    }

    public void setPoint(String point) {
        this.point = point;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }
}
