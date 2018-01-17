package com.unicorn.unicornquartett.Utility;

import android.app.Application;

import com.unicorn.unicornquartett.domain.Avg;
import com.unicorn.unicornquartett.domain.Card;
import com.unicorn.unicornquartett.domain.Deck;
import com.unicorn.unicornquartett.domain.Image;
import com.unicorn.unicornquartett.domain.Shema;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmList;

import static com.unicorn.unicornquartett.Utility.Constants.BIKES;
import static com.unicorn.unicornquartett.Utility.Constants.REALM_BIKE_NAME;
import static com.unicorn.unicornquartett.Utility.Constants.REALM_TUNING_NAME;
import static com.unicorn.unicornquartett.Utility.Constants.TUNING;

public class UnicornQuartett extends Application {
    private JSONArray bikeListcards;
    private JSONArray tuningListcards;
    private RealmList<Shema> bikeShemas;
    private RealmList<Shema> tuningShemas;
    private JSONArray bikeShemaArray;
    private JSONArray tuningShemaArray;

    @Override
    public void onCreate() {
        super.onCreate();


        Realm.init(getApplicationContext());
        RealmConfiguration config = new RealmConfiguration
                .Builder()
                .deleteRealmIfMigrationNeeded()
                .name("unicornQuartett")
                .build();

        Realm.setDefaultConfiguration(config);
        Realm realm = Realm.getDefaultInstance();

        // IMPORTANT For database testing purposes only
        clearDatabaseRealm(realm);

        String tuningsJSON = this.loadJSONFromAsset("tuning/tuning.json");
        String bikesJSON = this.loadJSONFromAsset("bikes/bikes.json");
        try {

            JSONObject bikesObject = new JSONObject(bikesJSON);
            JSONArray bikeCards = bikesObject.getJSONArray("cards");
            JSONArray bikeShema = bikesObject.getJSONArray("properties");
            this.bikeShemaArray = bikeShema;
            this.bikeListcards = bikeCards;

            JSONObject tuningObject = new JSONObject(tuningsJSON);
            JSONArray tuningCards = tuningObject.getJSONArray("cards");
            JSONArray tuningShema = tuningObject.getJSONArray("properties");
            this.tuningShemaArray = tuningShema;
            this.tuningListcards = tuningCards;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        realm.beginTransaction();

        Deck bikeExists = realm.where(Deck.class).equalTo("name", REALM_BIKE_NAME).findFirst();
        if (bikeExists == null) {
            Deck bikes = realm.createObject(Deck.class);
            bikes.setName(REALM_BIKE_NAME);
            bikes.setId(125);
            bikes.setNumberOfCards(32);
            bikes.setIsDownloaded(false);
            try {
                getShemas(realm, bikeShemaArray, BIKES);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            bikes.setShemaList(bikeShemas);

            RealmList<Card> bikeCards = getCards(realm, bikeListcards, bikeShemas);
            bikes.setCards(bikeCards);
        }

        Deck tuningExists = realm.where(Deck.class).equalTo("name", REALM_TUNING_NAME).findFirst();
        if (tuningExists == null) {

            Deck tuning = realm.createObject(Deck.class);
            tuning.setName(REALM_TUNING_NAME);
            tuning.setId(11);
            tuning.setIsDownloaded(false);
            tuning.setNumberOfCards(32);
            try {
                getShemas(realm, tuningShemaArray, TUNING);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            tuning.setShemaList(tuningShemas);

            RealmList<Card> tuningCards = getCards(realm, tuningListcards, tuningShemas);
            tuning.setCards(tuningCards);
        }

        // Create avgs of card values
        Avg tuningAvg = realm.createObject(Avg.class);
        Deck tuningDeck = realm.where(Deck.class).equalTo("name", REALM_TUNING_NAME).findFirst();
        tuningAvg.setAvgDoubles(calcAvgsForDeck(tuningDeck));
        tuningAvg.setHigherWins(createHigherWinsList(tuningDeck));
        tuningAvg.setName(REALM_TUNING_NAME);

        Avg bikesAvg = realm.createObject(Avg.class);
        Deck bikesDeck = realm.where(Deck.class).equalTo("name", REALM_BIKE_NAME).findFirst();
        bikesAvg.setAvgDoubles(calcAvgsForDeck(bikesDeck));
        bikesAvg.setHigherWins(createHigherWinsList(bikesDeck));
        bikesAvg.setName(REALM_BIKE_NAME);

        // Realm commit and close
        realm.commitTransaction();
        realm.close();
    }

    private String loadJSONFromAsset(String filename) {
        String json = null;
        try {

            InputStream is = getAssets().open(filename);

            int size = is.available();

            byte[] buffer = new byte[size];

            is.read(buffer);

            is.close();

            json = new String(buffer, "UTF-8");


        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;

    }

    private void clearDatabaseRealm(Realm realm) {
        realm.beginTransaction();
        realm.deleteAll();
        realm.commitTransaction();
    }

    private void getShemas(Realm realm, JSONArray listParameter, String deckParameter) throws JSONException {
        RealmList<Shema> tempShemasList = new RealmList<>();

        for (int i = 0; i < listParameter.length(); i++) {
            Shema tempShema = realm.createObject(Shema.class);
            tempShema.setProperty((String) listParameter.getJSONObject(i).get("text"));
            String compare = (String) listParameter.getJSONObject(i).get("compare");
            Boolean isHigher = Boolean.TRUE;
            if (compare.equals("-1")) {
                isHigher = Boolean.FALSE;
            }
            tempShema.setHigherWins(isHigher);
            tempShema.setUnit((String) listParameter.getJSONObject(i).get("unit"));
            tempShemasList.add(tempShema);
        }

        if (deckParameter.equals(BIKES)) {
            this.bikeShemas = tempShemasList;
        } else if (deckParameter.equals(TUNING)) {
            this.tuningShemas = tempShemasList;
        }
    }

    private RealmList<Card> getCards(Realm realm, JSONArray listParameter, RealmList<Shema> shemaParameter) {
        RealmList<Card> returnCardList = new RealmList<>();
        for (int i = 0; i < listParameter.length(); i++) {

            Card card = realm.createObject(Card.class);
            RealmList<String> imagesList = new RealmList();
            RealmList<String> attributeList = new RealmList<>();
            try {
                // TODO add iterator for multiple images
                String filename = (String) listParameter.getJSONObject(i).getJSONArray("images").getJSONObject(0).get("filename");
                imagesList.add(filename);
                Image image = realm.createObject(Image.class);
                image.setId(i);
                image.setImageIdentifiers(imagesList);
                card.setImages(image);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            for (int j = 0; j < shemaParameter.size(); j++) {
                try {
                    String attributeValue = (String) listParameter.getJSONObject(i).getJSONArray("values").getJSONObject(j).get("value");
                    attributeList.add(attributeValue);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            try {
                String cardName = (String) listParameter.getJSONObject(i).get("name");
                card.setName(cardName);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            card.setAttributes(attributeList);
            card.setDescription("");
            card.setId(i);
            returnCardList.add(card);
        }

        return returnCardList;
    }

    private RealmList<Double> calcAvgsForDeck(Deck deck) {
        RealmList<Double> avgListInDouble = new RealmList<>();
        Double tmpDouble = 0.0;
        for (int i = 0; i < deck.getShemaList().size(); i++) {
            for (int j = 0; j < deck.getCards().size(); j++) {
                tmpDouble += Double.parseDouble(deck.getCards().get(j).getAttributes().get(i));
            }
            Double avgDouble = tmpDouble / (deck.getCards().size());
            avgListInDouble.add(avgDouble);
            tmpDouble = 0.0;
        }

        return avgListInDouble;
    }

    private RealmList<Boolean> createHigherWinsList(Deck deck) {
        RealmList<Boolean> higherWins = new RealmList<>();
        for (int i = 0; i < deck.getShemaList().size(); i++) {
            higherWins.add(deck.getShemaList().get(i).getHigherWins());
        }
        return higherWins;
    }

}