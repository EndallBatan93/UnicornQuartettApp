package com.unicorn.unicornquartett.domain;


import io.realm.RealmObject;

public class CardDTO extends RealmObject {
    private int id;
    private String name;

    public CardDTO() {

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
}
