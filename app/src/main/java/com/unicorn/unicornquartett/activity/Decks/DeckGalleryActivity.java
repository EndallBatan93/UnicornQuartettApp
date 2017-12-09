package com.unicorn.unicornquartett.activity.Decks;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.unicorn.unicornquartett.activity.Profile.ProfileActivity;
import com.unicorn.unicornquartett.R;
import com.unicorn.unicornquartett.domain.Deck;
import com.unicorn.unicornquartett.domain.User;

import io.realm.Realm;
import io.realm.RealmResults;

public class DeckGalleryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deck_gallery);
        Realm realm = Realm.getDefaultInstance();
        RealmResults<User> all = realm.where(User.class).findAll();
        User user = all.first();

        TextView profileName = findViewById(R.id.userName);

        assert user != null;
        profileName.setText(user.getName());
        RealmResults<Deck> decks = realm.where(Deck.class).findAll();
        TextView decksAvailable = findViewById(R.id.decks);
        StringBuilder deckName= new StringBuilder();
        for (Deck deck : decks) {
            deckName.append(deck.getName());
        }
        decksAvailable.setText(deckName.toString());

    }
    public void goToProfileActivity(View view) {
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }
}
