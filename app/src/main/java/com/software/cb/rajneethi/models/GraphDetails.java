package com.software.cb.rajneethi.models;

/**
 * Created by w7 on 10/28/2017.
 */

public class GraphDetails {

    private String title, value;
    private int color;

    public String getTitle() {
        return title;
    }

    public String getValue() {
        return value;
    }

    public int getColor() {
        return color;
    }

    public GraphDetails(String title, String value, int color) {

        this.title = title;
        this.value = value;
        this.color = color;
    }
}
