package com.software.cb.rajneethi.models;

/**
 * Created by w7 on 12/1/2017.
 */

public class MainMenuDetails {
    private int id, count;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    private String title;
    private boolean isDataToUpload;

    public MainMenuDetails(int id, String title, boolean isDataToUpload, int count) {
        this.id = id;
        this.title = title;
        this.count = count;
        this.isDataToUpload = isDataToUpload;
    }

    public MainMenuDetails(int id, String title) {
        this.id = id;
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isDataToUpload() {
        return isDataToUpload;
    }

    public void setDataToUpload(boolean dataToUpload) {
        isDataToUpload = dataToUpload;
    }
}
