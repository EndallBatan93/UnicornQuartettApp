package com.unicorn.unicornquartett.activity.Menu;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.unicorn.unicornquartett.R;
import com.unicorn.unicornquartett.activity.Decks.DeckGalleryActivity;
import com.unicorn.unicornquartett.activity.Friends.FriendActivity;
import com.unicorn.unicornquartett.activity.PlayGame.PlayGameActivity;
import com.unicorn.unicornquartett.activity.Profile.ProfileActivity;
import com.unicorn.unicornquartett.activity.Ranglist.RangListActivity;
import com.unicorn.unicornquartett.domain.User;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

public class MenuActivity extends AppCompatActivity {
    private static int RESULT_LOAD_IMAGE = 1;
    static final int REQUEST_IMAGE_CAPTURE = 1;

    final Context c = this;
    Realm realm = Realm.getDefaultInstance();
    TextView profileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        // Initializing Varables
        Button playButton = findViewById(R.id.playbutton);
        Button ranglistButton = findViewById(R.id.ranglisbutton);
        Button deckButotn = findViewById(R.id.deckbutton);
        Button friendButton = findViewById(R.id.friendButton);
        CircleImageView profileButton = findViewById(R.id.profileButton);
        Button takePhoto = findViewById(R.id.takePhoto);
//        Button selectPhoto = findViewById(R.id.selectPhoto);
        profileName = findViewById(R.id.userName);

        RealmResults<User> allUsers = realm.where(User.class).findAll();
//        loadImageFromStorage(user.getImageAbsolutePath(), user.getImageIdentifier());

        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
            }
        });

//        selectPhoto.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                dispatchSelectPictureIntent();
//            }
//        });

        if (allUsers.isEmpty()) {
            new CreateUserDialogFragment();
        }

//        SharedPreferences userData = getSharedPreferences("USER", 0);
//        SharedPreferences.Editor editor = userData.edit();
//        editor.putString("imageIdentifier", user.getName() + user.getId() + ".jpg");
//        editor.apply();
    }

//    private void dispatchSelectPictureIntent() {
//        Intent i = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//        startActivityForResult(i, RESULT_LOAD_IMAGE);
//    }


    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            String absolutePath = saveToInternalStorage(imageBitmap);
            SharedPreferences userData = getSharedPreferences("USER", 0);
            SharedPreferences.Editor editor = userData.edit();
            editor.putString("absolutePath", absolutePath);
            editor.apply();
            loadImageFromStorage();

        }

//        if (requestCode == RESULT_LOAD_IMAGE  && resultCode == RESULT_OK) {
//            //TODO: get picture from gallery into bitmap variable and set it as bitmapProperty for CircleImageView
//        }
    }

    // give parameters absolutePath and imageIdentifier and on call set user.absolutePath and user.imageIdentifier
    private void loadImageFromStorage() {
        SharedPreferences userData = getSharedPreferences("USER", 0);
        String imageIdentifier = userData.getString("imageIdentifier", "");
        String absolutePath = userData.getString("absolutePath", "");
        try {
            File f = new File(absolutePath, imageIdentifier);
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            CircleImageView profileButton = findViewById(R.id.profileButton);
            profileButton.setImageBitmap(b);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    private String saveToInternalStorage(Bitmap bitmapImage) {
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        SharedPreferences userData = getSharedPreferences("USER", 0);
        String imageIdentifier = userData.getString("imageIdentifier", "");
        File mypath = new File(directory, imageIdentifier);

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return directory.getAbsolutePath();
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

    @SuppressLint("ValidFragment")
    public class CreateUserDialogFragment extends DialogFragment {

        public CreateUserDialogFragment() {
            LayoutInflater layoutInflater = LayoutInflater.from(c);
            View createUserDialogView = layoutInflater.inflate(R.layout.dialog_create_user, null);
            AlertDialog.Builder builder = new AlertDialog.Builder(c);
            builder.setView(createUserDialogView);
            final EditText userInputDialogText = (EditText) createUserDialogView.findViewById(R.id.createUserInput);

            builder.setCancelable(false)
                    .setMessage("Hallo i bims 1 nicer User vong Creation her")
                    .setPositiveButton("Create", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            String username = String.valueOf(userInputDialogText.getText());
                            createUser(username);
                        }
                    });
            AlertDialog createUserDialog = builder.create();
            createUserDialog.show();
        }

    }

    private void createUser(String username) {
        realm.beginTransaction();
        User user = realm.createObject(User.class);
        RealmList<String> decks = new RealmList<>();
        decks.add("Bikes");
        decks.add("Tuning");
        user.setId(1);
        user.setName(username);
        user.setDifficulty("Fluffy");
        user.setFriends(null);
        user.setDecks(decks);
        user.setRunningOffline(false);
        user.setRunningOnline(false);
        realm.commitTransaction();
        realm.close();

        profileName.setText(username);
    }


}



