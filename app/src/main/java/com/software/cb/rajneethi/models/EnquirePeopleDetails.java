package com.software.cb.rajneethi.models;

/**
 * Created by w7 on 11/28/2017.
 */

public class EnquirePeopleDetails {

    private String name, mobile;

    public String getName() {
        return name;
    }

    public String getMobile() {
        return mobile;
    }

    public EnquirePeopleDetails(String name, String mobile) {

        this.name = name;
        this.mobile = mobile;
    }
}
