package com.software.cb.rajneethi.models;

/**
 * Created by monika on 5/12/2017.
 */

public class SelectedContactDetails {
    public SelectedContactDetails(String contactDetails) {
        this.contactDetails = contactDetails;
        this.type = "contact";
    }

    public SelectedContactDetails(String contactDetails,String type) {
        this.contactDetails = contactDetails;
        this.type = type;
    }

    public String getContactDetails() {
        return contactDetails;
    }

    public void setContactDetails(String contactDetails) {
        this.contactDetails = contactDetails;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    private String contactDetails, type;
}
