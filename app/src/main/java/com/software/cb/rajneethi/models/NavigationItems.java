package com.software.cb.rajneethi.models;

/**
 * Created by w7 on 10/11/2017.
 */

public class NavigationItems {


    private String name, count;
    private int image;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public NavigationItems(String name, String count, int image) {

        this.name = name;
        this.count = count;
        this.image = image;
    }
}
