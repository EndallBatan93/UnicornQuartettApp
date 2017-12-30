package com.unicorn.unicornquartett.activity.Menu;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.text.method.BaseKeyListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.unicorn.unicornquartett.R;
import com.unicorn.unicornquartett.Utility.Constants;
import com.unicorn.unicornquartett.Utility.Util;
import com.unicorn.unicornquartett.activity.Decks.DeckGalleryActivity;
import com.unicorn.unicornquartett.activity.Friends.FriendActivity;
import com.unicorn.unicornquartett.activity.PlayGame.ChooseGameActivity;
import com.unicorn.unicornquartett.activity.Profile.ProfileActivity;
import com.unicorn.unicornquartett.activity.Ranglist.RangListActivity;
import com.unicorn.unicornquartett.domain.Game;
import com.unicorn.unicornquartett.domain.GameResult;
import com.unicorn.unicornquartett.domain.User;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

import static com.unicorn.unicornquartett.Utility.Constants.*;
import static com.unicorn.unicornquartett.Utility.Constants.BACKGROUND;
import static com.unicorn.unicornquartett.Utility.Constants.BAY_THEME;
import static com.unicorn.unicornquartett.Utility.Constants.Button_SOUND;
import static com.unicorn.unicornquartett.Utility.Constants.Fun_SOUND;
import static com.unicorn.unicornquartett.Utility.Constants.HIGH_FACTOR;
import static com.unicorn.unicornquartett.Utility.Constants.HOB_THEME;
import static com.unicorn.unicornquartett.Utility.Constants.INTRO_SOUND;
import static com.unicorn.unicornquartett.Utility.Constants.MEDIUM_FACTOR;
import static com.unicorn.unicornquartett.Utility.Constants.RAPTOR_THEME;
import static com.unicorn.unicornquartett.Utility.Constants.STANDARD_THEME;
import static com.unicorn.unicornquartett.Utility.Constants.STARWARS_THEME;
import static com.unicorn.unicornquartett.Utility.Constants.ULTRA_HIGH_FACTOR;
import static com.unicorn.unicornquartett.Utility.Constants.UNICORN_THEME;
import static com.unicorn.unicornquartett.Utility.Util.*;

public class MenuActivity extends AppCompatActivity {
    private static final int REQUEST_FROM_GALLERY = 2;
    static final int REQUEST_TAKE_PHOTO = 1;


    String mCurrentPhotoPath;
    final Context c = this;
    Realm realm = Realm.getDefaultInstance();
    TextView profileName;
    String theme;


    @Override
    public void onResume() {
//        final MediaPlayer mp = MediaPlayer.create(this, R.raw.horse);
//        mp.start();
        super.onResume();
        RealmResults<User> allUsers = realm.where(User.class).findAll();
        if (!allUsers.isEmpty()) {
            User user = allUsers.first();
            assert user != null;
            loadImageFromStorage(user.getImageAbsolutePath(), user.getImageIdentifier());
            profileName.setText(user.getName());
            if (user.getTheme() != null) {
                setTheme();
                INTRO_SOUND.start();
            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.activity_menu);
//
//        mp.start();
        // Initializing Varables
        Button playButton = findViewById(R.id.playbutton);
        Button ranglistButton = findViewById(R.id.ranglisbutton);
        Button deckButotn = findViewById(R.id.deckbutton);
        Button friendButton = findViewById(R.id.friendButton);
        profileName = findViewById(R.id.userName);
        Game unicorn = realm.where(Game.class).equalTo("id", 2).findFirst();
        Game standard = realm.where(Game.class).equalTo("id", 1).findFirst();
        RealmResults<User> allUsers = realm.where(User.class).findAll();

        if (unicorn != null && standard != null) {
            playButton.setText(R.string.PlayGame2);
        } else if (unicorn != null) {
            playButton.setText(R.string.PlayGame1);
        } else if (standard != null) {
            playButton.setText(R.string.PlayGame1);

        }
        if (allUsers.isEmpty()) {
            new CreateUserDialogFragment();

        } else {
            User user = allUsers.first();
            loadImageFromStorage(user.getImageAbsolutePath(), user.getImageIdentifier());
            profileName.setText(user.getName());
            if (user.getTheme() != null) {
                initializeTheme(user.getTheme());
                INTRO_SOUND.start();
            }
        }
    }


    // give parameters absolutePath and imageIdentifier and on call set user.absolutePath and user.imageIdentifier
    private void loadImageFromStorage(String absolutePath, String imageIdentifier) {
        verifyStoragePermissions(MenuActivity.this);
        try {
            File f = new File(absolutePath, imageIdentifier);
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            int height = b.getHeight();
            int width = b.getWidth();
            if (height > 4000 || width > 4000) {
                height = height / ULTRA_HIGH_FACTOR;
                width = width / ULTRA_HIGH_FACTOR;
            } else if (height > 2000 || width > 2000) {
                height = height / HIGH_FACTOR;
                width = width / HIGH_FACTOR;
            } else if (height > 1000 || width > 1000) {
                height = height / MEDIUM_FACTOR;
                width = width / MEDIUM_FACTOR;
            } else if (height > 700 || width > 700) {
                height = height / LOW_FACTOR;
                width = width / LOW_FACTOR;
            }
            Bitmap scaledBitmap = Bitmap.createScaledBitmap(b, width, height, true);
            CircleImageView profileButton = findViewById(R.id.profileButton);
            profileButton.setImageBitmap(scaledBitmap);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }


    private File createImageFile() throws IOException {
        User user = realm.where(User.class).findFirst();
        String imageFileName = user.getImageIdentifier();
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        user.setImageAbsolutePath(storageDir.getAbsolutePath());
        File image = new File(storageDir, imageFileName);


        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();

            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    private void choosePictureFromGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(galleryIntent, REQUEST_FROM_GALLERY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TAKE_PHOTO) {
            if (resultCode == RESULT_OK) {
                User user = realm.where(User.class).findFirst();
                loadImageFromStorage(user.getImageAbsolutePath(), user.getImageIdentifier());
            }
        } else if (requestCode == REQUEST_FROM_GALLERY) {
            if (resultCode != RESULT_CANCELED) {
                Uri contentURI = data.getData();
                String absolutePath = getRealPathFromURI(contentURI);
                User first = realm.where(User.class).findFirst();
                String identifier = absolutePath.substring(absolutePath.lastIndexOf('/') + 1);
                String absPath = absolutePath.replace(identifier, "");
                first.setImageAbsolutePath(absPath);
                first.setImageIdentifier(identifier);
                loadImageFromStorage(absPath, identifier);
            }

        }
        realm.commitTransaction();
        new ThemeChooser();
    }

    private String getRealPathFromURI(Uri contentUri) {
        String result;
        Cursor cursor = this.getContentResolver().query(contentUri, null, null, null, null);
        if (cursor == null) {
            result = contentUri.getPath();
        } else {
            cursor.moveToFirst();
            int indx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(indx);
            cursor.close();
        }
        return result;
    }

    // Navigation Methods
    public void goToPlayGameActivity(View view) {
        Button_SOUND.start();
        Intent intent = new Intent(this, ChooseGameActivity.class);
        startActivity(intent);


    }

    public void goToProfileActivity(View view) {
        Fun_SOUND.start();
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);

    }

    public void goToRangListActivity(View view) {
        Button_SOUND.start();
        Intent intent = new Intent(this, RangListActivity.class);
        startActivity(intent);

    }

    public void goToDeckGalleryActivity(View view) {
        Button_SOUND.start();
        Intent intent = new Intent(this, DeckGalleryActivity.class);
        startActivity(intent);

    }

    public void goToFriendActivity(View view) {
        Button_SOUND.start();
        Intent intent = new Intent(this, FriendActivity.class);
        startActivity(intent);

    }

    @SuppressLint("ValidFragment")
    private class CreateUserDialogFragment extends DialogFragment {

        public CreateUserDialogFragment() {
            LayoutInflater layoutInflater = LayoutInflater.from(c);
            View createUserDialogView = layoutInflater.inflate(R.layout.dialog_create_user, null);
            AlertDialog.Builder builder = new AlertDialog.Builder(c);
            builder.setView(createUserDialogView);
            final EditText userInputDialogText = (EditText) createUserDialogView.findViewById(R.id.createUserInput);

            builder.setCancelable(false)
                    .setMessage("Ich bin so süss ich könnte Zucker pupsen")
                    .setPositiveButton("Create", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            String username = String.valueOf(userInputDialogText.getText());
                            createUser(username);
                            takePictureDialog();
                        }
                    });
            AlertDialog createUserDialog = builder.create();
            createUserDialog.show();
        }

        private void takePictureDialog() {

            LayoutInflater layoutInflater = LayoutInflater.from(c);
            View createPhotoDialogView = layoutInflater.inflate(R.layout.dialog_create_photo, null);
            AlertDialog.Builder builder = new AlertDialog.Builder(c);
            builder.setView(createPhotoDialogView);

            builder.setCancelable(false)
                    .setPositiveButton("Take picture", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dispatchTakePictureIntent();
                        }
                    })
                    .setNeutralButton("Choose picture", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            choosePictureFromGallery();
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
        user.setImageIdentifier(user.getName() + user.getId() + ".jpg");
        user.setDate(new Date());
        user.setStats(createTestData(user));
        profileName.setText(username);
    }

    private RealmList<GameResult> createTestData(User user) {
        RealmList<GameResult> results = new RealmList<>();
//        GameResult object = realm.createObject(GameResult.class);
//        object.setId(0);
//        object.setWon(true);
//
//        GameResult object1 = realm.createObject(GameResult.class);
//        object1.setId(1);
//        object1.setWon(true);
//
//        GameResult object2 = realm.createObject(GameResult.class);
//        object2.setId(2);
//        object2.setWon(false);
//
//        GameResult object3 = realm.createObject(GameResult.class);
//        object3.setId(3);
//        object3.setWon(false);
//
//        GameResult object4 = realm.createObject(GameResult.class);
//        object4.setId(4);
//        object4.setWon(false);
//
//        results.add(object);
//        results.add(object1);
//        results.add(object2);
//        results.add(object3);
//        results.add(object4);

        return results;
    }

    @SuppressLint("ValidFragment")
    private class ThemeChooser extends DialogFragment {
        public ThemeChooser() {
            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(c);
            final String[] themes = new String[]{STANDARD_THEME, UNICORN_THEME, STARWARS_THEME, RAPTOR_THEME, HOB_THEME, BAY_THEME,};
            alertDialog.setTitle("Choose a theme")
                    .setSingleChoiceItems(themes, -1, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            initializeTheme(themes[i]);
                        }
                    });

            alertDialog.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                }
            });
            alertDialog.setCancelable(false);
            alertDialog.create();
            alertDialog.show();
        }
    }

    private void initializeTheme(String mode) {
        User user = realm.where(User.class).findFirst();
        ArrayList<MediaPlayer> themeBasedMP = getThemeBasedMP(c, mode);
        setSoundConstants(themeBasedMP);
        setBackGroundConstant(mode);
        setTheme();
        assert user != null;
        realm.beginTransaction();
        user.setTheme(mode);
        realm.commitTransaction();
    }

    private void setTheme() {
        ConstraintLayout layout = findViewById(R.id.menuLayout);
        layout.setBackground(getDrawable(BACKGROUND));
    }
}



