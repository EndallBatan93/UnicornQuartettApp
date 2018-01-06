package com.unicorn.unicornquartett.activity.PlayGame;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.unicorn.unicornquartett.R;
import com.unicorn.unicornquartett.activity.Menu.MenuActivity;
import com.unicorn.unicornquartett.activity.Profile.ProfileActivity;
import com.unicorn.unicornquartett.domain.Game;
import com.unicorn.unicornquartett.domain.User;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.Realm;
import io.realm.RealmList;

import static com.unicorn.unicornquartett.Utility.Constants.BACKGROUND;
import static com.unicorn.unicornquartett.Utility.Constants.Button_SOUND;
import static com.unicorn.unicornquartett.Utility.Constants.Fun_SOUND;
import static com.unicorn.unicornquartett.Utility.Constants.HIGH_FACTOR;
import static com.unicorn.unicornquartett.Utility.Constants.LOW_FACTOR;
import static com.unicorn.unicornquartett.Utility.Constants.MEDIUM_FACTOR;
import static com.unicorn.unicornquartett.Utility.Constants.REALM_ID;
import static com.unicorn.unicornquartett.Utility.Constants.SELECTED_DECK;
import static com.unicorn.unicornquartett.Utility.Constants.STANDARD;
import static com.unicorn.unicornquartett.Utility.Constants.ULTRA_HIGH_FACTOR;
import static com.unicorn.unicornquartett.Utility.Constants.UNICORN;

public class ChooseGameActivity extends AppCompatActivity {
    Realm realm = Realm.getDefaultInstance();
    TextView profileName;
    final Context activityContext = this;
    String selectedDeck;
    User user;
    Game unicornGame;
    Game standardGame;
    Button playStandard;
    Button playUnicorn;

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
        user = realm.where(User.class).findFirst();
        if (user != null) {
            setTheme(user.getTheme());
            loadImageFromStorage(user.getImageAbsolutePath(), user.getImageIdentifier());
            setUserName(user);
        }
        unicornGame = realm.where(Game.class).equalTo(REALM_ID, 2).findFirst();
        standardGame = realm.where(Game.class).equalTo(REALM_ID, 1).findFirst();
        playStandard = findViewById(R.id.playStandard);
        playUnicorn = findViewById(R.id.playUnicorn);

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
                    new DeckChooser("playStandard");
                } else {
                    Intent intent = new Intent(activityContext, PlayStandardModeActivity.class);
                    activityContext.startActivity(intent);
                }
            }
        });

        playUnicorn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Button_SOUND.start();
                if (unicornGame == null) {
                    new DeckChooser("playUnicorn");
                } else {
                    Intent intent = new Intent(activityContext, PlayUnicornModeActivity.class);
                    activityContext.startActivity(intent);
                }
            }
        });
    }

    public void setUserName(User user) {
        profileName = findViewById(R.id.userName);

        assert user != null;
        profileName.setText(user.getName());
    }

    public void goToProfileActivity(View view) {
        Fun_SOUND.start();
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);

    }

    private void loadImageFromStorage(String absolutePath, String imageIdentifier) {
        try {
            File f = new File(absolutePath, imageIdentifier);
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            int height = b.getHeight();
            int width = b.getWidth();
            if (height > 4000 || width > 4000) {
                height = height / ULTRA_HIGH_FACTOR;
                width = width / ULTRA_HIGH_FACTOR;
            } else if (height > 2000 || width > 2000) {
                height = height / HIGH_FACTOR;
                width = width / HIGH_FACTOR;
            } else if (height > 1000 || width > 1000) {
                height = height / MEDIUM_FACTOR;
                width = width / MEDIUM_FACTOR;
            } else if (height > 700 || width > 700) {
                height = height / LOW_FACTOR;
                width = width / LOW_FACTOR;
            }
            Bitmap scaledBitmap = Bitmap.createScaledBitmap(b, width, height, true);
            CircleImageView profileButton = findViewById(R.id.profileButton);
            profileButton.setImageBitmap(scaledBitmap);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    @SuppressLint("ValidFragment")
    private class DeckChooser extends DialogFragment {
        public DeckChooser(final String mode) {
            final User user = realm.where(User.class).findFirst();
            final RealmList<String> decks = user.getDecks();
            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(activityContext);
            alertDialog.setTitle("Choose your Deck")
                    .setSingleChoiceItems(decks.toArray(new String[decks.size()]), -1, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            selectedDeck = decks.get(i);
                        }
                    });
            alertDialog.setPositiveButton("Play", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if (mode.equals("playUnicorn")) {
                        startGameActivity(UNICORN);
                    } else {
                        startGameActivity(STANDARD);
                    }
                }
            });

            alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                }
            });
            alertDialog.create();
            alertDialog.show();
        }
    }

    private void startGameActivity(String game) {
        Intent intent;
        if (game.equals(UNICORN)) {
            intent = new Intent(activityContext, PlayUnicornModeActivity.class);
        } else {
            intent = new Intent(activityContext, PlayStandardModeActivity.class);

        }
        intent.putExtra(SELECTED_DECK, selectedDeck);
        activityContext.startActivity(intent);
    }

    private void setTheme(String mode) {
        ConstraintLayout layout = findViewById(R.id.playGameLayout);
        layout.setBackground(getDrawable(BACKGROUND));
    }

}
