package com.unicorn.unicornquartett.domain;


import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Index;

public class Game extends RealmObject {

    @Index
    private int id;
    private RealmList<User> users;
    private String deck;
    private RealmList<Card> usercards;
    private RealmList<Card> opponentCards;
    private Boolean turnUser;
    private int timeout;

    public Game () {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public RealmList<User> getUsers() {
        return users;
    }

    public void setUsers(RealmList<User> users) {
        this.users = users;
    }

    public String getDeck() {
        return deck;
    }

    public void setDeck(String deck) {
        this.deck = deck;
    }

    public RealmList<Card> getUsercards() {
        return usercards;
    }

    public void setUsercards(RealmList<Card> usercards) {
        this.usercards = usercards;
    }

    public RealmList<Card> getOpponentCards() {
        return opponentCards;
    }

    public void setOpponentCards(RealmList<Card> opponentCards) {
        this.opponentCards = opponentCards;
    }

    public Boolean getTurnUser() {
        return turnUser;
    }

    public void setTurnUser(Boolean turnUser) {
        this.turnUser = turnUser;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }
}
