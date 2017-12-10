package com.unicorn.unicornquartett.activity.Profile;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.unicorn.unicornquartett.R;
import com.unicorn.unicornquartett.domain.User;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.Realm;
import io.realm.RealmResults;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Realm realm = Realm.getDefaultInstance();
        RealmResults<User> all = realm.where(User.class).findAll();
        User user = all.first();
        //loadImageFromStorage(user.getImageAbsolutePath(), user.getImageIdentifier());

        ListView wonList = findViewById(R.id.wonView);
        ListView lostList = findViewById(R.id.lostView);
        TextView usernameTextView = findViewById(R.id.username);
        TextView registeredTextView = findViewById(R.id.registered);
        Button googlePlayButton = findViewById(R.id.googleButton);
        Button difficultyButton = findViewById(R.id.difficultyButton);
        Button editButton = findViewById(R.id.editbutton);

    }

    public void connectToGooglePlay(View view) {
        //tbd

    }

    private void loadImageFromStorage()
    {
        SharedPreferences userData = getSharedPreferences("USER", 0);
        String imageIdentifier = userData.getString("imageIdentifier", "");
        String absolutePath = userData.getString("absolutePath", "");
        try {
            File f=new File(absolutePath, imageIdentifier);
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            CircleImageView profileButton = findViewById(R.id.profileButton);
            profileButton.setImageBitmap(b);
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }

    }

}
