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
    String difficulty;
    Avg avg;
    RealmList<Double> attributesValues;
    RealmList<Boolean> higherWins;
    RealmList<Double> relativeValues = new RealmList<>();

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
        int easyPosition = ThreadLocalRandom.current().nextInt(0, relativeValues.size());
        return easyPosition;
    }

    private int chooseMedium() {
        int mediumPosition = 0;
        double firstHighestValue = Double.MIN_VALUE, secondHighestValue = Double.MIN_VALUE;
        int firstPosition = 0, secondPosition = 0;

        for (int i=0; i<relativeValues.size(); i++){
            if(relativeValues.get(i) > firstHighestValue){
                secondHighestValue = firstHighestValue;
                firstHighestValue = relativeValues.get(i);
                secondPosition = firstPosition;
                firstPosition = i;
            }
            else if (relativeValues.get(i) > secondHighestValue) {
                secondHighestValue = relativeValues.get(i);
                secondPosition = i;
            }
        }

        int[] posList = new int[] { firstPosition, secondPosition };
        int randomPos = ThreadLocalRandom.current().nextInt(0, posList.length);
        mediumPosition = posList[randomPos];
        return mediumPosition;
    }

    private int chooseHard() {
        int hardPosition = 0;

        int limit = relativeValues.size();
        double max = Double.MIN_VALUE;
        for (int i = 0; i < limit; i++) {
            Double value = relativeValues.get(i);
            if (value > max) {
                max = value;
                hardPosition = i;
            }
        }
        return hardPosition;
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
