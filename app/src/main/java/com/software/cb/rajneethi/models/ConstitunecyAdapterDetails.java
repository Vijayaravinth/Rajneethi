package com.software.cb.rajneethi.models;

/**
 * Created by w7 on 10/8/2017.
 */

public class ConstitunecyAdapterDetails {

    private String name;
    private String count;
    private String image;

    public String getName() {
        return name;
    }

    public String getCount() {
        return count;
    }

    public String getImage() {
        return image;
    }

    public ConstitunecyAdapterDetails(String name, String count, String image) {

        this.name = name;
        this.count = count;
        this.image = image;
    }
}
