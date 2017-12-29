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
import com.unicorn.unicornquartett.Utility.Constants;
import com.unicorn.unicornquartett.activity.Profile.ProfileActivity;
import com.unicorn.unicornquartett.domain.User;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

import static com.unicorn.unicornquartett.Utility.Constants.BACKGROUND;
import static com.unicorn.unicornquartett.Utility.Constants.BAY_THEME;
import static com.unicorn.unicornquartett.Utility.Constants.HOB_THEME;
import static com.unicorn.unicornquartett.Utility.Constants.RAPTOR_THEME;
import static com.unicorn.unicornquartett.Utility.Constants.STANDARD_THEME;
import static com.unicorn.unicornquartett.Utility.Constants.STARWARS_THEME;
import static com.unicorn.unicornquartett.Utility.Constants.UNICORN_THEME;

public class ChooseGameActivity extends AppCompatActivity {
    Realm realm = Realm.getDefaultInstance();
    TextView profileName;
    final Context activityContext = this;
    String selectedDeck;
    @Override
    public void onResume() {
        super.onResume();
        RealmResults<User> allUsers = realm.where(User.class).findAll();
        if (!allUsers.isEmpty()) {
            User user = allUsers.first();
            setTheme(user.getTheme());
            loadImageFromStorage(user.getImageAbsolutePath(), user.getImageIdentifier());
            profileName.setText(user.getName());
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_game);
        Realm realm = Realm.getDefaultInstance();
        RealmResults<User> all = realm.where(User.class).findAll();
        User user = all.first();
        setTheme(user.getTheme());
        loadImageFromStorage(user.getImageAbsolutePath(), user.getImageIdentifier());

        Button playStandard = findViewById(R.id.playStandard);
        Button playUnicorn = findViewById(R.id.playUnicorn);

        setUserName(user);

        playStandard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DeckChooser("playStandard");
            }
        });

        playUnicorn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                new DeckChooser("playUnicorn");
            }
        });

    }

    public void setUserName(User user) {
        profileName = findViewById(R.id.userName);

        assert user != null;
        profileName.setText(user.getName());
    }

    public void goToProfileActivity(View view) {
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);

    }
    private void loadImageFromStorage(String absolutePath, String imageIdentifier) {
        try {
            File f = new File(absolutePath, imageIdentifier);
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            CircleImageView profileButton = findViewById(R.id.profileButton);
            profileButton.setImageBitmap(b);
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
                        Intent intent = new Intent(activityContext,PlayUnicornModeActivity.class);
                        intent.putExtra("selectedDeck", selectedDeck);
                        activityContext.startActivity(intent);
                    } else {
                        Intent intent = new Intent(activityContext, PlayStandardModeActivity.class);
                        intent.putExtra("selectedDeck", selectedDeck);
                        activityContext.startActivity(intent);
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
    private void setTheme(String mode) {
        ConstraintLayout layout = findViewById(R.id.playGameLayout);
        layout.setBackground(getDrawable(BACKGROUND));
        }

    }
