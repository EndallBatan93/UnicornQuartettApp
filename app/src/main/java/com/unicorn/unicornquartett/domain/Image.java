package com.unicorn.unicornquartett.domain;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Index;

public class Image extends RealmObject {
    @Index
    private int id;
    private RealmList<String> imageIdentifiers;

    public Image () {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public RealmList<String> getImageIdentifiers() {
        return imageIdentifiers;
    }

    public void setImageIdentifiers(RealmList<String> imageIdentifiers) {
        this.imageIdentifiers = imageIdentifiers;
    }
}
