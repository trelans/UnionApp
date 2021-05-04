package com.union.unionapp.models;

public class ModelCalendar {
    String pDate, pHour, pId, pTitle , pType , username;

    public ModelCalendar(String pDate, String pHour, String pId, String pTitle, String pType, String username) {
        this.pDate = pDate;
        this.pHour = pHour;
        this.pId = pId;
        this.pTitle = pTitle;
        this.pType = pType;
        this.username = username;
    }

    public ModelCalendar() {

    }

    public String getpDate() {
        return pDate;
    }

    public void setpDate(String pDate) {
        this.pDate = pDate;
    }

    public String getpHour() {
        return pHour;
    }

    public void setpHour(String pHour) {
        this.pHour = pHour;
    }

    public String getpId() {
        return pId;
    }

    public void setpId(String pId) {
        this.pId = pId;
    }

    public String getpTitle() {
        return pTitle;
    }

    public void setpTitle(String pTitle) {
        this.pTitle = pTitle;
    }

    public String getpType() {
        return pType;
    }

    public void setpType(String pType) {
        this.pType = pType;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
