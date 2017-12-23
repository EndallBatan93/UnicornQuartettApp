package com.unicorn.unicornquartett.activity.PlayGame;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.unicorn.unicornquartett.R;
import com.unicorn.unicornquartett.Utility.Util;
import com.unicorn.unicornquartett.domain.Deck;
import com.unicorn.unicornquartett.domain.User;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

public class
PlayUnicornModeActivity extends AppCompatActivity {
    Realm realm = Realm.getDefaultInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_game_view);
        User user = getuser();
        Deck deck = getDecks();
        if (user != null) {
            loadImageFromStorage(user.getImageAbsolutePath(), user.getImageIdentifier());
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
        Util.verifyStoragePermissions(PlayUnicornModeActivity.this);
        try {
            File f = new File(absolutePath, imageIdentifier);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            options.inSampleSize = 3;
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            int imageHeigth = options.outHeight;
            int imageWidth = options.outWidth;
            String imageType = options.outMimeType;

            CircleImageView profileButton = findViewById(R.id.profileButton);
            profileButton.setImageBitmap(b);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
