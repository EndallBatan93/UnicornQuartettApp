package com.unicorn.unicornquartett.activity.Friends;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.unicorn.unicornquartett.activity.Profile.ProfileActivity;
import com.unicorn.unicornquartett.R;
import com.unicorn.unicornquartett.domain.User;

import io.realm.Realm;
import io.realm.RealmResults;

public class FriendActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Realm realm = Realm.getDefaultInstance();
        RealmResults<User> all = realm.where(User.class).findAll();
        User user = all.first();

        setContentView(R.layout.activity_friend);
        ListView friendList = findViewById(R.id.friendList);
        TextView profileName = findViewById(R.id.userName);

        assert user != null;
        profileName.setText(user.getName());
    }
    public void goToProfileActivity(View view) {
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);

    }
}
