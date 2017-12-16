package com.unicorn.unicornquartett.activity.PlayGame;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.unicorn.unicornquartett.activity.Profile.ProfileActivity;
import com.unicorn.unicornquartett.R;
import com.unicorn.unicornquartett.domain.User;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.Realm;
import io.realm.RealmResults;

public class PlayGameActivity extends AppCompatActivity {
    Realm realm = Realm.getDefaultInstance();
    TextView profileName;
    @Override
    public void onResume() {
        super.onResume();
        RealmResults<User> allUsers = realm.where(User.class).findAll();
        if (!allUsers.isEmpty()) {
            User user = allUsers.first();
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
        loadImageFromStorage(user.getImageAbsolutePath(), user.getImageIdentifier());

        Button offlineOnline = findViewById(R.id.onlineOffline);
        Button playStandard = findViewById(R.id.playStandard);
        Button playUnicorn = findViewById(R.id.playUnicorn);
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

}
