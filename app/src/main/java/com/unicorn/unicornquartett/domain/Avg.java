package com.unicorn.unicornquartett.domain;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by max on 28.12.17.
 */

public class Avg extends RealmObject {
    int deckID;
    String name;
    RealmList<Double> avgDoubles;
    RealmList<Boolean> higherWins;

    public int getDeckID() {
        return deckID;
    }

    public void setDeckID(int deckID) {
        this.deckID = deckID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public RealmList<Double> getAvgDoubles() {
        return avgDoubles;
    }

    public void setAvgDoubles(RealmList<Double> avgDoubles) {
        this.avgDoubles = avgDoubles;
    }

    public RealmList<Boolean> getHigherWins() {
        return higherWins;
    }

    public void setHigherWins(RealmList<Boolean> higherWins) {
        this.higherWins = higherWins;
    }
}
