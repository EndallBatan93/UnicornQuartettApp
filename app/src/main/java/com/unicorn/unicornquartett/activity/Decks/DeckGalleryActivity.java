package com.unicorn.unicornquartett.activity.Decks;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.unicorn.unicornquartett.R;
import com.unicorn.unicornquartett.Utility.DeckBuilder;
import com.unicorn.unicornquartett.activity.Profile.ProfileActivity;
import com.unicorn.unicornquartett.domain.CardDTO;
import com.unicorn.unicornquartett.domain.CardDTOList;
import com.unicorn.unicornquartett.domain.CardImageList;
import com.unicorn.unicornquartett.domain.Deck;
import com.unicorn.unicornquartett.domain.DeckDTO;
import com.unicorn.unicornquartett.domain.Shema;
import com.unicorn.unicornquartett.domain.ShemaList;
import com.unicorn.unicornquartett.domain.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

import static com.unicorn.unicornquartett.Utility.Constants.BACKGROUND;
import static com.unicorn.unicornquartett.Utility.Constants.HIGH_FACTOR;
import static com.unicorn.unicornquartett.Utility.Constants.LOW_FACTOR;
import static com.unicorn.unicornquartett.Utility.Constants.MEDIUM_FACTOR;
import static com.unicorn.unicornquartett.Utility.Constants.ULTRA_HIGH_FACTOR;

public class DeckGalleryActivity extends AppCompatActivity {

    ListView deckListView;
    final Realm realm = Realm.getDefaultInstance();
    TextView profileName;
    RequestQueue requestQueue;
    RequestQueue requestQueueImage;
    RequestQueue.RequestFinishedListener shemaListener;
    RequestQueue.RequestFinishedListener attributeListener;
    RequestQueue.RequestFinishedListener imageListener;
    RequestQueue.RequestFinishedListener imageFileListener;
    RequestQueue.RequestFinishedListener filesDownloadedListener;
    int idInCardDTOList;
    int endPositionInCardDTOList;

    @Override
    public void onResume() {
        super.onResume();
        handleDeckGalleryInit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    private void handleDeckGalleryInit() {
        setContentView(R.layout.activity_deck_gallery);
        Realm realm = Realm.getDefaultInstance();
        RealmResults<User> allUsers = realm.where(User.class).findAll();
        final User user = allUsers.first();

        assert user != null;
        setTheme();

        profileName = findViewById(R.id.userName);
        ListView deckListView = findViewById(R.id.decksListView);

        setUserName(user);
        final RealmResults<Deck> decks = realm.where(Deck.class).findAll();

        List<String> deckNames = new ArrayList<String>();
        List<Integer> imageList = new ArrayList<>();
        if (decks.size() != 0) {
            for (Deck deck : decks) {
                String deckName = deck.getName();
                deckNames.add(deckName);
                imageList.add(R.drawable.geilesau);
            }
        }

        List<ListViewItem> listOfDeckItems = setImages(decks, deckNames);

        CustomAdapter customAdapter = new CustomAdapter(getBaseContext(), listOfDeckItems);
        deckListView.setAdapter(customAdapter);
        deckListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if (decks.get(position).getCards().isEmpty()) {
                    downloadDeck(decks.get(position));
                } else {

                    Deck deckIdentity = decks.get(position);
                    goToDisplayCardActivity(view, deckIdentity.getName());
                }
            }
        });

        loadImageFromStorage(user.getImageAbsolutePath(), user.getImageIdentifier());
    }

    private void downloadDeck(final Deck deck) {
        requestQueue = Volley.newRequestQueue(this);
        requestQueueImage = Volley.newRequestQueue(this);
        getCardNames(deck.getId());
        shemaListener = new RequestQueue.RequestFinishedListener() {
            @Override
            public void onRequestFinished(Request request) {
                getShemaForDeck(deck);
                getAttributes(deck);
            }

        };
        requestQueue.addRequestFinishedListener(shemaListener);

    }

    private void getAttributes(final Deck deck) {
        requestQueue.removeRequestFinishedListener(shemaListener);

        attributeListener = new RequestQueue.RequestFinishedListener() {
            @Override
            public void onRequestFinished(Request request) {
                if (idInCardDTOList != endPositionInCardDTOList) {
                    getAttributesForCard(deck);
                } else {
                    idInCardDTOList = 0;
                    RealmList<CardDTO> listOfCard = realm.where(CardDTOList.class).equalTo("deckID", deck.getId()).findFirst().getListOfCardDTO();
                    endPositionInCardDTOList = listOfCard.size();
                    loadImagesForCards(deck);
                }
            }
        };
        requestQueue.addRequestFinishedListener(attributeListener);
    }

    private void loadImagesForCards(final Deck deck) {
        requestQueue.removeRequestFinishedListener(attributeListener);

        imageListener = new RequestQueue.RequestFinishedListener() {
            @Override
            public void onRequestFinished(Request request) {
                if (idInCardDTOList != endPositionInCardDTOList) {
                    getCardImages(deck);
                } else {
                    idInCardDTOList = -1;
                    endPositionInCardDTOList = -2;
                    loadImagesFromURL(deck);
                }
            }
        };
        requestQueue.addRequestFinishedListener(imageListener);
        if (idInCardDTOList == 0) {
            getCardImages(deck);
        }

    }

    private void loadImagesFromURL(Deck deck) {
        requestQueue.removeRequestFinishedListener(imageListener);
        idInCardDTOList = 0;
        final RealmList<CardImageList> listOfCardImagesLists = realm.where(DeckDTO.class).equalTo("id", deck.getId()).findFirst().getListOfCardImagesURLs();
        for (CardImageList cardImageList : listOfCardImagesLists) {
            RealmList<String> listOfImagesURLsForOneCard = cardImageList.getListOfImagesURLsForOneCard();
            for (String url : listOfImagesURLsForOneCard) {
                downloadFile(url, cardImageList.getCardID(), cardImageList.getDeckId());
            }
        }
        buildDeckFromDTOS(deck);
    }

    private void buildDeckFromDTOS(Deck deck) {
        filesDownloadedListener = new RequestQueue.RequestFinishedListener() {
            @Override
            public void onRequestFinished(Request request) {
            }
        };

        new DeckBuilder(deck);
    }

    private void getCardImages(Deck deck) {
        final int deckID = deck.getId();
        final CardDTO cardDTO = realm.where(CardDTOList.class).equalTo("deckID", deckID).findFirst().getListOfCardDTO().get(idInCardDTOList);
        final int cardId = cardDTO.getId();
        String url = "http://quartett.af-mba.dbis.info/decks/" + deckID + "/cards/" + cardId + "/images/";
        final RealmList<String> imageListForOneCard = new RealmList<>();
        JsonArrayRequest jsArrReqeust = new JsonArrayRequest

                (Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        JSONArray returnedJson = response;
                        for (int i = 0; i < returnedJson.length(); i++) {
                            try {
                                JSONObject o = returnedJson.getJSONObject(i);
                                String image = o.getString("image");
                                imageListForOneCard.add(image);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        addCardImageList(imageListForOneCard, deckID, cardId);
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        System.out.println("sth went wrong");
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return DeckGalleryActivity.this.getHeaders();
            }
        };

        requestQueue.add(jsArrReqeust);

    }

    private void addCardImageList(RealmList<String> imageListForOneCard, int deckID, int cardId) {
        DeckDTO deckDTO = realm.where(DeckDTO.class).equalTo("id", deckID).findFirst();
        realm.beginTransaction();
        CardImageList tmpCardImageList = realm.createObject(CardImageList.class);
        tmpCardImageList.setDeckId(deckID);
        tmpCardImageList.setCardID(cardId);
        tmpCardImageList.setListOfImagesURLsForOneCard(imageListForOneCard);
        deckDTO.addCardImageList(tmpCardImageList);
        realm.commitTransaction();
        idInCardDTOList++;
    }

    private void downloadFile(String imageUrl, final int cardId, final int deckID) {
        ImageRequest imageRequest = new ImageRequest(
                imageUrl, // Image URL
                new Response.Listener<Bitmap>() { // Bitmap listener
                    @Override
                    public void onResponse(Bitmap response) {
                        Uri uri = saveImageToInternalStorage(response, cardId, deckID);
                        realm.beginTransaction();
                        CardDTO cardDTO = realm.where(CardDTOList.class).findFirst().getListOfCardDTO().get(idInCardDTOList);
//                cardDTO.setImages(uri);
                        realm.commitTransaction();
                        idInCardDTOList++;
                    }
                },
                0, // Image width
                0, // Image height
                ImageView.ScaleType.CENTER_CROP, // Image scale type
                Bitmap.Config.RGB_565, //Image decode configuration
                new Response.ErrorListener() { // Error listener
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Do something with error response
                        error.printStackTrace();
                    }
                }
        );
        requestQueueImage.add(imageRequest);
    }


    protected Uri saveImageToInternalStorage(Bitmap bitmap, int cardId, int deckID) {
        // Initialize ContextWrapper
        ContextWrapper wrapper = new ContextWrapper(getApplicationContext());

        // Initializing a new file
        // The bellow line return a directory in internal storage
        File file = wrapper.getDir("Images", MODE_PRIVATE);

        // Create a file to save the image
        file = new File(file, deckID + "-" + cardId + ".jpg");

        try {
            // Initialize a new OutputStream
            OutputStream stream = null;

            // If the output file exists, it can be replaced or appended to it
            stream = new FileOutputStream(file);

            // Compress the bitmap
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);

            // Flushes the stream
            stream.flush();

            // Closes the stream
            stream.close();

        } catch (IOException e) // Catch the exception
        {
            e.printStackTrace();
        }

        // Parse the gallery image url to uri
        Uri savedImageURI = Uri.parse(file.getAbsolutePath());

        // Return the saved image Uri
        requestQueueImage.removeRequestFinishedListener(imageFileListener);
        return savedImageURI;
    }

    private void getAttributesForCard(Deck deck) {
        final int deckID = deck.getId();
        CardDTO cardDTO = realm.where(CardDTOList.class).equalTo("deckID", deckID).findFirst().getListOfCardDTO().get(idInCardDTOList);
        final int cardID = cardDTO.getId();
        String url = "http://quartett.af-mba.dbis.info/decks/" + deckID + "/cards/" + cardID + "/attributes/";
        final RealmList<Double> valueList = new RealmList<>();
        JsonArrayRequest jsArrReqeust = new JsonArrayRequest

                (Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        JSONArray returnedJson = response;
                        for (int i = 0; i < returnedJson.length(); i++) {
                            try {
                                JSONObject o = returnedJson.getJSONObject(i);
                                double value = o.getDouble("value");
                                valueList.add(value);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        createValueListForCard(valueList, deckID);
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        System.out.println("sth went wrong");
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return DeckGalleryActivity.this.getHeaders();
            }
        };

        requestQueue.add(jsArrReqeust);


    }

    @NonNull
    private Map<String, String> getHeaders() {
        Map<String, String> headers = new HashMap<>();
        String credentials = "student:afmba";
        String auth = "Basic " + "c3R1ZGVudDphZm1iYQ==";
        headers.put("Content-Type", "application/json");
        headers.put("Content-Type", "multipart/form/data");
        headers.put("Authorization", auth);
        return headers;
    }

    private void createValueListForCard(RealmList<Double> valueList, int deckID) {
        CardDTO cardDTO = realm.where(CardDTOList.class).equalTo("deckID", deckID).findFirst().getListOfCardDTO().get(idInCardDTOList);
        realm.beginTransaction();
        cardDTO.setValueList(valueList);
        realm.commitTransaction();
        idInCardDTOList += 1;
    }

    private void getShemaForDeck(Deck deck) {
        final int deckId = deck.getId();
        int cardID = realm.where(CardDTOList.class).equalTo("deckID", deckId).findFirst().getListOfCardDTO().first().getId();
        String url = "http://quartett.af-mba.dbis.info/decks/" + deckId + "/cards/" + cardID + "/attributes/";
        final RealmList<Shema> listOfShemas = new RealmList<>();
        JsonArrayRequest jsArrReqeust = new JsonArrayRequest

                (Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        JSONArray returnedJson = response;
                        for (int i = 0; i < returnedJson.length(); i++) {
                            try {
                                JSONObject o = returnedJson.getJSONObject(i);
                                String name = o.getString("name");
                                String unit = o.getString("unit");
                                String what_wins = o.getString("what_wins");
                                Boolean hW;
                                if (what_wins.equals("higher_wins")) {
                                    hW = true;
                                } else {
                                    hW = false;
                                }
                                Shema shemaForCard = createShemaForCard(deckId, name, unit, hW);
                                listOfShemas.add(shemaForCard);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        saveListOfShema(deckId, listOfShemas);
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        System.out.println("sth went wrong");
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return DeckGalleryActivity.this.getHeaders();
            }
        };

        requestQueue.add(jsArrReqeust);

    }

    private void saveListOfShema(int deckId, RealmList<Shema> listOfShemas) {
        realm.beginTransaction();
        ShemaList shemaList = realm.createObject(ShemaList.class);
        shemaList.setDeckID(deckId);
        shemaList.setListOfShemas(listOfShemas);
        realm.commitTransaction();
    }

    private Shema createShemaForCard(int deckId, String name, String unit, Boolean hW) {
        realm.beginTransaction();
        Shema shema = realm.createObject(Shema.class);
        shema.setHigherWins(hW);
        shema.setProperty(name);
        shema.setUnit(unit);
        shema.setId(deckId);
        realm.commitTransaction();
        return shema;
    }


    private void getCardNames(int id) {
        String url = "http://quartett.af-mba.dbis.info/decks/" + id + "/cards/";
        final RealmList<CardDTO> cardDTOList = new RealmList<>();
        final int deckID = id;
        JsonArrayRequest jsArrReqeust = new JsonArrayRequest

                (Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        JSONArray returnedJson = response;
                        for (int i = 0; i < returnedJson.length(); i++) {
                            try {
                                Gson gson = new Gson();
                                CardDTO card = gson.fromJson(returnedJson.get(i).toString(), CardDTO.class);
                                CardDTO realmCard = createCardDTO(deckID, card);
                                cardDTOList.add(realmCard);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        idInCardDTOList = 0;
                        endPositionInCardDTOList = cardDTOList.size();
                        saveListOfCardDTO(deckID, cardDTOList);
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        System.out.println("sth went wrong");
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return DeckGalleryActivity.this.getHeaders();
            }
        };

        requestQueue.add(jsArrReqeust);
    }

    @NonNull
    private CardDTO createCardDTO(int deckID, CardDTO card) {
        realm.beginTransaction();
        CardDTO realmCard = realm.createObject(CardDTO.class);
        realmCard.setDeckID(deckID);
        realmCard.setId(card.getId());
        realmCard.setName(card.getName());
        realm.commitTransaction();
        return realmCard;
    }

    private void saveListOfCardDTO(int deckDTOID, RealmList<CardDTO> listOfCardDTO) {
        realm.beginTransaction();
        CardDTOList cardDTOList = realm.createObject(CardDTOList.class);
        cardDTOList.setDeckID(deckDTOID);
        cardDTOList.setListOfCardDTO(listOfCardDTO);
        realm.commitTransaction();
    }

    @NonNull
    private List<ListViewItem> setImages(RealmResults<Deck> decks, List<String> deckNames) {
        List<ListViewItem> listOfDeckItems = new ArrayList<>();
        for (int i = 0; i < decks.size(); i++) {

            HashMap<String, String> tmpTitleFromNameMap = new HashMap<>();
            String deckName = deckNames.get(i);
            tmpTitleFromNameMap.put("title", deckName);
            if (!decks.get(i).getCards().isEmpty()) {


                String imageIdentifier = decks.get(i).getCards().first().getImage().getImageIdentifiers().first();
                String imagePath = deckName.toLowerCase() + "/" + imageIdentifier;
                Drawable tempDrawable;
                try {
                    InputStream tempInputStream = getAssets().open(imagePath);
                    tempDrawable = Drawable.createFromStream(tempInputStream, null);
                    ListViewItem tmpListViewItem = new ListViewItem(tmpTitleFromNameMap, tempDrawable);
                    listOfDeckItems.add(tmpListViewItem);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                ListViewItem tmpListViewItem = new ListViewItem(tmpTitleFromNameMap, getDrawable(R.drawable.deckplatzhalter));
                listOfDeckItems.add(tmpListViewItem);
            }
        }
        return listOfDeckItems;
    }

    private void setUserName(User user) {
        assert user != null;
        RealmResults<User> allUsers = realm.where(User.class).findAll();
        if (!allUsers.isEmpty()) {
            user = allUsers.first();
            loadImageFromStorage(user.getImageAbsolutePath(), user.getImageIdentifier());
            profileName.setText(user.getName());
        }
    }

    public void goToProfileActivity(View view) {
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }

    private void loadImageFromStorage(String absolutePath, String imageIdentifier) {
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

    public class ListViewItem {
        HashMap<String, String> titleFromName;
        Drawable drawableFromAsset;

        public ListViewItem(HashMap<String, String> titleFromName, Drawable drawableFromAsset) {
            this.titleFromName = titleFromName;
            this.drawableFromAsset = drawableFromAsset;
        }

        public HashMap<String, String> getTitleFromName() {
            return titleFromName;
        }

        public void setTitleFromName(HashMap<String, String> titleFromName) {
            this.titleFromName = titleFromName;
        }

        public Drawable getDrawableFromAsset() {
            return drawableFromAsset;
        }

        public void setDrawableFromAsset(Drawable drawableFromAsset) {
            this.drawableFromAsset = drawableFromAsset;
        }
    }

    public class CustomAdapter extends ArrayAdapter<ListViewItem> {
        public CustomAdapter(Context context, List<ListViewItem> listViewItemList) {
            super(context, 0, listViewItemList);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (view == null)
                view = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.listview_image_text, null);
            final ListViewItem item = getItem(position);
            TextView title = view.findViewById(R.id.vwiatImageTitle);
            ImageView image = view.findViewById(R.id.vwiatImage);

            String title1 = item.getTitleFromName().get("title");
            title.setText(title1);
            image.setImageDrawable(item.getDrawableFromAsset());

            return view;
        }
    }

    public void goToDisplayCardActivity(View view, String deckName) {
        Intent intent = new Intent(this, DisplayCardActivity.class);
        intent.putExtra("DeckName", deckName);
        startActivity(intent);
    }

    private void setTheme() {
        ConstraintLayout layout = findViewById(R.id.deckGalleryLayout);
        layout.setBackground(getDrawable(BACKGROUND));
    }
}
