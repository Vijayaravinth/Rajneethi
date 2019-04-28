package com.software.cb.rajneethi.supervisormigratoon.pojo;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by ItHours on 4/8/2017.
 */

public class Voterlist {

    private int Wor_id;
    private String Vo_name;
   private String supervisorId;
    private int Vo_id;
    private String MobileNo;
    private String isvalid;

    public Voterlist(String jsonString) {
        try {
            JSONObject json = new JSONObject(jsonString);
            new Voterlist(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Voterlist(JSONObject json) {
        try {
            this.Wor_id = json.getInt("partyworker_id");
            this.Vo_name = json.getString("nameEnglish");
            this.supervisorId = json.getString("supervisor_id");
            this.Vo_id = json.getInt("id");
            this.MobileNo = json.getString("MobileNo");
            this.isvalid = json.getString("isValid");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public Voterlist(Long Id, String V_Name, String Su_Number, Long V_Id, String mo_no, String is_v) {
        this.Wor_id = Integer.parseInt(Id.toString());
        this.Vo_name = V_Name;
        this.supervisorId = Su_Number;
        this.Vo_id = Integer.parseInt(V_Id.toString());
        this.MobileNo = mo_no;
        this.isvalid = is_v;
    }

   public int getId() {
        return Wor_id;
    }

    public String getName() {
        return Vo_name;
    }

   public String getSupervisorId() {
        return supervisorId;
    }
    public int getVo_id() {
        return Vo_id;
    }

    public String getMobileNo() {
        return MobileNo;
    }

    public String getIsvalid() {
        return isvalid;
    }

}
