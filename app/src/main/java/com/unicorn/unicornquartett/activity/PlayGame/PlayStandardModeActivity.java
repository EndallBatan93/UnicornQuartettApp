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

public class PlayStandardModeActivity extends AppCompatActivity {
    Realm realm = Realm.getDefaultInstance();
    RealmList<Card> teamUser = new RealmList<>();
    RealmList<Card> teamOpponent = new RealmList<>();
    private int currentShemaPosition;
    private String attrValue;
    private Context c = this;
    private TextView status;
    private TextView turn;
    private Game game;
    Boolean isChoosen = false;
    RealmList<String> attributes;
    int kiPosition;

    @Override
    public void onBackPressed() {
        //TODO: spielstandspeichern
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_game_view);
        ListView attributeList = findViewById(R.id.playCardView);
        status = findViewById(R.id.status);
        ImageView cardImage = findViewById(R.id.cardImage);
        turn = findViewById(R.id.turn);
        User user = getuser();
        Deck deck = getDecks();
        String PLAYERSTURN = "Your Turn";
        String OPPONENTTURN = "Opponent Turn";

        String runningGame = getIntent().getStringExtra("gameRunning");
        if ((runningGame != null) && runningGame.equals("true")) {
            game = realm.where(Game.class).findFirst();
            teamUser = game.getUsercards();
            teamOpponent = game.getOpponentCards();
            deck = realm.where(Deck.class).equalTo("name", game.getDeck()).findFirst();
            setAttributes(teamUser.first(), deck);
            status.setText(teamUser.size() + ":" + teamOpponent.size());

            
        } else {
            createStacks(deck);
            setAttributes(teamUser.first(), deck);
        }

        if(game == null) {
            turn.setText(PLAYERSTURN);
        } else {

            if (game.getTurn().equals("user")) {
                turn.setText(PLAYERSTURN);
            } else {
                turn.setText(OPPONENTTURN);
            }
        }
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

    public void createStacks(Deck deck) {
        List<Integer> indices = new ArrayList<>(deck.getCards().size());
        for (int i = 0; i < deck.getCards().size(); i++) {
            indices.add(i);
        }
        Collections.shuffle(indices);

        for (int i = 0; i < indices.size(); i += 2) {
            teamOpponent.add(deck.getCards().get(indices.get(i)));
            teamUser.add(deck.getCards().get(indices.get(i + 1)));

        }
        status.setText(teamUser.size() + ":" + teamOpponent.size());
    }


    public void setAttributes(final Card card, final Deck deck) {

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
        status.setText(teamUser.size() + ":" + teamOpponent.size());
        attributes = card.getAttributes();

        if (game == null || game.getTurn().equals("user")) {

            lw.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @SuppressLint("ResourceAsColor")
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    attrValue = attributes.get(i);
                    lw.getChildAt(currentShemaPosition).setBackgroundColor(Color.WHITE);
                    currentShemaPosition = i;
                    lw.getChildAt(i).setBackgroundColor(Color.GREEN);
                    isChoosen = true;
                    chooseValue.setBackgroundColor(Color.GREEN);

                }
            });
        } else {
            kiPosition = 2;
            attrValue = teamOpponent.first().getAttributes().get(kiPosition);
            currentShemaPosition = kiPosition;
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
                    startActivity(intent);
                    isChoosen = false;
                }
            }
        });
    }

    public ArrayList<String> getShemaForCard(Deck deck, int i) {
        RealmList<Shema> shemas = deck.getShema();
        Shema shema = shemas.get(i);
        ArrayList<String> attributeDescriptionList = new ArrayList<>();
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

    public Game compareValues(String value, int position, Deck deck) {
        Card contrahentCard;
        if (game == null || game.getTurn().equals("user")) {
            contrahentCard = teamOpponent.first();
        } else {
            contrahentCard = teamUser.first();
        }

        String valueOpponent = contrahentCard.getAttributes().get(position);
        ArrayList<String> shemaForCard = getShemaForCard(deck, position);
        String higherWins = shemaForCard.get(2);
        double playerValue = Double.parseDouble(value);
        double opponentValue = Double.parseDouble(valueOpponent);
        String winner = "";

        if (realm.where(Game.class).findAll().size() == 0) {
            game = realm.createObject(Game.class);
        } else {
            game = realm.where(Game.class).findFirst();
        }
        game.setId(1);
        game.setDeck(deck.getName());
        game.setOpponentCards(teamOpponent);
        game.setUsercards(teamUser);
        RealmList<String> realmList = convertArrayListToRealmList(shemaForCard);
        game.setShemas(realmList);

        if (playerValue < opponentValue) {
            if (higherWins.equals("true")) {
                winner = "opponent";
            } else {
                winner = "player";
            }
        } else if (opponentValue < playerValue) {
            if (higherWins.equals("true")) {
                winner = "player";
            } else {
                winner = "opponent";
            }
        } else {
            winner = "draw";
        }
        RealmList<String> values = new RealmList<>();
        values.add(value);
        values.add(valueOpponent);
        game.setValues(values);
        game.setLastWinner(winner);
        if(game.getTurn() == null) {
            game.setTurn("user");
        }

        if (game.getTurn().equals("user")) {
            game.setTurn("opponent");
        } else if (game.getTurn().equals("opponent")) {
            game.setTurn("user");
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

