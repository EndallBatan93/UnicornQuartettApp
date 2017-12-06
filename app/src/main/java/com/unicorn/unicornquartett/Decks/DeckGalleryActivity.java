package com.unicorn.unicornquartett.Decks;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.unicorn.unicornquartett.Profile.ProfileActivity;
import com.unicorn.unicornquartett.R;

import io.realm.Realm;

public class DeckGalleryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deck_gallery);
        Realm realm = Realm.getDefaultInstance();
    }
    public void goToProfileActivity(View view) {
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);

    }
}
