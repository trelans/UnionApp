package com.union.unionapp.models;

public class ModelAchievementsScores {
    String math, career, sport, technology, social, english, turkish, study;

    public ModelAchievementsScores( String career, String english, String math, String social, String sport, String study, String technology, String turkish ) {
        this.math = math;
        this.career = career;
        this.sport = sport;
        this.technology = technology;
        this.social = social;
        this.english = english;
        this.turkish = turkish;
        this.study = study;
    }

    public ModelAchievementsScores() {

    }

    public String getMath() {
        return math;
    }

    public void setMath( String math ) {
        this.math = math;
    }

    public String getCareer() {
        return career;
    }

    public void setCareer( String career ) {
        this.career = career;
    }

    public String getSport() {
        return sport;
    }

    public void setSport( String sport ) {
        this.sport = sport;
    }

    public String getTechnology() {
        return technology;
    }

    public void setTechnology( String technology ) {
        this.technology = technology;
    }

    public String getSocial() {
        return social;
    }

    public void setSocial( String social ) {
        this.social = social;
    }

    public String getEnglish() {
        return english;
    }

    public void setEnglish( String english ) {
        this.english = english;
    }

    public String getTurkish() {
        return turkish;
    }

    public void setTurkish( String turkish ) {
        this.turkish = turkish;
    }

    public String getStudy() {
        return study;
    }

    public void setStudy( String study ) {
        this.study = study;
    }
}
