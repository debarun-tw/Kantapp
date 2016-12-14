package com.example.kanjuice.models;

import com.google.gson.Gson;

public class Juice {
    @Override
    public String toString() {
        return "Juice{" +
                "name='" + name + '\'' +
                ", isSugarless=" + isSugarless +
                ", available=" + available +
                ", imageId=" + imageId +
                ", kanId=" + kanId +
                ", isFruit='" + isFruit + '\'' +
                '}';
    }

    public String name;
    public boolean isSugarless;
    public boolean available;
    public int imageId;
    public int kanId;
    public String isFruit;

    public String asJson() {
        return new Gson().toJson(this);
    }
}
