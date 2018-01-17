package com.unicorn.unicornquartett.domain;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Index;

public class Deck extends RealmObject {
    @Index
    private int id;
    private String name;
    private int numberOfCards;
    private RealmList<Card> cards = new RealmList<>();
    private RealmList<Shema> shemaList;
    private Boolean isDownloaded = false;

    public Deck(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public Deck () {

    }

    public void addToCardList(Card card){
        cards.add(card);
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

    public Boolean getIsDownloaded() {
        return isDownloaded;
    }

    public void setIsDownloaded(Boolean isDownloaded) {
        this.isDownloaded = isDownloaded;
    }

    public RealmList<Shema> getShemaList() {
        return shemaList;
    }

    public void setShemaList(RealmList<Shema> shemaList) {
        this.shemaList = shemaList;
    }
}



