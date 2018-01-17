package com.unicorn.unicornquartett.Utility;

import com.unicorn.unicornquartett.domain.Card;
import com.unicorn.unicornquartett.domain.CardDTO;
import com.unicorn.unicornquartett.domain.CardDTOList;
import com.unicorn.unicornquartett.domain.CardImageList;
import com.unicorn.unicornquartett.domain.Deck;
import com.unicorn.unicornquartett.domain.DeckDTO;
import com.unicorn.unicornquartett.domain.ShemaList;

import io.realm.Realm;
import io.realm.RealmList;

/**
 * Created by max on 17.01.18.
 */

public class DeckBuilder {
    private Realm realm;
    private Deck deckToFill;
    private ShemaList shemaList;
    private CardDTOList cardDTOList;
    private RealmList<Card> cardList;
    private DeckDTO deckDTO;
    RealmList<CardDTO> listOfCardDTO = new RealmList<>();
    RealmList<CardImageList> listOfCardImagesURLs = new RealmList<>();

    public DeckBuilder(Deck emptyDeck) {
        realm = Realm.getDefaultInstance();

        int deckID = emptyDeck.getId();
        deckToFill = realm.where(Deck.class).equalTo("id", deckID).findFirst();
        shemaList = realm.where(ShemaList.class).equalTo("deckID", deckID).findFirst();
        cardDTOList = realm.where(CardDTOList.class).equalTo("deckID", deckID).findFirst();
        deckDTO = realm.where(DeckDTO.class).equalTo("id", deckID).findFirst();

        listOfCardDTO = cardDTOList.getListOfCardDTO();
        listOfCardImagesURLs = deckDTO.getListOfCardImagesURLs();

        //List Of Shemas
        realm.beginTransaction();
        deckToFill.setShemaList(shemaList.getListOfShemas());
        //Number Of Cards
        deckToFill.setNumberOfCards(cardDTOList.getListOfCardDTO().size());
        realm.commitTransaction();
        //List Of Cards
        migrateCards();
        System.out.println("Es funtst");
    }

    private void migrateCards() {
        migrateCardValues();
    }

    private void migrateCardValues() {
        RealmList<Card> cardListToAdd = new RealmList<>();
        for (int i = 0; i < deckToFill.getNumberOfCards(); i++) {
            CardDTO cardDTO = listOfCardDTO.get(i);
            RealmList<Double> valueListDouble = cardDTO.getValueList();
            RealmList<String> cardValues = new RealmList<>();
            for (Double aDouble : valueListDouble) {
                cardValues.add(aDouble.toString());
            }

            realm.beginTransaction();
            Card cardToFill = realm.createObject(Card.class);
            //Adding values to card
            cardToFill.setAttributes(cardValues);
            cardToFill.setId(cardDTO.getId());
            cardToFill.setDeckID(cardDTO.getDeckID());
            cardToFill.setName(cardDTO.getName());
            realm.commitTransaction();
            realm.beginTransaction();
            //Adding List of CardUrls to Card
            RealmList<String> listOfImagesURLsForOneCard = listOfCardImagesURLs.get(i).getListOfImagesURLsForOneCard();
            for (String url : listOfImagesURLsForOneCard) {
                cardToFill.addUrlToList(url);
            }

            cardListToAdd.add(cardToFill);
            realm.commitTransaction();
        }
        //Adding list of cards to Deck
        realm.beginTransaction();
        deckToFill.setCards(cardListToAdd);
        //Is downloaded = true
        deckToFill.setIsDownloaded(true);
        realm.commitTransaction();
    }
}
