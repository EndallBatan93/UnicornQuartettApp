package com.unicorn.unicornquartett.Utility;

import com.unicorn.unicornquartett.domain.Card;
import com.unicorn.unicornquartett.domain.Deck;

import io.realm.Realm;

/**
 * Created by max on 28.12.17.
 */

public class ArtificialIntelligence {

    Realm realm = Realm.getDefaultInstance();
    Card card;
    String difficulty;

    public ArtificialIntelligence(Deck deck, String difficulty) {
        this.difficulty = difficulty;
    }

    public int playCard (Card card){
        int attributePosition;

        return attributePosition;
    }
}
