package com.unicorn.unicornquartett.activity.Decks;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.unicorn.unicornquartett.R;
import com.unicorn.unicornquartett.domain.Card;
import com.unicorn.unicornquartett.domain.Deck;
import com.unicorn.unicornquartett.domain.Shema;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmList;

import static com.unicorn.unicornquartett.Utility.Constants.Fun_SOUND;
import static com.unicorn.unicornquartett.Utility.Util.getCardImageFromStorage;

public class DisplayCardActivity extends AppCompatActivity {
    private final Realm realm = Realm.getDefaultInstance();
    private int currentCardIndex = 0;

    @Override
    public void onResume() {
        super.onResume();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_card);
        final Deck deck = getDeck();
        Card first = deck.getCards().first();
        setAttributes(deck, first);
    }


    private Card getCurrentCard(Boolean left, RealmList<Card> cards) {
        if (left) {
            if (this.currentCardIndex == 0) {
                this.currentCardIndex = cards.size() - 1;
            } else {
                this.currentCardIndex -= 1;
            }

        } else { //right
            if (this.currentCardIndex == cards.size() - 1) {
                this.currentCardIndex = 0;
            } else {
                this.currentCardIndex += 1;
            }
        }
        return cards.get(this.currentCardIndex);
    }

    private Deck getDeck() {
        Intent intent = getIntent();
        String deckName = intent.getStringExtra("DeckName");
        return realm.where(Deck.class).equalTo("name", deckName).findFirst();

    }

    private void setCardImage(Card card) {
        Bitmap cardBitmap = getCardImageFromStorage(card.getDeckID(), card.getId());
        ImageView cardImage = findViewById(R.id.card);
        cardImage.setImageBitmap(cardBitmap);

    }

    private void setAttributes(final Deck deck, Card card) {
        setContentView(R.layout.activity_display_card);
        TextView cardName = findViewById(R.id.cardName);

        String[] buildDescriptors = {"desc", "value", "unit", "higherWins"};
        int[] buildLocation = {R.id.cardAttributeTitle, R.id.cardAttributeValue, R.id.cardAttributeUnit, R.id.cardAttributeHW};

        ArrayList<Map<String, String>> listOfDeckAttributes = new ArrayList<>();
        for (int i = 0; i < card.getAttributes().size(); i++) {
            HashMap<String, String> tmpHashmap = new HashMap<>();
            tmpHashmap.put("value", card.getAttributes().get(i) + "  ");
            ArrayList<String> shemaForCard = getShemaForCard(deck, i);
            tmpHashmap.put("desc", shemaForCard.get(0));
            tmpHashmap.put("unit", shemaForCard.get(1));
            tmpHashmap.put("higherWins", shemaForCard.get(2));
            listOfDeckAttributes.add(tmpHashmap);
        }

        SimpleAdapter simpleAdapter = new SimpleAdapter(getBaseContext(), listOfDeckAttributes, R.layout.listview_display_card, buildDescriptors, buildLocation);
        ListView lw = findViewById(R.id.attributes);
        lw.setAdapter(simpleAdapter);
        cardName.setText(card.getName());
        setCardImage(card);

        //
//        final MediaPlayer mp = MediaPlayer.create(this, R.raw.lasershot);

        Button right = findViewById(R.id.right);
        Button left = findViewById(R.id.left);
        final RealmList<Card> cards = deck.getCards();
        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fun_SOUND.start();
                setAttributes(deck, getCurrentCard(false, cards));
//                mp.start();
            }
        });


        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fun_SOUND.start();
                setAttributes(deck, getCurrentCard(true, cards));
//                mp.start();
            }
        });
    }

    private ArrayList<String> getShemaForCard(Deck deck, int i) {
        RealmList<Shema> shemas = deck.getShemaList();
        Shema shema = shemas.get(i);
        ArrayList<String> attributeDescriptionList = new ArrayList<>();
        attributeDescriptionList.add(shema != null ? shema.getProperty() : null);
        attributeDescriptionList.add(shema != null ? shema.getUnit() : null);
        attributeDescriptionList.add(shema != null ? shema.getHigherWins().toString() : null);
        return attributeDescriptionList;
    }
}
