package com.union.unionapp;

public class ModelComment {
    // use the same name as used in uploading comments
    String cId, comment, timeStamp, upNumber, uid, cAnon, cPhoto, uName;

    public ModelComment() {
    }

    public ModelComment(String cId, String comment, String timeStamp, String upNumber, String uid, String cAnon, String cPhoto, String uName) {
        this.cId = cId;
        this.comment = comment;
        this.timeStamp = timeStamp;
        this.upNumber = upNumber;
        this.uid = uid;
        this.cAnon = cAnon;
        this.cPhoto = cPhoto;
        this.uName = uName;
    }

    public String getCId() {
        return cId;
    }

    public void setCId(String cId) {
        this.cId = cId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getUpNumber() {
        return upNumber;
    }

    public void setUpNumber(String upNumber) {
        this.upNumber = upNumber;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getCAnon() {
        return cAnon;
    }

    public void setCAnon(String cAnon) {
        this.cAnon = cAnon;
    }

    public String getCPhoto() {
        return cPhoto;
    }

    public void setCPhoto(String cPhoto) {
        this.cPhoto = cPhoto;
    }

    public String getUName() {
        return uName;
    }

    public void setUName(String uName) {
        this.uName = uName;
    }
}
