package com.unicorn.unicornquartett;

import android.app.Application;

import com.unicorn.unicornquartett.domain.Card;
import com.unicorn.unicornquartett.domain.Deck;
import com.unicorn.unicornquartett.domain.Shema;
import com.unicorn.unicornquartett.domain.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmList;

public class UnicornQuartett extends Application {
    private JSONArray cardlist;

    @Override
    public void onCreate() {
        super.onCreate();

        Realm.init(getApplicationContext());
        RealmConfiguration config = new RealmConfiguration
                .Builder()
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(config);
        Realm realm = Realm.getDefaultInstance();

//        clearDatabaseRealm(realm);

        String tuningsJSON = this.loadJSONFromAsset("tuning/tuning.json");
        String bikesJSON = this.loadJSONFromAsset("bikes/bikes.json");
        try {
            JSONObject bikesObject = new JSONObject(bikesJSON);
            JSONArray bikeCards = bikesObject.getJSONArray("cards");
            this.cardlist = bikeCards;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        realm.beginTransaction();
        //create User
        if (realm.where(User.class).findAll().size() != 1) {
            User user = realm.createObject(User.class);
            RealmList<String> decks = new RealmList<>();
            decks.add("Bikes");
            decks.add("Tuning");
            user.setId(1);
            user.setName("Unicorn Hunter69");
            user.setDifficulty("Fluffy");
            user.setFriends(null);
            user.setDecks(decks);
            user.setRunningOffline(false);
            user.setRunningOnline(false);
        }

        // Shemas
        Shema bikeShema1 = realm.createObject(Shema.class);
        bikeShema1.setProperty("Geschwindigkeit");
        bikeShema1.setHigherWins(true);
        bikeShema1.setUnit("km/h");

        Shema bikeShema2 = realm.createObject(Shema.class);
        bikeShema2.setProperty("Hubraum");
        bikeShema2.setHigherWins(true);
        bikeShema2.setUnit("ccm");

        Shema bikeShema3 = realm.createObject(Shema.class);
        bikeShema3.setProperty("0 auf 100");
        bikeShema3.setHigherWins(false);
        bikeShema3.setUnit("sec");

        Shema bikeShema4 = realm.createObject(Shema.class);
        bikeShema4.setProperty("Zylinder");
        bikeShema4.setHigherWins(true);
        bikeShema4.setUnit("0");

        Shema bikeShema5 = realm.createObject(Shema.class);
        bikeShema5.setProperty("Leistung");
        bikeShema5.setHigherWins(true);
        bikeShema5.setUnit("PS");

        Shema bikeShema6 = realm.createObject(Shema.class);
        bikeShema6.setProperty("Umdrehungen");
        bikeShema6.setHigherWins(true);
        bikeShema6.setUnit("1/min");


        RealmList<Shema> bikeShemas = new RealmList<>();
        bikeShemas.add(bikeShema1);
        bikeShemas.add(bikeShema2);
        bikeShemas.add(bikeShema3);
        bikeShemas.add(bikeShema4);
        bikeShemas.add(bikeShema5);

        Shema tuningShema1 = realm.createObject(Shema.class);
        tuningShema1.setProperty("Geschwindigkeit");
        tuningShema1.setHigherWins(true);
        tuningShema1.setUnit("km/h");

        Shema tuningShema2 = realm.createObject(Shema.class);
        tuningShema2.setProperty("Umdrehungen");
        tuningShema2.setHigherWins(true);
        tuningShema2.setUnit("1/min");


        Shema tuningShema3 = realm.createObject(Shema.class);
        tuningShema3.setProperty("0 auf 100");
        tuningShema3.setHigherWins(false);
        tuningShema3.setUnit("sec");


        Shema tuningShema4 = realm.createObject(Shema.class);
        tuningShema4.setProperty("Hubraum");
        tuningShema4.setHigherWins(true);
        tuningShema4.setUnit("ccm");

        Shema tuningShema5 = realm.createObject(Shema.class);
        tuningShema5.setProperty("Leistung");
        tuningShema5.setHigherWins(true);
        tuningShema5.setUnit("PS");

        Shema tuningShema6 = realm.createObject(Shema.class);
        tuningShema6.setProperty("Drehmoment");
        tuningShema6.setHigherWins(true);
        tuningShema6.setUnit("Nm");

        RealmList<Shema> tuningshemas = new RealmList<>();
        tuningshemas.add(tuningShema1);
        tuningshemas.add(tuningShema2);
        tuningshemas.add(tuningShema3);
        tuningshemas.add(tuningShema4);
        tuningshemas.add(tuningShema5);
        tuningshemas.add(tuningShema6);

        //Cards
        for (int i = 0; i <= cardlist.length(); i++) {
            Card card = realm.createObject(Card.class);
        }
        //Decks
        Deck bikes = realm.createObject(Deck.class);
        bikes.setName("Bikes");
        bikes.setId(1);
        bikes.setNumberOfCards(32);
        bikes.setLocked(false);
        bikes.setShema(bikeShemas);
//        bikes.setCards();

        Deck tuning = realm.createObject(Deck.class);
        tuning.setName("Tuning");
        tuning.setId(2);
        tuning.setLocked(false);
        tuning.setNumberOfCards(32);
        tuning.setShema(tuningshemas);
//        tuning.setCards();

        realm.commitTransaction();
        realm.close();
    }

    public String loadJSONFromAsset(String filename) {
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

}
