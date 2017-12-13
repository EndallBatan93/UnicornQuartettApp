package com.unicorn.unicornquartett.activity.Menu;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.unicorn.unicornquartett.R;
import com.unicorn.unicornquartett.Utility.Util;
import com.unicorn.unicornquartett.activity.Decks.DeckGalleryActivity;
import com.unicorn.unicornquartett.activity.Friends.FriendActivity;
import com.unicorn.unicornquartett.activity.PlayGame.PlayGameActivity;
import com.unicorn.unicornquartett.activity.Profile.ProfileActivity;
import com.unicorn.unicornquartett.activity.Ranglist.RangListActivity;
import com.unicorn.unicornquartett.domain.User;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;

import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

public class MenuActivity extends AppCompatActivity {
    private static final int REQUEST_FROM_GALLERY = 2;
    static final int REQUEST_TAKE_PHOTO = 1;

    final Context c = this;
    Realm realm = Realm.getDefaultInstance();
    TextView profileName;

    String mCurrentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        // Initializing Varables
        Button playButton = findViewById(R.id.playbutton);
        Button ranglistButton = findViewById(R.id.ranglisbutton);
        Button deckButotn = findViewById(R.id.deckbutton);
        Button friendButton = findViewById(R.id.friendButton);
        profileName = findViewById(R.id.userName);

        RealmResults<User> allUsers = realm.where(User.class).findAll();

        if (allUsers.isEmpty()) {
            new CreateUserDialogFragment();

        } else {
            User user = allUsers.first();
            loadImageFromStorage(user.getImageAbsolutePath(), user.getImageIdentifier());

        }
    }


    // give parameters absolutePath and imageIdentifier and on call set user.absolutePath and user.imageIdentifier
    private void loadImageFromStorage(String absolutePath, String imageIdentifier) {
        Util.verifyStoragePermissions(MenuActivity.this);
        try {
            File f = new File(absolutePath, imageIdentifier);
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            CircleImageView profileButton = findViewById(R.id.profileButton);
            profileButton.setImageBitmap(b);
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
                String identifier = absolutePath.substring(absolutePath.lastIndexOf('/')+1);
                String absPath = absolutePath.replace(identifier,"");
                first.setImageAbsolutePath(absPath);
                first.setImageIdentifier(identifier);
                loadImageFromStorage(absPath,identifier);
            }

        }
        realm.commitTransaction();
        realm.close();
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
        profileName.setText(username);
    }

}



