package com.software.cb.rajneethi.models;

/**
 * Created by Monica on 19-03-2017.
 */

public class ConstituencyDetails {
    //"id":2,"projectId":2,"name":"Ramamurthy Nagar","number":"W26"

    public ConstituencyDetails(String id, String project_id, String name, String number) {
        this.id = id;
        this.project_id = project_id;
        this.name = name;
        this.number = number;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProject_id() {
        return project_id;
    }

    public void setProject_id(String project_id) {
        this.project_id = project_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    private String id,project_id, name, number;
}
