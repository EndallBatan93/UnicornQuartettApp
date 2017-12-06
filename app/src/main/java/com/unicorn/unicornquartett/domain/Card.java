package com.unicorn.unicornquartett.domain;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Index;

public class Card extends RealmObject {
    @Index
    private int id;
    private String name;
    private String quartettindex;
    private RealmList <String> attributes;
    private String description;
    private RealmList<String> images;

    public Card ()  {

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

    public String getQuartettindex() {
        return quartettindex;
    }

    public void setQuartettindex(String quartettindex) {
        this.quartettindex = quartettindex;
    }

    public RealmList<String> getAttributes() {
        return attributes;
    }

    public void setAttributes(RealmList<String> attributes) {
        this.attributes = attributes;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public RealmList<String> getImages() {
        return images;
    }

    public void setImages(RealmList<String> images) {
        this.images = images;
    }
}
