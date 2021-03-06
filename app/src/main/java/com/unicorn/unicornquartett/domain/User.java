package com.unicorn.unicornquartett.domain;


import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Index;

public class User extends RealmObject {

    @Index
    private int id;
    private String name;
    private RealmList<String> decks = new RealmList<>();
    private RealmList<GameResult> stats;
    private RealmList<User> friends;
    // Fluffy,moreFluffy,superFluffy
    private String difficulty;
    private Boolean runningOffline;
    private Boolean runningOnline;
    private String imageAbsolutePath;
    private String imageIdentifier;
    private Date date;
    private String theme;

    public User() {
    }

    public void addDeckName(String deckName){
        decks.add(deckName);
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getImageIdentifier() {
        return imageIdentifier;
    }

    public void setImageIdentifier(String imageIdentifier) {
        this.imageIdentifier = imageIdentifier;
    }

    public String getImageAbsolutePath() {
        return imageAbsolutePath;
    }

    public void setImageAbsolutePath(String imageAbsolutePath) {
        this.imageAbsolutePath = imageAbsolutePath;
    }

    public int getId() {
        return id;
    }

    public void setId() {
        this.id = 1;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public RealmList<String> getDecks() {
        return decks;
    }

    public void setDecks(RealmList<String> decks) {
        this.decks = decks;
    }

    public RealmList<GameResult> getStats() {
        return stats;
    }

    public void setStats(RealmList<GameResult> stats) {
        this.stats = stats;
    }

    public RealmList<User> getFriends() {
        return friends;
    }

    public void setFriends() {
        this.friends = null;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public Boolean getRunningOffline() {
        return runningOffline;
    }

    public void setRunningOffline() {
        this.runningOffline = false;
    }

    public Boolean getRunningOnline() {
        return runningOnline;
    }

    public void setRunningOnline() {
        this.runningOnline = false;
    }
}
