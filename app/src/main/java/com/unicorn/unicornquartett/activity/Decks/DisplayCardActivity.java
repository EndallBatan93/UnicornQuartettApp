package com.unicorn.unicornquartett.activity.Decks;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.unicorn.unicornquartett.domain.User;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

public class DisplayCardActivity extends AppCompatActivity {
    Realm realm = Realm.getDefaultInstance();
    TextView cardName;
    private  int currentCardIndex = 0;

    @Override
    public void onResume() {
        super.onResume();
        System.out.println("Resume");
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_card);
        final Deck deck = getDeck();
        Card first = deck.getCards().first();


        setAttributes(deck,first);
    }


    public Card getCurrentCard(Boolean left, RealmList<Card> cards) {
        if(left) {
            if(this.currentCardIndex == 0) {
                this.currentCardIndex = cards.size()-1;
            } else {
                this.currentCardIndex -= 1;
            }

            }else { //right
            if(this.currentCardIndex == cards.size()-1) {
                this.currentCardIndex = 0;
            } else {
                this.currentCardIndex += 1;
            }
        }
        return cards.get(this.currentCardIndex);
    }

    public Deck getDeck() {
        Intent intent = getIntent();
        String deckName = intent.getStringExtra("DeckName");
        return realm.where(Deck.class).equalTo("name", deckName).findFirst();

    }

    public void setImage(Card card, Deck deck) {
        RealmList<String> imageIdentifiers = card.getImage().getImageIdentifiers();
        String identifier = imageIdentifiers.first();
        try {
            InputStream open = getAssets().open(deck.getName().toLowerCase() + "/" + identifier);
            Drawable fromStream = Drawable.createFromStream(open, null);
            ImageView view = findViewById(R.id.card);
            view.setImageDrawable(fromStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setAttributes(final Deck deck, Card card) {
        setContentView(R.layout.activity_display_card);
//        cardName = findViewById(R.id.cardName);

        String[] buildDescriptors = {"desc", "value", "unit", "higherWins"};
        int[] buildLocation = {R.id.cardAttributeTitle, R.id.cardAttributeValue, R.id.cardAttributeUnit, R.id.cardAttributeHW};

        ArrayList<Map<String, String>> listOfDeckAttributes = new ArrayList<>();
        for (int i = 0; i < card.getAttributes().size(); i++) {
            HashMap<String, String> tmpHashmap = new HashMap<>();
            tmpHashmap.put("value", card.getAttributes().get(i)+"  ");
            ArrayList<String> shemaForCard = getShemaForCard(deck, i);
            tmpHashmap.put("desc", shemaForCard.get(0));
            tmpHashmap.put("unit", shemaForCard.get(1));
            tmpHashmap.put("higherWins", shemaForCard.get(2));
            listOfDeckAttributes.add(tmpHashmap);
        }

        SimpleAdapter simpleAdapter = new SimpleAdapter(getBaseContext(), listOfDeckAttributes, R.layout.listview_display_card, buildDescriptors, buildLocation);
        ListView lw = findViewById(R.id.attributes);
        lw.setAdapter(simpleAdapter);
//        cardName.setText(card.getName());
        setImage(card,deck);

        //
        final MediaPlayer mp = MediaPlayer.create(this, R.raw.lasershot);

        Button right = findViewById(R.id.right);
        Button left = findViewById(R.id.left);
        final RealmList<Card> cards = deck.getCards();
        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setAttributes(deck, getCurrentCard(false, cards));
                mp.start();
            }
        });


        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setAttributes(deck,getCurrentCard(true,cards));
                mp.start();
            }
        });
    }

    public ArrayList<String> getShemaForCard(Deck deck, int i) {
        RealmList<Shema> shemas = deck.getShema();
        Shema shema = shemas.get(i);
        ArrayList<String> attributeDescriptionList= new ArrayList<>();
        attributeDescriptionList.add(shema.getProperty());
        attributeDescriptionList.add(shema.getUnit());
        attributeDescriptionList.add(shema.getHigherWins().toString());
        return attributeDescriptionList;
    }
}
