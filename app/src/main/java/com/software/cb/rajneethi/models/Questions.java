package com.software.cb.rajneethi.models;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by monika on 4/4/2017.
 */

public class Questions {

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getConstituencyId() {
        return constituencyId;
    }

    public void setConstituencyId(String constituencyId) {
        this.constituencyId = constituencyId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    JSONObject json;

    public Questions(String id, String constituencyId, String name, String data) {

        this.id = id;
        this.constituencyId = constituencyId;
        this.name = name;
        this.data = data;
        parseQuestionJSON();
    }


    public Questions(String constituencyId, String name, String data) {


        this.constituencyId = constituencyId;
        this.name = name;
        this.data = data;
        parseQuestionJSON();
    }

    private String id, constituencyId, name, data;

    public void parseQuestionJSON() {
        try {
            if (data != null) {
                json = new JSONObject(data);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public JSONObject getJson() {
        parseQuestionJSON();
        return json;
    }
}
