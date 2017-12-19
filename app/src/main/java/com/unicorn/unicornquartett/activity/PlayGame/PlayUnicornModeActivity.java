package com.unicorn.unicornquartett.activity.PlayGame;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.unicorn.unicornquartett.R;
import com.unicorn.unicornquartett.domain.Deck;
import com.unicorn.unicornquartett.domain.User;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

public class
PlayUnicornModeActivity extends AppCompatActivity {

    Realm realm = Realm.getDefaultInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_unicorn_mode);
        User user = getuser();
        Deck deck = getDecks();


    }

    private Deck getDecks() {
        String selectedDeck = getIntent().getStringExtra("selectedDeck");
        Deck deck = realm.where(Deck.class).equalTo("deckName", selectedDeck).findFirst();
        return deck;
    }

    private User getuser() {
        User user = realm.where(User.class).findFirst();
        return user;
    }

}
