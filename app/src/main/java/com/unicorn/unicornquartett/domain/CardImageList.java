package com.unicorn.unicornquartett.domain;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by max on 17.01.18.
 */

public class CardImageList extends RealmObject {
    private int deckId;
    private int cardID;
    private RealmList<String> listOfImagesURLsForOneCard;

    public CardImageList() {
    }

    public int getDeckId() {
        return deckId;
    }

    public void setDeckId(int deckId) {
        this.deckId = deckId;
    }

    public int getCardID() {
        return cardID;
    }

    public void setCardID(int cardID) {
        this.cardID = cardID;
    }

    public RealmList<String> getListOfImagesURLsForOneCard() {
        return listOfImagesURLsForOneCard;
    }

    public void setListOfImagesURLsForOneCard(RealmList<String> listOfImagesURLsForOneCard) {
        this.listOfImagesURLsForOneCard = listOfImagesURLsForOneCard;
    }
}
