package com.unicorn.unicornquartett.domain;

import java.util.Map;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Index;

public class Deck extends RealmObject {
    @Index
    private int id;
    private String name;
    private int numberOfCards;
    private RealmList<Card> cards;
    private Boolean locked;
    private RealmList<Shema>shema;


    public Deck () {

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

    public int getNumberOfCards() {
        return numberOfCards;
    }

    public void setNumberOfCards(int numberOfCards) {
        this.numberOfCards = numberOfCards;
    }

    public RealmList<Card> getCards() {
        return cards;
    }

    public void setCards(RealmList<Card> cards) {
        this.cards = cards;
    }

    public Boolean getLocked() {
        return locked;
    }

    public void setLocked(Boolean locked) {
        this.locked = locked;
    }

    public RealmList<Shema> getShema() {
        return shema;
    }

    public void setShema(RealmList<Shema> shema) {
        this.shema = shema;
    }
}



