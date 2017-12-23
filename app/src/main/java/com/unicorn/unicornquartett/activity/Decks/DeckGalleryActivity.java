package com.unicorn.unicornquartett.activity.Decks;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.unicorn.unicornquartett.R;
import com.unicorn.unicornquartett.activity.Profile.ProfileActivity;
import com.unicorn.unicornquartett.domain.Deck;
import com.unicorn.unicornquartett.domain.User;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.Realm;
import io.realm.RealmResults;

public class DeckGalleryActivity extends AppCompatActivity {

    ListView deckListView;
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
        setContentView(R.layout.activity_deck_gallery);
        Realm realm = Realm.getDefaultInstance();
        RealmResults<User> allUsers = realm.where(User.class).findAll();
        final User user = allUsers.first();

        profileName = findViewById(R.id.userName);
        ListView deckListView = findViewById(R.id.decksListView);

        assert user != null;
        profileName.setText(user.getName());
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

        List<ListViewItem> listOfDeckItems = new ArrayList<>();
        for (int i = 0; i <decks.size(); i++){

            HashMap<String, String> tmpTitleFromNameMap = new HashMap<>();
            String deckName = deckNames.get(i);
            tmpTitleFromNameMap.put("title", deckName);
            String imageIdentifier = decks.get(i).getCards().first().getImage().getImageIdentifiers().first();
            String imagePath = deckName.toLowerCase()+"/"+imageIdentifier;
            Drawable tempDrawable;
            try {
                InputStream tempInputStream = getAssets().open(imagePath);
                tempDrawable = Drawable.createFromStream(tempInputStream, null);
                ListViewItem tmpListViewItem = new ListViewItem(tmpTitleFromNameMap,tempDrawable);
                listOfDeckItems.add(tmpListViewItem);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        CustomAdapter customAdapter = new CustomAdapter(getBaseContext(), listOfDeckItems);
        deckListView.setAdapter(customAdapter);
        deckListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Deck deckIdentity = decks.get(position);
                goToDisplayCardActivity(view, deckIdentity.getName());
            }
        });

        loadImageFromStorage(user.getImageAbsolutePath(), user.getImageIdentifier());

    }
    public void goToProfileActivity(View view) {
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }
    private void loadImageFromStorage(String absolutePath, String imageIdentifier) {
        try {
            File f = new File(absolutePath, imageIdentifier);
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            CircleImageView profileButton = findViewById(R.id.profileButton);
            profileButton.setImageBitmap(b);
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
            if(view==null)
                view =((LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.listview_image_text,null);
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
}
