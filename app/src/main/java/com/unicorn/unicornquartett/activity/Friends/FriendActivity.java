package com.unicorn.unicornquartett.activity.Friends;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
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

public class FriendActivity extends AppCompatActivity {
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
        Realm realm = Realm.getDefaultInstance();
        RealmResults<User> all = realm.where(User.class).findAll();
        User user = all.first();
        setContentView(R.layout.activity_friend);
        ListView friendList = findViewById(R.id.friendList);
        profileName = findViewById(R.id.userName);
        loadImageFromStorage(user.getImageAbsolutePath(), user.getImageIdentifier());



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
