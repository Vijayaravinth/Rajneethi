package com.software.cb.rajneethi.models;

public class PaymentDetails {

    private String title;
    private int id;
    private String unit;
    private String price;

    public PaymentDetails(String title, int id, String unit, String price) {
        this.title = title;
        this.id = id;
        this.unit = unit;
        this.price = price;
    }

    public String getTitle() {
        return title;
    }

    public int getId() {
        return id;
    }

    public String getUnit() {
        return unit;
    }

    public String getPrice() {
        return price;
    }
}
