package com.unicorn.unicornquartett.Utility;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.view.LayoutInflater;
import android.view.View;

import com.unicorn.unicornquartett.R;
import com.unicorn.unicornquartett.activity.Menu.MenuActivity;
import com.unicorn.unicornquartett.activity.PlayGame.ChooseGameActivity;
import com.unicorn.unicornquartett.domain.User;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;

import io.realm.Realm;

import static com.unicorn.unicornquartett.Utility.Constants.*;
import static com.unicorn.unicornquartett.Utility.Constants.BAY_THEME;
import static com.unicorn.unicornquartett.Utility.Constants.HOB_THEME;
import static com.unicorn.unicornquartett.Utility.Constants.RAPTOR_THEME;
import static com.unicorn.unicornquartett.Utility.Constants.STANDARD_THEME;
import static com.unicorn.unicornquartett.Utility.Constants.STARWARS_THEME;

public class Util {

    private Context context;

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public Util(Context context) {
        this.context = context;
    }

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
}
