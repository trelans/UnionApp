package com.union.unionapp.models;

public class ModelNotification {
    String pId, timestamp, pUid, notification, sUid, sName, sEmail, sImage, sTag;

    public ModelNotification( String pId, String timestamp, String pUid, String notification, String sUid, String sName, String sImage, String sTag ) {
        this.pId = pId;
        this.timestamp = timestamp;
        this.pUid = pUid;
        this.notification = notification;
        this.sUid = sUid;
        this.sName = sName;
        this.sImage = sImage;
        this.sTag = sTag;
    }

    public ModelNotification() {
    }

    public String getsEmail() {
        return sEmail;
    }

    public void setsEmail( String sEmail ) {
        this.sEmail = sEmail;
    }

    public String getsTag() {
        return sTag;
    }

    public void setsTag( String sTag ) {
        this.sTag = sTag;
    }

    public String getpId() {
        return pId;
    }

    public void setpId( String pId ) {
        this.pId = pId;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp( String timestamp ) {
        this.timestamp = timestamp;
    }

    public String getpUid() {
        return pUid;
    }

    public void setpUid( String pUid ) {
        this.pUid = pUid;
    }

    public String getNotification() {
        return notification;
    }

    public void setNotification( String notification ) {
        this.notification = notification;
    }

    public String getsUid() {
        return sUid;
    }

    public void setsUid( String sUid ) {
        this.sUid = sUid;
    }

    public String getsName() {
        return sName;
    }

    public void setsName( String sName ) {
        this.sName = sName;
    }

    public String getsImage() {
        return sImage;
    }

    public void setsImage( String sImage ) {
        this.sImage = sImage;
    }
}
