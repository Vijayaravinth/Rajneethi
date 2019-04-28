package com.software.cb.rajneethi.models;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by admin on 5/29/2017.
 */

public class Partyworkerstats {


    private String Boothname, Statscount;

    public String getBoothname() {
        return Boothname;
    }

    public void setBoothname(String id) {
        this.Boothname = id;
    }

    public String getStatscount() {
        return Statscount;
    }

    public void setStatscount(String name) {
        this.Statscount = name;
    }

    public Partyworkerstats(JSONObject json) {
        try {
            this.Boothname = json.getString("BoothName").isEmpty() || json.getString("BoothName").equals("Not found") ? "Booth Name Not Found " : json.getString("BoothName");
            this.Statscount = json.getString("SurveyCount");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Partyworkerstats(String name, String statscount) {
        this.Boothname = name;
        this.Statscount = statscount;
    }
}


