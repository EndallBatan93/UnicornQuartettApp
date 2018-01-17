package com.unicorn.unicornquartett.activity.Profile;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.unicorn.unicornquartett.R;
import com.unicorn.unicornquartett.domain.GameResult;
import com.unicorn.unicornquartett.domain.User;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

import static com.unicorn.unicornquartett.Utility.Constants.BACKGROUND;
import static com.unicorn.unicornquartett.Utility.Constants.BAY_THEME;
import static com.unicorn.unicornquartett.Utility.Constants.Button_SOUND;
import static com.unicorn.unicornquartett.Utility.Constants.DIFFICULTY_1;
import static com.unicorn.unicornquartett.Utility.Constants.DIFFICULTY_2;
import static com.unicorn.unicornquartett.Utility.Constants.DIFFICULTY_3;
import static com.unicorn.unicornquartett.Utility.Constants.Fun_SOUND;
import static com.unicorn.unicornquartett.Utility.Constants.HOB_THEME;
import static com.unicorn.unicornquartett.Utility.Constants.INTRO_SOUND;
import static com.unicorn.unicornquartett.Utility.Constants.RAPTOR_THEME;
import static com.unicorn.unicornquartett.Utility.Constants.STANDARD_THEME;
import static com.unicorn.unicornquartett.Utility.Constants.STARWARS_THEME;
import static com.unicorn.unicornquartett.Utility.Constants.UNICORN_THEME;
import static com.unicorn.unicornquartett.Utility.Util.getImageFromStorage;
import static com.unicorn.unicornquartett.Utility.Util.getThemeBasedMP;
import static com.unicorn.unicornquartett.Utility.Util.setBackGroundConstant;
import static com.unicorn.unicornquartett.Utility.Util.setSoundConstants;

public class ProfileActivity extends AppCompatActivity {
    final Realm realm = Realm.getDefaultInstance();
    String mCurrentPhotoPath;
    private static final int REQUEST_FROM_GALLERY = 2;
    static final int REQUEST_TAKE_PHOTO = 1;
    Button googlePlayButton;

    String[] diffArray = new String[]{DIFFICULTY_1, DIFFICULTY_2, DIFFICULTY_3};

    final Context c = this;

    @Override
    public void onResume() {
        super.onResume();
        RealmResults<User> allUsers = realm.where(User.class).findAll();
        final EditText usernameTextView = findViewById(R.id.username);
        if (!allUsers.isEmpty()) {
            User user = allUsers.first();
            loadImageFromStorage(user.getImageAbsolutePath(), user.getImageIdentifier());
            usernameTextView.setText(user.getName());
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        RealmResults<User> all = realm.where(User.class).findAll();
        final User user = all.first();

        assert user != null;
        setTheme();
        loadImageFromStorage(user.getImageAbsolutePath(), user.getImageIdentifier());
        final EditText usernameTextView = findViewById(R.id.username);
        TextView registeredTextView = findViewById(R.id.registered);
        TextView dissView = findViewById(R.id.dissView);
        final Button difficultyButton = findViewById(R.id.difficultyButton);
        CircleImageView circleImageView = findViewById(R.id.profileButton);
        TextView statistics = findViewById(R.id.stats);
        Button theme = findViewById(R.id.themeChooser);
        Date date = user.getDate();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd:MM:yyyy");
        String dateString = dateFormat.format(date);

        registeredTextView.setText("Registered since: " + dateString);
        usernameTextView.setText(user.getName());

        RealmList<GameResult> stats = user.getStats();
        int won = 0;
        int lost = 0;
        for (GameResult stat : stats) {
            if(stat.getWon()) {
                won = won+1;
            } else {
                lost = lost+1;
            }
        }

        statistics.setText("won: "+won +" || lost: " +lost);
        if(won > lost) {
            dissView.setText("Such Wow. Get youself an icecream. Just jizzed in my pants.");
        } else if(won == lost) {
            dissView.setText("Seriously. Much too learn you have dimwit.");

        } else {
            dissView.setText("You should just throw your phone away and cry.");
        }



        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fun_SOUND.start();
                takePictureDialog();
            }
        });
        usernameTextView.setOnKeyListener(new View.OnKeyListener() {

            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                Button_SOUND.start();
                realm.beginTransaction();
                user.setName(usernameTextView.getText().toString());
                realm.commitTransaction();
                return false;
            }
        });
        difficultyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                INTRO_SOUND.start();
                new DifficultyChooser();
            }
        });

        theme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                INTRO_SOUND.start();
                new ThemeChooser();
            }
        });

    }

    public void connectToGooglePlay(View view) {
        //tbd
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

    private void takePictureDialog() {

        LayoutInflater layoutInflater = LayoutInflater.from(c);
        View createPhotoDialogView = layoutInflater.inflate(R.layout.dialog_create_photo, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setView(createPhotoDialogView);

        builder.setPositiveButton("Take picture", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        realm.beginTransaction();
                        dispatchTakePictureIntent();
                    }
                })
                .setNeutralButton("Choose picture", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        realm.beginTransaction();
                        choosePictureFromGallery();
                    }
                });
        AlertDialog createUserDialog = builder.create();
        createUserDialog.show();
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
//        realm.close();
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

    private void loadImageFromStorage(String absolutePath, String imageIdentifier) {
        CircleImageView profileButton = findViewById(R.id.profileButton);
        profileButton.setImageBitmap(getImageFromStorage(absolutePath, imageIdentifier));
    }


    @SuppressLint("ValidFragment")
    private class DifficultyChooser extends DialogFragment {
        public DifficultyChooser() {
            realm.beginTransaction();
            final User user = realm.where(User.class).findFirst();
            LayoutInflater layoutInflater = LayoutInflater.from(c);
            View createUserDialogView = layoutInflater.inflate(R.layout.dialog_difficulty, null);
            AlertDialog.Builder builder = new AlertDialog.Builder(c);
            builder.setView(createUserDialogView);

            builder.setCancelable(false)
                    .setSingleChoiceItems(diffArray, -1, new DialogInterface
                            .OnClickListener() {
                        public void onClick(DialogInterface dialog, int item) {
                            String diff = diffArray[item];
                            dialog.dismiss();// dismiss the alertbox after chose option
                            user.setDifficulty(diff);
                            realm.commitTransaction();
                        }
                    });
            AlertDialog createUserDialog = builder.create();
            createUserDialog.show();
        }
    }


    @SuppressLint("ValidFragment")
    private class ThemeChooser extends DialogFragment {
        public ThemeChooser() {
            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(c);
            final String[] themes = new String[]{STANDARD_THEME, UNICORN_THEME, STARWARS_THEME, RAPTOR_THEME, HOB_THEME, BAY_THEME, };
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
        ConstraintLayout layout = findViewById(R.id.profileLayout);
        layout.setBackground(getDrawable(BACKGROUND));
    }
}
