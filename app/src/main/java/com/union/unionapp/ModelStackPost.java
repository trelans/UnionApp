package com.union.unionapp;

public class ModelStackPost {
    // use same name as we given while uploading posts
    String pId, pTitle, pDetails, pImage, pTime, uid, uEmail, pAnon , username, pUpvoteNumber;

    public ModelStackPost() {
    }

    public ModelStackPost(String pId, String pTitle, String pDetails, String pImage, String pTime, String uid, String uEmail, String pAnon, String username, String pUpvoteNumber) {
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

    public String getPId() {
        return pId;
    }

    public void setPId(String pId) {
        this.pId = pId;
    }

    public String getPTitle() {
        return pTitle;
    }

    public void setPTitle(String pTitle) {
        this.pTitle = pTitle;
    }

    public String getPDetails() {
        return pDetails;
    }

    public void setPDetails(String pDetails) {
        this.pDetails = pDetails;
    }

    public String getPImage() {
        return pImage;
    }

    public void setPImage(String pImage) {
        this.pImage = pImage;
    }

    public String getPTime() {
        return pTime;
    }

    public void setPTime(String pTime) {
        this.pTime = pTime;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUEmail() {
        return uEmail;
    }

    public void setUEmail(String uEmail) {
        this.uEmail = uEmail;
    }

    public String getPUpvoteNumber() {
        return pUpvoteNumber;
    }

    public void setPUpvoteNumber(String pUpvoteNumber) {
        this.pUpvoteNumber = pUpvoteNumber;
    }

    public String getPAnon() {
        return pAnon;
    }

    public void setPAnon(String pAnon) {
        this.pAnon = pAnon;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
