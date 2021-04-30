package com.union.unionapp;

import android.view.Display;

public class ModelAchievementsScores {
    String Math , Carrier , Sport , Technology, Social, English, Turkish, Study;

    public ModelAchievementsScores(String math, String career, String sport, String technology, String social, String english, String turkish, String study) {
        Math = math;
        Carrier = career;
        Sport = sport;
        Technology = technology;
        Social = social;
        English = english;
        Turkish = turkish;
        Study = study;
    }
    public ModelAchievementsScores() {

    }

    public String getMath() {
        return Math;
    }

    public void setMath(String math) {
        Math = math;
    }

    public String getCarrier() {
        return Carrier;
    }

    public void setCarrier(String carrier) {
        Carrier = carrier;
    }

    public String getSport() {
        return Sport;
    }

    public void setSport(String sport) {
        Sport = sport;
    }

    public String getTechnology() {
        return Technology;
    }

    public void setTechnology(String technology) {
        Technology = technology;
    }

    public String getSocial() {
        return Social;
    }

    public void setSocial(String social) {
        Social = social;
    }

    public String getEnglish() {
        return English;
    }

    public void setEnglish(String english) {
        English = english;
    }

    public String getTurkish() {
        return Turkish;
    }

    public void setTurkish(String turkish) {
        Turkish = turkish;
    }

    public String getStudy() {
        return Study;
    }

    public void setStudy(String study) {
        Study = study;
    }
}
