package com.unicorn.unicornquartett.domain;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by max on 14.01.18.
 */

public class DeckDTO extends RealmObject{
    private String name;
    private int id;
    private RealmList<CardDTO> listOfCardsDTO = new RealmList<>();
    private RealmList<CardImageList> listOfCardImagesURLs = new RealmList<>();

    public DeckDTO() {

    }

    public void addCardImageList(CardImageList cardImageList) {
        listOfCardImagesURLs.add(cardImageList);
    }

    public RealmList<CardImageList> getListOfCardImagesURLs() {
        return listOfCardImagesURLs;
    }

    public void setListOfCardImagesURLs(RealmList<CardImageList> listOfCardImagesURLs) {
        this.listOfCardImagesURLs = listOfCardImagesURLs;
    }

    public RealmList<CardDTO> getListOfCardsDTO() {
        return listOfCardsDTO;
    }

    public void setListOfCardsDTO(RealmList<CardDTO> listOfCardsDTO) {
        this.listOfCardsDTO = listOfCardsDTO;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
