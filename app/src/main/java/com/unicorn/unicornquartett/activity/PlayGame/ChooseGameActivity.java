package com.unicorn.unicornquartett.activity.PlayGame;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.unicorn.unicornquartett.R;
import com.unicorn.unicornquartett.activity.Menu.MenuActivity;
import com.unicorn.unicornquartett.activity.Profile.ProfileActivity;
import com.unicorn.unicornquartett.domain.Game;
import com.unicorn.unicornquartett.domain.User;

import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

import static com.unicorn.unicornquartett.Utility.Constants.BACKGROUND;
import static com.unicorn.unicornquartett.Utility.Constants.Button_SOUND;
import static com.unicorn.unicornquartett.Utility.Constants.Fun_SOUND;
import static com.unicorn.unicornquartett.Utility.Constants.PLAY_STANDARD;
import static com.unicorn.unicornquartett.Utility.Constants.PLAY_UNICORN;
import static com.unicorn.unicornquartett.Utility.Constants.REALM_ID;
import static com.unicorn.unicornquartett.Utility.Constants.SELECTED_DECK;
import static com.unicorn.unicornquartett.Utility.Constants.STANDARD;
import static com.unicorn.unicornquartett.Utility.Constants.STANDARD_GAME;
import static com.unicorn.unicornquartett.Utility.Constants.UNICORN;
import static com.unicorn.unicornquartett.Utility.Constants.UNICORN_GAME;
import static com.unicorn.unicornquartett.Utility.Util.getImageFromStorage;

public class ChooseGameActivity extends AppCompatActivity {
    private final Realm realm = Realm.getDefaultInstance();
    private final Context activityContext = this;
    private String selectedDeck;
    private Game unicornGame;
    private Game standardGame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_game);

        handleInitialization();

    }

    @SuppressLint("WrongViewCast")
    @Override
    public void onResume() {
        super.onResume();
        setContentView(R.layout.activity_play_game);

        handleInitialization();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MenuActivity.class);
        startActivity(intent);
    }

    private void handleInitialization() {
        User user = realm.where(User.class).findFirst();
        if (user != null) {
            setTheme();
            loadImageFromStorage(user.getImageAbsolutePath(), user.getImageIdentifier());
            setUserName(user);
        }
        unicornGame = realm.where(Game.class).equalTo(REALM_ID, 2).findFirst();
        standardGame = realm.where(Game.class).equalTo(REALM_ID, 1).findFirst();
        Button playStandard = findViewById(R.id.playStandard);
        Button playUnicorn = findViewById(R.id.playUnicorn);

        if (unicornGame != null) {
            playUnicorn.setText(R.string.resumeUnicorn);
        }
        if (standardGame != null) {
            playStandard.setText(R.string.resumeStandard);
        }

        playStandard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Button_SOUND.start();
                if (standardGame == null) {
                    new DeckChooser(PLAY_STANDARD);
                } else {
                    new GameResumer(PLAY_STANDARD);
                }
            }
        });

        playUnicorn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Button_SOUND.start();
                if (unicornGame == null) {
                    new DeckChooser(PLAY_UNICORN);
                } else {
                    new GameResumer(PLAY_UNICORN);
                }
            }
        });
    }

    private void setUserName(User user) {
        TextView profileName = findViewById(R.id.userName);

        profileName.setText(user.getName());
    }

    public void goToProfileActivity(View view) {
        Fun_SOUND.start();
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);

    }

    private void loadImageFromStorage(String absolutePath, String imageIdentifier) {
        CircleImageView profileButton = findViewById(R.id.profileButton);
        profileButton.setImageBitmap(getImageFromStorage(absolutePath, imageIdentifier));
    }

    @SuppressLint("ValidFragment")
    private class GameResumer extends DialogFragment {
        public GameResumer(final String mode) {
            final AlertDialog.Builder gameResumerDialog = new AlertDialog.Builder(activityContext);
            gameResumerDialog.setTitle("Resume game");
            gameResumerDialog.setNeutralButton("Delete & New", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    RealmResults<Game> unicornGames;
                    realm.beginTransaction();
                    if (mode.equals(PLAY_UNICORN)) {
                        unicornGames = realm.where(Game.class).equalTo(REALM_ID, UNICORN_GAME).findAll();
                        Game game = realm.where(Game.class).equalTo(REALM_ID, UNICORN_GAME).findFirst();
                        if(game != null){
                            selectedDeck = game.getDeck();
                        }
                    } else {
                        unicornGames = realm.where(Game.class).equalTo(REALM_ID, STANDARD_GAME).findAll();
                        Game game = realm.where(Game.class).equalTo(REALM_ID, STANDARD_GAME).findFirst();
                        if(game != null){
                            selectedDeck = game.getDeck();
                        }
                    }
                    unicornGames.deleteAllFromRealm();
                    realm.commitTransaction();
                    startGame(mode);
                }
            });
            gameResumerDialog.setPositiveButton("Resume", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    resumeGame(mode);
                }
            });
            gameResumerDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                }
            });
            gameResumerDialog.create();
            gameResumerDialog.show();
        }

        private void resumeGame(String mode) {
            if (mode.equals(PLAY_UNICORN)) {
                Intent unicornIntent = new Intent(activityContext, PlayStandardModeActivity.class);
                activityContext.startActivity(unicornIntent);
            } else {
                Intent standardIntent = new Intent(activityContext, PlayStandardModeActivity.class);
                activityContext.startActivity(standardIntent);
            }
        }
    }

    @SuppressWarnings("ConstantConditions")
    @SuppressLint("ValidFragment")
    private class DeckChooser extends DialogFragment {
        public DeckChooser(final String mode) {
            final User user = realm.where(User.class).findFirst();
            final RealmList<String> decks = user != null ? user.getDecks() : null;
            if (decks != null && decks.isEmpty()) {
                Toast noDecksToast = Toast.makeText(activityContext, "No decks found. Please download one.", Toast.LENGTH_LONG);
                noDecksToast.show();
            } else {
                final AlertDialog.Builder deckChooserDialog = new AlertDialog.Builder(activityContext);
                deckChooserDialog.setTitle("Choose your Deck")
                        .setSingleChoiceItems(decks != null ? decks.toArray(new String[decks != null ? decks.size() : 0]) : new String[0], -1, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                selectedDeck = decks.get(i);
                            }
                        });
                deckChooserDialog.setPositiveButton("Play", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startGame(mode);
                    }
                });

                deckChooserDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
                deckChooserDialog.create();
                deckChooserDialog.show();
            }
        }
    }

    private void startGame(String mode) {
        if (mode.equals(PLAY_UNICORN)) {
            startGameActivity(UNICORN);
        } else {
            startGameActivity(STANDARD);
        }
    }

    private void startGameActivity(String mode) {
        Intent intent;
        if (mode.equals(UNICORN)) {
            intent = new Intent(activityContext, PlayUnicornModeActivity.class);
        } else {
            intent = new Intent(activityContext, PlayStandardModeActivity.class);
        }
        intent.putExtra(SELECTED_DECK, selectedDeck);
        activityContext.startActivity(intent);
    }

    private void setTheme() {
        ConstraintLayout layout = findViewById(R.id.playGameLayout);
        layout.setBackground(getDrawable(BACKGROUND));
    }

}
