package com.unicorn.unicornquartett.domain;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by max on 14.01.18.
 */

public class ShemaList extends RealmObject {
    private int deckID;
    private RealmList<Shema> listOfShemas;

    public ShemaList(RealmList<Shema> listOfShemas) {
        this.listOfShemas = listOfShemas;
    }

    public RealmList<Shema> getListOfShemas() {
        return listOfShemas;
    }

    public void setListOfShemas(RealmList<Shema> listOfShemas) {
        this.listOfShemas = listOfShemas;
    }

    public int getDeckID() {
        return deckID;
    }

    public void setDeckID(int deckID) {
        this.deckID = deckID;
    }
}
