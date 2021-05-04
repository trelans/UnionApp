package com.union.unionapp.models;

public class ModelLastActivities {
    String pId, timestamp, pUid, notification, sUid, sName, sEmail, sImage, sTag, type;

    public ModelLastActivities( String pId, String timestamp, String sUid, String sName, String sTag, String type ) {
        this.pId = pId;
        this.timestamp = timestamp;
        this.sUid = sUid;
        this.sName = sName;
        this.sTag = sTag; // geçmişteki kutay gelecekteki kutaya not : burdan achievements puanını kontrol et
        this.type = type;
    }

    public ModelLastActivities() {
    }

    public String getType() {
        return type;
    }

    public void setType( String type ) {
        this.type = type;
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
