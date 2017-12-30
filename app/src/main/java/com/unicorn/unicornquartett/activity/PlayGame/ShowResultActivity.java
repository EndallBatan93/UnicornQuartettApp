package com.unicorn.unicornquartett.activity.PlayGame;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.unicorn.unicornquartett.R;
import com.unicorn.unicornquartett.Utility.Constants;
import com.unicorn.unicornquartett.domain.Card;
import com.unicorn.unicornquartett.domain.Deck;
import com.unicorn.unicornquartett.domain.Game;
import com.unicorn.unicornquartett.domain.GameResult;
import com.unicorn.unicornquartett.domain.User;

import java.io.IOException;
import java.io.InputStream;

import io.realm.Realm;
import io.realm.RealmList;

import static com.unicorn.unicornquartett.Utility.Constants.*;

public class ShowResultActivity extends AppCompatActivity {
    Realm realm = Realm.getDefaultInstance();
    String category;
    Game game;
    Deck deck;
    Intent intent;
    String multiply;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_result);
        final Context context = this;

        ImageView opponentImageView = findViewById(R.id.oppentCardImage);
        TextView opponentCardName = findViewById(R.id.opponentCardName);
        TextView opponentProperty = findViewById(R.id.opponentProperty);
        TextView opponentValue = findViewById(R.id.opponentValueShow);

        TextView winnerLoser = findViewById(R.id.winnerOrLoser);
        TextView status = findViewById(R.id.status);

        TextView playerCardName = findViewById(R.id.playerCardName);
        TextView playerProperty = findViewById(R.id.playerProperty);
        TextView playerValue = findViewById(R.id.playerValueShow);
        ImageView playerImageView = findViewById(R.id.playerCardImage);

        LinearLayout mainResultLayout = findViewById(R.id.resultMainLayout);

        category = getIntent().getStringExtra(GAME_CATEGORY);
        if (category.equals(STANDARD)) {
            game = realm.where(Game.class).equalTo("id", STANDARD_GAME).findFirst();
            intent = new Intent(context, PlayStandardModeActivity.class);
            intent.putExtra(GAME_RUNNING, "true");
        } else if (category.equals(UNICORN)) {
            game = realm.where(Game.class).equalTo("id", UNICORN_GAME).findFirst();
            intent = new Intent(context, PlayUnicornModeActivity.class);
            intent.putExtra(GAME_RUNNING, "true");
        }
        deck = realm.where(Deck.class).equalTo("name", game.getDeck()).findFirst();

        mainResultLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(intent);
            }
        });

        //some unicorn stuff
        multiply = getIntent().getStringExtra(MULTIPLY);

        //opponent
        Card firstOpponentCard = game.getOpponentCards().first();
        setImage(firstOpponentCard, deck, opponentImageView);
        opponentCardName.setText(game.getOpponentCards().first().getName());
        opponentProperty.setText(game.getShemas().get(0));
        Double opponentCardValue = Double.parseDouble(game.getValues().get(1));
        if(multiply != null && multiply.equals(OPPONENT)){
            opponentCardValue = opponentCardValue * 5.0;
        }
        opponentValue.setText(opponentCardValue.toString());

        //user
        Card firstUserCard = game.getUsercards().first();
        setImage(firstUserCard, deck, playerImageView);
        playerCardName.setText(game.getUsercards().first().getName());
        playerProperty.setText(game.getShemas().get(0));
        Double playerCardValue = Double.parseDouble(game.getValues().get(0));
        if(multiply != null && multiply.equals(USER)){
            playerCardValue = playerCardValue * 5.0;
        }
        playerValue.setText(playerCardValue.toString());

        realm.beginTransaction();
        updateStacks(game.getLastWinner(), game);
        realm.commitTransaction();

        //resultView
        if (game.getLastWinner().equals(PLAYER)) {
            winnerLoser.setText("WON");
            winnerLoser.setTextColor(Color.GREEN);
        } else if (game.getLastWinner().equals(OPPONENT)) {
            winnerLoser.setText("LOST");
            winnerLoser.setTextColor(Color.RED);
        } else {
            winnerLoser.setText("DRAWN");
            winnerLoser.setTextColor(Color.BLUE);
        }
        status.setText(game.getUsercards().size() + ":" + game.getOpponentCards().size());
    }

    @Override
    public void onBackPressed() {
        //TODO Spielstand speichern
    }

    public void setImage(Card card, Deck deck, ImageView view) {
        RealmList<String> imageIdentifiers = card.getImage().getImageIdentifiers();
        String identifier = imageIdentifiers.first();
        try {
            InputStream open = getAssets().open(deck.getName().toLowerCase() + "/" + identifier);
            Drawable fromStream = Drawable.createFromStream(open, null);
            view.setImageDrawable(fromStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateStacks(String winner, Game game) {
        Card firstPlayerCard = game.getUsercards().first();
        Card firstOpponentCard = game.getOpponentCards().first();
        if (winner.equals(PLAYER)) {
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

        } else if (winner.equals(OPPONENT)) {
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

        } else if (winner.equals(DRAW)) {
            int increasedDrawn = game.getDrawnInRow() + 1;
            game.setDrawnInRow(increasedDrawn);
            game.getDrawnCards().add(firstPlayerCard);
            game.getDrawnCards().add(firstOpponentCard);
            game.getUsercards().remove(firstPlayerCard);
            game.getOpponentCards().remove(firstOpponentCard);
        }
        checkIfGameIsOver(game);
    }

    private void checkIfGameIsOver(Game game) {
        Intent intent = new Intent(this, EndGameActivity.class);
        User user = realm.where(User.class).findFirst();
        if (game.getOpponentCards().isEmpty()) {
            GameResult gameResult = realm.createObject(GameResult.class);
            gameResult.setWon(true);
            user.getStats().add(gameResult);

            intent.putExtra(WINNER, USER);
            startActivity(intent);
        } else if (game.getUsercards().isEmpty()) {
            GameResult gameResult = realm.createObject(GameResult.class);
            gameResult.setWon(false);
            user.getStats().add(gameResult);
            game.deleteFromRealm();
            intent.putExtra(WINNER, OPPONENT);
            startActivity(intent);
        }
    }

}
