package com.unicorn.unicornquartett.domain;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Index;

public class GameResult extends RealmObject {
    @Index
    private int id;
    private Boolean won;

    public GameResult() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Boolean getWon() {
        return won;
    }

    public void setWon(Boolean won) {
        this.won = won;
    }
}
