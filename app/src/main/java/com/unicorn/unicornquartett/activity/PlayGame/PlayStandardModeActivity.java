package com.unicorn.unicornquartett.activity.PlayGame;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.unicorn.unicornquartett.R;
import com.unicorn.unicornquartett.Utility.Util;
import com.unicorn.unicornquartett.domain.Card;
import com.unicorn.unicornquartett.domain.Deck;
import com.unicorn.unicornquartett.domain.Shema;
import com.unicorn.unicornquartett.domain.User;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.Realm;
import io.realm.RealmList;

public class PlayStandardModeActivity extends AppCompatActivity {
    Realm realm = Realm.getDefaultInstance();
    RealmList<Card> teamUser = new RealmList<>();
    RealmList<Card> teamOpponent = new RealmList<>();

    @Override
    public void onBackPressed() {
        //TODO: spielstandspeichern
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_game_view);
        ListView attributeList = findViewById(R.id.playCardView);
        ImageView cardImage = findViewById(R.id.cardImage);
        User user = getuser();
        Deck deck = getDecks();
        if (user != null) {
            loadImageFromStorage(user.getImageAbsolutePath(), user.getImageIdentifier());
        }
        createStacks(deck);
        setAttributes(teamUser.first(),deck);
    }



    private Deck getDecks() {
        String selectedDeck = getIntent().getStringExtra("selectedDeck");
        Deck deck = realm.where(Deck.class).equalTo("name", selectedDeck).findFirst();
        return deck;
    }

    private User getuser() {
        User user = realm.where(User.class).findFirst();
        return user;
    }

    private void loadImageFromStorage(String absolutePath, String imageIdentifier) {
        Util.verifyStoragePermissions(PlayStandardModeActivity.this);
        try {
            File f = new File(absolutePath, imageIdentifier);
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            CircleImageView profileButton = findViewById(R.id.profileButton);
            profileButton.setImageBitmap(b);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void createStacks(Deck deck) {
        List<Integer> indices = new ArrayList<>(deck.getCards().size());
        for (int i = 0; i < deck.getCards().size(); i++) {
            indices.add(i);
        }
        Collections.shuffle(indices);

        for(int i = 0; i<indices.size(); i+=2) {
            teamOpponent.add(deck.getCards().get(indices.get(i)));
            teamUser.add(deck.getCards().get(indices.get(i+1)));

        }

    }


    public void setAttributes(Card card, Deck deck) {

        String[] buildDescriptors = {"desc", "value", "unit", "higherWins"};
        int[] buildLocation = {R.id.cardAttributeTitle, R.id.cardAttributeValue, R.id.cardAttributeUnit, R.id.cardAttributeHW};

        ArrayList<Map<String, String>> listOfDeckAttributes = new ArrayList<>();
        for (int i = 0; i < card.getAttributes().size(); i++) {
            HashMap<String, String> tmpHashmap = new HashMap<>();
            tmpHashmap.put("value", card.getAttributes().get(i)+"  ");
            ArrayList<String> shemaForCard = getShemaForCard(deck,i);
            tmpHashmap.put("desc", shemaForCard.get(0));
            tmpHashmap.put("unit", shemaForCard.get(1));
            tmpHashmap.put("higherWins", shemaForCard.get(2));
            listOfDeckAttributes.add(tmpHashmap);
        }

        SimpleAdapter simpleAdapter = new SimpleAdapter(getBaseContext(), listOfDeckAttributes, R.layout.listview_text_x4, buildDescriptors, buildLocation);
        ListView lw = findViewById(R.id.playCardView);
        lw.setAdapter(simpleAdapter);
        setImage(card,deck);
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

    public void setImage(Card card, Deck deck) {
        RealmList<String> imageIdentifiers = card.getImage().getImageIdentifiers();
        String identifier = imageIdentifiers.first();
        try {
            String deckName = deck.getName().toLowerCase();
            InputStream open = getAssets().open(deckName + "/" + identifier);
            Drawable fromStream = Drawable.createFromStream(open, null);
            ImageView view = findViewById(R.id.cardImage);
            view.setImageDrawable(fromStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

