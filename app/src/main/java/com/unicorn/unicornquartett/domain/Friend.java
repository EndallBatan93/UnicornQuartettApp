package com.unicorn.unicornquartett.domain;


import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Index;

public class Friend extends RealmObject {
    @Index
    private  int id;
    private String name;
    private RealmList<Deck> decks;
    private Boolean friend;
    private Boolean friendOnline;

    public Friend() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public RealmList<Deck> getDecks() {
        return decks;
    }

    public void setDecks(RealmList<Deck> decks) {
        this.decks = decks;
    }

    public Boolean getFriend() {
        return friend;
    }

    public void setFriend(Boolean friend) {
        this.friend = friend;
    }

    public Boolean getFriendOnline() {
        return friendOnline;
    }

    public void setFriendOnline(Boolean friendOnline) {
        this.friendOnline = friendOnline;
    }
}
