package com.unicorn.unicornquartett.activity.PlayGame;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.unicorn.unicornquartett.R;
import com.unicorn.unicornquartett.Utility.Util;
import com.unicorn.unicornquartett.domain.Card;
import com.unicorn.unicornquartett.domain.Deck;
import com.unicorn.unicornquartett.domain.Game;
import com.unicorn.unicornquartett.domain.User;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.Realm;
import io.realm.RealmList;

public class ShowResultActivity extends AppCompatActivity {
    Realm realm = Realm.getDefaultInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_result);
        final Context context = this;
        Game game = realm.where(Game.class).findFirst();
        Deck deck = realm.where(Deck.class).equalTo("name", game.getDeck()).findFirst();
        ImageView opponentImageView = findViewById(R.id.oppentCardImage);
        TextView opponentProperty = findViewById(R.id.opponentProperty);
        TextView opponentValue = findViewById(R.id.opponentValueShow);

        TextView winnerLoser = findViewById(R.id.winnerOrLoser);
        TextView status = findViewById(R.id.status);

        TextView playerProperty = findViewById(R.id.playerProperty);
        TextView playerValue = findViewById(R.id.playerValueShow);
        ImageView playerImageView = findViewById(R.id.playerCardImage);

        LinearLayout mainResultLayout = findViewById(R.id.resultMainLayout);
        mainResultLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, PlayStandardModeActivity.class);
                intent.putExtra("gameRunning", "true");
                startActivity(intent);
            }
        });

        //opponent
        Card firstOpponentCard = game.getOpponentCards().first();
        setImage(firstOpponentCard,deck,opponentImageView);
        opponentProperty.setText(game.getShemas().get(0));
        opponentValue.setText(game.getValues().get(1));

        //user
        Card firstUserCard = game.getUsercards().first();
        setImage(firstUserCard,deck,playerImageView);
        playerProperty.setText(game.getShemas().get(0));
        playerValue.setText(game.getValues().get(0));

        realm.beginTransaction();
        updateStacks(game.getLastWinner(),game);
        realm.commitTransaction();

        //resultView
        if(game.getLastWinner().equals("player")) {
            winnerLoser.setText("WON");
            winnerLoser.setTextColor(Color.GREEN);
        } else if(game.getLastWinner().equals("opponent")) {
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


        if(winner.equals("player")) {
            Card first = game.getOpponentCards().first();
            game.getUsercards().add(first);
            game.getOpponentCards().remove(first);
            game.getUsercards().move(0,game.getUsercards().size()-1);
        } else {
            Card first = game.getUsercards().first();
            game.getOpponentCards().add(first);
            game.getUsercards().remove(first);
            game.getOpponentCards().move(0,game.getOpponentCards().size()-1);

        }
        Intent intent = new Intent(this, EndGameActivity.class);

        if(game.getOpponentCards().isEmpty()) {
//            if(true) {
            intent.putExtra("winner", "user");
            startActivity(intent);
        }
//        else if(true) {
        else if (game.getUsercards().isEmpty()) {
            intent.putExtra("winner", "opponent");
            startActivity(intent);
        }


    }
}
