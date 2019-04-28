package com.software.cb.rajneethi.supervisormigratoon.pojo;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by admin on 4/8/2017.
 */

public class SupervisorPartyWorker {

    private int id;
    private String name;
    private String supervisorId;
    private String role;
    private String mobile;
    private String count;
    /*private int survey_count;

    public int getSurvey_count() {
        return survey_count;
    }
*/
    public SupervisorPartyWorker(String jsonString) {
        try {
            JSONObject json = new JSONObject(jsonString);
            new SupervisorPartyWorker(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public SupervisorPartyWorker(int id, String name, String supervisorId, String role, String mobile, String count) {
        this.id = id;
        this.name = name;
        this.supervisorId = supervisorId;
        this.role = role;
        this.mobile = mobile;
        this.count =  count;
    }

    public String getCount() {
        return count;
    }

    public String getMobile() {
        return mobile;
    }

    public SupervisorPartyWorker(JSONObject json) {
        try {
            this.id = json.getInt("id");
            this.name = json.getString("username");
            this.supervisorId = "63"; //json.getString("supervisorId");
            this.role = json.getString("role");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public SupervisorPartyWorker(Long constituencyId, String constituencyName, String constituencyNumber) {
        this.id = Integer.parseInt(constituencyId.toString());
        this.name = constituencyName;
        this.supervisorId = constituencyNumber;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSupervisorId() {
        return supervisorId;
    }

    public String getRole(){
        return role;
    }
}
