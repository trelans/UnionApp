package com.union.unionapp;

import java.util.List;

public class ModelStackPost {
    // use same name as we given while uploading posts
    String pId, pTitle, pDetails, pImage, pTime, uid, uEmail, pAnon, username, pTagIndex;
    int pUpvoteNumber;
    List<String> pUpUsers;

    public ModelStackPost() {
    }

    public ModelStackPost(String pId, String pTitle, String pDetails, String pImage, String pTime, String uid, String uEmail, String pAnon, String username, Integer pUpvoteNumber, String pTagIndex, List<String> pUpUsers) {
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
        this.pTagIndex = pTagIndex;
        this.pUpUsers = pUpUsers;
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

    public int getpUpvoteNumber() {
        return pUpvoteNumber;
    }

    public void setpUpvoteNumber(int pUpvoteNumber) {
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

    public String getpTagIndex() {
        return pTagIndex;
    }

    public void setpTagIndex(String pTagIndex) {
        this.pTagIndex = pTagIndex;
    }

    public List<String> getpUpUsers() {
        return pUpUsers;
    }

    public void setpUpUsers(List<String> pUpUsers) {
        this.pUpUsers = pUpUsers;
    }
}
