package com.unicorn.unicornquartett.activity.Decks;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import android.widget.Toast;

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
import com.unicorn.unicornquartett.activity.Menu.MenuActivity;
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
import java.io.FileOutputStream;
import java.io.IOException;
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
import static com.unicorn.unicornquartett.Utility.Constants.IMAGE_PATH;
import static com.unicorn.unicornquartett.Utility.Util.getCardImageFromStorage;
import static com.unicorn.unicornquartett.Utility.Util.getHeadersForHTTP;
import static com.unicorn.unicornquartett.Utility.Util.getImageFromStorage;

public class DeckGalleryActivity extends AppCompatActivity {

    ListView deckListView;
    final Realm realm = Realm.getDefaultInstance();
    final Context context = this;
    TextView profileName;
    RequestQueue requestQueue;
    RequestQueue requestQueueImage;
    RequestQueue.RequestFinishedListener shemaListener;
    RequestQueue.RequestFinishedListener attributeListener;
    RequestQueue.RequestFinishedListener imageListener;
    RequestQueue.RequestFinishedListener imageFileListener;
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

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MenuActivity.class);
        startActivity(intent);
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

        List<ListViewItem> listOfDeckItems = setImagesForDecks(decks, deckNames);

        CustomAdapter customAdapter = new CustomAdapter(getBaseContext(), listOfDeckItems);
        deckListView.setAdapter(customAdapter);
        deckListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if (decks.get(position).getCards().isEmpty()) {
                    checkIfWiFiIsOn(decks, position);
                } else {
                    Deck deckIdentity = decks.get(position);
                    goToDisplayCardActivity(view, deckIdentity.getName());
                }
            }
        });

        loadImageFromStorage(user.getImageAbsolutePath(), user.getImageIdentifier());
    }

    private void checkIfWiFiIsOn(RealmResults<Deck> decks, int position) {
        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        boolean isConnectedWithWIFI = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting() &&
                activeNetwork.getType() == ConnectivityManager.TYPE_WIFI;

        if (isConnectedWithWIFI){
            downloadDeck(decks.get(position));
        } else {
            Toast wifiToast = Toast.makeText(context, "Please enable your WIFI. \nDownloading with mobile data is not supported.", Toast.LENGTH_LONG);
            wifiToast.show();
        }
    }

    private void downloadDeck(final Deck deck) {

        Toast progressToast = Toast.makeText(context, "Downloading. Please wait", Toast.LENGTH_SHORT);
        progressToast.show();
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

        idInCardDTOList = 0;
        RealmList<CardDTO> listOfCard = realm.where(CardDTOList.class).equalTo("deckID", deck.getId()).findFirst().getListOfCardDTO();
        endPositionInCardDTOList = listOfCard.size();

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
        Toast progressToast = Toast.makeText(context, "Downloading. Please wait", Toast.LENGTH_SHORT);
        progressToast.show();
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
        Toast progressToast = Toast.makeText(context, "Downloading. Please wait", Toast.LENGTH_SHORT);
        progressToast.show();
        idInCardDTOList = 0;
        final RealmList<CardImageList> listOfCardImagesLists = realm.where(DeckDTO.class).equalTo("id", deck.getId()).findFirst().getListOfCardImagesURLs();
        for (CardImageList cardImageList : listOfCardImagesLists) {
            RealmList<String> listOfImagesURLsForOneCard = cardImageList.getListOfImagesURLsForOneCard();
            for (int nthImage = 0; nthImage< listOfImagesURLsForOneCard.size(); nthImage++) {
                String url = listOfImagesURLsForOneCard.get(nthImage);
                downloadFile(url, cardImageList.getCardID(), cardImageList.getDeckId(), nthImage);
            }
        }
        buildDeckFromDTOs(deck);
    }

    private void buildDeckFromDTOs(Deck deck) {
        Toast progressToast = Toast.makeText(context, "Downloading. Please wait", Toast.LENGTH_SHORT);
        progressToast.show();

        new DeckBuilder(context, deck);
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
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return getHeadersForHTTP();
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

    private void downloadFile(String imageUrl, final int cardId, final int deckID, final int nthImage) {
        ImageRequest imageRequest = new ImageRequest(
                imageUrl, // Image URL
                new Response.Listener<Bitmap>() { // Bitmap listener
                    @Override
                    public void onResponse(Bitmap response) {
                            Uri uri = saveImageToInternalStorage(response, cardId, deckID, nthImage);

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

    protected Uri saveImageToInternalStorage(Bitmap bitmap, int cardId, int deckID, int nthImage) {

        ContextWrapper wrapper = new ContextWrapper(getApplicationContext());

        File file = wrapper.getDir("Images", MODE_PRIVATE);
        if(nthImage == 0){
            file = new File(file, deckID + "-" + cardId + ".jpg");
            idInCardDTOList++;
        } else {
            file = new File(file, deckID + "-" + cardId + "-" +nthImage+ ".jpg");
        }

        try {
            OutputStream stream = null;

            stream = new FileOutputStream(file);

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);

            stream.flush();

            stream.close();

        } catch (IOException e) // Catch the exception
        {
            e.printStackTrace();
        }

        Uri savedImageURI = Uri.parse(file.getAbsolutePath());

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
                return getHeadersForHTTP();
            }
        };

        requestQueue.add(jsArrReqeust);


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
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return getHeadersForHTTP();
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
                return getHeadersForHTTP();
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

    private void saveListOfCardDTO(int deckDTOId, RealmList<CardDTO> listOfCardDTO) {
        realm.beginTransaction();
        CardDTOList cardDTOList = realm.createObject(CardDTOList.class);
        cardDTOList.setDeckID(deckDTOId);
        cardDTOList.setListOfCardDTO(listOfCardDTO);
        realm.commitTransaction();
    }

    @NonNull
    private List<ListViewItem> setImagesForDecks(RealmResults<Deck> decks, List<String> deckNames) {
        List<ListViewItem> listOfDeckItems = new ArrayList<>();
        for (int i = 0; i < decks.size(); i++) {

            HashMap<String, String> tmpTitleFromNameMap = new HashMap<>();
            String deckName = deckNames.get(i);
            tmpTitleFromNameMap.put("title", deckName);
            if (!decks.get(i).getCards().isEmpty()) {
                Deck deck = decks.get(i);
                int deckID = deck.getId();
                int cardID = deck.getCards().first().getId();
                Bitmap cardImageFromStorage = getCardImageFromStorage(IMAGE_PATH, deckID, cardID);
                ListViewItem tmpListViewItem = new ListViewItem(tmpTitleFromNameMap, cardImageFromStorage);
                listOfDeckItems.add(tmpListViewItem);

            } else {
                Bitmap standardPicture = BitmapFactory.decodeResource(context.getResources(),
                        R.drawable.deckplatzhalter);
                ListViewItem tmpListViewItem = new ListViewItem(tmpTitleFromNameMap, standardPicture);
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
        CircleImageView profileButton = findViewById(R.id.profileButton);
        profileButton.setImageBitmap(getImageFromStorage(absolutePath, imageIdentifier));
    }

    public class ListViewItem {
        HashMap<String, String> titleFromName;
        Bitmap cardBitmap;

        public ListViewItem(HashMap<String, String> titleFromName, Bitmap cardBitmap) {
            this.titleFromName = titleFromName;
            this.cardBitmap = cardBitmap;
        }

        public HashMap<String, String> getTitleFromName() {
            return titleFromName;
        }

        public void setTitleFromName(HashMap<String, String> titleFromName) {
            this.titleFromName = titleFromName;
        }

        public Bitmap getCardBitmap() {
            return cardBitmap;
        }

        public void setCardBitmap(Bitmap cardBitmap) {
            this.cardBitmap = cardBitmap;
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
            image.setImageBitmap(item.getCardBitmap());

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
