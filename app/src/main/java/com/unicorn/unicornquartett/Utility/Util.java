package com.unicorn.unicornquartett.Utility;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;

import com.unicorn.unicornquartett.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.unicorn.unicornquartett.Utility.Constants.BACKGROUND;
import static com.unicorn.unicornquartett.Utility.Constants.BAY_THEME;
import static com.unicorn.unicornquartett.Utility.Constants.Button_SOUND;
import static com.unicorn.unicornquartett.Utility.Constants.Fun_SOUND;
import static com.unicorn.unicornquartett.Utility.Constants.HIGH_FACTOR;
import static com.unicorn.unicornquartett.Utility.Constants.HOB_THEME;
import static com.unicorn.unicornquartett.Utility.Constants.INTRO_SOUND;
import static com.unicorn.unicornquartett.Utility.Constants.LOW_FACTOR;
import static com.unicorn.unicornquartett.Utility.Constants.MEDIUM_FACTOR;
import static com.unicorn.unicornquartett.Utility.Constants.RAPTOR_THEME;
import static com.unicorn.unicornquartett.Utility.Constants.STANDARD_THEME;
import static com.unicorn.unicornquartett.Utility.Constants.STARWARS_THEME;
import static com.unicorn.unicornquartett.Utility.Constants.ULTRA_HIGH_FACTOR;
import static com.unicorn.unicornquartett.Utility.Constants.UNICORN_THEME;

public class Util {

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static final String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public Util(Context context) {
    }

    // 1. PERMISSIONS

    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    // 2. AUDIO

    public static ArrayList<MediaPlayer> getThemeBasedMP(Context context, String theme) {
        ArrayList<MediaPlayer> unicornMPs = new ArrayList<>();
        ArrayList<MediaPlayer> mBayMPs = new ArrayList<>();
        ArrayList<MediaPlayer> starWarsMPs = new ArrayList<>();
        ArrayList<MediaPlayer> hoBMPs = new ArrayList<>();
        ArrayList<MediaPlayer> laserRaptorMPS = new ArrayList<>();
        ArrayList<MediaPlayer> standardMPS = new ArrayList<>();

        switch (theme) {
            case UNICORN_THEME:
                // Unicorn
                MediaPlayer horsHeigh = MediaPlayer.create(context, R.raw.horse);
                MediaPlayer blob = MediaPlayer.create(context, R.raw.kameraden);
                MediaPlayer magicalExplosion = MediaPlayer.create(context, R.raw.magicalexplosion);
                unicornMPs.add(horsHeigh);
                unicornMPs.add(blob);
                unicornMPs.add(magicalExplosion);
                return unicornMPs;

            case STARWARS_THEME:
                MediaPlayer lasershot = MediaPlayer.create(context, R.raw.lasershot);
                MediaPlayer lightsaber = MediaPlayer.create(context, R.raw.lightsaber);
                MediaPlayer chewi = MediaPlayer.create(context, R.raw.chewi);
                MediaPlayer thyBidding = MediaPlayer.create(context, R.raw.bidding);
                MediaPlayer vaderBreath = MediaPlayer.create(context, R.raw.vaderbreath);
                MediaPlayer simm = MediaPlayer.create(context, R.raw.simm);
                starWarsMPs.add(thyBidding);
                starWarsMPs.add(lasershot);
                starWarsMPs.add(lightsaber);
                starWarsMPs.add(chewi);
                starWarsMPs.add(vaderBreath);
                starWarsMPs.add(simm);
                return starWarsMPs;

            case RAPTOR_THEME:
                MediaPlayer roar = MediaPlayer.create(context, R.raw.beastroar);
                MediaPlayer lasergunn = MediaPlayer.create(context, R.raw.lasergunn);
                MediaPlayer chewir = MediaPlayer.create(context, R.raw.chewi);
                laserRaptorMPS.add(roar);
                laserRaptorMPS.add(lasergunn);
                laserRaptorMPS.add(chewir);
                return laserRaptorMPS;

            case HOB_THEME:
                MediaPlayer bisdudumm = MediaPlayer.create(context, R.raw.bistdudumm);
                MediaPlayer geschossen = MediaPlayer.create(context, R.raw.geschossen);
                MediaPlayer waschdich = MediaPlayer.create(context, R.raw.waschdich);
                MediaPlayer ichlebe = MediaPlayer.create(context, R.raw.ichlebe);
                MediaPlayer kameraden = MediaPlayer.create(context, R.raw.kameraden);

                hoBMPs.add(bisdudumm);
                hoBMPs.add(geschossen);
                hoBMPs.add(waschdich);
                hoBMPs.add(ichlebe);
                hoBMPs.add(kameraden);
                return hoBMPs;
            case BAY_THEME:
                MediaPlayer explosion1 = MediaPlayer.create(context, R.raw.explosion);
                MediaPlayer explosion2 = MediaPlayer.create(context, R.raw.explosion2);
                MediaPlayer explosion3 = MediaPlayer.create(context, R.raw.explosion3);
                mBayMPs.add(explosion1);
                mBayMPs.add(explosion2);
                mBayMPs.add(explosion3);
                return mBayMPs;
            case STANDARD_THEME:
                MediaPlayer standard = MediaPlayer.create(context, R.raw.magicalexplosion);
                standardMPS.add(standard);
                standardMPS.add(standard);
                standardMPS.add(standard);
                return standardMPS;
        }
        return  null;

    }

    public static void setSoundConstants(ArrayList<MediaPlayer> mediaPlayers) {
        Button_SOUND = mediaPlayers.get(0);
        Fun_SOUND = mediaPlayers.get(1);
        INTRO_SOUND = mediaPlayers.get(2);
    }

    //3. BACKGROUND

    public static void setBackGroundConstant(String mode) {
        switch (mode) {
            case STANDARD_THEME:
                BACKGROUND = R.drawable.standard;
                break;
            case STARWARS_THEME:
                BACKGROUND = R.drawable.vader;
                break;
            case BAY_THEME:
                BACKGROUND = R.drawable.bay;
                break;
            case UNICORN_THEME:
                BACKGROUND = R.drawable.uniconr;
                break;
            case HOB_THEME:
                BACKGROUND = R.drawable.hob;
                break;
            case RAPTOR_THEME:
                BACKGROUND = R.drawable.raptorsplash;
                break;
        }
    }

    //4. IMAGES

    private static int getResizeFactor(int size){
        int resizeFactor = 1;
        if (size > 4000){
            resizeFactor = ULTRA_HIGH_FACTOR;
        } else if (size > 2000) {
            resizeFactor = HIGH_FACTOR;
        } else if (size > 1000) {
            resizeFactor = MEDIUM_FACTOR;
        } else if (size > 500) {
            resizeFactor = LOW_FACTOR;
        }

        return resizeFactor;
    }

    private static int getResizedDim(int size, int factor){
        return size / factor;
    }

    public static Bitmap getCardImageFromStorage(int deckID, int cardID){
        File file = new File(Constants.IMAGE_PATH +deckID+"-"+cardID+".jpg");
        return BitmapFactory.decodeFile(file.getAbsolutePath());
    }

    public static Bitmap getImageFromStorage(String absolutePath, String imageIdentifier){
        Bitmap bitmap;
        try {
            File f = new File(absolutePath, imageIdentifier);
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            int factor;
            if (b.getHeight() > b.getWidth()) {
                factor = getResizeFactor(b.getHeight());
            } else {
                factor = getResizeFactor(b.getWidth());
            }
            int width = getResizedDim(b.getWidth(), factor);
            int height = getResizedDim(b.getHeight(), factor);

            bitmap = Bitmap.createScaledBitmap(b, width, height, true);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            bitmap = null;
        }
        return bitmap;
    }

    //5. HTTP

    @NonNull
    public static Map<String, String> getHeadersForHTTP() {
        Map<String, String> headers = new HashMap<>();
        String credentials = "student:afmba";
        String auth = "Basic " + "c3R1ZGVudDphZm1iYQ==";
        headers.put("Content-Type", "application/json");
        headers.put("Content-Type", "multipart/form/data");
        headers.put("Authorization", auth);
        return headers;
    }

}
