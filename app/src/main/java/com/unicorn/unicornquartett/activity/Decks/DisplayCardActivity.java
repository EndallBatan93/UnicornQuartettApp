package com.unicorn.unicornquartett.activity.Decks;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.unicorn.unicornquartett.R;
import com.unicorn.unicornquartett.domain.Card;
import com.unicorn.unicornquartett.domain.Deck;
import com.unicorn.unicornquartett.domain.Shema;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmList;

public class DisplayCardActivity extends AppCompatActivity {
    Realm realm = Realm.getDefaultInstance();
    TextView cardName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_card);
        ListView attributes = findViewById(R.id.attributes);
        Deck deck = getDeck();

        setAttributes(deck,attributes);

        setImage(deck);
    }

    public Deck getDeck() {
        Intent intent = getIntent();
        String deckName = intent.getStringExtra("DeckName");
        return realm.where(Deck.class).equalTo("name", deckName).findFirst();

    }

    public void setImage(Deck deck) {
        RealmList<String> imageIdentifiers = deck.getCards().first().getImage().getImageIdentifiers();
        String first = imageIdentifiers.first();
        try {
            InputStream open = getAssets().open(deck.getName() + "/" + first);
            Drawable fromStream = Drawable.createFromStream(open, null);
            ImageView view = findViewById(R.id.card);
            view.setImageDrawable(fromStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setAttributes(Deck deck, ListView attributes) {
        RealmList<Card> cards = deck.getCards();
        setContentView(R.layout.activity_display_card);
        cardName = findViewById(R.id.cardName);

        String[] buildDescriptors = {"desc", "value", "unit", "higherWins"};
        int[] buildLocation = {R.id.cardAttributeTitle, R.id.cardAttributeValue, R.id.cardAttributeUnit, R.id.cardAttributeHW};

        ArrayList<Map<String, String>> listOfDeckAttributes = new ArrayList<>();
        for (int i = 0; i < cards.first().getAttributes().size(); i++) {
            HashMap<String, String> tmpHashmap = new HashMap<>();
            tmpHashmap.put("value", cards.first().getAttributes().get(i)+"  ");
            ArrayList<String> shemaForCard = getShemaForCard(deck, i);
            tmpHashmap.put("desc", shemaForCard.get(0));
            tmpHashmap.put("unit", shemaForCard.get(1));
            tmpHashmap.put("higherWins", shemaForCard.get(2));
            listOfDeckAttributes.add(tmpHashmap);
        }

        SimpleAdapter simpleAdapter = new SimpleAdapter(getBaseContext(), listOfDeckAttributes, R.layout.listview_text_x4, buildDescriptors, buildLocation);
        ListView lw = findViewById(R.id.attributes);
        lw.setAdapter(simpleAdapter);
        cardName.setText(cards.first().getName());
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
