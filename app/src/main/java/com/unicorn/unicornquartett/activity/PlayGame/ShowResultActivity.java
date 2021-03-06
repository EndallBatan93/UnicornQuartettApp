package com.unicorn.unicornquartett.activity.PlayGame;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.unicorn.unicornquartett.R;
import com.unicorn.unicornquartett.domain.Card;
import com.unicorn.unicornquartett.domain.Deck;
import com.unicorn.unicornquartett.domain.Game;
import com.unicorn.unicornquartett.domain.GameResult;
import com.unicorn.unicornquartett.domain.User;

import java.util.Objects;

import io.realm.Realm;
import io.realm.RealmResults;

import static com.unicorn.unicornquartett.Utility.Constants.DRAW;
import static com.unicorn.unicornquartett.Utility.Constants.EVEN_STACKS;
import static com.unicorn.unicornquartett.Utility.Constants.GAME_CATEGORY;
import static com.unicorn.unicornquartett.Utility.Constants.GAME_RUNNING;
import static com.unicorn.unicornquartett.Utility.Constants.INSTANT_WIN;
import static com.unicorn.unicornquartett.Utility.Constants.MULTIPLY;
import static com.unicorn.unicornquartett.Utility.Constants.NONE;
import static com.unicorn.unicornquartett.Utility.Constants.OPPONENT;
import static com.unicorn.unicornquartett.Utility.Constants.PLAYER;
import static com.unicorn.unicornquartett.Utility.Constants.RANDOM_EVENT_TRIGGERED;
import static com.unicorn.unicornquartett.Utility.Constants.REALM_ID;
import static com.unicorn.unicornquartett.Utility.Constants.STANDARD;
import static com.unicorn.unicornquartett.Utility.Constants.STANDARD_GAME;
import static com.unicorn.unicornquartett.Utility.Constants.SWITCH_STACKS;
import static com.unicorn.unicornquartett.Utility.Constants.SWITCH_WINNER;
import static com.unicorn.unicornquartett.Utility.Constants.UNICORN;
import static com.unicorn.unicornquartett.Utility.Constants.UNICORN_GAME;
import static com.unicorn.unicornquartett.Utility.Constants.USER;
import static com.unicorn.unicornquartett.Utility.Constants.WINNER;
import static com.unicorn.unicornquartett.Utility.Util.getCardImageFromStorage;

@SuppressWarnings("ConstantConditions")
public class ShowResultActivity extends AppCompatActivity {
    private final Realm realm = Realm.getDefaultInstance();
    private Game game;
    private Intent playIntent;
    private Intent finishIntent;
    private TextView status;
    private TextView winnerLoser;
    private User user;
    private RealmResults<Game> gamesToDelete;
    private final Context context = this;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_result);
        final Context context = this;

        finishIntent = new Intent(this, EndGameActivity.class);
        user = realm.where(User.class).findFirst();

        //some unicorn stuff
        String multiply = getIntent().getStringExtra(MULTIPLY);
        String instantWin = getIntent().getStringExtra(INSTANT_WIN);
        String category = getIntent().getStringExtra(GAME_CATEGORY);
        String randomEvent = getIntent().getStringExtra(RANDOM_EVENT_TRIGGERED);

        if (category != null && category.equals(STANDARD)) {
            game = realm.where(Game.class).equalTo(REALM_ID, STANDARD_GAME).findFirst();
            gamesToDelete = realm.where(Game.class).equalTo(REALM_ID, STANDARD_GAME).findAll();
        } else if (category != null && category.equals(UNICORN)) {
            game = realm.where(Game.class).equalTo(REALM_ID, UNICORN_GAME).findFirst();
            gamesToDelete = realm.where(Game.class).equalTo(REALM_ID, UNICORN_GAME).findAll();
            switch (randomEvent) {
                case NONE:
                    break;
                case SWITCH_STACKS:
                    new RandomEventTriggeredDialog("Ups! Switched the card stacks?!\n");
                    break;
                case SWITCH_WINNER:
                    new RandomEventTriggeredDialog("Ups! Switched the winner ????!!!!\n");
                    break;
                case EVEN_STACKS:
                    new RandomEventTriggeredDialog("Ups!? Evened the ods :D\n");
                    break;
            }
        }

        if (instantWin != null && instantWin.equals(USER)) {
            createUserWonGame(user);
        } else if (instantWin != null && instantWin.equals(OPPONENT)) {
            createOpponentWonGame(user);
        } else {

            // View
            ImageView opponentImageView = findViewById(R.id.oppentCardImage);
            TextView opponentCardName = findViewById(R.id.opponentCardName);
            TextView opponentProperty = findViewById(R.id.opponentProperty);
            TextView opponentValue = findViewById(R.id.opponentValueShow);

            winnerLoser = findViewById(R.id.winnerOrLoser);
            status = findViewById(R.id.status);

            TextView playerCardName = findViewById(R.id.playerCardName);
            TextView playerProperty = findViewById(R.id.playerProperty);
            TextView playerValue = findViewById(R.id.playerValueShow);
            ImageView playerImageView = findViewById(R.id.playerCardImage);

            LinearLayout mainResultLayout = findViewById(R.id.resultMainLayout);

            if (Objects.equals(category, STANDARD)) {
                game = realm.where(Game.class).equalTo("id", STANDARD_GAME).findFirst();
                playIntent = new Intent(context, PlayStandardModeActivity.class);
                playIntent.putExtra(GAME_RUNNING, "true");
            } else if (category.equals(UNICORN)) {
                game = realm.where(Game.class).equalTo("id", UNICORN_GAME).findFirst();
                playIntent = new Intent(context, PlayUnicornModeActivity.class);
                playIntent.putExtra(GAME_RUNNING, "true");
            }
            Deck deck = realm.where(Deck.class).equalTo("name", game.getDeck()).findFirst();

            mainResultLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(playIntent);
                }
            });


            //opponent
            Card firstOpponentCard = game.getOpponentCards().first();
            setImage(firstOpponentCard, opponentImageView);
            opponentCardName.setText(game.getOpponentCards().first().getName());
            opponentProperty.setText(game.getShemas().get(0));
            Double opponentCardValue = Double.parseDouble(game.getValues().get(1));
            if (multiply != null && multiply.equals(OPPONENT)) {
                opponentCardValue = opponentCardValue * 5.0;
            }
            opponentValue.setText(opponentCardValue.toString());

            //user
            Card firstUserCard = game.getUsercards().first();
            setImage(firstUserCard, playerImageView);
            playerCardName.setText(game.getUsercards().first().getName());
            playerProperty.setText(game.getShemas().get(0));
            Double playerCardValue = Double.parseDouble(game.getValues().get(0));
            if (multiply != null && multiply.equals(USER)) {
                playerCardValue = playerCardValue * 5.0;
            }
            playerValue.setText(playerCardValue.toString());

            updateStacks(game.getLastWinner(), game);

        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(context, ChooseGameActivity.class);
        startActivity(intent);
    }

    private void setImage(Card card, ImageView cardView) {
        Bitmap cardBitmap = getCardImageFromStorage(card.getDeckID(), card.getId());
        cardView.setImageBitmap(cardBitmap);
    }

    @SuppressLint("SetTextI18n")
    private void updateStacks(String winner, Game game) {
        realm.beginTransaction();
        Card firstPlayerCard = game.getUsercards().first();
        Card firstOpponentCard = game.getOpponentCards().first();
        switch (winner) {
            case PLAYER:
                game.getUsercards().add(firstOpponentCard);
                game.getOpponentCards().remove(firstOpponentCard);
                game.getUsercards().move(0, game.getUsercards().size() - 1);
                if (game.getDrawnCards() != null && !game.getDrawnCards().isEmpty()) {
                    for (Card card : game.getDrawnCards()) {
                        game.getUsercards().add(card);
                    }
                    game.getDrawnCards().clear();
                    game.setDrawnInRow(0);
                }

                break;
            case OPPONENT:
                game.getOpponentCards().add(firstPlayerCard);
                game.getUsercards().remove(firstPlayerCard);
                game.getOpponentCards().move(0, game.getOpponentCards().size() - 1);
                if (game.getDrawnCards() != null && !game.getDrawnCards().isEmpty()) {
                    for (Card card : game.getDrawnCards()) {
                        game.getOpponentCards().add(card);
                    }
                    game.getDrawnCards().clear();
                    game.setDrawnInRow(0);
                }

                break;
            case DRAW:
                int increasedDrawn = game.getDrawnInRow() + 1;
                game.setDrawnInRow(increasedDrawn);
                game.getDrawnCards().add(firstPlayerCard);
                game.getDrawnCards().add(firstOpponentCard);
                game.getUsercards().remove(firstPlayerCard);
                game.getOpponentCards().remove(firstOpponentCard);
                break;
        }

        //resultView
        switch (game.getLastWinner()) {
            case PLAYER:
                winnerLoser.setText("WON");
                winnerLoser.setTextColor(Color.GREEN);
                break;
            case OPPONENT:
                winnerLoser.setText("LOST");
                winnerLoser.setTextColor(Color.RED);
                break;
            default:
                winnerLoser.setText("DRAWN");
                winnerLoser.setTextColor(Color.BLUE);
                break;
        }
        status.setText(game.getUsercards().size() + ":" + game.getOpponentCards().size());

        checkIfGameIsOver(game);
    }

    private void checkIfGameIsOver(Game game) {
        realm.commitTransaction();
        if (game.getDrawnCards().isEmpty()) {
            if (game.getOpponentCards().isEmpty()) {
                createUserWonGame(user);
            } else if (game.getUsercards().isEmpty()) {
                createOpponentWonGame(user);
            }
        }
    }

    private void createUserWonGame(User user) {
        realm.beginTransaction();
        GameResult gameResult = realm.createObject(GameResult.class);
        gameResult.setWon(true);
        user.getStats().add(gameResult);
        gamesToDelete.deleteAllFromRealm();
        realm.commitTransaction();
        finishIntent.putExtra(WINNER, USER);
        startActivity(finishIntent);
    }

    private void createOpponentWonGame(User user) {
        realm.beginTransaction();
        GameResult gameResult = realm.createObject(GameResult.class);
        gameResult.setWon(false);
        user.getStats().add(gameResult);
        gamesToDelete.deleteAllFromRealm();
        realm.commitTransaction();
        finishIntent.putExtra(WINNER, OPPONENT);
        startActivity(finishIntent);
    }

    @SuppressLint("ValidFragment")
    private class RandomEventTriggeredDialog extends DialogFragment {
        public RandomEventTriggeredDialog(final String text) {
            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
            alertDialog.setTitle("Random Event appeared:");
            alertDialog.setMessage(text);
            alertDialog.create();
            alertDialog.show();
        }
    }
}
