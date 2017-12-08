package com.unicorn.unicornquartett;

import android.app.Application;

import com.unicorn.unicornquartett.domain.User;

import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmList;

public class UnicornQuartett extends Application {
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
        realm.beginTransaction();
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
        realm.commitTransaction();
        realm.close();
        //TODO: Setting up TestData Here like Decks,Cards, User , Stats, Friends
    }
}
