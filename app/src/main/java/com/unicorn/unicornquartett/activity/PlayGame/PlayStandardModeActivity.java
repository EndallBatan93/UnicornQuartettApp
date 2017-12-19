package com.unicorn.unicornquartett.activity.PlayGame;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.unicorn.unicornquartett.R;
import com.unicorn.unicornquartett.Utility.Util;
import com.unicorn.unicornquartett.activity.Menu.MenuActivity;
import com.unicorn.unicornquartett.domain.Deck;
import com.unicorn.unicornquartett.domain.User;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.Realm;

public class PlayStandardModeActivity extends AppCompatActivity {
    Realm realm = Realm.getDefaultInstance();
    TextView profileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_standard_mode);
        profileName = findViewById(R.id.userName);
        User user = getuser();
        Deck deck = getDecks();
        if (user != null) {
            loadImageFromStorage(user.getImageAbsolutePath(), user.getImageIdentifier());
            profileName.setText(user.getName());
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
}

