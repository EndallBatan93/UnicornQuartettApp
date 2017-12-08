package com.unicorn.unicornquartett.domain;

import io.realm.RealmObject;

public class Shema extends RealmObject {

    private String description;
    private String unit;
    private Boolean higherWins;

    public Shema (){}

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
