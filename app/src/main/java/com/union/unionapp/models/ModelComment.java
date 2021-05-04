package com.union.unionapp.models;

import java.util.List;

/**
 * This model represents comments
 *
 * @author unionTeam
 * @version 04.05.2021
 */
public class ModelComment {
    // use the same name as used in uploading comments
    String cId, comment, timeStamp, uid, cAnon, cPhoto, uName;
    int upNumber;
    List<String> cUpUsers;

    public ModelComment() {
    }

    public ModelComment( String cId, String comment, String timeStamp, Integer upNumber, String uid, String cAnon, String cPhoto, String uName, List<String> cUpUsers ) {
        this.cId = cId;
        this.comment = comment;
        this.timeStamp = timeStamp;
        this.upNumber = upNumber;
        this.uid = uid;
        this.cAnon = cAnon;
        this.cPhoto = cPhoto;
        this.uName = uName;
        this.cUpUsers = cUpUsers;
    }

    public String getCId() {
        return cId;
    }

    public void setCId( String cId ) {
        this.cId = cId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment( String comment ) {
        this.comment = comment;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp( String timeStamp ) {
        this.timeStamp = timeStamp;
    }

    public int getUpNumber() {
        return upNumber;
    }

    public void setUpNumber( int upNumber ) {
        this.upNumber = upNumber;
    }

    public String getUid() {
        return uid;
    }

    public void setUid( String uid ) {
        this.uid = uid;
    }

    public String getCAnon() {
        return cAnon;
    }

    public void setCAnon( String cAnon ) {
        this.cAnon = cAnon;
    }

    public String getCPhoto() {
        return cPhoto;
    }

    public void setCPhoto( String cPhoto ) {
        this.cPhoto = cPhoto;
    }

    public String getUName() {
        return uName;
    }

    public void setUName( String uName ) {
        this.uName = uName;
    }

    public List<String> getcUpUsers() {
        return cUpUsers;
    }

    public void setcUpUsers( List<String> cUpUsers ) {
        this.cUpUsers = cUpUsers;
    }
}
