package com.unicorn.unicornquartett.Menu;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.unicorn.unicornquartett.Decks.DeckGalleryActivity;
import com.unicorn.unicornquartett.Friends.FriendActivity;
import com.unicorn.unicornquartett.PlayGame.PlayGameActivity;
import com.unicorn.unicornquartett.Profile.ProfileActivity;
import com.unicorn.unicornquartett.R;
import com.unicorn.unicornquartett.Ranglist.RangListActivity;
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
        User user = all.first();
        System.out.println(user.getId() + user.getName() + user.getDecks().size() + user.getRunningOffline() + user.getRunningOnline() + user.getDifficulty());

        // start loading screen
//        this.manageLoadingScreen();

        // Initializing Varables
        Button playButton = findViewById(R.id.playbutton);
        Button ranglistButton = findViewById(R.id.ranglisbutton);
        Button deckButotn = findViewById(R.id.deckbutton);
        Button profileButton = findViewById(R.id.profileButton);
        Button friendButton = findViewById(R.id.friendButton);

        profileButton.setText(user.getName());
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
    /**
     * makes dialog which shows loading animation
     * this methods starts a countdown of 5 second
     * on finish the dialog for loading activity is hidden
     * can siimply used everywhere else you like
     *
     * **/
    public void manageLoadingScreen() {
        final ProgressDialog dialog=new ProgressDialog(this);
        dialog.setMessage("loading");
        dialog.setCancelable(false);
        dialog.setInverseBackgroundForced(false);
        dialog.show();

        CountDownTimer countDownTimer = new CountDownTimer(5000, 1000) {
            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {
                dialog.hide();
            }
        }.start();
    }

    }



