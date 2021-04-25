package com.union.unionapp;

public class ModelBuddyAndClubPost {
    // use same name as we given while uploading posts
    String pId, pTitle, pDate, pHour, pLocation, pQuota, pDetails, pImage, pTime, uid, uEmail, pAnon , username;

    public ModelBuddyAndClubPost() {
    }

    public ModelBuddyAndClubPost(String pId, String pTitle, String pDate, String pHour, String pLocation, String pQuota, String pDetails, String pImage, String pTime, String uid, String uEmail, String pAnon, String username) {
        this.pId = pId;
        this.pTitle = pTitle;
        this.pDate = pDate;
        this.pHour = pHour;
        this.pLocation = pLocation;
        this.pQuota = pQuota;
        this.pDetails = pDetails;
        this.pImage = pImage;
        this.pTime = pTime;
        this.uid = uid;
        this.uEmail = uEmail;
        this.pAnon = pAnon;
        this.username = username;
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

    public String getpLocation() {
        return pLocation;
    }

    public void setpLocation(String pLocation) {
        this.pLocation = pLocation;
    }

    public String getpQuota() {
        return pQuota;
    }

    public void setpQuota(String pQuota) {
        this.pQuota = pQuota;
    }

    public String getpDetails() {
        return pDetails;
    }

    public void setpDetails(String pDetails) {
        this.pDetails = pDetails;
    }

    public String getpImage() {
        return pImage;
    }

    public void setpImage(String pImage) {
        this.pImage = pImage;
    }

    public String getpTime() {
        return pTime;
    }

    public void setpTime(String pTime) {
        this.pTime = pTime;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getuEmail() {
        return uEmail;
    }

    public void setuEmail(String uEmail) {
        this.uEmail = uEmail;
    }

    public String getpAnon() {
        return pAnon;
    }

    public void setpAnon(String pAnon) {
        this.pAnon = pAnon;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
