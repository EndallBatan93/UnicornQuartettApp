package com.unicorn.unicornquartett.activity.PlayGame;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.unicorn.unicornquartett.R;
import com.unicorn.unicornquartett.Utility.ArtificialIntelligence;
import com.unicorn.unicornquartett.domain.Card;
import com.unicorn.unicornquartett.domain.Deck;
import com.unicorn.unicornquartett.domain.Game;
import com.unicorn.unicornquartett.domain.Shema;
import com.unicorn.unicornquartett.domain.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import io.realm.Realm;
import io.realm.RealmList;

import static com.unicorn.unicornquartett.Utility.Constants.DRAW;
import static com.unicorn.unicornquartett.Utility.Constants.EVEN_STACKS;
import static com.unicorn.unicornquartett.Utility.Constants.Fun_SOUND;
import static com.unicorn.unicornquartett.Utility.Constants.GAME_CATEGORY;
import static com.unicorn.unicornquartett.Utility.Constants.GAME_RUNNING;
import static com.unicorn.unicornquartett.Utility.Constants.INSTANT_WIN;
import static com.unicorn.unicornquartett.Utility.Constants.MULTIPLY;
import static com.unicorn.unicornquartett.Utility.Constants.NONE;
import static com.unicorn.unicornquartett.Utility.Constants.OPPONENT;
import static com.unicorn.unicornquartett.Utility.Constants.OPPONENTTURN;
import static com.unicorn.unicornquartett.Utility.Constants.PLAYER;
import static com.unicorn.unicornquartett.Utility.Constants.PLAYERSTURN;
import static com.unicorn.unicornquartett.Utility.Constants.RANDOM_EVENT_TRIGGERED;
import static com.unicorn.unicornquartett.Utility.Constants.REALM_ID;
import static com.unicorn.unicornquartett.Utility.Constants.SELECTED_DECK;
import static com.unicorn.unicornquartett.Utility.Constants.SWITCH_STACKS;
import static com.unicorn.unicornquartett.Utility.Constants.SWITCH_WINNER;
import static com.unicorn.unicornquartett.Utility.Constants.UNICORN;
import static com.unicorn.unicornquartett.Utility.Constants.UNICORN_GAME;
import static com.unicorn.unicornquartett.Utility.Constants.USER;
import static com.unicorn.unicornquartett.Utility.Util.getCardImageFromStorage;

@SuppressWarnings("ConstantConditions")
public class PlayUnicornModeActivity extends AppCompatActivity {
    private final Realm realm = Realm.getDefaultInstance();
    private RealmList<Card> userCards = new RealmList<>();
    private RealmList<Card> opponentCards = new RealmList<>();
    private int currentShemaPosition;
    private String attrValue;
    private final Context c = this;
    private TextView status;
    private TextView turn;
    private TextView supriseInfo;
    private Game game;
    private Boolean isChoosen = false;
    private RealmList<String> attributes;
    private String difficulty;
    private User user;
    private Deck deck;
    private Intent intent;
    private boolean multiplyUser = false;
    private boolean multiplyOpponent = false;

    @Override
    protected void onResume() {
        super.onResume();
        setContentView(R.layout.unicorn_mode_play_view);

        status = findViewById(R.id.status);
        turn = findViewById(R.id.turn);
        Game unicornGame = realm.where(Game.class).equalTo(REALM_ID, UNICORN_GAME).findFirst();
        if (unicornGame != null) {
            handleResume();
        } else {
            handleInitialization();
        }

    }

    private void handleResume() {
        supriseInfo = findViewById(R.id.supriseInfo);

        game = realm.where(Game.class).equalTo(REALM_ID, UNICORN_GAME).findFirst();
        user = game != null ? game.getUsers().first() : null;
        difficulty = user != null ? user.getDifficulty() : null;
        deck = getDeckFromString(game.getDeck());
        opponentCards = game.getOpponentCards();
        userCards = game.getUsercards();
        setAttributes(userCards.first(), deck);
        setStatus();
        setTurn();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(c, ChooseGameActivity.class);
        startActivity(intent);
    }
    private void handleInitialization() {
        status = findViewById(R.id.status);
        turn = findViewById(R.id.turn);
        supriseInfo = findViewById(R.id.supriseInfo);

        user = getUser();
        deck = getDeckFromIntent();

        difficulty = user.getDifficulty();

        String runningGame = getIntent().getStringExtra(GAME_RUNNING);
        if ((runningGame != null) && runningGame.equals("true")) {
            game = realm.where(Game.class).equalTo(REALM_ID, UNICORN_GAME).findFirst();
            userCards = game != null ? game.getUsercards() : null;
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

    @SuppressLint("SetTextI18n")
    private void setStatus() {
        status.setText(userCards.size() + ":" + opponentCards.size());
    }


    private Deck getDeckFromIntent() {
        String selectedDeck = getIntent().getStringExtra(SELECTED_DECK);
        return realm.where(Deck.class).equalTo("name", selectedDeck).findFirst();
    }

    private Deck getDeckFromString(String deckName) {
        return deck = realm.where(Deck.class).equalTo("name", deckName).findFirst();
    }

    private User getUser() {
        return realm.where(User.class).findFirst();
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


    @SuppressLint("SetTextI18n")
    private void setAttributes(final Card card, final Deck deck) {
        intent = new Intent(c, ShowResultActivity.class);
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
        setImage(card);

        final Button chooseValue = findViewById(R.id.chooseValueButton);
        chooseValue.setBackgroundColor(Color.GRAY);
        ImageView supriseButton = findViewById(R.id.supriseButton);
        TextView status = findViewById(R.id.status);
        status.setText(userCards.size() + ":" + opponentCards.size());
        attributes = card.getAttributes();
        TextView cardName = findViewById(R.id.playingCardName);
        cardName.setText(userCards.first().getName());
        supriseButton.setImageDrawable(getDrawable(R.drawable.suprisedeactivated));

        if (game != null && game.getUserStreak() >= 3 && game.getTurn().equals(USER)) {
            supriseButton.setImageDrawable(getDrawable(R.drawable.suprise));
            Toast toast = Toast.makeText(c, "You have a suprise ", Toast.LENGTH_LONG);
            toast.show();
            supriseButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    int evenIndex = ThreadLocalRandom.current().nextInt(0, 3);
                    randomUserTriggeredUnicornEvent(evenIndex, USER);
                }
            });
            realm.beginTransaction();
            game.setUserStreak(0);
            realm.commitTransaction();
        }
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
            if(game != null && game.getOpponentStreak() >= 3 && game.getTurn().equals(OPPONENT)){
                int evenIndex = ThreadLocalRandom.current().nextInt(0, 3);
                randomUserTriggeredUnicornEvent(evenIndex, OPPONENT);
            }
            ArtificialIntelligence currentAI = new ArtificialIntelligence(deck, difficulty);
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

                    intent.putExtra(GAME_CATEGORY, UNICORN);

                    int chanceToRandomStuff = ThreadLocalRandom.current().nextInt(0, 9);
                    if(chanceToRandomStuff < 3) {
                        int randomEventIndex = ThreadLocalRandom.current().nextInt(0, 2);
                        randomRandomlyTriggeredUnicornEvent(randomEventIndex, round);
                    } else {
                        intent.putExtra(RANDOM_EVENT_TRIGGERED, NONE);
                    }
                    startActivity(intent);
                    isChoosen = false;
                }
            }
        });
    }

    private void randomRandomlyTriggeredUnicornEvent(int randomEventIndex, Game game) {
        switch (randomEventIndex) {
            case 0:
                switchStacks();
                // Stapel tauschen
                break;
            case 1:
                switchWinner(game);
                //  Gewinner wird vertauscht
                break;
            case 2:
                resetStacks();
                // HigherWins becomes LowerWins
                break;
        }
    }

    private void resetStacks() {
        RealmList<Card> tmpUserCards = this.userCards;
        RealmList<Card> tmpOpponentCards = this.opponentCards;
        int userStackSize = tmpUserCards.size();
        int opponentStackSize = tmpOpponentCards.size();
        int difference;

        if (userStackSize < opponentStackSize) {
            difference = opponentStackSize - userStackSize;
            realm.beginTransaction();
            for(int i = 0; i <= difference; i++) {
                userCards.add(opponentCards.get(i));
                opponentCards.remove(i);
            }
            realm.commitTransaction();
        } else if (userStackSize > opponentStackSize) {
            realm.beginTransaction();
            difference = userStackSize - opponentStackSize;
            for(int i = 0; i <= difference;i++) {
                opponentCards.add(userCards.get(i));
                userCards.remove(i);
            }
            realm.commitTransaction();
        }
        intent.putExtra(RANDOM_EVENT_TRIGGERED, EVEN_STACKS);
    }


    private void switchWinner(Game game) {
        String lastWinner = game.getLastWinner();
        String newWinner = "";
        if (!lastWinner.equals(DRAW)) {
            realm.beginTransaction();
            if (lastWinner.equals(PLAYER)) {
                newWinner = OPPONENT;
            } else if (lastWinner.equals(OPPONENT)) {
                newWinner = PLAYER;
            }
            game.setLastWinner(newWinner);
            realm.commitTransaction();
            intent.putExtra(RANDOM_EVENT_TRIGGERED, SWITCH_WINNER);
        }
    }

    private void switchStacks() {
        realm.beginTransaction();
        RealmList<Card> tmpuserCards = this.userCards;

        userCards = this.opponentCards;
        opponentCards = tmpuserCards;
        realm.commitTransaction();
        intent.putExtra(RANDOM_EVENT_TRIGGERED, SWITCH_STACKS);
    }

    @SuppressLint("SetTextI18n")
    private void randomUserTriggeredUnicornEvent(int evenIndex, String who) {

        switch (evenIndex) {
            case 0:
                mulitplyAttributeValue(who);
                break;
            case 1:
                instantWin(who);
                break;
            case 2:
                supriseInfo.setText("New Card");
                switchCards(who);
                break;
            case 3:
                supriseInfo.setText("Switch cards");
                switchCardsWithOpponent();
                break;
        }
    }

    private void switchCardsWithOpponent() {
        realm.beginTransaction();
        Card userFirst = userCards.first();
        Card opponentFirst = opponentCards.first();
        userCards.remove(0);
        userCards.add(opponentFirst);
        userCards.move(userCards.size() - 1, 0);
        opponentCards.remove(0);
        opponentCards.add(userFirst);
        opponentCards.move(opponentCards.size() - 1, 0);

        game.setOpponentCards(opponentCards);
        game.setUsercards(userCards);
        realm.commitTransaction();


        this.setAttributes(userCards.first(), deck);
        Toast toast = Toast.makeText(c, "You switched cards with your opponent ", Toast.LENGTH_LONG);
        toast.show();
    }

    private void switchCards(String who) {
        if (who.equals(USER)) {
            realm.beginTransaction();
            Card first = userCards.first();
            userCards.move(0, userCards.size() - 1);
            game.setUsercards(userCards);
            realm.commitTransaction();
            this.setAttributes(game.getUsercards().first(), deck);
            Toast toast = Toast.makeText(c, "You get a new card from your deck ", Toast.LENGTH_LONG);
            toast.show();
        } else if (who.equals(OPPONENT)) {
            realm.beginTransaction();
            Card first = opponentCards.first();
            opponentCards.move(0, opponentCards.size() - 1);
            game.setOpponentCards(opponentCards);
            realm.commitTransaction();
            this.setAttributes(game.getOpponentCards().first(), deck);
            Toast toast = Toast.makeText(c, "Opponent got a new Card", Toast.LENGTH_LONG);
            toast.show();
        }
    }

    @SuppressLint("SetTextI18n")
    private void instantWin(String who) {
        if (who.equals(USER)) {
            intent.putExtra(INSTANT_WIN, USER);
            supriseInfo.setText("Win");
        } else if (who.equals(OPPONENT)) {
            supriseInfo.setText("Lost");
            intent.putExtra(INSTANT_WIN, OPPONENT);
        }
    }

    private void mulitplyAttributeValue(String who) {
        if (who.equals(USER)) {
            supriseInfo.setText("*5");
            supriseInfo.setTextColor(Color.BLACK);
            multiplyUser = true;
            intent.putExtra(MULTIPLY, USER);
        } else if (who.equals(OPPONENT)) {
            multiplyOpponent = true;
            intent.putExtra(MULTIPLY, OPPONENT);
        }
    }


    private ArrayList<String> getShemaForCard(Deck deck, int i) {
        RealmList<Shema> shemas = deck.getShemaList();
        Shema shema = shemas.get(i);
        ArrayList<String> attributeDescriptionList = new ArrayList<>();
        attributeDescriptionList.add(shema != null ? shema.getProperty() : null);
        attributeDescriptionList.add(shema.getUnit());
        attributeDescriptionList.add(shema.getHigherWins().toString());
        return attributeDescriptionList;
    }

    private void setImage(Card card) {
        ImageView cardImage = findViewById(R.id.cardImage);
        Bitmap cardBitmap = getCardImageFromStorage(card.getDeckID(), card.getId());
        cardImage.setImageBitmap(cardBitmap);
    }

    private Game compareValues(String value, int position, Deck deck) {
        Card contrahentCard;
        if (game == null || game.getTurn().equals(USER)) {
            contrahentCard = opponentCards.first();
        } else {
            contrahentCard = userCards.first();
        }

        String valueOpponent = contrahentCard != null ? contrahentCard.getAttributes().get(position) : null;
        ArrayList<String> shemaForCard = getShemaForCard(deck, position);
        String higherWins = shemaForCard.get(2);
        double playerValue = Double.parseDouble(value);
        double opponentValue = Double.parseDouble(valueOpponent);
        String winner;

        if (realm.where(Game.class).equalTo(REALM_ID, UNICORN_GAME).findFirst() == null) {
            game = realm.createObject(Game.class);
            game.setId(UNICORN_GAME);
        } else {
            game = realm.where(Game.class).equalTo(REALM_ID, UNICORN_GAME).findFirst();
        }
        game.setDeck(deck.getName());
        game.setOpponentCards(opponentCards);
        game.setUsercards(userCards);
        RealmList<User> userList = new RealmList<>();
        userList.add(user);
        game.setUsers(userList);
        RealmList<String> realmList = convertArrayListToRealmList(shemaForCard);
        game.setShemas(realmList);

        if (multiplyUser) {
            playerValue = playerValue * 5;
            multiplyUser = false;
        }

        if (multiplyOpponent) {
            opponentValue = opponentValue * 5;
            multiplyOpponent = false;
        }
        if (playerValue < opponentValue) {
            if (higherWins.equals("true")) {
                winner = OPPONENT;
                game.setOpponentStreak(game.getUserStreak() + 1);
                game.setUserStreak(0);
            } else {
                winner = PLAYER;
                game.setUserStreak(game.getUserStreak() + 1);
                game.setOpponentStreak(0);
            }
        } else if (opponentValue < playerValue) {
            if (higherWins.equals("true")) {
                winner = PLAYER;
                game.setUserStreak(game.getUserStreak() + 1);
                game.setOpponentStreak(0);
            } else {
                winner = OPPONENT;
                game.setOpponentStreak(game.getUserStreak() + 1);
                game.setUserStreak(0);
            }
        } else {
            game.setOpponentStreak(0);
            game.setUserStreak(0);
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
        realmList.addAll(shemaForCard);
        return realmList;
    }


}

