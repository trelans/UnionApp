package com.union.unionapp.models;

/**
 * This model represents Chatlist
 *
 * @author unionTeam
 * @version 04.05.2021
 */
public class ModelChatlist {
    String id;  // to get chat list , sender/receiver uid

    public ModelChatlist( String id ) {
        this.id = id;

    }

    public String getId() {
        return id;
    }

    public void setId( String id ) {
        this.id = id;
    }

    public ModelChatlist() {

    }
}
