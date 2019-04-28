package com.software.cb.rajneethi.models;

/**
 * Created by MVIJAYAR on 30-05-2017.
 */

public class ChartDetails {

    private String  value;



    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ChartDetails(int id,String value) {
        this.value = value;
        this.id = id;
    }

    private int color, id;

}
