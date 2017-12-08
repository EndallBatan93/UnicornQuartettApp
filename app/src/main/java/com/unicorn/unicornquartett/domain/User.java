package com.unicorn.unicornquartett.domain;


import java.util.UUID;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Index;

public class User extends RealmObject {

    @Index
    private int id;
    private String name;
    private RealmList<String> decks;
    private RealmList<GameResult> stats;
    private RealmList<User> friends;
    // Fluffy,moreFluffy,superFluffy
    private String difficulty;
    private Boolean runningOffline;
    private Boolean runningOnline;


    public User() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public void setFriends(RealmList<User> friends) {
        this.friends = friends;
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

    public void setRunningOffline(Boolean runningOffline) {
        this.runningOffline = runningOffline;
    }

    public Boolean getRunningOnline() {
        return runningOnline;
    }

    public void setRunningOnline(Boolean runningOnline) {
        this.runningOnline = runningOnline;
    }
}
