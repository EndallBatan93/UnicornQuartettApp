package com.unicorn.unicornquartett.activity.Profile;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.unicorn.unicornquartett.R;

import io.realm.Realm;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Realm realm = Realm.getDefaultInstance();


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
}
