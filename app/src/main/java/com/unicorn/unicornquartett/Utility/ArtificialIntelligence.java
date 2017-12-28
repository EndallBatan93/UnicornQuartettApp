package com.unicorn.unicornquartett.Utility;

import com.unicorn.unicornquartett.domain.Avg;
import com.unicorn.unicornquartett.domain.Card;
import com.unicorn.unicornquartett.domain.Deck;

import java.util.concurrent.ThreadLocalRandom;

import io.realm.Realm;
import io.realm.RealmList;

/**
 * Created by max on 28.12.17.
 */

public class ArtificialIntelligence {

    Realm realm = Realm.getDefaultInstance();
    Card card;
    String difficulty;
    Avg avg;
    RealmList<Double> attributesValues;
    RealmList<Boolean> higherWins;
    RealmList<Double> relativeValues;

    public ArtificialIntelligence(Deck deck, String difficulty) {
        this.difficulty = difficulty;
        this.avg = realm.where(Avg.class).equalTo("name", deck.getName()).findFirst();
        this.attributesValues = avg.getAvgDoubles();
        this.higherWins = avg.getHigherWins();
    }

    public int playCard (Card card){
        int attributePosition = 0;
        calcRelativeValues(card);
        if (difficulty.equals(Constants.DIFFICULTY_1)) {
            attributePosition = chooseEasy();
        }
        else if (difficulty.equals(Constants.DIFFICULTY_2)) {
            attributePosition = chooseMedium();
        }
        else {
            attributePosition = chooseHard();
        }
        return attributePosition;
    }

    private int chooseEasy() {
        int easyPosition = ThreadLocalRandom.current().nextInt(0, relativeValues.size() + 1);
        return easyPosition;
    }

    private int chooseMedium() {
        int mediumPosition = 0;
        return 0;
    }

    private int chooseHard() {
        int hardPosition;
        return 0;
    }

    private void calcRelativeValues(Card card) {
        for(int i=0; i< card.getAttributes().size(); i++){
            Double tmpCardValue = Double.parseDouble(card.getAttributes().get(i));
            if (higherWins.get(i) == true) {
                Double tmpRelativeValue = tmpCardValue / attributesValues.get(i);
                relativeValues.add(tmpRelativeValue);
            } else {
                Double tmpRelativeValue = 1 - (tmpCardValue / attributesValues.get(i));
                relativeValues.add(tmpRelativeValue);
            }
        }
    }
}
