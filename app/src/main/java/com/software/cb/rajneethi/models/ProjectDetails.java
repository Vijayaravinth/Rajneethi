package com.software.cb.rajneethi.models;

/**
 * Created by Monica on 19-03-2017.
 */

public class ProjectDetails {

    private String id,name,description;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ProjectDetails(String id, String name, String description) {

        this.id = id;
        this.name = name;
        this.description = description;
    }
}
