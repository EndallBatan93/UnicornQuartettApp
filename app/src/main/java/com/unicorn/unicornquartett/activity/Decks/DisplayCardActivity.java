package com.unicorn.unicornquartett.activity.Decks;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.unicorn.unicornquartett.R;
import com.unicorn.unicornquartett.domain.Deck;

import java.io.IOException;
import java.io.InputStream;

import io.realm.Realm;
import io.realm.RealmList;

public class DisplayCardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_card);
        Intent intent = getIntent();
        String deckName = intent.getStringExtra("DeckName");
        Realm realm = Realm.getDefaultInstance();
        Deck deck = realm.where(Deck.class).equalTo("name", deckName).findFirst();


        RealmList<String> imageIdentifiers = deck.getCards().first().getImage().getImageIdentifiers();
        String first = imageIdentifiers.first();
        try {
            InputStream open = getAssets().open(deckName+"/"+first);
            Drawable fromStream = Drawable.createFromStream(open, null);
            ImageView view = findViewById(R.id.card);
            view.setImageDrawable(fromStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
