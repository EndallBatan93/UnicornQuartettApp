package com.unicorn.unicornquartett.Utility;


import android.annotation.SuppressLint;
import android.media.MediaPlayer;

import com.unicorn.unicornquartett.R;

public final class Constants {
    public static final  String UNICORN_THEME = "Unicorn";
    public static final  String STARWARS_THEME = "Starwars";
    public static final  String HOB_THEME = "HandOfBlood";
    public static final  String RAPTOR_THEME = "LaserRaptor";
    public static final  String BAY_THEME = "MichaelBay";
    public static final  String STANDARD_THEME = "Standard";
    public static MediaPlayer INTRO_SOUND = new MediaPlayer();
    public static MediaPlayer Button_SOUND = new MediaPlayer();
    public static MediaPlayer Fun_SOUND = new MediaPlayer();
    public static int BACKGROUND = R.drawable.standard;

    // NAMES
    public static final String REALM_BIKE_NAME = "Bikes";
    public static final String REALM_TUNING_NAME = "Tuning";
    public static final String BIKES = "bikes";
    public static final String TUNING = "tuning";

    // DIFFICULTY
    public static final String DIFFICULTY_1 = "fluffy";
    public static final String DIFFICULTY_2 = "fluffier";
    public static final String DIFFICULTY_3 = "superfluffy";

    // GAME
    public static final String PLAYERSTURN = "Your Turn";
    public static final String OPPONENTTURN = "Opponent Turn";

    public static final int STANDARD_GAME = 1;
    public static final int UNICORN_GAME = 2;

    public static final String PLAY_STANDARD = "playStandard";
    public static final String PLAY_UNICORN = "playUnicorn";

    public static final String OPPONENT = "opponent";
    public static final String PLAYER = "player";
    public static final String DRAW = "draw";
    public static final String WINNER = "winner";
    public static final String USER = "user";
    public static final String GAME_RUNNING = "gameRunning";
    public static final String SELECTED_DECK = "selectedDeck";
    public static final String UNICORN = "unicorn";
    public static final String STANDARD = "standard";
    public static final String GAME_CATEGORY = "category";

    public static final String SWITCH_STACKS = "switch_stacks";
    public static final String RANDOM_EVENT_TRIGGERED = "randomEventTriggered";
    public static final String SWITCH_WINNER = "switchedWinner";
    public static final String EVEN_STACKS = "evenStacks";
    public static final String NONE = "none";

    // BITMAP SCALINGS
    public static final int ULTRA_HIGH_FACTOR = 20;
    public static final int HIGH_FACTOR = 10;
    public static final int MEDIUM_FACTOR = 5;
    public static final int LOW_FACTOR = 3;

    // REALM
    public static final String REALM_ID = "id";

    // UNICORN MODE
    public static final String MULTIPLY = "multiply" ;
    public static final String INSTANT_WIN = "win";

    // IMAGES
    @SuppressLint("SdCardPath")
    public static final String IMAGE_PATH = "/data/user/0/com.unicorn.unicornquartett/app_Images/";

}
