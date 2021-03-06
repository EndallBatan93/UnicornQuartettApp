package com.unicorn.unicornquartett.domain;


import io.realm.RealmList;
import io.realm.RealmObject;

public class CardDTO extends RealmObject {
    private int id;
    private String name;
    private int deckID;
    private RealmList<Double> valueList;

    public CardDTO() {

    }

    public RealmList<Double> getValueList() {
        return valueList;
    }

    public void setValueList(RealmList<Double> valueList) {
        this.valueList = valueList;
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

    public int getDeckID() {
        return deckID;
    }

    public void setDeckID(int deckID) {
        this.deckID = deckID;
    }
}
