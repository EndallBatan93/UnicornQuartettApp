package com.unicorn.unicornquartett.activity.PlayGame;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.unicorn.unicornquartett.R;
import com.unicorn.unicornquartett.Utility.ArtificialIntelligence;
import com.unicorn.unicornquartett.domain.Card;
import com.unicorn.unicornquartett.domain.Deck;
import com.unicorn.unicornquartett.domain.Game;
import com.unicorn.unicornquartett.domain.Shema;
import com.unicorn.unicornquartett.domain.User;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmList;

import static com.unicorn.unicornquartett.Utility.Constants.*;

public class PlayStandardModeActivity extends AppCompatActivity {
    Realm realm = Realm.getDefaultInstance();
    RealmList<Card> userCards = new RealmList<>();
    RealmList<Card> opponentCards = new RealmList<>();
    private int currentShemaPosition;
    private String attrValue;
    private Context c = this;
    private TextView status;
    private TextView turn;
    private Game game;
    Boolean isChoosen = false;
    RealmList<String> attributes;
    ArtificialIntelligence currentAI;
    String difficulty;
    User user;
    Deck deck;
    String runningGame;

    @Override
    protected void onResume() {
        super.onResume();
        setContentView(R.layout.activity_play_game_view);
        Game standardGame = realm.where(Game.class).equalTo(REALM_ID, STANDARD_GAME).findFirst();
        if (standardGame != null) {
            handleResume();
        } else {
            handleInitialization();
        }

    }

    private void handleResume() {
        status = findViewById(R.id.status);
        turn = findViewById(R.id.turn);

        game = realm.where(Game.class).equalTo(REALM_ID, STANDARD_GAME).findFirst();
        user = game.getUsers().first();
        difficulty = user.getDifficulty();
        deck = getDeckFromString(game.getDeck());
        opponentCards = game.getOpponentCards();
        userCards = game.getUsercards();
        setAttributes(userCards.first(), deck);
        setStatus();
        setTurn();
    }

    @Override
    public void onBackPressed() {
    }

    private void handleInitialization() {
        status = findViewById(R.id.status);
        turn = findViewById(R.id.turn);

        user = getUser();
        deck = getDeckFromIntent();

        difficulty = user.getDifficulty();

        runningGame = getIntent().getStringExtra(GAME_RUNNING);
        if ((runningGame != null) && runningGame.equals("true")) {
            game = realm.where(Game.class).equalTo(REALM_ID, STANDARD_GAME).findFirst();
            userCards = game.getUsercards();
            opponentCards = game.getOpponentCards();
            setAttributes(userCards.first(), deck);
            setStatus();
        } else {
            createStacks(deck);
            setAttributes(userCards.first(), deck);
        }

        setTurn();
    }

    private void setTurn() {
        if (game == null) {
            turn.setText(PLAYERSTURN);
        } else {

            if (game.getTurn().equals(USER)) {
                turn.setText(PLAYERSTURN);
            } else {
                turn.setText(OPPONENTTURN);
            }
        }
    }

    private void setStatus() {
        status.setText(userCards.size() + ":" + opponentCards.size());
    }


    private Deck getDeckFromIntent() {
        String selectedDeck = getIntent().getStringExtra(SELECTED_DECK);
        Deck deck = realm.where(Deck.class).equalTo("name", selectedDeck).findFirst();
        return deck;
    }

    private Deck getDeckFromString(String deckName) {
        return deck = realm.where(Deck.class).equalTo("name", deckName).findFirst();
    }

    private User getUser() {
        User user = realm.where(User.class).findFirst();
        return user;
    }

    private void createStacks(Deck deck) {
        List<Integer> indices = new ArrayList<>(deck.getCards().size());
        for (int i = 0; i < deck.getCards().size(); i++) {
            indices.add(i);
        }
        Collections.shuffle(indices);

        for (int i = 0; i < indices.size(); i += 2) {
            opponentCards.add(deck.getCards().get(indices.get(i)));
            userCards.add(deck.getCards().get(indices.get(i + 1)));

        }
        setStatus();
    }


    private void setAttributes(final Card card, final Deck deck) {

        String[] buildDescriptors = {"desc", "attrValue", "unit", "higherWins"};
        int[] buildLocation = {R.id.cardAttributeTitle, R.id.cardAttributeValue, R.id.cardAttributeUnit, R.id.cardAttributeHW};

        final ArrayList<Map<String, String>> listOfDeckAttributes = new ArrayList<>();
        for (int i = 0; i < card.getAttributes().size(); i++) {
            HashMap<String, String> tmpHashmap = new HashMap<>();
            tmpHashmap.put("attrValue", card.getAttributes().get(i) + "  ");
            ArrayList<String> shemaForCard = getShemaForCard(deck, i);
            tmpHashmap.put("desc", shemaForCard.get(0));
            tmpHashmap.put("unit", shemaForCard.get(1));
            tmpHashmap.put("higherWins", shemaForCard.get(2));
            listOfDeckAttributes.add(tmpHashmap);
        }


        SimpleAdapter simpleAdapter = new SimpleAdapter(getBaseContext(), listOfDeckAttributes, R.layout.listview_text_x4, buildDescriptors, buildLocation);
        final ListView lw = findViewById(R.id.playCardView);
        lw.setAdapter(simpleAdapter);
        setImage(card, deck);

        final Button chooseValue = findViewById(R.id.chooseValueButton);
        chooseValue.setBackgroundColor(Color.GRAY);
        TextView status = findViewById(R.id.status);
        status.setText(userCards.size() + ":" + opponentCards.size());
        attributes = card.getAttributes();
        TextView cardName = findViewById(R.id.playingCardName);
        cardName.setText(userCards.first().getName());

        // User is playing
        if (game == null || game.getTurn().equals(USER)) {

            lw.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @SuppressLint("ResourceAsColor")
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Fun_SOUND.start();
                    attrValue = attributes.get(i);
                    lw.getChildAt(currentShemaPosition).setBackgroundColor(Color.WHITE);
                    currentShemaPosition = i;
                    lw.getChildAt(i).setBackgroundColor(Color.GREEN);
                    isChoosen = true;
                    chooseValue.setBackgroundColor(Color.GREEN);

                }
            });
        }
        // AI is playing
        else {
            currentAI = new ArtificialIntelligence(deck, difficulty);
            int choosenAttrPosition = currentAI.playCard(opponentCards.first());
            currentShemaPosition = choosenAttrPosition;
            attrValue = opponentCards.first().getAttributes().get(choosenAttrPosition);
            isChoosen = true;
            chooseValue.setBackgroundColor(Color.GREEN);
            chooseValue.setText("Continue");

        }

        chooseValue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isChoosen) {
                    realm.beginTransaction();
                    Game round = compareValues(attrValue, currentShemaPosition, deck);
                    realm.commitTransaction();

                    Intent intent = new Intent(c, ShowResultActivity.class);
                    intent.putExtra(GAME_CATEGORY, STANDARD);
                    startActivity(intent);
                    isChoosen = false;
                }
            }
        });
    }

    private ArrayList<String> getShemaForCard(Deck deck, int i) {
        RealmList<Shema> shemas = deck.getShema();
        Shema shema = shemas.get(i);
        ArrayList<String> attributeDescriptionList = new ArrayList<>();
        attributeDescriptionList.add(shema.getProperty());
        attributeDescriptionList.add(shema.getUnit());
        attributeDescriptionList.add(shema.getHigherWins().toString());
        return attributeDescriptionList;
    }

    private void setImage(Card card, Deck deck) {
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

    private Game compareValues(String value, int position, Deck deck) {
        Card contrahentCard;
        if (game == null || game.getTurn().equals(USER)) {
            contrahentCard = opponentCards.first();
        } else {
            contrahentCard = userCards.first();
        }

        String valueOpponent = contrahentCard.getAttributes().get(position);
        ArrayList<String> shemaForCard = getShemaForCard(deck, position);
        String higherWins = shemaForCard.get(2);
        double playerValue = Double.parseDouble(value);
        double opponentValue = Double.parseDouble(valueOpponent);
        String winner = "";

        if (realm.where(Game.class).equalTo(REALM_ID, STANDARD_GAME).findFirst() == null) {
            game = realm.createObject(Game.class);
            game.setId(STANDARD_GAME);
        } else {
            game = realm.where(Game.class).equalTo(REALM_ID, STANDARD_GAME).findFirst();
        }
        game.setDeck(deck.getName());
        game.setOpponentCards(opponentCards);
        game.setUsercards(userCards);
        RealmList<User> userList = new RealmList<>();
        userList.add(user);
        game.setUsers(userList);
        RealmList<String> realmList = convertArrayListToRealmList(shemaForCard);
        game.setShemas(realmList);

        if (playerValue < opponentValue) {
            if (higherWins.equals("true")) {
                winner = OPPONENT;
            } else {
                winner = PLAYER;
            }
        } else if (opponentValue < playerValue) {
            if (higherWins.equals("true")) {
                winner = PLAYER;
            } else {
                winner = OPPONENT;
            }
        } else {
            winner = DRAW;
        }
        RealmList<String> values = new RealmList<>();
        values.add(value);
        values.add(valueOpponent);
        game.setValues(values);
        game.setLastWinner(winner);
        if (game.getTurn() == null) {
            game.setTurn(USER);
        }

        if (game.getTurn().equals(USER)) {
            game.setTurn(OPPONENT);
        } else if (game.getTurn().equals(OPPONENT)) {
            game.setTurn(USER);
        }
        return game;
    }

    private RealmList<String> convertArrayListToRealmList(ArrayList<String> shemaForCard) {
        RealmList<String> realmList = new RealmList<>();
        for (String s : shemaForCard) {
            realmList.add(s);
        }
        return realmList;
    }


}

