package com.unicorn.unicornquartett.domain;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Index;

public class Card extends RealmObject {
    @Index
    private int id;
    private int deckID;
    private String name;
    private RealmList <String> attributes;
    private String description;
    private Image image;
    private RealmList<String> listOfImagePaths = new RealmList<>();

    public Card ()  {
    }

    public void addUrlToList(String url){
        listOfImagePaths.add(url);
    }

    public RealmList<String> getListOfImagePaths() {
        return listOfImagePaths;
    }

    public void setListOfImagePaths(RealmList<String> listOfImagePaths) {
        this.listOfImagePaths = listOfImagePaths;
    }

    public int getDeckID() {
        return deckID;
    }

    public void setDeckID(int deckID) {
        this.deckID = deckID;
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

    public Image getImage() {
        return image;
    }

    public void setImages(Image image) {
        this.image = image;
    }
}
