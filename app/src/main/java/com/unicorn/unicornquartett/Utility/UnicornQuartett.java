package com.unicorn.unicornquartett.Utility;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class UnicornQuartett extends Application {

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


    }

    private void clearDatabaseRealm(Realm realm) {
        realm.beginTransaction();
        realm.deleteAll();
        realm.commitTransaction();
    }


}