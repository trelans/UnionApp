package com.union.unionapp;

public class ModelPost {
    // use same name as we given while uploading posts
    String pId, pTitle, pDetails, pImage, pTime, uid, uEmail, pAnon , username, pUpvoteNumber;

    public ModelPost() {
    }

    public ModelPost(String pId, String pTitle, String pDetails, String pImage, String pTime, String uid, String uEmail, String pAnon, String username, String pUpvoteNumber) {
        this.pId = pId;
        this.pTitle = pTitle;
        this.pDetails = pDetails;
        this.pImage = pImage;
        this.pTime = pTime;
        this.uid = uid;
        this.uEmail = uEmail;
        this.pAnon = pAnon;
        this.username = username;
        this.pUpvoteNumber = pUpvoteNumber;
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

    public String getpUpvoteNumber() {
        return pUpvoteNumber;
    }

    public void setpUpvoteNumber(String pUpvoteNumber) {
        this.pUpvoteNumber = pUpvoteNumber;
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
