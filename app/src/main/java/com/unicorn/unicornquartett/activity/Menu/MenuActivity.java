package com.unicorn.unicornquartett.activity.Menu;

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
import android.os.Handler;
import android.provider.MediaStore;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.unicorn.unicornquartett.R;
import com.unicorn.unicornquartett.activity.Decks.DeckGalleryActivity;
import com.unicorn.unicornquartett.activity.PlayGame.ChooseGameActivity;
import com.unicorn.unicornquartett.activity.Profile.ProfileActivity;
import com.unicorn.unicornquartett.domain.Deck;
import com.unicorn.unicornquartett.domain.DeckDTO;
import com.unicorn.unicornquartett.domain.Game;
import com.unicorn.unicornquartett.domain.User;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.Realm;
import io.realm.RealmResults;

import static com.unicorn.unicornquartett.Utility.Constants.BACKGROUND;
import static com.unicorn.unicornquartett.Utility.Constants.BAY_THEME;
import static com.unicorn.unicornquartett.Utility.Constants.Button_SOUND;
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
import static com.unicorn.unicornquartett.Utility.Util.verifyStoragePermissions;

public class MenuActivity extends AppCompatActivity {
    private static final int REQUEST_FROM_GALLERY = 2;
    static final int REQUEST_TAKE_PHOTO = 1;
    private boolean doubleBackToExitPressedOnce = false;

    String mCurrentPhotoPath;
    final Context c = this;
    Realm realm = Realm.getDefaultInstance();
    TextView profileName;
    String theme;
    List<DeckDTO> downloadableDecks = new ArrayList<>();

    @Override
    public void onResume() {
        super.onResume();
        resumeLogicMenu();
    }

    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.activity_menu);

        // Initializing Varables
        Button playButton = findViewById(R.id.playbutton);
        Button ranglistButton = findViewById(R.id.ranglisbutton);
        Button deckButotn = findViewById(R.id.deckbutton);
        Button friendButton = findViewById(R.id.friendButton);
        profileName = findViewById(R.id.userName);
        Game unicorn = realm.where(Game.class).equalTo("id", 2).findFirst();
        Game standard = realm.where(Game.class).equalTo("id", 1).findFirst();
        RealmResults<User> allUsers = realm.where(User.class).findAll();

        getDownloadableDecks();

        checkForRunningGames(playButton, unicorn, standard);

        checkForOrLoadUser(allUsers);
    }

    private void checkForOrLoadUser(RealmResults<User> allUsers) {
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

    private void checkForRunningGames(Button playButton, Game unicorn, Game standard) {
        if (unicorn != null && standard != null) {
            playButton.setText(R.string.PlayGame2);
        } else if (unicorn != null) {
            playButton.setText(R.string.PlayGame1);
        } else if (standard != null) {
            playButton.setText(R.string.PlayGame1);

        }
    }

    private void getDownloadableDecks() {
        final RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url = "http://quartett.af-mba.dbis.info/decks/";
        JsonArrayRequest jsArrReqeust = new JsonArrayRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        JSONArray returnedJson = response;
                        for (int i = 0; i < returnedJson.length(); i++) {
                            Gson gson = new Gson();
                            try {
                                DeckDTO deckDTO = gson.fromJson(returnedJson.get(i).toString(), DeckDTO.class);
                                downloadableDecks.add(deckDTO);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        createNewDecksIfAvailable(downloadableDecks);

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                String credentials = "student:afmba";
                String auth = "Basic " + "c3R1ZGVudDphZm1iYQ==";
                headers.put("Content-Type", "application/json");
                headers.put("Content-Type", "multipart/form/data");
                headers.put("Authorization", auth);
                return headers;
            }
        };

        requestQueue.add(jsArrReqeust);
    }

    private void createNewDecksIfAvailable(List<DeckDTO> downloadableDecks) {
        for (DeckDTO downloadableDeck : downloadableDecks) {
            Deck tmpDeck = realm.where(Deck.class).equalTo("id", downloadableDeck.getId()).findFirst();
            if (tmpDeck != null) {
                //checkIfDeckIsUpToDate()
            } else {
                realm.beginTransaction();
                DeckDTO deckDTO = realm.createObject(DeckDTO.class);
                deckDTO.setId(downloadableDeck.getId());
                deckDTO.setName(downloadableDeck.getName());
                Deck emptyDeck = realm.createObject(Deck.class);
                emptyDeck.setId(downloadableDeck.getId());
                emptyDeck.setName(downloadableDeck.getName());
                realm.commitTransaction();
            }
        }
    }

    // give parameters absolutePath and imageIdentifier and on call set user.absolutePath and user.imageIdentifier
    private void loadImageFromStorage(String absolutePath, String imageIdentifier) {
        verifyStoragePermissions(MenuActivity.this);
        CircleImageView profileButton = findViewById(R.id.profileButton);
        profileButton.setImageBitmap(getImageFromStorage(absolutePath, imageIdentifier));
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
        final AlertDialog.Builder rangListDialog = new AlertDialog.Builder(this);
        rangListDialog.setTitle("Highscore\n");
        rangListDialog.setMessage("This content will be available for only 29.99$ soon.\n");
        rangListDialog.create();
        rangListDialog.show();

    }

    public void goToFriendActivity(View view) {
        Button_SOUND.start();
        final AlertDialog.Builder friendDialog = new AlertDialog.Builder(this);
        friendDialog.setTitle("Friend List\n");
        friendDialog.setMessage("This content will be available for only 39.99$ soon.\n");
        friendDialog.create();
        friendDialog.show();

    }

    public void goToDeckGalleryActivity(View view) {
        Button_SOUND.start();
        Intent intent = new Intent(this, DeckGalleryActivity.class);
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
                    .setPositiveButton("Create User", new DialogInterface.OnClickListener() {
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

            verifyStoragePermissions(MenuActivity.this);

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
        user.setId(1);
        user.setName(username);
        user.setDifficulty("Fluffy");
        user.setFriends(null);
        user.setRunningOffline(false);
        user.setRunningOnline(false);
        user.setImageIdentifier(user.getName() + user.getId() + ".jpg");
        user.setDate(new Date());
        profileName.setText(username);
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

    private void resumeLogicMenu() {
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

}



