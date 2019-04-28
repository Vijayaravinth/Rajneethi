package com.software.cb.rajneethi.supervisormigratoon.pojo;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by ItHours on 4/8/2017.
 */

public class VoterListDetail {
    private String Vo_name;
    private int Vo_id;
    private String addressEnglish;
    private String isvalid;
    private String voterCardNumber;
    private String gender;
    private String age;
    private String relatedEnglish;
    private String relationship;
    private String MobileNo;
    private String houseNo;
    public String superVisor;
    public String partyworker;
    private String SerialNo;
    private String Booth;


    public VoterListDetail(String jsonString) {
        try {
            JSONObject json = new JSONObject(jsonString);
            new VoterListDetail(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public VoterListDetail(JSONObject json) {
        try {

            this.Vo_name = json.getString("nameEnglish");
            this.Vo_id = json.getInt("id");
            this.addressEnglish = json.getString("addressEnglish");
            this.voterCardNumber = json.getString("voterCardNumber");
            this.gender = json.getString("gender");
            this.age = json.getString("age");
            this.relatedEnglish = json.getString("relatedEnglish");
            this.MobileNo = json.getString("MobileNo");
            this.relationship = json.getString("relationship");
            this.houseNo = json.getString("houseNo");
            this.isvalid = json.getString("isValid");
            this.SerialNo=json.getString("serialNo");



        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public VoterListDetail(String V_Name, Long V_Id, String address_eng, String card_no, String gender, String age, String related, String mo_no, String relationship, String ho_no, String is_v, String serial_no) {
        this.Vo_name = V_Name;
        this.Vo_id = Integer.parseInt(V_Id.toString());
        this.addressEnglish =address_eng ;
        this.voterCardNumber =card_no;
        this.gender =gender;
        this.age =age ;
        this.relatedEnglish = related;
        this.MobileNo = mo_no;
        this.relationship = relationship;
        this.houseNo = ho_no;
        this.isvalid = is_v;
        this.SerialNo=serial_no;
        this.partyworker="";
        this.superVisor="";
        this.Booth="";

    }

    public String getId() {
        return partyworker;
    }

    public String getName() {
        return Vo_name;
    }
    public String getAddressEnglish() {
        return addressEnglish;
    }
    public String getVoterCardNumber() {
        return voterCardNumber;
    }
    public String getGender() {
        return gender;
    }
    public String getAge() {
        return age;
    }
    public String getRelatedEnglish() {
        return relatedEnglish;
    }
    public String getRelationship() {
        return relationship;
    }
    public String getHouseNo() {
        return houseNo;
    }

    public String getSupervisorId() {
        return superVisor;
    }
    public int getVo_id() {
        return Vo_id;
    }

    public String getMobileNo() {
        return MobileNo;
    }
    public String getSerialNo() {
        return SerialNo;
    }

    public String getIsvalid() {
        return isvalid;
    }

}
