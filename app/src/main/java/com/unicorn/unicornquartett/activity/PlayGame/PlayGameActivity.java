package com.unicorn.unicornquartett.activity.PlayGame;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.unicorn.unicornquartett.activity.Profile.ProfileActivity;
import com.unicorn.unicornquartett.R;
import com.unicorn.unicornquartett.domain.User;

import io.realm.Realm;
import io.realm.RealmResults;

public class PlayGameActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_game);
        Realm realm = Realm.getDefaultInstance();
        RealmResults<User> all = realm.where(User.class).findAll();
        User user = all.first();

        Button offlineOnline = findViewById(R.id.onlineOffline);
        Button playStandard = findViewById(R.id.playStandard);
        Button playUnicorn = findViewById(R.id.playUnicorn);
        TextView profileName = findViewById(R.id.userName);

        assert user != null;
        profileName.setText(user.getName());

    }

    public void goToProfileActivity(View view) {
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);

    }
}
