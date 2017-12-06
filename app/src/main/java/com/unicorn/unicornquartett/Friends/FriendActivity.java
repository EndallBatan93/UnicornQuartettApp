package com.unicorn.unicornquartett.Friends;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.unicorn.unicornquartett.Profile.ProfileActivity;
import com.unicorn.unicornquartett.R;

import io.realm.Realm;

public class FriendActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Realm realm = Realm.getDefaultInstance();
        setContentView(R.layout.activity_friend);
        ListView friendList = findViewById(R.id.friendList);
    }
    public void goToProfileActivity(View view) {
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);

    }
}
