package com.unicorn.unicornquartett.activity.Friends;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.unicorn.unicornquartett.Utility.Constants;
import com.unicorn.unicornquartett.activity.Profile.ProfileActivity;
import com.unicorn.unicornquartett.R;
import com.unicorn.unicornquartett.domain.User;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.Realm;
import io.realm.RealmResults;

import static com.unicorn.unicornquartett.Utility.Constants.BACKGROUND;

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
        setContentView(R.layout.activity_friend);
        Realm realm = Realm.getDefaultInstance();
        RealmResults<User> all = realm.where(User.class).findAll();
        User user = all.first();
        assert user != null;
        setTheme();
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
            int height = b.getHeight();
            int width = b.getWidth();
            if (height > 4000 || width > 4000) {
                height = height / Constants.ULTRA_HIGH_FACTOR;
                width = width / Constants.ULTRA_HIGH_FACTOR;
            } else if (height > 2000 || width > 2000) {
                height = height / Constants.HIGH_FACTOR;
                width = width / Constants.HIGH_FACTOR;
            } else if (height > 1000 || width > 1000) {
                height = height / Constants.MEDIUM_FACTOR;
                width = width / Constants.MEDIUM_FACTOR;
            } else if (height > 700 || width > 700) {
                height = height / Constants.LOW_FACTOR;
                width = width / Constants.LOW_FACTOR;
            }
            Bitmap scaledBitmap = Bitmap.createScaledBitmap(b, width, height, true);
            CircleImageView profileButton = findViewById(R.id.profileButton);
            profileButton.setImageBitmap(scaledBitmap);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }
    private void setTheme() {
        ConstraintLayout layout = findViewById(R.id.constraintLayoutFriend);
        layout.setBackground(getDrawable(BACKGROUND));
    }

}
