package com.software.cb.rajneethi.models;

/**
 * Created by monika on 5/10/2017.
 */

public class EventsDetails {

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEventTitle() {
        return eventTitle;
    }

    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }

    public String getEventDate() {
        return eventDate;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    public String getEventTime() {
        return eventTime;
    }

    public void setEventTime(String eventTime) {
        this.eventTime = eventTime;
    }

    public String getContacts() {
        return contacts;
    }

    public void setContacts(String contacts) {
        this.contacts = contacts;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    private String id, eventTitle, eventDate, eventTime, contacts, remarks, contactWithName, place, booths;

    public String getContactWithName() {
        return contactWithName;
    }

    public void setContactWithName(String contactWithName) {
        this.contactWithName = contactWithName;
    }

    public EventsDetails(String eventTitle, String eventDate, String eventTime, String contacts, String remarks, String contactWIthName, String place) {

        this.eventTitle = eventTitle;
        this.eventDate = eventDate;
        this.eventTime = eventTime;
        this.contacts = contacts;
        this.place = place;
        this.remarks = remarks;
        this.contactWithName = contactWIthName;

    }


    private boolean isMenuOpen;

    public boolean isMenuOpen() {
        return isMenuOpen;
    }

    public void setMenuOpen(boolean menuOpen) {
        isMenuOpen = menuOpen;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public EventsDetails(String id, String eventTitle, String eventDate, String eventTime, String contacts, String remarks, String place, boolean isMenuOpen) {
        this.id = id;
        this.eventTitle = eventTitle;
        this.eventDate = eventDate;
        this.eventTime = eventTime;
        this.contacts = contacts;
        this.remarks = remarks;
        this.isMenuOpen = isMenuOpen;
        this.place = place;

    }
    public EventsDetails(String id, String eventTitle, String eventDate, String eventTime, String contacts, String remarks, String place, boolean isMenuOpen,String booths) {
        this.id = id;
        this.eventTitle = eventTitle;
        this.eventDate = eventDate;
        this.eventTime = eventTime;
        this.contacts = contacts;
        this.remarks = remarks;
        this.isMenuOpen = isMenuOpen;
        this.booths = booths;
        this.place = place;

    }


    public String getBooths() {
        return booths;
    }

    public void setBooths(String booths) {
        this.booths = booths;
    }

    public EventsDetails(String id, String eventTitle, String eventDate, String place) {
        this.id = id;
        this.eventTitle = eventTitle;
        this.eventDate = eventDate;
        this.place = place;
    }
}
