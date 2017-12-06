package com.unicorn.unicornquartett;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class UnicornQuartett  extends Application{
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

    //TODO: Setting up TestData Here like Decks,Cards, User , Stats, Friends
    }
}
