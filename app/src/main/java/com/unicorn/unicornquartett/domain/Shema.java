package com.unicorn.unicornquartett.domain;

import io.realm.RealmObject;

public class Shema extends RealmObject {

    private int id;
    private String property;
    private String unit;
    private Boolean higherWins;

    public Shema (){}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Boolean getHigherWins() {
        return higherWins;
    }

    public void setHigherWins(Boolean higherWins) {
        this.higherWins = higherWins;
    }
}
