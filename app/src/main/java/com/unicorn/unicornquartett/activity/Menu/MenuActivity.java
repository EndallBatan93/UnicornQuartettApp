package com.unicorn.unicornquartett.activity.Menu;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.unicorn.unicornquartett.activity.Decks.DeckGalleryActivity;
import com.unicorn.unicornquartett.activity.Friends.FriendActivity;
import com.unicorn.unicornquartett.activity.PlayGame.PlayGameActivity;
import com.unicorn.unicornquartett.activity.Profile.ProfileActivity;
import com.unicorn.unicornquartett.R;
import com.unicorn.unicornquartett.activity.Ranglist.RangListActivity;
import com.unicorn.unicornquartett.domain.User;

import io.realm.Realm;
import io.realm.RealmResults;

public class MenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        Realm realm = Realm.getDefaultInstance();
        RealmResults<User> all = realm.where(User.class).findAll();
        if(all.size()!= 0) {
            User user = all.first();
        }


        // Initializing Varables
        Button playButton = findViewById(R.id.playbutton);
        Button ranglistButton = findViewById(R.id.ranglisbutton);
        Button deckButotn = findViewById(R.id.deckbutton);
        Button profileButton = findViewById(R.id.profileButton);
        Button friendButton = findViewById(R.id.friendButton);

    }


    // Navigation Methods
    public void goToPlayGameActivity(View view) {
        Intent intent = new Intent(this, PlayGameActivity.class);
        startActivity(intent);

    }

    public void goToProfileActivity(View view) {
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);

    }

    public void goToRangListActivity(View view) {
        Intent intent = new Intent(this, RangListActivity.class);
        startActivity(intent);

    }

    public void goToDeckGalleryActivity(View view) {
        Intent intent = new Intent(this, DeckGalleryActivity.class);
        startActivity(intent);

    }

    public void goToFriendActivity(View view) {
        Intent intent = new Intent(this, FriendActivity.class);
        startActivity(intent);

    }


}



