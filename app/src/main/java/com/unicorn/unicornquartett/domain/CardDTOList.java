package com.unicorn.unicornquartett.domain;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by max on 14.01.18.
 */

public class CardDTOList extends RealmObject {
    private int deckID;
    private RealmList<CardDTO> listOfCardDTO;

    public CardDTOList(RealmList<CardDTO> listOfCardDTO) {
        this.listOfCardDTO = listOfCardDTO;
    }

    public RealmList<CardDTO> getListOfCardDTO() {
        return listOfCardDTO;
    }

    public void setListOfCardDTO(RealmList<CardDTO> listOfCardDTO) {
        this.listOfCardDTO = listOfCardDTO;
    }

    public int getDeckID() {
        return deckID;
    }

    public void setDeckID(int deckID) {
        this.deckID = deckID;
    }
}
