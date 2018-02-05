package com.unicorn.unicornquartett.activity.Ranglist;

import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.unicorn.unicornquartett.R;
import com.unicorn.unicornquartett.activity.Profile.ProfileActivity;
import com.unicorn.unicornquartett.domain.User;

import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.Realm;
import io.realm.RealmResults;

import static com.unicorn.unicornquartett.Utility.Constants.BACKGROUND;
import static com.unicorn.unicornquartett.Utility.Util.getImageFromStorage;

public class RangListActivity extends AppCompatActivity {
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
        setContentView(R.layout.activity_rang_list);
        Realm realm = Realm.getDefaultInstance();
        RealmResults<User> all = realm.where(User.class).findAll();
        User user = all.first();
        assert user != null;
        setTheme();
        loadImageFromStorage(user.getImageAbsolutePath(), user.getImageIdentifier());
        ListView rangList = findViewById(R.id.ranglistView);
        profileName = findViewById(R.id.userName);

        assert user != null;
        profileName.setText(user.getName());


    }
    public void goToProfileActivity(View view) {
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);

    }
    private void loadImageFromStorage(String absolutePath, String imageIdentifier) {
        CircleImageView profileButton = findViewById(R.id.profileButton);
        profileButton.setImageBitmap(getImageFromStorage(absolutePath, imageIdentifier));
    }

    private void setTheme() {
        ConstraintLayout layout = findViewById(R.id.ranglistLayout);
        layout.setBackground(getDrawable(BACKGROUND));
    }

}
